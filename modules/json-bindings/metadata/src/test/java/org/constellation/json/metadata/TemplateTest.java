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

import java.util.Set;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.opengis.metadata.citation.Party;
import org.opengis.metadata.citation.Role;
import org.opengis.metadata.citation.DateType;
import org.apache.sis.metadata.MetadataStandard;
import org.apache.sis.metadata.iso.DefaultMetadata;
import org.apache.sis.metadata.iso.citation.DefaultCitation;
import org.apache.sis.metadata.iso.citation.DefaultCitationDate;
import org.apache.sis.metadata.iso.citation.DefaultIndividual;
import org.apache.sis.metadata.iso.citation.DefaultOrganisation;
import org.apache.sis.metadata.iso.citation.DefaultResponsibleParty;
import org.apache.sis.metadata.iso.extent.DefaultExtent;
import org.apache.sis.metadata.iso.extent.DefaultGeographicBoundingBox;
import org.apache.sis.metadata.iso.identification.DefaultDataIdentification;
import org.apache.sis.metadata.iso.identification.DefaultKeywords;
import org.apache.sis.util.CharSequences;
import org.apache.sis.util.ComparisonMode;
import org.junit.Test;

import static org.junit.Assert.*;
import static java.util.Arrays.asList;
import static java.util.Collections.singleton;


/**
 * Tests the {@link Template} class.
 *
 * @author Martin Desruisseaux (Geomatys)
 */
public final strictfp class TemplateTest {
    /**
     * Verifies the validity of templates returned by {@link Template#getInstance(String)}.
     *
     * @throws ParseException if a predefined template is not valid.
     */
    @Test
    public void validateInstances() throws ParseException {
        final Set<String> names = Template.getAvailableNames();
        assertTrue("profile_import",             names.contains("profile_import"));
        assertTrue("profile_inspire_vector",     names.contains("profile_inspire_vector"));
        assertTrue("profile_inspire_raster",     names.contains("profile_inspire_raster"));
        assertTrue("profile_sensorml_component", names.contains("profile_sensorml_component"));
        assertTrue("profile_sensorml_system",    names.contains("profile_sensorml_system"));
        for (final String name : names) {
            final Template template = Template.getInstance(name);
            assertNotNull(name, template);
            assertEquals(name, template.root.validatePath(null), template.depth);
        }
    }

    /**
     * Creates the metadata object corresponding to the test JSon string.
     */
    private static DefaultMetadata createMetadata() {
        final DefaultCitation citation = new DefaultCitation("Data \"title\"");
        citation.setDates(singleton(new DefaultCitationDate(new Date(1272844800000L + 24*60*60*1000), DateType.CREATION)));
        final DefaultDataIdentification identification = new DefaultDataIdentification();
        identification.setCitation(citation);
        identification.setExtents(singleton(new DefaultExtent(null,
                new DefaultGeographicBoundingBox(-11.4865013, -4.615912, 43.165467, 49.9990223), null, null)
        ));
        identification.setDescriptiveKeywords(asList(
                new DefaultKeywords("keyword 1", "keyword 2", "keyword 3"),
                new DefaultKeywords("keyword 4", "keyword 5")
        ));
        identification.setPointOfContacts(asList(
                responsibility(Role.AUTHOR,       new DefaultIndividual  ("An author",       null, null)),
                responsibility(Role.COLLABORATOR, new DefaultIndividual  ("A collaborator",  null, null)),
                responsibility(Role.DISTRIBUTOR,  new DefaultOrganisation("An organisation", null, null, null))
        ));

        final DefaultMetadata metadata = new DefaultMetadata();
        metadata.setFileIdentifier("An archive");
        metadata.setLanguage(Locale.ENGLISH);
        metadata.setCharacterSets(Collections.singleton(StandardCharsets.UTF_16));
        metadata.setMetadataStandardName("ISO19115");
        metadata.setMetadataStandardVersion("2003/Cor.1:2006");
        metadata.setIdentificationInfo(singleton(identification));
        return metadata;
    }

    /**
     * Helper method for {@link #createMetadata()}. We need the deprecated {@link DefaultResponsibleParty}
     * instance because we need the legacy {@code organisationName} and {@code individualName} properties.
     */
    @SuppressWarnings("deprecation")
    private static DefaultResponsibleParty responsibility(final Role role, final Party party) {
        final DefaultResponsibleParty r = new DefaultResponsibleParty(role);
        r.setParties(singleton(party));
        return r;
    }

    /**
     * Returns a reader for the test resource of the given name.
     */
    private static BufferedReader open(final String file) throws IOException {
        return new BufferedReader(new InputStreamReader(Template.class.getResourceAsStream(file), "UTF-8"));
    }

    /**
     * Returns all lines read from the test resource of the given name.
     */
    static List<String> readAllLines(final String file) throws IOException {
        final List<String> lines = new ArrayList<>();
        try (final BufferedReader in = open(file)) {
            String line;
            while ((line = in.readLine()) != null) {
                assertTrue(lines.add(line));
            }
        }
        return lines;
    }

    /**
     * Asserts that a JSON output is equals to the expected one.
     *
     * @param  expectedFile The filename (without directory) of the test resource containing the expected JSON content.
     * @param  actual The JSON content produced by {@link Template}.
     * @throws IOException if an error occurred while reading the expected JSON file.
     */
    static void assertJsonEquals(final String expectedFile, final CharSequence actual) throws IOException {
        int lineNumber = 0;
        final CharSequence[] lines = CharSequences.splitOnEOL(actual);
        try (final BufferedReader in = open(expectedFile)) {
            String expectedLine;
            while ((expectedLine = in.readLine()) != null) {
                final CharSequence actualLine = lines[lineNumber++];
                if (!expectedLine.equals(actualLine)) {
                    fail("Comparison failure at line " + lineNumber + ".\n" +
                         "Expected: " + expectedLine + "\n" +
                         "Actual:   " + actualLine + '\n');
                }
            }
        }
    }

    /**
     * Test writing of a simple metadata while pruning the empty nodes.
     *
     * @throws IOException if an error occurred while applying the template.
     */
    @Test
    public void testWritePrune() throws IOException {
        final DefaultMetadata metadata = createMetadata();
        final StringBuilder buffer = new StringBuilder(10000);
        Template.getInstance("profile_inspire_vector").write(metadata, buffer, true);
        assertJsonEquals("vector_prune.json", buffer);
    }

    /**
     * Test writing of a simple metadata without pruning the empty nodes.
     *
     * @throws IOException if an error occurred while applying the template.
     */
    @Test
    public void testWriteFull() throws IOException {
        final DefaultMetadata metadata = createMetadata();
        final StringBuilder buffer = new StringBuilder(40000);
        Template.getInstance("profile_inspire_vector").write(metadata, buffer, false);
        assertJsonEquals("vector_test.json", buffer);
    }

    /**
     * Tests {@link Template#read(Iterable, Object)} when storing in an initially empty {@link DefaultMetadata}.
     *
     * @throws IOException if an error occurred while reading the test JSON file.
     */
    @Test
    public void testRead() throws IOException {
        /*
         * Get the expected metadata with only the first keyword of the first DescriptiveKeywords section.
         * This is because the JSON template restricts the first DescriptiveKeywords to only one keyword.
         * Also clear the geographic bouding box inclusion flag.
         */
        final DefaultMetadata expected = createMetadata();
        ((List<?>) expected.getIdentificationInfo().iterator().next()
                .getDescriptiveKeywords().iterator().next()
                .getKeywords()).subList(1, 3).clear();
        ((DefaultGeographicBoundingBox) expected.getIdentificationInfo().iterator().next()
                .getExtents().iterator().next()
                .getGeographicElements().iterator().next()).setInclusion(null);
        /*
         * Parse and compare.
         */
        final DefaultMetadata metadata = new DefaultMetadata();
        Template.getInstance("profile_inspire_vector").read(readAllLines("vector_prune.json"), metadata, true);
        assertTrue(expected.equals(metadata, ComparisonMode.DEBUG));
    }

    /**
     * Tests reading the un-pruned file. This test just ensures that no exception is thrown - we do not
     * yet verify the content.
     *
     * @throws IOException if an error occurred while reading the test JSON file.
     */
    @Test
    public void testReadFull() throws IOException {
        final DefaultMetadata metadata = new DefaultMetadata();
        Template.getInstance("profile_inspire_vector").read(readAllLines("vector_test.json"), metadata, false);
        assertFalse(metadata.isEmpty());
    }

    /**
     * Tests with geographic elements of different types.
     *
     * @throws IOException if an error occurred while reading the test JSON file.
     */
    @Test
    public void testGeographicElements() throws IOException {
        final List<String> lines = readAllLines("profile_dataset.json");
        final Template template = new Template(MetadataStandard.ISO_19115, lines, null);
        final DefaultMetadata metadata = new DefaultMetadata();
        template.read(lines, metadata, false);
        assertTrue(metadata.isEmpty());
    }
}
