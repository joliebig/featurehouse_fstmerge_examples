
package org.jmol.jvxl.readers;

import java.io.BufferedReader;

import org.jmol.util.Logger;
import org.jmol.viewer.Viewer;

class XplorReader extends MapFileReader {

  

  XplorReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    params.insideOut = !params.insideOut;
    nSurfaces = 1;
  }

  private int nBlock;

  protected void readParameters() throws Exception {

    jvxlFileHeaderBuffer = new StringBuffer();
    int nLines = parseInt(getLine());
    for (int i = nLines; --i >= 0; ) {
      line = br.readLine().trim();
      Logger.info("XplorReader: " + line);
      jvxlFileHeaderBuffer.append("# ").append(line).append('\n');
    }
    jvxlFileHeaderBuffer.append("Xplor data\nJmol " + Viewer.getJmolVersion() + '\n');

    na = parseInt(getLine());
    nxyzStart[0] = parseInt();
    nx = parseInt() - nxyzStart[0] + 1;
    
    nb = parseInt();
    nxyzStart[1] = parseInt();
    ny = parseInt() - nxyzStart[1] + 1;
    
    nc = parseInt();
    nxyzStart[2] = parseInt();
    nz = parseInt() - nxyzStart[2] + 1;
    
    a = parseFloat(getLine());
    b = parseFloat();
    c = parseFloat();
    alpha = parseFloat();
    beta = parseFloat();
    gamma = parseFloat();

    getLine();     
    
    maps = 3;
    mapr = 2;
    mapc = 1;

    getVectorsAndOrigin();      

    nBlock = voxelCounts[2] * voxelCounts[1];
    if (params.cutoffAutomatic && params.thePlane == null) {
      params.cutoff = (boundingBox == null ? 3.0f : 1.6f);
      Logger.info("XplorReader: setting cutoff to default value of " + params.cutoff + (boundingBox == null ? " (no BOUNDBOX parameter)" : ""));
    }
    
  }


  private String getLine() throws Exception {
    readLine();
    while (line != null && (line.length() == 0 || line.indexOf("REMARKS") >= 0 || line.indexOf("XPLOR:") >= 0))
      readLine();
    return line;
  }
  
  private int linePt = Integer.MAX_VALUE;
  private int nRead;
  
  protected float nextVoxel() throws Exception {
    if (linePt >= line.length()) {
      readLine();
      
      linePt = 0;
      if ((nRead % nBlock) == 0) {
        
          
           
        readLine();
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


