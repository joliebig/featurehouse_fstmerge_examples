

package net.sf.freecol.common.resources;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.logging.Logger;


public class ResourceFactory {

    private static final Logger logger = Logger.getLogger(ResourceFactory.class.getName());

    
    private static Map<URI, WeakReference<Resource>> resources = new WeakHashMap<URI, WeakReference<Resource>>();
    

    
    private static Resource getResource(URI uri) {
        final WeakReference<Resource> wr = resources.get(uri);
        if (wr != null) {
            final Resource r = wr.get();
            if (r != null) {
                return r;
            }
        }
        return null;
    }
    
    
    public static Resource createResource(URI uri) {
        Resource r = getResource(uri);
        if (r == null) {
            try {
                if ("urn".equals(uri.getScheme())) {
                    if (uri.getSchemeSpecificPart().startsWith(ColorResource.SCHEME)) {
                        r = new ColorResource(uri);
                    }
                } else if (uri.getPath().endsWith(".sza")) {
                    r = new SZAResource(uri);
                } else if (uri.getPath().endsWith("video.ogg")) {
                    r = new VideoResource(uri);
                } else {
                    r = new ImageResource(uri);
                }
                resources.put(uri, new WeakReference<Resource>(r));
            } catch(Exception e) {
                logger.warning("Failed to create resource with URI " + uri);
            }
        }
        return r;
    }
}
