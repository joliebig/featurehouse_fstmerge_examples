
package net.sourceforge.pmd.cpd;

import java.io.IOException;

public interface Tokenizer {
    void tokenize(SourceCode sourceCode, Tokens tokenEntries) throws IOException;
}
