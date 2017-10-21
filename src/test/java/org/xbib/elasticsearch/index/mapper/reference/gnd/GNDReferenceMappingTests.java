package org.xbib.elasticsearch.index.mapper.reference.gnd;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchAction;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;
import org.xbib.elasticsearch.NodeTestUtils;

import java.io.IOException;
import java.io.InputStreamReader;

import static org.elasticsearch.common.io.Streams.copyToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class GNDReferenceMappingTests extends NodeTestUtils {

    private static final Logger logger = LogManager.getLogger(GNDReferenceMappingTests.class.getName());

    @Test
    public void testGND() throws IOException {
        startCluster();
        try {
            try {
                client().admin().indices().prepareDelete("title", "gnd").execute().actionGet();
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
            String gndSettings = copyToStringFromClasspath("gnd-settings.json");
            String gndMapping = copyToStringFromClasspath("gnd-mapping.json");
            client().admin().indices().prepareCreate("gnd")
                    .setSettings(gndSettings)
                    .addMapping("gnd", gndMapping)
                    .execute().actionGet();
            String gndDocument = copyToStringFromClasspath("gnd-document.json");
            client().prepareIndex("gnd", "gnd", "11862444X")
                    .setSource(gndDocument)
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                    .execute().actionGet();

            String titleSettings = copyToStringFromClasspath("title-settings.json");
            String titleMapping = copyToStringFromClasspath("title-mapping.json");
            client().admin().indices().prepareCreate("title")
                    .setSettings(titleSettings)
                    .addMapping("title", titleMapping)
                    .execute().actionGet();
            client().prepareIndex("title", "title", "(DE-605)008427902")
                    .setSource(copyToStringFromClasspath("title-document-1.json"))
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                    .execute().actionGet();
            client().prepareIndex("title", "title", "(DE-605)017215715")
                    .setSource(copyToStringFromClasspath("title-document-2.json"))
                    .setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE)
                    .execute().actionGet();

            SearchRequestBuilder searchRequestBuilder = new SearchRequestBuilder(client(), SearchAction.INSTANCE)
                    .setIndices("title")
                    .setTypes("title")
                    .setQuery(QueryBuilders.matchPhraseQuery("bib.namePersonal", "Tucholsky, Kurt"));

            SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

            logger.info("hits = {}", searchResponse.getHits().getTotalHits());
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                logger.info("kurt tucholsky = {}", hit.getSource());
            }
            assertEquals(1, searchResponse.getHits().getTotalHits());

            searchRequestBuilder = new SearchRequestBuilder(client(), SearchAction.INSTANCE)
                    .setIndices("title")
                    .setTypes("title")
                    .setQuery(QueryBuilders.matchPhraseQuery("bib.namePersonal", "Panter, Peter"));
            searchResponse = searchRequestBuilder.execute().actionGet();
            logger.info("hits = {}", searchResponse.getHits().getTotalHits());
            assertTrue(searchResponse.getHits().getTotalHits() > 0);
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                logger.info("peter panter = {}", hit.getSource());
            }
            assertEquals(1, searchResponse.getHits().getTotalHits());

            searchRequestBuilder = new SearchRequestBuilder(client(), SearchAction.INSTANCE)
                    .setIndices("title")
                    .setTypes("title")
                    .setQuery(QueryBuilders.matchQuery("bib.namePersonal", "Panter, Peter"))
                    .setExplain(true);
            searchResponse = searchRequestBuilder.execute().actionGet();
            logger.info("hits = {}", searchResponse.getHits().getTotalHits());
            assertTrue(searchResponse.getHits().getTotalHits() > 0);
            for (SearchHit hit : searchResponse.getHits().getHits()) {
                logger.info("schroeder = {}", hit.getSource());
                logger.info(hit.getExplanation().toString());
            }
            assertEquals(1, searchResponse.getHits().getTotalHits());

            try {
                client().admin().indices().prepareDelete("title", "gnd").execute().actionGet();
            } catch (Exception e) {
                logger.warn(e.getMessage());
            }
        } finally {
            stopCluster();
        }
    }

    private String copyToStringFromClasspath(String path) throws IOException {
        return copyToString(new InputStreamReader(getClass().getResource(path).openStream(), "UTF-8"));
    }
}
