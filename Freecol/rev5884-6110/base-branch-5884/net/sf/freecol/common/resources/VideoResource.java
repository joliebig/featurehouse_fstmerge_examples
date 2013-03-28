

package net.sf.freecol.common.resources;

import java.net.URL;
import java.util.logging.Logger;

import net.sf.freecol.client.gui.video.Video;


public class VideoResource extends Resource {
    private static final Logger logger = Logger.getLogger(VideoResource.class.getName());

    private final Video video;
    
    
    VideoResource(URL resourceLocator) {
        super(resourceLocator);
        
        this.video = new Video(resourceLocator);
    }
    
    
    
    public Video getVideo() {
        return video;
    }
}
