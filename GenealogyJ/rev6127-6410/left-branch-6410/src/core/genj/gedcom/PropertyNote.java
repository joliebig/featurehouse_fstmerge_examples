
package genj.gedcom;

import java.util.regex.Pattern;




public class PropertyNote extends PropertyXRef {

  public static final String TAG = "NOTE";

  
   Property init(MetaProperty meta, String value) throws GedcomException {
    
    meta.assertTag("NOTE");
    
    if (value.startsWith("@")&&value.endsWith("@"))
      return super.init(meta, value);
    
    return new PropertyMultilineValue().init(meta, value);
  }

  
  protected boolean findPropertiesRecursivelyTest(Pattern tag, Pattern value) {
    
    Note note = (Note)getTargetEntity();
    if (note!=null) {
      if (tag.matcher(getTag()).matches() && value.matcher(note.getDelegate().getValue()).matches())
        return true;
    }
    
    return false;
  }

  
  public String getTag() {
    return "NOTE";
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

