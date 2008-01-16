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
 * <p>Java class for StrokeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StrokeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice minOccurs="0">
 *           &lt;element ref="{http://www.opengis.net/se}GraphicFill"/>
 *           &lt;element ref="{http://www.opengis.net/se}GraphicStroke"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://www.opengis.net/se}SvgParameter" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StrokeType", propOrder = {
    "graphicFill",
    "graphicStroke",
    "svgParameter"
})
public class StrokeType {

    @XmlElement(name = "GraphicFill")
    private GraphicFillType graphicFill;
    @XmlElement(name = "GraphicStroke")
    private GraphicStrokeType graphicStroke;
    @XmlElement(name = "SvgParameter")
    private List<SvgParameterType> svgParameter;

    /**
     * Empty Constructor used by JAXB.
     */
    StrokeType() {
        
    }
    
    /**
     * Build a new Stroke.
     */
    public StrokeType(GraphicFillType graphicFill, GraphicStrokeType graphicStroke, 
            List<SvgParameterType> svgParameter) {
        this.graphicFill   = graphicFill;
        this.graphicStroke = graphicStroke;
        this.svgParameter  = svgParameter;
    }
    
    /**
     * Gets the value of the graphicFill property.
     */
    public GraphicFillType getGraphicFill() {
        return graphicFill;
    }

    /**
     * Gets the value of the graphicStroke property.
     */
    public GraphicStrokeType getGraphicStroke() {
        return graphicStroke;
    }

    /**
     * Gets the value of the svgParameter property.
     * (unmodifiable)
     */
    public List<SvgParameterType> getSvgParameter() {
        if (svgParameter == null) {
            svgParameter = new ArrayList<SvgParameterType>();
        }
        return Collections.unmodifiableList(svgParameter);
    }

}
