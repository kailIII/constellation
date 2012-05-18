/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2009-2010, Geomatys
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

// J2SE dependencies
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

// Constellation dependencies
import org.constellation.test.ImageTesting;
import org.constellation.provider.LayerProviderProxy;
import org.constellation.provider.configuration.Configurator;

import static org.constellation.provider.coveragesql.CoverageSQLProviderService.*;
import static org.constellation.provider.configuration.ProviderParameters.*;

// Geotoolkit dependencies
import org.geotoolkit.ows.xml.v110.ExceptionReport;
import org.geotoolkit.feature.DefaultName;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.wcs.xml.WCSMarshallerPool;
import org.geotoolkit.wcs.xml.v100.CoverageDescription;
import org.geotoolkit.wcs.xml.v100.CoverageOfferingBriefType;
import org.geotoolkit.wcs.xml.v100.CoverageOfferingType;
import org.geotoolkit.wcs.xml.v100.LonLatEnvelopeType;
import org.geotoolkit.wcs.xml.v100.WCSCapabilitiesType;
import org.geotoolkit.ogc.xml.exception.ServiceExceptionReport;
import org.geotoolkit.wcs.xml.v100.DCPTypeType.HTTP.Get;

// JUnit dependencies
import org.junit.*;
import static org.junit.Assert.*;
import static org.junit.Assume.*;

import org.opengis.parameter.ParameterDescriptorGroup;
import org.opengis.parameter.ParameterValueGroup;

/**
 * A set of methods that request a Grizzly server which embeds a WCS service.
 *
 * @version $Id$
 *
 * @author Cédric Briançon (Geomatys)
 * @since 0.3
 */
public class WCSRequestsTest extends AbstractTestRequest {

    /**
     * The layer to test.
     */
    private static final DefaultName LAYER_TEST = new DefaultName("SST_tests");
    
    /**
     * URLs which will be tested on the server.
     */
    private static final String WCS_URL  ="http://localhost:9090/wcs/default?SERVICE=WCS&";
    private static final String WCS_URL2 ="http://localhost:9090/wcs/test?SERVICE=WCS&";
    
    private static final String WCS_FALSE_REQUEST =
            "http://localhost:9090/wcs/default?request=SomethingElse";

    private static final String WCS_FALSE_REQUEST_100 =
            "http://localhost:9090/wcs/default?request=GetCoverage&service=WCS&version=1.0.0&" +
                                      "format=image/png&width=1024&height=512&" +
                                      "crs=EPSG:4326&bbox=-180,-90,180,90&" +
                                      "coverage=wrongLayer";

    private static final String WCS_FALSE_REQUEST_111 =
            "http://localhost:9090/wcs/default?request=GetCoverage&service=WCS&version=1.1.1&" +
                                      "format=image/png&width=1024&height=512&" +
                                      "crs=EPSG:4326&boundingbox=-180,-90,180,90,EPSG4326&" +
                                      "identifier=wrongLayer";

    private static final String WCS_GETCOVERAGE =
            "http://localhost:9090/wcs/default?request=GetCoverage&service=WCS&version=1.0.0&" +
                                      "format=image/png&width=1024&height=512&" +
                                      "crs=EPSG:4326&bbox=-180,-90,180,90&" +
                                      "coverage="+ LAYER_TEST;

    private static final String WCS_GETCOVERAGE_MATRIX =
            "http://localhost:9090/wcs/default?request=GetCoverage&service=WCS&version=1.0.0&" +
                                      "format=matrix&width=1024&height=512&" +
                                      "crs=EPSG:4326&bbox=-180,-90,180,90&" +
                                      "coverage="+ LAYER_TEST;

    private static final String WCS_GETCAPABILITIES =
            "http://localhost:9090/wcs/default?request=GetCapabilities&service=WCS&version=1.0.0";
    
    private static final String WCS_GETCAPABILITIES2 =
            "http://localhost:9090/wcs/test?request=GetCapabilities&service=WCS&version=1.0.0";

    private static final String WCS_DESCRIBECOVERAGE =
            "http://localhost:9090/wcs/default?request=DescribeCoverage&coverage=SST_tests&service=wcs&version=1.0.0";

    /**
     * Initialize the list of layers from the defined providers in Constellation's configuration.
     */
    @BeforeClass
    public static void initLayerList() throws JAXBException {
        pool = WCSMarshallerPool.getInstance();
        
        final Configurator config = new Configurator() {
            @Override
            public ParameterValueGroup getConfiguration(String serviceName, ParameterDescriptorGroup desc) {

                final ParameterValueGroup config = desc.createValue();
                
                if("coverage-sql".equals(serviceName)){
                    // Defines a PostGrid data provider
                    final ParameterValueGroup source = config.addGroup(SOURCE_DESCRIPTOR_NAME);
                    final ParameterValueGroup srcconfig = getOrCreate(COVERAGESQL_DESCRIPTOR,source);
                    srcconfig.parameter(URL_DESCRIPTOR.getName().getCode()).setValue("jdbc:postgresql://db.geomatys.com/coverages-test");
                    srcconfig.parameter(PASSWORD_DESCRIPTOR.getName().getCode()).setValue("test");
                    final String rootDir = System.getProperty("java.io.tmpdir") + "/Constellation/images";
                    srcconfig.parameter(ROOT_DIRECTORY_DESCRIPTOR.getName().getCode()).setValue(rootDir);
                    srcconfig.parameter(USER_DESCRIPTOR.getName().getCode()).setValue("test");
                    srcconfig.parameter(SCHEMA_DESCRIPTOR.getName().getCode()).setValue("coverages");
                    srcconfig.parameter(NAMESPACE_DESCRIPTOR.getName().getCode()).setValue("no namespace");
                    source.parameter(SOURCE_LOADALL_DESCRIPTOR.getName().getCode()).setValue(Boolean.TRUE);
                    source.parameter(SOURCE_ID_DESCRIPTOR.getName().getCode()).setValue("coverageTestSrc");

                }

                //empty configuration for others
                return config;
            }

            @Override
            public void saveConfiguration(String serviceName, ParameterValueGroup params) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        LayerProviderProxy.getInstance().setConfigurator(config);
        
        WorldFileImageReader.Spi.registerDefaults(null);
        
        //reset values, only allow pure java readers
        for(String jn : ImageIO.getReaderFormatNames()){
            Registry.setNativeCodecAllowed(jn, ImageReaderSpi.class, false);
        }

        //reset values, only allow pure java writers
        for(String jn : ImageIO.getWriterFormatNames()){
            Registry.setNativeCodecAllowed(jn, ImageWriterSpi.class, false);
        }
    }

    /**
     * Ensure that a wrong value given in the request parameter for the WCS server
     * returned an error report for the user.
     */
    @Test
    public void testWCSWrongRequest() throws Exception {
        waitForStart();
        
        // Creates an intentional wrong url, regarding the WCS version 1.0.0 standard
        URL wrongUrl;
        try {
            wrongUrl = new URL(WCS_FALSE_REQUEST);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to marshall something from the response returned by the server.
        // The response should be a ServiceExceptionReport.
        Object obj = unmarshallResponse(wrongUrl);
        assertTrue(obj instanceof ServiceExceptionReport);

        try {
            wrongUrl = new URL(WCS_FALSE_REQUEST_100);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to marshall something from the response returned by the server.
        // The response should be a ServiceExceptionReport.
        obj = unmarshallResponse(wrongUrl);
        assertTrue(obj instanceof ServiceExceptionReport);

        try {
            wrongUrl = new URL(WCS_FALSE_REQUEST_111);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to marshall something from the response returned by the server.
        // The response should be a OWS ExceptionReport.
        obj = unmarshallResponse(wrongUrl);
        assertTrue("exception type:" + obj.getClass().getName(), obj instanceof ExceptionReport);
    }

    /**
     * Ensures that a valid GetCoverage request returns indeed a {@link BufferedImage}.
     */
    @Test
    public void testWCSGetCoverage() throws IOException {

        // Creates a valid GetCoverage url.
        final URL getCoverageUrl;
        try {
            getCoverageUrl = new URL(WCS_GETCOVERAGE);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to get the coverage from the url.
        final BufferedImage image = getImageFromURL(getCoverageUrl, "image/png");

        // Test on the returned image.
        assertFalse (ImageTesting.isImageEmpty(image));
        assertEquals(1024, image.getWidth());
        assertEquals(512,  image.getHeight());
        assertTrue  (ImageTesting.getNumColors(image) > 8);
    }

    /**
     * Ensures a GetCoverage request with the output format matrix works fine.
     *
     * For now, this format is not well handled by the current Geotools. There are some
     * errors in the reading of this format, and they will be corrected in the next version
     * of Geotools.
     *
     * @TODO: do this test when moving of Geotools' version
     */
    @Ignore
    public void testWCSGetCoverageMatrixFormat() throws IOException {

        // Creates a valid GetCoverage url.
        final URL getCovMatrixUrl;
        try {
            getCovMatrixUrl = new URL(WCS_GETCOVERAGE_MATRIX);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        final BufferedImage image = getImageFromURL(getCovMatrixUrl, "application/matrix");
        //assertEquals(Commons.checksum(image), ...);
    }

    /**
     * Ensures that a valid GetCapabilities request returns indeed a valid GetCapabilities
     * document representing the server capabilities in the WCS version 1.0.0 standard.
     */
    @Test
    public void testWCSGetCapabilities() throws JAXBException, IOException {

        // Creates a valid GetCapabilities url.
        URL getCapsUrl;
        try {
            getCapsUrl = new URL(WCS_GETCAPABILITIES);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to marshall something from the response returned by the server.
        // The response should be a WCSCapabilitiesType.
        Object obj = unmarshallResponse(getCapsUrl);
        assertTrue(obj instanceof WCSCapabilitiesType);

        WCSCapabilitiesType responseCaps = (WCSCapabilitiesType)obj;
        final List<CoverageOfferingBriefType> coverages = responseCaps.getContentMetadata().getCoverageOfferingBrief();

        assertNotNull(coverages);
        assertFalse(coverages.isEmpty());
        boolean layerTestFound = false;
        for (CoverageOfferingBriefType coverage : coverages) {
            for (JAXBElement<String> elem : coverage.getRest()) {
                if (elem.getValue().equals(LAYER_TEST.getLocalPart())) {
                    layerTestFound = true;
                    final LonLatEnvelopeType env = coverage.getLonLatEnvelope();
                    assertTrue(env.getPos().get(0).getValue().get(0) == -180d);
                    assertTrue(env.getPos().get(0).getValue().get(1) ==  -90d);
                    assertTrue(env.getPos().get(1).getValue().get(0) ==  180d);
                    assertTrue(env.getPos().get(1).getValue().get(1) ==   90d);
                }
            }
        }
        if (layerTestFound == false) {
            throw new AssertionError("The layer \""+ LAYER_TEST +"\" was not found in the returned GetCapabilities.");
        }
        
        Get get = (Get) responseCaps.getCapability().getRequest().getGetCapabilities().getDCPType().get(0).getHTTP().getGetOrPost().get(0);
        assertEquals(WCS_URL, get.getOnlineResource().getHref());
        
        try {
            getCapsUrl = new URL(WCS_GETCAPABILITIES2);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to marshall something from the response returned by the server.
        // The response should be a WCSCapabilitiesType.
        obj = unmarshallResponse(getCapsUrl);
        assertTrue(obj instanceof WCSCapabilitiesType);

        responseCaps = (WCSCapabilitiesType)obj;
        
        get = (Get) responseCaps.getCapability().getRequest().getGetCapabilities().getDCPType().get(0).getHTTP().getGetOrPost().get(0);
        assertEquals(WCS_URL2, get.getOnlineResource().getHref());
        
        
        try {
            getCapsUrl = new URL(WCS_GETCAPABILITIES);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to marshall something from the response returned by the server.
        // The response should be a WCSCapabilitiesType.
        obj = unmarshallResponse(getCapsUrl);
        assertTrue(obj instanceof WCSCapabilitiesType);

        responseCaps = (WCSCapabilitiesType)obj;
        
        get = (Get) responseCaps.getCapability().getRequest().getGetCapabilities().getDCPType().get(0).getHTTP().getGetOrPost().get(0);
        assertEquals(WCS_URL, get.getOnlineResource().getHref());
    }

    /**
     * Ensures that a valid DescribeCoverage request returns indeed a valid document.
     */
    @Test
    public void testWCSDescribeCoverage() throws JAXBException, IOException {

        // Creates a valid DescribeCoverage url.
        final URL getCapsUrl;
        try {
            getCapsUrl = new URL(WCS_DESCRIBECOVERAGE);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to marshall something from the response returned by the server.
        // The response should be a WCSCapabilitiesType.
        final Object obj = unmarshallResponse(getCapsUrl);
        assertTrue(obj instanceof CoverageDescription);

        final CoverageDescription responseDesc = (CoverageDescription)obj;
        assertNotNull(responseDesc);
        final List<CoverageOfferingType> coverageOffs = responseDesc.getCoverageOffering();
        assertFalse (coverageOffs.isEmpty());
        assertEquals(LAYER_TEST.getLocalPart(), coverageOffs.get(0).getRest().get(1).getValue());
        // TODO: add more tests on returned XML doc
    }

    /**
     * Free some resources.
     */
    @AfterClass
    public static void finish() {
    }
}