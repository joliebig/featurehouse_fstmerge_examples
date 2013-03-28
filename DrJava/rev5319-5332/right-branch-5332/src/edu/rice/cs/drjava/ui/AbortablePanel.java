

package edu.rice.cs.drjava.ui;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import edu.rice.cs.drjava.model.SingleDisplayModel;


public abstract class AbortablePanel extends TabbedPanel {
  protected JPanel _leftPane;
  protected JScrollPane _scrollPane;
  
  protected final SingleDisplayModel _model;
  protected final MainFrame _frame;
  
  protected String _title;
  protected JPanel _buttonPanel;
  
  protected JButton _abortButton;

  
  public AbortablePanel(MainFrame frame, String title) {
    super(frame, title);
    
    _title = title;
    this.setLayout(new BorderLayout());
    
    _frame = frame;
    _model = frame.getModel();
    
    this.removeAll(); 

    
    _closePanel = new JPanel(new BorderLayout());
    _closePanel.add(_closeButton, BorderLayout.NORTH);
    
    _leftPane = new JPanel(new BorderLayout());
    Component leftPanel = makeLeftPanel();
    _scrollPane = new JScrollPane(leftPanel);
    _leftPane.add(_scrollPane);
    _setColors(leftPanel);
    
    this.add(_leftPane, BorderLayout.CENTER);
    
    _buttonPanel = new JPanel(new BorderLayout());
    _setupButtonPanel();
    this.add(_buttonPanel, BorderLayout.EAST);
    updateButtons();
    
  }
  
  
  protected static void _setColors(Component c) {
    new ForegroundColorListener(c);
    new BackgroundColorListener(c);
  }
  
  
  @Override
  protected void _close() {
    super._close();
    abortActionPerformed(null);
    updateButtons();
  }

  
  protected abstract Component makeLeftPanel();

  
  protected abstract void abortActionPerformed(ActionEvent e);
  
  
  protected void updateButtons() { }  

  
  protected JComponent[] makeButtons() {
    return new JComponent[0];    
  }
  
  
  private void _setupButtonPanel() {
    JPanel mainButtons = new JPanel();
    JPanel emptyPanel = new JPanel();
    JPanel closeButtonPanel = new JPanel(new BorderLayout());
    GridBagLayout gbLayout = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    mainButtons.setLayout(gbLayout);
    
    JComponent[] buts = makeButtons();

    closeButtonPanel.add(_closeButton, BorderLayout.NORTH);    
    mainButtons.add(_abortButton = new JButton("Abort"));
    _abortButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { abortActionPerformed(e); }
    });
    for (JComponent b: buts) { mainButtons.add(b); }
    mainButtons.add(emptyPanel);
    
    c.fill = GridBagConstraints.HORIZONTAL;
    c.anchor = GridBagConstraints.NORTH;
    c.gridwidth = GridBagConstraints.REMAINDER;
    c.weightx = 1.0;

    gbLayout.setConstraints(_abortButton, c);
    for (JComponent b: buts) { gbLayout.setConstraints(b, c); }
    
    c.fill = GridBagConstraints.BOTH;
    c.anchor = GridBagConstraints.SOUTH;
    c.gridheight = GridBagConstraints.REMAINDER;
    c.weighty = 1.0;
    
    gbLayout.setConstraints(emptyPanel, c);
    
    _buttonPanel.add(mainButtons, BorderLayout.CENTER);
    _buttonPanel.add(closeButtonPanel, BorderLayout.EAST);
  }
}
