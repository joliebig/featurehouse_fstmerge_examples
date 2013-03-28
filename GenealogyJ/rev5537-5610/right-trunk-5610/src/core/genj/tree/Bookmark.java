
package genj.tree;

import genj.gedcom.Entity;
import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.util.swing.Action2;


public class Bookmark extends Action2 {
  
  
  private TreeView tree;
  
  
  private String name;
  
  
  private Entity entity;
  
  
   Bookmark(TreeView t, Gedcom ged, String s) throws IllegalArgumentException {
    
    
    int at = s.indexOf('#');
    if (at<0) throw new IllegalArgumentException("id#expected name");
    
    tree = t;
    name = s.substring(at+1);
    String id = s.substring(0,at);
    
    
    entity = ged.getEntity(id);
    if (!(entity instanceof Indi||entity instanceof Fam))
      throw new IllegalArgumentException("id "+id+" doesn't point to Indi or Fam");
  
    
    setText(name);
    setImage(Gedcom.getEntityImage(entity.getTag()));
  }
  
  
  public Bookmark(TreeView t, String n, Entity e) {
    tree = t;
    name = n;
    entity = e;
  
    setText(name);
    setImage(Gedcom.getEntityImage(entity.getTag()));
  }
  
  
  public String getName() {
    return name;
  }
  
  
  public Entity getEntity() {
    return entity;
  }
  
  
  protected void execute() {
    
    TreeNode node = tree.getModel().getNode(entity);
    if (node!=null)
      tree.setCurrent(entity);
    else
      tree.setRoot(entity);
  }

  
  public String toString() {
    return entity.getId()+'#'+name;
  }
  

} 
