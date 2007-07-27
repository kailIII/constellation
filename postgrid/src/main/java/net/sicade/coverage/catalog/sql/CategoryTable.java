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
package net.sicade.coverage.catalog.sql;

import java.net.URL;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.io.IOException;
import java.text.ParseException;

import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.MathTransform1D;
import org.opengis.referencing.operation.MathTransformFactory;
import org.geotools.util.NumberRange;
import org.geotools.coverage.Category;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.referencing.ReferencingFactoryFinder;

import net.sicade.image.Utilities;
import net.sicade.coverage.catalog.Element;
import net.sicade.coverage.catalog.CatalogException;
import net.sicade.coverage.catalog.ServerException;
import net.sicade.coverage.catalog.IllegalRecordException;
import net.sicade.catalog.Database;
import net.sicade.catalog.Table;
import net.sicade.catalog.QueryType;


/**
 * Connection to a table of {@linkplain Category categories}. This table creates a list of
 * {@link Category} objects for a given sample dimension. Categories are one of the components
 * required for creating a {@link GridCoverage2D}; they are not an {@link Element} subinterface.
 *
 * @author Martin Desruisseaux
 * @version $Id$
 */
public class CategoryTable extends Table {
    /**
     * Transformation <code>f(x) = 10<sup>x</sup></code>. Utilisée pour le décodage des images de
     * concentrations en chlorophylle-a. Ne sera construite que la première fois où elle sera
     * nécessaire.
     */
    private transient MathTransform1D exponential;

    /**
     * Creates a category table.
     * 
     * @param database Connection to the database.
     */
    public CategoryTable(final Database database) {
        super(new CategoryQuery(database));
    }

    /**
     * Returns the list of categories for the given sample dimension.
     *
     * @param  band The name of the sample dimension.
     * @return The categories for the given sample dimension.
     * @throws CatalogException if an inconsistent record is found in the database.
     * @throws SQLException if an error occured while reading the database.
     */
    public synchronized Category[] getCategories(final String band) throws CatalogException, SQLException {
        final CategoryQuery query = (CategoryQuery) this.query;
        final PreparedStatement statement = getStatement(QueryType.FILTERED_LIST);
        statement.setString(indexOf(query.byBand), band);
        final int nameIndex     = indexOf(query.name    );
        final int lowerIndex    = indexOf(query.lower   );
        final int upperIndex    = indexOf(query.upper   );
        final int c0Index       = indexOf(query.c0      );
        final int c1Index       = indexOf(query.c1      );
        final int functionIndex = indexOf(query.function);
        final int colorsIndex   = indexOf(query.colors  );
        final List<Category> categories = new ArrayList<Category>();
        final ResultSet result = statement.executeQuery();
        while (result.next()) {
            boolean isQuantifiable = true;
            final String     name = result.getString(nameIndex);
            final int       lower = result.getInt   (lowerIndex);
            final int       upper = result.getInt   (upperIndex);
            final double       c0 = result.getDouble(c0Index); isQuantifiable &= !result.wasNull();
            final double       c1 = result.getDouble(c1Index); isQuantifiable &= !result.wasNull();
            final String function = result.getString(functionIndex);
            final String  colorID = result.getString(colorsIndex);
            /*
             * Procède maintenant au décodage du champ "colors". Ce champ contient
             * une chaîne de caractère qui indique soit le code RGB d'une couleur
             * uniforme, ou soit l'adresse URL d'une palette de couleurs.
             */
            Color[] colors = null;
            if (colorID != null) try {
                colors = decode(colorID);
            } catch (Exception exception) { // Includes IOException and ParseException
                final String table = result.getMetaData().getTableName(colorsIndex);
                result.close();
                throw new IllegalRecordException(table, exception);
            }
            /*
             * Construit une catégorie correspondant à l'enregistrement qui vient d'être lu.
             * Une catégorie peut être qualitative (premier cas), quantitative mais linéaire
             * (deuxième cas), ou quantitative et logarithmique (troisième cas).
             */
            Category category;
            final NumberRange range = new NumberRange(lower, upper);
            if (!isQuantifiable) {
                // Catégorie qualitative.
                category = new Category(name, colors, range, (MathTransform1D)null);
            } else {
                // Catégorie quantitative
                category = new Category(name, colors, range, c1, c0);
                if (function != null) {
                    if (function.equalsIgnoreCase("log")) try {
                        // Catégorie quantitative et logarithmique.
                        final MathTransformFactory factory = ReferencingFactoryFinder.getMathTransformFactory(null);
                        if (exponential == null) {
                            final ParameterValueGroup param = factory.getDefaultParameters("Exponential");
                            param.parameter("base").setValue(10.0); // Must be a 'double'
                            exponential = (MathTransform1D) factory.createParameterizedTransform(param);
                        }
                        MathTransform1D tr = category.getSampleToGeophysics();
                        tr = (MathTransform1D) factory.createConcatenatedTransform(tr, exponential);
                        category = new Category(name, colors, range, tr);
                    } catch (FactoryException exception) {
                        result.close();
                        throw new ServerException(exception);
                    } else {
                        final String table = result.getMetaData().getTableName(functionIndex);
                        result.close();
                        throw new IllegalRecordException(table, "Fonction inconnue: " + function);
                    }
                }
            }
            categories.add(category);
        }
        result.close();
        return categories.toArray(new Category[categories.size()]);
    }

    /**
     * Optient une couleur uniforme ou une palette de couleur. L'argument {@code colors}
     * peut être un code RGB d'une seule couleur (par exemple {@code "#D2C8A0"}), ou un
     * lien URL vers une palette de couleurs (par exemple {@code "SST-Nasa.pal"}).
     *
     * @param  colors Identificateur de la ou les couleurs désirées.
     * @return Palette de couleurs demandée.
     * @throws IOException si les couleurs n'ont pas pu être lues.
     * @throws ParseException si le fichier de la palette de couleurs a été ouvert,
     *         mais qu'elle contient des caractères qui n'ont pas pus être interprétés.
     */
    private static Color[] decode(String colors) throws IOException, ParseException {
        /*
         * Retire les guillements au début et à la fin de la chaîne, s'il y en a.
         * Cette opération vise à éviter des problèmes de compatibilités lorsque
         * l'importation des thèmes dans la base des données s'est senti obligée
         * de placer des guillemets partout.
         */
        if (true) {
            colors = colors.trim();
            final int length = colors.length();
            if (length>=2 && colors.charAt(0)=='"' && colors.charAt(length-1)=='"') {
                colors = colors.substring(1, length-1);
            }
        }
        /*
         * Vérifie si la chaîne de caractère représente un code de couleurs
         * unique, comme par exemple "#D2C8A0". Si oui, ce code sera retourné
         * dans un tableau de longueur 1.
         */
        try {
            return new Color[] {Color.decode(colors)};
        } catch (NumberFormatException exception) {
            /*
             * Le décodage de la chaîne a échoué. C'est peut-être
             * parce qu'il s'agit d'un nom de fichier.  On ignore
             * l'erreur et on continue en essayant de décoder l'URL.
             */
        }
        final URL url = new URL(colors);
        return Utilities.getPaletteFactory(null).getColors(url.getPath());
    }
}
