

package net.sf.freecol.common.resources;

import java.net.URL;


public abstract class Resource {

    
    private final URL resourceLocator;
    
    
    
    Resource(URL resourceLocator) {
        this.resourceLocator = resourceLocator;
    }
    
    
    
    public URL getResourceLocator() {
        return resourceLocator;
    }
}
