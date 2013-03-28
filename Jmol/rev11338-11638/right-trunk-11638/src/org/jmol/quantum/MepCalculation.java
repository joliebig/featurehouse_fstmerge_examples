
package org.jmol.quantum;

import java.util.BitSet;

import javax.vecmath.Point3f;

import org.jmol.api.MepCalculationInterface;
import org.jmol.api.VolumeDataInterface;


public class MepCalculation extends QuantumCalculation implements MepCalculationInterface {

  public MepCalculation() {
    rangeBohr = 15; 
  }
  
  public void calculate(VolumeDataInterface volumeData, BitSet bsSelected, Point3f[] atomCoordAngstroms, float[] charges) {
    voxelData = volumeData.getVoxelData();
    int[] countsXYZ = volumeData.getVoxelCounts();
    initialize(countsXYZ[0], countsXYZ[1], countsXYZ[2]);
    setupCoordinates(volumeData.getOriginFloat(), 
        volumeData.getVolumetricVectorLengths(), 
        bsSelected, atomCoordAngstroms);
    processMep(charges);
  }
  
  private void processMep(float[] charges) {
    for (int atomIndex = qmAtoms.length; --atomIndex >= 0;) {
      if ((thisAtom = qmAtoms[atomIndex]) == null)
        continue;
      float charge = charges[atomIndex];
      
      thisAtom.setXYZ(true);
      for (int ix = xMax; --ix >= xMin;) {
        float dX = X2[ix];
        for (int iy = yMax; --iy >= yMin;) {
          float dXY = dX + Y2[iy];
          for (int iz = zMax; --iz >= zMin;) {
            float d2 = dXY + Z2[iz];
            voxelData[ix][iy][iz] += (d2 == 0 ? charge
                * Float.POSITIVE_INFINITY : charge / (float) Math.sqrt(d2));

  
          }
        }
      }
    }
    
  }


}
