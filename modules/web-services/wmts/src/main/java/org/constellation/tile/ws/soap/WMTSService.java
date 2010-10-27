/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2009, Geomatys
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
package org.constellation.tile.ws.soap;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import org.constellation.tile.ws.DefaultWMTSWorker;
import org.constellation.tile.ws.WMTSWorker;
import org.constellation.ws.CstlServiceException;
import org.constellation.ws.ExceptionCode;
import org.geotoolkit.util.ImageIOUtilities;
import org.geotoolkit.wmts.xml.v100.BinaryPayload;
import org.geotoolkit.wmts.xml.v100.Capabilities;
import org.geotoolkit.wmts.xml.v100.GetCapabilities;
import org.geotoolkit.wmts.xml.v100.GetFeatureInfo;
import org.geotoolkit.wmts.xml.v100.GetTile;

/**
 * The SOAP facade to an OGC Web Map Tile Service, implementing the 1.0.0 version.
 * <p>
 * This SOAP service is not runned in that implementation because the working part is
 * not written.
 * </p>
 *
 * @version $Id$
 *
 * @author Cédric Briançon (Geomatys)
 */
//@WebService(name = "WMTSpepRegion", serviceName = "WMTSpepRegion")
@SOAPBinding(parameterStyle = ParameterStyle.BARE)
public class WMTSService {
    /**
     * The default logger.
     */
    private static final Logger LOGGER = Logger.getLogger("org.constellation.tile.ws.soap");

    /**
     * The worker for the SOAP service.
     */
    protected WMTSWorker worker;

    /**
     * Creates a WMTS SOAP service.
     */
    public WMTSService() {
        worker = new DefaultWMTSWorker("");
    }

    /**
     * Web service operation describing the service and its capabilities.
     *
     * @param requestCapabilities A document specifying the section you would obtain like :
     *      ServiceIdentification, ServiceProvider, Contents, operationMetadata.
     *
     * @throws SOAPServiceException
     */
    @WebMethod(action="getCapabilities")
    public Capabilities getCapabilities(@WebParam(name = "GetCapabilities") GetCapabilities requestCapabilities)
                                                                                     throws SOAPServiceException
    {
        try {
            LOGGER.info("received SOAP getCapabilities request");

            return worker.getCapabilities(requestCapabilities);

        } catch (CstlServiceException ex) {
            throw new SOAPServiceException(ex.getMessage(), ex.getExceptionCode().name(),
                                           requestCapabilities.getVersion().toString());
        }
    }

    /**
     * Web service operation giving the value of a precise point in an image.
     *
     * @param requestFeatureInfo The request to execute.
     *
     * @throws SOAPServiceException
     */
    @WebMethod(action="getFeatureInfo")
    public String getFeatureInfo(@WebParam(name = "GetFeatureInfo") GetFeatureInfo requestFeatureInfo)
                                                                           throws SOAPServiceException
    {
        try {
            LOGGER.info("received SOAP getFeatureInfo request");

            return worker.getFeatureInfo(requestFeatureInfo);

        } catch (CstlServiceException ex) {
            throw new SOAPServiceException(ex.getMessage(), ex.getExceptionCode().name(),
                                           requestFeatureInfo.getVersion());
        }
    }

    /**
     * Web service operation returning the image chosen.
     *
     * @param requestTile The request to execute.
     *
     * @throws SOAPServiceException
     */
    @WebMethod(action="getTile")
    public BinaryPayload getTile(@WebParam(name = "GetTile") GetTile requestTile)
                                                      throws SOAPServiceException
    {
        LOGGER.info("received SOAP getTile request");
        try {
            final ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            /*
            final RenderedImage buffered = worker.getTile(requestTile);
            final String mimeType = ImageIOUtilities.fileExtensionToMimeType(requestTile.getFormat());
            ImageIOUtilities.writeImage(buffered, mimeType, byteOut);*/
            final BinaryPayload binaryPayLoad = new BinaryPayload();
            binaryPayLoad.setBinaryContent(byteOut.toByteArray());
            binaryPayLoad.setFormat(requestTile.getFormat());
            byteOut.close();
            return binaryPayLoad;
        /*} catch (CstlServiceException ex) {
            throw new SOAPServiceException(ex.getMessage(), ex.getExceptionCode().name(),
                                           requestTile.getVersion());*/
        } catch (IOException ex) {
            throw new SOAPServiceException(ex.getMessage(), ExceptionCode.NO_APPLICABLE_CODE.name(), requestTile.getVersion());
        }
    }
}
