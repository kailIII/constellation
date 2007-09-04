/*
 * Sicade - Syst�mes int�gr�s de connaissances pour l'aide � la d�cision en environnement
 * (C) 2005, Institut de Recherche pour le D�veloppement
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
import net.sicade.catalog.CatalogException;
import net.sicade.catalog.Database;
import net.sicade.catalog.SingletonTable;
import net.sicade.observation.Point;
import net.sicade.observation.Position;
import net.sicade.observation.SamplingPointEntry;
import org.opengis.observation.sampling.SamplingPoint;

/**
 *SamplingPointTable.java
 *
 * @author Guilhem Legal
 */
public class SamplingPointTable extends SingletonTable<SamplingPoint> {
    
    /** Creates a new instance of SamplingPointTable */
    public SamplingPointTable(final Database database) {
        super(new SamplingPointQuery(database));
    }
    
    /**
     * Construit une station pour l'enregistrement courant. L'impl�mentation par d�faut extrait une
     * premi�re s�rie d'informations telles que le {@linkplain Station#getName nom de la station},
     * {@linkplain Station#getProvider son fournisseur}, <cite>etc.</cite> et appele la m�thode
     * <code>{@linkplain #createEntry(int,String,Platform,DataQuality,Citation,ResultSet)
     * createEntry}(name, identifier, ...)</code> avec ces informations.
     */
    protected SamplingPoint createEntry(final ResultSet result) throws CatalogException, SQLException {
        final SamplingPointQuery query = (SamplingPointQuery) super.query;
        
        Point p = new Point(result.getString(indexOf(query.pointIdentifier)),
                            new Position(result.getString(indexOf(query.srsName)),
                                         result.getInt(indexOf(query.srsDimension)),
                                         result.getInt(indexOf(query.positionValue))));
                            
        return new SamplingPointEntry( result.getString(indexOf(query.identifier)),
                                       result.getString(indexOf(query.name)),
                                       result.getString(indexOf(query.description)),
                                       result.getString(indexOf(query.sampledFeature)),
                                       p);
        
    }
    
}
