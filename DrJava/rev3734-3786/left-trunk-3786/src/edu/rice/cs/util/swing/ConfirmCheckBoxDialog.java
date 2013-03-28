

package edu.rice.cs.util.swing;

import javax.swing.*;


public class ConfirmCheckBoxDialog {
  private JDialog _dialog;
  private JOptionPane _optionPane;
  private JCheckBox _checkBox;

  
  public ConfirmCheckBoxDialog(JFrame parent, String title, Object message) {
    this(parent, title, message, "Do not show this message again");
  }

  
  public ConfirmCheckBoxDialog(JFrame parent, String title,
                               Object message, String checkBoxText) {
    this(parent, title, message, checkBoxText, JOptionPane.QUESTION_MESSAGE,
         JOptionPane.YES_NO_OPTION);
  }

  
  public ConfirmCheckBoxDialog(JFrame parent, String title, Object message,
                               String checkBoxText, int messageType, int optionType) {
    _optionPane = new JOptionPane(message, messageType, optionType);
    JPanel checkBoxPane = new JPanel();
    checkBoxPane.add(_initCheckBox(checkBoxText));
    _optionPane.add(checkBoxPane, 1);
    _dialog = _optionPane.createDialog(parent, title);
  }

  
  private JCheckBox _initCheckBox(String text) {
    _checkBox = new JCheckBox(text);
    return _checkBox;
  }

  
  public int show() {
    _dialog.setVisible(true);
    Object val = _optionPane.getValue();
    if (val == null || !(val instanceof Integer)) {
      return JOptionPane.CLOSED_OPTION;
    }
    return ((Integer)val).intValue();
  }

  
  public boolean getCheckBoxValue() {
    return _checkBox.isSelected();
  }
}