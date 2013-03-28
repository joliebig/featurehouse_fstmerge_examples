
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.SourceFileSelector;

import java.io.File;
import java.io.FilenameFilter;


public class SourceFileOrDirectoryFilter implements FilenameFilter {
	
    private SourceFileSelector fileSelector;

    private static final String fileSeparator = System.getProperty("file.separator");
    
    
    public SourceFileOrDirectoryFilter(SourceFileSelector fileSelector) {
        this.fileSelector = fileSelector;
    }

    public boolean accept(File dir, String filename) {
        return (fileSelector.isWantedFile(filename) || (new File(dir.getAbsolutePath() + fileSeparator + filename).isDirectory())) && !filename.equals("SCCS");

    }
}