/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2011, Geomatys
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
package org.constellation.scheduler;

import org.quartz.SimpleTrigger;
import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamException;
import org.geotoolkit.process.ProcessFinder;

import org.geotoolkit.process.quartz.ProcessJobDetail;
import org.geotoolkit.xml.StaxStreamReader;
import org.geotoolkit.xml.parameter.ParameterValueReader;
import org.opengis.parameter.ParameterDescriptorGroup;

import org.opengis.parameter.ParameterValueGroup;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.TriggerBuilder;

import static javax.xml.stream.XMLStreamReader.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class TasksReader extends StaxStreamReader{
    
    private static final String TAG_TASKS = "tasks";
    private static final String TAG_TASK = "task";
    private static final String ATT_AUTHORITY = "authority";
    private static final String ATT_CODE = "code";
    
    private static final String TAG_TRIGGER = "trigger";
    private static final String ATT_STEP = "step";
    
    private static final String TAG_PARAMETERS = "parameters";
    
    public TasksReader(){
        
    }
    
    public List<Task> read() throws XMLStreamException, IOException{
        
        while (reader.hasNext()) {
            final int type = reader.next();
            if (type == START_ELEMENT
                    && reader.getLocalName().equalsIgnoreCase(TAG_TASKS)) {
                return readTasks();
            }
        }

        throw new XMLStreamException("Tasks tag not found");
    }
    
    private List<Task> readTasks() throws XMLStreamException, IOException {
        final List<Task> tasks = new ArrayList<Task>();
        
        while (reader.hasNext()) {
            final int type = reader.next();
            if (type == START_ELEMENT
                    && reader.getLocalName().equalsIgnoreCase(TAG_TASK)) {
                final Task t = readTask();
                if(t != null){
                    tasks.add(t);
                }
            }else if(type == END_ELEMENT && reader.getLocalName().equals(TAG_TASKS)){
                break;
            }
        }
        
        return tasks;
    }
    
    private Task readTask() throws XMLStreamException, IOException {
        final Task task = new Task();
        
        final String authority = reader.getAttributeValue(null, ATT_AUTHORITY);
        final String code = reader.getAttributeValue(null, ATT_CODE);
        final ParameterDescriptorGroup inputDesc = ProcessFinder.getProcessDescriptor(authority, code).getInputDescriptor();
        
        ParameterValueGroup params = null;
        SimpleTrigger trigger = null;
        
        
        
        while (reader.hasNext()) {
            final int type = reader.next();
            if (type == START_ELEMENT){
                final String localName = reader.getLocalName();
                
                if(localName.equalsIgnoreCase(TAG_PARAMETERS)) {
                    params = readParameters(inputDesc);
                }else if(localName.equalsIgnoreCase(TAG_TRIGGER)) {
                    trigger = readTrigger();
                }
            }else if(type == END_ELEMENT){
                break;
            }
        }
                
        
        final ProcessJobDetail detail = new ProcessJobDetail(authority, code, params);
        
        task.setDetail(detail);
        task.setTrigger(trigger);        
        return task;
    }
    
    private ParameterValueGroup readParameters(final ParameterDescriptorGroup desc) throws XMLStreamException, IOException{
        final ParameterValueReader prmreader = new ParameterValueReader(desc);
        prmreader.setInput(reader);
        return (ParameterValueGroup) prmreader.read();
    }
    
    private SimpleTrigger readTrigger() throws XMLStreamException{
        
        final double step = parseDouble(reader.getAttributeValue(null, ATT_STEP));
        toTagEnd(TAG_TRIGGER);
        
        final TriggerBuilder tb = TriggerBuilder.newTrigger();
        tb.startNow();
        tb.withSchedule(SimpleScheduleBuilder.repeatSecondlyForever((int)step));
        return (SimpleTrigger) tb.build();
    }
    
}
