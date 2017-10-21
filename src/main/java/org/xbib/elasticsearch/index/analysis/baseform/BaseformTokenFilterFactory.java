package org.xbib.elasticsearch.index.analysis.baseform;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.xbib.elasticsearch.common.fsa.Dictionary;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 */
public class BaseformTokenFilterFactory extends AbstractTokenFilterFactory {

    private final boolean respectKeywords;

    private final Dictionary dictionary;

    public BaseformTokenFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
        this.respectKeywords = settings.getAsBoolean("respect_keywords", false);
        this.dictionary = createDictionary(settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new BaseformTokenFilter(tokenStream, dictionary, respectKeywords);
    }

    private Dictionary createDictionary(Settings settings) {
        try {
            String lang = settings.get("language", "de");
            String path = "/baseform/" + lang + "-lemma-utf8.txt";
            return new Dictionary().load(new InputStreamReader(getClass().getResourceAsStream(path), "UTF-8"));
        } catch (IOException e) {
            throw new ElasticsearchException("resources in settings not found: " + settings, e);
        }
    }
}
