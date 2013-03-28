

package edu.rice.cs.drjava.ui;

import java.awt.Component;
import java.awt.Color;

import edu.rice.cs.drjava.config.*;
import edu.rice.cs.drjava.*;


class BackgroundColorListener implements OptionListener<Color> {
  final Component _target;

  public BackgroundColorListener(Component target) {
    _target = target;

    Color color = DrJava.getConfig().getSetting(OptionConstants.DEFINITIONS_BACKGROUND_COLOR);
    _target.setBackground(color);

    DrJava.getConfig().addOptionListener(OptionConstants.DEFINITIONS_BACKGROUND_COLOR, this);
  }

  public void optionChanged(OptionEvent<Color> oce) { _target.setBackground(oce.value); }
}