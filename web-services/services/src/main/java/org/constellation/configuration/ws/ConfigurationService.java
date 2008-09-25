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
package org.constellation.configuration.ws;

import com.sun.jersey.spi.resource.Singleton;
import java.io.StringWriter;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBException;
import org.constellation.coverage.web.Service;
import org.constellation.coverage.web.ServiceVersion;
import org.constellation.coverage.web.WMSWebServiceException;
import org.constellation.coverage.web.WebServiceException;
import org.constellation.ows.OWSExceptionCode;
import org.constellation.ows.v110.OWSWebServiceException;
import org.constellation.ws.rs.WebService;

/**
 * A Web service dedicate to perform administration and configuration operations
 * 
 * @author Guilhem Legal
 */
@Path("configuration")
@Singleton
public class ConfigurationService extends WebService {

    public ConfigurationService() {
        super("Configuration", new ServiceVersion(Service.OTHER, "1.0.0"));
    }
    
    @Override
    public Response treatIncomingRequest(Object objectRequest) throws JAXBException {
        try {
            if (objectRequest == null) {
                throw new OWSWebServiceException("This service only accept POST XML request.", OWSExceptionCode.INVALID_REQUEST, null, getCurrentVersion());
            } else {
                return Response.ok("<wait>wait a little we are working on it</wait>", "text/xml").build();
            }
        
        
        } catch (WebServiceException ex) {

            if (ex instanceof WMSWebServiceException) {
                ex = new OWSWebServiceException(ex.getMessage(),
                        OWSExceptionCode.valueOf(ex.getExceptionCode().name()),
                        null,
                        getVersionFromNumber(ex.getVersion()));
            }
            /* We don't print the stack trace:
             * - if the user have forget a mandatory parameter.
             * - if the version number is wrong.
             */
           
            OWSWebServiceException owsex = (OWSWebServiceException) ex;
            if (!owsex.getExceptionCode().equals(OWSExceptionCode.MISSING_PARAMETER_VALUE) &&
                    !owsex.getExceptionCode().equals(OWSExceptionCode.VERSION_NEGOTIATION_FAILED) &&
                    !owsex.getExceptionCode().equals(OWSExceptionCode.OPERATION_NOT_SUPPORTED)) {
                owsex.printStackTrace();
            } else {
                LOGGER.info(owsex.getMessage());
            }
            StringWriter sw = new StringWriter();
            marshaller.marshal(owsex.getExceptionReport(), sw);
            return Response.ok(cleanSpecialCharacter(sw.toString()), "text/xml").build();
        }
        
    }

}
