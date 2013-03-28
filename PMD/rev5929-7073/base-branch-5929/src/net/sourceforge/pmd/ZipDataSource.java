
package net.sourceforge.pmd;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class ZipDataSource implements DataSource {
    private ZipFile zipFile;
    private ZipEntry zipEntry;

    
    public ZipDataSource(ZipFile zipFile, ZipEntry zipEntry) {
        this.zipFile = zipFile;
        this.zipEntry = zipEntry;
    }

    public InputStream getInputStream() throws IOException {
        return zipFile.getInputStream(zipEntry);
    }

    public String getNiceFileName(boolean shortNames, String inputFileName) {
        
        return zipFile.getName() + ":" + zipEntry.getName();
    }
}
