
package org.jmol.jvxl.readers;

import java.io.BufferedReader;

import org.jmol.util.Parser;

class ApbsReader extends VolumeFileReader {

  ApbsReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    jvxlData.wasCubic = isApbsDx = true;
  }
  
  protected void readTitleLines() throws Exception {
    jvxlFileHeaderBuffer = new StringBuffer(skipComments(false));
    while (line != null && line.length() == 0)
      br.readLine();
    jvxlFileHeaderBuffer.append("APBS OpenDx DATA ").append(line).append("\n");
    jvxlFileHeaderBuffer.append("see http://apbs.sourceforge.net\n");
    isAngstroms = true;
  }
  
  protected void readAtomCountAndOrigin() throws Exception {
    String atomLine = br.readLine();
    String[] tokens = Parser.getTokens(atomLine, 0);
    negativeAtomCount = false;
    atomCount = 0;
    if (tokens.length >= 4)
      volumetricOrigin.set(parseFloat(tokens[1]), parseFloat(tokens[2]),
          parseFloat(tokens[3]));
    VolumeFileReader.checkAtomLine(isXLowToHigh, isAngstroms, tokens[0],
        atomLine, jvxlFileHeaderBuffer);
  }

  protected void readVoxelVector(int voxelVectorIndex) throws Exception {
    super.readVoxelVector(voxelVectorIndex);
    if (voxelVectorIndex == 2) {
      line = br.readLine();
      String[] tokens = getTokens();
      
      for (int i = 0; i < 3; i++)
        voxelCounts[i] = parseInt(tokens[i + 5]);
      br.readLine();
    }
  }
}
