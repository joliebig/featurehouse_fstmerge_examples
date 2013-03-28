
package org.jmol.api;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.Rectangle;

import org.jmol.viewer.Viewer;



abstract public class JmolSimpleViewer {

  
  static public JmolSimpleViewer
    allocateSimpleViewer(Component awtComponent, JmolAdapter jmolAdapter) {
    return Viewer.allocateViewer(awtComponent, jmolAdapter, 
        null, null, null, null, null);
  }

  abstract public void renderScreenImage(Graphics g, Dimension size,
                                         Rectangle clip);

  abstract public String evalFile(String strFilename);
  abstract public String evalString(String strScript);

  abstract public String openStringInline(String strModel);
  abstract public String openDOM(Object DOMNode);
  abstract public String openFile(String fileName);
  abstract public String openFiles(String[] fileNames);
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  abstract public Object getProperty(String returnType, String infoType, Object paramInfo);
}
