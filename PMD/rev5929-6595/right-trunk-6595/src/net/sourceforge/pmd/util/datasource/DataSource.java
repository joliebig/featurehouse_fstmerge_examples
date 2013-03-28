
package net.sourceforge.pmd.util.datasource;

import java.io.IOException;
import java.io.InputStream;


public interface DataSource {
    
    InputStream getInputStream() throws IOException;

    
    String getNiceFileName(boolean shortNames, String inputFileName);
}
