

package edu.rice.cs.drjava.ui.config;


import java.awt.event.*;
import java.io.File;
import javax.swing.filechooser.FileFilter;
import javax.swing.*;

import java.util.Vector;
import java.util.List;

import edu.rice.cs.drjava.ui.*;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.util.swing.SwingFrame;


public class VectorStringOptionComponent extends VectorOptionComponent<String> implements OptionConstants {
  public VectorStringOptionComponent(VectorOption<String> opt, String text, SwingFrame parent) {
    this(opt, text, parent, null);
  }
  
  
  public VectorStringOptionComponent(VectorOption<String> opt, String text, SwingFrame parent, String description) {
    this(opt, text, parent, description, false);
  }

  
  public VectorStringOptionComponent(VectorOption<String> opt, String text, SwingFrame parent,
                                     String description, boolean moveButtonEnabled) {
    super(opt, text, parent, new String[] { }, description, moveButtonEnabled);  
  }

  
  public void chooseString() {
    String s = (String)JOptionPane.showInputDialog(_parent,
                                                   "Enter the value to add:",
                                                   "Add",
                                                   JOptionPane.QUESTION_MESSAGE,
                                                   null,
                                                   null,
                                                   "");    
    
    if ((s != null) && (s.length() > 0)) {
      if (verify(s)) {
        _addValue(s);
      }
    }
  }
  
  
  protected boolean verify(String s) {
    return true;
  }
  
  protected Action _getAddAction() {
    return new AbstractAction("Add") {
      public void actionPerformed(ActionEvent ae) {
        chooseString();
      }
    };
  }
}
