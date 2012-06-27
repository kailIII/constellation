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
package org.constellation.process.wms.stop;

import org.constellation.ServiceDef;
import org.constellation.process.AbstractCstlProcess;
import org.constellation.ws.WSEngine;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessException;
import org.opengis.parameter.ParameterValueGroup;
import static org.geotoolkit.parameter.Parameters.*;
import static org.constellation.process.wms.stop.StopWMSServiceDescriptor.*;

/**
 * Stop service specified instance. If instance already stopped or didn't exist, a ProcessException will be throws.
 * 
 * @author Quentin Boileau (Geomatys).
 */
public class StopWMSService extends AbstractCstlProcess {

    public StopWMSService(final ProcessDescriptor desc, final ParameterValueGroup parameter) {
        super(desc, parameter);
    }

    @Override
    protected void execute() throws ProcessException {
        final String identifier = value(IDENTIFIER, inputParameters);

        if (identifier == null || identifier.isEmpty()) {
            throw new ProcessException("Service instance identifier can't be null or empty.", this, null);
        }

        final String serviceName = ServiceDef.WMS_1_3_0.specification.name();
        if (WSEngine.serviceInstanceExist(serviceName, identifier)) {
            WSEngine.shutdownInstance(serviceName, identifier);
        } else {
            throw new ProcessException("Instance "+identifier+" doesn't exist.", this, null);
        }
    }

}