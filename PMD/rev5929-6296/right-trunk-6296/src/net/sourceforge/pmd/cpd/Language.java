
package net.sourceforge.pmd.cpd;

import java.io.FilenameFilter;

public interface Language {

    String fileSeparator = System.getProperty("file.separator");

    Tokenizer getTokenizer();

    FilenameFilter getFileFilter();
}
