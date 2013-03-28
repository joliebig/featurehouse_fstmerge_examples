

package org.jmol.shape;

import org.jmol.g3d.*;
import org.jmol.util.Escape;

import java.util.BitSet;
import java.util.Hashtable;

import javax.vecmath.Point3i;

public class Hover extends TextShape {

  private final static String FONTFACE = "SansSerif";
  private final static String FONTSTYLE = "Plain";
  private final static int FONTSIZE = 12;

  Text hoverText;
  int atomIndex = -1;
  Point3i xy;
  String text;
  String labelFormat = "%U";
  String[] atomFormats;
  String specialLabel;

  public void initShape() {
    super.initShape();
    isHover = true;
    Font3D font3d = g3d.getFont3D(FONTFACE, FONTSTYLE, FONTSIZE);
    short bgcolix = Graphics3D.getColix("#FFFFC3"); 
    short colix = Graphics3D.BLACK;
    currentObject = hoverText = new Text(g3d, font3d, null, colix, bgcolix, 0, 0,
        1, Integer.MIN_VALUE, Object2d.ALIGN_LEFT, 0);
    hoverText.setAdjustForWindow(true);
  }

  public void setProperty(String propertyName, Object value, BitSet bsSelected) {

    
      
    

    if ("target" == propertyName) {
      if (value == null)
        atomIndex = -1;
      else {
        atomIndex = ((Integer) value).intValue();
        viewer
            .setStatusAtomHovered(atomIndex, viewer.getAtomInfoXYZ(atomIndex, false));
      }
      return;
    }

    if ("text" == propertyName) {
      text = (String) value;
      if (text != null && text.length() == 0)
        text = null;
      return;
    }

    if ("specialLabel" == propertyName) {
      specialLabel = (String) value;
      return;
    }

    if ("atomLabel" == propertyName) {
      String text = (String) value;
      if (text != null && text.length() == 0)
        text = null;
      int count = viewer.getAtomCount();
      if (atomFormats == null || atomFormats.length < count)
        atomFormats = new String[count];
      for (int i = bsSelected.nextSetBit(0); i >= 0; i = bsSelected.nextSetBit(i + 1))
        atomFormats[i] = text;
      return;
    }

    if ("xy" == propertyName) {
      xy = (Point3i) value;
      return;
    }

    if ("label" == propertyName) {
      labelFormat = (String) value;
      if (labelFormat != null && labelFormat.length() == 0)
        labelFormat = null;
      return;
    }

    if (propertyName == "deleteModelAtoms") {
      atomIndex = -1;
      return;
    }
    
    super.setProperty(propertyName, value, null);

  }

  public String getShapeState() {
    Hashtable temp = new Hashtable();
    int atomCount = viewer.getAtomCount();
    if (atomFormats != null)
      for (int i = atomCount; --i >= 0;)
        if (atomFormats[i] != null)
          setStateInfo(temp, i, "set hoverLabel " + Escape.escape(atomFormats[i]));
    return "\n  hover " + Escape.escape((labelFormat == null ? "" : labelFormat)) 
    + ";\n" + getShapeCommands(temp, null, atomCount);
  }
}
