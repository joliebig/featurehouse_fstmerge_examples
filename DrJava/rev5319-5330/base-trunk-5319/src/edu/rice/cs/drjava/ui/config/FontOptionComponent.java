

package edu.rice.cs.drjava.ui.config;

import javax.swing.*;
import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;
import edu.rice.cs.util.swing.FontChooser;
import edu.rice.cs.util.swing.SwingFrame;

import java.awt.*;
import java.awt.event.*;

 
public class FontOptionComponent extends OptionComponent<Font,JPanel> {
  
  private final JButton _button;
  private final JTextField _fontField;
  private final JPanel _panel;
  private volatile Font _font;
  
  public FontOptionComponent(FontOption opt, String text, SwingFrame parent) {
    super(opt, text, parent);
    _button = new JButton();
    _button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { chooseFont(); }
    });
    _button.setText("...");
    _button.setMaximumSize(new Dimension(10,10));
    _button.setMinimumSize(new Dimension(10,10));
    
    _fontField = new JTextField();
    _fontField.setEditable(false);
    _fontField.setBackground(Color.white);
    _fontField.setHorizontalAlignment(JTextField.CENTER);
    _panel = new JPanel(new BorderLayout());
    _panel.add(_fontField, BorderLayout.CENTER);
    _panel.add(_button, BorderLayout.EAST);

    _font = DrJava.getConfig().getSetting(_option);
    _updateField(_font);
    setComponent(_panel);
  }
  
  
  public FontOptionComponent(FontOption opt, String text, SwingFrame parent, String description) {
    this(opt, text, parent);
    setDescription(description);
  }

  
  public void setDescription(String description) {
    _panel.setToolTipText(description);
    _fontField.setToolTipText(description);
    _label.setToolTipText(description);
  }

  
  private void _updateField(Font f) {
    _fontField.setFont(f);
    _fontField.setText(_option.format(f));
  }
  
  
  public void chooseFont() {
    String oldText = _fontField.getText();
    Font f = FontChooser.showDialog(_parent, "Choose '" + getLabelText() + "'", _font);
    if (f != null) {
      _font = f;
      _updateField(_font);
      if (!oldText.equals(_fontField.getText())) { notifyChangeListeners(); }
    }
  }
  
  
  public boolean updateConfig() {
    if (! _font.equals(DrJava.getConfig().getSetting(_option))) DrJava.getConfig().setSetting(_option, _font);
    return true;
  }
  
   
  public void setValue(Font value) {
    _font = value;
    _updateField(value);
  }
}