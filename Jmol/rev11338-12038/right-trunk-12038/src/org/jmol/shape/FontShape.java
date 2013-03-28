

package org.jmol.shape;

import org.jmol.g3d.Font3D;

import java.util.BitSet;

public abstract class FontShape extends Shape {

  

  Font3D font3d;
  protected String myType;

  public void initShape() {
    translucentAllowed = false;
  }

  public void setProperty(String propertyName, Object value, BitSet bs) {
    if ("font" == propertyName) {
      font3d = (Font3D) value;
      return;
    }
  }

  public String getShapeState() {
    String s = viewer.getObjectState(myType);
    String fcmd = Shape.getFontCommand(myType, font3d);
      if (fcmd.length() > 0)
        fcmd = "  " + fcmd + ";\n"; 
    return (s.length() < 3 ? "" : s + fcmd);
  }
}
