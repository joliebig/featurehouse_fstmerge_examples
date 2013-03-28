
package net.sourceforge.pmd.cpd;

import java.io.FilenameFilter;

public interface Language {

    Tokenizer getTokenizer();

    FilenameFilter getFileFilter();
}
