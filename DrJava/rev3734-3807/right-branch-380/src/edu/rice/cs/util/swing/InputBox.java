  

package edu.rice.cs.util.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JTextArea;
import java.awt.RenderingHints;
import javax.swing.border.Border;

import java.io.Serializable;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.config.OptionEvent;
import edu.rice.cs.drjava.config.OptionListener;


public class InputBox extends JTextArea implements Serializable {
  private static final int BORDER_WIDTH = 1;
  private static final int INNER_BUFFER_WIDTH = 3;
  private static final int OUTER_BUFFER_WIDTH = 2;
  private Color _bgColor = DrJava.getConfig().getSetting(OptionConstants.DEFINITIONS_BACKGROUND_COLOR);
  private Color _fgColor = DrJava.getConfig().getSetting(OptionConstants.DEFINITIONS_NORMAL_COLOR);
  private Color _sysInColor = DrJava.getConfig().getSetting(OptionConstants.SYSTEM_IN_COLOR);
  private boolean _antiAliasText = DrJava.getConfig().getSetting(OptionConstants.TEXT_ANTIALIAS);
  
  public InputBox() {
    setForeground(_sysInColor);
    setBackground(_bgColor);
    setCaretColor(_fgColor);
    setBorder(_createBorder());
    setLineWrap(true);
    
    DrJava.getConfig().addOptionListener(OptionConstants.DEFINITIONS_NORMAL_COLOR, new OptionListener<Color>() {
      public void optionChanged(OptionEvent<Color> oe) {
        _fgColor = oe.value;
        setBorder(_createBorder());
        setCaretColor(oe.value);
      }
    });
    DrJava.getConfig().addOptionListener(OptionConstants.DEFINITIONS_BACKGROUND_COLOR, new OptionListener<Color>() {
      public void optionChanged(OptionEvent<Color> oe) {
        _bgColor = oe.value;
        setBorder(_createBorder());
        setBackground(oe.value);
      }
    });
    DrJava.getConfig().addOptionListener(OptionConstants.SYSTEM_IN_COLOR, new OptionListener<Color>() {
      public void optionChanged(OptionEvent<Color> oe) {
        _sysInColor = oe.value;
        setForeground(oe.value);
      }
    });
    DrJava.getConfig().addOptionListener(OptionConstants.TEXT_ANTIALIAS, new OptionListener<Boolean>() {
      public void optionChanged(OptionEvent<Boolean> oce) {
        _antiAliasText = oce.value.booleanValue();
        InputBox.this.repaint();
      }
    });
  }
  private Border _createBorder() {
    Border outerouter = BorderFactory.createLineBorder(_bgColor, OUTER_BUFFER_WIDTH);
    Border outer = BorderFactory.createLineBorder(_fgColor, BORDER_WIDTH);
    Border inner = BorderFactory.createLineBorder(_bgColor, INNER_BUFFER_WIDTH);
    Border temp = BorderFactory.createCompoundBorder(outer, inner);
    return BorderFactory.createCompoundBorder(outerouter, temp);
  }
  
  protected void paintComponent(Graphics g) {
    if (_antiAliasText && g instanceof Graphics2D) {
      Graphics2D g2d = (Graphics2D)g;
      g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }
    super.paintComponent(g);
  }
}

