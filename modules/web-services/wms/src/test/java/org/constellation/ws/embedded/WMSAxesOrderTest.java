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
package org.constellation.ws.embedded;

// J2SE dependencies
import org.constellation.business.IDataBusiness;
import org.constellation.business.ILayerBusiness;
import org.constellation.business.IProviderBusiness;
import org.constellation.business.IServiceBusiness;
import org.constellation.test.utils.TestDatabaseHandler;
import org.constellation.admin.SpringHelper;
import org.constellation.api.ProviderType;
import org.constellation.configuration.Language;
import org.constellation.configuration.Languages;
import org.constellation.configuration.LayerContext;
import org.constellation.configuration.WMSPortrayal;
import org.constellation.provider.DataProviders;
import org.constellation.provider.ProviderFactory;
import org.constellation.test.ImageTesting;
import org.constellation.test.utils.Order;
import org.constellation.test.utils.SpringTestRunner;
import org.geotoolkit.feature.type.DefaultName;
import org.geotoolkit.image.io.plugin.WorldFileImageReader;
import org.geotoolkit.image.jai.Registry;
import org.geotoolkit.test.Commons;
import org.junit.AfterClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.spi.ImageWriterSpi;
import javax.inject.Inject;
import javax.xml.namespace.QName;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.constellation.configuration.ConfigDirectory;

import static org.constellation.provider.configuration.ProviderParameters.SOURCE_ID_DESCRIPTOR;
import static org.constellation.provider.configuration.ProviderParameters.SOURCE_LOADALL_DESCRIPTOR;
import static org.constellation.provider.configuration.ProviderParameters.getOrCreate;
import static org.constellation.provider.coveragesql.CoverageSQLProviderService.COVERAGESQL_DESCRIPTOR;
import static org.constellation.provider.coveragesql.CoverageSQLProviderService.NAMESPACE_DESCRIPTOR;
import static org.constellation.provider.coveragesql.CoverageSQLProviderService.PASSWORD_DESCRIPTOR;
import static org.constellation.provider.coveragesql.CoverageSQLProviderService.ROOT_DIRECTORY_DESCRIPTOR;
import static org.constellation.provider.coveragesql.CoverageSQLProviderService.SCHEMA_DESCRIPTOR;
import static org.constellation.provider.coveragesql.CoverageSQLProviderService.URL_DESCRIPTOR;
import static org.constellation.provider.coveragesql.CoverageSQLProviderService.USER_DESCRIPTOR;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeNoException;
import static org.junit.Assume.assumeTrue;
import org.junit.BeforeClass;
import org.springframework.test.context.ActiveProfiles;

// JUnit dependencies


/**
 * Do some tests on the {@code WMS GetMap} request, in order to ensure that the axes
 * order is well handled by Constellation, for different kinds of
 * {@linkplain CoordinateReferenceSystem CRS}.
 *
 * @version $Id$
 *
 * @author Cédric Briançon (Geomatys)
 * @since 0.3
 */
@RunWith(SpringTestRunner.class)
@ContextConfiguration("classpath:/cstl/spring/test-derby.xml")
@ActiveProfiles({"standard","derby"})
public class WMSAxesOrderTest extends AbstractGrizzlyServer  implements ApplicationContextAware {

    protected ApplicationContext applicationContext;
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Inject
    private IServiceBusiness serviceBusiness;
    
    @Inject
    protected ILayerBusiness layerBusiness;
    
    @Inject
    protected IProviderBusiness providerBusiness;
    
    @Inject
    protected IDataBusiness dataBusiness;
    
    /**
     * The layer to test.
     */
    private static final DefaultName LAYER_TEST = new DefaultName("SST_tests");

    /**
     * Checksum value on the returned image expressed in a geographic CRS for the SST_tests layer.
     */
    private Long sstChecksumGeo = null;

    /**
     * URLs which will be tested on the server.
     */
    private static final String WMS_GETMAP_111_PROJ ="request=GetMap&service=WMS&version=1.1.1&" +
                                      "format=image/png&width=1024&height=512&" +
                                      "srs=EPSG:3395&bbox=-19000000,-19000000,19000000,19000000&" +
                                      "layers="+ LAYER_TEST +"&styles=";
    private static final String WMS_GETMAP_130_PROJ ="request=GetMap&service=WMS&version=1.3.0&" +
                                      "format=image/png&width=1024&height=512&" +
                                      "crs=EPSG:3395&bbox=-19000000,-19000000,19000000,19000000&" +
                                      "layers="+ LAYER_TEST +"&styles=";
    private static final String WMS_GETMAP_111_GEO ="request=GetMap&service=WMS&version=1.1.1&" +
                                      "format=image/png&width=1024&height=512&" +
                                      "srs=EPSG:4022&bbox=-90,-180,90,180&" +
                                      "layers="+ LAYER_TEST +"&styles=";
    private static final String WMS_GETMAP_111_EPSG_4326 ="request=GetMap&service=WMS&version=1.1.1&" +
                                      "format=image/png&width=1024&height=512&" +
                                      "srs=EPSG:4326&bbox=-180,-90,180,90&" +
                                      "layers="+ LAYER_TEST +"&styles=";
    private static final String WMS_GETMAP_130_EPSG_4326 ="request=GetMap&service=WMS&version=1.3.0&" +
                                      "format=image/png&width=512&height=1024&" +
                                      "crs=EPSG:4326&bbox=-90,-180,90,180&" +
                                      "layers="+ LAYER_TEST +"&styles=";
    private static final String WMS_GETMAP_111_CRS_84 ="request=GetMap&service=WMS&version=1.1.1&" +
                                      "format=image/png&width=1024&height=512&" +
                                      "srs=CRS:84&bbox=-180,-90,180,90&" +
                                      "layers="+ LAYER_TEST +"&styles=";
    private static final String WMS_GETMAP_130_CRS_84 ="request=GetMap&service=WMS&version=1.3.0&" +
                                      "format=image/png&width=1024&height=512&" +
                                      "crs=CRS:84&bbox=-180,-90,180,90&" +
                                      "layers="+ LAYER_TEST +"&styles=";

    private static boolean initialized = false;
    
    private static boolean localdb_active = true;
    
    @BeforeClass
    public static void startup() {
        ConfigDirectory.setupTestEnvironement("WMSAxesOrderTest");
    }
    
    /**
     * Initialize the list of layers from the defined providers in Constellation's configuration.
     */
    @PostConstruct
    public void initLayerList() {
        SpringHelper.setApplicationContext(applicationContext);
        if (!initialized) {
            try {
                layerBusiness.removeAll();
                dataBusiness.deleteAll();
                serviceBusiness.deleteAll();
                providerBusiness.removeAll();
                
                // coverage-sql datastore
                localdb_active = TestDatabaseHandler.hasLocalDatabase();
                if (localdb_active) {
                    final String rootDir                = System.getProperty("java.io.tmpdir") + "/Constellation/images";
                    final ProviderFactory factory       = DataProviders.getInstance().getFactory("coverage-sql");
                    final ParameterValueGroup source    = factory.getProviderDescriptor().createValue();
                    final ParameterValueGroup srcconfig = getOrCreate(COVERAGESQL_DESCRIPTOR,source);
                    
                    srcconfig.parameter(URL_DESCRIPTOR           .getName().getCode()).setValue(TestDatabaseHandler.testProperties.getProperty("coverage_db_url"));
                    srcconfig.parameter(PASSWORD_DESCRIPTOR      .getName().getCode()).setValue(TestDatabaseHandler.testProperties.getProperty("coverage_db_pass"));
                    srcconfig.parameter(ROOT_DIRECTORY_DESCRIPTOR.getName().getCode()).setValue(rootDir);
                    srcconfig.parameter(USER_DESCRIPTOR          .getName().getCode()).setValue(TestDatabaseHandler.testProperties.getProperty("coverage_db_user"));
                    srcconfig.parameter(SCHEMA_DESCRIPTOR        .getName().getCode()).setValue(TestDatabaseHandler.testProperties.getProperty("coverage_db_schema"));
                    srcconfig.parameter(NAMESPACE_DESCRIPTOR     .getName().getCode()).setValue("no namespace");
                    source.parameter(SOURCE_LOADALL_DESCRIPTOR   .getName().getCode()).setValue(Boolean.TRUE);
                    source.parameter(SOURCE_ID_DESCRIPTOR        .getName().getCode()).setValue("coverageTestSrc");
                    providerBusiness.storeProvider("coverageTestSrc", null, ProviderType.LAYER, "coverage-sql", source);

                    dataBusiness.create(new QName("SST_tests"), "coverageTestSrc", rootDir, false, true, null, null);
                } else {
                    LOGGER.log(Level.WARNING, "-- SOME TEST WILL BE SKIPPED BECAUSE THE LOCAL DATABASE IS MISSING --");
                }
                
                DataProviders.getInstance().reload();

                final LayerContext config = new LayerContext();
                config.getCustomParameters().put("shiroAccessible", "false");

                serviceBusiness.create("wms", "default", config, null, null);
                if (localdb_active) layerBusiness.add("SST_tests", null, "coverageTestSrc", null, "default", "wms", null);


                final LayerContext config2 = new LayerContext();
                config2.setSupportedLanguages(new Languages(Arrays.asList(new Language("fre"), new Language("eng", true))));
                config2.getCustomParameters().put("shiroAccessible", "false");


                serviceBusiness.create("wms", "wms1", config2, null, null);
                if (localdb_active) layerBusiness.add("SST_tests", null, "coverageTestSrc", null, "wms1", "wms", null);

                initServer(null, null);

                WorldFileImageReader.Spi.registerDefaults(null);
                WMSPortrayal.setEmptyExtension(true);

                //reset values, only allow pure java readers
                for(String jn : ImageIO.getReaderFormatNames()){
                    Registry.setNativeCodecAllowed(jn, ImageReaderSpi.class, false);
                }

                //reset values, only allow pure java writers
                for(String jn : ImageIO.getWriterFormatNames()){
                    Registry.setNativeCodecAllowed(jn, ImageWriterSpi.class, false);
                }

                // Get the list of layers
                initialized = true;
            } catch (Exception ex) {
                Logger.getLogger(WMSAxesOrderTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Free some resources.
     */
    @AfterClass
    public static void shutDown() {
        ConfigDirectory.shutdownTestEnvironement("WMSAxesOrderTest");
        File f = new File("derby.log");
        if (f.exists()) {
            f.delete();
        }
        finish();
    }

    /**
     * Ensures that a GetMap in version 1.1.1 on a projected
     * {@linkplain CoordinateReferenceSystem CRS} provides the same image that a GetMap
     * in version 1.3.0 on the same CRS.
     */
    @Test
    @Order(order = 1)
    public void testGetMap111And130Projected() throws Exception {
        waitForStart();

        assumeTrue(localdb_active);

        // Creates a valid GetMap url.
        final URL getMap111Url, getMap130Url;
        try {
            getMap111Url = new URL("http://localhost:" + grizzly.getCurrentPort() + "/wms/default?" + WMS_GETMAP_111_PROJ);
            getMap130Url = new URL("http://localhost:" + grizzly.getCurrentPort() + "/wms/default?" + WMS_GETMAP_130_PROJ);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to get a map from the url. The test is skipped in this method if it fails.
        final BufferedImage image111 = getImageFromURL(getMap111Url, "image/png");
        final BufferedImage image130 = getImageFromURL(getMap130Url, "image/png");

        // Tests on the returned images.
        assertTrue  (!(ImageTesting.isImageEmpty(image111)));
        assertEquals(1024, image111.getWidth());
        assertEquals(512,  image111.getHeight());
        assertTrue  (!(ImageTesting.isImageEmpty(image130)));
        assertEquals(1024, image130.getWidth());
        assertEquals(512,  image130.getHeight());
        assertEquals(Commons.checksum(image111), Commons.checksum(image130));
    }

    /**
     * Test a GetMap request on a WMS version 1.1.1 for a geographical
     * {@linkplain CoordinateReferenceSystem CRS}.
     *
     * TODO: fix the implementation of the GetMap request concerning the handling of
     *       geographical CRS (not WGS84) and do this test then.
     */
    @Ignore
    @Order(order = 2)
    public void testCRSGeographique111() throws IOException {
        assumeTrue(localdb_active);

        // Creates a valid GetMap url.
        final URL getMapUrl;
        try {
            getMapUrl = new URL("http://localhost:" + grizzly.getCurrentPort() + "/wms/default?" + WMS_GETMAP_111_GEO);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to get a map from the url. The test is skipped in this method if it fails.
        final BufferedImage image = getImageFromURL(getMapUrl, "image/png");

        // Tests on the returned image.
        assertTrue  (!(ImageTesting.isImageEmpty(image)));
        assertEquals(1024, image.getWidth());
        assertEquals(512,  image.getHeight());
    }

    /**
     * Verify the axis order for a GetMap in version 1.1.1 for the {@code WGS84} CRS.
     */
    @Test
    @Order(order = 3)
    public void testGetMap111Epsg4326() throws IOException {
        assumeTrue(localdb_active);

        // Creates a valid GetMap url.
        final URL getMapUrl;
        try {
            getMapUrl = new URL("http://localhost:" + grizzly.getCurrentPort() + "/wms/default?" + WMS_GETMAP_111_EPSG_4326);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to get a map from the url. The test is skipped in this method if it fails.
        final BufferedImage image = getImageFromURL(getMapUrl, "image/png");

        // Tests on the returned image.
        assertTrue  (!(ImageTesting.isImageEmpty(image)));
        assertEquals(1024, image.getWidth());
        assertEquals(512,  image.getHeight());
        if (sstChecksumGeo == null) {
            sstChecksumGeo = Commons.checksum(image);
            assertTrue(ImageTesting.getNumColors(image) > 8);
        } else {
            assertEquals(sstChecksumGeo.longValue(), Commons.checksum(image));
        }
    }

    /**
     * Verify the axis order for a GetMap in version 1.3.0 for the {@code WGS84} CRS.
     */
    @Test
    @Order(order = 4)
    public void testGetMap130Epsg4326() throws IOException {
        assumeTrue(localdb_active);

        // Creates a valid GetMap url.
        final URL getMapUrl;
        try {
            getMapUrl = new URL("http://localhost:" + grizzly.getCurrentPort() + "/wms/default?" + WMS_GETMAP_130_EPSG_4326);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to get a map from the url. The test is skipped in this method if it fails.
        final BufferedImage image = getImageFromURL(getMapUrl, "image/png");

        // Tests on the returned image.
        assertTrue  (!(ImageTesting.isImageEmpty(image)));
        assertEquals(512, image.getWidth());
        assertEquals(1024,  image.getHeight());
        if (sstChecksumGeo == null) {
            assertTrue(ImageTesting.getNumColors(image) > 8);
        } else {
            assertTrue(sstChecksumGeo.longValue() != Commons.checksum(image));
        }
    }

    /**
     * Verify the axis order for a GetMap in version 1.1.1 for the {@code WGS84} CRS.
     */
    @Test
    @Order(order = 5)
    public void testGetMap111Crs84() throws IOException {
        assumeTrue(localdb_active);

        // Creates a valid GetMap url.
        final URL getMapUrl;
        try {
            getMapUrl = new URL("http://localhost:" + grizzly.getCurrentPort() + "/wms/default?" + WMS_GETMAP_111_CRS_84);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to get a map from the url. The test is skipped in this method if it fails.
        final BufferedImage image = getImageFromURL(getMapUrl, "image/png");

        // Tests on the returned image.
        assertTrue  (!(ImageTesting.isImageEmpty(image)));
        assertEquals(1024, image.getWidth());
        assertEquals(512,  image.getHeight());
        if (sstChecksumGeo == null) {
            sstChecksumGeo = Commons.checksum(image);
            assertTrue(ImageTesting.getNumColors(image) > 8);
        } else {
            assertEquals(sstChecksumGeo.longValue(), Commons.checksum(image));
        }
    }

    /**
     * Verify the axis order for a GetMap in version 1.3.0 for the {@code WGS84} CRS.
     *
     * TODO: fix the implementation of the GetMap request concerning the axes order,
     *       and do this test then.
     */
    @Test
    @Order(order = 6)
    public void testGetMap130Crs84() throws IOException {
        assumeTrue(localdb_active);

        // Creates a valid GetMap url.
        final URL getMapUrl;
        try {
            getMapUrl = new URL("http://localhost:" + grizzly.getCurrentPort() + "/wms/default?" + WMS_GETMAP_130_CRS_84);
        } catch (MalformedURLException ex) {
            assumeNoException(ex);
            return;
        }

        // Try to get a map from the url. The test is skipped in this method if it fails.
        final BufferedImage image = getImageFromURL(getMapUrl, "image/png");

        // Tests on the returned image.
        assertTrue  (!(ImageTesting.isImageEmpty(image)));
        assertEquals(1024, image.getWidth());
        assertEquals(512,  image.getHeight());
        if (sstChecksumGeo == null) {
            sstChecksumGeo = Commons.checksum(image);
            assertTrue(ImageTesting.getNumColors(image) > 8);
        } else {
            assertEquals(sstChecksumGeo.longValue(), Commons.checksum(image));
        }
    }
}
