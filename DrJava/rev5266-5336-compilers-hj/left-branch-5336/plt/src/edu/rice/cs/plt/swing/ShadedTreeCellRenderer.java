

package edu.rice.cs.plt.swing;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;


public class ShadedTreeCellRenderer implements TreeCellRenderer {
  
  
  private static final Component DUMMY_CELL = Box.createRigidArea(new Dimension(0, 0));

  private final TreeCellRenderer _renderer;
  private final Color _even;
  private final Color _odd;
  
  
  public ShadedTreeCellRenderer(TreeCellRenderer renderer, Color even, Color odd) {
    _renderer = renderer;
    _even = even;
    _odd = odd;
  }
  
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
                                                boolean expanded, boolean leaf, int row,
                                                boolean hasFocus) {
    
    int maxRow = tree.getRowCount() - (tree.isRootVisible() ? 0 : 1);
    if (row < maxRow) {
      Component c = _renderer.getTreeCellRendererComponent(tree, value, selected, expanded,
                                                           leaf, row, hasFocus);
      c.setBackground(row % 2 == 0 ? _even : _odd);
      return c;
    }
    else { return DUMMY_CELL; }
  }
  
  
  public static void shadeTree(JTree tree, Color even, Color odd) {
    tree.setCellRenderer(new ShadedTreeCellRenderer(tree.getCellRenderer(), even, odd));
  }
    

}