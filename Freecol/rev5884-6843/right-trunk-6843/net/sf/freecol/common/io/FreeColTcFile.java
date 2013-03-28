

package net.sf.freecol.common.io;

import java.io.File;
import java.io.IOException;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.FreeColObject;
import net.sf.freecol.common.model.Map.Direction;
import net.sf.freecol.common.model.ResourceType;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.resources.ResourceFactory;
import net.sf.freecol.common.resources.ResourceManager;
import net.sf.freecol.common.resources.ResourceMapping;


public class FreeColTcFile extends FreeColModFile {
    
    
    public FreeColTcFile(final String id) {
        super(id, new File(FreeCol.getDataDirectory(), id));
    }

    
    @Override
    public ResourceMapping getResourceMapping() {
        
        ResourceMapping result;
        try {
            final ModDescriptor info = getModDescriptor();
            if (info.getParent() != null) {
                final FreeColTcFile parentTcData = new FreeColTcFile(info.getParent());
                result = parentTcData.getResourceMapping();
                
            } else {
                result = new ResourceMapping();
            }
            result.addAll(createRiverMapping());
            result.addAll(createBeachMapping());
            result.addAll(super.getResourceMapping());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    
    @Override
    protected String[] getFileEndings() {
        return new String[] {".ftc", ".zip"};
    }


    private String getShortId(FreeColObject object) {
        return object.getId().substring(object.getId().lastIndexOf('.') + 1);
    }


    

    public ResourceMapping createRiverMapping() {
        ResourceMapping map = new ResourceMapping();
        String pathPrefix = "resources/images/river/";
        String key, path;
        for (int index = 0; index < ResourceManager.RIVER_STYLES; index++) {
            path = pathPrefix +"river" + index + ".png";
            map.add("river" + index, ResourceFactory.createResource(getURI(path)));
        }
        for (Direction d : Direction.longSides) {
            key = "delta_" + d + "_small";
            path = pathPrefix + key + ".png";
            map.add(key, ResourceFactory.createResource(getURI(path)));
            key = "delta_" + d + "_large";
            path = pathPrefix + key + ".png";
            map.add(key, ResourceFactory.createResource(getURI(path)));
        }
        return map;
    }

    public ResourceMapping createBeachMapping() {
        ResourceMapping map = new ResourceMapping();
        String pathPrefix = "resources/images/terrain/beach/";
        String key, path;
        
        for (int index = 1; index < ResourceManager.BEACH_STYLES; index++) {
            path = pathPrefix +"beach" + index + ".png";
            map.add("beach" + index, ResourceFactory.createResource(getURI(path)));
        }
        return map;
    }

}
