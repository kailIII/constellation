/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2005, Institut de Recherche pour le Développement
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


package net.seagis.swe;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import org.geotools.resources.Utilities;


/**
 * <p>Java class for PhenomenonPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PhenomenonPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element ref="{http://www.opengis.net/swe/1.0.1}Phenomenon"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml}AssociationAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PhenomenonPropertyType", propOrder = {
    "phenomenon"
})
public class PhenomenonPropertyType {

    @XmlElementRef(name = "Phenomenon", namespace = "http://www.opengis.net/swe/1.0.1", type = JAXBElement.class)
    private JAXBElement<? extends PhenomenonEntry> phenomenon;
    
    /**
     * Allow to record the pehnomenon when its in href mode
     */
    @XmlTransient
    JAXBElement<? extends PhenomenonEntry>  hiddenPhenomenon;
    
    @XmlAttribute(namespace = "http://www.opengis.net/gml")
    @XmlSchemaType(name = "anyURI")
    private String remoteSchema;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String type;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String href;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String role;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    @XmlSchemaType(name = "anyURI")
    private String arcrole;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String title;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String show;
    @XmlAttribute(namespace = "http://www.w3.org/1999/xlink")
    private String actuate;

    @XmlTransient
    private static ObjectFactory sweFactory = new ObjectFactory();
    
    /**
     * An empty constructor used by JAXB.
     */
    PhenomenonPropertyType() {
        
    }
    
    /**
     * Build a new Phenomenon Property.
     */
    public PhenomenonPropertyType(PhenomenonEntry observedProperty) {
        
        if (observedProperty instanceof CompositePhenomenonEntry) {
            CompositePhenomenonEntry compo = (CompositePhenomenonEntry)observedProperty;
            this.phenomenon    = sweFactory.createCompositePhenomenon(compo);
        } else if (observedProperty instanceof PhenomenonEntry) {
            this.phenomenon    = sweFactory.createPhenomenon(observedProperty);
        } else {
            throw new IllegalArgumentException("only phenomenonEntry and CompositePhenomenonEntry are allowed");
        }
    }
    
    /**
     * Set the phenomenon into href mode.
     */
    public void setToHref() {
        if (phenomenon != null) {
            this.href = phenomenon.getValue().getPhenomenonName();
            hiddenPhenomenon = phenomenon;
            phenomenon = null;
        }
    }
    
    /**
     * Gets the value of the phenomenon property.
     */
    public PhenomenonEntry getPhenomenon() {
        if (phenomenon != null) {
            return phenomenon.getValue();
        } else {
            if (hiddenPhenomenon != null) {
                return hiddenPhenomenon.getValue();
            }
            return null;
        }
    }

    /**
     * Gets the value of the remoteSchema property.
     */
    public String getRemoteSchema() {
        return remoteSchema;
    }

    /**
     * Gets the value of the type property.
     */
    public String getType() {
        if (type == null) {
            return "simple";
        } else {
            return type;
        }
    }

    /**
     * Gets the value of the href property.
     */
    public String getHref() {
        return href;
    }

    /**
     * Gets the value of the role property.
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets the value of the arcrole property.
     */
    public String getArcrole() {
        return arcrole;
    }

    /**
     * Gets the value of the title property.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the value of the show property.
     */
    public String getShow() {
        return show;
    }

    /**
     * Gets the value of the actuate property.
     */
    public String getActuate() {
        return actuate;
    }
    
    /**
     * Verify if this entry is identical to specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        boolean pheno = false;
        final PhenomenonPropertyType that = (PhenomenonPropertyType) object;
        if (this.phenomenon != null && that.phenomenon != null) {
            pheno = Utilities.equals(this.phenomenon.getValue(), that.phenomenon.getValue());
            //System.out.println("Phenomenon NOT NULL :" + pheno);
        } else {
            pheno = (this.phenomenon == null && that.phenomenon == null);
            //System.out.println("Phenomenon NULL :" + pheno);
        }
        
        boolean hiddenPheno = false;
        if (this.hiddenPhenomenon != null && that.hiddenPhenomenon != null) {
            hiddenPheno = Utilities.equals(this.hiddenPhenomenon.getValue(), that.hiddenPhenomenon.getValue());
            //System.out.println("Phenomenon NOT NULL :" + pheno);
        } else {
            hiddenPheno = (this.hiddenPhenomenon == null && that.hiddenPhenomenon == null);
            //System.out.println("Phenomenon NULL :" + pheno);
        }
        
        return pheno                                                            &&
               hiddenPheno                                                      &&
               Utilities.equals(this.actuate,            that.actuate)          &&
               Utilities.equals(this.arcrole,            that.arcrole)          &&  
               Utilities.equals(this.type,               that.type)             &&
               Utilities.equals(this.href,               that.href)             &&
               Utilities.equals(this.remoteSchema,       that.remoteSchema)     &&
               Utilities.equals(this.show,               that.show)             &&
               Utilities.equals(this.role,               that.role)             &&
               Utilities.equals(this.title,              that.title);
    }

    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (this.phenomenon != null ? this.phenomenon.hashCode() : 0);
        hash = 47 * hash + (this.remoteSchema != null ? this.remoteSchema.hashCode() : 0);
        hash = 47 * hash + (this.actuate != null ? this.actuate.hashCode() : 0);
        hash = 47 * hash + (this.arcrole != null ? this.arcrole.hashCode() : 0);
        hash = 47 * hash + (this.href != null ? this.href.hashCode() : 0);
        hash = 47 * hash + (this.role != null ? this.role.hashCode() : 0);
        hash = 47 * hash + (this.show != null ? this.show.hashCode() : 0);
        hash = 47 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 47 * hash + (this.type != null ? this.type.hashCode() : 0);
        return hash;
    }

    /**
     * Retourne une representation de l'objet.
     */
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        if (phenomenon != null)
            s.append(phenomenon.getValue().toString()).append('\n');
        
        if(actuate != null) {
            s.append("actuate=").append(actuate).append('\n');
        }
        if(arcrole != null) {
            s.append("arcrole=").append(arcrole).append('\n');
        }
        if(href != null) {
            s.append("href=").append(href).append('\n');
        }
        if(role != null) {
            s.append("role=").append(role).append('\n');
        }
        if(show != null) {
            s.append("show=").append(show).append('\n');
        }
        if(title != null) {
            s.append("title=").append(title).append('\n');
        }
        if(title != null) {
            s.append("title=").append(title).append('\n');
        }
        return s.toString();
    }
}