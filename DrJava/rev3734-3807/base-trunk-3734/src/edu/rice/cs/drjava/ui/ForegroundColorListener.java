

package edu.rice.cs.drjava.ui;

import javax.swing.text.JTextComponent;
import java.awt.Component;
import java.awt.Color;

import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;


class ForegroundColorListener implements OptionListener<Color> {
  final Component _target;

  public ForegroundColorListener(Component target) {
    _target = target;

    Color color = DrJava.getConfig().getSetting
      (OptionConstants.DEFINITIONS_NORMAL_COLOR);
    _setColor(color);

    DrJava.getConfig().addOptionListener
      (OptionConstants.DEFINITIONS_NORMAL_COLOR, this);
  }

  public void optionChanged(OptionEvent<Color> oce) {
    _setColor(oce.value);
  }

  private void _setColor(Color c) {
    _target.setForeground(c);

    if (_target instanceof JTextComponent) {
      ((JTextComponent) _target).setCaretColor(c);
    }
  }
}