/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.constellation.map;

import java.awt.Color;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.measure.unit.NonSI;
import javax.measure.unit.Unit;
import org.constellation.catalog.CatalogException;
import org.constellation.catalog.Database;
import org.constellation.coverage.catalog.Layer;
import org.constellation.coverage.catalog.LayerTable;
import org.geotools.display.renderer.GridMarkGraphicBuilder;
import org.geotools.factory.CommonFactoryFinder;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.map.MapLayerBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.style.MutableStyle;
import org.geotools.style.RandomStyleFactory;
import org.geotools.style.StyleConstants;
import org.geotools.style.StyleFactory;
import org.geotools.style.function.InterpolationPoint;
import org.geotools.style.function.Method;
import org.geotools.style.function.Mode;
import org.geotools.style.function.ThreshholdsBelongTo;
import org.opengis.filter.expression.Expression;
import org.opengis.filter.expression.Function;
import org.opengis.filter.expression.Literal;
import org.opengis.style.ChannelSelection;
import org.opengis.style.ColorMap;
import org.opengis.style.ContrastEnhancement;
import org.opengis.style.Description;
import org.opengis.style.OverlapBehavior;
import org.opengis.style.RasterSymbolizer;
import org.opengis.style.ShadedRelief;
import org.opengis.style.Symbolizer;

/**
 *
 * @author sorel
 */
public class PostGRIDContextBuilder {

    public static final MapLayerBuilder LAYER_BUILDER = new MapLayerBuilder();
    public static final StyleFactory SF = CommonFactoryFinder.getStyleFactory(null);
    public static final RandomStyleFactory RANDOM_FACTORY = new RandomStyleFactory();

    public static MapContext buildPostGridContext() {

        MapContext context = null;
        MapLayer layer = null;

        try {
            context = new DefaultMapContext(DefaultGeographicCRS.WGS84);
            context.layers().add(createPostGridLayer());

//            context.setCoordinateReferenceSystem(layer.getFeatureSource().getSchema().getCoordinateReferenceSystem());
//            context.setTitle("DemoContext");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return context;
    }

    public static MapLayer createPostGridLayer() throws IOException, CatalogException, SQLException {
        Database database = null;
        LayerTable layers = null;
        Layer selectedLayer = null;

        database = new Database();
        layers = database.getTable(LayerTable.class);

        Set<Layer> entries = layers.getEntries();
        for (Layer lay : entries) {
            System.out.println(lay.getName());
        }
        selectedLayer = layers.getEntry("SPOT5_Guyane_Panchro");
//        selectedLayer = layers.getEntry("BlueMarble");
//        selectedLayer = layers.getEntry("AO_Coriolis_(Temp)");
//        selectedLayer = layers.getEntry("Mars3D_Gascogne_(UZ-VZ)");

        PostGridMapLayer layer = new PostGridMapLayer(database, selectedLayer);
        layer.setStyle(createInterpolationStyle());
//        layer.graphicBuilders().add(new GridMarkGraphicBuilder());

        return layer;
    }

    public static MapLayer createPostGridLayer2() throws IOException, CatalogException, SQLException {
        Database database = null;
        LayerTable layers = null;
        Layer selectedLayer = null;

        database = new Database();
        layers = database.getTable(LayerTable.class);

        Set<Layer> entries = layers.getEntries();
        for (Layer lay : entries) {
            System.out.println(lay.getName());
        }
        selectedLayer = layers.getEntry("SPOT5_Guyane_Panchro");
//        selectedLayer = layers.getEntry("BlueMarble");
//        selectedLayer = layers.getEntry("AO_Coriolis_(Temp)");
//        selectedLayer = layers.getEntry("Mars3D_Gascogne_(UZ-VZ)");

        PostGridMapLayer2 layer = new PostGridMapLayer2(database, selectedLayer);
        layer.setStyle(createInterpolationStyle());
//        layer.graphicBuilders().add(new GridMarkGraphicBuilder());

        return layer;
    }

    public static MutableStyle createInterpolationStyle() {

        final List<InterpolationPoint> values = new ArrayList<InterpolationPoint>();
        values.add(SF.createInterpolationPoint(SF.colorExpression(Color.WHITE), -10));
        values.add(SF.createInterpolationPoint(SF.colorExpression(Color.BLUE), 0));
        values.add(SF.createInterpolationPoint(SF.colorExpression(Color.YELLOW), 19));
        values.add(SF.createInterpolationPoint(SF.colorExpression(Color.GREEN), 20));
        values.add(SF.createInterpolationPoint(SF.colorExpression(Color.YELLOW), 21));
        values.add(SF.createInterpolationPoint(SF.colorExpression(Color.RED), 30));
        final Literal lookup = StyleConstants.DEFAULT_CATEGORIZE_LOOKUP;
        final Literal fallback = StyleConstants.DEFAULT_FALLBACK;
        final Function interpolateFunction = SF.createInterpolateFunction(
                lookup, values, Method.COLOR, Mode.LINEAR, fallback);

        final ChannelSelection selection = SF.createChannelSelection(
                SF.createSelectedChannelType("0", SF.literalExpression(1)));

        Expression opacity = SF.literalExpression(1f);
        OverlapBehavior overlap = OverlapBehavior.LATEST_ON_TOP;
        ColorMap colorMap = SF.createColorMap(interpolateFunction);
        ContrastEnhancement enchance = StyleConstants.DEFAULT_CONTRAST_ENHANCEMENT;
        ShadedRelief relief = StyleConstants.DEFAULT_SHADED_RELIEF;
        Symbolizer outline = null; //createRealWorldLineSymbolizer();
        Unit uom = NonSI.FOOT;
        String geom = StyleConstants.DEFAULT_GEOM;
        String name = "raster symbol name";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        RasterSymbolizer symbol = SF.createRasterSymbolizer(opacity, selection, overlap, colorMap, enchance, relief, outline, uom, geom, name, desc);

        return SF.createStyle(symbol);
    }
    
    public static MutableStyle createCategorizeStyle() {

        final Map<Expression,Expression> values = new HashMap<Expression,Expression>();
        values.put(StyleConstants.CATEGORIZE_LESS_INFINITY, SF.colorExpression(Color.WHITE));
        values.put(SF.literalExpression(0), SF.colorExpression(Color.BLUE));
        values.put(SF.literalExpression(19), SF.colorExpression(Color.YELLOW));
        values.put(SF.literalExpression(20), SF.colorExpression(Color.GREEN));
        values.put(SF.literalExpression(21), SF.colorExpression(Color.YELLOW));
        values.put(SF.literalExpression(30), SF.colorExpression(Color.RED));
        final Literal lookup = StyleConstants.DEFAULT_CATEGORIZE_LOOKUP;
        final Literal fallback = StyleConstants.DEFAULT_FALLBACK;
        final Function categorizeFunction = SF.createCategorizeFunction(
                lookup, values, ThreshholdsBelongTo.PRECEDING, fallback);

        final ChannelSelection selection = SF.createChannelSelection(
                SF.createSelectedChannelType("0", SF.literalExpression(1)));


        Expression opacity = SF.literalExpression(1f);
        OverlapBehavior overlap = OverlapBehavior.LATEST_ON_TOP;
        ColorMap colorMap = SF.createColorMap(categorizeFunction);
        ContrastEnhancement enchance = StyleConstants.DEFAULT_CONTRAST_ENHANCEMENT;
        ShadedRelief relief = StyleConstants.DEFAULT_SHADED_RELIEF;
        Symbolizer outline = null; //createRealWorldLineSymbolizer();
        Unit uom = NonSI.FOOT;
        String geom = StyleConstants.DEFAULT_GEOM;
        String name = "raster symbol name";
        Description desc = StyleConstants.DEFAULT_DESCRIPTION;

        RasterSymbolizer symbol = SF.createRasterSymbolizer(opacity, selection, overlap, colorMap, enchance, relief, outline, uom, geom, name, desc);

        return SF.createStyle(symbol);
    }
    
    
}
