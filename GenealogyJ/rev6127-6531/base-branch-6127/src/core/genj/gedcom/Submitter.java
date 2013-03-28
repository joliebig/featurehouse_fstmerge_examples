
package genj.gedcom;


public class Submitter extends Entity {
  
  private final static TagPath PATH_NAME =new TagPath("SUBM:NAME");

  
  @Override
  protected String getToStringPrefix(boolean showIds) {
    return getName();
  }
  
  
  public String getName() {
    return getValue(PATH_NAME, "");
  }
  
  
  public void setName(String name) {
    setValue(PATH_NAME, name);
  }
  
} 
