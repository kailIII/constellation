/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
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
package net.seagis.metadata;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import net.seagis.catalog.Entry;
import org.opengis.metadata.citation.Contact;
import org.opengis.metadata.citation.ResponsibleParty;
import org.opengis.metadata.citation.Role;
import org.opengis.util.InternationalString;

/**
 *
 * @author legal
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponsibleParty")
public class ResponsiblePartyEntry extends Entry implements ResponsibleParty {

    public String getIndividualName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InternationalString getOrganisationName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public InternationalString getPositionName() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Contact getContactInfo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Role getRole() {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
