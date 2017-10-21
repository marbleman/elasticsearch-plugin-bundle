package org.xbib.elasticsearch.index.analysis.sortform;

import com.ibm.icu.text.Collator;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.index.analysis.CharFilterFactory;
import org.elasticsearch.index.analysis.CustomAnalyzer;
import org.elasticsearch.index.analysis.CustomAnalyzerProvider;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.xbib.elasticsearch.index.analysis.icu.IcuCollationKeyAnalyzerProvider;
import org.xbib.elasticsearch.index.analysis.icu.IcuCollationAttributeFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 */
public class SortformAnalyzerProvider extends CustomAnalyzerProvider {

    private final Settings analyzerSettings;

    private final TokenizerFactory tokenizerFactory;

    private CustomAnalyzer customAnalyzer;

    public SortformAnalyzerProvider(IndexSettings indexSettings, Environment environment, String name,
                                    Settings settings) {
        super(indexSettings, name, settings);
        this.tokenizerFactory = new SortformTokenizerFactory(indexSettings, name, settings);
        this.analyzerSettings = settings;
    }

    @Override
    public void build(final Map<String, TokenizerFactory> tokenizers,
                      final Map<String, CharFilterFactory> charFilters,
                      final Map<String, TokenFilterFactory> tokenFilters) {
        List<CharFilterFactory> myCharFilters = new ArrayList<>();
        String[] charFilterNames = analyzerSettings.getAsArray("char_filter");
        for (String charFilterName : charFilterNames) {
            CharFilterFactory charFilter = charFilters.get(charFilterName);
            if (charFilter == null) {
                throw new IllegalArgumentException("Sortform Analyzer [" + name() + "] failed to find char_filter under name [" + charFilterName + "]");
            }
            myCharFilters.add(charFilter);
        }
        List<TokenFilterFactory> myTokenFilters = new ArrayList<>();
        String[] tokenFilterNames = analyzerSettings.getAsArray("filter");
        for (String tokenFilterName : tokenFilterNames) {
            TokenFilterFactory tokenFilter = tokenFilters.get(tokenFilterName);
            if (tokenFilter == null) {
                throw new IllegalArgumentException("Sortform Analyzer [" + name() + "] failed to find filter under name [" + tokenFilterName + "]");
            }
            myTokenFilters.add(tokenFilter);
        }
        int positionOffsetGap = analyzerSettings.getAsInt("position_offset_gap", 0);
        int offsetGap = analyzerSettings.getAsInt("offset_gap", -1);
        this.customAnalyzer = new CustomAnalyzer(tokenizerFactory,
                myCharFilters.toArray(new CharFilterFactory[myCharFilters.size()]),
                myTokenFilters.toArray(new TokenFilterFactory[myTokenFilters.size()]),
                positionOffsetGap,
                offsetGap
        );
    }

    @Override
    public CustomAnalyzer get() {
        return this.customAnalyzer;
    }

    class SortformTokenizerFactory extends AbstractTokenizerFactory {

        IcuCollationAttributeFactory factory;

        int bufferSize;

        SortformTokenizerFactory(IndexSettings indexSettings, String name, Settings settings) {
            super(indexSettings, name, settings);
            Collator collator = IcuCollationKeyAnalyzerProvider.createCollator(settings);
            factory = new IcuCollationAttributeFactory(collator);
            bufferSize = settings.getAsInt("bufferSize", 256);
        }

        @Override
        public Tokenizer create() {
            return new KeywordTokenizer(factory, bufferSize);
        }
    }
}
