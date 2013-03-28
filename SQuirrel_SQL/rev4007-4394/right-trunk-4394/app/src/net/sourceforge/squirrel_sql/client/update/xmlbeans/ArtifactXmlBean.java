
package net.sourceforge.squirrel_sql.client.update.xmlbeans;

import java.io.Serializable;


public class ArtifactXmlBean implements Serializable {

    private static final long serialVersionUID = -6653935534454353144L;

    private String name;

    private String type;

    private String version;

    private long size;

    private long checksum;

    private boolean installed = false;
    
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

   
   public boolean isInstalled() {
      return installed;
   }

   
   public void setInstalled(boolean installed) {
      this.installed = installed;
   }

   
   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (checksum ^ (checksum >>> 32));
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + (int) (size ^ (size >>> 32));
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
      return result;
   }

   
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final ArtifactXmlBean other = (ArtifactXmlBean) obj;
      if (checksum != other.checksum)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (size != other.size)
         return false;
      if (type == null) {
         if (other.type != null)
            return false;
      } else if (!type.equals(other.type))
         return false;
      if (version == null) {
         if (other.version != null)
            return false;
      } else if (!version.equals(other.version))
         return false;
      return true;
   }

	
	 @Override
	 public String toString()
	 {
	     final String TAB = "    ";
	 
	     StringBuilder retValue = new StringBuilder();
	     
	     retValue.append("ArtifactXmlBean ( ")
	         .append(super.toString()).append(TAB)
	         .append("name = ").append(this.name).append(TAB)
	         .append("type = ").append(this.type).append(TAB)
	         .append("version = ").append(this.version).append(TAB)
	         .append("size = ").append(this.size).append(TAB)
	         .append("checksum = ").append(this.checksum).append(TAB)
	         .append("installed = ").append(this.installed).append(TAB)
	         .append(" )");
	     
	     return retValue.toString();
	 }

   
}
