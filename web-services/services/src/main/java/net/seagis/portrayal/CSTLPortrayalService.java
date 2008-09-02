/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.seagis.portrayal;

import java.awt.Dimension;
import java.io.IOException;
import java.util.List;

import net.seagis.provider.NamedLayerDP;
import net.seagis.provider.NamedStyleDP;

import org.geotools.display.service.DefaultPortrayalService;
import org.geotools.factory.Hints;
import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.map.DefaultMapContext;
import org.geotools.map.MapContext;
import org.geotools.map.MapLayer;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.sld.MutableLayer;
import org.geotools.sld.MutableLayerStyle;
import org.geotools.sld.MutableNamedLayer;
import org.geotools.sld.MutableNamedStyle;
import org.geotools.sld.MutableStyledLayerDescriptor;
import org.geotools.style.MutableStyle;

import org.opengis.referencing.operation.TransformException;

/**
 * Portrayal service, extends the GT portrayal service by adding support
 * to reconize Named layers and styles from constellation data providers.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class CSTLPortrayalService extends DefaultPortrayalService{

    private final NamedLayerDP layerDPS = NamedLayerDP.getInstance();
    private final NamedStyleDP styleDPS = NamedStyleDP.getInstance();


    public CSTLPortrayalService(){

    }

    public boolean portray(List<String> layers, List<String> styles,
            MutableStyledLayerDescriptor sld,ReferencedEnvelope contextEnv, Object output,
            String mime, Dimension canvasDimension,Hints hints)
            throws IOException,TransformException{

        MapContext context = toMapContext(layers,styles,sld);


        StringBuilder builder = new StringBuilder();
        builder.append("Layers => " + layers.toArray() + "\n");
        builder.append("Styles => " + styles.toArray() + "\n");
        builder.append("Context env => " + contextEnv.toString() + "\n");
        builder.append("Mime => " + mime.toString() + "\n");
        builder.append("Dimension => " + canvasDimension.toString() + "\n");
        builder.append("File => " + output.toString() + "\n");

        System.out.println(builder.toString());

        return this.portray(context, contextEnv, output, mime, canvasDimension, hints);
    }

    private MapContext toMapContext(List<String> layers, List<String> styles, MutableStyledLayerDescriptor sld) {
        MapContext ctx = new DefaultMapContext(DefaultGeographicCRS.WGS84);

        int index = 0;
        for(String layerName : layers){
            MapLayer layer = layerDPS.get(layerName);

            if(layer != null){

                if(sld != null){
                    //try to use the provided SLD
                    MutableStyle style = extractStyle(layerName,sld);
                    if(style != null){
                        layer.setStyle(style);
                    }
                } else if (styles.size() > index){
                    //try to grab the style if provided
                    //a style has been given for this layer, try to use it
                    MutableStyle style = styleDPS.get(styles.get(index));
                    if(style != null){
                        layer.setStyle(style);
                    }
                }
            }
            ctx.layers().add(layer);

            index++;
        }

        return ctx;
    }

    private MutableStyle extractStyle(String layerName,MutableStyledLayerDescriptor sld){
        if(sld == null){
            throw new NullPointerException("SLD should not be null");
        }

        List<MutableLayer> layers = sld.layers();
        for(MutableLayer layer : layers){

            if(layer instanceof MutableNamedLayer && layerName.equals(layer.getName()) ){
                //we can only extract style from a NamedLayer that has the same name
                final MutableNamedLayer mnl = (MutableNamedLayer) layer;
                final List<MutableLayerStyle> styles = mnl.styles();
                
                for(MutableLayerStyle mls : styles){
                    MutableStyle GTStyle = null;
                    if(mls instanceof MutableNamedStyle){
                        MutableNamedStyle mns = (MutableNamedStyle) mls;
                        GTStyle = styleDPS.get(mns.getName());
                    }else if(mls instanceof MutableStyle){
                        GTStyle = (MutableStyle) mls;
                    }
                    
                    if(GTStyle != null){
                        //we have found a valid style
                        return GTStyle;
                    }
                }
                
            }
        }

        //no valid style found
        return null;
    }


}
