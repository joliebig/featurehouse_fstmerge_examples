
package org.jmol.jvxl.readers;

import java.io.BufferedReader;
import java.io.IOException;

import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.util.Logger;
import org.jmol.viewer.Viewer;

class PltFormattedReader extends VolumeFileReader {

  
  
  PltFormattedReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    isAngstroms = true;
    jvxlData.wasCubic = true;
  }

  protected int readVolumetricHeader() {
    try {
        readTitleLines();
        readAtomCountAndOrigin();
        Logger.info(jvxlFileHeaderBuffer.toString());
        readVoxelVectors();
        Logger.info("voxel grid origin:" + volumetricOrigin);
        for (int i = 0; i < 3; ++i)
          Logger.info("voxel grid vector:" + volumetricVectors[i]);
        JvxlCoder.jvxlCreateHeaderWithoutTitleOrAtoms(volumeData, jvxlFileHeaderBuffer);
      return readExtraLine();
    } catch (Exception e) {
      Logger.error(e.toString());
      throw new NullPointerException();
    }
  }
   
  protected void readTitleLines() throws Exception {
    jvxlFileHeaderBuffer = new StringBuffer();
  }

  protected void readAtomCountAndOrigin() throws Exception {
    atomCount = 0;
    negativeAtomCount = false;    
    int n1 = parseInt(getLine());
    int n2 = parseInt();
    
    nPointsX = parseInt(getLine());
    nPointsY = parseInt();
    nPointsZ = parseInt();
    jvxlFileHeaderBuffer.append("Plt formatted data (" + n1 + "," + n2 + ") "
        + nPointsX + " x " + nPointsY + " x " + nPointsZ + " \nJmol " + Viewer.getJmolVersion() + '\n');    
    volumetricOrigin.set(0, 0, 0);
  }
  
  protected void readVoxelVectors() throws Exception {
    

    float xmin = parseFloat(getLine().substring(0, 12));
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
    
    Logger.info("boundbox corners {" 
        + zmin + " " + ymin + " " + xmin 
        + "} {" 
        + zmax + " " + ymax + " " + xmax +" }");
  }

  private String getLine() throws IOException {
    return line = br.readLine();
  }  
}


