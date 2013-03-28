
package genj.gedcom;

import java.io.File;
import java.util.List;



public class Media extends Entity {

  
  protected String getToStringPrefix() {
    return getTitle();
  }
  
  
  public boolean addFile(File file) {
    List pfiles = getProperties(PropertyBlob.class);
    PropertyBlob pfile;
    if (pfiles.isEmpty()) {
      pfile = (PropertyBlob)addProperty("BLOB", "");
    } else {
      pfile = (PropertyBlob)pfiles.get(0);
    }
    
    return pfile.addFile(file);
  }

  
  public PropertyFile getFile() {
    Property file = getProperty("FILE", true);
    return (file instanceof PropertyFile) ? (PropertyFile)file : null;    
  }
  
  
  public String getTitle() {
    Property title = getProperty("TITL");
    return title==null ? "" : title.getValue();
  }

} 
