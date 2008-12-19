/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
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

package org.constellation.metadata.io;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import org.constellation.cat.csw.v202.DomainValuesType;
import org.constellation.cat.csw.v202.ElementSetType;
import org.constellation.ws.WebServiceException;

/**
 *
 * @author Guilhem Legal
 */
public abstract class MetadataReader {
    
    public final static int DUBLINCORE = 0;
    public final static int ISO_19115  = 1;
    public final static int EBRIM      = 2;
    
    /**
     * A debugging logger
     */
    protected Logger logger = Logger.getLogger("org.constellation.metadata.io");
    
    /**
     * A flag indicating if the cache mecanism is enabled or not.
     */
    private final boolean isCacheEnabled;
    
    /**
     * A map containing the metadata already extract from the database.
     */
    private final Map<String, Object> metadatas = new HashMap<String, Object>();
    
    public MetadataReader(boolean isCacheEnabled) {
        this.isCacheEnabled = isCacheEnabled;
    }
    
    /**
     * Return a metadata object from the specified identifier.
     * 
     * @param identifier The metadata identifier.
     * @param mode An output schema mode: EBRIM, ISO_19115 and DUBLINCORE supported.
     * @param type An elementSet: FULL, SUMMARY and BRIEF. (implies elementName == null)
     * @param elementName A list of QName describing the requested fields. (implies type == null)
     * 
     * @return A marshallable metadata object.
     * @throws java.sql.SQLException
     */
    public abstract Object getMetadata(String identifier, int mode, ElementSetType type, List<QName> elementName) throws SQLException, WebServiceException;
    
    /**
     * Return a list of values for each specific fields specified as a coma separated String.
     */
    public abstract List<DomainValuesType> getFieldDomainofValues(String propertyNames) throws WebServiceException;
    
    /**
     * Execute a SQL query and return the result as a List of identifier;
     * 
     * @param query
     * @return
     * @throws WebServiceException
     */
    public abstract List<String> executeEbrimSQLQuery(String SQLQuery) throws WebServiceException;
    
    /**
     * Return all the entries from the database
     */
    public abstract List<? extends Object> getAllEntries() throws WebServiceException;
    
    /**
     * Return the list of supported data types.
     */
    public abstract List<Integer> getSupportedDataTypes();

    /**
     * Return the list of QName for Additional queryable element.
     */
    public abstract List<QName> getAdditionalQueryableQName();

    /**
     * Return the list of Additional queryable element.
     */
    public abstract Map<String, List<String>> getAdditionalQueryablePathMap();

    /**
     * Destroy all the resource used by this reader.
     */
    public abstract void destroy();
    
    /**
     * Add a metadata to the cache.
     * @param identifier The metadata identifier.
     * @param metadata The object to put in cache.
     */
    protected void addInCache(String identifier,  Object metadata) {
        if (isCacheEnabled)
            metadatas.put(identifier, metadata);
    }
    
    /**
     * Return a metadata from the cache if it present.
     * 
     * @param identifier The metadata identifier.
     */
    protected Object getFromCache(String identifier) {
        return metadatas.get(identifier);
    }
    
    
    /**
     * Return true is the cache mecanism is enbled.
     */
    public boolean isCacheEnabled() {
        return isCacheEnabled;
    }
}
