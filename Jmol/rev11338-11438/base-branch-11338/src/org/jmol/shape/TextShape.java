

package org.jmol.shape;

import org.jmol.util.Logger;

import org.jmol.g3d.*;

import java.util.BitSet;
import java.util.Enumeration;

public abstract class TextShape extends Object2dShape {

  
  
  public void setProperty(String propertyName, Object value, BitSet bsSelected) {

    if (Logger.debugging) {
      Logger.debug("TextShape.setProperty(" + propertyName + "," + value + ")");
    }

    if ("text" == propertyName) {
      String text = (String) value;
      if (currentObject == null) {
        if (isAll) {
          Enumeration e = objects.elements();
          while (e.hasMoreElements())
            ((Text) e.nextElement()).setText(text);
        }
        return;
      }
      ((Text) currentObject).setText(text);
      return;
    }

    if ("font" == propertyName) {
      currentFont = (Font3D) value;
      if (currentObject == null) {
        if (isAll) {
          Enumeration e = objects.elements();
          while (e.hasMoreElements())
            ((Text) e.nextElement()).setFont(currentFont);
        }
        return;
      }
      ((Text) currentObject).setFont(currentFont);
      ((Text) currentObject).setFontScale(0);
      return;
    }
    
    super.setProperty(propertyName, value, bsSelected);
  }
}

