
package net.sourceforge.pmd.util.datasource;

import java.io.IOException;
import java.io.InputStream;


public interface DataSource {
    
    public InputStream getInputStream() throws IOException;

    
    public String getNiceFileName(boolean shortNames, String inputFileName);
}
