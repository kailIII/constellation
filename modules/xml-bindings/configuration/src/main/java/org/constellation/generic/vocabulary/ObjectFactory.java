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

package org.constellation.generic.vocabulary;

import javax.xml.bind.annotation.XmlRegistry;


/**
 * This object contains factory methods for each Java content interface and Java element interface 
 * generated in the generated package. 
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content. 
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding of schema 
 * type definitions, element declarations and model groups.  
 * Factory methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {


    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: o
     * rg.constellation.generic.vocabulary
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@Vocabulary Vocabulary }
     * 
     */
    public Vocabulary createVocabulary() {
        return new Vocabulary();
    }
    
    /**
     * Create an instance of {@Keyword Keyword }
     * 
     */
    public Keyword createKeyword() {
        return new Keyword();
    }
    
    /**
     * Create an instance of {@GeoObjTypCd GeoObjTypCd }
     * 
     */
    public GeoObjTypCd createGeoObjTypCd() {
        return new GeoObjTypCd();
    }
}
