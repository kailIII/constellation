//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.1.5-b01-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2007.12.14 at 03:51:19 PM CET 
//


package net.seagis.se;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GraphicStrokeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GraphicStrokeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/se}Graphic"/>
 *         &lt;element ref="{http://www.opengis.net/se}InitialGap" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/se}Gap" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GraphicStrokeType", propOrder = {
    "graphic",
    "initialGap",
    "gap"
})
public class GraphicStrokeType {

    @XmlElement(name = "Graphic", required = true)
    protected GraphicType graphic;
    @XmlElement(name = "InitialGap")
    protected ParameterValueType initialGap;
    @XmlElement(name = "Gap")
    protected ParameterValueType gap;

    /**
     * Gets the value of the graphic property.
     * 
     * @return
     *     possible object is
     *     {@link GraphicType }
     *     
     */
    public GraphicType getGraphic() {
        return graphic;
    }

    /**
     * Sets the value of the graphic property.
     * 
     * @param value
     *     allowed object is
     *     {@link GraphicType }
     *     
     */
    public void setGraphic(GraphicType value) {
        this.graphic = value;
    }

    /**
     * Gets the value of the initialGap property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getInitialGap() {
        return initialGap;
    }

    /**
     * Sets the value of the initialGap property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setInitialGap(ParameterValueType value) {
        this.initialGap = value;
    }

    /**
     * Gets the value of the gap property.
     * 
     * @return
     *     possible object is
     *     {@link ParameterValueType }
     *     
     */
    public ParameterValueType getGap() {
        return gap;
    }

    /**
     * Sets the value of the gap property.
     * 
     * @param value
     *     allowed object is
     *     {@link ParameterValueType }
     *     
     */
    public void setGap(ParameterValueType value) {
        this.gap = value;
    }

}
