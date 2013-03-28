

package edu.rice.cs.drjava.ui.config;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import java.awt.*;


import java.util.Vector;


public class ConfigPanel extends JPanel {

  protected final String _title;
  protected final Vector<OptionComponent<?>> _components;

  
  public ConfigPanel(String title) {
    
    _title = title;
    _components = new Vector<OptionComponent<?>>();
  }

  public String getTitle() { return _title; }

  
  public void addComponent( OptionComponent<?> oc) { _components.add(oc); }

  public void displayComponents() {
    this.setLayout(new BorderLayout());

    JPanel panel = new JPanel();  
    panel.setLayout(new BorderLayout());
    JPanel panel2 = new JPanel();  
    panel.add(panel2, BorderLayout.NORTH);
    
    JScrollPane scroll = 
      new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    scroll.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), _title));
    
    
    JScrollBar bar = scroll.getVerticalScrollBar();
    bar.setUnitIncrement(25);
    bar.setBlockIncrement(400);

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    panel2.setLayout(gridbag);
    c.fill = GridBagConstraints.HORIZONTAL;
    Insets labelInsets = new Insets(0, 10, 0, 10);
    Insets compInsets  = new Insets(0, 0, 0, 0);
    for (int i = 0; i < _components.size(); i++) {
      OptionComponent<?> comp = _components.get(i);

      if (!comp.useEntireColumn()) {
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0.0;
        c.gridwidth = 1;
        c.insets = labelInsets;

        JLabel label= comp.getLabel();
        gridbag.setConstraints(label, c);
        panel2.add(label);

        c.weightx = 1.0;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = compInsets;

        JComponent otherC = comp.getComponent();
        gridbag.setConstraints(otherC, c);
        panel2.add(otherC);
      }
      else {
        c.anchor = GridBagConstraints.NORTH;
        c.weightx = 0.0;
        c.gridwidth = 2;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.insets = compInsets;

        JComponent otherC = comp.getComponent();
        gridbag.setConstraints(otherC, c);
        panel2.add(otherC);
      }
    }

    
    JButton _resetToDefaultButton = new JButton("Reset to Defaults");
    _resetToDefaultButton.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { resetToDefault(); }
    });
    JPanel resetPanel = new JPanel();
    resetPanel.setLayout(new FlowLayout());
    resetPanel.setBorder(new EmptyBorder(5,5,5,5));
    resetPanel.add(_resetToDefaultButton);
    panel.add(resetPanel, BorderLayout.SOUTH);

    this.add(scroll, BorderLayout.CENTER);
  }

  
  public boolean update() {

    for (int i = 0; i < _components.size(); i++) {
      boolean isValidUpdate = _components.get(i).updateConfig();
      if (! isValidUpdate) return false;
    }
    return true;
  }

  
  public void resetToCurrent() {
    for (int i = 0; i < _components.size(); i++){
      _components.get(i).resetToCurrent();
      if (_components.get(i) instanceof VectorOptionComponent<?>)
        ((VectorOptionComponent<?>)_components.get(i)).resizeTable();
    }
  }

  
  public void resetToDefault() {
    for (int i = 0; i < _components.size(); i++){
      _components.get(i).resetToDefault();
      if (_components.get(i) instanceof VectorOptionComponent<?>)
        ((VectorOptionComponent<?>)_components.get(i)).resizeTable();
    }
  }
}
