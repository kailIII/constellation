/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2009, Geomatys
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

package org.constellation.jaxb;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.geotoolkit.xml.MarshallerPool;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
public class AnchoredMarshallerPool extends MarshallerPool {

    public AnchoredMarshallerPool() throws JAXBException {
        super();
    }

    /**
     * Creates a new factory for the given class to be bound, with a default empty namespace.
     *
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetaData.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final Class<?>... classesToBeBound) throws JAXBException {
        super(classesToBeBound);
    }

    /**
     * Creates a new factory for the given class to be bound.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetaData.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final Class<?>... classesToBeBound) throws JAXBException {
        super(rootNamespace, classesToBeBound);
    }

    /**
     * Creates a new factory for the given class to be bound.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  classesToBeBound The classes to be bound, for example {@code DefaultMetaData.class}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final String schemaLocation, final Class<?>... classesToBeBound) throws JAXBException {
        super(getProperties(rootNamespace, schemaLocation), classesToBeBound);
    }

    /**
     * Creates a new factory for the given packages, with a default empty namespace.
     * The separator character for the packages is the colon.
     *
     * @param  packages         The packages in which JAXB will search for annotated classes to be bound,
     *                          for example {@code "org.geotoolkit.metadata.iso:org.geotoolkit.metadata.iso.citation"}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String packages) throws JAXBException {
        super(packages);
    }

    /**
     * Creates a new factory for the given packages. The separator character for the packages is the colon.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  packages         The packages in which JAXB will search for annotated classes to be bound,
     *                          for example {@code "org.geotoolkit.metadata.iso:org.geotoolkit.metadata.iso.citation"}.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final String packages) throws JAXBException {
        super(rootNamespace, packages);
    }

    /**
     * Creates a new factory for the given packages. The separator character for the packages is the colon.
     *
     * @param  rootNamespace    The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param  packages         The packages in which JAXB will search for annotated classes to be bound,
     *                          for example {@code "org.geotoolkit.metadata.iso:org.geotoolkit.metadata.iso.citation"}.
     * @param schemaLocation    The main xsd schema location for all the returned xml.
     * @throws JAXBException    If the JAXB context can not be created.
     */
    public AnchoredMarshallerPool(final String rootNamespace, final String packages, final String schemaLocation) throws JAXBException {
        super(getProperties(rootNamespace, schemaLocation), packages);
    
    }

    /**
     * Return a Map of Marshaller properties.
     *
     * @param rootNamespace The root namespace, for example {@code "http://www.isotc211.org/2005/gmd"}.
     * @param schemaLocation The main xsd schema location for all the returned xml.
     * @return
     */
    public static Map<String, String> getProperties(String rootNamespace, String schemaLocation) {
        final Map<String, String> properties = new HashMap<String, String>();
        properties.put(ROOT_NAMESPACE_KEY, rootNamespace);
        properties.put(Marshaller.JAXB_SCHEMA_LOCATION, schemaLocation);
        return properties;
    }

    @Override
    public void addAnchor(String text, URI uri) {
        super.addAnchor(text, uri);
    }
}
