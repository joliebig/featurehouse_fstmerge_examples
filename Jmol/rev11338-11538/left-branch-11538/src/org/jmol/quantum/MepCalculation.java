
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
      System.out.println("process map for atom " + atomIndex + " nX,nY,nZ=" + nX + "," + nY + "," + nZ + " charge=" + charge);
      thisAtom.setXYZ(true);
      for (int ix = xMax; --ix >= xMin;) {
        float dX = X2[ix];
        for (int iy = yMax; --iy >= yMin;) {
          float dXY = dX + Y2[iy];
          for (int iz = zMax; --iz >= zMin;) {
            float d2 = dXY + Z2[iz];
            voxelData[ix][iy][iz] += (d2 == 0 ? charge
                * Float.POSITIVE_INFINITY : charge / (float) Math.sqrt(d2));
if (iy == 2 && iz == 4 && (ix == 0 || ix == 7))
  System.out.println("atom " + atomIndex + " " + thisAtom + " ix=" + ix + " data:" + voxelData[ix][iy][iz] + " x2 y2 z2 d2=" + X2[ix] + " " + Y2[iy] + " " + Z2[iz] + " " + d2);
          }
        }
      }
    }
    
  }


}
