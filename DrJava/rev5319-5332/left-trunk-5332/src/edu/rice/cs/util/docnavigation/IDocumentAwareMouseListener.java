  

package edu.rice.cs.util.docnavigation;

import java.awt.event.*;



public interface IDocumentAwareMouseListener<ItemT extends INavigatorItem> {
  public void mouseClicked(MouseEvent e, ItemT clickee);
  public void mouseEntered(MouseEvent e, ItemT onDocument);
  public void mouseExited(MouseEvent e, ItemT fromDocument);
  public void mousePressed(MouseEvent e, ItemT pressee);
  public void mouseReleased(MouseEvent e, ItemT releaseee);
}
