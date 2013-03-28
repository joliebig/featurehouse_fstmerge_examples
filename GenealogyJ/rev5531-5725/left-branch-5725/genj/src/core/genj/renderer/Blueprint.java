
package genj.renderer;

public class Blueprint {
  
  
  private String tag;
  
  
  private String name;
  
  
  private String html;
  
  
  private boolean isReadOnly = false;
  
  
  private boolean isDirty = true;

  
  public Blueprint(String hTml) {
    html = hTml;
  }
    
  
   Blueprint(String etag, String nAme, String hTml, boolean readOnly) {
    
    tag = etag;
    name = nAme;
    html = hTml;
    isReadOnly = readOnly;
    
  }

  
  public void setHTML(String hTml) {
    
    if (isReadOnly()) 
      throw new IllegalArgumentException("Can't change read-only Blueprint");
    
    html = hTml;
    isDirty = true;
    
  }
  
  
   void clearDirty() {
    isDirty = false;
  }
  
  
   boolean isDirty() {
    return isDirty;
  }
  
  
  public String getHTML() {
    return html;
  }

  
  public String getName() {
    return name;
  }
  
  
   boolean isReadOnly() {
    return isReadOnly;
  }
  
  
  public String getTag() {
    return tag;
  }
  
  @Override
  public String toString() {
    return getName();
  }
  
} 
