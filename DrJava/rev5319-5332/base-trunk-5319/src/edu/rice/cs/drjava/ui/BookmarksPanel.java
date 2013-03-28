

package edu.rice.cs.drjava.ui;

import java.util.ArrayList;

import javax.swing.*;
import java.awt.event.*;

import edu.rice.cs.drjava.model.RegionManager;
import edu.rice.cs.drjava.model.RegionManagerListener;
import edu.rice.cs.drjava.model.MovingDocumentRegion;


public class BookmarksPanel extends RegionsTreePanel<MovingDocumentRegion> {
  protected JButton _goToButton;
  protected JButton _removeButton;
  protected JButton _removeAllButton;
  
  
  public BookmarksPanel(MainFrame frame, RegionManager<MovingDocumentRegion> bookmarkManager) {
    super(frame, "Bookmarks", bookmarkManager);
    _regionManager.addListener(new RegionManagerListener<MovingDocumentRegion>() {      
      public void regionAdded(MovingDocumentRegion r) { addRegion(r); }
      public void regionChanged(MovingDocumentRegion r) { 
        regionRemoved(r);
        regionAdded(r);
      }
      public void regionRemoved(MovingDocumentRegion r) { removeRegion(r); }
    });
  }
  
  
  protected void performDefaultAction() {
    goToRegion();
  }
  
  
  protected JComponent[] makeButtons() {    
    Action goToAction = new AbstractAction("Go to") {
      public void actionPerformed(ActionEvent ae) {
        goToRegion();
      }
    };
    _goToButton = new JButton(goToAction);

    Action removeAction = new AbstractAction("Remove") {
      public void actionPerformed(ActionEvent ae) { _remove(); }  
    };
  
    _removeButton = new JButton(removeAction);
    
    Action removeAllAction = new AbstractAction("Remove All") {
      public void actionPerformed(ActionEvent ae) {

        _regionManager.clearRegions();

      }
    };
    _removeAllButton = new JButton(removeAllAction);
    
    JComponent[] buts = new JComponent[] { 
      _goToButton, 
        _removeButton,
        _removeAllButton
    };
    
    return buts;
  }

  
  protected void _updateButtons() {
    ArrayList<MovingDocumentRegion> regs = getSelectedRegions();
    _goToButton.setEnabled(regs.size() == 1);
    _removeButton.setEnabled(regs.size() > 0);
    _removeAllButton.setEnabled(_rootNode != null && _rootNode.getDepth() > 0);
  }
  
  
  protected AbstractAction[] makePopupMenuActions() {
    AbstractAction[] acts = new AbstractAction[] {
      new AbstractAction("Go to") { public void actionPerformed(ActionEvent e) { goToRegion(); } },
        
      new AbstractAction("Remove") {
        public void actionPerformed(ActionEvent e) {
          for (MovingDocumentRegion r: getSelectedRegions()) _regionManager.removeRegion(r);
        }
      }
    };
    return acts;
  }
}
