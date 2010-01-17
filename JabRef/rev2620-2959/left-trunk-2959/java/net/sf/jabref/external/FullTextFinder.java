package net.sf.jabref.external;

import java.net.URL;
import java.io.IOException;


public interface FullTextFinder {

    
    public boolean supportsSite(URL url);

       
    public URL findFullTextURL(URL url) throws IOException;
}
