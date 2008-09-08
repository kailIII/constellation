/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2008, Geomatys
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
package net.seagis.coverage.model;

import java.util.Set;
import java.sql.SQLException;
import javax.media.jai.KernelJAI;
import org.opengis.parameter.ParameterValueGroup;
import net.seagis.catalog.CatalogException;
import net.seagis.catalog.TableTest;

import org.junit.*;


/**
 * Tests {@link OperationTable}.
 *
 * @version $Id$
 * @author Martin Desruisseaux
 */
public class OperationTableTest extends TableTest {
    /**
     * The name of the operation to be tested.
     */
    public static final String SAMPLE_NAME = "Magnitude du gradient 3×3";

    /**
     * Tests the {@link OperationTable#getEntry} and {@link OperationTable#getEntries} methods.
     */
    @Test
    public void testSelectAndList() throws CatalogException, SQLException {
        final OperationTable table = new OperationTable(database);
        final OperationEntry entry = (OperationEntry) table.getEntry(SAMPLE_NAME);
        assertEquals(SAMPLE_NAME, entry.getName());
        assertSame(entry, table.getEntry(SAMPLE_NAME));
        assertEquals("∇₃", entry.getPrefix());

        final ParameterValueGroup parameters = entry.getParameters();
        assertNotNull(parameters);
        assertEquals("GradientMagnitude", parameters.getDescriptor().getName().getCode());
        assertTrue(parameters.parameter("mask1").getValue() instanceof KernelJAI);
        assertTrue(parameters.parameter("mask2").getValue() instanceof KernelJAI);

        final Set<Operation> entries = table.getEntries();
        assertFalse(entries.isEmpty());
        assertTrue(entries.contains(entry));
    }
}
