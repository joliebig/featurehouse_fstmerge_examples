

package net.sf.freecol.common.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import net.sf.freecol.FreeCol;
import net.sf.freecol.client.gui.i18n.Messages;



public class FreeColModFile extends FreeColDataFile {
    
    private static final String SPECIFICATION_FILE = "specification.xml";
    private static final String MOD_DESCRIPTOR_FILE = "mod.xml";
    public static final String[] FILE_ENDINGS = new String[] {".fmd", ".zip"};

    private String id;
    private final ModInfo modInfo;
    
    
    public FreeColModFile(final String id) {
        this(id, new File(FreeCol.getModsDirectory(), id));
    }
    
    
    public FreeColModFile(final ModInfo mi) {
        this(mi.getId());
    }
    
    
    protected FreeColModFile(final String id, final File file) {
        super(file);
        
        this.id = id;
        this.modInfo = new ModInfo(id);
    }
    
    
    public InputStream getSpecificationInputStream() throws IOException {
        return getInputStream(SPECIFICATION_FILE);
    }
    
    
    protected ModDescriptor getModDescriptor() throws IOException {
        XMLInputFactory xif = XMLInputFactory.newInstance();
        XMLStreamReader in = null;
        try {
            in = xif.createXMLStreamReader(getModDescriptorInputStream());
            in.nextTag();
            final ModDescriptor mi = new ModDescriptor(in);
            return mi;
        } catch (XMLStreamException e) {
            final IOException e2 = new IOException("XMLStreamException.");
            e2.initCause(e);
            throw e2;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {}
        }
    }
    
    
    private InputStream getModDescriptorInputStream() throws IOException {
        return getInputStream(MOD_DESCRIPTOR_FILE);
    }
    
    
    @Override
    protected String[] getFileEndings() {
        return FILE_ENDINGS;
    }
    
    
    public String getId() {
        return id;
    }
    
    public ModInfo getModInfo() {
        return modInfo;
    }
        
    public static class ModInfo {

        private final String id;
        
        private ModInfo(final String id) {
            this.id = id;
        }
        
        
        public String getId() {
            return id;
        }

        
        public String getName() {
            
            return Messages.message("mod." + getId() + ".name");
        }
        
        
        public String getShortDescription() {
            
            return Messages.message("mod." + getId() + ".shortDescription");
        }
        
        
        public String toString() {
            return getName();
        }
    }
    
    protected static class ModDescriptor {

        private final String parent;
        
        
        protected ModDescriptor(XMLStreamReader in) throws XMLStreamException {
            this.parent = in.getAttributeValue(null, "parent");
        }
        
        
        public String getParent() {
            return parent;
        }
    }
}
