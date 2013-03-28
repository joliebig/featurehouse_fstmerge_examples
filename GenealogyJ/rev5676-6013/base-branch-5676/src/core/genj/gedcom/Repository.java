
package genj.gedcom;


public class Repository extends Entity {

  
  protected String getToStringPrefix(boolean hideIds, boolean showAsLink) {
    return getRepositoryName();
  }

  
  private String getRepositoryName() {
    Property name = getProperty("NAME");
    return name!=null ? name.getValue() : ""; 
  }
  
} 
