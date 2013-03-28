

package edu.rice.cs.plt.swing;

import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeModelEvent;


public class ComposedTreeModelListener extends ComposedListener<TreeModelListener> implements TreeModelListener {
  public void treeNodesChanged(TreeModelEvent e) {
    for (TreeModelListener l : listeners()) { l.treeNodesChanged(e); }
  }
  public void treeNodesInserted(TreeModelEvent e) {
    for (TreeModelListener l : listeners()) { l.treeNodesInserted(e); }
  }
  public void treeNodesRemoved(TreeModelEvent e) {
    for (TreeModelListener l : listeners()) { l.treeNodesRemoved(e); }
  }
  public void treeStructureChanged(TreeModelEvent e) {
    for (TreeModelListener l : listeners()) { l.treeStructureChanged(e); }
  }
}
