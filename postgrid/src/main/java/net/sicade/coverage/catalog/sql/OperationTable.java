/*
 * Sicade - Systèmes intégrés de connaissances pour l'aide à la décision en environnement
 * (C) 2005, Institut de Recherche pour le Développement
 * (C) 2007, Geomatys
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
package net.sicade.coverage.catalog.sql;

import java.sql.SQLException;
import java.sql.ResultSet;
import org.opengis.parameter.ParameterValueGroup;

import net.sicade.coverage.catalog.CatalogException;
import net.sicade.coverage.catalog.Operation;
import net.sicade.catalog.Database;
import net.sicade.catalog.SingletonTable;


/**
 * Connection to the table of image {@linkplain Operation operations}.
 * 
 * @version $Id$
 * @author Antoine Hnawia
 * @author Martin Desruisseaux
 */
public class OperationTable extends SingletonTable<Operation> {
    /** 
     * La table des paramètres des opérations. Ne sera construite que la première fois
     * où elle sera nécessaire.
     */
    private OperationParameterTable parameters;

    /** 
     * Creates an operation table.
     * 
     * @param database Connection to the database.
     */
    public OperationTable(final Database database) {
        super(new OperationQuery(database));
        setIdentifierParameters(((OperationQuery) query).byName, null);
    }

    /**
     * Creates an operation for the current row in the specified result set.
     *
     * @param  results The result set to read.
     * @return The entry for current row in the specified result set.
     * @throws CatalogException if an inconsistent record is found in the database.
     * @throws SQLException if an error occured while reading the database.
     */
    protected Operation createEntry(final ResultSet results) throws SQLException, CatalogException {
        final OperationQuery query = (OperationQuery) super.query;
        final String name      = results.getString(indexOf(query.name     ));
        final String prefix    = results.getString(indexOf(query.prefix   ));
        final String operation = results.getString(indexOf(query.operation));
        final String remarks   = results.getString(indexOf(query.remarks  ));
        final OperationEntry entry = new OperationEntry(name, prefix, operation, remarks);
        final ParameterValueGroup values = entry.getParameters();
        if (values != null) {
            if (parameters == null) {
                parameters = getDatabase().getTable(OperationParameterTable.class);
            }
            parameters.fillValues(name, values);
        }
        return entry;
    }
}
