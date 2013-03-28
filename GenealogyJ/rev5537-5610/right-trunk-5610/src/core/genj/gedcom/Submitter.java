
package genj.gedcom;


public class Submitter extends Entity {
  
  private final static TagPath PATH_NAME =new TagPath("SUBM:NAME");

  
  protected String getToStringPrefix(boolean hideIds, boolean showAsLink) {
    return getName();
  }
  
  
  public String getName() {
    return getValue(PATH_NAME, "");
  }
  
  
  public void setName(String name) {
    setValue(PATH_NAME, name);
  }
  
} 
