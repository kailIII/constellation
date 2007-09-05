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
package net.sicade.observation.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import net.sicade.catalog.Database;
import net.sicade.catalog.SingletonTable;
import net.sicade.observation.MeasureEntry;
import org.opengis.observation.Measure;

/**
 *
 * @version $Id:
 * @author Guilhem Legal
 */
public class MeasureTable extends SingletonTable<Measure> {
    
    /**
     * Construit une table des resultats de mesure.
     * 
     * @param  database Connexion vers la base de données.
     */
    public MeasureTable(final Database database) {
         super(new MeasureQuery(database));
    }

    /**
     * Construit un resultat de mesure pour l'enregistrement courant.
     */
    protected Measure createEntry(final ResultSet results) throws SQLException {
        final MeasureQuery query = (MeasureQuery) super.query;
        return new MeasureEntry(results.getString(indexOf(query.name   )),
                                   results.getString(indexOf(query.uom)),
                                   results.getFloat(indexOf(query.value)));
    }
    
}
