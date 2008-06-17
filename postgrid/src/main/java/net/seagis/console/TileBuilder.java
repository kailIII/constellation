/*
 * (C) 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.seagis.console;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.logging.Level;
import javax.imageio.ImageWriteParam;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;

import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import org.geotools.referencing.CRS;
import org.geotools.geometry.Envelope2D;
import org.geotools.image.io.mosaic.Tile;
import org.geotools.image.io.mosaic.TileManager;
import org.geotools.image.io.mosaic.MosaicBuilder;
import org.geotools.image.io.mosaic.TileWritingPolicy;
import org.geotools.image.io.mosaic.TileManagerFactory;
import org.geotools.util.FrequencySortedSet;
import org.geotools.console.Option;
import org.geotools.resources.image.ImageUtilities;
import org.geotools.console.ExternalyConfiguredCommandLine;

import net.seagis.catalog.Database;
import net.seagis.catalog.CatalogException;
import net.seagis.catalog.NoSuchRecordException;
import net.seagis.coverage.catalog.WritableGridCoverageEntry;
import net.seagis.coverage.catalog.WritableGridCoverageTable;


/**
 * Creates tiles and write the entries in the database. This is only a convenience utility
 * for creating tiles and filling the database from a set of source images. The database is
 * <strong>not</strong> required to contains tiles created by this utility, and the tiles
 * layout in the database are <strong>not</strong> restricted to the default layout used by
 * this utility class.
 * <p>
 * The {@linkplain #main main} method expects a {@code .properties} file describing the source
 * tiles and their layout. An example is given <a href="doc-files/TileBuilder.properties>here</a>.
 * <p>
 * By default, this utility just display a table of tiles that would be written or inserted in
 * the database. For processing to the actual tile creation or database update, one or many of
 * the following options must be specified:
 * <p>
 * <ul>
 *   <li>{@code -write}     writes the tiles to the target directory, skipping existing ones.</li>
 *   <li>{@code -overwrite} writes the tiles to the target directory, overwriting existing ones.</li>
 *   <li>{@code -insert}    inserts the tiles metadata in the database.</li>
 *   <li>{@code -pretend}   like {@code -insert} but prints the SQL statement instead
 *                          of executing them.</li>
 * </ul>
 *
 * @author Cédric Briançon
 * @author Martin Desruisseaux
  */
public class TileBuilder extends ExternalyConfiguredCommandLine implements Runnable {
    /**
     * The prefix for tiles in the {@linkplain #properties} map.
     */
    private static final String TILE_PREFIX = "tile.";

    /**
     * Extension of referencing files.
     */
    private static final String REFERENCING_EXTENSION = "tfw";

    /**
     * Directory extracted from the {@linkplain #properties}. The source directory contains
     * the tiles of an existing mosaic, typically as very big tiles. The target directory is
     * the place where to write the tiles generated by this class.
     */
    private final File sourceDirectory, targetDirectory;

    /**
     * The horizontal SRID, or {@code 0} if unspecified.
     */
    private final int horizontalSRID;

    /**
     * The geographic envelope, or {@code null} if unspecified.
     */
    private final Envelope2D envelope;

    /**
     * The target tile size, or {@code null} if unspecified.
     */
    private final Dimension tileSize;

    /**
     * The format of the tile to be written, or {@code null} if unspecified.
     */
    private final String format;

    /**
     * The series for the tiles to be inserted in the database.
     */
    private final String series;

    /**
     * Flags specified in the properties file.
     */
    private final boolean keepLayout, compress;

    /**
     * Flag specified on the command lines.
     */
    @Option(description="Write the tiles to the target directory, skipping existing ones.")
    private boolean write;

    /**
     * Flag specified on the command lines.
     */
    @Option(description="Write the tiles to the target directory, overwriting existing ones.")
    private boolean overwrite;

    /**
     * Flag specified on the command lines.
     */
    @Option(description="Do not ommit empty tiles (default is to skip them).")
    private boolean empty;

    /**
     * Flag specified on the command lines.
     */
    @Option(description="Insert the tiles metadata in the database.")
    private boolean insert;

    /**
     * Flag specified on the command lines.
     */
    @Option(description="Print the SQL statements rather than executing them (for debugging only).")
    private boolean pretend;

    /**
     * Disables JAI codecs. Experience shows that the coded bundled in J2SE work better.
     */
    static {
        ImageUtilities.allowNativeCodec("PNG", ImageReaderSpi.class, false);
        ImageUtilities.allowNativeCodec("PNG", ImageWriterSpi.class, false);
    }

    /**
     * Creates a new builder. In case of failure, a message is printed to the
     * {@link #err standard error stream} and this method invokes {@link System#exit}.
     *
     * @param args The command line arguments.
     */
    protected TileBuilder(final String[] args) {
        super(args);
        final String mosaicCRS;
        CoordinateReferenceSystem crs = null;
        sourceDirectory = getFile     ("SourceDirectory");
        targetDirectory = getFile     ("TargetDirectory");
        envelope        = getEnvelope ("MosaicEnvelope" );
        mosaicCRS       = getString   ("MosaicCRS"      );
        tileSize        = getDimension("TileSize"       );
        format          = getString   ("Format"         );
        series          = getString   ("Series"         );
        keepLayout      = getBoolean  ("KeepLayout", false);
        compress        = getBoolean  ("Compress",   true);
        if (mosaicCRS != null) try {
            crs = CRS.decode(mosaicCRS, true);
            if (envelope != null) {
                envelope.setCoordinateReferenceSystem(crs);
            }
        } catch (FactoryException e) {
            err.println(e);
            System.exit(ILLEGAL_ARGUMENT_EXIT_CODE);
        }
        Integer id = null;
        if (crs != null) {
            crs = CRS.getHorizontalCRS(crs);
            try {
                id = CRS.lookupEpsgCode(crs, true);
            } catch (FactoryException e) {
                err.println(e);
                // Continue anyway. The horizontalSRID will be set to "unknown".
            }
        }
        // TODO: 'id' is the EPSG code. This is not necessarly the same that spatial_ref_sys primary key...
        horizontalSRID = (id != null) ? id : 0;
    }

    /**
     * Process to the command execution. This method can be invoked at any time (but only once)
     * after {@code TileBuilder} construction. If this method fails, a message is printed to the
     * {@link System#err standard error stream} and {@link System#exit} is invoked.
     */
    public void run() {
        final Collection<Tile> tiles = createSourceTiles();
        ensureEmptyProperties();
        if (tiles.isEmpty()) {
            err.println("At least one tile must be specified.");
            System.exit(BAD_CONTENT_EXIT_CODE);
        }
        final TileManager manager = createTargetTiles(tiles);
        if (insert) {
            updateDatabase(manager);
        }
        out.flush();
        err.flush();
    }

    /**
     * Creates the collection of source tiles. The default implementation builds the collection
     * from the files declared in the property file given on the command line. Subclasses can
     * override this method in order to complete the collection in an other way.
     * <p>
     * If this method fails, a message is printed to the {@link System#err standard error stream}
     * and {@link System#exit} is invoked.
     *
     * @return A collection of tiles.
     */
    protected Collection<Tile> createSourceTiles() {
        final Collection<Tile> tiles = new LinkedHashSet<Tile>();
        /*
         * Creates the tiles from the properties given explicitly with "tile." prefix.
         */
        for (final Object key : properties.keySet()) {
            String filename = (String) key;
            if (filename.startsWith(TILE_PREFIX)) {
                final Point origin = getPoint(filename);
                filename = filename.substring(TILE_PREFIX.length());
                File file = new File(filename);
                if (sourceDirectory != null && !file.isAbsolute()) {
                    file = new File(sourceDirectory, filename);
                }
                final Tile tile = new Tile(null, file, 0, origin, null);
                tiles.add(tile);
            }
        }
        /*
         * Loads the file given by the "tiles" property. We read all lines before to
         * process them because we want to close the stream first in case of failure.
         */
        final File tileFiles = getFile("Tiles");
        if (tileFiles != null) {
            final List<File> files = new ArrayList<File>();
            try {
                final BufferedReader in = new BufferedReader(new FileReader(tileFiles));
                String line;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.length() != 0) {
                        files.add(new File(line));
                    }
                }
                in.close();
            } catch (IOException e) {
                err.println(e);
                System.exit(IO_EXCEPTION_EXIT_CODE);
            }
            for (final File file : files) {
                final AffineTransform tr;
                try {
                    tr = parseTransform(file);
                } catch (IOException e) {
                    err.println(e);
                    System.exit(IO_EXCEPTION_EXIT_CODE);
                    continue;
                }
                final Tile tile = new Tile(null, file, 0, null, tr);
                tiles.add(tile);
            }
        }
        return tiles;
    }

    /**
     * Creates the target tiles, optionnaly writes them to disk and to the database. If this
     * method fails, a message is printed to the {@link System#err standard error stream}
     * and {@link System#exit} is invoked.
     *
     * @param  tiles The source tiles.
     * @return The tile manager containing target tiles.
     */
    private TileManager createTargetTiles(final Collection<Tile> tiles) {
        if (!targetDirectory.isDirectory()) {
            err.print(targetDirectory.getPath());
            err.println(" is not a directory.");
            System.exit(ILLEGAL_ARGUMENT_EXIT_CODE);
        }
        /*
         * From the big tiles declared in the property files, infers a set of smaller tiles at
         * different overview levels. For example starting with 6 BlueMarble tiles, we can get
         * 4733 tiles of size 960 x 960 pixels. The result is printed to the standard output.
         */
        MosaicBuilder builder = new MosaicBuilder() {
            @Override
            protected void onTileWrite(Tile tile, ImageWriteParam parameters) throws IOException {
                if (!compress) {
                    parameters.setCompressionMode(ImageWriteParam.MODE_DISABLED);
                }
            }
        };
        builder.setLogLevel(Level.INFO);
        if (tileSize != null) {
            builder.setTileSize(tileSize);
        }
        builder.setTileDirectory(targetDirectory);
        builder.setMosaicEnvelope(envelope);
        builder.setTileReaderSpi(format);
        final TileManager manager;
        try {
            if (keepLayout) {
                manager = TileManagerFactory.DEFAULT.create(tiles)[0];
            } else {
                TileWritingPolicy policy = TileWritingPolicy.NO_WRITE;
                if (!pretend) {
                    if (overwrite) {
                        policy = TileWritingPolicy.OVERWRITE;
                    } else if (write) {
                        if (empty) {
                            policy = TileWritingPolicy.WRITE_NEWS_ONLY;
                        } else {
                            policy = TileWritingPolicy.WRITE_NEWS_NONEMPTY;
                        }
                    }
                }
                manager = builder.createTileManager(tiles, 0, policy);
            }
        } catch (IOException e) {
            err.println(e);
            System.exit(IO_EXCEPTION_EXIT_CODE);
            return null; // Should never reach this point.
        }
        if (manager == null) {
            err.println("Aborted.");
            System.exit(ABORT_EXIT_CODE);
        }
        out.println(manager);
        return manager;
    }

    /**
     * Insert the tile entries in the database. If this method fails, a message is printed
     * to the {@link System#err standard error stream} and {@link System#exit} is invoked.
     *
     * @param tileManager The tile manager containing target tiles.
     */
    private void updateDatabase(final TileManager manager) {
        Collection<Tile> tiles;
        /*
         * Keep only the tiles associated to existing files. Those tiles should have been written
         * by the above code. We do this filtering only after the tiles writting because the empty
         * tiles are detected reliably only by MosaicImageWriter. An other reason is that if tiles
         * writting and datebase update are two separated steps, looking for existing files is the
         * only way to know which tiles were determined as empty by the previous run.
         */
        try {
            tiles = manager.getTiles();
        } catch (IOException e) {
            err.println(e);
            System.exit(IO_EXCEPTION_EXIT_CODE);
            return;
        }
        if (!empty) {
            final ArrayList<Tile> filtered = new ArrayList<Tile>(tiles.size());
            for (final Tile tile : tiles) {
                final Object input = tile.getInput();
                if (input instanceof File) {
                    final File file = (File) input;
                    if (!file.isFile()) {
                        continue;
                    }
                }
                filtered.add(tile);
            }
            filtered.trimToSize();
            tiles = filtered;
        }
        /*
         * Creates a global tiles which cover the whole area.
         * We will use the most frequent file suffix for this tile.
         *
         * TODO: probably a bad idea - WritableGridCoverageTable will no accepts arbitrary suffix,
         *       but only the suffix expected by the series. We will need to revisit this policy.
         */
        final SortedSet<String> suffixes = new FrequencySortedSet<String>(true);
        for (final Tile tile : tiles) {
            final Object input = tile.getInput();
            if (input instanceof File) {
                final String file = ((File) input).getName();
                final int split = file.lastIndexOf('.');
                if (split >= 0) {
                    suffixes.add(file.substring(split));
                }
            }
        }
        String name = series;
        if (!suffixes.isEmpty()) {
            name += suffixes.first();
        }
        final Tile global;
        try {
            global = manager.createGlobalTile(null, name, 0);
        } catch (IOException e) {
            err.println(e);
            System.exit(IO_EXCEPTION_EXIT_CODE);
            return;
        }
        /*
         * Fills the database if requested by the user. The tiles entries will be inserted in the
         * "Tiles" table while the global entry will be inserted into the "GridCoverages" table.
         */
        try {
            final Database database = new Database();
            if (pretend) {
                database.setUpdateSimulator(out);
            }
            WritableGridCoverageTable table = database.getTable(WritableGridCoverageTable.class);
            table = new WritableGridCoverageTable(table) {
                @Override
                protected WritableGridCoverageEntry createEntry(final Tile tile) throws IOException {
                    return new Entry(tile);
                }
            };
            table.setCanInsertNewLayers(true);
            table.setLayer(series); // TODO: we currently assume the same name than the series.
            try {
                table.setSeries(series);
            } catch (NoSuchRecordException e) {
                // Ignore... We will let the WritableGridCoverageTable selects a series.
            }
            table.addEntry(global);
            table.addTiles(tiles);
            database.close();
        } catch (IOException e) {
            err.println(e);
            System.exit(IO_EXCEPTION_EXIT_CODE);
        } catch (SQLException e) {
            err.println(e);
            System.exit(SQL_EXCEPTION_EXIT_CODE);
        } catch (CatalogException e) {
            err.println(e);
            System.exit(SQL_EXCEPTION_EXIT_CODE);
        }
    }

    /**
     * The entry for a tile to be added.
     */
    private final class Entry extends WritableGridCoverageEntry {
        public Entry(final Tile tile) throws IOException {
            super(tile);
        }

        @Override
        public int getHorizontalSRID() throws IOException, CatalogException {
            return (horizontalSRID != 0) ? horizontalSRID : super.getHorizontalSRID();
        }
    }

    /**
     * Returns a file with the same path and extension than the given one, and the extension
     * replaced by {@value #REFERENCING_EXTENSION}.
     */
    static File toTFW(File file) {
        if (file != null) {
            String name = file.getName();
            final int separator = name.lastIndexOf('.');
            if (separator >= 0) {
                final File parent = file.getParentFile();
                final String basename = name.substring(0, separator + 1);
                name = basename + REFERENCING_EXTENSION;
                file = new File(parent, name);
                final File preferred = file;
                if (!file.exists()) {
                    name = basename + REFERENCING_EXTENSION.toUpperCase();
                    file = new File(parent, name);
                    if (!file.exists()) {
                        name = basename + REFERENCING_EXTENSION;
                        file = preferred;
                    }
                }
            }
        }
        return file;
    }

    /**
     * Creates an affine transform from the coefficients in the given file.
     */
    private AffineTransform parseTransform(File file) throws IOException {
        file = toTFW(file);
        final BufferedReader in = new BufferedReader(new FileReader(file));
        final double[] m = new double[6];
        int count = 0;
        String line;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if (line.length() != 0) {
                if (count >= m.length) {
                    in.close();
                    err.print("Too many lines in \"");
                    err.print(file.getName());
                    err.print("\" file.");
                    System.exit(BAD_CONTENT_EXIT_CODE);
                }
                try {
                    m[count++] = Double.parseDouble(line);
                } catch (NumberFormatException e) {
                    in.close();
                    err.println(e);
                    System.exit(BAD_CONTENT_EXIT_CODE);
                }
            }
        }
        in.close();
        if (count != m.length) {
            err.print("Not enough lines in \"");
            err.print(file.getName());
            err.print("\" file.");
            System.exit(BAD_CONTENT_EXIT_CODE);
        }
        return new AffineTransform(m);
    }

    /**
     * Runs from the command line.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        final TileBuilder builder = new TileBuilder(args);
        builder.run();
    }
}
