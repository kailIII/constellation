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
package org.constellation.security.wms;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.constellation.query.Query;
import org.constellation.wms.AbstractWMSCapabilities;
import org.constellation.ws.ExceptionCode;
import org.constellation.ws.WebServiceException;


/**
 * {@code REST} client for a {@code WMS} request.
 *
 * @version $Id$
 * @author Cédric Briançon (Geomatys)
 */
public class WmsRestClient {
    /**
     * The URL of the webservice to request.
     */
    private final String url;

    /**
     * The marshaller of the result given by the service.
     */
    private final Marshaller marshaller;

    /**
     * The unmarshaller of the result given by the service.
     */
    private final Unmarshaller unmarshaller;

    public WmsRestClient(final String url, final Marshaller marshaller, final Unmarshaller unmarshaller) {
        this.url = url;
        this.marshaller = marshaller;
        this.unmarshaller = unmarshaller;
    }

    public AbstractWMSCapabilities sendGetCapabilities(final String service, final String request,
                                                       final String version) throws WebServiceException
    {
        assert (service.equalsIgnoreCase("wms"));

        //Connect to URL and get result
        final URL connectionURL;
        final URLConnection connec;
        try {
            connectionURL = new URL(url +"?"+ Query.KEY_REQUEST +"="+ request +"&"+
                Query.KEY_SERVICE +"="+ service +"&"+ Query.KEY_VERSION +"="+ version);
            connec = connectionURL.openConnection();
        } catch (IOException ex) {
            throw new WebServiceException(ex, ExceptionCode.NO_APPLICABLE_CODE);
        }
        connec.setDoOutput(true);
        connec.setRequestProperty("Content-Type","text/xml");

        // Extract the GetCapabilities XML result.
        final InputStream in;
        try {
            in = connec.getInputStream();
        } catch (IOException ex) {
            throw new WebServiceException(ex, ExceptionCode.NO_APPLICABLE_CODE);
        }

        //Parse response
        final Object response;
        try {
            response = unmarshaller.unmarshal(in);
        } catch (JAXBException ex) {
            throw new WebServiceException(ex, ExceptionCode.NO_APPLICABLE_CODE);
        }
        if (!(response instanceof AbstractWMSCapabilities)) {
            throw new WebServiceException("The respone is not unmarshallable, since it is not an" +
                    " instance of AbstractWMSCapabilities", ExceptionCode.NO_APPLICABLE_CODE);
        }

        return (AbstractWMSCapabilities) response;
    }
}
