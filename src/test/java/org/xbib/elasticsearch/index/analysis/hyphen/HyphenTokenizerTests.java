package org.xbib.elasticsearch.index.analysis.hyphen;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.xbib.elasticsearch.MapperTestUtils.analyzer;
import static org.xbib.elasticsearch.MapperTestUtils.tokenFilterFactory;
import static org.xbib.elasticsearch.MapperTestUtils.tokenizerFactory;

/**
 *
 */
public class HyphenTokenizerTests extends Assert {

    @Test
    public void testOne() throws IOException {

        String source = "Das ist ein Bindestrich-Wort.";

        String[] expected = {
                "Das",
                "ist",
                "ein",
                "Bindestrich-Wort",
                "BindestrichWort",
                "Wort",
                "Bindestrich"
        };
        String resource = "org/xbib/elasticsearch/index/analysis/hyphen/hyphen_tokenizer.json";
        Tokenizer tokenizer = tokenizerFactory(resource, "my_hyphen_tokenizer").create();
        tokenizer.setReader(new StringReader(source));
        TokenFilterFactory tokenFilter = tokenFilterFactory(resource,"hyphen");
        TokenStream tokenStream = tokenFilter.create(tokenizer);
        assertSimpleTSOutput(tokenStream, expected);
    }

    @Test
    public void testTwo() throws IOException {

        String source = "Das E-Book muss dringend zum Buchbinder.";

        String[] expected = {
                "Das",
                "E-Book",
                "EBook",
                "Book",
                "muss",
                "dringend",
                "zum",
                "Buchbinder"
        };
        String resource = "org/xbib/elasticsearch/index/analysis/hyphen/hyphen_tokenizer.json";
        Tokenizer tokenizer = tokenizerFactory(resource,"my_icu_tokenizer").create();
        tokenizer.setReader(new StringReader(source));
        TokenFilterFactory tokenFilter = tokenFilterFactory(resource,"hyphen");
        assertSimpleTSOutput(tokenFilter.create(tokenizer), expected);
    }

    @Test
    public void testThree() throws IOException {

        String source = "Ich will nicht als Service-Center-Mitarbeiterin, sondern 100-prozentig als Dipl.-Ing. arbeiten!";

        String[] expected = {
                "Ich",
                "will",
                "nicht",
                "als",
                "Service-Center-Mitarbeiterin",
                "ServiceCenterMitarbeiterin",
                "Mitarbeiterin",
                "ServiceCenter",
                "ServiceCenter-Mitarbeiterin",
                "Center-Mitarbeiterin",
                "Service",
                "sondern",
                "100-prozentig",
                "als",
                "Dipl",
                "Ing",
                "arbeiten"
        };
        String resource = "org/xbib/elasticsearch/index/analysis/hyphen/hyphen_tokenizer.json";
        Tokenizer tokenizer = tokenizerFactory(resource,"my_hyphen_tokenizer").create();
        tokenizer.setReader(new StringReader(source));
        TokenFilterFactory tokenFilter = tokenFilterFactory(resource,"hyphen");
        assertSimpleTSOutput(tokenFilter.create(tokenizer), expected);
    }

    @Test
    public void testFour() throws IOException {

        String source = "So wird's was: das Elasticsearch-Buch erscheint beim O'Reilly-Verlag.";

        String[] expected = {
                "So",
                "wird's",
                "was",
                "das",
                "Elasticsearch-Buch",
                "ElasticsearchBuch",
                "Buch",
                "Elasticsearch",
                "erscheint",
                "beim",
                "O'Reilly-Verlag"
        };
        String resource = "org/xbib/elasticsearch/index/analysis/hyphen/hyphen_tokenizer.json";
        Tokenizer tokenizer = tokenizerFactory(resource,"my_hyphen_tokenizer").create();
        tokenizer.setReader(new StringReader(source));
        TokenFilterFactory tokenFilter = tokenFilterFactory(resource,"hyphen");
        assertSimpleTSOutput(tokenFilter.create(tokenizer), expected);
    }


    @Test
    public void testFive() throws IOException {

        String source = "978-1-4493-5854-9";

        String[] expected = {
                "978-1-4493-5854-9"
        };

        String resource = "org/xbib/elasticsearch/index/analysis/hyphen/hyphen_tokenizer.json";
        Tokenizer tokenizer = tokenizerFactory(resource,"my_hyphen_tokenizer").create();
        tokenizer.setReader(new StringReader(source));
        TokenFilterFactory tokenFilter = tokenFilterFactory(resource,"hyphen");
        assertSimpleTSOutput(tokenFilter.create(tokenizer), expected);
    }

    @Test
    public void testSix() throws IOException {

        String source = "E-Book";

        String[] expected = {
                "E-Book",
                "EBook",
                "Book"
        };

        String resource = "org/xbib/elasticsearch/index/analysis/hyphen/hyphen_tokenizer.json";
        Tokenizer tokenizer = tokenizerFactory(resource,"my_hyphen_tokenizer").create();
        tokenizer.setReader(new StringReader(source));
        TokenFilterFactory tokenFilter = tokenFilterFactory(resource,"hyphen");
        assertSimpleTSOutput(tokenFilter.create(tokenizer), expected);
    }

    @Test
    public void testSeven() throws IOException {
        String source = "Procter & Gamble ist Procter&Gamble. Schwarz - weiss ist schwarz-weiss";

        String[] expected = {
                "Procter",
                "Gamble",
                "ist",
                "Procter&Gamble",
                "Schwarz",
                "weiss",
                "ist",
                "schwarz-weiss",
                "schwarzweiss",
                "weiss",
                "schwarz"
        };

        String resource = "org/xbib/elasticsearch/index/analysis/hyphen/hyphen_tokenizer.json";
        Tokenizer tokenizer = tokenizerFactory(resource,"my_hyphen_tokenizer").create();
        tokenizer.setReader(new StringReader(source));
        TokenFilterFactory tokenFilter = tokenFilterFactory(resource,"hyphen");
        assertSimpleTSOutput(tokenFilter.create(tokenizer), expected);
    }

    @Test
    public void testEight() throws IOException {

        String source = "Ich will nicht als Service-Center-Mitarbeiterin mit C++, sondern 100-prozentig als Dipl.-Ing. arbeiten!";

        String[] expected = {
                "Ich",
                "will",
                "nicht",
                "als",
                "Service-Center-Mitarbeiterin",
                "ServiceCenterMitarbeiterin",
                "mit",
                "C++",
                "sondern",
                "100-prozentig",
                "100prozentig",
                "als",
                "Dipl",
                "Ing",
                "arbeiten"
        };
        String resource = "org/xbib/elasticsearch/index/analysis/hyphen/hyphen_tokenizer_without_subwords.json";
        Tokenizer tokenizer = tokenizerFactory(resource, "my_hyphen_tokenizer").create();
        tokenizer.setReader(new StringReader(source));
        TokenFilterFactory tokenFilter = tokenFilterFactory(resource,"my_hyphen_tokenfilter");
        assertSimpleTSOutput(tokenFilter.create(tokenizer), expected);
    }


    @Test
    public void testNine() throws IOException {

        String source = "Das ist ein Punkt. Und noch ein Punkt für U.S.A. Oder? Nicht doch.";

        String[] expected = {
                "Das",
                "ist",
                "ein",
                "Punkt",
                "Und",
                "noch",
                "ein",
                "Punkt",
                "für",
                "U.S.A",
                "Oder",
                "Nicht",
                "doch"

        };
        String resource = "org/xbib/elasticsearch/index/analysis/hyphen/hyphen_tokenizer_without_subwords.json";
        Tokenizer tokenizer = tokenizerFactory(resource,"my_hyphen_tokenizer").create();
        tokenizer.setReader(new StringReader(source));
        TokenFilterFactory tokenFilter = tokenFilterFactory(resource,"my_hyphen_tokenfilter");
        assertSimpleTSOutput(tokenFilter.create(tokenizer), expected);
    }

    @Test
    public void testTen() throws IOException {

        String source = "Das ist ein Punkt. Und noch ein Punkt für U.S.A. Oder? Nicht doch.";

        String[] expected = {
                "Das",
                "ist",
                "ein",
                "Punkt",
                "Und",
                "noch",
                "ein",
                "Punkt",
                "für",
                "U.S.A",
                "Oder",
                "Nicht",
                "doch"

        };
        String resource = "org/xbib/elasticsearch/index/analysis/hyphen/hyphen_analyzer.json";
        Analyzer analyzer = analyzer(resource, "my_hyphen_analyzer");
        assertSimpleTSOutput(analyzer.tokenStream("text", new StringReader(source)), expected);
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
        assertEquals(expected.length, i);
        stream.close();
    }
}
