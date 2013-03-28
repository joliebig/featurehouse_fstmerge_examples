

package net.sf.freecol.common.resources;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;


public class ResourceFactory {

    
    private static Map<URL, WeakReference<Resource>> resources = new WeakHashMap<URL, WeakReference<Resource>>();
    

    
    private static Resource getResource(URL url) {
        final WeakReference<Resource> wr = resources.get(url);
        if (wr != null) {
            final Resource r = wr.get();
            if (r != null) {
                return r;
            }
        }
        return null;
    }
    
    
    public static Resource createResource(URL url) {
        Resource r = getResource(url);
        if (r == null) {
            if (url.getPath().endsWith(".sza")) {
                r = new SZAResource(url);
            } else if (url.getPath().endsWith("video.ogg")) {
                r = new VideoResource(url);
            } else {
                r = new ImageResource(url);
            }
            resources.put(url, new WeakReference<Resource>(r));
        }
        return r;
    }
}
