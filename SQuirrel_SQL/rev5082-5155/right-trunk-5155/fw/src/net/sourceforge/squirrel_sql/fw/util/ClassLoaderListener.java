
package net.sourceforge.squirrel_sql.fw.util;

public interface ClassLoaderListener {

    public void loadedZipFile(String filename);
        
    public void finishedLoadingZipFiles();
    
}
