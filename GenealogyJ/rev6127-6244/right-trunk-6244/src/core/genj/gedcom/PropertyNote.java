
package genj.gedcom;

import java.util.regex.Pattern;




public class PropertyNote extends PropertyXRef {

  public static final String TAG = "NOTE";
  
  
  PropertyNote(String tag) {
    super(tag);
    assertTag("NOTE");
  }

  
  protected boolean findPropertiesRecursivelyTest(Pattern tag, Pattern value) {
    
    Note note = (Note)getTargetEntity();
    if (note!=null) {
      if (tag.matcher(getTag()).matches() && value.matcher(note.getDelegate().getValue()).matches())
        return true;
    }
    
    return false;
  }

  
  public void link() throws GedcomException {
    
    
    Note enote = (Note)getCandidate();

    
    PropertyForeignXRef fxref = new PropertyForeignXRef();
    enote.addProperty(fxref);

    
    link(fxref);

    
  }
  
  
  public String getTargetType() {
    return Gedcom.NOTE;
  }
  
} 

