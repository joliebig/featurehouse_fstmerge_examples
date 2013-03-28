
package org.jmol.modelset;


import javax.vecmath.Point3f;

public class TickInfo {
  
  public TickInfo(Point3f ticks) {
    this.ticks = ticks;
  }

  public String id = "";
  public String type = " ";
  public Point3f ticks;
  public String[] tickLabelFormats;
  public Point3f scale;
  public float first; 
  public float signFactor = 1;
  public Point3f reference;
}

