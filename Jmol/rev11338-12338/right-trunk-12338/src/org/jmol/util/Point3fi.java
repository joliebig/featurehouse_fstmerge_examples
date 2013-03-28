

package org.jmol.util;
import javax.vecmath.Point3f;

public class Point3fi extends Point3f {
  public int index;
  public int screenX;
  public int screenY;
  public int screenZ;
  public short screenDiameter = -1;
  public short modelIndex = -1;

  
  
  public Point3fi() {
    super();
  }
  
  public Point3fi(Point3f pt) {
    super(pt);
  }

  public Point3fi(float x, float y, float z) {
    super(x, y, z);
  }

  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (obj.getClass() != this.getClass())) {
      return false;
    }
    Point3fi other = (Point3fi) obj;
    if (modelIndex != other.modelIndex
        || screenX != other.screenX 
        || screenY != other.screenY 
        || screenZ != other.screenZ)
      return false;
    return super.equals(other);
  }
  
  
  public int hashCode() {
    int hash = super.hashCode();
    hash = 31 * hash + screenX;
    hash = 31 * hash + screenY;
    hash = 31 * hash + screenZ;
    return hash;
  }
}
