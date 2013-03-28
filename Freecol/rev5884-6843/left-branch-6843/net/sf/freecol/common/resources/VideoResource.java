

package net.sf.freecol.common.resources;

import java.net.URI;
import java.util.logging.Logger;

import net.sf.freecol.client.gui.video.Video;


public class VideoResource extends Resource {
    private static final Logger logger = Logger.getLogger(VideoResource.class.getName());

    private final Video video;
    
    
    VideoResource(URI resourceLocator) throws Exception {
        super(resourceLocator);
        
        this.video = new Video(resourceLocator.toURL());
    }
    
    
    
    public Video getVideo() {
        return video;
    }
}
