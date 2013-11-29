/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2010-2012, Geomatys
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

package org.constellation.configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.geotoolkit.gui.swing.tree.Trees;

/**
 * Configuration for a WPS service.
 * 
 * @author Guilhem Legal (Geomatys)
 * @author Quentin Boileau (Geomatys)
 * @since 0.9
 */
@XmlRootElement(name="ProcessContext")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessContext {

    private Processes processes;

    private String security;

    private Languages supportedLanguages;

    /**
     * Path where output wps data will be saved.
     */
    private String webdavDirectory;

    /**
     * Identifier of FileCoverageStore provider used by WPS to publish
     * coverages in WMS.
     */
    private String fileCoverageProviderId;

    /**
     * Instance name of the WMS service linked to current WPS.
     */
    private String wmsInstanceName;
    
    private final Map<String, String> customParameters = new HashMap<>();

    public ProcessContext() {

    }

    public ProcessContext(Processes processes) {
        this.processes = processes;
    }

    public ProcessContext(Processes processes, String security) {
        this.processes = processes;
        this.security = security;
    }

    public Processes getProcesses() {
        return processes;
    }
    
    /**
     * @return the layers
     */
    public List<ProcessFactory> getProcessFactories() {
        if (processes == null) {
            processes = new Processes();
            return processes.getFactory();
        } else {
            return processes.getFactory();
        }
    }


    /**
     * @param processes the layers to set
     */
    public void setProcesses(List<ProcessFactory> processes) {
        this.processes = new Processes(processes);
    }

    /**
     * @return the security constraint, or {@code null} if none.
     */
    public String getSecurity() {
        return security;
    }

    /**
     * Sets the security value.
     *
     * @param security the security value.
     */
    public void setSecurity(String security) {
        this.security = security;
    }

    /**
     * @return the supportedLanguages
     */
    public Languages getSupportedLanguages() {
        return supportedLanguages;
    }

    /**
     * @param supportedLanguages the supportedLanguages to set
     */
    public void setSupportedLanguages(Languages supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }
    
    /**
     * @return the webdavDirectory
     */
    public String getWebdavDirectory() {
        return webdavDirectory;
    }

    /**
     * @param webdavDirectory the webdavDirectory to set
     */
    public void setWebdavDirectory(String webdavDirectory) {
        this.webdavDirectory = webdavDirectory;
    }

    /**
     * @return the wmsInstanceName
     */
    public String getWmsInstanceName() {
        return wmsInstanceName;
    }

    /**
     * @param wmsInstanceName the wmsInstanceName to set
     */
    public void setWmsInstanceName(String wmsInstanceName) {
        this.wmsInstanceName = wmsInstanceName;
    }

    /**
     * @return the fileCoverageProviderId
     */
    public String getFileCoverageProviderId() {
        return fileCoverageProviderId;
    }


    /**
     * @param fileCoverageProviderId the fileCoverageProviderId to set
     */
    public void setFileCoverageProviderId(String fileCoverageProviderId) {
        this.fileCoverageProviderId = fileCoverageProviderId;
    }
    
    /**
     * @return the customParameters
     */
    public Map<String, String> getCustomParameters() {
        return customParameters;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(Trees.toString("ProcessContext", getProcessFactories()));
        if (security != null && !security.isEmpty()) {
            sb.append("Security=").append(security);
        }
        if (supportedLanguages != null) {
            sb.append("Supported languages:\n").append(supportedLanguages);
        }
        if (webdavDirectory != null) {
            sb.append("WebDav directory :\n").append(webdavDirectory);
        }
        if (wmsInstanceName != null) {
            sb.append("WMS instance name :\n").append(wmsInstanceName);
        }
        if (fileCoverageProviderId != null) {
            sb.append("FileCoverageStore id :\n").append(fileCoverageProviderId);
        }
        if (customParameters != null && !customParameters.isEmpty()) {
            sb.append("Custom parameters:\n");
            for (Map.Entry<String, String> entry : customParameters.entrySet()) {
                sb.append("key:").append(entry.getKey()).append(" value:").append(entry.getValue()).append('\n');
            }
        }
        return sb.toString();
    }

}
