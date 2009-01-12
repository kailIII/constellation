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

package org.constellation.metadata.index.mdweb;

// J2SE dependencies
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Apache Lucene dependencies
import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

// Constellation dependencies
import org.constellation.metadata.index.AbstractIndexSearcher;
import org.constellation.ws.WebServiceException;

/**
 *
 * @author Guilhem Legal
 */
public class MDWebIndexSearcher extends AbstractIndexSearcher {

    public MDWebIndexSearcher(File configDir, String serviceID) throws WebServiceException {
        super(configDir, serviceID);
    }

    /**
     * This method proceed a lucene search and returns a list of ID.
     *
     * @param query A simple Term query.
     *
     * @return      A List of id.
     */
    public String identifierQuery(String id) throws CorruptIndexException, IOException, ParseException {

        TermQuery query      = new TermQuery(new Term("identifier_sort", id));
        List<String> results = new ArrayList<String>();
        initSearcher();
        int maxRecords       = searcher.maxDoc();
        logger.info("TermQuery: " + query.toString());
        TopDocs hits = searcher.search(query, maxRecords);

        for (ScoreDoc doc :hits.scoreDocs) {
            Document document = searcher.doc(doc.doc, new IDFieldSelector());
            results.add(document.get("id") + ':' + document.get("catalog"));
        }
        if (results.size() > 1)
            logger.warning("multiple record in lucene index for identifier: " + id);

        if (results.size() > 0)
            return results.get(0);
        else
            return null;
    }

    @Override
    public String getMatchingID(Document doc) throws CorruptIndexException, IOException, org.apache.lucene.queryParser.ParseException {
        return doc.get("id") + ':' + doc.get("catalog");
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    private class IDFieldSelector implements FieldSelector {

        public FieldSelectorResult accept(String fieldName) {
            if (fieldName != null) {
                if (fieldName.equals("id") || fieldName.equals("catalog")) {
                    return FieldSelectorResult.LOAD;
                } else {
                    return FieldSelectorResult.NO_LOAD;
                }
            }
            return FieldSelectorResult.NO_LOAD;
        }
    }
}
