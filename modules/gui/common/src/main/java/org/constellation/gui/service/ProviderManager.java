/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2012, Geomatys
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
package org.constellation.gui.service;

import org.constellation.admin.service.ConstellationServer;
import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Juzu service to call constellation services server side
 *
 * @author Benjamin Garcia (Geomatys)
 * @version 0.9
 * @since 0.9
 */
public class ProviderManager {

    private static final Logger LOGGER = Logger.getLogger(ProviderManager.class.getName());

    /**
     * constellation server URL
     */
    private String constellationUrl;

    /**
     * constellation server user login
     */
    private String login;

    /**
     * constellation server user password
     */
    private String password;


    public ProviderManager() {
    }

    public void setConstellationUrl(String constellationUrl) {
        this.constellationUrl = constellationUrl;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void createProvider(final String type, final String fileIdentifier, final String path) {
        try {
            final URL serverUrl = new URL(constellationUrl);
            final ConstellationServer cs = new ConstellationServer(serverUrl, login, password);
            final ParameterDescriptorGroup serviceDesc = (ParameterDescriptorGroup) cs.providers.getServiceDescriptor(type);
            final ParameterDescriptorGroup sourceDesc = (ParameterDescriptorGroup) serviceDesc.descriptor("source");
            final ParameterValueGroup sources = sourceDesc.createValue();
            sources.parameter("id").setValue(fileIdentifier);

            final String folderPath = path.substring(0, path.lastIndexOf('/'));

            switch (type) {
                case "coverage-file":
                    sources.groups("coveragefile").get(0).parameter("path").setValue(folderPath);
                    break;
                case "sld":
                    sources.groups("sldFolder").get(0).parameter("path").setValue(folderPath);
                    break;
                default:
                    if (LOGGER.isLoggable(Level.FINER)) {
                        LOGGER.log(Level.FINER, "Provider type not known");
                    }
            }

            cs.providers.createProvider(type, sources);
        } catch (MalformedURLException e) {
            LOGGER.log(Level.WARNING, "", e);
        }
    }
}
