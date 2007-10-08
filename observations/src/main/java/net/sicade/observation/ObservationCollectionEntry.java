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

package net.sicade.observation;

import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.opengis.observation.ObservationCollection;

/**
 *
 * @author legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObservationCollection")
@XmlRootElement(name = "ObservationCollection")
public class ObservationCollectionEntry implements ObservationCollection {

    /**
     *  The observation collection
     */
    private Collection<ObservationEntry> member;
    
    /**
     * A JAXB constructor. 
     */
    public ObservationCollectionEntry() {}
    
    /**
     * Build a new Collection of Observation
     */
    public ObservationCollectionEntry(Collection<ObservationEntry> member) {
        this.member = member;
    }
    
    /**
     * Add a new Observation to the collection. 
     */
    public void add(ObservationEntry observation) {
        this.member.add(observation);
    }
    
    /**
     * Return a collection of Observation
     */
    public Collection<ObservationEntry> getMember() {
        return this.member;
    }

}
