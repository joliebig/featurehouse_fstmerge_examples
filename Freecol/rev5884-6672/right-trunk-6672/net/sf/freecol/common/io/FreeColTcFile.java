

package net.sf.freecol.common.io;

import java.io.File;
import java.io.IOException;

import net.sf.freecol.FreeCol;
import net.sf.freecol.common.model.FreeColObject;
import net.sf.freecol.common.model.ResourceType;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.Unit.Role;
import net.sf.freecol.common.model.UnitType;
import net.sf.freecol.common.resources.ResourceFactory;
import net.sf.freecol.common.resources.ResourceMapping;


public class FreeColTcFile extends FreeColModFile {
    
    
    public FreeColTcFile(final String id) {
        super(id, new File(FreeCol.getDataDirectory(), id));
    }

    
    @Override
    public ResourceMapping getResourceMapping() {
        
        try {
            final ModDescriptor info = getModDescriptor();
            if (info.getParent() != null) {
                final FreeColTcFile parentTcData = new FreeColTcFile(info.getParent());
                final ResourceMapping rc = parentTcData.getResourceMapping();
                
                rc.addAll(super.getResourceMapping());
                return rc;
            } else {
                return super.getResourceMapping();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    
    @Override
    protected String[] getFileEndings() {
        return new String[] {".ftc", ".zip"};
    }


    private String getShortId(FreeColObject object) {
        return object.getId().substring(object.getId().lastIndexOf('.') + 1);
    }

    public ResourceMapping getDefaultResourceMapping() throws Exception {
        Specification.createSpecification(getSpecificationInputStream());
            
        ResourceMapping map = new ResourceMapping();
        String key, value, keyPrefix, urlPrefix, urlSuffix, roleId, shortId;

        
        urlPrefix = "resources/images/bonus/";
        for (ResourceType resourceType : Specification.getSpecification().getResourceTypeList()) {
            key = resourceType.getId() + ".image";
            value = urlPrefix + getShortId(resourceType).toLowerCase() + ".png";
            map.add(key, ResourceFactory.createResource(getURL(value)));
        }

        
        String[][] attackAnimations = new String[][] {
            { ".attack.w.animation", "-attack-left.sza" },
            { ".attack.e.animation", "-attack-right.sza" }
        };

        urlPrefix = "resources/images/units/";
        for (UnitType unitType : Specification.getSpecification().getUnitTypeList()) {
            keyPrefix = unitType.getId() + ".";
            shortId = getShortId(unitType);
            urlSuffix = "/" + shortId + ".png";

            for (Role role : Role.values()) {
                
                roleId = role.getId();
                key = keyPrefix + roleId + ".image";
                value = urlPrefix + roleId + urlSuffix;
                map.add(key, ResourceFactory.createResource(getURL(value)));
                
                for (String[] animation : attackAnimations) {
                    key = keyPrefix + roleId + animation[0];
                    value = urlPrefix + roleId + "/" + shortId + animation[1];
                    map.add(key, ResourceFactory.createResource(getURL(value)));
                }
            }
        }
        return map;
    }

}
