package org.constellation.gui;

import juzu.Path;
import juzu.Response;
import juzu.Route;
import juzu.View;
import juzu.template.Template;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * main adminstration part controller
 *
 * @author Benjamin Garcia (Geomatys)
 * @version 0.9
 * @since 0.9
 *
 */
public class AdministrationController {

    private static final Logger LOGGER = Logger.getLogger(AdministrationController.class.getName());

    @Inject
    @Path("administration.gtmpl")
    Template administration;

    @Inject
    @Path("database.gtmpl")
    Template database;


    @View
    @Route("/administration")
    public Response getAdministration(){
        return administration.ok().withMimeType("text/html");
    }

    @View
    @Route("/database/{type}")
    public Response getDatabase(final String type){
        Map<String, Object> parameters = new HashMap<>(0);
        parameters.put("type", type);
        return database.with(parameters).ok().withMimeType("text/html");
    }
}