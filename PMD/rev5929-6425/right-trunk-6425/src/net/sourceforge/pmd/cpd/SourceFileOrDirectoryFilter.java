
package net.sourceforge.pmd.cpd;

import net.sourceforge.pmd.SourceFileSelector;

import java.io.File;
import java.io.FilenameFilter;


public class SourceFileOrDirectoryFilter implements FilenameFilter {
	
    private SourceFileSelector fileSelector;

    private static final String FILE_SEPARATOR = System.getProperty("file.separator");
    
    
    public SourceFileOrDirectoryFilter(SourceFileSelector fileSelector) {
        this.fileSelector = fileSelector;
    }

    public boolean accept(File dir, String filename) {
        return (fileSelector.isWantedFile(filename) || (new File(dir.getAbsolutePath() + FILE_SEPARATOR + filename).isDirectory())) && !filename.equals("SCCS");

    }
}