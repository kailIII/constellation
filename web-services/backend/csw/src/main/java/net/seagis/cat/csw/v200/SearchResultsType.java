/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2008, Institut de Recherche pour le Développement
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */


package net.seagis.cat.csw.v200;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 *  Returns representations of result set members if maxRecords > 0.
 * 
 *      resultSetId       - id of the result set (a URI).
 *      
 *      elementSet        - The element set that has been returned (i.e., "brief", "summary", "full")
 * 
 *      recordSchema      - schema reference for included records(URI)
 * 
 *      numberOfRecordsMatched  - number of records matched by the query
 * 
 *      numberOfRecordsReturned - number of records returned to client
 * 
 *      nextRecord        - position of next record in the result set (0 if no records remain).
 * 
 *      expires           - the time instant when the result set expires and is discarded (ISO 8601 format)
 *          
 * 
 * <p>Java class for SearchResultsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SearchResultsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/cat/csw}AbstractRecord" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="resultSetId" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="elementSet" type="{http://www.opengis.net/cat/csw}ElementSetType" />
 *       &lt;attribute name="recordSchema" type="{http://www.w3.org/2001/XMLSchema}anyURI" />
 *       &lt;attribute name="numberOfRecordsMatched" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="numberOfRecordsReturned" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="nextRecord" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *       &lt;attribute name="expires" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SearchResultsType", propOrder = {
    "abstractRecord"
})
public class SearchResultsType {

    @XmlElementRef(name = "AbstractRecord", namespace = "http://www.opengis.net/cat/csw", type = JAXBElement.class)
    private List<JAXBElement<? extends AbstractRecordType>> abstractRecord;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String resultSetId;
    @XmlAttribute
    private ElementSetType elementSet;
    @XmlAttribute
    @XmlSchemaType(name = "anyURI")
    private String recordSchema;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    private BigInteger numberOfRecordsMatched;
    @XmlAttribute(required = true)
    @XmlSchemaType(name = "nonNegativeInteger")
    private BigInteger numberOfRecordsReturned;
    @XmlAttribute
    @XmlSchemaType(name = "nonNegativeInteger")
    private BigInteger nextRecord;
    @XmlAttribute
    private XMLGregorianCalendar expires;

    /**
     * Gets the value of the abstractRecord property.
     * 
     */
    public List<JAXBElement<? extends AbstractRecordType>> getAbstractRecord() {
        if (abstractRecord == null) {
            abstractRecord = new ArrayList<JAXBElement<? extends AbstractRecordType>>();
        }
        return this.abstractRecord;
    }

    /**
     * Gets the value of the resultSetId property.
     * 
     */
    public String getResultSetId() {
        return resultSetId;
    }

    /**
     * Sets the value of the resultSetId property.
     * 
    */
    public void setResultSetId(String value) {
        this.resultSetId = value;
    }

    /**
     * Gets the value of the elementSet property.
     * 
     */
    public ElementSetType getElementSet() {
        return elementSet;
    }

    /**
     * Sets the value of the elementSet property.
     * 
     */
    public void setElementSet(ElementSetType value) {
        this.elementSet = value;
    }

    /**
     * Gets the value of the recordSchema property.
     * 
     */
    public String getRecordSchema() {
        return recordSchema;
    }

    /**
     * Sets the value of the recordSchema property.
     * 
     */
    public void setRecordSchema(String value) {
        this.recordSchema = value;
    }

    /**
     * Gets the value of the numberOfRecordsMatched property.
     * 
     */
    public BigInteger getNumberOfRecordsMatched() {
        return numberOfRecordsMatched;
    }

    /**
     * Sets the value of the numberOfRecordsMatched property.
     * 
     */
    public void setNumberOfRecordsMatched(BigInteger value) {
        this.numberOfRecordsMatched = value;
    }

    /**
     * Gets the value of the numberOfRecordsReturned property.
     * 
     */
    public BigInteger getNumberOfRecordsReturned() {
        return numberOfRecordsReturned;
    }

    /**
     * Sets the value of the numberOfRecordsReturned property.
     * 
     */
    public void setNumberOfRecordsReturned(BigInteger value) {
        this.numberOfRecordsReturned = value;
    }

    /**
     * Gets the value of the nextRecord property.
     * 
     */
    public BigInteger getNextRecord() {
        return nextRecord;
    }

    /**
     * Sets the value of the nextRecord property.
     * 
     */
    public void setNextRecord(BigInteger value) {
        this.nextRecord = value;
    }

    /**
     * Gets the value of the expires property.
     * 
     */
    public XMLGregorianCalendar getExpires() {
        return expires;
    }

    /**
     * Sets the value of the expires property.
     * 
     */
    public void setExpires(XMLGregorianCalendar value) {
        this.expires = value;
    }

}
