/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 * Copyright 2014 Geomatys.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.constellation.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.sis.util.Static;
import org.apache.sis.util.logging.Logging;
import org.constellation.admin.util.SQLExecuter;
import org.constellation.configuration.ConfigDirectory;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.util.sql.DerbySqlScriptRunner;

/**
 * Constellation embedded administration database class.
 *
 * @author Fabien Bernard (Geomatys).
 * @version 0.9
 * @since 0.9
 */
public final class EmbeddedDatabase extends Static {

    /**
     * Logger used for debugging and event notification.
     */
    private static final Logger LOGGER = Logging.getLogger(EmbeddedDatabase.class);

    /**
     * Administration database configuration keys.
     */
    private static final String CONFIG_KEY_URL = "admin-db-url";

    /**
     * Administration database {@link DefaultDataSource} instance.
     */
    private static DefaultDataSource DATA_SOURCE;

    
    /**
     * Exposes dataSource (Spring artifact).
     * @return
     * @throws SQLException
     */
    public static DataSource getOrCreateDataSource() throws SQLException {
        if (DATA_SOURCE == null) {
            synchronized(EmbeddedDatabase.class) {
                if (DATA_SOURCE == null) {
                    setup();
                }
            }
        }
        return DATA_SOURCE;
    }

    public static SQLExecuter createSQLExecuter() throws SQLException {
        getOrCreateDataSource();
        return new SQLExecuter(DATA_SOURCE.getConnection());
    }

   

    /**
     * Sets static connection variables and check if the administration schema named
     * {@code "CstlAdmin"} exists on the current {@link DataSource}.
     * <p />
     * If the schema is missing create it executing the {@code admin-db.sql} resource file.
     *
     * @throws SQLException if an error occurred while connecting to database or executing a SQL statement
     */
    private static void setup() throws SQLException {
        // Force loading driver because some container like tomcat 7.0.21+ disable drivers at startup.
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
        } catch (ClassNotFoundException ex) {
            LOGGER.log(Level.WARNING, ex.getMessage(), ex);
        }

        /*
         * Browses Constellation configuration folder to obtain the admin database
         * configuration. The configuration is stored into the admin-db.properties
         * file from .constellation/admin folder. If this file is missing a default
         * configuration file will be created during Constellation startup.
         */
        final Properties config = new Properties();
        try {
            final File configDirectory = ConfigDirectory.getAdminConfigDirectory();
            final File configFile      = new File(configDirectory, "admin-db.properties");

            // Read or create default configuration.
            if (configFile.exists()) {
                config.load(new FileInputStream(configFile));
            } else {
                config.setProperty(CONFIG_KEY_URL, "jdbc:derby:" + configDirectory.getPath() + "/admin-db");
                config.store(new FileOutputStream(configFile), "Auto-generated at first Constellation startup.");
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Unexpected exception while reading/writing administration database property file.", ex);
        }

        // Initialize data source.
        final String dbUrl = config.getProperty(CONFIG_KEY_URL);
        if (dbUrl == null) {
            throw new IllegalStateException("Embedded database configuration property \"" + CONFIG_KEY_URL + "\" is missing.");
        }
        DATA_SOURCE = new DefaultDataSource(dbUrl.replace('\\','/') + ";create=true;");

        // Establish connection and create schema if does not exist.
        Connection conec = null;
        try {
            conec = DATA_SOURCE.getConnection();
            if (false && !schemaExists("admin", conec)) {
                // Load database schema SQL stream.
                final ClassLoader loader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                    public ClassLoader run() {
                        return Thread.currentThread().getContextClassLoader();
                    }
                });
                final InputStream stream = loader.getResourceAsStream("org/constellation/sql/v1/create-admin-db.sql");

                // Create schema.
                runSql(stream, conec);

                // Create default admin user.
            }
        } catch (IOException unexpected) {
            throw new IllegalStateException("Unexpected error occurred while trying to create admin database schema.", unexpected);
        } finally {
            if (conec != null) {
                conec.close();
            }
        }
    }
    
    /**
     * Runs an {@code .sql} script file.
     *
     * @param stream the sql stream
     * @throws IOException if an error occurred while reading the input
     * @throws SQLException if an error occurred while executing a SQL statement
     */
    private static void runSql(final InputStream stream, final Connection connect) throws IOException, SQLException {
        new DerbySqlScriptRunner(connect).run(stream);
    }
    
    /**
     * Checks if a schema exists on a database.
     *
     * @param schemaName the schema name to find
     * @return {@code true} if the schema exists, otherwise {@code false}
     * @throws SQLException if an error occurred while executing a SQL statement
     */
    private static boolean schemaExists(final String schemaName, final Connection connect) throws SQLException {
        final ResultSet schemas = connect.getMetaData().getSchemas();
        while (schemas.next()) {
            if (schemaName.equals(schemas.getString(1))) {
                return true;
            }
        }
        return false;
    }

    public static void clear() {
        if (DATA_SOURCE != null) {
            DATA_SOURCE.shutdown();
        }
        DATA_SOURCE = null;
    }
}
