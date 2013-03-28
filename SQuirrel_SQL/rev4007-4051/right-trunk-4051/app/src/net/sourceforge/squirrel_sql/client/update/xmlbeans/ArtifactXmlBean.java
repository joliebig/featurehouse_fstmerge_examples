
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.io.Serializable;


public class ArtifactXmlBean implements Serializable {

    private static final long serialVersionUID = -6653935534454353144L;

    private String name;

    private String type;

    private String version;

    private long size;

    private long checksum;

    public ArtifactXmlBean() {

    }

    public ArtifactXmlBean(String name, String type, String version, long size,
            long checksum) {

        this.name = name;
        this.type = type;
        this.version = version;
        this.size = size;
        this.checksum = checksum;
    }

    
    public String getName() {
        return name;
    }

    
    public void setName(String name) {
        this.name = name;
    }

    
    public String getType() {
        return type;
    }

    
    public void setType(String type) {
        this.type = type;
    }

    
    public String getVersion() {
        return version;
    }

    
    public void setVersion(String version) {
        this.version = version;
    }

    
    public long getSize() {
        return size;
    }

    
    public void setSize(long size) {
        this.size = size;
    }

    
    public long getChecksum() {
        return checksum;
    }

    
    public void setChecksum(long checksum) {
        this.checksum = checksum;
    }

}
