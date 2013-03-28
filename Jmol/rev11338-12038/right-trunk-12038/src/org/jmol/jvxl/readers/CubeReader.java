
package org.jmol.jvxl.readers;

import java.io.BufferedReader;
import org.jmol.util.Parser;

class CubeReader extends VolumeFileReader {

  CubeReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    isAngstroms = false;
    jvxlData.wasCubic = true;
    canDownsample = true;
  }

  protected void readTitleLines() throws Exception {
    jvxlFileHeaderBuffer = new StringBuffer();
    jvxlFileHeaderBuffer.append(br.readLine()).append('\n');
    jvxlFileHeaderBuffer.append(br.readLine()).append('\n');
  }

  protected void readAtomCountAndOrigin() throws Exception {
    String atomLine = br.readLine();
    String[] tokens = Parser.getTokens(atomLine, 0);
    atomCount = parseInt(tokens[0]);
    negativeAtomCount = (atomCount < 0); 
    if (negativeAtomCount)
      atomCount = -atomCount;
    volumetricOrigin.set(parseFloat(tokens[1]), parseFloat(tokens[2]),
        parseFloat(tokens[3]));
    if (isAnisotropic)
      setVolumetricOriginAnisotropy();
    VolumeFileReader.checkAtomLine(isXLowToHigh, isAngstroms, tokens[0],
        atomLine, jvxlFileHeaderBuffer);
    if (!isAngstroms)
      volumetricOrigin.scale(ANGSTROMS_PER_BOHR);
  }
}


