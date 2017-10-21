package org.xbib.elasticsearch.index.analysis.german;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.xbib.elasticsearch.MapperTestUtils.tokenFilterFactory;
import static org.xbib.elasticsearch.MapperTestUtils.tokenizerFactory;

/**
 *
 */
public class GermanNormalizationTests extends Assert {

    @Test
    public void testGerman1() throws IOException {

        String source = "Ein schöner Tag in Köln im Café an der Straßenecke";

        String[] expected = {
            "Ein",
            "schoner",
            "Tag",
            "in",
            "Koln",
            "im",
            "Café",
            "an",
            "der",
            "Strassenecke"
        };
        String resource = "org/xbib/elasticsearch/index/analysis/german/german_normalization_analysis.json";
        TokenFilterFactory tokenFilter = tokenFilterFactory(resource, "umlaut");
        Tokenizer tokenizer = tokenizerFactory(resource, "standard").create();
        tokenizer.setReader(new StringReader(source));
        assertSimpleTSOutput(tokenFilter.create(tokenizer), expected);
    }

    private void assertSimpleTSOutput(TokenStream stream, String[] expected) throws IOException {
        stream.reset();
        CharTermAttribute termAttr = stream.getAttribute(CharTermAttribute.class);
        assertNotNull(termAttr);
        int i = 0;
        while (stream.incrementToken()) {
            assertTrue(i < expected.length);
            assertEquals(expected[i], termAttr.toString());
            i++;
        }
        assertEquals(i, expected.length);
        stream.close();
    }
}