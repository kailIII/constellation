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

package org.constellation.swe.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractVectorType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractVectorType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/swe/1.0}AbstractDataRecordType">
 *       &lt;attribute name="referenceFrame" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="localFrame" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractVectorType")
@XmlSeeAlso({
    GeoLocationArea.class,
    VectorType.class,
    EnvelopeType.class,
    PositionType.class
})
public abstract class AbstractVectorType extends AbstractDataRecordType {

    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String referenceFrame;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String localFrame;

    public AbstractVectorType() {

    }

    public AbstractVectorType(String definition) {
        super(definition);
    }

    public AbstractVectorType(String referenceFrame, String localFrame) {
        this.localFrame = localFrame;
        this.referenceFrame = referenceFrame;
    }

    /**
     * Gets the value of the referenceFrame property.
     */
    public String getReferenceFrame() {
        return referenceFrame;
    }

    /**
     * Sets the value of the referenceFrame property.
     */
    public void setReferenceFrame(String value) {
        this.referenceFrame = value;
    }

    /**
     * Gets the value of the localFrame property.
     */
    public String getLocalFrame() {
        return localFrame;
    }

    /**
     * Sets the value of the localFrame property.
     */
    public void setLocalFrame(String value) {
        this.localFrame = value;
    }

}
