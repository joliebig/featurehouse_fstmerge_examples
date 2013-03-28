

package net.sf.freecol.client.gui.video;

import java.net.URL;


public class Video {

    private URL url;
    
    
    public Video(URL url) {
        this.url = url;
    }
    
    URL getURL() {
        return url;
    }
}
