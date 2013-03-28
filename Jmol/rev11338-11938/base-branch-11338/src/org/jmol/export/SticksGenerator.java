

package org.jmol.export;

import org.jmol.shape.*;

public class SticksGenerator extends SticksRenderer {
  protected void renderBond(int dottedMask) {
    _Exporter exporter = (_Exporter) ((Export3D)g3d).getExporter();
    if (!exporter.use2dBondOrderCalculation 
        || (exporter.isCartesianExport && bondOrder == 1))
      
      
        exporter.fillCylinder(atomA, atomB, colixA, colixB, endcaps, mad, -1);
    else
      
      
      super.renderBond(dottedMask); 
  }
  
  protected void fillCylinder(short colixA, short colixB, byte endcaps,
                              int diameter, int xA, int yA, int zA, int xB,
                              int yB, int zB) {
    g3d.fillCylinder(colixA, colixB, endcaps, mad == 1 ? diameter : mad, xA, yA, zA, xB, yB, zB);
  }
}
