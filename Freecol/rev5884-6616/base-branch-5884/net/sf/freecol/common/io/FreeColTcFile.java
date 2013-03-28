

package net.sf.freecol.common.io;

import java.io.File;
import java.io.IOException;

import net.sf.freecol.FreeCol;
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
}
