/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
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
package org.constellation.provider.om2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.constellation.provider.AbstractProviderService;
import org.constellation.provider.LayerDetails;
import org.constellation.provider.LayerProvider;
import org.constellation.provider.LayerProviderService;
import static org.constellation.provider.configuration.ProviderParameters.createDescriptor;
import org.geotoolkit.data.FeatureStoreFactory;
import org.geotoolkit.data.FeatureStoreFinder;
import org.geotoolkit.parameter.DefaultParameterDescriptorGroup;
import org.opengis.feature.type.Name;
import org.opengis.parameter.GeneralParameterDescriptor;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 *
 * @version $Id$
 *
 * @author Johann Sorel (Geomatys)
 */
public class OM2ProviderService extends AbstractProviderService
        <Name,LayerDetails,LayerProvider> implements LayerProviderService {

    private static final String ERROR_MSG = "[PROVIDER]> Invalid OM2 store provider config";
    public static final ParameterDescriptorGroup SOURCE_CONFIG_DESCRIPTOR;

    static {
        final List<ParameterDescriptorGroup> descs = new ArrayList<>();
        final Iterator<FeatureStoreFactory> ite = FeatureStoreFinder.getAllFactories(null).iterator();
        while(ite.hasNext()){
            //copy the descriptor with a minimum number of zero
            final ParameterDescriptorGroup desc = ite.next().getParametersDescriptor();

            final DefaultParameterDescriptorGroup mindesc = new DefaultParameterDescriptorGroup(
                    Collections.singletonMap("name", desc.getName()),
                    0, 1,
                    desc.descriptors().toArray(new GeneralParameterDescriptor[0]));

            descs.add(mindesc);
        }

        SOURCE_CONFIG_DESCRIPTOR = new DefaultParameterDescriptorGroup(
            "choice", descs.toArray(new GeneralParameterDescriptor[0]));

    }

    public static final ParameterDescriptorGroup SERVICE_CONFIG_DESCRIPTOR =
            createDescriptor(SOURCE_CONFIG_DESCRIPTOR);

    public OM2ProviderService(){
        super("om2-store");
    }

    @Override
    public ParameterDescriptorGroup getServiceDescriptor() {
        return SERVICE_CONFIG_DESCRIPTOR;
    }

    @Override
    public ParameterDescriptorGroup getSourceDescriptor() {
        return SOURCE_CONFIG_DESCRIPTOR;
    }

    @Override
    public LayerProvider createProvider(ParameterValueGroup ps) {
        if(!canProcess(ps)){
            return null;
        }

        try {
            final OM2StoreProvider provider = new OM2StoreProvider(this,ps);
            getLogger().log(Level.INFO, "[PROVIDER]> om2-store provider created.");
            return provider;
        } catch (Exception ex) {
            // we should not catch exception, but here it's better to start all source we can
            // rather than letting a potential exception block the provider proxy
            getLogger().log(Level.SEVERE, ERROR_MSG, ex);
        }
        return null;
    }

}