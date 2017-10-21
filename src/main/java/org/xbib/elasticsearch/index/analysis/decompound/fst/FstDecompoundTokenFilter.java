package org.xbib.elasticsearch.index.analysis.decompound.fst;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.util.LinkedList;

/**
 *
 */
public class FstDecompoundTokenFilter extends TokenFilter {

    protected final LinkedList<DecompoundToken> tokens;

    protected final FstDecompounder decomp;

    protected final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

    protected final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);

    private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);

    private State current;

    protected FstDecompoundTokenFilter(TokenStream input, FstDecompounder decomp) {
        super(input);
        this.tokens = new LinkedList<>();
        this.decomp = decomp;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!tokens.isEmpty()) {
            if (current == null) {
                throw new IllegalArgumentException("current is null");
            }
            DecompoundToken token = tokens.removeFirst();
            restoreState(current);
            termAtt.setEmpty().append(token.txt);
            offsetAtt.setOffset(token.startOffset, token.endOffset);
            posIncAtt.setPositionIncrement(0);
            return true;
        }
        if (input.incrementToken()) {
            decompound();
            if (!tokens.isEmpty()) {
                current = captureState();
            }
            return true;
        } else {
            return false;
        }
    }

    protected void decompound() {
        int start = offsetAtt.startOffset();
        CharSequence term = new String(termAtt.buffer(), 0, termAtt.length());
        for (String s : decomp.decompound(term.toString())) {
            int len = s.length();
            tokens.add(new DecompoundToken(s, start, len));
            start += len;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokens.clear();
        current = null;
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof FstDecompoundTokenFilter &&
                decomp.equals( ((FstDecompoundTokenFilter)object).decomp);
    }

    @Override
    public int hashCode() {
        return decomp.hashCode();
    }

    protected class DecompoundToken {

        public final CharSequence txt;
        public final int startOffset;
        public final int endOffset;

        public DecompoundToken(CharSequence txt, int offset, int length) {
            this.txt = txt;
            int startOff = FstDecompoundTokenFilter.this.offsetAtt.startOffset();
            int endOff = FstDecompoundTokenFilter.this.offsetAtt.endOffset();
            if (endOff - startOff != FstDecompoundTokenFilter.this.termAtt.length()) {
                this.startOffset = startOff;
                this.endOffset = endOff;
            } else {
                this.startOffset = offset;
                this.endOffset = offset + length;
            }
        }
    }
}
