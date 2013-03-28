
package org.jmol.jvxl.readers;

import java.io.BufferedReader;
import java.io.IOException;

import javax.vecmath.Point3f;

import org.jmol.api.Interface;
import org.jmol.api.SymmetryInterface;
import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.util.Logger;
import org.jmol.viewer.Viewer;

class XplorReader extends VolumeFileReader {

  
  XplorReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    isAngstroms = false;
    jvxlData.wasCubic = true;
  }

  protected int readVolumetricHeader() {
    try {
        readTitleLines();
        Logger.info(jvxlFileHeaderBuffer.toString());
        readAtomCountAndOrigin();
        readVoxelVectors();
        if (isAnisotropic)
          setVolumetricAnisotropy();
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
    int nLines = parseInt(getLine());
    for (int i = nLines; --i >= 0; ) {
      line = br.readLine().trim();
      jvxlFileHeaderBuffer.append("# ").append(line).append('\n');
    }
    jvxlFileHeaderBuffer.append("Xplor data\nJmol " + Viewer.getJmolVersion() + '\n');
  }

  int nBlock;
  protected void readVoxelVectors() throws Exception {
    
    
    int nA = parseInt(getLine());
    int minA = parseInt();
    int maxA = parseInt();
    int nB = parseInt();
    int minB = parseInt();
    int maxB = parseInt();
    int nC = parseInt();
    int minC = parseInt();
    int maxC = parseInt();
    
    voxelCounts[0] = maxC - minC + 1;
    voxelCounts[1] = maxB - minB + 1;
    voxelCounts[2] = maxA - minA + 1;

    nBlock = voxelCounts[2] * voxelCounts[1];
    
    float a = parseFloat(getLine());
    float b = parseFloat();
    float c = parseFloat();
    float alpha = parseFloat();
    float beta = parseFloat();
    float gamma = parseFloat();

    SymmetryInterface symmetry = (SymmetryInterface) Interface.getOptionInterface("symmetry.Symmetry");
    symmetry.setUnitCell(new float[] {a, b, c, alpha, beta, gamma});
    Point3f pt;
    
    pt = new Point3f(0, 0, 1f/nC);
    symmetry.toCartesian(pt);
    volumetricVectors[0].set(pt);
    pt = new Point3f(0, 1f/nB, 0);
    symmetry.toCartesian(pt);
    volumetricVectors[1].set(pt);
    pt = new Point3f(1f/nA, 0, 0);
    symmetry.toCartesian(pt);
    volumetricVectors[2].set(pt);
    if (isAnisotropic)
      setVolumetricAnisotropy();
 
    
    
    getLine();
    
  }

  protected void readAtomCountAndOrigin() throws Exception {
    atomCount = 0;
    negativeAtomCount = false;    
    volumetricOrigin.set(0, 0, 0);
  }
  
  private String getLine() throws IOException {
    line = br.readLine();
    while (line != null && (line.length() == 0 || line.indexOf("REMARKS") >= 0 || line.indexOf("XPLOR:") >= 0))
      line = br.readLine();
    return line;
  }
  

  int linePt = Integer.MAX_VALUE;
  int nRead;
  protected float nextVoxel() throws Exception {
    if (linePt >= line.length()) {
      line = br.readLine();
      
      linePt = 0;
      if ((nRead % nBlock) == 0) {
        System.out.println("block " + line);
        line = br.readLine();
      }
    }
    if (line == null)
      return 0;
    float val = parseFloat(line.substring(linePt, linePt+12));
    linePt += 12;
    nRead++;
    
    return val;
  }


  

}


