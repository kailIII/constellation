/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2012, Geomatys
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
package org.constellation.wps.converters.outputs.references;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageWriter;
import org.geotoolkit.image.io.XImageIO;
import org.geotoolkit.util.converter.NonconvertibleObjectException;
import org.geotoolkit.wps.xml.v100.OutputReferenceType;

/**
 *
 * @author Quentin Boileau (Geomatys).
 */
public class RenderedImageToReferenceConverter extends AbstractReferenceOutputConverter {

    private static RenderedImageToReferenceConverter INSTANCE;

    private RenderedImageToReferenceConverter() {
    }

    public static synchronized RenderedImageToReferenceConverter getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RenderedImageToReferenceConverter();
        }
        return INSTANCE;
    }

    @Override
    public OutputReferenceType convert(final Map<String, Object> source) throws NonconvertibleObjectException {
        
        final OutputReferenceType reference = new OutputReferenceType();

        reference.setMimeType((String) source.get(OUT_MIME));
        reference.setEncoding((String) source.get(OUT_ENCODING));
        reference.setSchema((String) source.get(OUT_SCHEMA));

        final Object data = source.get(OUT_DATA);

        if (!(data instanceof RenderedImage)) {
            throw new NonconvertibleObjectException("The output data is not an instance of RenderedImage.");
        }

        final String randomFileName = UUID.randomUUID().toString();
        ImageWriter writer = null;
        try {
            //create file
            final File imageFile = new File((String) source.get(OUT_TMP_DIR_PATH), randomFileName);
            final RenderedImage image = (RenderedImage) data;
            writer = XImageIO.getWriterByMIMEType((String) source.get(OUT_MIME), imageFile, image);
            writer.write(image);
            reference.setSchema((String) source.get(OUT_TMP_DIR_URL) + "/" + randomFileName);
            
        } catch (IOException ex) {
            Logger.getLogger(RenderedImageToReferenceConverter.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (writer != null) {
                writer.dispose();
            }
        }
        
        return reference;
    }
}
