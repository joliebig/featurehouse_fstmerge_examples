

package edu.rice.cs.drjava.ui;

import java.util.ArrayList;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.text.BadLocationException;
import java.awt.event.*;
import java.awt.*;

import edu.rice.cs.plt.lambda.Lambda;
import edu.rice.cs.drjava.model.RegionManager;
import edu.rice.cs.drjava.model.RegionManagerListener;
import edu.rice.cs.drjava.model.debug.*;


public class BreakpointsPanel extends RegionsTreePanel<Breakpoint> {
  protected JButton _goToButton;
  protected JButton _enableDisableButton;
  protected JButton _removeButton;
  protected JButton _removeAllButton;
  protected final Debugger _debugger;
  
  
  public BreakpointsPanel(MainFrame frame, RegionManager<Breakpoint> breakpointManager) {
    super(frame, "Breakpoints", breakpointManager);
    
    _regionManager.addListener(new RegionManagerListener<Breakpoint>() {
      
      public void regionAdded(final Breakpoint bp) { 
        assert EventQueue.isDispatchThread();
        addRegion(bp); 
      }
      
      
      public void regionChanged(final Breakpoint bp) {
        assert EventQueue.isDispatchThread();

        DefaultMutableTreeNode regNode = _regionToTreeNode.get(bp);
        ((DefaultTreeModel)_regTree.getModel()).nodeChanged(regNode);
      }
      
      
      public void regionRemoved(final Breakpoint bp) { removeRegion(bp); }
    });
    _debugger = _model.getDebugger();
  }
  
  
  protected void performDefaultAction() { goToRegion(); }
  
  
  protected JComponent[] makeButtons() {    
    Action goToAction = new AbstractAction("Go to") {
      public void actionPerformed(ActionEvent ae) {
        goToRegion();
      }
    };
    _goToButton = new JButton(goToAction);
    
    Action enableDisableAction = new AbstractAction("Disable") {
      public void actionPerformed(ActionEvent ae) {
        enableDisableBreakpoint();
      }
    };
    _enableDisableButton = new JButton(enableDisableAction);
    
    Action removeAction = new AbstractAction("Remove") {
      public void actionPerformed(ActionEvent ae) {

        for (Breakpoint bp: getSelectedRegions()) _regionManager.removeRegion(bp);

      }
    };
    _removeButton = new JButton(removeAction);
    
    Action removeAllAction = new AbstractAction("Remove All") {
      public void actionPerformed(ActionEvent ae) {

        _regionManager.clearRegions();

      }
    };
    _removeAllButton = new JButton(removeAllAction);
    
    JComponent[] buts = new JComponent[] { 
      _enableDisableButton,
        _goToButton, 
        _removeButton,
        _removeAllButton
    };
    
    return buts;
  }
  
  
  protected void _updateButtons() {
    ArrayList<Breakpoint> regs = getSelectedRegions();
    _goToButton.setEnabled(regs.size() == 1);
    _removeButton.setEnabled(regs.size() > 0);
    _removeAllButton.setEnabled(_rootNode != null && _rootNode.getDepth() > 0);
    _enableDisableButton.setEnabled(regs.size()>0);
    if (regs.size() > 0) {
      if (regs.get(0).isEnabled()) _enableDisableButton.setText("Disable");
      else _enableDisableButton.setText("Enable");
    }
    _removeAllButton.setEnabled(_rootNode != null && _rootNode.getDepth() > 0);
  }
  
  
  protected void closeIfEmpty() {
    
  }
  
  
  protected AbstractAction[] makePopupMenuActions() {
    AbstractAction[] acts = new AbstractAction[] {
      new AbstractAction("Go to") {
        public void actionPerformed(ActionEvent e) { goToRegion(); }
      },
        
        new AbstractAction("Remove") {
          public void actionPerformed(ActionEvent e) {
            for (Breakpoint bp: getSelectedRegions()) _regionManager .removeRegion(bp);
          }
        }
    };
    return acts;
  }
  
  
  protected void goToRegion() {
    ArrayList<Breakpoint> bps = getSelectedRegions();
    if (bps.size() == 1) _debugger.scrollToSource(bps.get(0));
  }
  
  
  protected void enableDisableBreakpoint() {
    final ArrayList<Breakpoint> bps = getSelectedRegions();
    if (bps.size() > 0) {
      final boolean newState = !bps.get(0).isEnabled();
      for (Breakpoint bp: bps) {
        _regionManager.changeRegion(bp, new Lambda<Breakpoint,Object>() {
          public Object value(Breakpoint bp) {
            bp.setEnabled(newState);
            return null;
          }
        });
      }
    }
  }
  
  
  
  protected RegionTreeUserObj<Breakpoint> makeRegionTreeUserObj(Breakpoint bp) {
    return new BreakpointRegionTreeUserObj(bp);
  }
  
  
  protected static class BreakpointRegionTreeUserObj extends RegionTreeUserObj<Breakpoint> {
    public BreakpointRegionTreeUserObj (Breakpoint bp) { super(bp); }
    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append(lineNumber());
      try {
        if (!_region.isEnabled()) { sb.append(" (disabled)"); }
        sb.append(": ");
        int length = Math.min(120, _region.getEndOffset()-_region.getStartOffset());
        sb.append(_region.getDocument().getText(_region.getStartOffset(), length).trim());
      } catch(BadLocationException bpe) {  }        
      return sb.toString();
    }
  }
}
