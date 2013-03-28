
package org.jmol.jvxl.readers;

import java.io.BufferedReader;

import org.jmol.util.Parser;


class JaguarReader extends VolumeFileReader {

  JaguarReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    nSurfaces = 1;
    
  }

  

  
  protected void readParameters() throws Exception {
    jvxlFileHeaderBuffer = new StringBuffer();
    jvxlFileHeaderBuffer.append("Jaguar data\n");
    jvxlFileHeaderBuffer.append("\n");
    String atomLine;
    while ((atomLine = readLine()) != null
        && atomLine.indexOf("origin=") < 0) {
    }
    String[] tokens = Parser.getTokens(atomLine, 0);
    if (tokens.length == 4 && tokens[0].equals("origin=")) {
      volumetricOrigin.set(parseFloat(tokens[1]), parseFloat(tokens[2]),
          parseFloat(tokens[3]));
      VolumeFileReader
          .checkAtomLine(isXLowToHigh, isAngstroms, "0", "0 " + tokens[1]
              + " " + tokens[2] + " " + tokens[3], jvxlFileHeaderBuffer);
      if (!isAngstroms)
        volumetricOrigin.scale(ANGSTROMS_PER_BOHR);
    }
    
    readExtents(0);
    readExtents(1);
    readExtents(2);
    
    tokens = Parser.getTokens(readLine());
    voxelCounts[0] = parseInt(tokens[1]);
    voxelCounts[1] = parseInt(tokens[2]);
    voxelCounts[2] = parseInt(tokens[3]);
    float factor = (isAngstroms ? 1 : ANGSTROMS_PER_BOHR);
    float d = extents[0] / (voxelCounts[0] - 1);
    volumetricVectors[0].set(d * factor, 0, 0);
    jvxlFileHeaderBuffer.append(voxelCounts[0] + " " + d + " 0.0 0.0\n");

    d = extents[1] / (voxelCounts[1] - 1);
    volumetricVectors[1].set(0, d * factor, 0);
    jvxlFileHeaderBuffer.append(voxelCounts[1] + " 0.0 " + d + " 0.0\n");

    d = extents[2] / (voxelCounts[2] - 1);
    volumetricVectors[2].set(0, 0, d * factor);
    jvxlFileHeaderBuffer.append(voxelCounts[2] + " 0.0 0.0 " + d + "\n");

    
    
    
    
    
    readLine();

  }

  private float[] extents = new float[3];
  
  
  private void readExtents(int voxelVectorIndex) throws Exception {
    String[] tokens = Parser.getTokens(readLine());
    extents[voxelVectorIndex] = parseFloat(tokens[voxelVectorIndex + 1]);
  }
}
