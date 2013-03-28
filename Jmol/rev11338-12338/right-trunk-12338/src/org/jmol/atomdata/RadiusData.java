




package org.jmol.atomdata;

import java.util.Vector;

import org.jmol.util.XmlUtil;
import org.jmol.viewer.JmolConstants;

public class RadiusData {
  public final static int TYPE_ABSOLUTE = 0;
  public final static int TYPE_OFFSET = 1;
  public final static int TYPE_FACTOR = 2;
  public static final int TYPE_SCREEN = 3;
  private static final String[] typeNames = new String[] { "=", "+", "*", "." };
  public int type;
  public int vdwType = JmolConstants.VDW_AUTO;
  public float value = Float.NaN;
  public float valueExtended = 0;
  
  public RadiusData() {
  }

  public RadiusData(float value, int type, int vdwType) {
    this.type = type;
    this.value = value;
    this.vdwType = vdwType;
  }

  public String toString() {
    StringBuffer sb = new StringBuffer("\n");
    Vector v = new Vector();
    v.add(new String[] { "type", typeNames[type] } );
    v.add(new String[] { "value", "" + value });
    if (type == TYPE_FACTOR && value != 0 && vdwType >= 0)
      v.add(new String[] { "vdwType", vdwType + "|" + JmolConstants.getVdwLabel(vdwType) } );
    if (valueExtended != 0)
      v.add(new String[] { "plus", "" + valueExtended });
    XmlUtil.appendTag(sb, "radiusData", v.toArray());
    return sb.toString();
  }
}

