
package net.sourceforge.pmd.cpd;

import java.io.IOException;

public interface Tokenizer {
    void tokenize(SourceCode tokens, Tokens tokenEntries) throws IOException;
}
