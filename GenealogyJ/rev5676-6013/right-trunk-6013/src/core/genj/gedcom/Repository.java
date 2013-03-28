
package genj.gedcom;


public class Repository extends Entity {

  
  @Override
  protected String getToStringPrefix(boolean showIds) {
    return getRepositoryName();
  }

  
  private String getRepositoryName() {
    Property name = getProperty("NAME");
    return name!=null ? name.getValue() : ""; 
  }
  
} 
