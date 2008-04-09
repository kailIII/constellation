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

package net.seagis.ogc;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import net.seagis.gml.EnvelopeEntry;
import net.seagis.gml.EnvelopeWithTimePeriodType;


/**
 * <p>Java class for BBOXType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BBOXType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/ogc}SpatialOpsType">
 *       &lt;sequence>
 *         &lt;element name="PropertyName" type="{http://www.opengis.net/ogc}PropertyNameType" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element ref="{http://www.opengis.net/gml}Envelope"/>
 *           &lt;element ref="{http://www.opengis.net/gml}EnvelopeWithTimePeriod"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BBOXType", propOrder = {
    "propertyName",
    "envelope",
    "envelopeWithTimePeriod"
})
public class BBOXType extends SpatialOpsType {

    @XmlElement(name = "PropertyName")
    private String propertyName;
    @XmlElement(name = "Envelope", namespace = "http://www.opengis.net/gml", nillable = true)
    private EnvelopeEntry envelope;
    @XmlElement(name = "EnvelopeWithTimePeriod", namespace = "http://www.opengis.net/gml", nillable = true)
    private EnvelopeWithTimePeriodType envelopeWithTimePeriod;

    /**
     * Gets the value of the propertyName property.
     */
    public String getPropertyName() {
        return propertyName;
    }

    /**
     * Gets the value of the envelope property.
     */
    public EnvelopeEntry getEnvelope() {
        return envelope;
    }

    /**
     * Gets the value of the envelopeWithTimePeriod property.
     */
    public EnvelopeWithTimePeriodType getEnvelopeWithTimePeriod() {
        return envelopeWithTimePeriod;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(super.toString());
        s.append("PropertyName=").append(s).append('\n');
        if (envelope != null) {
            s.append("envelope= ").append(envelope.toString()).append('\n');
        } else {
            s.append("envelope null").append('\n');
        }
        if (envelopeWithTimePeriod != null) {
            s.append("envelope with time period= ").append(envelopeWithTimePeriod.toString()).append('\n');
        } else {
            s.append("envelope with time null").append('\n');
        }
        return s.toString();
    }
}
