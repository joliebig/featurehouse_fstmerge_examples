
package org.jmol.jvxl.readers;

import javax.vecmath.Point4f;
import java.io.BufferedReader;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.util.Escape;
import org.jmol.g3d.Graphics3D;
import org.jmol.jvxl.data.JvxlCoder;

public class JvxlReader extends JvxlXmlReader {

  
  
  
  
  
  
  JvxlReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    isXmlFile = false;
    JVXL_VERSION = "2.0";
  }

  

  

  protected void readTitleLines() throws Exception {
    jvxlFileHeaderBuffer = new StringBuffer(skipComments(false));
    if (line == null || line.length() == 0)
      line = "Line 1";
    jvxlFileHeaderBuffer.append(line).append('\n');
    if ((line = br.readLine()) == null || line.length() == 0)
      line = "Line 2";
    jvxlFileHeaderBuffer.append(line).append('\n');
  }

  
  protected void readAtomCountAndOrigin() throws Exception {
      jvxlFileHeaderBuffer.append(skipComments(false));
      String atomLine = line;
      String[] tokens = Parser.getTokens(atomLine, 0);
      isXLowToHigh = false;
      negativeAtomCount = true;
      atomCount = 0;
      if (tokens[0] == "-0") {
      } else if (tokens[0].charAt(0) == '+'){
        isXLowToHigh = true;
        atomCount = parseInt(tokens[0].substring(1));
      } else {
        atomCount = -parseInt(tokens[0]);
      }
      if (atomCount == Integer.MIN_VALUE)
        return;
      volumetricOrigin.set(parseFloat(tokens[1]), parseFloat(tokens[2]), parseFloat(tokens[3]));
      isAngstroms = VolumeFileReader.checkAtomLine(isXLowToHigh, isAngstroms, null, atomLine, jvxlFileHeaderBuffer);
      if (!isAngstroms)
        volumetricOrigin.scale(ANGSTROMS_PER_BOHR);
  }

  protected void readVoxelVector(int voxelVectorIndex) throws Exception {
    readVolumeFileVoxelVector(voxelVectorIndex);
  }

  protected int readExtraLine() throws Exception {
    skipComments(true);
    Logger.info("Reading extra JVXL information line: " + line);
    int nSurfaces = parseInt(line);
    if (!(isJvxl = (nSurfaces < 0)))
      return nSurfaces;
    nSurfaces = -nSurfaces;
    Logger.info("jvxl file surfaces: " + nSurfaces);
    int ich;
    if ((ich = parseInt()) == Integer.MIN_VALUE) {
      Logger.info("using default edge fraction base and range");
    } else {
      edgeFractionBase = ich;
      edgeFractionRange = parseInt();
    }
    if ((ich = parseInt()) == Integer.MIN_VALUE) {
      Logger.info("using default color fraction base and range");
    } else {
      colorFractionBase = ich;
      colorFractionRange = parseInt();
    }
    cJvxlEdgeNaN = (char)(edgeFractionBase + edgeFractionRange);
    return nSurfaces;
  }

  protected String jvxlReadData(String type, int nPoints) {
    String str = "";
    try {
      while (str.length() < nPoints) {
        line = br.readLine();
        str += JvxlCoder.jvxlUncompressString(line);
      }
    } catch (Exception e) {
      Logger.error("Error reading " + type + " data " + e);
      throw new NullPointerException();
    }
    return str;
  }

  protected void gotoData(int n, int nPoints) throws Exception {

    

    if (n > 0)
      Logger.info("skipping " + n + " data sets, " + nPoints + " points each");
    vertexDataOnly = jvxlData.vertexDataOnly = (nPoints == 0);
    for (int i = 0; i < n; i++) {
      jvxlReadDefinitionLine(true);
      Logger.info("JVXL skipping: jvxlSurfaceDataCount=" + surfaceDataCount
          + " jvxlEdgeDataCount=" + edgeDataCount
          + " jvxlDataIsColorMapped=" + jvxlDataIsColorMapped);
      jvxlSkipData(nPoints, true);
    }
    jvxlReadDefinitionLine(true);
  }

  private void jvxlReadDefinitionLine(boolean showMsg) throws Exception {
    
    
    String comment = skipComments(true);
    if (showMsg)
      Logger.info("reading jvxl data set: " + comment + line);
    haveContourData = (comment.indexOf("+contourlines") >= 0);
    jvxlCutoff = parseFloat(line);
    Logger.info("JVXL read: cutoff " + jvxlCutoff);

    
    
    
    
    
    
    
    
    
    
    
    

    
    
    
    
    
    
    

    int param1 = parseInt();
    int param2 = parseInt();
    int param3 = parseInt();
    if (param3 == Integer.MIN_VALUE || param3 == -1)
      param3 = 0;

    if (param1 == -1) {
      
      try {
        params.thePlane = new Point4f(parseFloat(), parseFloat(), parseFloat(),
            parseFloat());
      } catch (Exception e) {
        Logger
            .error("Error reading 4 floats for PLANE definition -- setting to 0 0 1 0  (z=0)");
        params.thePlane = new Point4f(0, 0, 1, 0);
      }
      Logger.info("JVXL read: plane " + params.thePlane);
      if (param2 == -1 && param3 < 0)
        param3 = -param3;
      
    } else {
      params.thePlane = null;
    }
    if (param1 < 0 && param2 != -1) {
      
      
      params.isContoured = (param3 != 0);
      int nContoursRead = parseInt();
      if (nContoursRead == Integer.MIN_VALUE) {
        if (line.charAt(next[0]) == '[') {
           jvxlData.contourValues = params.contoursDiscrete = parseFloatArray();
           Logger.info("JVXL read: contourValues " + Escape.escapeArray(jvxlData.contourValues));            
           jvxlData.contourColixes = params.contourColixes = Graphics3D.getColixArray(getNextQuotedString());
           jvxlData.contourColors = Graphics3D.getHexCodes(jvxlData.contourColixes);
           Logger.info("JVXL read: contourColixes " + jvxlData.contourColors); 
           params.nContours = jvxlData.contourValues.length;
                 }
      } else {
        if (nContoursRead < 0) {
          nContoursRead = -1 - nContoursRead;
          params.contourFromZero = false; 
        }
        if (nContoursRead != 0 && params.nContours == 0) {
          params.nContours = nContoursRead;
          Logger.info("JVXL read: contours " + params.nContours);
        }
      }
    } else {
      params.isContoured = false;
    }

    jvxlData.isJvxlPrecisionColor = (param1 == -1 && param2 == -2 
        || param3 < 0);
    params.isBicolorMap = (param1 > 0 && param2 < 0);
    jvxlDataIsColorMapped = (param3 != 0);
    jvxlDataIs2dContour = (jvxlDataIsColorMapped && params.isContoured);

    if (params.isBicolorMap || params.colorBySign)
      jvxlCutoff = 0;
    surfaceDataCount = (param1 < -1 ? -1 - param1 : param1 > 0 ? param1 : 0);
    
    
    
    if (param1 == -1)
      edgeDataCount = 0; 
    else
      edgeDataCount = (param2 < -1 ? -param2 : param2 > 0 ? param2 : 0);
    colorDataCount = (params.isBicolorMap ? -param2 : param3 < -1 ? -param3
        : param3 > 0 ? param3 : 0);
    if (params.colorBySign)
      params.isBicolorMap = true;
    float dataMin = Float.NaN;
    float dataMax = Float.NaN;
    float red = Float.NaN;
    float blue = Float.NaN;
    boolean insideOut = (line.indexOf("insideOut") >= 0);
    if (jvxlDataIsColorMapped) {
      dataMin = parseFloat();
      dataMax = parseFloat();
      red = parseFloat();
      blue = parseFloat();
    }
    jvxlSetColorRanges(dataMin, dataMax, red, blue, insideOut);
  }

  protected void readSurfaceData(boolean isMapDataIgnored) throws Exception {
    thisInside = !params.isContoured;
    if (readSurfaceData())
      return;
    readVolumeFileSurfaceData();
  }

  protected void jvxlSkipData(int nPoints, boolean doSkipColorData)
      throws Exception {
    
    
    if (surfaceDataCount > 0) 
      jvxlSkipDataBlock(nPoints, true);
    if (edgeDataCount > 0)
      jvxlSkipDataBlock(edgeDataCount, false);
    if (jvxlDataIsColorMapped && doSkipColorData)
      jvxlSkipDataBlock(colorDataCount, false);
  }

  private void jvxlSkipDataBlock(int nPoints, boolean isInt) throws Exception {
    int n = 0;
    while (n < nPoints) {
      line = br.readLine();
      n += (isInt ? countData(line) : JvxlCoder.jvxlUncompressString(line).length());
    }
  }

  private int countData(String str) {
    int count = 0;
    int n = parseInt(str);
    while (n != Integer.MIN_VALUE) {
      count += n;
      n = parseIntNext(str);
    }
    return count;
  }
}
