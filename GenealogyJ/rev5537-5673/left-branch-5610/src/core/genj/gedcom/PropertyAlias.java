
package genj.gedcom;

import java.util.List;



public class PropertyAlias extends PropertyXRef {
  
  
   PropertyAlias() {
  }

  
  public String getTag() {
    return "ALIA";
  }

  
  public void link() throws GedcomException {
    
    Indi indi = (Indi)getEntity();

     
    Entity ent = getCandidate();
    
    
    List aliass = ent.getProperties(PropertyAlias.class);
    for (int i=0, j=aliass.size(); i<j; i++) {
      PropertyAlias alias = (PropertyAlias)aliass.get(i);
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
