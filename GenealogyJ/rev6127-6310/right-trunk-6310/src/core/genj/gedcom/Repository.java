
package genj.gedcom;


public class Repository extends Entity {

  
  public Repository(String tag, String id) {
    super(tag, id);
    assertTag(Gedcom.REPO);
  }
  
  
  @Override
  protected String getToStringPrefix(boolean showIds) {
    return getRepositoryName();
  }

  
  private String getRepositoryName() {
    Property name = getProperty("NAME");
    return name!=null ? name.getValue() : ""; 
  }
  
} 
