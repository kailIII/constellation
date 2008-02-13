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


package net.seagis.ows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import net.seagis.wcs.v111.AxisType;
import net.seagis.wcs.v111.CoverageDescriptionType;
import net.seagis.wcs.v111.CoverageSummaryType;
import net.seagis.wcs.v111.FieldType;
import org.geotools.resources.Utilities;


/**
 * Human-readable descriptive information for the object it is included within.
 * This type shall be extended if needed for specific OWS use to include additional metadata for each type of information. This type shall not be restricted for a specific OWS to change the multiplicity (or optionality) of some elements.
 * 			If the xml:lang attribute is not included in a Title, Abstract or Keyword element, then no language is specified for that element unless specified by another means.  All Title, Abstract and Keyword elements in the same Description that share the same xml:lang attribute value represent the description of the parent object in that language. Multiple Title or Abstract elements shall not exist in the same Description with the same xml:lang attribute value unless otherwise specified. 
 * 
 * <p>Java class for DescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Title" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Abstract" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element ref="{http://www.opengis.net/ows/1.1}Keywords" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescriptionType", propOrder = {
    "title",
    "_abstract",
    "keywords"
})
@XmlSeeAlso({
    CoverageSummaryType.class,
    CoverageDescriptionType.class,
    FieldType.class,
    AxisType.class,
    BasicIdentificationType.class,
    ServiceIdentification.class
})
public class DescriptionType {

    @XmlElement(name = "Title")
    private List<LanguageStringType> title = new ArrayList<LanguageStringType>();
    @XmlElement(name = "Abstract")
    private List<LanguageStringType> _abstract = new ArrayList<LanguageStringType>();;
    @XmlElement(name = "Keywords")
    private List<KeywordsType> keywords = new ArrayList<KeywordsType>();

    /**
     * An empty constructor used by JAXB.
     */
    protected DescriptionType() {
    }
    
    /**
     * Build a new DescriptionType.
     */
    public DescriptionType(List<LanguageStringType> title,  List<LanguageStringType> _abstract,
            List<KeywordsType> keywords) {
        this._abstract = _abstract;
        this.keywords  = keywords;
        this.title     = title;
    }
    
    /**
     * Gets the value of the title property.
     */
    public List<LanguageStringType> getTitle() {
        return Collections.unmodifiableList(title);
    }

    /**
     * Gets the value of the abstract property.
     */
    public List<LanguageStringType> getAbstract() {
        return Collections.unmodifiableList(_abstract);
    }

    /**
     * Gets the value of the keywords property.
     */
    public List<KeywordsType> getKeywords() {
        return Collections.unmodifiableList(keywords);
    }
    
    /**
     * Verify that this entry is identical to the specified object.
     */
    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        final DescriptionType that = (DescriptionType) object;

        return Utilities.equals(this._abstract, that._abstract) &&
               Utilities.equals(this.keywords, that.keywords) &&
               Utilities.equals(this.title, that.title);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.title != null ? this.title.hashCode() : 0);
        hash = 59 * hash + (this._abstract != null ? this._abstract.hashCode() : 0);
        hash = 59 * hash + (this.keywords != null ? this.keywords.hashCode() : 0);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("class=DescriptionType").append('\n');
        s.append("title:").append('\n');
        for (LanguageStringType l:title) {
             s.append(l).append('\n');
        }
        s.append("abstract:").append('\n');
        for (LanguageStringType l:_abstract) {
             s.append(l).append('\n');
        }
        s.append("keywords:").append('\n');
        for (KeywordsType l:keywords) {
             s.append(l).append('\n');
        }
        return s.toString();
    }
}
