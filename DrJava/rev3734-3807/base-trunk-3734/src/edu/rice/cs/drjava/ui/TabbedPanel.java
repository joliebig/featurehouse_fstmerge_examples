

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;


public abstract class TabbedPanel extends JPanel {
  
  protected boolean _displayed;
  
  protected JButton _closeButton;
  
  
  protected JPanel _closePanel;
  
  protected JPanel _mainPanel;
  
  protected MainFrame _frame;
  
  private String _name;

  
  public TabbedPanel(MainFrame frame, String name) {
    _frame = frame;
    _name = name;
    _setUpPanes();
    _displayed = false;
  }

  
  private void _setUpPanes() {
    this.setFocusCycleRoot(true);
    this.setLayout(new BorderLayout());

    _mainPanel = new JPanel();
    _closePanel = new JPanel(new BorderLayout());
    _closeButton = new CommonCloseButton(_closeListener);
    _closePanel.add(_closeButton, BorderLayout.NORTH);
    add(_closePanel, BorderLayout.EAST);
    add(_mainPanel, BorderLayout.CENTER);
  }

  
  private final ActionListener _closeListener =
    new ActionListener() {
    public void actionPerformed(ActionEvent e) {
      _close();
    }
  };

  
  protected void _close() {
     _displayed = false;
     _frame.removeTab(this);
  }

  
  public boolean isDisplayed() {
    return _displayed;
  }

  
  public String getName() {
    return _name;
  }

  
  public void setDisplayed(boolean displayed) {
    _displayed = displayed;
  }

  JPanel getMainPanel() {
    return _mainPanel;
  }

  
  public boolean requestFocusInWindow() {
    super.requestFocusInWindow();
    return _mainPanel.requestFocusInWindow();
  }
}