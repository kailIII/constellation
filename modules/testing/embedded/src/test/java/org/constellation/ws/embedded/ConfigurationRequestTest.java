/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2011, Geomatys
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
package org.constellation.ws.embedded;

// JUnit dependencies
import org.constellation.configuration.ExceptionReport;
import javax.xml.bind.Marshaller;
import java.io.File;
import org.geotoolkit.csw.xml.v202.RecordType;
import org.geotoolkit.util.StringUtilities;
import org.geotoolkit.xml.MarshallerPool;
import java.net.URLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.constellation.configuration.AcknowlegementType;
import org.constellation.configuration.ConfigDirectory;
import org.constellation.configuration.ServiceReport;
import org.constellation.sos.ws.soap.SOService;
import org.constellation.test.utils.Order;
import org.constellation.test.utils.TestRunner;
import org.constellation.ws.ExceptionCode;
import org.geotoolkit.csw.xml.v202.GetRecordsResponseType;
import org.geotoolkit.dublincore.xml.v2.elements.SimpleLiteral;
import org.junit.*;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;

/**
 *
 * @author Guilhem Legal (Geomatys)
 */
@RunWith(TestRunner.class)
public class ConfigurationRequestTest extends AbstractGrizzlyServer {

    @BeforeClass
    public static void initPool() throws Exception {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("sos", new SOService());
        initServer(null, map);
        // Get the list of layers
        pool = new MarshallerPool("org.constellation.configuration:"
                                + "org.constellation.generic.database:"
                                + "org.geotoolkit.ows.xml.v110:"
                                + "org.geotoolkit.csw.xml.v202:"
                                + "org.geotoolkit.internal.jaxb.geometry:"
                                + "org.geotoolkit.ows.xml.v100");
    }

    @AfterClass
    public static void shutDown() {
        File f = new File("derby.log");
        if (f.exists()) {
            f.delete();
        }
        //finish();
    }

    private static String getConfigurationURL() {
        return "http://localhost:" +  grizzly.getCurrentPort() + "/configuration?";
    }

    private static String getCswURL() {
        return "http://localhost:" +  grizzly.getCurrentPort() + "/csw/default?";
    }

    @Test
    @Order(order=1)
    public void testRestart() throws Exception {

        waitForStart();
        //update the federated catalog in case of busy port
        URL fedCatURL = new URL("http://localhost:" +  grizzly.getCurrentPort() + "/csw/admin?request=setFederatedCatalog&id=csw2&servers=" + getCswURL());
        URLConnection conec = fedCatURL.openConnection();

        Object obj = unmarshallResponse(conec);

        assertTrue(obj instanceof AcknowlegementType);


        URL niUrl = new URL(getConfigurationURL() + "request=restart");

        // for a POST request
        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof AcknowlegementType);
        AcknowlegementType expResult = new AcknowlegementType("Success",  "services succefully restarted");
        assertEquals(expResult, obj);
    }

    @Test
    @Order(order=2)
    public void testDownloadFile() throws Exception {

        URL niUrl = new URL(getConfigurationURL() + "request=download");


        // for a POST request
        URLConnection conec = niUrl.openConnection();

        Object obj = unmarshallResponse(conec);

        assertTrue(obj instanceof ExceptionReport);
        ExceptionReport expResult = new ExceptionReport("Download operation not implemented",
                                                         StringUtilities.transformCodeName(ExceptionCode.OPERATION_NOT_SUPPORTED.name()));
        assertEquals(expResult, obj);
    }

    @Test
    @Order(order=3)
    public void testCSWRefreshIndex() throws Exception {

        /*
         * try to get a missing parameter error
         */
        URL niUrl = new URL(getConfigurationURL() + "request=refreshIndex");

        URLConnection conec = niUrl.openConnection();

        Object obj = unmarshallResponse(conec);

        assertTrue(obj instanceof ExceptionReport);
        ExceptionReport exception = new ExceptionReport("The parameter ID must be specified",
                                                         StringUtilities.transformCodeName(ExceptionCode.MISSING_PARAMETER_VALUE.name()));
        assertEquals(exception, obj);


        // first we make a getRecords request to count the number of record
        niUrl = new URL(getCswURL() + "request=getRecords&version=2.0.2&service=CSW&typenames=csw:Record");

        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof GetRecordsResponseType);
        GetRecordsResponseType response = (GetRecordsResponseType) obj;

        assertEquals(12, response.getSearchResults().getNumberOfRecordsMatched());

        // build 2 new metadata file
        RecordType record = new RecordType();
        record.setIdentifier(new SimpleLiteral("urn_test00"));
        File f = new File(ConfigDirectory.getConfigDirectory(), "CSW/default/data/urn_test00.xml");
        RecordType record2 = new RecordType();
        record2.setIdentifier(new SimpleLiteral("urn_test01"));
        File f2 = new File(ConfigDirectory.getConfigDirectory(), "CSW/default/data/urn_test01.xml");


        Marshaller m = pool.acquireMarshaller();
        m.marshal(record, f);
        m.marshal(record2, f2);
        pool.release(m);


        niUrl = new URL(getConfigurationURL() + "request=refreshIndex&id=default");

        // for a POST request
        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof AcknowlegementType);
        AcknowlegementType expResult = new AcknowlegementType("Success",  "CSW index succefully recreated");
        assertEquals(expResult, obj);

        niUrl = new URL(getCswURL() + "request=getRecords&version=2.0.2&service=CSW&typenames=csw:Record");

        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof GetRecordsResponseType);
        response = (GetRecordsResponseType) obj;

        assertEquals(14, response.getSearchResults().getNumberOfRecordsMatched());
        
        // remove data
        f.delete();
        f2.delete();
        
        niUrl = new URL(getConfigurationURL() + "request=refreshIndex&id=default");

        // for a POST request
        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof AcknowlegementType);
        expResult = new AcknowlegementType("Success",  "CSW index succefully recreated");
        assertEquals(expResult, obj);

        niUrl = new URL(getCswURL() + "request=getRecords&version=2.0.2&service=CSW&typenames=csw:Record");

        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof GetRecordsResponseType);
        response = (GetRecordsResponseType) obj;

        assertEquals(12, response.getSearchResults().getNumberOfRecordsMatched());
    }

    @Test
    @Order(order=4)
    public void testCSWAddToIndex() throws Exception {

        // first we make a getRecords request to count the number of record
        URL niUrl = new URL(getCswURL() + "request=getRecords&version=2.0.2&service=CSW&typenames=csw:Record");

        URLConnection conec = niUrl.openConnection();

        Object obj = unmarshallResponse(conec);

        assertTrue(obj instanceof GetRecordsResponseType);
        GetRecordsResponseType response = (GetRecordsResponseType) obj;

        assertEquals(12, response.getSearchResults().getNumberOfRecordsMatched());

        // build a new metadata file
        RecordType record = new RecordType();
        record.setIdentifier(new SimpleLiteral("urn_test"));
        File f = new File(ConfigDirectory.getConfigDirectory(), "CSW/default/data/urn_test.xml");

        Marshaller m = pool.acquireMarshaller();
        m.marshal(record, f);
        pool.release(m);

        // add a metadata to the index
        niUrl = new URL(getConfigurationURL() + "request=addToIndex&id=default&identifiers=urn_test");

        // for a POST request
        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof AcknowlegementType);
        AcknowlegementType expResult = new AcknowlegementType("Success",  "The specified record have been added to the CSW index");
        assertEquals(expResult, obj);


        //clear the csw cache
        niUrl = new URL("http://localhost:" + grizzly.getCurrentPort() + "/csw/admin?request=clearCache&id=default");
        conec = niUrl.openConnection();
        obj = unmarshallResponse(conec);


         // verify that the number of record have increased
        niUrl = new URL(getCswURL() + "request=getRecords&version=2.0.2&service=CSW&typenames=csw:Record");

        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof GetRecordsResponseType);
        response = (GetRecordsResponseType) obj;

        assertEquals(13, response.getSearchResults().getNumberOfRecordsMatched());
        
        // restore previous context
        f.delete();
        niUrl = new URL(getConfigurationURL() + "request=refreshIndex&id=default");

        // for a POST request
        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof AcknowlegementType);
        expResult = new AcknowlegementType("Success",  "CSW index succefully recreated");
        assertEquals(expResult, obj);

        niUrl = new URL(getCswURL() + "request=getRecords&version=2.0.2&service=CSW&typenames=csw:Record");

        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof GetRecordsResponseType);
        response = (GetRecordsResponseType) obj;

        assertEquals(12, response.getSearchResults().getNumberOfRecordsMatched());
    }
    
    @Test
    @Order(order=5)
    public void testCSWRemoveFromIndex() throws Exception {

        // first we make a getRecords request to count the number of record
        URL niUrl = new URL(getCswURL() + "request=getRecords&version=2.0.2&service=CSW&typenames=csw:Record");

        URLConnection conec = niUrl.openConnection();

        Object obj = unmarshallResponse(conec);

        assertTrue(obj instanceof GetRecordsResponseType);
        GetRecordsResponseType response = (GetRecordsResponseType) obj;

        assertEquals(12, response.getSearchResults().getNumberOfRecordsMatched());

        // remove metadata from the index
        niUrl = new URL(getConfigurationURL() + "request=removeFromIndex&id=default&identifiers=urn:uuid:19887a8a-f6b0-4a63-ae56-7fba0e17801f");

        // for a POST request
        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof AcknowlegementType);
        AcknowlegementType expResult = new AcknowlegementType("Success",  "The specified record have been remove from the CSW index");
        assertEquals(expResult, obj);


        //clear the csw cache
        niUrl = new URL("http://localhost:" + grizzly.getCurrentPort() + "/csw/admin?request=clearCache&id=default");
        conec = niUrl.openConnection();
        obj = unmarshallResponse(conec);


         // verify that the number of record have increased
        niUrl = new URL(getCswURL() + "request=getRecords&version=2.0.2&service=CSW&typenames=csw:Record");

        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof GetRecordsResponseType);
        response = (GetRecordsResponseType) obj;

        assertEquals(11, response.getSearchResults().getNumberOfRecordsMatched());
        
        // restore previous context
        niUrl = new URL(getConfigurationURL() + "request=refreshIndex&id=default");

        // for a POST request
        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof AcknowlegementType);
        expResult = new AcknowlegementType("Success",  "CSW index succefully recreated");
        assertEquals(expResult, obj);

        niUrl = new URL(getCswURL() + "request=getRecords&version=2.0.2&service=CSW&typenames=csw:Record");

        conec = niUrl.openConnection();

        obj = unmarshallResponse(conec);

        assertTrue(obj instanceof GetRecordsResponseType);
        response = (GetRecordsResponseType) obj;

        assertEquals(12, response.getSearchResults().getNumberOfRecordsMatched());
    }

    @Test
    @Order(order=6)
    public void testListAvailableService() throws Exception {
        URL niUrl = new URL(getConfigurationURL() + "request=ListAvailableService");


        // for a POST request
        URLConnection conec = niUrl.openConnection();

        Object obj = unmarshallResponse(conec);

        assertTrue(obj instanceof ServiceReport);
        final ServiceReport result = (ServiceReport) obj;
        //assertTrue(result.getAvailableServices().containsKey("WMS"));
        assertTrue(result.getAvailableServices().containsKey("SOS"));
        assertTrue(result.getAvailableServices().containsKey("CSW"));
        //assertTrue(result.getAvailableServices().containsKey("WCS"));
        //assertTrue(result.getAvailableServices().containsKey("WFS"));
        //assertTrue(result.getAvailableServices().containsKey("WPS"));

        assertEquals(result.getAvailableServices().toString(), 2, result.getAvailableServices().size());


    }
}
