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
package org.constellation.admin;

import com.google.common.eventbus.EventBus;
import org.apache.sis.util.logging.Logging;
import org.springframework.context.ApplicationContext;

import java.util.logging.Logger;

public class SpringHelper {

    private static final Logger LOGGER = Logging.getLogger(SpringHelper.class);
    
    private static ApplicationContext applicationContext;

    private static EventBus eventBus;

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringHelper.applicationContext = applicationContext;
        SpringHelper.eventBus = applicationContext.getBean(EventBus.class);
        LOGGER.info("spring application context loaded");
    }

    public static void injectDependencies(Object object) {
        if (SpringHelper.applicationContext != null) {
            SpringHelper.applicationContext.getAutowireCapableBeanFactory().autowireBean(object);
        } else {
            LOGGER.warning("No spring application context available");
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        if (SpringHelper.applicationContext != null) {
             return applicationContext.getBean(clazz);
        } else {
            LOGGER.warning("No spring application context available");
        }
        return null;
    }

    public static void sendEvent(Object event) {
        if (eventBus != null) {
            eventBus.post(event);
        } else {
            LOGGER.warning("No event bus available");
        }
    }

}
