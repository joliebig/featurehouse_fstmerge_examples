
package genj.tree;

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;


public class Bookmark {
  
  
  private String name;
  
  
  private Entity entity;
  
  
   Bookmark(Gedcom ged, String s) throws IllegalArgumentException {
    
    
    int at = s.indexOf('#');
    if (at<0) throw new IllegalArgumentException("id#expected name");
    
    name = s.substring(at+1);
    String id = s.substring(0,at);
    
    
    entity = ged.getEntity(id);
    if (!(entity instanceof Indi||entity instanceof Fam))
      throw new IllegalArgumentException("id "+id+" doesn't point to Indi or Fam");
  
  }
  
  
  public Bookmark(String n, Entity e) {
    name = n;
    entity = e;
  }
  
  
  public String getName() {
    return name;
  }
  
  
  public Entity getEntity() {
    return entity;
  }
  
  
  public String toString() {
    return entity.getId()+'#'+name;
  }
  

} 
