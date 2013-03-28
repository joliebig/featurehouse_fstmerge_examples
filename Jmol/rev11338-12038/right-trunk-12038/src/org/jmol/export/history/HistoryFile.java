
package org.jmol.export.history;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;


public class HistoryFile {

  
  private Properties properties = new Properties();

  
  File file;

  
  String header;

  
  public HistoryFile(File file, String header) {
    this.file = file;
    this.header = header;
    load();
  }

  
  public void addProperties(Properties properties) {

    Enumeration keys = properties.keys();
    boolean modified = false;
    while (keys.hasMoreElements()) {
      String key = (String) keys.nextElement();
      String value = properties.getProperty(key);
      modified |= addProperty(key, value);
    }
    save();
  }

  
  public Properties getProperties() {
    return new Properties(properties);
  }

  
  public String getProperty(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }

  
  private boolean addProperty(String key, String value) {
    boolean modified = false;
    Object oldValue = properties.setProperty(key, value);
    if (!value.equals(oldValue)) {
        modified = true;
    }
    return modified;
  }

  
  public Point getWindowPosition(String name) {
    Point result = null;
    if (name != null) {
      try {
        String x = getProperty("Jmol.window." + name + ".x", null);
        String y = getProperty("Jmol.window." + name + ".y", null);
        if ((x != null) && (y != null)) {
          int posX = Integer.parseInt(x);
          int posY = Integer.parseInt(y);
          result = new Point(posX, posY);
        }
      } catch (Exception e) {
        
      }
    }
    return result;
  }

  
  public Point getWindowBorder(String name) {
    Point result = null;
      try {
        String x = getProperty("Jmol.windowBorder."+name+".x", null);
        String y = getProperty("Jmol.windowBorder."+name+".y", null);
        if ((x != null) && (y != null)) {
          int X = Integer.parseInt(x);
          int Y = Integer.parseInt(y);
          result = new Point(X, Y);
        }
      } catch (Exception e) {
        
      }
    return result;
  }

  
  public Dimension getWindowSize(String name) {
    Dimension result = null;
    if (name != null) {
      try {
        String w = getProperty("Jmol.window." + name + ".w", null);
        String h = getProperty("Jmol.window." + name + ".h", null);
        if ((w != null) && (h != null)) {
          int dimW = Integer.parseInt(w);
          int dimH = Integer.parseInt(h);
          result = new Dimension(dimW, dimH);
        }
      } catch (Exception e) {
        
      }
    }
    return result;
  }

  
  public Boolean getWindowVisibility(String name) {
    Boolean result = null;
    if (name != null) {
      try {
        String v = getProperty("Jmol.window." + name + ".visible", null);
        if (v != null) {
          result = Boolean.valueOf(v);
        }
      } catch (Exception e) {
        
      }
    }
    return result;
  }

  
  private boolean addWindowPosition(String name, Point position) {
    boolean modified = false;
    if (name != null) {
      if (position != null) {
        modified |= addProperty("Jmol.window." + name + ".x", "" + position.x);
        modified |= addProperty("Jmol.window." + name + ".y", "" + position.y);
      }
    }
    return modified;
  }


  
  private boolean addWindowBorder(String name, Point border) {
    boolean modified = false;
    if (name != null && border != null) {
      modified |= addProperty("Jmol.windowBorder." + name + ".x", "" + border.x);
      modified |= addProperty("Jmol.windowBorder." + name + ".y", "" + border.y);
    }
    return modified;
  }

  
  private boolean addWindowSize(String name, Dimension size) {
    boolean modified = false;
    if (name != null) {
      if (size != null) {
        modified |= addProperty("Jmol.window." + name + ".w", "" + size.width);
        modified |= addProperty("Jmol.window." + name + ".h", "" + size.height);
      }
    }
    return modified;
  }

  
  private boolean addWindowVisibility(String name, boolean visible) {
    boolean modified = false;
    if (name != null) {
      modified |= addProperty("Jmol.window." + name + ".visible", "" + visible);
    }
    return modified;
  }

  
  public void addWindowInfo(String name, Component window, Point border) {
    if (window != null) {
      boolean modified = false;
      modified |= addWindowPosition(name, window.getLocation());
      modified |= addWindowSize(name, window.getSize());
      modified |= addWindowBorder(name, border);
      modified |= addWindowVisibility(name, window.isVisible());
      if (modified) {
        save();
      }
    }
  }

  
  public void repositionWindow(String name, Component window, 
                        int minWidth, int minHeight) {
    if (window != null) {
      Point position = getWindowPosition(name);
      Dimension size = getWindowSize(name);
      Boolean visible = getWindowVisibility(name);
      if (position != null) {
        window.setLocation(position);
      }
      if (size != null) {
        if (size.width < minWidth)
          size.width = minWidth;
        if (size.height < minHeight)
          size.height = minHeight;
        window.setSize(size);
      }
      if ((visible != null) && (visible.equals(Boolean.TRUE))) {
        window.setVisible(true);
      }
    }
  }

  
  public void repositionWindow(String name, Component window) {
    repositionWindow(name, window, 10, 10);
  }

  public File getFile() {
    return file;
  }
  
  
  private void load() {

    try {
      FileInputStream input = new FileInputStream(file);
      properties.load(input);
      input.close();
    } catch (IOException ex) {
      
    }
  }

  
  private void save() {

    try {
      FileOutputStream output = new FileOutputStream(file);
      properties.store(output, header);
      output.close();
    } catch (IOException ex) {
      System.err.println("Error saving history: " + ex);
    }
  }

}
