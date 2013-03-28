
package genj.gedcom;




public class Source extends Entity {

  
  @Override
  protected String getToStringPrefix(boolean showIds) {
    return getTitle();
  }
  
  
  public String getTitle() {
    Property title = getProperty("TITL");
    return title!=null ? title.getValue() : "";
  }
  
  
  public String getText() {
    Property text = getProperty("TEXT");
    if (text!=null) 
      return text.getValue();
    return "";
  }
  
} 
