




package org.jmol.atomdata;

import org.jmol.viewer.JmolConstants;

public class RadiusData {
  public final static int TYPE_ABSOLUTE = 0;
  public final static int TYPE_OFFSET = 1;
  public final static int TYPE_FACTOR = 2;
  public static final int TYPE_SCREEN = 3;
  
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

}

