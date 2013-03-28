
package genj.gedcom;

import java.io.File;
import java.util.List;



public class Media extends Entity {
  
  private final static TagPath
    TITLE55 = new TagPath("OBJE:TITL"),
    TITLE551 = new TagPath("OBJE:FILE:TITL");
  
  private TagPath titlepath = TITLE55;

  
  public Media(String tag, String id) {
    super(tag, id);
    assertTag(Gedcom.OBJE);
  }
  
  
  @Override
  protected String getToStringPrefix(boolean showIds) {
    return getTitle();
  }
  
  
  public boolean addFile(File file) {

    
    if (!getMetaProperty().allows("BLOB")) 
      return super.addFile(file);
      
    List<PropertyBlob> blobs = getProperties(PropertyBlob.class);
    PropertyBlob blob;
    if (blobs.isEmpty()) {
      blob = (PropertyBlob)addProperty("BLOB", "");
    } else {
      blob = (PropertyBlob)blobs.get(0);
    }
    
    return blob.addFile(file);
  }

  
  public File getFile() {
    Property file = getProperty("FILE", true);
    return (file instanceof PropertyFile) ? ((PropertyFile)file).getFile() : null;    
  }
  
  
  public PropertyBlob getBlob() {
    Property blob = getProperty("BLOB", true);
    return (blob instanceof PropertyBlob) ? (PropertyBlob)blob : null;    
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

  public void setTitle(String title) {
    setValue(titlepath, title);
  }

} 
