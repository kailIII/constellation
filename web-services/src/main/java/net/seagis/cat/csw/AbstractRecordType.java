/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2005, Institut de Recherche pour le Développement
 * (C) 2007, Geomatys
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


package net.seagis.cat.csw;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractRecordType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractRecordType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractRecordType")
@XmlSeeAlso({
    DCMIRecordType.class,
    SummaryRecordType.class,
    BriefRecordType.class
})
public abstract class AbstractRecordType {
    
    @XmlTransient
    protected static net.seagis.ows.v100.ObjectFactory owsFactory = new net.seagis.ows.v100.ObjectFactory();
    
    @XmlTransient
    protected static net.seagis.dublincore.elements.ObjectFactory dublinFactory = new net.seagis.dublincore.elements.ObjectFactory();
    
    @XmlTransient
    protected static net.seagis.dublincore.terms.ObjectFactory dublinTermFactory = new net.seagis.dublincore.terms.ObjectFactory();

}
