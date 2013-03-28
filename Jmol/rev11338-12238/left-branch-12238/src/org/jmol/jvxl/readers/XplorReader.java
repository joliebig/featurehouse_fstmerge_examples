
package org.jmol.jvxl.readers;

import java.io.BufferedReader;
import java.io.IOException;

import org.jmol.util.Logger;
import org.jmol.viewer.Viewer;

class XplorReader extends MapFileReader {

  

  XplorReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    params.insideOut = !params.insideOut;
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

    if (params.cutoff == 0.02f)
      params.cutoff = 1.0f;
    
    getVectorsAndOrigin();      

    nBlock = voxelCounts[2] * voxelCounts[1];
  }


  private String getLine() throws IOException {
    line = br.readLine();
    while (line != null && (line.length() == 0 || line.indexOf("REMARKS") >= 0 || line.indexOf("XPLOR:") >= 0))
      line = br.readLine();
    return line;
  }
  
  private int linePt = Integer.MAX_VALUE;
  private int nRead;
  
  protected float nextVoxel() throws Exception {
    if (linePt >= line.length()) {
      line = br.readLine();
      
      linePt = 0;
      if ((nRead % nBlock) == 0) {
        
          
           
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


