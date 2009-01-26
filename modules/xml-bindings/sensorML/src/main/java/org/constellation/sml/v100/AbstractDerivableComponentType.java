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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.constellation.sml.AbstractDerivableComponent;
import org.geotools.util.Utilities;


/**
 * Complex Type to allow creation of component profiles by extension
 * 
 * <p>Java class for AbstractDerivableComponentType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractDerivableComponentType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/sensorML/1.0}AbstractProcessType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}spatialReferenceFrame" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}temporalReferenceFrame" minOccurs="0"/>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/sensorML/1.0}location"/>
 *           &lt;element ref="{http://www.opengis.net/sensorML/1.0}position"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}timePosition" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/sensorML/1.0}interfaces" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractDerivableComponentType", propOrder = {
    "rest",
    "location",
    "spatialReferenceFrame",
    "position"

})
@XmlSeeAlso({AbstractComponentType.class, ComponentArrayType.class}) 
public abstract class AbstractDerivableComponentType extends AbstractProcessType implements AbstractDerivableComponent {

    @XmlElementRefs({
        @XmlElementRef(name = "timePosition",           namespace = "http://www.opengis.net/sensorML/1.0", type = TimePosition.class),
        @XmlElementRef(name = "temporalReferenceFrame", namespace = "http://www.opengis.net/sensorML/1.0", type = TemporalReferenceFrame.class),
        @XmlElementRef(name = "interfaces",             namespace = "http://www.opengis.net/sensorML/1.0", type = Interfaces.class)
    })
    private List<Object> rest;

    @XmlElementRef(name = "spatialReferenceFrame",  namespace = "http://www.opengis.net/sensorML/1.0", type = SpatialReferenceFrame.class)
    private SpatialReferenceFrame spatialReferenceFrame;

    @XmlElementRef(name = "location", namespace = "http://www.opengis.net/sensorML/1.0", type = Location.class)
    private Location location;

    @XmlElementRef(name = "position", namespace = "http://www.opengis.net/sensorML/1.0", type = Position.class)
    private Position position;
    

    /**
     * Gets the rest of the content model. 
     * 
     */
    public List<Object> getRest() {
        if (rest == null) {
            rest = new ArrayList<Object>();
        }
        return this.rest;
    }

    public Location getSMLLocation() {
        return location;
    }
    
    public void setSMLLocation(Location location) {
        this.location = location;
    }

    /**
     * @return the spatialReferenceFrame
     */
    public SpatialReferenceFrame getSpatialReferenceFrame() {
        return spatialReferenceFrame;
    }

    /**
     * @param spatialReferenceFrame the spatialReferenceFrame to set
     */
    public void setSpatialReferenceFrame(SpatialReferenceFrame spatialReferenceFrame) {
        this.spatialReferenceFrame = spatialReferenceFrame;
    }

    /**
     * @return the position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @param position the position to set
     */
    public void setPosition(Position position) {
        this.position = position;
    }
    
    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }

        if (object instanceof AbstractDerivableComponentType && super.equals(object)) {
            final AbstractDerivableComponentType that = (AbstractDerivableComponentType) object;
            return Utilities.equals(this.rest,                  that.rest) &&
                   Utilities.equals(this.spatialReferenceFrame, that.spatialReferenceFrame) &&
                   Utilities.equals(this.location,              that.location) &&
                   Utilities.equals(this.position,              that.position);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (this.rest != null ? this.rest.hashCode() : 0);
        hash = 29 * hash + (this.spatialReferenceFrame != null ? this.spatialReferenceFrame.hashCode() : 0);
        hash = 29 * hash + (this.location != null ? this.location.hashCode() : 0);
        hash = 29 * hash + (this.position != null ? this.position.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString()).append('\n');
        if (location != null)
            sb.append("location: ").append(location).append('\n');
        if (position != null)
            sb.append("position: ").append(position).append('\n');
        if (spatialReferenceFrame != null)
            sb.append("spatialReferenceFrame: ").append(spatialReferenceFrame).append('\n');
        if (rest != null) {
            sb.append("rest:").append('\n');
            int i = 0;
            for (Object r: rest) {
                sb.append("rest n°").append(i).append(": ").append(r).append('\n');
            }
        }
        return sb.toString();
    }

}
