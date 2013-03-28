

package edu.rice.cs.util.swing;

import javax.swing.*;
import java.awt.*;

public class LayeredIcon implements Icon {
  private Icon[] _layers;
  private int[] _xoffs;
  private int[] _yoffs;
  private int _w=0;
  private int _h=0;
  public LayeredIcon(Icon[] layers, int[] x, int[] y) {
    _layers = layers;
    _xoffs = x;
    _yoffs = y;
    if (layers.length != x.length || x.length != y.length) {
      throw new IllegalArgumentException("Array lengths don't match");
    }
    _w = 0; _h = 0;
    for (int i=0; i < layers.length; i++) {
      if (layers[i] != null) {
        _w = Math.max(_w, layers[i].getIconWidth() + x[i]);
        _h = Math.max(_h, layers[i].getIconHeight() + x[i]);
      }
    }
  }
  public int getIconHeight(){
    return _h;
  }
  public int getIconWidth(){
    return _w;
  }
  public void paintIcon(Component c, Graphics g, int x, int y){
    for (int i=0; i < _layers.length; i++) {
      Icon ico = _layers[i];
      if (ico != null) _layers[i].paintIcon(c,g, x+_xoffs[i], y+_yoffs[i]);
    }
  }
  
  public Icon[] getLayers(){
    return _layers;
  }
  public int[] getXOffsets() {
    return _xoffs;
  }
  public int[] getYOffsets(){
    return _xoffs;
  }
}