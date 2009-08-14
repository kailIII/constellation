/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 3 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.constellation.portrayal.internal;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.util.logging.Level;

import org.constellation.portrayal.Portrayal;
import org.constellation.portrayal.PortrayalServiceIF;
import org.constellation.provider.LayerDetails;

import org.geotoolkit.display.canvas.GraphicVisitor;
import org.geotoolkit.display.canvas.control.FailOnErrorMonitor;
import org.geotoolkit.display.exception.PortrayalException;
import org.geotoolkit.display2d.service.DefaultPortrayalService;
import org.geotoolkit.display2d.service.PortrayalExtension;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.style.MutableStyle;


/**
 * Service class to portray or work with two dimensional scenes defined by a 
 * scene definition, a view definition, and a canvas definition.
 * <p>
 * <b>Users should *not* call this class directly.</b><br/>
 * Instead, the {@link Cstl.Portrayal} reference should be used.
 * </p>
 *
 * @author Johann Sorel (Geomatys)
 * @author Cédric Briançon (Geomatys)
 * @see Cst.Portrayal
 * @see Portrayal
 */
public final class CstlPortrayalService implements PortrayalServiceIF {

    /**
     * This generates a new instance for each thread (i.e. each service request) 
     * but still allows us to have a statically accessible reference with the 
     * {@link #getInstance()} method.
     */
    private static final ThreadLocal<CstlPortrayalService> INSTANCES = new ThreadLocal<CstlPortrayalService>() {
        @Override
        protected CstlPortrayalService initialValue() {
            return new CstlPortrayalService();
        }
    };
    
    /**
     * An internal method which provides the instance for the thread in which 
     * the caller is running.
     * <p>
     * In this design, each CstlPortrayalService instance runs in its own thread, 
     * </p>
     * @return
     */
    public static CstlPortrayalService internalGetInstance(){
        return INSTANCES.get();
    }
        
    private CstlPortrayalService(){}
    
    /**
     * Portray a set of Layers over a given geographic extent with a given 
     * resolution yielding a {@code BufferedImage} of the scene.
     * @param sdef A structure which defines the scene.
     * @param vdef A structure which defines the view.
     * @param cdef A structure which defines the canvas.
     * 
     * @return A rendered image of the scene, in the chosen view and for the 
     *           given canvas.
     * @throws PortrayalException For errors during portrayal, TODO: common examples?
     */
    @Override
    public BufferedImage portray( final Portrayal.SceneDef sdef,
                                  final Portrayal.ViewDef vdef,
                                  final Portrayal.CanvasDef cdef) 
    		throws PortrayalException {
    	
    	final MapContext context = createContext(sdef);
        final ReportMonitor monitor = new ReportMonitor();

        try {
            final BufferedImage buffer = DefaultPortrayalService.portray(
                    context,
                    vdef.envelope,
                    null,null,
                    cdef.dimension,
                    true,
                    (float)vdef.azimuth,
                    monitor,
                    cdef.background,
                    null,
                    sdef.extensions.toArray(new PortrayalExtension[sdef.extensions.size()])
                    );

            final Exception exp = monitor.getLastException();
            if(exp != null){
                throw exp;
            }

            return buffer;

        } catch(Exception ex) {
            if (ex instanceof PortrayalException) {
                throw (PortrayalException)ex;
            } else {
                throw new PortrayalException(ex);
            }
        } finally {
            context.layers().clear();
        }

    }
    
    /**
     * Apply the Visitor to all the 
     * {@link org..opengis.display.primitive.Graphic} objects which lie within 
     * the {@link java.awt.Shape} in the given scene.
     * <p>
     * The visitor could be an extension of the AbstractGraphicVisitor class in
     * this same package.
     * </p>
     * 
     * TODO: why are the last two arguments not final?
     * 
     * @see AbstractGraphicVisitor
     */
    @Override
    public void visit( final Portrayal.SceneDef sdef,
                       final Portrayal.ViewDef vdef,
                       final Portrayal.CanvasDef cdef,
                       Shape selectedArea, 
                       GraphicVisitor visitor)
            throws PortrayalException {

        final MapContext context = createContext(sdef);

        try{
            DefaultPortrayalService.visit(
                    context,
                    vdef.envelope,
                    cdef.dimension,
                    true,
                    null,
                    selectedArea,
                    visitor);
        }catch(Exception ex){
            if (ex instanceof PortrayalException) {
                throw (PortrayalException)ex;
            } else {
                throw new PortrayalException(ex);
            }
        }finally{
            visitor.endVisit();
            context.layers().clear();
        }

    }
    
    /*
     * TODO: document how the size of the text is chosen.
     */
    @Override
    public BufferedImage writeInImage(Exception e, Dimension dim){
        return DefaultPortrayalService.writeException(e, dim);
    }
    
    private MapContext createContext( final Portrayal.SceneDef sdef ) throws PortrayalException {

    	assert ( sdef.layerRefs.size() == sdef.styleRefs.size() );
        final MapContext context = MapBuilder.createContext(DefaultGeographicCRS.WGS84);
        context.layers().clear();

        for (int i = 0; i < sdef.layerRefs.size(); i++){

        	final LayerDetails layerRef = sdef.layerRefs.get(i);
        	final MutableStyle style = sdef.styleRefs.get(i);

        	assert ( null != layerRef );
        	//style can be null

            final MapLayer mapLayer = layerRef.getMapLayer(style, sdef.renderingParameters);
            if (mapLayer == null) {
                throw new PortrayalException("Could not create a mapLayer for layer: " + layerRef.getName());
            }
            mapLayer.setSelectable(true);
            mapLayer.setVisible(true);
            context.layers().add(mapLayer);
        }

        return context;
    }
        
    /*
     * TODO: document me, pretty please.
     */
    private class ReportMonitor extends FailOnErrorMonitor{

        private Exception lastError = null;
        
        public Exception getLastException(){
            return lastError;
        }
        
        @Override
        public void renderingStarted() {
            lastError = null;
            super.renderingStarted();
        }

        @Override
        public void exceptionOccured(Exception ex, Level level) {
            lastError = ex;
            //request stop rendering
            stopRendering();
        }
        
    }
    
}
