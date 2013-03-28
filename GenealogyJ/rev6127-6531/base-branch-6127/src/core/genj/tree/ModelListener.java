
package genj.tree;

import java.util.Collection;


public interface ModelListener {

  
  public void nodesChanged(Model model, Collection<TreeNode> nodes);
  
  
  public void structureChanged(Model model);
  
} 
