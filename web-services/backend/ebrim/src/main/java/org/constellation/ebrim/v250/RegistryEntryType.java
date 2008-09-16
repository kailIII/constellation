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

package org.constellation.ebrim.v250;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RegistryEntryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RegistryEntryType">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}RegistryObjectType">
 *       &lt;attribute name="expiration" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="majorVersion" type="{http://www.w3.org/2001/XMLSchema}integer" default="1" />
 *       &lt;attribute name="minorVersion" type="{http://www.w3.org/2001/XMLSchema}integer" default="0" />
 *       &lt;attribute name="stability">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NCName">
 *             &lt;enumeration value="Dynamic"/>
 *             &lt;enumeration value="DynamicCompatible"/>
 *             &lt;enumeration value="Static"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="userVersion" type="{urn:oasis:names:tc:ebxml-regrep:rim:xsd:2.5}ShortName" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RegistryEntryType")
@XmlSeeAlso({
    RegistryPackageType.class,
    ExtrinsicObjectType.class,
    ClassificationSchemeType.class,
    ServiceType.class,
    RegistryType.class,
    FederationType.class
})
public class RegistryEntryType extends RegistryObjectType {

    @XmlAttribute
    private XMLGregorianCalendar expiration;
    @XmlAttribute
    private BigInteger majorVersion;
    @XmlAttribute
    private BigInteger minorVersion;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    private String stability;
    @XmlAttribute
    private String userVersion;

    /**
     * Gets the value of the expiration property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpiration() {
        return expiration;
    }

    /**
     * Sets the value of the expiration property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setExpiration(XMLGregorianCalendar value) {
        this.expiration = value;
    }

    /**
     * Gets the value of the majorVersion property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMajorVersion() {
        if (majorVersion == null) {
            return new BigInteger("1");
        } else {
            return majorVersion;
        }
    }

    /**
     * Sets the value of the majorVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMajorVersion(BigInteger value) {
        this.majorVersion = value;
    }

    /**
     * Gets the value of the minorVersion property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMinorVersion() {
        if (minorVersion == null) {
            return new BigInteger("0");
        } else {
            return minorVersion;
        }
    }

    /**
     * Sets the value of the minorVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMinorVersion(BigInteger value) {
        this.minorVersion = value;
    }

    /**
     * Gets the value of the stability property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStability() {
        return stability;
    }

    /**
     * Sets the value of the stability property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStability(String value) {
        this.stability = value;
    }

    /**
     * Gets the value of the userVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUserVersion() {
        return userVersion;
    }

    /**
     * Sets the value of the userVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUserVersion(String value) {
        this.userVersion = value;
    }

}
