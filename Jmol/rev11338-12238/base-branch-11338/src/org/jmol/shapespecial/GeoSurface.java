

package org.jmol.shapespecial;

import org.jmol.shape.Dots;

public class GeoSurface extends Dots {
  
 public void initShape() {
   super.initShape();
   isSurface = true;
   translucentAllowed = true;
  }

}
