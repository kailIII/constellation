/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2010, Geomatys
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

package org.constellation.sos.ws;

// JUnit dependencies
import java.io.File;
import java.sql.Connection;
import java.util.logging.Level;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import org.constellation.configuration.DataSourceType;
import org.constellation.configuration.SOSConfiguration;
import org.constellation.generic.database.Automatic;
import org.constellation.generic.database.GenericDatabaseMarshallerPool;
import org.constellation.generic.database.Query;
import org.constellation.test.utils.Order;
import org.constellation.test.utils.TestRunner;
import org.constellation.util.Util;
import org.geotoolkit.internal.sql.DefaultDataSource;
import org.geotoolkit.util.FileUtilities;
import org.geotoolkit.util.sql.DerbySqlScriptRunner;
import org.apache.sis.xml.MarshallerPool;

import org.junit.*;
import org.junit.runner.RunWith;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@RunWith(TestRunner.class)
public class GenericPostgridSOSWorkerTest extends SOSWorkerTest {

    private static DefaultDataSource ds = null;

    private static File workingDirectory;
    
    @BeforeClass
    public static void setUpClass() throws Exception {

        final String url = "jdbc:derby:memory:GPGTest1;create=true";
        ds = new DefaultDataSource(url);

        Connection con = ds.getConnection();

        DerbySqlScriptRunner sr = new DerbySqlScriptRunner(con);
        sr.run(Util.getResourceAsStream("org/constellation/observation/structure_observations.sql"));
        sr.run(Util.getResourceAsStream("org/constellation/sql/sos-data.sql"));


        MarshallerPool pool   = GenericDatabaseMarshallerPool.getInstance();
        Marshaller marshaller =  pool.acquireMarshaller();




        final File configDir = new File("GPGSOSWorkerTest");
        if (configDir.exists()) {
            FileUtilities.deleteDirectory(configDir);
        }

        if (!configDir.exists()) {
            configDir.mkdir();

            Unmarshaller unmarshaller = pool.acquireUnmarshaller();

            //we write the configuration file
            File filterFile = new File(configDir, "affinage.xml");
            Query query = (Query) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/affinage.xml"));
            marshaller.marshal(query, filterFile);

            //we write the configuration file
            File configFile = new File(configDir, "config.xml");
            Automatic SMLConfiguration = new Automatic();
            SMLConfiguration.setFormat("nosml");

            Automatic OMConfiguration = (Automatic) unmarshaller.unmarshal(Util.getResourceAsStream("org/constellation/sos/generic-config.xml"));
            pool.recycle(unmarshaller);

            OMConfiguration.getBdd().setConnectURL(url);


            SOSConfiguration configuration = new SOSConfiguration(SMLConfiguration, OMConfiguration);
            configuration.setObservationReaderType(DataSourceType.GENERIC);
            configuration.setObservationWriterType(DataSourceType.NONE);
            configuration.setSMLType(DataSourceType.NONE);
            configuration.setObservationFilterType(DataSourceType.GENERIC);
            configuration.setPhenomenonIdBase("urn:ogc:def:phenomenon:GEOM:");
            configuration.setProfile("discovery");
            configuration.setObservationTemplateIdBase("urn:ogc:object:observation:template:GEOM:");
            configuration.setObservationIdBase("urn:ogc:object:observation:GEOM:");
            configuration.setSensorIdBase("urn:ogc:object:sensor:GEOM:");
            configuration.getParameters().put("transactionSecurized", "false");
            marshaller.marshal(configuration, configFile);

        }
        pool.recycle(marshaller);
        init();
        worker = new SOSworker("", configDir);
        worker.setServiceUrl(URL);
        worker.setLogLevel(Level.FINER);
        workingDirectory = configDir;
    }
    
    @Override
    public void initWorker() {
        worker = new SOSworker("", workingDirectory);
        worker.setServiceUrl(URL);
        worker.setLogLevel(Level.FINER);
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        if (worker != null) {
            worker.destroy();
        }

        FileUtilities.deleteDirectory(new File("GPGSOSWorkerTest"));
        File derbyLog = new File("derby.log");
        if (derbyLog.exists()) {
            derbyLog.delete();
        }
        File mappingFile = new File("mapping.properties");
        if (mappingFile.exists()) {
            mappingFile.delete();
        }
        if (ds != null) {
            ds.shutdown();
        }
    }



    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }


    /**
     * Tests the getcapabilities method
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    @Order(order=1)
    public void getCapabilitiesErrorTest() throws Exception {
        super.getCapabilitiesErrorTest();

    }


    /**
     * Tests the getcapabilities method
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    @Order(order=2)
    public void getCapabilitiesTest() throws Exception {
        super.getCapabilitiesTest();

    }


    /**
     * Tests the GetObservation method
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    @Order(order=3)
    public void GetObservationErrorTest() throws Exception {
        super.GetObservationErrorTest();
    }

    /**
     * Tests the GetObservation method
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    @Order(order=4)
    public void GetObservationTest() throws Exception {
        super.GetObservationTest();
    }

    /**
     * Tests the GetObservation method
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    @Order(order=5)
    public void GetObservationSamplingCurveTest() throws Exception {
        super.GetObservationSamplingCurveTest();
    }

    /**
     * Tests the GetObservationById method
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    @Order(order=6)
    public void GetObservationByIdTest() throws Exception {
        super.GetObservationByIdTest();
    }
    
    /**
     * Tests the GetResult method
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    @Order(order=7)
    public void GetResultErrorTest() throws Exception {
        super.GetResultErrorTest();
    }

    /**
     * Tests the GetResult method
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    @Order(order=8)
    public void GetResultTest() throws Exception {
        super.GetResultTest();
    }

    /**
     * Tests the GetFeatureOfInterest method
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    @Order(order=9)
    public void GetFeatureOfInterestErrorTest() throws Exception {
        super.GetFeatureOfInterestErrorTest();
    }

    /**
     * Tests the GetFeatureOfInterest method
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    @Order(order=10)
    public void GetFeatureOfInterestTest() throws Exception {
        super.GetFeatureOfInterestTest();
    }


    /**
     * Tests the destroy method
     *
     * @throws java.lang.Exception
     */
    @Test
    @Override
    @Order(order=11)
    public void destroyTest() throws Exception {
        super.destroyTest();
    }
}
