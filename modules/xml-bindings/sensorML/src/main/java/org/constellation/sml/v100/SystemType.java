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

package org.constellation.sml.v100;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SystemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SystemType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorML/1.0}AbstractComponentType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}components" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}positions" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}connections" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SystemType")
public class SystemType extends AbstractComponentType {

    @XmlElement(required = true)
    private Components components;
    @XmlElement(required = true)
    private Positions positions;
    @XmlElement(required = true)
    private Connections connections;

    /**
     * @return the components
     */
    public Components getComponents() {
        return components;
    }

    /**
     * @param components the components to set
     */
    public void setComponents(Components components) {
        this.components = components;
    }

    /**
     * @return the positions
     */
    public Positions getPositions() {
        return positions;
    }

    /**
     * @param positions the positions to set
     */
    public void setPositions(Positions positions) {
        this.positions = positions;
    }

    /**
     * @return the connections
     */
    public Connections getConnections() {
        return connections;
    }

    /**
     * @param connections the connections to set
     */
    public void setConnections(Connections connections) {
        this.connections = connections;
    }

}
