
package org.jmol.jvxl.readers;

import java.io.BufferedReader;

import org.jmol.util.Logger;
import org.jmol.util.Parser;

class CubeReader extends VolumeFileReader {

  CubeReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
  }
  
  protected void readParameters() throws Exception {
    jvxlFileHeaderBuffer = new StringBuffer();
    jvxlFileHeaderBuffer.append(readLine()).append('\n');
    jvxlFileHeaderBuffer.append(readLine()).append('\n');
    String atomLine = readLine();
    String[] tokens = Parser.getTokens(atomLine, 0);
    atomCount = parseInt(tokens[0]);
    negativeAtomCount = (atomCount < 0); 
    if (negativeAtomCount)
      atomCount = -atomCount;
    volumetricOrigin.set(parseFloat(tokens[1]), parseFloat(tokens[2]),
        parseFloat(tokens[3]));
    VolumeFileReader.checkAtomLine(isXLowToHigh, isAngstroms, tokens[0],
        atomLine, jvxlFileHeaderBuffer);
    if (!isAngstroms)
      volumetricOrigin.scale(ANGSTROMS_PER_BOHR);
    for (int i = 0; i < 3; ++i)
      readVoxelVector(i);
    for (int i = 0; i < atomCount; ++i)
      jvxlFileHeaderBuffer.append(readLine() + "\n");

    if (!negativeAtomCount) {
      nSurfaces = 1;
    } else {
      readLine();
      Logger.info("Reading extra CUBE information line: " + line);
      nSurfaces = parseInt(line);
    }
  }  
}


