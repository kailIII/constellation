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

package org.constellation.utils;

import org.apache.sis.util.Static;
import org.constellation.configuration.ConfigDirectory;
import org.constellation.configuration.ConfigurationException;
import org.constellation.dto.Service;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.constellation.generic.database.GenericDatabaseMarshallerPool;

/**
 * Utility class for map services management/configuration.
 *
 * @author Fabien Bernard (Geomatys).
 * @version 0.9
 * @since 0.9
 */
public final class MetadataUtilities extends Static {

    /**
     * The service metadata file name.
     */
    private static final String METADATA_FILE_NAME = "serviceMetadata.xml";

    /**
     * Error message for invalid directories.
     */
    private static final String INVALID_DIRECTORY ="The $ folder does not exist or is not a directory.";


    /**
     * Gets the service instance folder from its identifier.
     *
     * @param identifier  the service identifier
     * @param serviceType the service type (WMS, WFS, WPS...)
     * @return the service instance folder
     * @throws ConfigurationException if the service instance directory doesn't exists
     */
    public static File getInstanceDirectory(final String identifier, String serviceType) throws ConfigurationException {
        ensureNonNull("identifier",  identifier);
        ensureNonNull("serviceType", serviceType);

        serviceType = serviceType.toUpperCase();
        final File cstlDirectory = ConfigDirectory.getConfigDirectory();
        if (!cstlDirectory.exists() || !cstlDirectory.isDirectory()) {
            throw new ConfigurationException(INVALID_DIRECTORY.replace("$",".constellation"));
        }
        final File wmsDirectory = new File(cstlDirectory, serviceType);
        if (!wmsDirectory.exists() || !wmsDirectory.isDirectory()) {
            throw new ConfigurationException(INVALID_DIRECTORY.replace("$",".constellation/" + serviceType));
        }
        final File instanceDirectory = new File(wmsDirectory,  identifier);
        if (!instanceDirectory.exists() || !instanceDirectory.isDirectory()) {
            throw new ConfigurationException(INVALID_DIRECTORY.replace("$",".constellation/" + serviceType + "/" + identifier));
        }
        return instanceDirectory;
    }

    /**
     * Writes the service metadata file into the service instance directory.
     *
     * @param directory the service instance directory
     * @param metadata  the service metadata
     * @throws IOException if failed to write the service metadata for any reason
     */
    public static void writeMetadata(final File directory, final Service metadata) throws IOException {
        ensureNonNull("directory", directory);
        ensureNonNull("metadata", metadata);

        try {
            final Marshaller m = GenericDatabaseMarshallerPool.getInstance().acquireMarshaller();
            final File metadataFile = new File(directory, METADATA_FILE_NAME);
            m.marshal(metadata, metadataFile);
            GenericDatabaseMarshallerPool.getInstance().recycle(m);
        } catch (JAXBException ex) {
            throw new IOException("Metadata marshalling has failed.", ex);
        }
    }

    /**
     * Writes the service metadata file into the service instance directory.
     *
     * @param identifier  the service identifier
     * @param serviceType the service type (WMS, WFS, WPS...)
     * @param metadata    the service metadata
     * @throws ConfigurationException if the service instance directory doesn't exists
     * @throws IOException if failed to write the service metadata for any reason
     */
    public static void writeMetadata(final String identifier, final String serviceType, final Service metadata) throws ConfigurationException, IOException {
        ensureNonNull("identifier",  identifier);
        ensureNonNull("serviceType", serviceType);
        ensureNonNull("metadata",    metadata);

        writeMetadata(getInstanceDirectory(identifier, serviceType), metadata);
    }

    /**
     * Writes the service metadata file into the service instance directory.
     *
     * @param directory the service instance directory
     * @throws IOException if failed to read the service metadata for any reason
     */
    public static Service readMetadata(final File directory) throws IOException {
        ensureNonNull("directory", directory);

        final File metadataFile = new File(directory, METADATA_FILE_NAME);
        if (metadataFile.exists() && !metadataFile.isDirectory()) {
            try {
                final Unmarshaller um = GenericDatabaseMarshallerPool.getInstance().acquireUnmarshaller();
                final Object metadata = um.unmarshal(metadataFile);
                GenericDatabaseMarshallerPool.getInstance().recycle(um);
                if (metadata instanceof Service) {
                    return (Service) metadata;
                }
                throw new IOException("Unexpected metadata object: " + metadata.getClass());
            } catch (JAXBException ex) {
                throw new IOException("Metadata unmarshalling has failed.", ex);
            }
        }
        return null;
    }

    /**
     * Reads the service metadata file from the service instance directory.
     *
     * @param identifier  the service identifier
     * @param serviceType the service type (WMS, WFS, WPS...)
     * @return the service metadata
     * @throws ConfigurationException if the service instance directory doesn't exists
     * @throws IOException if failed to read the service metadata for any reason
     */
    public static Service readMetadata(final String identifier, final String serviceType) throws ConfigurationException, IOException {
        ensureNonNull("identifier",  identifier);
        ensureNonNull("serviceType", serviceType);

        return readMetadata(getInstanceDirectory(identifier, serviceType));
    }
}