
package genj.gedcom;

import java.io.File;
import java.util.List;



public class Media extends Entity {
  
  private final static TagPath
    TITLE55 = new TagPath("OBJE:TITL"),
    TITLE551 = new TagPath("OBJE:FILE:TITL");
  
  private TagPath titlepath = TITLE55;

  
  @Override
  protected String getToStringPrefix(boolean showIds) {
    return getTitle();
  }
  
  
  public boolean addFile(File file) {
    List<PropertyBlob> pfiles = getProperties(PropertyBlob.class);
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
    Property title = getProperty(titlepath);
    return title==null ? "" : title.getValue();
  }
  
  @Override
  void addNotify(Gedcom ged) {
    super.addNotify(ged);
    
    if (getMetaProperty().allows("TITLE"))
      titlepath = TITLE55;
    else
      titlepath = TITLE551;
      
  }

} 
