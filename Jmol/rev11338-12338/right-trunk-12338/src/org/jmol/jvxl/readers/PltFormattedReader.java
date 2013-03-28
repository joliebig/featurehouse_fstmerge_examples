
package org.jmol.jvxl.readers;

import java.io.BufferedReader;

import org.jmol.viewer.Viewer;

class PltFormattedReader extends VolumeFileReader {

  
  
  PltFormattedReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    isAngstroms = true;
    jvxlData.wasCubic = true;
    jvxlFileHeaderBuffer = new StringBuffer();
    nSurfaces = 1;
  }

  
  protected void readParameters() throws Exception {
    int n1 = parseInt(readLine());
    int n2 = parseInt();
    
    nPointsX = parseInt(readLine());
    nPointsY = parseInt();
    nPointsZ = parseInt();
    jvxlFileHeaderBuffer.append("Plt formatted data (" + n1 + "," + n2 + ") "
        + nPointsX + " x " + nPointsY + " x " + nPointsZ + " \nJmol " + Viewer.getJmolVersion() + '\n');    
    volumetricOrigin.set(0, 0, 0);
    

    float xmin = parseFloat(readLine().substring(0, 12));
    float xmax = parseFloat(line.substring(12,24));
    float ymin = parseFloat(line.substring(24,36));
    float ymax = parseFloat(line.substring(36,48));
    float zmin = parseFloat(line.substring(48,60));
    float zmax = parseFloat(line.substring(60,72));
    volumetricOrigin.set(xmin, ymin, zmin);
    voxelCounts[0] = nPointsX;
    voxelCounts[1] = nPointsY;
    voxelCounts[2] = nPointsZ;

    
    
    volumetricVectors[0].set(0, 0, (xmax - xmin)/nPointsX);
    volumetricVectors[1].set(0, (ymax - ymin)/nPointsY, 0);
    volumetricVectors[2].set((zmax - zmin)/nPointsZ, 0, 0);
    
  }
}


