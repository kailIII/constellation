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
package net.sicade.observation.coverage.sql;

import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.opengis.coverage.Coverage;
import org.opengis.geometry.Envelope;
import org.opengis.metadata.extent.GeographicBoundingBox;
import org.geotools.measure.Latitude;
import org.geotools.measure.Longitude;
import org.geotools.geometry.GeneralDirectPosition;
import org.geotools.coverage.CoverageStack;
import org.geotools.resources.Utilities;
import org.geotools.resources.geometry.XRectangle2D;
import org.geotools.metadata.iso.extent.GeographicBoundingBoxImpl;

import net.sicade.observation.CatalogException;
import net.sicade.observation.coverage.Layer;
import net.sicade.observation.coverage.Operation;
import net.sicade.observation.coverage.CoverageReference;
import net.sicade.observation.coverage.DataAvailability;
import net.sicade.observation.coverage.CoverageComparator;
import net.sicade.observation.coverage.rmi.DataConnection;
import net.sicade.observation.sql.BoundedSingletonTable;
import net.sicade.observation.sql.SpatialColumn;
import net.sicade.observation.sql.Column;
import net.sicade.observation.sql.Use;
import net.sicade.observation.sql.UsedBy;
import net.sicade.observation.sql.Database;
import net.sicade.observation.sql.QueryType;
import net.sicade.resources.i18n.Resources;
import net.sicade.resources.i18n.ResourceKeys;
import static net.sicade.observation.sql.QueryType.*;


/**
 * Connexion vers une table d'images. Cette table contient des références vers des images sous
 * forme d'objets {@link CoverageReference}. Une table {@code GridCoverageTable} est capable
 * de fournir la liste des images qui interceptent une certaines région géographique et une
 * certaine plage de dates.
 *
 * @version $Id$
 * @author Martin Desruisseaux
 */
@Use({FormatTable.class, CoordinateReferenceSystemTable.class})
@UsedBy(LayerTable.class)
public class GridCoverageTable extends BoundedSingletonTable<CoverageReference> implements DataConnection {
    /**
     * Requête SQL utilisée pour obtenir l'enveloppe spatio-temporelle couverte
     * par toutes les images d'une couche (ou de l'ensemble des couches).
     */
//     static final SpatialConfigurationKey BOUNDING_BOX = new SpatialConfigurationKey("GridCoverages:BOX",
//            "SELECT MIN(\"startTime\") "           + "AS \"tmin\", "  +
//                   "MAX(\"endTime\") "             + "AS \"tmax\", "  +
//                   "MIN(\"westBoundLongitude\") "  + "AS \"xmin\", "  +
//                   "MAX(\"eastBoundLongitude\") "  + "AS \"xmax\", "  +
//                   "MIN(\"southBoundLatitude\") "  + "AS \"ymin\", "  +
//                   "MAX(\"northBoundLatitude\") "  + "AS \"ymax\"\n"  +
//                   "MIN(\"altitudeMin\") "         + "AS \"zmin\"\n"  +
//                   "MAX(\"altitudeMax\") "         + "AS \"zmax\"\n"  +
//             "  FROM \"GridCoverages\"\n"          +
//             "  JOIN \"GridGeometries\" ON extent=\"GridGeometries\".id\n"        +
//             "  JOIN \"Series\" ON layer=\"Series\".identifier\n"                 +
//             " WHERE (  \"endTime\" IS NULL OR   \"endTime\" >= ?)\n"             +
//             "   AND (\"startTime\" IS NULL OR \"startTime\" <= ?)\n"             +
//             "   AND (\"eastBoundLongitude\">=? AND \"westBoundLongitude\"<=?)\n" +
//             "   AND (\"northBoundLatitude\">=? AND \"southBoundLatitude\"<=?)\n" +
//             "   AND (series LIKE ?) AND visible=TRUE\n",
//
//            "SELECT MIN(\"startTime\") AS \"tmin\", " +
//                   "MAX(\"endTime\") AS \"tmax\", " +
//                   "\"spatialExtent\"\n"  +
//             "  FROM \"GridCoverages\"\n" +
//             "  JOIN \"GridGeometries\" ON extent=\"GridGeometries\".id\n" +
//             "  JOIN \"Series\" ON layer=\"Series\".identifier\n"          +
//             " WHERE (  \"endTime\" IS NULL OR   \"endTime\" >= ?)\n"      +
//             "   AND (\"startTime\" IS NULL OR \"startTime\" <= ?)\n"      +
//             "   AND (\"spatialExtent\" && ?)\n"                           +
//             "   AND (series LIKE ?) AND visible=TRUE\n");

    /**
     * Le modèle à utiliser pour formatter des angles.
     */
    static final String ANGLE_PATTERN = "D°MM.m'";

    /**
     * Réference vers la couche d'images.
     */
    private Layer layer;

    /**
     * L'opération à appliquer sur les images lue, ou {@code null} s'il n'y en a aucune.
     */
    private Operation operation;

    /**
     * Dimension logique (en degrés de longitude et de latitude) désirée des pixels
     * de l'images. Cette information n'est qu'approximative. Il n'est pas garantie
     * que les lectures produiront effectivement des images de cette résolution.
     * Une valeur nulle signifie que les lectures doivent se faire avec la meilleure
     * résolution possible.
     */
    private Dimension2D resolution;

    /**
     * Index des ordonnées dans une position géographique qui correspondent aux coordonnées
     * (<var>x</var>,<var>y</var>) dans une image.
     *
     * @todo Codés en dur pour l'instant. Peut avoir besoin d'être paramètrables dans une
     *       version future.
     */
    private static final int xDimension=0, yDimension=1;

    /**
     * Formatteur à utiliser pour écrire des dates pour le journal. Les caractères et les
     * conventions linguistiques dépendront de la langue de l'utilisateur. Toutefois, le
     * fuseau horaire devrait être celui de la région d'étude (ou GMT) plutôt que celui
     * du pays de l'utilisateur.
     */
    private final DateFormat dateFormat;

    /**
     * Table des systèmes de coordonnées. Ne sera construit que la première fois où elle
     * sera nécessaire.
     */
    private transient CoordinateReferenceSystemTable crsTable;

    /**
     * Table des formats. Cette table ne sera construite que la première fois
     * où elle sera nécessaire.
     */
    private transient FormatTable formatTable;

    /**
     * Le comparateur à utiliser pour choisir une image parmis un ensemble d'images interceptant
     * les coordonnées spatio-temporelles spécifiées. Ne sera construit que la première fois où
     * il sera nécessaire.
     */
    private transient CoverageComparator comparator;

    /**
     * Envelope spatio-temporelle couvertes par l'ensemble des images de cette table, ou
     * {@code null} si elle n'a pas encore été déterminée. Cette envelope est calculée par
     * {@link BoundedSingletonTable#getEnvelope} et cachée ici pour des raisons de performances.
     */
    private transient Envelope envelope;

    /**
     * Derniers paramètres à avoir été construits. Ces paramètres sont
     * retenus afin d'éviter d'avoir à les reconstruires trop souvent
     * si c'est évitable.
     */
    private transient Parameters parameters;

    /**
     * The set of available depths for each dates. Will be computed by
     * {@link #getAvailableCentroids} when first needed.
     */
    private transient Map<Date, Set<Number>> availableCentroids;

    /**
     * Une vue tri-dimensionnelle de toutes les données d'une couche.
     * Ne sera construite que la première fois où elle sera nécessaire.
     */
    private transient CoverageStack coverage3D;

    /**
     * Une instance d'une coordonnées à utiliser avec {@link #evaluate}.
     */
    private transient GeneralDirectPosition position;

    /**
     * Un buffer pré-alloué à utiliser avec {@link #evaluate}.
     */
    private transient double[] samples;

    /**
     * Construit une table pour la connexion spécifiée.
     *
     * @param  database Connexion vers la base de données d'observations.
     * @throws SQLException if an error occured while reading the database.
     */
    public GridCoverageTable(final Database database) throws SQLException {
        super(new GridCoverageQuery(database), net.sicade.observation.sql.CRS.XYT);
        this.dateFormat = DateFormat.getDateInstance(DateFormat.LONG);
        this.dateFormat.setTimeZone(database.getTimeZone());
    }

    /**
     * Construit une nouvelle table avec la même configuration initiale que celle de la table
     * spécifiée.
     */
    public GridCoverageTable(final GridCoverageTable table) {
        super(table);
        layer       = table.layer;
        operation   = table.operation;
        resolution  = table.resolution;
        dateFormat  = table.dateFormat;
        crsTable    = table.crsTable;
        formatTable = table.formatTable;
        comparator  = table.comparator;
        parameters  = table.parameters;
        coverage3D  = table.coverage3D;
    }

    /**
     * {inheritDoc}
     */
    public GridCoverageTable newInstance(final Operation operation) {
        final GridCoverageTable view = new GridCoverageTable(this);
        view.setOperation(operation);
        return view;
    }

    /**
     * Retourne la référence vers la couche d'images.
     */
    public Layer getLayer() {
        return layer;
    }

    /**
     * Définit la couche dont on veut les images.
     *
     * @param  layer Réference vers la couche d'images.
     */
    public synchronized void setLayer(final Layer layer) {
        if (!layer.equals(this.layer)) {
            clearCacheKeepEntries();
            this.layer = layer;
            fireStateChanged("Layer");
            log("setLayer", Level.CONFIG, ResourceKeys.SET_SERIES_$1, layer.getName());
        }
    }

    /**
     * Sets the later as a string. This methode should be avoided as much as possible -
     * use {@link #setLayer(Layer)} instead. However it still useful for debugging purpose.
     */
    public void setLayer(final String layer) {
        setLayer(new LayerEntry(layer, null, null, 1, null));
    }

    /**
     * Définit la période de temps d'intérêt (dans laquelle rechercher des images).
     */
    @Override
    public synchronized boolean setTimeRange(final Date startTime, final Date endTime) {
        final boolean change = super.setTimeRange(startTime, endTime);
        if (change) {
            clearCacheKeepEntries();
            final String startText, endText;
            synchronized (dateFormat) {
                startText = dateFormat.format(startTime);
                endText   = dateFormat.format(  endTime);
            }
            log("setTimeRange", Level.CONFIG, ResourceKeys.SET_TIME_RANGE_$3,
                                new String[]{startText, endText, layer.getName()});
        }
        return change;
    }

    /**
     * Définit la région géographique d'intérêt dans laquelle rechercher des images.
     */
    @Override
    public synchronized boolean setGeographicBoundingBox(final GeographicBoundingBox area) {
        final boolean change = super.setGeographicBoundingBox(area);
        if (change) {
            clearCache();
            log("setGeographicArea", Level.CONFIG, ResourceKeys.SET_GEOGRAPHIC_AREA_$2, new String[] {
                GeographicBoundingBoxImpl.toString(area, ANGLE_PATTERN, getDatabase().getLocale()),
                layer.getName()
            });
        }
        return change;
    }

    /**
     * Retourne la dimension désirée des pixels de l'images.
     *
     * @return Résolution préférée, ou {@code null} si la lecture doit se faire avec
     *         la meilleure résolution disponible.
     */
    public synchronized Dimension2D getPreferredResolution() {
        return (resolution!=null) ? (Dimension2D)resolution.clone() : null;
    }

    /**
     * Définit la dimension désirée des pixels de l'images.  Cette information n'est
     * qu'approximative. Il n'est pas garantie que la lecture produira effectivement
     * des images de cette résolution. Une valeur nulle signifie que la lecture doit
     * se faire avec la meilleure résolution disponible.
     *
     * @param  pixelSize Taille préférée des pixels, en degrés de longitude et de latitude.
     */
    public synchronized void setPreferredResolution(final Dimension2D pixelSize) {
        if (!Utilities.equals(resolution, pixelSize)) {
            clearCache();
            final int clé;
            final Object param;
            if (pixelSize != null) {
                resolution = (Dimension2D)pixelSize.clone();
                clé = ResourceKeys.SET_RESOLUTION_$3;
                param = new Object[] {
                    new Double(resolution.getWidth()),
                    new Double(resolution.getHeight()),
                    layer.getName()
                };
            } else {
                resolution = null;
                clé = ResourceKeys.UNSET_RESOLUTION_$1;
                param = layer.getName();
            }
            fireStateChanged("PreferredResolution");
            log("setPreferredResolution", Level.CONFIG, clé, param);
        }
    }

    /**
     * Retourne l'opération appliquée sur les images lues. L'opération retournée
     * peut représenter par exemple un gradient. Si aucune opération n'est appliquée
     * (c'est-à-dire si les images retournées représentent les données originales),
     * alors cette méthode retourne {@code null}.
     */
    public Operation getOperation() {
        return operation;
    }

    /**
     * Définit l'opération à appliquer sur les images lues.
     *
     * @param  operation L'opération à appliquer sur les images, ou {@code null} pour
     *         n'appliquer aucune opération.
     */
    public synchronized void setOperation(final Operation operation) {
        if (!Utilities.equals(operation, this.operation)) {
            clearCache();
            this.operation = operation;
            final int clé;
            final Object param;
            if (operation != null) {
                param = new String[] {operation.getName(), layer.getName()};
                clé   = ResourceKeys.SET_OPERATION_$2;
            } else {
                param = layer.getName();
                clé   = ResourceKeys.UNSET_OPERATION_$1;
            }
            fireStateChanged("Operation");
            log("setOperation", Level.CONFIG, clé, param);
        }
    }

    /**
     * Retourne la liste des images disponibles dans la plage de coordonnées spatio-temporelles
     * préalablement sélectionnées. Ces plages auront été spécifiées à l'aide des différentes
     * méthodes {@code set...} de cette classe.
     *
     * @return Liste d'images qui interceptent la plage de temps et la région géographique d'intérêt.
     * @throws CatalogException si un enregistrement est invalide.
     * @throws SQLException si la base de données n'a pas pu être interrogée pour une autre raison.
     */
    @Override
    public Set<CoverageReference> getEntries() throws CatalogException, SQLException {
        if (envelope == null) {
            /*
             * getEnvelope() doit être appelée au moins une fois (sauf si l'enveloppe n'a
             * pas changé) avant super.getEntries() afin d'éviter que le java.sql.Statement
             * de QueryType.LIST ne soit fermé en pleine itération pour exécuter le Statement
             * de QueryType.BOUNDING_BOX.
             */
            envelope = getEnvelope();
        }
        final  Set<CoverageReference> entries  = super.getEntries();
        final List<CoverageReference> filtered = new ArrayList<CoverageReference>(entries.size());
loop:   for (final CoverageReference newReference : entries) {
            if (newReference instanceof GridCoverageEntry) {
                final GridCoverageEntry newEntry = (GridCoverageEntry) newReference;
                /*
                 * Vérifie si une entrée existait déjà précédemment pour les mêmes coordonnées
                 * spatio-temporelle mais une autre résolution. Si c'était le cas, alors l'entrée
                 * avec une résolution proche de la résolution demandée sera retenue et les autres
                 * retirées de la liste.
                 */
                for (int i=filtered.size(); --i>=0;) {
                    final CoverageReference oldReference = filtered.get(i);
                    if (oldReference instanceof GridCoverageEntry) {
                        final GridCoverageEntry oldEntry = (GridCoverageEntry) oldReference;
                        if (!oldEntry.compare(newEntry)) {
                            // Entries not equals according the "ORDER BY" clause.
                            break;
                        }
                        final GridCoverageEntry lowestResolution = oldEntry.getLowestResolution(newEntry);
                        if (lowestResolution != null) {
                            // Two entries has the same spatio-temporal coordinates.
                            if (lowestResolution.hasEnoughResolution(resolution)) {
                                // The entry with the lowest resolution is enough.
                                filtered.set(i, lowestResolution);
                            } else if (lowestResolution == oldEntry) {
                                // No entry has enough resolution;
                                // keep the one with the finest resolution.
                                filtered.set(i, newEntry);
                            }
                            continue loop;
                        }
                    }
                }
            }
            filtered.add(newReference);
        }
        entries.retainAll(filtered);
        log("getEntries", Level.FINE, ResourceKeys.FOUND_COVERAGES_$1, entries.size());
        return entries;
    }

    /**
     * Retourne une des images disponibles dans la plage de coordonnées spatio-temporelles
     * préalablement sélectionnées. Si plusieurs images interceptent la région et la plage
     * de temps (c'est-à-dire si {@link #getEntries} retourne un ensemble d'au moins deux
     * entrées), alors le choix de l'image se fera en utilisant un objet
     * {@link CoverageComparator} par défaut.
     *
     * @return Une image choisie arbitrairement dans la région et la plage de date
     *         sélectionnées, ou {@code null} s'il n'y a pas d'image dans ces plages.
     * @throws CatalogException si un enregistrement est invalide.
     * @throws SQLException si la base de données n'a pas pu être interrogée pour une autre raison.
     */
    public synchronized CoverageReference getEntry() throws CatalogException, SQLException {
        /*
         * Obtient la liste des entrées avant toute opération impliquant l'envelope,
         * puisque cette envelope peut avoir été calculée par 'getEntries()'.
         */
        final Set<CoverageReference> entries = getEntries();
        assert getEnvelope().equals(envelope) : envelope; // Vérifie que l'enveloppe n'a pas changée.
        CoverageReference best = null;
        if (comparator == null) {
            comparator = new CoverageComparator(getCoordinateReferenceSystem(), envelope);
        }
        for (final CoverageReference entry : entries) {
            if (best==null || comparator.compare(entry, best) <= -1) {
                best = entry;
            }
        }
        return best;
    }

    /**
     * Retourne l'entrée pour le nom de fichier spécifié. Ces noms sont habituellement unique pour
     * une couche donnée (mais pas obligatoirement). En cas de doublon, une exception sera lancée.
     *
     * @param  name Le nom du fichier.
     * @return L'entrée demandée, ou {@code null} si {@code name} était nul.
     * @throws CatalogException si aucun enregistrement ne correspond au nom demandé,
     *         ou si un enregistrement est invalide.
     * @throws SQLException si l'interrogation de la base de données a échoué pour une autre raison.
     */
    @Override
    public synchronized CoverageReference getEntry(final String name) throws CatalogException, SQLException {
        if (name == null) {
            return null;
        }
        if (envelope == null) {
            envelope = getEnvelope();
            // Voir le commentaire du code équivalent de 'getEntries()'
        }
        return super.getEntry(name);
    }

    /**
     * Returns the set of dates for which a coverage is available. Only the images in
     * the currently {@linkplain #setTimeRange selected time range} are considered.
     *
     * @return The set of dates.
     * @throws SQLException If an error occured while reading the database.
     */
    public Set<Date> getAvailableTimes() throws SQLException {
        return getAvailableCentroids().keySet();
    }

    /**
     * Returns the available altitudes for each dates. This method returns portion of "centroids",
     * i.e. vertical ranges are replaced by the middle vertical points and temporal ranges are
     * replaced by the middle time. This method considers only the vertical and temporal axis.
     * The horizontal axis are omitted.
     *
     * @return An immutable collection of centroids. Keys are the dates, are values ar the set
     *         of altitudes for that date.
     * @throws SQLException If an error occured while reading the database.
     */
    public synchronized Map<Date, Set<Number>> getAvailableCentroids() throws SQLException {
        if (availableCentroids == null) {
            availableCentroids = new TreeMap<Date, Set<Number>>();
            final GridCoverageQuery  query     = (GridCoverageQuery) super.query;
            final Calendar           calendar  = getCalendar();
            final PreparedStatement  statement = getStatement(QueryType.AVAILABLE_DATA);
            final ResultSet          results   = statement.executeQuery();
            final int startTimeIndex = indexOf(query.startTime);
            final int endTimeIndex   = indexOf(query.  endTime);
            final int zminIndex      = indexOf(query.zmin);
            final int zmaxIndex      = indexOf(query.zmax);
            while (results.next()) {
                final Date startTime = results.getTimestamp(startTimeIndex, calendar);
                final Date   endTime = results.getTimestamp(  endTimeIndex, calendar);
                final Date      time;
                if (startTime != null) {
                    if (endTime != null) {
                        time = new Date((startTime.getTime() + endTime.getTime()) / 2);
                    } else {
                        time = new Date(startTime.getTime());
                    }
                } else if (endTime != null) {
                    time = new Date(endTime.getTime());
                } else {
                    continue;
                }
                Set<Number> depths = availableCentroids.get(time);
                if (depths == null) {
                    depths = new TreeSet<Number>();
                    availableCentroids.put(time, depths);
                }
                double zmin = results.getDouble(zminIndex); if (results.wasNull()) zmin=Double.NEGATIVE_INFINITY;
                double zmax = results.getDouble(zmaxIndex); if (results.wasNull()) zmax=Double.POSITIVE_INFINITY;
                double z = (zmin + zmax) / 2;
                if (!Double.isNaN(z) && !Double.isInfinite(z)) {
                    depths.add(z);
                }
            }
            /*
             * Replaces the depths tree by shared instances, in order to reduce memory usage.
             * It is quite common to have many dates (if not all) associated with identical
             * set of depth values.
             */
            final Map<Set<Number>, Set<Number>> pool = new HashMap<Set<Number>, Set<Number>>();
            for (final Map.Entry<Date, Set<Number>> entry : availableCentroids.entrySet()) {
                Set<Number> current = entry.getValue();
                Set<Number> shared  = pool.get(current);
                if (shared == null) {
                    shared = Collections.unmodifiableSet(current);
                    pool.put(current, shared);
                }
                entry.setValue(shared);
            }
            availableCentroids = Collections.unmodifiableMap(availableCentroids);
        }
        return availableCentroids;
    }

    /**
     * Obtient les plages de temps et de coordonnées des images. L'objet retourné ne contiendra que
     * les informations demandées. Par exemple si {@link DataAvailability#t} est {@code null}, alors
     * la plage de temps ne sera pas examinée.
     *
     * @param  ranges L'objet dans lequel ajouter les plages de cette couche. Pour chaque champs
     *         nul dans cet objet, les informations correspondantes ne seront pas interrogées.
     * @return Un objet contenant les plages demandées. Il ne s'agira pas nécessairement du même
     *         objet que celui qui a été spécifié en argument; ça dépendra si cette méthode est
     *         appelée localement ou sur une machine distante.
     * @throws SQLException si la base de données n'a pas pu être interrogée.
     */
    public synchronized DataAvailability getRanges(final DataAvailability ranges)
            throws SQLException
    {
        final GridCoverageQuery query = (GridCoverageQuery) super.query;
        long  lastEndTime        = Long.MIN_VALUE;
        final Calendar calendar  = getCalendar();
        final ResultSet  result  = getStatement(AVAILABLE_DATA).executeQuery();
        final int startTimeIndex = indexOf(query.startTime);
        final int   endTimeIndex = indexOf(query.endTime);
        final int xminIndex      = indexOf(query.xmin);
        final int xmaxIndex      = indexOf(query.xmax);
        final int yminIndex      = indexOf(query.ymin);
        final int ymaxIndex      = indexOf(query.ymax);
        while (result.next()) {
            if (ranges.t != null) {
                final long timeInterval = Math.round(layer.getTimeInterval() * LocationOffsetEntry.DAY);
                final Date    startTime = result.getTimestamp(startTimeIndex, calendar);
                final Date      endTime = result.getTimestamp(  endTimeIndex, calendar);
                if (startTime!=null && endTime!=null) {
                    final long lgEndTime = endTime.getTime();
                    final long checkTime = lgEndTime - timeInterval;
                    if (checkTime <= lastEndTime  &&  checkTime < startTime.getTime()) {
                        // Il arrive parfois que des images soient prises à toutes les 24 heures,
                        // mais pendant 12 heures seulement. On veut éviter que de telles images
                        // apparaissent tout le temps entrecoupées d'images manquantes.
                        startTime.setTime(checkTime);
                    }
                    lastEndTime = lgEndTime;
                    ranges.t.add(startTime, endTime);
                }
            }
            if (ranges.x != null) {
                final double xmin = result.getDouble(xminIndex);
                final double xmax = result.getDouble(xmaxIndex);
                ranges.x.add(new Longitude(xmin), new Longitude(xmax));
            }
            if (ranges.y != null) {
                final double ymin = result.getDouble(yminIndex);
                final double ymax = result.getDouble(ymaxIndex);
                ranges.y.add(new Latitude(ymin), new Latitude(ymax));
            }
        }
        result.close();
        return ranges;
    }

    /**
     * Configure la requête spécifiée. Cette méthode est appelée automatiquement lorsque la table
     * a {@linkplain #fireStateChanged changé d'état}.
     */
    @Override
    @SuppressWarnings("fallthrough")
    protected void configure(final QueryType type, final PreparedStatement statement) throws SQLException {
        super.configure(type, statement);
        final GridCoverageQuery query = (GridCoverageQuery) super.query;
        int index = query.byLayer.indexOf(type);
        if (index != 0) {
            final String name = (layer != null) ? layer.getName() : null;
            statement.setString(index, name);
            // TODO: need to take in account the case where the layer is null.
        }
        index = query.byVisibility.indexOf(type);
        if (index != 0) {
            statement.setBoolean(index, true);
        }
    }

    /**
     * Retourne l'image correspondant à l'enregistrement courant. Les classes dérivées peuvent
     * redéfinir cette méthode si elle souhaite contruire autrement la référence vers l'image.
     */
    @Override
    protected CoverageReference createEntry(final ResultSet result) throws CatalogException, SQLException {
        assert Thread.holdsLock(this);
        final Calendar calendar = getCalendar();
        final GridCoverageQuery query = (GridCoverageQuery) super.query;
        final String layer     = result.getString   (indexOf(query.layer));
        final String series    = result.getString   (indexOf(query.series));
        final String pathname  = result.getString   (indexOf(query.pathname));
        final String filename  = result.getString   (indexOf(query.filename));
        final String extension = result.getString   (indexOf(query.extension));
        final Date   startTime = result.getTimestamp(indexOf(query.startTime), calendar);
        final Date   endTime   = result.getTimestamp(indexOf(query.endTime),   calendar);
        final Envelope envelope;
        final int index = indexOf(query.spatialExtent);
        final Column column = query.getColumns(SELECT).get(index - 1);
        if (column instanceof SpatialColumn.Box) {
            envelope = ((SpatialColumn.Box) column).getEnvelope(result, SELECT);
        } else {
            envelope = SpatialColumn.Box.getEnvelope(result, index);
        }
        final short  width     = result.getShort    (indexOf(query.width));
        final short  height    = result.getShort    (indexOf(query.height));
        final String crs       = result.getString   (indexOf(query.crs));
        final String format    = result.getString   (indexOf(query.format));
        return new GridCoverageEntry(this, layer, series, pathname, filename, extension, startTime,
                    endTime, envelope, width, height, crs, format, null).canonicalize();
    }

    /**
     * Retourne les paramètres de cette table. Pour des raisons d'économie de mémoire (de très
     * nombreux objets {@code Parameters} pouvant être créés), cette méthode retourne un exemplaire
     * unique autant que possible. L'objet retourné ne doit donc pas être modifié!
     * <p>
     * Cette méthode est appelée par le constructeur de {@link GridCoverageEntry}.
     *
     * @param  seriesID  Nom ID de la couche, pour fin de vérification. Ce nom doit correspondre
     *                   à celui de la couche examinée par cette table.
     * @param  formatID  Nom ID du format des images.
     * @param  crsID     Nom ID du système de référence des coordonnées.
     * @param  pathname  Chemin relatif des images.
     * @param  extension Extension (sans le point) des noms de fichier des images à lire.
     *
     * @return Un objet incluant les paramètres demandées ainsi que ceux de la table.
     * @throws CatalogException si les paramètres n'ont pas pu être obtenus.
     * @throws SQLException si une erreur est survenue lors de l'accès à la base de données.
     *
     * @todo L'implémentation actuelle n'accepte pas d'autres impléméntations de Format que FormatEntry.
     */
    final synchronized Parameters getParameters(final String seriesID,
                                                final String formatID,
                                                final String crsID,
                                                final String pathname,
                                                final String extension)
            throws CatalogException, SQLException
    {
        final String seriesName = layer.getName();
        if (!Utilities.equals(seriesID, seriesName)) {
            throw new CatalogException(Resources.format(ResourceKeys.ERROR_WRONG_SERIES_$1, seriesName));
        }
        /*
         * Vérifie que l'enveloppe n'a pas changé. Note: getEnvelope() doit avoir été appelée au
         * moins une fois (sauf si elle n'a pas changée) juste avant super.getEntries(), afin
         * d'éviter que le java.sql.Statement de QueryType.LIST n'aie été fermé pour exécuter
         * le Statement de QueryType.BOUNDING_BOX.
         */
        assert getEnvelope().equals(envelope) : envelope;
        /*
         * Si les paramètres spécifiés sont identiques à ceux qui avaient été
         * spécifiés la dernière fois, retourne le dernier bloc de paramètres.
         */
        if (parameters != null &&
            Utilities.equals(parameters.format     .getName(), formatID) &&
            Utilities.equals(parameters.coverageCRS.getName(), crsID)    &&
            Utilities.equals(parameters.pathname,              pathname) &&
            Utilities.equals(parameters.extension,             extension))
        {
            return parameters;
        }
        /*
         * Construit un nouveau bloc de paramètres et projète les
         * coordonnées vers le système de coordonnées de l'image.
         */
        final Rectangle2D geographicArea = XRectangle2D.createFromExtremums(
                            envelope.getMinimum(xDimension), envelope.getMinimum(yDimension),
                            envelope.getMaximum(xDimension), envelope.getMaximum(yDimension));
        if (formatTable == null) {
            formatTable = getDatabase().getTable(FormatTable.class);
        }
        if (crsTable == null) {
            crsTable = getDatabase().getTable(CoordinateReferenceSystemTable.class);
        }
        parameters = new Parameters(layer,
                                    (FormatEntry) formatTable.getEntry(formatID),
                                    pathname.intern(),
                                    extension.intern(),
                                    operation,
                                    getCoordinateReferenceSystem(),
                                    crsTable.getEntry(crsID),
                                    geographicArea,
                                    resolution,
                                    dateFormat,
                                    getProperty(CoverageReference.ROOT_DIRECTORY),
                                    getProperty(CoverageReference.ROOT_URL),
                                    getProperty(CoverageReference.URL_ENCODING));
        return parameters;
    }

    /**
     * Prépare l'évaluation d'un point.
     */
    @SuppressWarnings("fallthrough")
    private void prepare(final double x, final double y, final double t)
            throws CatalogException, SQLException, IOException
    {
        assert Thread.holdsLock(this);
        if (coverage3D == null) {
            coverage3D = new CoverageStack(getLayer().getName(), getCoordinateReferenceSystem(), getEntries());
            position   = new GeneralDirectPosition(getCoordinateReferenceSystem());
        }
        switch (position.ordinates.length) {
            default: // Fall through in all cases.
            case 3:  position.ordinates[2] = t;
            case 2:  position.ordinates[1] = y;
            case 1:  position.ordinates[0] = x;
            case 0:  break;
        }
    }

    /**
     * {@inheritDoc}
     */
    public synchronized double evaluate(final double x, final double y, final double t, final short band)
            throws CatalogException, SQLException, IOException
    {
        prepare(x, y, t);
        samples = coverage3D.evaluate(position, samples);
        return samples[band];
    }

    /**
     * {@inheritDoc}
     */
    public synchronized double[] snap(final double x, final double y, final double t)
            throws CatalogException, SQLException, IOException
    {
        prepare(x, y, t);
        coverage3D.snap(position);
        return (double[]) position.ordinates.clone();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public synchronized List<Coverage> coveragesAt(final double t)
            throws CatalogException, SQLException, IOException
    {
        prepare(Double.NaN, Double.NaN, t);
        return coverage3D.coveragesAt(t);
    }

    /**
     * Vide la cache de toutes les références vers les entrées précédemment créées.
     */
    @Override
    protected void clearCache() {
        super.clearCache();
        clearCacheKeepEntries();
    }

    /**
     * Réinitialise les caches, mais en gardant les références vers les entrées déjà créées.
     * Cette méthode devrait être appellée à la place de {@link #clearCache} lorsque l'état
     * de la table a changé, mais que cet état n'affecte pas les prochaines entrées à créer.
     */
    private void clearCacheKeepEntries() {
        coverage3D = null;
        parameters = null;
        comparator = null;
        envelope   = null;
        availableCentroids = null;
    }

    /**
     * Enregistre un évènement dans le journal.
     */
    private void log(final String method, final Level level, final int clé, final Object param) {
        final Resources resources = Resources.getResources(getDatabase().getLocale());
        final LogRecord record = resources.getLogRecord(level, clé, param);
        record.setSourceClassName("CoverageTable");
        record.setSourceMethodName(method);
        CoverageReference.LOGGER.log(record);
    }

    /**
     * Retourne une chaîne de caractères décrivant cette table.
     */
    @Override
    public final String toString() {
        String area;
        try {
            area = GeographicBoundingBoxImpl.toString(getGeographicBoundingBox(),
                                                      ANGLE_PATTERN, getDatabase().getLocale());
        } catch (CatalogException e) {
            area = e.getLocalizedMessage();
        }
        final StringBuilder buffer = new StringBuilder(Utilities.getShortClassName(this));
        buffer.append("[\"");
        buffer.append(String.valueOf(layer));
        buffer.append("\": ");
        buffer.append(area);
        buffer.append(']');
        return buffer.toString();
    }
}
