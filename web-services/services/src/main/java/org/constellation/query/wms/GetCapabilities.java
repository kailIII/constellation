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
package org.constellation.query.wms;

import org.constellation.query.QueryRequest;
import org.constellation.query.QueryVersion;


/**
 *
 * @version $Id$
 * @author Cédric Briançon (Geomatys)
 */
public class GetCapabilities extends WMSQuery {
    /**
     * Version of the GetCapabilities requested.
     */
    private final QueryVersion version;

    /**
     * 
     */
    public GetCapabilities(final QueryVersion version) {
        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    public String getExceptionFormat() {
        return "application/vnd.ogc.se_xml";
    }

    /**
     * {@inheritDoc}
     */
    public QueryRequest getRequest() {
        return WMSQueryRequest.GET_CAPABILITIES;
    }

    /**
     * {@inheritDoc}
     */
    public QueryVersion getVersion() {
        return version;
    }
}
