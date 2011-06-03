/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2011, Geomatys
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

package org.constellation.map.configuration;

import org.constellation.configuration.ProviderReport;
import org.opengis.feature.type.Name;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.stream.XMLStreamException;

import org.constellation.configuration.ProviderServiceReport;
import org.constellation.configuration.AbstractConfigurer;
import org.constellation.configuration.AcknowlegementType;
import org.constellation.configuration.ProvidersReport;
import org.constellation.provider.LayerProvider;
import org.constellation.provider.LayerProviderProxy;
import org.constellation.provider.LayerProviderService;
import org.constellation.provider.StyleProvider;
import org.constellation.provider.StyleProviderProxy;
import org.constellation.provider.configuration.ProviderParameters;
import org.constellation.ws.CstlServiceException;
import org.geotoolkit.feature.DefaultName;

import org.geotoolkit.xml.parameter.ParameterValueReader;

import org.opengis.parameter.ParameterValue;
import org.opengis.parameter.GeneralParameterValue;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import static org.constellation.ws.ExceptionCode.*;
import static org.constellation.map.configuration.QueryConstants.*;

/**
 *
 * @author Guilhem Legal (Geomatys)
 * @author Johann Sorel (Geomatys)
 */
public class DefaultMapConfigurer extends AbstractConfigurer {
    
    private final Map<String, LayerProviderService> services = new HashMap<String, LayerProviderService>();
    
    public DefaultMapConfigurer() {
        final Collection<LayerProviderService> availableServices = LayerProviderProxy.getInstance().getServices();
        for (LayerProviderService service: availableServices) {
            this.services.put(service.getName(), service);
        }
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public Object treatRequest(final String request, final MultivaluedMap<String, String> parameters, final Object objectRequest) throws CstlServiceException {
        
        //Provider services operations
        if (REQUEST_LIST_SERVICES.equalsIgnoreCase(request)) {
            return listProviderServices();
        } else if (REQUEST_GET_SERVICE_DESCRIPTOR.equalsIgnoreCase(request)) {
            return getServiceDescriptor(parameters);
        } else if (REQUEST_GET_SOURCE_DESCRIPTOR.equalsIgnoreCase(request)) {
            return getSourceDescriptor(parameters);
        }
        
        //Provider operations
        else if (REQUEST_LIST_LAYERS.equalsIgnoreCase(request)) {
            return listLayers(parameters);
        }else if (REQUEST_CREATE_PROVIDER.equalsIgnoreCase(request)) {
            return createProvider(parameters, objectRequest);
        } else if (REQUEST_UPDATE_PROVIDER.equalsIgnoreCase(request)) {
            return updateProvider(parameters, objectRequest);
        } else if (REQUEST_GET_PROVIDER_CONFIG.equalsIgnoreCase(request)) {
            return getProviderConfiguration(parameters);
        } else if (REQUEST_DELETE_PROVIDER.equalsIgnoreCase(request)) {
            return deleteProvider(parameters);
        } else if (REQUEST_RESTART_PROVIDER.equalsIgnoreCase(request)) {
            return restartProvider(parameters);
        }
                
        //Layer operations
        else if (REQUEST_CREATE_LAYER.equalsIgnoreCase(request)) {
            return createLayer(parameters, objectRequest);
        } else if (REQUEST_UPDATE_LAYER.equalsIgnoreCase(request)) {
            return updateLayer(parameters, objectRequest);        
        } else if (REQUEST_DELETE_LAYER.equalsIgnoreCase(request)) {
            return deleteLayer(parameters);
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc }
     */
    @Override
    public void beforeRestart() {
        StyleProviderProxy.getInstance().dispose();
        LayerProviderProxy.getInstance().dispose();
    }
    
    /**
     * Return a list of layers for the given provider
     * 
     * @param parameters The GET KVP parameters send in the request.
     * @throws CstlServiceException 
     */
    private Object listLayers(final MultivaluedMap<String, String> parameters) throws CstlServiceException{
        final String sourceId = getParameter("id", true, parameters);
        Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
        for (LayerProvider p : providers) {
            if (p.getId().equals(sourceId)) {
                final List<String> keys = new ArrayList<String>();
                for(Name n : p.getKeys()){
                    keys.add(DefaultName.toJCRExtendedForm(n));
                }
                return new ProviderReport(keys);
            }
        }
        throw new CstlServiceException("No provider id : " + sourceId + " has been found", INVALID_PARAMETER_VALUE);
    }
    
    /**
     * Add a new source to the specified provider.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * @param objectRequest The POST parameters send in the request.
     * 
     * @return An acknowledgement informing if the request have been succesfully treated or not.
     * @throws CstlServiceException 
     */
    private AcknowlegementType createProvider(final MultivaluedMap<String, String> parameters,
            final Object objectRequest) throws CstlServiceException{
        final String serviceName = getParameter("serviceName", true, parameters);
        final LayerProviderService service = this.services.get(serviceName);
        if (service != null) {

            final ParameterValueReader reader = new ParameterValueReader(
                    service.getServiceDescriptor().descriptor(ProviderParameters.SOURCE_DESCRIPTOR_NAME));
            try {
                // we read the soruce parameter to add
                reader.setInput(objectRequest);
                final ParameterValueGroup sourceToAdd = (ParameterValueGroup) reader.read();
                reader.dispose();
                LayerProviderProxy.getInstance().createProvider(service, sourceToAdd);

                return new AcknowlegementType("Success", "The source has been added");
            } catch (XMLStreamException ex) {
                throw new CstlServiceException(ex);
            } catch (IOException ex) {
                throw new CstlServiceException(ex);
            }
        } else {
            throw new CstlServiceException("No provider service for: " + serviceName + " has been found", INVALID_PARAMETER_VALUE);
        }
    }
    
    /**
     * Modify a source in the specified provider.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * @param objectRequest The POST parameters send in the request.
     * 
     * @return An akcnowledgement informing if the request have been succesfully treated or not.
     * @throws CstlServiceException 
     */
    private AcknowlegementType updateProvider(final MultivaluedMap<String, String> parameters,
            final Object objectRequest) throws CstlServiceException{
        final String serviceName = getParameter("serviceName", true, parameters);
        final String currentId = getParameter("id", true, parameters);
        final LayerProviderService service = services.get(serviceName);
        if (service != null) {

            final ParameterValueReader reader = new ParameterValueReader(service.getServiceDescriptor());
            try {
                // we read the soruce parameter to add
                reader.setInput(objectRequest);
                final ParameterValueGroup sourceToModify = (ParameterValueGroup) reader.read();
                reader.dispose();

                final Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
                for (LayerProvider p : providers) {
                    if (p.getId().equals(currentId)) {
                        p.updateSource(sourceToModify);
                        return new AcknowlegementType("Success", "The source has been modified");
                    }
                }
                return new AcknowlegementType("Failure", "Unable to find a source named:" + currentId);   

            } catch (XMLStreamException ex) {
                throw new CstlServiceException(ex);
            } catch (IOException ex) {
                throw new CstlServiceException(ex);
            }
        } else {
            throw new CstlServiceException("No descriptor for: " + serviceName + " has been found", INVALID_PARAMETER_VALUE);
        }
    }
    
    /**
     * Return the configuration object  of the specified source.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * 
     * @return The configuration object  of the specified source.
     * @throws CstlServiceException 
     */
    private Object getProviderConfiguration(final MultivaluedMap<String, String> parameters) throws CstlServiceException{
        final String id = getParameter("id", true, parameters);
            
        Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
        for (LayerProvider p : providers) {
            if (p.getId().equals(id)) {
                return p.getSource();
            }
        }

        return new AcknowlegementType("Failure", "Unable to find a source named:" + id);
    }
    
    /**
     * Remove a source in the specified provider.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * 
     * @return An akcnowledgement informing if the request have been succesfully treated or not.
     * @throws CstlServiceException 
     */
    private AcknowlegementType deleteProvider(final MultivaluedMap<String, String> parameters) throws CstlServiceException{
        final String sourceId = getParameter("id", true, parameters);
        Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
        for (LayerProvider p : providers) {
            if (p.getId().equals(sourceId)) {
                LayerProviderProxy.getInstance().removeProvider(p);
                return new AcknowlegementType("Success", "The source has been deleted");
            }
        }
        return new AcknowlegementType("Failure", "Unable to find a source named:" + sourceId);
    }
    
    /**
     * Restart a provider in the specified service.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * 
     * @return An akcnowledgement informing if the request have been succesfully treated or not.
     * @throws CstlServiceException 
     */
    private AcknowlegementType restartProvider(final MultivaluedMap<String, String> parameters) throws CstlServiceException{
        final String sourceId = getParameter("id", true, parameters);
        for (LayerProvider p : LayerProviderProxy.getInstance().getProviders()) {
            if (p.getId().equals(sourceId)) {
                p.reload();
                return new AcknowlegementType("Success", "The provider has been restarted");
            }
        }
        for (StyleProvider p : StyleProviderProxy.getInstance().getProviders()) {
            if (p.getId().equals(sourceId)) {
                p.reload();
                return new AcknowlegementType("Success", "The provider has been restarted");
            }
        }
        return new AcknowlegementType("Failure", "Unable to find a provider named:" + sourceId);
    }
    
    
    /**
     * Add a layer to the specified provider.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * @param objectRequest The POST parameters send in the request.
     * 
     * @return An akcnowledgement informing if the request have been succesfully treated or not.
     * @throws CstlServiceException 
     */
    private AcknowlegementType createLayer(final MultivaluedMap<String, String> parameters, 
            final Object objectRequest) throws CstlServiceException{
        
        final String sourceId = getParameter("id", true, parameters);            
        final ParameterValueReader reader = new ParameterValueReader(ProviderParameters.LAYER_DESCRIPTOR);
        try {
            // we read the soruce parameter to add
            reader.setInput(objectRequest);
            final ParameterValueGroup newLayer = (ParameterValueGroup) reader.read();
            reader.dispose();

            final Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
            for (LayerProvider p : providers) {
                if (p.getId().equals(sourceId)) {
                    p.getSource().values().add(newLayer);
                    p.updateSource(p.getSource());
                    return new AcknowlegementType("Success", "The layer has been added");
                }
            }
            return new AcknowlegementType("Failure", "Unable to find a source named:" + sourceId);


        } catch (XMLStreamException ex) {
            throw new CstlServiceException(ex);
        } catch (IOException ex) {
            throw new CstlServiceException(ex);
        }
    }
    
    /**
     * Remove a layer in the specified provider.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * 
     * @return An akcnowledgement informing if the request have been succesfully treated or not.
     * @throws CstlServiceException 
     */
    private AcknowlegementType deleteLayer(final MultivaluedMap<String, String> parameters) throws CstlServiceException{
        final String sourceId = getParameter("id", true, parameters);
        final String layerName = getParameter("layerName", true, parameters);

        final Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
        for (LayerProvider p : providers) {
            if (p.getId().equals(sourceId)) {
                for (GeneralParameterValue param : p.getSource().values()) {
                    if (param instanceof ParameterValueGroup) {
                        final ParameterValueGroup pvg = (ParameterValueGroup)param;
                        if (param.getDescriptor().equals(ProviderParameters.LAYER_DESCRIPTOR)) {
                            final ParameterValue value = pvg.parameter("name");
                            if (value.stringValue().equals(layerName)) {
                                p.getSource().values().remove(pvg);
                                break;
                            }
                        }
                    }
                }
                p.updateSource(p.getSource());
                return new AcknowlegementType("Success", "The layer has been removed");
            }
        }
        return new AcknowlegementType("Failure", "Unable to find a source named:" + sourceId);
    }
    
    /**
     * Modify a layer to the specified provider.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * @param objectRequest The POST parameters send in the request.
     * 
     * @return An akcnowledgement informing if the request have been succesfully treated or not.
     * @throws CstlServiceException 
     */
    private AcknowlegementType updateLayer(final MultivaluedMap<String, String> parameters, 
            final Object objectRequest) throws CstlServiceException{
        
        final String sourceId = getParameter("id", true, parameters);
        final String layerName = getParameter("layerName", true, parameters);

        final ParameterValueReader reader = new ParameterValueReader(ProviderParameters.LAYER_DESCRIPTOR);
        try {
            // we read the source parameter to add
            reader.setInput(objectRequest);
            ParameterValueGroup newLayer = (ParameterValueGroup) reader.read();
            reader.dispose();

            Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
            for (LayerProvider p : providers) {
                if (p.getId().equals(sourceId)) {
                    for (GeneralParameterValue param : p.getSource().values()) {
                        if (param instanceof ParameterValueGroup) {
                            ParameterValueGroup pvg = (ParameterValueGroup)param;
                            if (param.getDescriptor().equals(ProviderParameters.LAYER_DESCRIPTOR)) {
                                ParameterValue value = pvg.parameter("name");
                                if (value.stringValue().equals(layerName)) {
                                    p.getSource().values().remove(pvg);
                                    p.getSource().values().add(newLayer);
                                    break;
                                }
                            }
                        }
                    }
                    p.updateSource(p.getSource());
                    return new AcknowlegementType("Success", "The layer has been modified");
                }
            }
        } catch (XMLStreamException ex) {
            throw new CstlServiceException(ex);
        } catch (IOException ex) {
            throw new CstlServiceException(ex);
        }
        return new AcknowlegementType("Failure", "Unable to find a source named:" + sourceId);
    }
    
    /**
     * Return the service descriptor of the specified type.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * 
     * @return The descriptor of the specified provider type.
     * @throws CstlServiceException 
     */
    private ParameterDescriptorGroup getServiceDescriptor(final MultivaluedMap<String, String> parameters) throws CstlServiceException{
        final String serviceName = getParameter("serviceName", true, parameters);
        final LayerProviderService service = services.get(serviceName);
        if (service != null) {
            return service.getServiceDescriptor();
        }
        throw new CstlServiceException("No provider service for: " + serviceName + " has been found", INVALID_PARAMETER_VALUE);
    }
    
    /**
     * Return the service source descriptor of the specified type.
     * 
     * @param parameters The GET KVP parameters send in the request.
     * 
     * @return The descriptor of the specified provider type.
     * @throws CstlServiceException 
     */
    private GeneralParameterDescriptor getSourceDescriptor(final MultivaluedMap<String, String> parameters) throws CstlServiceException{
        final String serviceName = getParameter("serviceName", true, parameters);
        final LayerProviderService service = services.get(serviceName);
        if (service != null) {
            return service.getSourceDescriptor();
        }
        throw new CstlServiceException("No provider service for: " + serviceName + " has been found", INVALID_PARAMETER_VALUE);
    }
    
    /**
     * Return a description of the available providers.
     * 
     * @return A description of the available providers.
     */
    private ProvidersReport listProviderServices(){
        final List<ProviderServiceReport> providerServ = new ArrayList<ProviderServiceReport>();
        
        final Collection<LayerProvider> providers = LayerProviderProxy.getInstance().getProviders();
        for (LayerProviderService service : services.values()) {
            
            final List<String> layer = new ArrayList<String>();
            for (LayerProvider p : providers) {
                if (p.getService().equals(service)) {
                    layer.add(p.getId());
                }
            }
            providerServ.add(new ProviderServiceReport(service.getName(), layer));
        }
        
        return new ProvidersReport(providerServ);
    }
    
}
