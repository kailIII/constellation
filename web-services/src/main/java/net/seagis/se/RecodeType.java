/*
 * Sicade - SystÃ¨mes intÃ©grÃ©s de connaissances pour l'aide Ã  la dÃ©cision en environnement
 * (C) 2005, Institut de Recherche pour le DÃ©veloppement
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

package net.seagis.se;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RecodeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RecodeType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/se}FunctionType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}LookupValue"/>
 *         &lt;element ref="{http://www.opengis.net/se}MapItem" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecodeType", propOrder = {
    "lookupValue",
    "mapItem"
})
public class RecodeType extends FunctionType  {

    @XmlElement(name = "LookupValue", required = true)
    private ParameterValueType lookupValue;
    @XmlElement(name = "MapItem", required = true)
    private List<MapItemType> mapItem;

    /**
     * Empty Constructor used by JAXB.
     */
    RecodeType() {
        
    }
    
    /**
     * Build a new Recode type.
     */
    public RecodeType(ParameterValueType lookupValue, List<MapItemType> mapItem) {
        this.lookupValue = lookupValue;
        this.mapItem     = mapItem;
    }
    
    /**
     * Gets the value of the lookupValue property.
     */
    public ParameterValueType getLookupValue() {
        return lookupValue;
    }

    /**
     * Gets the value of the mapItem property.
     */
    public List<MapItemType> getMapItem() {
        if (mapItem == null) {
            mapItem = new ArrayList<MapItemType>();
        }
        return Collections.unmodifiableList(mapItem);
    }

}
