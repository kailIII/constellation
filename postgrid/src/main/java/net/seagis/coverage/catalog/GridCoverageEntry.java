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

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Dimension2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.net.URISyntaxException;
import javax.imageio.IIOException;
import javax.imageio.ImageReadParam;
import java.sql.SQLException;

import java.util.Map;
import java.util.Date;
import java.util.Arrays;
import java.util.Collections;
import static java.lang.Math.*;

import org.opengis.coverage.SampleDimension;
import org.opengis.coverage.grid.GridRange;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;
import org.opengis.metadata.extent.GeographicBoundingBox;

import org.geotools.image.io.IIOListeners;
import org.geotools.image.io.netcdf.NetcdfReadParam;
import org.geotools.image.io.mosaic.MosaicImageReadParam;
import org.geotools.image.io.mosaic.TileManager;
import org.geotools.geometry.GeneralEnvelope;
import org.geotools.coverage.GridSampleDimension;
import org.geotools.coverage.grid.ViewType;
import org.geotools.coverage.grid.GeneralGridRange;
import org.geotools.coverage.grid.GridGeometry2D;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.GridCoverageFactory;
import org.geotools.referencing.crs.DefaultTemporalCRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.metadata.iso.extent.GeographicBoundingBoxImpl;

import org.geotools.util.DateRange;
import org.geotools.util.NumberRange;
import org.geotools.util.logging.Logging;
import org.geotools.referencing.CRS;
import org.geotools.resources.Classes;
import org.geotools.resources.Utilities;
import org.geotools.resources.CRSUtilities;
import org.geotools.resources.geometry.XRectangle2D;
import org.geotools.referencing.operation.matrix.XAffineTransform;

import net.seagis.catalog.Entry;
import net.seagis.coverage.model.Operation;
import net.seagis.catalog.IllegalRecordException;
import net.seagis.catalog.CatalogException;

import ucar.nc2.dataset.AxisType;


/**
 * Implementation of {@linkplain CoverageReference coverage reference} to a single image.
 * This implementation is immutable and thread-safe.
 *
 * @version $Id$
 * @author Martin Desruisseaux
 */
final class GridCoverageEntry extends Entry implements CoverageReference {
    /**
     * For cross-version compatibility.
     */
    private static final long serialVersionUID = -5725249398707248625L;

    /**
     * Petite valeur utilisée pour contourner les erreurs d'arrondissement.
     */
    private static final double EPS = 1E-6;

    /**
     * Largeur et hauteur minimale des images, en pixels. Si l'utilisateur demande une région plus
     * petite, la région demandée sera agrandie pour que l'image fasse cette taille.
     */
    private static final int MIN_SIZE = 64;

    /** The file name.               */ private final String filename;
    /** Image start time, inclusive. */ private final long   startTime;
    /** Image end time, exclusive.   */ private final long   endTime;
    /** Index of image to be read.   */ private final short  index;
    /** The band to read, or 0.      */ private final short  band;

    /**
     * If the image is tiled, the tiles. Otherwise {@code null}. This field is set only after
     * construction in order to avoid loading thousands of lines from the database when a suitable
     * entry was already in the cache.
     * <p>
     * Should be the same instance than the one cached in {@link TileTable}
     * (not a clone, otherwise the caching will not work as expected).
     */
    private TileManager[] tiles;

    /**
     * The grid geometry. Include the image size (in pixels), the geographic envelope
     * and the vertical ordinate values.
     */
    private final GridGeometryEntry geometry;

    /**
     * The series in which this coverage is declared.
     */
    private final Series series;

    /**
     * Bloc de paramètres de la table d'images. On retient ce bloc de paramètres plutôt qu'une
     * référence directe vers {@link GridCoverageTable} afin de ne pas empêcher le ramasse-miettes
     * de détruire la table et ses connections vers la base de données.
     */
    private final GridCoverageSettings settings;

    /**
     * Référence molle vers l'image {@link GridCoverage2D} qui a été retournée lors du dernier appel
     * de {@link #getCoverage}. Cette référence est retenue afin d'éviter de charger inutilement une
     * autre fois l'image si elle est déjà en mémoire.
     */
    private transient Reference<GridCoverage2D> gridCoverage;

    /**
     * Référence molle vers l'image {@link RenderedImage} qui a été retournée lors du dernier appel
     * de {@link #getCoverage}. Cette référence est retenue afin d'éviter de charger inutilement une
     * autre fois l'image si elle est déjà en mémoire.
     */
    private transient Reference<RenderedImage> renderedImage;

    /**
     * Quantité de mémoire utilisée par les pixels de {@link #renderedImage}. Seuls les pixels
     * sont pris en compte; l'espace occupé par l'objet lui-même n'est pas mesuré. La valeur 0
     * indique que {@link #renderedImage} n'a pas encore été lue.
     */
    private transient int memoryUsage;

    /**
     * Construit une entré contenant des informations sur une image. Un {@linkplain #getName nom unique}
     * sera construit à partir de la série et du nom de fichiers (les colonnes {@code series} et
     * {@code filename}, qui constituent habituellement la clé primaire de la table).
     * <p>
     * <strong>NOTE:</strong> Les coordonnées {@code xmin}, {@code xmax}, {@code ymin} et {@code ymax}
     * ne sont <u>pas</u> exprimées selon le système de coordonnées de l'image, mais plutôt selon le
     * système de coordonnées de la table d'images ({@code table}). La transformation sera effectuée
     * par {@link #getEnvelope()} à la volé.
     *
     * @param  table Table d'où proviennent les enregistrements.
     * @throws CatalogException si des arguments sont invalides.
     * @throws SQLException si une erreur est survenue lors de l'accès à la base de données.
     */
    protected GridCoverageEntry(final GridCoverageTable table,
                                final Series            series,
                                final String            filename,
                                final short             index,
                                final Date              startTime,
                                final Date              endTime,
                                final GridGeometryEntry geometry,
                                final short             band,
                                final String            remarks)
            throws CatalogException, SQLException
    {
        super(createName(series.getName(), filename, index, band), remarks);
        // TODO: need to include the temporal CRS here.
        CoordinateReferenceSystem crs = geometry.getCoordinateReferenceSystem();
        this.series    = series;
        this.filename  = filename;
        this.geometry  = geometry;
        this.index     = index;
        this.band      = band;
        this.settings  = table.getSettings(crs);
        this.startTime = (startTime!=null) ? startTime.getTime() : Long.MIN_VALUE;
        this.  endTime = (  endTime!=null) ?   endTime.getTime() : Long.MAX_VALUE;
        if (geometry.isEmpty() || this.startTime > this.endTime) {
            // TODO: localize
            throw new IllegalRecordException("The spatio-temporal envelope is empty.");
        }
    }

    /**
     * Workaround for RFE #4093999
     * ("Relax constraint on placement of this()/super() call in constructors").
     */
    private static String createName(final String series, final String filename,
                                     final short index, final short band)
    {
        final StringBuilder buffer = new StringBuilder(series.trim());
        buffer.append(':').append(filename);
        if (index != 0) {
            buffer.append(':').append(index);
        }
        if (band != 0) {
            buffer.append(':').append(band);
        }
        return buffer.toString();
    }

    /**
     * Retourne un exemplaire unique de cette entrée. Une banque d'entrées, initialement vide, est
     * maintenue de façon interne par la classe {@code GridCoverageEntry}. Lorsque la méthode
     * {@code unique} est appelée, elle recherchera une entrée égale à {@code this} au
     * sens de la méthode {@link #equals}. Si une telle entrée est trouvée, elle sera retournée.
     * Sinon, l'entrée {@code this} sera ajoutée à la banque de données en utilisant une
     * {@linkplain WeakReference référence faible} et cette méthode retournera {@code this}.
     * <p>
     * De cette méthode il s'ensuit que pour deux entrées <var>u</var> et <var>v</var>,
     * la condition {@code u.unique()==v.unique()} sera vrai si et seulement
     * si {@code u.equals(v)} est vrai.
     */
    final GridCoverageEntry unique() {
        return GridCoveragePool.DEFAULT.unique(this);
    }

    /**
     * Sets the tiles. Invoked after construction only if no suitable entry was in the cache.
     * The given array is not and should not be cloned - we need to keep a reference to this
     * exact instance in order to let the {@link TileTable} cache be effective.
     */
    final void setTiles(final TileManager[] tiles) {
        if (this.tiles != null) {
            throw new IllegalStateException();
        }
        this.tiles = tiles;
    }

    /**
     * {@inheritDoc}
     */
    public Series getSeries() {
        return series;
    }

    /**
     * {@inheritDoc}
     */
    public File getFile() {
        final File file = series.file(filename);
        return file.isAbsolute() ? file : null;
    }

    /**
     * {@inheritDoc}
     */
    public URI getURI() {
        try {
            return series.uri(filename);
        } catch (URISyntaxException exception) {
            Logging.unexpectedException(LOGGER, GridCoverageEntry.class, "getURI", exception);
            return null;
        }
    }

    /**
     * Returns the source as a {@link File} or an {@link URI}, in this preference order.
     */
    private Object getInput() throws IOException {
        if (tiles != null) {
            return tiles;
        }
        final File file = series.file(filename);
        if (file.isAbsolute()) {
            return file;
        }
        try {
            return series.uri(filename);
        } catch (URISyntaxException e) {
            throw new IIOException(e.getLocalizedMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public CoordinateReferenceSystem getCoordinateReferenceSystem() {
        return settings.coverageCRS;
    }

    /**
     * {@inheritDoc}
     */
    public Envelope getEnvelope() {
        final Rectangle clipPixels = new Rectangle();
        try {
            return computeBounds(clipPixels, null);
        } catch (TransformException exception) {
            // Should not happen if the coordinate in the database are valids.
            throw new IllegalStateException(exception.getLocalizedMessage(), exception);
        }
    }

    /**
     * {@inheritDoc}
     *
     * Note: Cette bibliothèque utilise une instance spéciale de {@link DefaultTemporalCRS}
     *       (définie dans {@link net.seagis.observation.sql.CRS}) qui sait représenter les
     *       plages de temps illimitées par {@link Double#POSITIVE_INFINITY} ou
     *       {@link Double#NEGATIVE_INFINITY}.
     *
     * @todo Revisit now that we are 4D.
     */
    public NumberRange getZRange() {
        final DefaultTemporalCRS temporalCRS = settings.getTemporalCRS();
        return new NumberRange(temporalCRS.toValue(new Date(startTime)),
                               temporalCRS.toValue(new Date(  endTime)));
    }

    /**
     * {@inheritDoc}
     */
    public DateRange getTimeRange() {
        return new DateRange((startTime!=Long.MIN_VALUE) ? new Date(startTime) : null, true,
                               (endTime!=Long.MAX_VALUE) ? new Date(  endTime) : null, false);
    }

    /**
     * {@inheritDoc}
     *
     * @todo L'implémentation actuelle suppose que le CRS de la table est toujours WGS84.
     */
    public GeographicBoundingBox getGeographicBoundingBox() {
        try {
            assert CRS.equalsIgnoreMetadata(DefaultGeographicCRS.WGS84, CRSUtilities.getCRS2D(settings.tableCRS));
        } catch (TransformException e) {
            throw new AssertionError(e);
        }
        return geometry.getGeographicBoundingBox();
    }

    /**
     * {@inheritDoc}
     *
     * @todo L'implémentation actuelle suppose que le CRS de la table a toujours des axes dans
     *       l'ordre (x,y).
     *
     * @todo Should compute the geometry by {@link GridGeometryEntry} instead.
     */
    @SuppressWarnings("fallthrough")
    public GridGeometry2D getGridGeometry() {
        final Rectangle clipPixels = new Rectangle();
        final Point subsampling = new Point();
        final Envelope envelope;
        try {
            envelope = computeBounds(clipPixels, subsampling);
        } catch (TransformException exception) {
            // Should not happen if the coordinate in the database are valids.
            throw new IllegalStateException(exception.getLocalizedMessage(), exception);
        }
        if (envelope == null) {
            return null;
        }
        final int dimension = envelope.getDimension();
        final int[]   lower = new int[dimension];
        final int[]   upper = new int[dimension];
        switch (dimension) {
            // Fall through in every cases.
            default: Arrays.fill(upper, 2, dimension, 1);
            case 2:  upper[1] = clipPixels.height / Math.max(subsampling.x, 1);
            case 1:  upper[0] = clipPixels.width  / Math.max(subsampling.y, 1);
            case 0:  break;
        }
        final GeneralGridRange gridRange = new GeneralGridRange(lower, upper);
        return new GridGeometry2D(gridRange, envelope, new boolean[]{false,true,false}, false);
    }

    /**
     * {@inheritDoc}
     */
    public SampleDimension[] getSampleDimensions() {
        final GridSampleDimension[] bands = series.getFormat().getSampleDimensions();
        for (int i=0; i<bands.length; i++) {
            bands[i] = bands[i].geophysics(true);
        }
        return bands;
    }

    /**
     * Handles special cases for some specific image formats.
     *
     * @deprecated We need to find a better way to do this stuff.
     */
    private boolean handleSpecialCases(final ImageReadParam param) {
        if (param instanceof NetcdfReadParam) {
            final NetcdfReadParam p = (NetcdfReadParam) param;
            p.setBandDimensionTypes(AxisType.Height, AxisType.Pressure);
            if (index != 0) {
                p.setSliceIndice(AxisType.Time, index - 1);
            }
            return true;
        } else if (param instanceof MosaicImageReadParam) {
            ((MosaicImageReadParam) param).setSubsamplingChangeAllowed(true);
            return true;
        }
        return false;
    }

    /**
     * Computes the region to read, in "real world" coordinates and in pixel coordinates. The main
     * purpose of this method is to be invoked just before an image is read, but it can also be
     * invoked by some informative methods like {@link #getEnvelope} and {@link #getGridGeometry}.
     * Every arguments given to this method are output arguments (this method will only write in
     * them - no information is read from them).
     *
     * @param  clipPixel The rectangle in which to write the pixel coordinates of the region to be
     *         read.
     * @param  subsampling The objet in which to write the subsampling, or {@code null}
     *         if this information is not wanted.
     * @return The "real world" coordinates of the region to read, or {@code null} if the image
     *         should not be read (for example because the envelope is empty). This envelope may
     *         have more than 2 dimensions.
     */
    @SuppressWarnings("fallthrough")
    private GeneralEnvelope computeBounds(final Rectangle clipPixel, final Point subsampling)
            throws TransformException
    {
        final GridRange gridRange = geometry.gridRange;
        final int width  = gridRange.getLength(0);
        final int height = gridRange.getLength(1);
        final XRectangle2D boundingBox = geometry.geographicEnvelope;
        final AffineTransform gridToCRS = geometry.gridToCRS; // DO NOT MODIFY.
        final AffineTransform crsToGrid;
        try {
            crsToGrid = gridToCRS.createInverse();
        } catch (NoninvertibleTransformException exception) {
            throw new TransformException(exception.getLocalizedMessage(), exception);
        }
        /*
         * Computes the subsampling from the desired resolution. We process by transforming
         * the ["real world" resolution] to [pixel subsampling] using a sample rectangle in
         * the center of the geographic bounding box.
         */
        final Dimension2D resolution = settings.resolution;
        final int xSubsampling;
        final int ySubsampling;
        if (resolution != null) {
            final double xResolution = resolution.getWidth();
            final double yResolution = resolution.getHeight();
            Rectangle2D sample = new Rectangle2D.Double(
                    boundingBox.getCenterX() - 0.5 * xResolution,
                    boundingBox.getCenterY() - 0.5 * yResolution, xResolution, yResolution);
            sample = settings.tableToCoverageCRS(sample, sample);
            sample = XAffineTransform.transform(crsToGrid, sample, sample);
            xSubsampling = max(1, min(width /MIN_SIZE, (int)round(sample.getWidth ())));
            ySubsampling = max(1, min(height/MIN_SIZE, (int)round(sample.getHeight())));
        } else {
            xSubsampling = 1;
            ySubsampling = 1;
        }
        if (subsampling != null) {
            subsampling.x = xSubsampling;
            subsampling.y = ySubsampling;
        }
        /*
         * Gets the geographic coordinates of the ROI (region of interest). Those coordinates
         * are expressed in the table CRS, not in the coverage CRS. If a projection is wanted,
         * it can be applied using GridCoverageSettings.tableToCoverageCRS(...). Better avoid
         * to project the clip however, because it often includes the poles which are not
         * handled by every kind of projection.
         */
        final Rectangle2D clipArea = settings.geographicArea;
        Rectangle2D clipLogical;
        if (clipArea == null) {
            clipLogical = geometry.getEnvelope2D();
        } else {
            /*
             * Vérifie si le rectangle demandé (clipArea) intercepte la région géographique
             * couverte par l'image. On utilise un code spécial plutôt que de faire appel à
             * Rectangle2D.intersects(..) parce qu'on veut accepter les cas où le rectangle
             * demandé se résume à une ligne ou un point.
             */
            if (clipArea.getWidth()<0 || clipArea.getHeight()<0 || boundingBox.isEmpty()) {
                return null;
            }
            if (clipArea.getMaxX() < boundingBox.getMinX() ||
                clipArea.getMinX() > boundingBox.getMaxX() ||
                clipArea.getMaxY() < boundingBox.getMinY() ||
                clipArea.getMinY() > boundingBox.getMaxY())
            {
                return null;
            }
            clipLogical = new Rectangle2D.Double(); // Will be computed below.
            Rectangle2D.intersect(boundingBox, clipArea, clipLogical);
            clipLogical = settings.tableToCoverageCRS(clipLogical, clipLogical);
            /*
             * Transforms ["real world" envelope] --> [region in pixel coordinates].
             * We temporarily overwrite "clipLogical", but will recompute it later.
             */
            clipLogical = XAffineTransform.transform(crsToGrid, clipLogical, clipLogical);
            clipPixel.x      = (int)floor(clipLogical.getX()      + EPS);
            clipPixel.y      = (int)floor(clipLogical.getY()      + EPS);
            clipPixel.width  = (int)ceil (clipLogical.getWidth()  - EPS);
            clipPixel.height = (int)ceil (clipLogical.getHeight() - EPS);
            if (clipPixel.width <  MIN_SIZE) {
                clipPixel.x    -= (MIN_SIZE - clipPixel.width)/2;
                clipPixel.width =  MIN_SIZE;
            }
            if (clipPixel.height < MIN_SIZE) {
                clipPixel.y     -= (MIN_SIZE - clipPixel.height)/2;
                clipPixel.height = MIN_SIZE;
            }
            /*
             * Vérifie que les coordonnées obtenues sont bien
             * dans les limites de la dimension de l'image.
             */
            final int clipX2 = min(width,  clipPixel.width  + clipPixel.x);
            final int clipY2 = min(height, clipPixel.height + clipPixel.y);
            if (clipPixel.x < 0) clipPixel.x = 0;
            if (clipPixel.y < 0) clipPixel.y = 0;
            clipPixel.width  = clipX2 - clipPixel.x;
            clipPixel.height = clipY2 - clipPixel.y;
            /*
             * Vérifie que la largeur du rectangle est un
             * multiple entier de la fréquence d'échantillonage.
             */
            clipPixel.width  = (clipPixel.width  / xSubsampling) * xSubsampling;
            clipPixel.height = (clipPixel.height / ySubsampling) * ySubsampling;
            if (clipPixel.isEmpty()) {
                return null;
            }
            /*
             * Recomputes the "clipLogical" envelope. Typically it doesn't change much
             * compared to the value it had before to be overwriten. But it may still
             * change because of rounding error.
             */
            clipLogical = XAffineTransform.transform(gridToCRS, clipLogical, clipLogical);
        }
        CoordinateReferenceSystem coverageCRS = settings.coverageCRS;
        final DefaultTemporalCRS  temporalCRS = settings.getTemporalCRS();
        final double tmin = temporalCRS.toValue(new Date(startTime));
        final double tmax = temporalCRS.toValue(new Date(  endTime));
        if (Double.isInfinite(tmin) && Double.isInfinite(tmax)) {
            // TODO : Attention getCRS2D ne tient pas compte des dimensions des GridCoverages
            coverageCRS = CRSUtilities.getCRS2D(coverageCRS);
        }
        final GeneralEnvelope envelope = new GeneralEnvelope(coverageCRS);
        switch (coverageCRS.getCoordinateSystem().getDimension()) {
            default: // Fall through (apply also for all cases below)
            case  3: envelope.setRange(2, tmin, tmax);
            case  2: envelope.setRange(1, clipLogical.getMinY(), clipLogical.getMaxY());
            case  1: envelope.setRange(0, clipLogical.getMinX(), clipLogical.getMaxX());
            case  0: break;
        }
        return envelope;
    }

    /**
     * Retourne l'image correspondant à cette entrée. Cette méthode délègue son travail à
     * <code>{@linkplain #getCoverage(IIOListeners) getCoverage}(null)</code>.
     */
    public GridCoverage2D getCoverage() throws IOException {
        return getCoverage(null);
    }

    /**
     * Retourne l'image correspondant à cette entrée. Si l'image avait déjà été lue précédemment et
     * qu'elle n'a pas encore été réclamée par le ramasse-miette, alors l'image existante sera
     * retournée sans qu'une nouvelle lecture du fichier ne soit nécessaire. Si au contraire l'image
     * n'était pas déjà en mémoire, alors un décodage du fichier sera nécessaire.
     * <p>
     * Cette méthode ne décodera pas nécessairement l'ensemble de l'image. La partie décodée dépend de la
     * {@linkplain GridCoverageTable#setGeographicBoundingBox région géographique} et de la {@linkplain
     * GridCoverageTable#setPreferredResolution résolution} qui étaient actifs au moment où
     * {@link GridCoverageTable#getEntries} a été appelée (les changement subséquents des paramètres
     * de {@link GridCoverageTable} n'ont pas d'effets sur les {@code GridCoverageEntry} déjà créés).
     *
     * @todo Current implementation requires a {@link FormatEntry} implementation.
     */
    public GridCoverage2D getCoverage(final IIOListeners listeners) throws IOException {
        /*
         * NOTE SUR LES SYNCHRONISATIONS: Cette méthode est synchronisée à plusieurs niveau:
         *
         *  1) Toute la méthode sur 'this',  afin d'éviter qu'une image ne soit lue deux fois
         *     si un thread tente d'accéder à la cache alors que l'autre thread n'a pas eu le
         *     temps de placer le résultat de la lecture dans cette cache.   Synchroniser sur
         *     'this' ne devrait pas avoir d'impact significatif sur la performance,    étant
         *     donné que l'opération vraiment longue (la lecture de l'image) est synchronisée
         *     sur 'format' de toute façon (voir prochain item).
         *
         *  2) La lecture de l'image sur 'format'. On ne synchronise pas toute la méthode sur
         *     'format' afin de ne pas bloquer l'accès à la cache pour un objet 'CoverageReference'
         *     donné pendant qu'une lecture est en cours sur un autre objet 'CoverageReference' qui
         *     utiliserait le même format.
         *
         *  3) Les demandes d'annulation de lecture (abort) sur FormatEntry.enqueued, afin de
         *     pouvoir être faite pendant qu'une lecture est en cours. Cette synchronisation
         *     est gérée en interne par FormatEntry.
         */

        /*
         * Vérifie d'abord si l'image demandée se trouve déjà en mémoire. Si
         * oui, elle sera retournée et la méthode se termine immédiatement.
         */
        if (gridCoverage != null) {
            final GridCoverage2D coverage = gridCoverage.get();
            if (coverage != null) {
                return coverage;
            }
            gridCoverage = null;
        }
        /*
         * Obtient les coordonnées pixels et les coordonnées logiques de la région à extraire.
         */
        final Rectangle       clipPixel   = new Rectangle();
        final Point           subsampling = new Point();
        final GeneralEnvelope envelope;
        try {
            envelope = computeBounds(clipPixel, subsampling);
        } catch (TransformException exception) {
            throw new IIOException(exception.getLocalizedMessage(), exception);
        }
        if (envelope == null) {
            return null;
        }
        /*
         * Avant d'effectuer la lecture, vérifie si l'image est déjà en mémoire. Une image
         * {@link RenderedGridCoverage} peut être en mémoire même si {@link GridCoverage2D}
         * ne l'est plus si, par exemple, l'image est entrée dans une chaîne d'opérations de JAI.
         */
        RenderedImage image = null;
        if (renderedImage != null) {
            image = renderedImage.get();
            if (image == null) {
                renderedImage = null;
                LOGGER.fine("Charge une nouvelle fois les données de \"" + getName() + "\".");
            }
        }
        final FormatEntry format = (FormatEntry) series.getFormat();
        final Object input = getInput();
        final GridSampleDimension[] bands;
        try {
            format.setReading(this, true);
            synchronized (format) {
                final ImageReadParam param = format.getDefaultReadParam(input);
                if (!clipPixel.isEmpty()) {
                    param.setSourceRegion(clipPixel);
                }
                param.setSourceSubsampling(subsampling.x,   subsampling.y,
                                           subsampling.x/2, subsampling.y/2);
                if (band != 0) {
                    // Selects a particular depth in a 3D coverage.
                    param.setSourceBands(new int[] {band});
                }
                final int imageIndex;
                if (handleSpecialCases(param) || format.getImageFormat().equalsIgnoreCase("RAW")) {
                    imageIndex = 0; // The index has been processed by 'handleSpecialCases'.
                } else {
                    imageIndex = (index != 0) ? index-1 : 0;
                }
                if (image == null) {
                    final Dimension size = geometry.getSize();
                    image = format.read(input, imageIndex, param, listeners, size, this);
                    if (image == null) {
                        return null;
                    }
                }
                bands = format.getSampleDimensions(param);
            }
        } finally {
            format.setReading(this, false);
        }
        /*
         * La lecture est terminée et n'a pas été annulée. On construit maintenant l'objet
         * GridCoverage2D, on le conserve dans une cache interne puis on le retourne.
         */
        final Map properties = Collections.singletonMap(REFERENCE_KEY, this);
        final GridCoverageFactory factory = GridCoveragePool.DEFAULT.factory;
        GridCoverage2D coverage;
        if (bands != null && bands.length != 0) {
            coverage = factory.create(filename, image, envelope, bands, null, properties);
        } else {
            // No SampleDimension in the database. Lets the factory use default ones.
            coverage = factory.create(filename, image, envelope, null, null, properties);
        }
        /*
         * Retourne toujours la version "géophysique" de l'image.
         */
        coverage = coverage.view(ViewType.GEOPHYSICS);
        /*
         * Si l'utilisateur a spécifié une operation à appliquer
         * sur les images, applique cette opération maintenant.
         */
        Operation operation = settings.operation;
        if (operation == null) {
            operation = Operation.DEFAULT;
        }
        coverage = (GridCoverage2D) operation.doOperation(coverage);
        renderedImage = new WeakReference<RenderedImage>(image);
        gridCoverage  = new SoftReference<GridCoverage2D>(coverage);
        /*
         * Calcule la quantité de mémoire utilisée par l'image. Si la quantité totale utilisée par
         * les dernières images dépasse un seuil maximal, alors les images les plus anciennes veront
         * leurs références molles transformées en références faibles.
         */
        memoryUsage = (int) Math.min(GridCoveragePool.DEFAULT.addMemoryUsage(this, image), Integer.MAX_VALUE);
        return coverage;
    }

    /**
     * Returns an estimation of memory usage in bytes, or 0 if unknown.
     */
    final int getMemoryUsage() {
        return memoryUsage;
    }

    /**
     * Replace {@link #gridCoverage} soft reference by a weak reference. This method is invoked
     * when it has been decided that the memory allocated to the {@link GridCoverage2D} should
     * be collected.
     */
    final synchronized void clearSoftReference() {
        if (gridCoverage instanceof SoftReference) {
            final GridCoverage2D coverage = gridCoverage.get();
            gridCoverage = (coverage!=null) ? new WeakReference<GridCoverage2D>(coverage) : null;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void abort() {
        final Format format = series.getFormat();
        if (format instanceof FormatEntry) {
            ((FormatEntry) format).abort(this);
        }
    }

    /**
     * Indique si cette image a au moins la résolution spécifiée.
     *
     * @param  resolution Résolution désirée, exprimée selon le CRS de la table d'images.
     * @return {@code true} si la résolution de cette image est égale ou supérieure à la résolution
     *         demandée. Cette méthode retourne {@code false} si {@code resolution} était nul.
      */
    final boolean hasEnoughResolution(final Dimension2D resolution) {
        final GridRange gridRange = geometry.gridRange;
        final int width  = gridRange.getLength(0);
        final int height = gridRange.getLength(1);
        final XRectangle2D boundingBox = geometry.geographicEnvelope;
        return (resolution != null) &&
               (1+EPS)*resolution.getWidth()  >= boundingBox.getWidth() /width &&
               (1+EPS)*resolution.getHeight() >= boundingBox.getHeight()/height;
    }

    /**
     * Si les deux images couvrent les mêmes coordonnées spatio-temporelles, retourne celle qui a
     * la plus basse résolution. Si les deux images ne couvrent pas les mêmes coordonnées ou si
     * leurs résolutions sont incompatibles, alors cette méthode retourne {@code null}.
     */
    final GridCoverageEntry getLowestResolution(final GridCoverageEntry that) {
        final GridRange gridRange  = this.geometry.gridRange;
        final GridRange gridRange2 = that.geometry.gridRange;
        final int width   = gridRange .getLength(0);
        final int height  = gridRange .getLength(1);
        final int width2  = gridRange2.getLength(0);
        final int height2 = gridRange2.getLength(1);
        if (this.startTime == that.startTime &&
            this.endTime   == that.endTime   &&
            this.band      == that.band      &&
            geometry.sameEnvelope(that.geometry) &&
            Utilities.equals(this.series.getLayer(), that.series.getLayer()) &&
            CRS.equalsIgnoreMetadata(settings.tableCRS, that.settings.tableCRS))
        {
            if (width <= width2 && height <= height2) return this;
            if (width >= width2 && height >= height2) return that;
        }
        return null;
    }

    /**
     * Compares two entries on the same criterion than the one used in the SQL {@code "ORDER BY"}
     * statement of {@link GridCoverageTable}). Entries without date are treated as unordered.
     */
    final boolean equalsAsSQL(final GridCoverageEntry other) {
        if (startTime == Long.MIN_VALUE && endTime == Long.MAX_VALUE) {
            return false;
        }
        return endTime == other.endTime;
    }

    /**
     * Indique si cette entrée est identique à l'entrée spécifiée. Cette méthode vérifie
     * tous les paramètres de {@code GridCoverageEntry}, incluant le chemin de l'image et
     * les coordonnées géographiques de la région qui a été demandée. Si vous souhaitez
     * seulement vérifier si deux objets {@code GridCoverageEntry} décrivent bien la même
     * image (même si les coordonnées de la région demandée sont différentes), comparez plutôt
     * leur identifiant {@link #getName}. Notez que cette dernière solution n'est valide que si
     * les deux objets {@code GridCoverageEntry} proviennent de la même base de données.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (super.equals(object)) {
            final GridCoverageEntry that = (GridCoverageEntry) object;
            return this.band      == that.band      &&
                   this.index     == that.index     &&
                   this.startTime == that.startTime &&
                   this.endTime   == that.endTime   &&
                   Utilities.equals(this.series,   that.series    ) &&
                   Utilities.equals(this.filename, that.filename  ) &&
                   Utilities.equals(this.geometry, that.geometry  ) &&
                   Utilities.equals(this.settings, that.settings);
        }
        return false;
    }

    /**
     * Returns a string representation of this entry.
     */
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder(40);
        buffer.append(Classes.getShortClassName(this)).append('[').append(getName());
        if (startTime!=Long.MIN_VALUE && endTime!=Long.MAX_VALUE) {
            buffer.append(" (").append(settings.format(new Date((startTime+endTime)/2))).append(')');
        }
        buffer.append(' ')
              .append(GeographicBoundingBoxImpl.toString(getGeographicBoundingBox(),
                      GridCoverageTable.ANGLE_PATTERN, null))
              .append(']');
        return buffer.toString();
    }

    /**
     * Après la lecture binaire, remplace l'entrée lue par une entrée qui se trouvaient
     * déjà en mémoire, si une telle entrée existe. Ce remplacement augmente les chances
     * que la méthode {@code #getCoverage} retourne une image qui se trouvait déjà en mémoire.
     *
     * @see #unique
     */
    protected final Object readResolve() throws ObjectStreamException {
        return unique();
    }
}
