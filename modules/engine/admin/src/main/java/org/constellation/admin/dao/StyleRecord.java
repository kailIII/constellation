/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2013, Geomatys
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

package org.constellation.admin.dao;

import org.geotoolkit.style.MutableStyle;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Fabien Bernard (Geomatys).
 * @version 0.9
 * @since 0.9
 */
public final class StyleRecord implements Record {

    public static enum StyleType {
        VECTOR,
        COVERAGE
    }

    private final Session session;

    final int id;
    private String name;
    private int provider;
    private StyleType type;
    private final Date date;
    private final int title;
    private final int description;
    private String owner;

    StyleRecord(final Session session, final int id, final String name, final int provider, final StyleType type,
                final Date date, final int title, final int description, final String owner) {
        this.session     = session;
        this.id          = id;
        this.name        = name;
        this.provider    = provider;
        this.type        = type;
        this.date        = date;
        this.title       = title;
        this.description = description;
        this.owner       = owner;
    }

    public StyleRecord(final Session s, final ResultSet rs) throws SQLException {
        this(s, rs.getInt(1),
                rs.getString(2),
                rs.getInt(3),
                StyleType.valueOf(rs.getString(4)),
                new Date(rs.getLong(5)),
                rs.getInt(6),rs.getInt(7),
                rs.getString(8));
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) throws SQLException {
        this.name = name;
        session.updateStyle(id, name, provider, type, owner);
    }

    public ProviderRecord getProvider() throws SQLException {
        return session.readProvider(provider);
    }

    public void setProvider(final ProviderRecord provider) throws SQLException {
        this.provider = provider.id;
        session.updateStyle(id, name, provider.id, type, owner);
    }

    public StyleType getType() {
        return type;
    }

    public void setType(final StyleType type) throws SQLException {
        this.type = type;
        session.updateStyle(id, name, provider, type, owner);
    }

    public Date getDate() {
        return date;
    }

    public String getTitle(final Locale locale) throws SQLException {
        return session.readI18n(title, locale);
    }

    public void setTitle(final Locale locale, final String value) throws SQLException {
        session.updateI18n(title, locale, value);
    }

    public String getDescription(final Locale locale) throws SQLException {
        return session.readI18n(description, locale);
    }

    public void setDescription(final Locale locale, final String value) throws SQLException {
        session.updateI18n(description, locale, value);
    }

    public InputStream getBody() throws SQLException {
        return session.readStyleBody(id);
    }

    public void setBody(final MutableStyle style) throws SQLException, IOException {
        session.updateStyleBody(id, style);
    }

    public String getOwnerLogin() {
        return owner;
    }

    public UserRecord getOwner() throws SQLException {
        return session.readUser(owner);
    }

    public void setOwner(final UserRecord owner) throws SQLException {
        this.owner = owner.getLogin();
        session.updateStyle(id, name, provider, type, owner.getLogin());
    }

    public List<DataRecord> getLinkedData() throws SQLException {
        return session.readData(this);
    }

    public void linkToData(final DataRecord data) throws SQLException {
        session.writeStyledData(this, data);
    }
}