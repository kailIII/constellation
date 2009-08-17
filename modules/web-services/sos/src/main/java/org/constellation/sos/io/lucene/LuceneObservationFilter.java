/*
 *    Constellation - An open source and standard compliant SDI
 *    http://www.constellation-sdi.org
 *
 *    (C) 2007 - 2009, Geomatys
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

package org.constellation.sos.io.lucene;

import java.util.List;
import java.util.Properties;
import javax.xml.namespace.QName;
import org.constellation.generic.database.Automatic;
import org.constellation.sos.io.ObservationFilter;
import org.constellation.sos.io.ObservationResult;
import org.constellation.sos.ws.Parameters;
import org.constellation.ws.CstlServiceException;
import org.geotoolkit.gml.xml.v311.EnvelopeEntry;
import org.geotoolkit.gml.xml.v311.ReferenceEntry;
import org.geotoolkit.gml.xml.v311.TimeInstantType;
import org.geotoolkit.gml.xml.v311.TimePeriodType;
import org.geotoolkit.lucene.IndexingException;
import org.geotoolkit.lucene.SearchingException;
import org.geotoolkit.lucene.filter.SpatialQuery;
import org.geotoolkit.sos.xml.v100.ObservationOfferingEntry;
import org.geotoolkit.sos.xml.v100.ResponseModeType;
//import static org.geotoolkit.sos.xml.v100.ResponseModeType.*;
import static org.geotoolkit.ows.xml.OWSExceptionCode.*;
import static org.constellation.sos.ws.Utils.*;

/**
 * TODO
 * 
 * @author Guilhem Legal (Geomatys)
 */
public class LuceneObservationFilter implements ObservationFilter {

    private static final String NOT_SUPPORTED_YET = "Not supported yet.";

    private String observationIdBase;
    private String observationTemplateIdBase;
    private Properties map;
    private Automatic configuration;

    private StringBuilder luceneRequest;

    private LuceneObservationSearcher searcher;

    public LuceneObservationFilter(String observationIdBase, String observationTemplateIdBase, Properties map, Automatic configuration) throws CstlServiceException {
        this.configuration             = configuration;
        this.map                       = map;
        this.observationIdBase         = observationIdBase;
        this.observationTemplateIdBase = observationTemplateIdBase;
        try {
            this.searcher = new LuceneObservationSearcher(configuration.getConfigurationDirectory(), "");
        } catch (IndexingException ex) {
            ex.printStackTrace();
            throw new CstlServiceException("IndexingException in LuceneObservationFilter constructor", ex, NO_APPLICABLE_CODE);
        }
    }
    
    @Override
    public void initFilterObservation(ResponseModeType requestMode, QName resultModel) {
        if (resultModel.equals(Parameters.MEASUREMENT_QNAME)) {
            luceneRequest = new StringBuilder("type:measurement ");
        } else {
            luceneRequest = new StringBuilder("type:observation ");
        }
    }

    @Override
    public void initFilterGetResult(String procedure, QName resultModel) {
        if (resultModel.equals(Parameters.MEASUREMENT_QNAME)) {
            luceneRequest = new StringBuilder("type:measurement AND procedure:\"" + procedure + "\" ");
        } else {
            luceneRequest = new StringBuilder("type:observation AND procedure:\"" + procedure + "\" ");
        }
    }

    @Override
    public void setProcedure(List<String> procedures, ObservationOfferingEntry off) {
        luceneRequest.append(" ( ");
        if (procedures.size() != 0) {

            for (String s : procedures) {
                if (s != null) {
                    String dbId = map.getProperty(s);
                    if (dbId == null) {
                        dbId = s;
                    }
                    luceneRequest.append(" procedure:\"").append(dbId).append("\" OR ");
                }
            }
        } else {
            //if is not specified we use all the process of the offering
            for (ReferenceEntry proc : off.getProcedure()) {
                luceneRequest.append(" procedure:\"").append(proc.getHref()).append("\" OR ");
            }
        }
        luceneRequest.delete(luceneRequest.length() - 3, luceneRequest.length());
        luceneRequest.append(") ");
    }

    @Override
    public void setObservedProperties(List<String> phenomenon, List<String> compositePhenomenon) {
        luceneRequest.append(" AND( ");
        for (String p : phenomenon) {
            luceneRequest.append(" observed_property:").append(p).append(" OR ");

        }
        for (String p : compositePhenomenon) {
            luceneRequest.append(" observed_property:").append(p).append(" OR ");
        }
        luceneRequest.delete(luceneRequest.length() - 3, luceneRequest.length());
        luceneRequest.append(") ");
    }

    @Override
    public void setFeatureOfInterest(List<String> fois) {
        luceneRequest.append(" AND (");
        for (String foi : fois) {
            luceneRequest.append("feature_of_interest:").append(foi).append(" OR");
        }
        luceneRequest.delete(luceneRequest.length() - 3, luceneRequest.length());
        luceneRequest.append(") ");
    }

    @Override
    public void setTimeEquals(Object time) throws CstlServiceException {
        if (time instanceof TimePeriodType) {
            final TimePeriodType tp = (TimePeriodType) time;
            final String begin      = getTimeValue(tp.getBeginPosition());
            final String end        = getTimeValue(tp.getEndPosition());

            // we request directly a multiple observation or a period observation (one measure during a period)
            luceneRequest.append("AND (");
            luceneRequest.append(" sampling_time_begin:'").append(begin).append("' AND ");
            luceneRequest.append(" sampling_time_end:'").append(end).append("') ");

        // if the temporal object is a timeInstant
        } else if (time instanceof TimeInstantType) {
            final TimeInstantType ti = (TimeInstantType) time;
            final String position    = getTimeValue(ti.getTimePosition());
            luceneRequest.append("AND (");

            // case 1 a single observation
            luceneRequest.append("(sampling_time_begin:'").append(position).append("' AND sampling_time_end:NULL)");
            luceneRequest.append(" OR ");

            //case 2 multiple observations containing a matching value
            luceneRequest.append("(sampling_time_begin[19700000 ").append(position).append("] ").append(" AND sampling_time_end: [").append(position).append("30000000]))");

        } else {
            throw new CstlServiceException("TM_Equals operation require timeInstant or TimePeriod!",
                    INVALID_PARAMETER_VALUE, Parameters.EVENT_TIME);
        }
    }

    @Override
    public void setTimeBefore(Object time) throws CstlServiceException {
        // for the operation before the temporal object must be an timeInstant
        if (time instanceof TimeInstantType) {
            final TimeInstantType ti = (TimeInstantType) time;
            final String position    = getTimeValue(ti.getTimePosition());
            luceneRequest.append("AND (");

            // the single and multpile observations whitch begin after the bound
            luceneRequest.append("(sampling_time_begin: [19700000 ").append(position).append("]))");

        } else {
            throw new CstlServiceException("TM_Before operation require timeInstant!",
                    INVALID_PARAMETER_VALUE, Parameters.EVENT_TIME);
        }
    }

    @Override
    public void setTimeAfter(Object time) throws CstlServiceException {
        // for the operation after the temporal object must be an timeInstant
        if (time instanceof TimeInstantType) {
            final TimeInstantType ti = (TimeInstantType) time;
            final String position    = getTimeValue(ti.getTimePosition());
            luceneRequest.append("AND (");

            // the single and multpile observations whitch begin after the bound
            luceneRequest.append("(sampling_time_begin:[").append(position).append(" 30000000])");
            luceneRequest.append(" OR ");
            // the multiple observations overlapping the bound
            luceneRequest.append("(sampling_time_begin: [19700000 ").append(position).append("] AND sampling_time_end:[").append(position).append(" 30000000]))");


        } else {
            throw new CstlServiceException("TM_After operation require timeInstant!",
                    INVALID_PARAMETER_VALUE, Parameters.EVENT_TIME);
        }
    }

    @Override
    public void setTimeDuring(Object time) throws CstlServiceException {
        if (time instanceof TimePeriodType) {
            final TimePeriodType tp = (TimePeriodType) time;
            final String begin      = getTimeValue(tp.getBeginPosition());
            final String end        = getTimeValue(tp.getEndPosition());
            luceneRequest.append("AND (");

            // the multiple observations included in the period
            luceneRequest.append(" (sampling_time_begin:[").append(begin).append(" 30000000] AND sampling_time_end:[19700000 ").append(end).append("])");
            luceneRequest.append(" OR ");
            // the single observations included in the period
            luceneRequest.append(" (sampling_time_begin:[").append(begin).append(" 30000000] AND sampling_time_begin:[19700000 ").append(end).append("] AND sampling_time_end IS NULL)");
            luceneRequest.append(" OR ");
            // the multiple observations whitch overlaps the first bound
            luceneRequest.append(" (sampling_time_begin:[19700000 ").append(begin).append("] AND sampling_time_end:[19700000 ").append(end).append("] AND sampling_time_end:[").append(begin).append(" 30000000])");
            luceneRequest.append(" OR ");
            // the multiple observations whitch overlaps the second bound
            luceneRequest.append(" (sampling_time_begin:[").append(begin).append(" 30000000] AND sampling_time_end:[").append(end).append(" 30000000] AND sampling_time_begin:[19700000 ").append(end).append("])");
            luceneRequest.append(" OR ");
            // the multiple observations whitch overlaps the whole period
            luceneRequest.append(" (sampling_time_begin:[19700000 ").append(begin).append("] AND sampling_time_end:[").append(end).append(" 30000000]))");


        } else {
            throw new CstlServiceException("TM_During operation require TimePeriod!",
                    INVALID_PARAMETER_VALUE, Parameters.EVENT_TIME);
        }
    }

    @Override
    public List<ObservationResult> filterResult() throws CstlServiceException {
        try {
            searcher.doSearch(new SpatialQuery(luceneRequest.toString()));
            return null;
        } catch(SearchingException ex) {
            throw new CstlServiceException("Search exception while filtering the obsevation", ex, NO_APPLICABLE_CODE);
        }
    }

    @Override
    public List<String> filterObservation() throws CstlServiceException {
        try {
            return searcher.doSearch(new SpatialQuery(luceneRequest.toString()));
        } catch(SearchingException ex) {
            throw new CstlServiceException("Search exception while filtering the obsevation", ex, NO_APPLICABLE_CODE);
        }
    }

    public String getInfos() {
        return "Constellation Lucene O&M Filter 0.4";
    }

    public boolean isBoundedObservation() {
        return false;
    }

    public void setBoundingBox(EnvelopeEntry e) throws CstlServiceException {
        throw new CstlServiceException("SetBoundingBox is not supported by this ObservationFilter implementation.");
    }

}
