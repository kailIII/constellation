/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.constellation.ogc.configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import org.constellation.ServiceDef.Specification;
import org.constellation.admin.ConfigurationEngine;
import org.constellation.admin.ServiceBusiness;
import org.constellation.configuration.ConfigurationException;
import org.constellation.configuration.Instance;
import org.constellation.configuration.ServiceStatus;
import org.constellation.configuration.TargetNotFoundException;
import org.constellation.dto.Service;
import org.constellation.process.ConstellationProcessFactory;
import org.constellation.ws.ServiceConfigurer;
import org.constellation.ws.WSEngine;
import org.geotoolkit.process.ProcessDescriptor;
import org.geotoolkit.process.ProcessFinder;
import org.opengis.util.NoSuchIdentifierException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Describe methods which need to be specify by an implementation to manage
 * service (create, set configuration, etc...).
 *
 * @author Benjamin Garcia (Geomatys).
 * @author Fabien Bernard (Geomatys).
 * @version 0.9
 * @since 0.9
 */
public abstract class OGCConfigurer extends ServiceConfigurer {

    @Autowired
    private ServiceBusiness serviceBusiness;
    
    /**
     * Create a new {@link OGCConfigurer} instance.
     *
     * @param configClass    the target service config class
     */
    protected OGCConfigurer(final Class configClass) {
        super(configClass);
    }

    /**
     * Creates a new service instance.
     *
     * @param serviceType   The type of the service.
     * @param identifier    The identifier of the service.
     * @param metadata      the service metadata (can be null)
     * @param configuration the service configuration (can be null)
     * @throws org.constellation.configuration.ConfigurationException if the operation has failed for any reason
     */
    public void createInstance(final String serviceType, final String identifier, final Service metadata, final Object configuration) throws ConfigurationException {
        serviceBusiness.createInstance(serviceType, identifier, configuration, metadata, configClass);
    }

    /**
     * Starts a service instance.
     *
     * @param serviceType The type of the service.
     * @param identifier The service identifier
     * @throws TargetNotFoundException if the service with specified identifier does not exist
     * @throws org.constellation.configuration.ConfigurationException if the operation has failed for any reason
     */
    public void startInstance(final String serviceType, final String identifier) throws ConfigurationException {
        this.ensureExistingInstance(serviceType, identifier);
        serviceBusiness.startInstance(serviceType, identifier);
    }

    /**
     * Stops a service instance.
     *
     * @param serviceType The type of the service.
     * @param identifier the service identifier
     * @throws TargetNotFoundException if the service with specified identifier does not exist
     * @throws org.constellation.configuration.ConfigurationException if the operation has failed for any reason
     */
    public void stopInstance(final String serviceType, final String identifier) throws ConfigurationException {
        this.ensureExistingInstance(serviceType, identifier);
        serviceBusiness.stopInstance(serviceType, identifier);
    }

    /**
     * Restarts a service instance.
     *
     * @param serviceType The type of the service.
     * @param identifier the service identifier
     * @param closeFirst indicates if the service should be closed before trying to restart it
     * @throws TargetNotFoundException if the service with specified identifier does not exist
     * @throws org.constellation.configuration.ConfigurationException if the operation has failed for any reason
     */
    public void restartInstance(final String serviceType, final String identifier, final boolean closeFirst) throws ConfigurationException {
        this.ensureExistingInstance(serviceType, identifier);
        serviceBusiness.restartInstance(serviceType, identifier, closeFirst);
    }

    /**
     * Renames a service instance.
     *
     * @param serviceType The type of the service.
     * @param identifier    the current service identifier
     * @param newIdentifier the new service identifier
     * @throws TargetNotFoundException if the service with specified identifier does not exist
     * @throws org.constellation.configuration.ConfigurationException if the operation has failed for any reason
     */
    public void renameInstance(final String serviceType, final String identifier, final String newIdentifier) throws ConfigurationException {
        this.ensureExistingInstance(serviceType, identifier);
        serviceBusiness.renameInstance(serviceType, identifier, newIdentifier);
    }

    /**
     * Deletes a service instance.
     *
     * @param serviceType The type of the service.
     * @param identifier the service identifier
     * @throws TargetNotFoundException if the service with specified identifier does not exist
     * @throws org.constellation.configuration.ConfigurationException if the operation has failed for any reason
     */
    public void deleteInstance(final String serviceType, final String identifier) throws ConfigurationException {
        this.ensureExistingInstance(serviceType, identifier);
        serviceBusiness.deleteInstance(serviceType, identifier);
    }

    /**
     * Configures a service instance.
     *
     * @param serviceType The type of the service.
     * @param identifier    the service identifier
     * @param configuration the service configuration (depending on implementation)
     * @param metadata      the service metadata
     * @throws TargetNotFoundException if the service with specified identifier does not exist
     * @throws org.constellation.configuration.ConfigurationException if the operation has failed for any reason
     */
    public void configureInstance(final String serviceType, final String identifier, final Service metadata, final Object configuration) throws ConfigurationException {
        this.ensureExistingInstance(serviceType, identifier);
        serviceBusiness.configureInstance(serviceType, identifier, metadata, configuration, configClass);
        if (metadata != null && !identifier.equals(metadata.getIdentifier())) { // rename if necessary
            renameInstance(serviceType, identifier, metadata.getIdentifier());
        }
    }

    /**
     * Returns the configuration object of a service instance.
     *
     * @param serviceType The type of the service.
     * @param identifier the service
     * @return a configuration {@link Object} (depending on implementation)
     * @throws TargetNotFoundException if the service with specified identifier does not exist
     * @throws org.constellation.configuration.ConfigurationException if the operation has failed for any reason
     */
    public Object getInstanceConfiguration(final String serviceType, final String identifier) throws ConfigurationException {
        this.ensureExistingInstance(serviceType, identifier);
        return serviceBusiness.getInstanceConfiguration(serviceType, identifier, configClass);
    }

    /**
     * Updates a service instance configuration object.
     *
     * @param serviceType The type of the service.
     * @param identifier    the service identifier
     * @param configuration the service configuration
     * @throws TargetNotFoundException if the service with specified identifier does not exist
     * @throws org.constellation.configuration.ConfigurationException if the operation has failed for any reason
     */
    public void setInstanceConfiguration(final String serviceType, final String identifier, final Object configuration) throws ConfigurationException {
        this.ensureExistingInstance(serviceType, identifier);
        this.configureInstance(serviceType, identifier, getInstanceMetadata(serviceType, identifier), configuration);
    }

    /**
     * Returns a service instance metadata.
     *
     * @param serviceType The type of the service.
     * @param identifier the service identifier
     * @return 
     * @throws TargetNotFoundException if the service with specified identifier does not exist
     * @throws org.constellation.configuration.ConfigurationException if the operation has failed for any reason
     */
    public Service getInstanceMetadata(final String serviceType, final String identifier) throws ConfigurationException {
        this.ensureExistingInstance(serviceType.toUpperCase(), identifier);
        try {
            // todo add language parameter
            return ConfigurationEngine.readServiceMetadata(identifier, serviceType.toUpperCase(), null);
        } catch (JAXBException | IOException ex) {
            throw new ConfigurationException("The serviceMetadata.xml file can't be read.", ex);
        }
    }

    /**
     * Updates a service instance metadata.
     *
     * @param serviceType The type of the service.
     * @param identifier the service identifier
     * @param metadata   the service metadata
     * @throws TargetNotFoundException if the service with specified identifier does not exist
     * @throws org.constellation.configuration.ConfigurationException if the operation has failed for any reason
     */
    public void setInstanceMetadata(final String serviceType, final String identifier, final Service metadata) throws ConfigurationException {
        this.ensureExistingInstance(serviceType, identifier);
        this.configureInstance(serviceType, identifier, metadata, getInstanceConfiguration(serviceType, identifier));
    }

    /**
     * Find and returns a service {@link Instance}.
     *
     * @param serviceType The type of the service.
     * @param identifier the service identifier
     * @throws TargetNotFoundException if the service with specified identifier does not exist
     * @return an {@link Instance} instance
     */
    public Instance getInstance(final String serviceType, final String identifier) throws ConfigurationException {
        org.constellation.engine.register.Service service = serviceBusiness.getServiceByIdentifierAndType(serviceType, identifier);
        final Instance instance = new Instance(service.getId(), identifier, serviceType, getInstanceStatus(serviceType, identifier));
        Service metadata = null;
        try {
            metadata = getInstanceMetadata(serviceType, identifier);
        } catch (ConfigurationException ignore) {
            // Do nothing.
        }
        instance.setIdentifier(identifier);
        if (metadata != null) {
            instance.setName(metadata.getName());
            instance.set_abstract(metadata.getDescription());
            instance.setVersions(metadata.getVersions());
        } else {
            instance.set_abstract("");
            instance.setVersions(new ArrayList<String>());
        }
        return instance;
    }

    /**
     * Returns a service instance status.
     *
     * @param serviceType
     * @param identifier the service identifier
     * @return a {@link ServiceStatus} status
     * @throws TargetNotFoundException if the service with specified identifier does not exist
     */
    public ServiceStatus getInstanceStatus(final String serviceType, final String identifier) throws TargetNotFoundException {
        this.ensureExistingInstance(serviceType, identifier);
        for (Map.Entry<String, Boolean> entry : WSEngine.getEntriesStatus(serviceType)) {
            if (entry.getKey().equals(identifier)) {
                return entry.getValue() ? ServiceStatus.WORKING : ServiceStatus.ERROR;
            }
        }
        return ServiceStatus.NOT_STARTED;
    }

    /**
     * Returns all service instances (for current specification) status.
     *
     * @param spec
     * @return a {@link Map} of {@link ServiceStatus} status
     */
    public Map<String, ServiceStatus> getInstancesStatus(final String spec) {
        final Map<String, ServiceStatus> status = new HashMap<>();
        for (Map.Entry<String, Boolean> entry : WSEngine.getEntriesStatus(spec)) {
            status.put(entry.getKey(), entry.getValue() ? ServiceStatus.WORKING : ServiceStatus.ERROR);
        }
        final List<String> serviceIDs = ConfigurationEngine.getServiceConfigurationIds(spec);
        for (String serviceID : serviceIDs) {
            if (!WSEngine.serviceInstanceExist(spec, serviceID)) {
                status.put(serviceID, ServiceStatus.NOT_STARTED);
            }
        }
        return status;
    }

    /**
     * Returns list of service {@link Instance}(s) related to the {@link OGCConfigurer}
     * implementation.
     *
     * @param spec
     * @return the {@link Instance} list
     */
    public List<Instance> getInstances(final String spec) {
        final List<Instance> instances = new ArrayList<>();
        final Map<String, ServiceStatus> statusMap = getInstancesStatus(spec);
        for (final String key : statusMap.keySet()) {
            try {
                instances.add(getInstance(spec, key));
            } catch (ConfigurationException ignore) {
                // Do nothing.
            }
        }
        return instances;
    }

    /**
     * Returns a Constellation {@link ProcessDescriptor} from its name.
     *
     * @param name the process descriptor name
     * @return a {@link ProcessDescriptor} instance
     */
    protected ProcessDescriptor getProcessDescriptor(final String name) {
        try {
            return ProcessFinder.getProcessDescriptor(ConstellationProcessFactory.NAME, name);
        } catch (NoSuchIdentifierException ex) { // unexpected
            throw new IllegalStateException("Unexpected error has occurred", ex);
        }
    }

    /**
     * Ensure that a service instance really exists.
     *
     * @param identifier the service identifier
     * @throws TargetNotFoundException if the service with specified identifier does not exist
     */
    protected void ensureExistingInstance(final String spec, final String identifier) throws TargetNotFoundException {
        if (!WSEngine.serviceInstanceExist(spec.toUpperCase(), identifier)) {
            if (!ConfigurationEngine.serviceConfigurationExist(spec, identifier)) {
                throw new TargetNotFoundException(spec + " service instance with identifier \"" + identifier +
                        "\" not found. There is not configuration in the database.");
            }
        }
    }
}
