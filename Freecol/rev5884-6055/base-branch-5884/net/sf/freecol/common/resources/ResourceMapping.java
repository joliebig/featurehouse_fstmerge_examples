

package net.sf.freecol.common.resources;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class ResourceMapping {

    
    protected Map<String, Resource> resources;

    
    
    public ResourceMapping() {
        resources = new HashMap<String, Resource>();
    }
    
    
    
    public void add(String id, Resource value) {
        resources.put(id, value);
    }
    
    
    public void addAll(ResourceMapping rc) {
        if (rc == null) {
            return;
        }
        Map<String, Resource> map = rc.getResources();
        for (String key : map.keySet()) {
            resources.put(key, map.get(key));
        }
    }

    
    public Map<String, Resource> getResources() {
        return Collections.unmodifiableMap(resources);
    }
    
    
    public Resource get(String id) {
        return resources.get(id);
    }
}