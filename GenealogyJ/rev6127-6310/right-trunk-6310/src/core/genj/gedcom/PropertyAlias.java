
package genj.gedcom;

import java.util.List;



public class PropertyAlias extends PropertyXRef {
  
  
   PropertyAlias() {
    super("ALIA");
  }

  
   PropertyAlias(String tag) {
    super(tag);
    assertTag("ALIA");
  }

  
  public void link() throws GedcomException {
    
    Indi indi = (Indi)getEntity();

     
    Entity ent = getCandidate();
    
    
    List<PropertyAlias> aliass = ent.getProperties(PropertyAlias.class);
    for (int i=0, j=aliass.size(); i<j; i++) {
      PropertyAlias alias = aliass.get(i);
      if (alias.isCandidate(indi)) {
        link(alias);
        return;
      }        
    }
    

    
    PropertyAlias alias = new PropertyAlias();
    try {
      ent.addProperty(alias);
    } catch (Throwable t) {
    }

    
    link(alias);

    
  }

  
  public String getTargetType() {
    return Gedcom.INDI;
  }
  
} 
