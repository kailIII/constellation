/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2005, Institut de Recherche pour le Développement
 * (C) 2007, Geomatys
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
 */
package net.seagis.coverage.catalog;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import org.junit.Test;

import net.seagis.catalog.Element;
import net.seagis.catalog.TableTest;
import net.seagis.catalog.ConfigurationKey;
import net.seagis.catalog.CatalogException;


/**
 * Tests the addition of new entries in the database.
 *
 * @version $Id$
 * @author Cédric Briançon
 * @author Martin Desruisseaux
 */
public class WritableGridCoverageTableTest extends TableTest {
    /**
     * The file to test for inclusion, relative to the data root directory.
     */
    private static final String TEST_FILE = "Monde/SST/Coriolis/OA_RTQCGL01_20070606_FLD_TEMP.nc";

    /**
     * Tests a {@code INSERT} statement (but do not really performs the insert).
     */
    @Test
    public void testPseudoInserts() throws Exception {
        final StringWriter insertStatements = new StringWriter();
        database.setUpdateSimulator(new PrintWriter(insertStatements));

        final String root = database.getProperty(ConfigurationKey.ROOT_DIRECTORY);
        assertNotNull("The ROOT_DIRECTORY property must be defined.", root);
        final File file = new File(root, TEST_FILE);
        if (!file.isFile()) {
            Element.LOGGER.warning("Test file \"" + file + "\" not found.");
            return;
        }
        final LayerTable layers = database.getTable(LayerTable.class);
        final Layer layer = layers.getEntry(LayerTableTest.NETCDF_NAME);
        final Set<Series> series = layer.getSeries();
        assertEquals("Expected only one series in the layer.", 1, series.size());
        final Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName("NetCDF");
        assertTrue("A NetCDF reader must be available.", readers.hasNext());
        final ImageReader reader = readers.next();
        reader.setInput(file);

        WritableGridCoverageTable table = database.getTable(WritableGridCoverageTable.class);
        try {
            table.addEntry(reader);
            fail("Should not accept to add an entry without layer.");
        } catch (CatalogException exception) {
            // This is the expected exception since no layer has been specified.
        }
        try {
            table.setLayer(layer);
            fail("Should not accept to modify a shared table.");
        } catch (IllegalStateException exception) {
            // This is the expected exception since the table has not been cloned.
        }
        table = new WritableGridCoverageTable(table);
        table.setLayer(layer);
        table.addEntry(reader);
        reader.dispose();
    }
}
