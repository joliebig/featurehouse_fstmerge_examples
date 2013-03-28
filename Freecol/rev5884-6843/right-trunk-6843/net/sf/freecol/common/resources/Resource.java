

package net.sf.freecol.common.resources;

import java.net.URI;


public abstract class Resource {

    
    private final URI resourceLocator;
    
    protected Resource() {
        
        resourceLocator = null;
    }
    
    
    Resource(URI resourceLocator) {
        this.resourceLocator = resourceLocator;
    }
    
    
    
    public URI getResourceLocator() {
        return resourceLocator;
    }
}
