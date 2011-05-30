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

package org.constellation.menu.system;

import java.io.File;
import javax.faces.context.FacesContext;
import org.constellation.admin.service.ConstellationServer;
import org.constellation.configuration.ConfigDirectory;
import org.constellation.menu.service.AbstractServiceBean;
import org.constellation.provider.LayerProviderProxy;
import org.constellation.provider.StyleProviderProxy;
import org.mapfaces.i18n.I18NBean;

/**
 * Bean for general constellation configuration.
 * 
 * @author Johann Sorel (Geomatys)
 */
public class CstlBean extends I18NBean{

    public String getConfigurationDirectory(){
        return ConfigDirectory.getConfigDirectory().getPath();
    }

    public void setConfigurationDirectory(final String path){
        
        // Set the new user directory
        if (path != null && !path.isEmpty()) {
            final File userDirectory = new File(path);
            if (!userDirectory.isDirectory()) {
                userDirectory.mkdir();
            }
            ConfigDirectory.setConfigDirectory(userDirectory);
        }

        //reload services
        final ConstellationServer admin = (ConstellationServer) FacesContext.getCurrentInstance()
                .getExternalContext().getSessionMap().get(AbstractServiceBean.SERVICE_ADMIN_KEY);
        admin.services.restartAll();

        //reload providers
        LayerProviderProxy.getInstance().reload();
        StyleProviderProxy.getInstance().reload();
    }

}
