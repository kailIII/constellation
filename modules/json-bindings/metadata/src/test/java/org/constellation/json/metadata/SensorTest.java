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
package org.constellation.json.metadata;

import java.util.Arrays;
import java.io.IOException;
import java.net.URI;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import org.geotoolkit.sml.xml.v101.*;
import org.apache.sis.util.CharSequences;
import org.apache.sis.xml.MarshallerPool;
import org.junit.Test;

import static org.junit.Assert.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.constellation.json.metadata.TemplateTest.readAllLines;
import static org.constellation.json.metadata.TemplateTest.assertJsonEquals;


/**
 * Tests the {@link Template} class with a Sensor ML.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final strictfp class SensorTest {
    /**
     * Creates the metadata object corresponding to the test JSon string.
     */
    private static SensorML createSensorML() {
        final SystemType process = new SystemType();
        process.setIdentification(singletonList(
            new Identification(
                new IdentifierList(null, asList(
                    new Identifier("uniqueID",              // member.process.identification.identifierList.identifier.name
                    new Term("My unique ID", (URI) null)),  // member.process.identification.identifierList.identifier.term.value
                    new Identifier("My identifier",
                    new Term("My identifier term", (URI) null)))))));

        final SensorML sensor = new SensorML();
        sensor.getMember().add(new SensorML.Member(process));
        return sensor;
    }

    /**
     * Tests writing the metadata produced by {@link #createSensorML()}.
     *
     * @throws IOException if an error occurred while applying the template.
     */
    @Test
    public void testWrite() throws IOException {
        final SensorML metadata = createSensorML();
        final StringBuilder buffer = new StringBuilder(10000);
        Template.getInstance("profile_sensorml_system").write(metadata, buffer, true);
        assertJsonEquals("sensor_prune.json", buffer);
    }

    /**
     * Tests {@link Template#read(Iterable, Object)} with the JSon produced by {@link #testWrite()}.
     * The result is stored in an initially empty {@link SensorML}.
     *
     * @throws IOException if an error occurred while reading the test JSON file.
     */
    @Test
    public void testRead() throws IOException {
        final SensorML expected = createSensorML();
        final SensorML metadata = new SensorML();
        Template.getInstance("profile_sensorml_system").read(readAllLines("sensor_prune.json"), metadata, true);
        assertEquals(expected, metadata);
    }

    /**
     * Tests reading a larger, arbitrary SensorML file.
     *
     * @throws IOException if an error occurred while reading the test JSON file.
     */
    @Test
    public void testReadLarger() throws IOException {
        final SensorML metadata = new SensorML();
        Template.getInstance("profile_sensorml_system").read(readAllLines("sensor_larger.json"), metadata, true);
        // Current test just ensure that we didn't got any exception.
    }

    /**
     * Tests reading and writing with a larger SensorML.
     *
     * @throws JAXBException if an error occurred while reading the XML file.
     * @throws IOException if an error occurred while writing the test JSON file.
     */
    @Test
    public void testReadWriteFromXML() throws JAXBException, IOException {
        testReadWriteFromXML(true);
    }

    /**
     * Same test than {@link #testReadWriteFromXML()}, but without pruning.
     * This is the mode used when we plan to let the user edit the SensorML.
     *
     * @throws JAXBException if an error occurred while reading the XML file.
     * @throws IOException if an error occurred while writing the test JSON file.
     */
    @Test
    public void testReadWriteEditable() throws JAXBException, IOException {
        testReadWriteFromXML(false);
    }

    /**
     * Implementation of {@code testReadWrite()}.
     */
    private void testReadWriteFromXML(final boolean prune) throws JAXBException, IOException {
        final MarshallerPool pool = new MarshallerPool(JAXBContext.newInstance("org.geotoolkit.sml.xml.v101"), null);
        final Unmarshaller m = pool.acquireUnmarshaller();
        final Object metadata = m.unmarshal(SensorTest.class.getResource("sensorML.xml"));
        pool.recycle(m);

        final StringBuilder buffer = new StringBuilder(15000);
        final Template template = Template.getInstance("profile_sensorml_system");
        template.write(metadata, buffer, prune);

        final Object back = new SensorML();
        template.read(Arrays.asList(CharSequences.splitOnEOL(buffer)), back, prune);
        // Current test just ensure that we didn't got any exception.
    }

    /**
     * Tests the {@code "profile_sensorml_component"} profile.
     *
     * @throws IOException if an error occurred while writing the test JSON file.
     */
    @Test
    public void testReadWriteComponent() throws IOException {
        final SensorML metadata = createSensorML();
        final StringBuilder buffer = new StringBuilder(15000);
        final Template template = Template.getInstance("profile_sensorml_component");
        template.write(metadata, buffer, false);

        final Object back = new SensorML();
        template.read(Arrays.asList(CharSequences.splitOnEOL(buffer)), back, false);
    }
}
