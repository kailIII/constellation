/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2005, Institut de Recherche pour le Développement
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
package org.constellation.sie.type.bbox;

// J2SE dependencies
import java.util.Date;

// OpenGIS dependencies
import org.opengis.metadata.extent.GeographicBoundingBox;

// Geotools dependencies
import org.geotools.resources.geometry.XRectangle2D;
import org.geotools.gui.swing.referencing.CoordinateChooser;
import org.geotools.metadata.iso.extent.GeographicBoundingBoxImpl;

// Constellation dependencies
import org.constellation.util.DateRange;


/**
 * La partie visuelle propre à la deuxième page de l'assistant.
 *
 * @version $Id$
 * @author Martin Desruisseaux
 */
final class WizardStep2 extends WizardStep {
    /**
     * La composante servant à sélectionner une enveloppe spatio-temporelle.
     */
    private final CoordinateChooser chooser;

    /**
     * Construit la composante visuelle.
     */
    public WizardStep2() {
        initComponents();
        final Date minTime = new Date(0);                             // 1 janvier 1970
        final Date maxTime = new Date(60L * 365250 * 24 * 60 * 60);   // 1 janvier 2030
        chooser = new CoordinateChooser(minTime, maxTime);
        add(chooser);
    }

    /**
     * {@inheritDoc}
     */
    public void readSettings(final BoundingBox settings) {
        final GeographicBoundingBox area = settings.getGeographicBoundingBox();
        final DateRange        timeRange = settings.getTimeRange();
        if (area != null) {
            chooser.setGeographicArea(XRectangle2D.createFromExtremums(
                    area.getWestBoundLongitude(),
                    area.getSouthBoundLatitude(),
                    area.getEastBoundLongitude(),
                    area.getNorthBoundLatitude()));
        }
        if (timeRange != null) {
            chooser.setTimeRange(timeRange.getMinValue(),
                                 timeRange.getMaxValue());
        }
        chooser.setPreferredResolution(settings.getResolution());
    }

    /**
     * {@inheritDoc}
     */
    public void storeSettings(final BoundingBox settings) {
        settings.setGeographicBoundingBox(new GeographicBoundingBoxImpl(chooser.getGeographicArea()));
        settings.setTimeRange            (new DateRange(chooser.getStartTime(), chooser.getEndTime()));
        settings.setResolution           (chooser.getPreferredResolution());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());

    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
