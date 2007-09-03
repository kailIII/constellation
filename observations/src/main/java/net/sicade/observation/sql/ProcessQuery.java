/* Created on 3 septembre 2007, 11:54 */

package net.sicade.observation.sql;

import net.sicade.catalog.Column;
import net.sicade.catalog.Database;
import net.sicade.catalog.Parameter;
import net.sicade.catalog.Query;
import net.sicade.catalog.QueryType;
import static net.sicade.catalog.QueryType.*;

/**
 * The query to execute for a {@link ProcessTable}.
 *
 * @author Guilhem Legal
 */
public class ProcessQuery extends Query{
    
    /**
     * Column to appear after the {@code "SELECT"} clause.
     */
    protected final Column name, remarks;
    
    /**
     * Parameter to appear after the {@code "FROM"} clause.
     */
    protected final Parameter byName;
    
    /**
     * Creates a new query for the specified database.
     *
     * @param database The database for which this query is created.
     */
    public ProcessQuery(final Database database) {
        super(database);
        final QueryType[] usage = {SELECT, LIST};
        name    = addColumn   ("Process", "name",        usage);
        remarks = addColumn   ("Process", "description", usage);
        byName  = addParameter(name, SELECT);
        name.setOrdering("ASC");
    }
    
}
