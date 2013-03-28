

package org.jmol.shapespecial;

import java.util.BitSet;

import org.jmol.shape.AtomShape;

public class Vectors extends AtomShape {

 protected void initModelSet() {
    if (!(isActive = modelSet.modelSetHasVibrationVectors()))
      return;
    super.initModelSet();
  }

 public void setProperty(String propertyName, Object value, BitSet bsSelected) {
    if (!isActive)
      return;
    super.setProperty(propertyName, value, bsSelected);
  }
  
 public Object getProperty(String propertyName, int param) {
   if (propertyName == "mad")
     return new Integer(mads == null || param < 0 || mads.length <= param ? 0 : mads[param]);
   return super.getProperty(propertyName, param);
 }

 public String getShapeState() {
    return (isActive ? super.getShapeState() : "");
  }
}
