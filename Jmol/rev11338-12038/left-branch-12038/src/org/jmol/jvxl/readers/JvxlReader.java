
package org.jmol.jvxl.readers;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.Point4f;
import java.io.BufferedReader;
import java.util.BitSet;
import java.util.Vector;

import org.jmol.shapesurface.IsosurfaceMesh;
import org.jmol.util.*;
import org.jmol.jvxl.api.MeshDataServer;
import org.jmol.jvxl.data.JvxlData;
import org.jmol.jvxl.data.MeshData;
import org.jmol.jvxl.data.VolumeData;

public class JvxlReader extends VolumeFileReader {

  private final static String JVXL_VERSION = "2.0";
  
  
  
  
  

  
  
  final public static int defaultEdgeFractionBase = 35; 
  final public static int defaultEdgeFractionRange = 90;
  final public static int defaultColorFractionBase = 35;
  final public static int defaultColorFractionRange = 90;

  JvxlReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    jvxlData.wasJvxl = isJvxl = true;
    isXLowToHigh = false;
  }

  protected static void jvxlUpdateInfo(JvxlData jvxlData, String[] title, int nBytes) {
    jvxlData.title = title;
    jvxlData.nBytes = nBytes;
    jvxlUpdateInfoLines(jvxlData);
  }

  public static void jvxlUpdateInfoLines(JvxlData jvxlData) {
    jvxlData.jvxlDefinitionLine = jvxlGetDefinitionLine(jvxlData, false);
    jvxlData.jvxlInfoLine = jvxlGetDefinitionLine(jvxlData, true);
  }
  

  

  private int surfaceDataCount;
  private int edgeDataCount;
  private int colorDataCount;
  private boolean haveContourData;

  protected boolean readVolumeData(boolean isMapData) {
    if (!super.readVolumeData(isMapData))
      return false;
    strFractionTemp = jvxlEdgeDataRead;
    fractionPtr = 0;
    return true;
  }

  protected boolean gotoAndReadVoxelData(boolean isMapData) {
    initializeVolumetricData();
    if (nPointsX < 0 || nPointsY < 0 || nPointsZ < 0) 
      return true;
    try {
      gotoData(params.fileIndex - 1, nPointsX * nPointsY * nPointsZ);
      if (vertexDataOnly)
        return true;
      readSurfaceData(isMapData);
      if (edgeDataCount > 0)
        jvxlEdgeDataRead = jvxlReadData("edge", edgeDataCount);
      if (colorDataCount > 0)
        jvxlColorDataRead = jvxlReadData("color", colorDataCount);
      if (haveContourData)
        jvxlDecodeContourData(getXmlData("jvxlContourData", null, false));
    } catch (Exception e) {
      Logger.error(e.toString());
      try {
      br.close();
      } catch (Exception e2) {
        
      }
      return false;
    }
    return true;
  }
  
  private int nThisValue;
  private boolean thisInside;
  
  protected void initializeVoxelData() {
    thisInside = !params.isContoured;
    nThisValue = 0;
  }
  
  protected void readSurfaceData(boolean isMapDataIgnored) throws Exception {
    initializeVoxelData();
    
    if (vertexDataOnly) {
      getEncodedVertexData();
      return;
    }
    if (params.thePlane == null) {
      super.readSurfaceData(false);
      return;
    }
    volumeData.setDataDistanceToPlane(params.thePlane);
    setVolumeData(volumeData);
    params.cutoff = 0f;
    setSurfaceInfo(jvxlData, params.thePlane, 0, new StringBuffer());
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

  
  
  protected static boolean jvxlCheckAtomLine(boolean isXLowToHigh, boolean isAngstroms,
                                   String strAtomCount, String atomLine,
                                   StringBuffer bs) {
    if (strAtomCount != null) {
      int atomCount = Parser.parseInt(strAtomCount);
      if (atomCount == Integer.MIN_VALUE) {
        atomCount = 0;
        atomLine = " " + atomLine.substring(atomLine.indexOf(" ") + 1);
      } else {
        String s = "" + atomCount;
        atomLine = atomLine.substring(atomLine.indexOf(s) + s.length());
      }
      bs.append((isXLowToHigh ? "+" : "-") + Math.abs(atomCount));
    }
    int i = atomLine.indexOf("ANGSTROM");
    if (isAngstroms && i < 0)
      atomLine += " ANGSTROMS";
    else if (atomLine.indexOf("ANGSTROMS") >= 0)
      isAngstroms = true;
    i = atomLine.indexOf("BOHR");
    if (!isAngstroms && i < 0)
      atomLine += " BOHR";
    bs.append(atomLine).append('\n');
    return isAngstroms;
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
      isAngstroms = jvxlCheckAtomLine(isXLowToHigh, isAngstroms, null, atomLine, jvxlFileHeaderBuffer);
      if (!isAngstroms)
        volumetricOrigin.scale(ANGSTROMS_PER_BOHR);
  }

  protected static void jvxlReadAtoms(BufferedReader br, StringBuffer bs, int atomCount,
                            VolumeData v) throws Exception {
    
    for (int i = 0; i < atomCount; ++i)
      bs.append(br.readLine() + "\n");
    
      
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
      Logger.info("JVXL read: {" + params.thePlane.x + " " + params.thePlane.y
          + " " + params.thePlane.z + " " + params.thePlane.w + "}");
      if (param2 == -1 && param3 < 0)
        param3 = -param3;
      
    } else {
      params.thePlane = null;
    }
    if (param1 < 0 && param2 != -1) {
      
      
      params.isContoured = (param3 != 0);
      int nContoursRead = parseInt();
      if (nContoursRead != Integer.MIN_VALUE) {
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

    jvxlDataIsPrecisionColor = (param1 == -1 && param2 == -2 
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
    if (jvxlDataIsColorMapped) {
      float dataMin = parseFloat();
      float dataMax = parseFloat();
      float red = parseFloat();
      float blue = parseFloat();
      if (!Float.isNaN(dataMin) && !Float.isNaN(dataMax)) {
        if (dataMax == 0 && dataMin == 0) {
          
          dataMin = -1;
          dataMax = 1;
        }
        params.mappedDataMin = dataMin;
        params.mappedDataMax = dataMax;
        Logger.info("JVXL read: data min/max: " + params.mappedDataMin + "/"
            + params.mappedDataMax);
      }
      if (!params.rangeDefined)
        if (!Float.isNaN(red) && !Float.isNaN(blue)) {
          if (red == 0 && blue == 0) {
            
            red = -1;
            blue = 1;
          }
          params.valueMappedToRed = red;
          params.valueMappedToBlue = blue;
          params.rangeDefined = true;
        } else {
          params.valueMappedToRed = 0f;
          params.valueMappedToBlue = 1f;
          params.rangeDefined = true;
        }
      Logger.info("JVXL read: color red/blue: " + params.valueMappedToRed + "/"
          + params.valueMappedToBlue);
    }
    jvxlData.insideOut = (line.indexOf("insideOut") >= 0);
    if (params.insideOut)
      jvxlData.insideOut = !jvxlData.insideOut;
    params.insideOut = jvxlData.insideOut;
    jvxlData.valueMappedToRed = params.valueMappedToRed;
    jvxlData.valueMappedToBlue = params.valueMappedToBlue;
    jvxlData.mappedDataMin = params.mappedDataMin;
    jvxlData.mappedDataMax = params.mappedDataMax;
  }

  private String jvxlReadData(String type, int nPoints) {
    String str = "";
    try {
      while (str.length() < nPoints) {
        line = br.readLine();
        str += jvxlUncompressString(line);
      }
    } catch (Exception e) {
      Logger.error("Error reading " + type + " data " + e);
      throw new NullPointerException();
    }
    return str;
  }

  public static String jvxlCompressString(String data) {
    
    StringBuffer dataOut = new StringBuffer();
    char chLast = '\0';
    data += '\0';
    int nLast = 0;
    for (int i = 0; i < data.length(); i++) {
      char ch = data.charAt(i);
      if (ch == '\n' || ch == '\r')
        continue;
      if (ch == chLast) {
        ++nLast;
        if (ch != '~')
          ch = '\0';
      } else if (nLast > 0) {
        if (nLast < 4 || chLast == '~' || chLast == ' '
            || chLast == '\t')
          while (--nLast >= 0)
            dataOut.append(chLast);
        else 
          dataOut.append("~" + nLast + " ");
        nLast = 0;
      }
      if (ch != '\0') {
        dataOut.append(ch);
        chLast = ch;
      }
    }
    return dataOut.toString();
  }

  private static String jvxlUncompressString(String data) {
    if (data.indexOf("~") < 0)
      return data;
    StringBuffer dataOut = new StringBuffer();
    char chLast = '\0';
    int[] next = new int[1];
    for (int i = 0; i < data.length(); i++) {
      char ch = data.charAt(i);
      if (ch == '~') {
        next[0] = ++i;
        int nChar = Parser.parseInt(data, next);
        if (nChar == Integer.MIN_VALUE) {
          if (chLast == '~') {
            dataOut.append('~');
            while ((ch = data.charAt(++i)) == '~')
              dataOut.append('~');
          } else {
            Logger.error("Error uncompressing string " + data.substring(0, i)
                + "?");
          }
        } else {
          for (int c = 0; c < nChar; c++)
            dataOut.append(chLast);
          i = next[0];
        }
      } else {
        dataOut.append(ch);
        chLast = ch;
      }
    }
    return dataOut.toString();
  }

  protected BitSet getVoxelBitSet(int nPoints) throws Exception {
    BitSet bs = new BitSet();
    int bsVoxelPtr = 0;
    if (surfaceDataCount <= 0)
      return bs; 
    int nThisValue = 0;
    while (bsVoxelPtr < nPoints) {
      nThisValue = parseInt();
      if (nThisValue == Integer.MIN_VALUE) {
        line = br.readLine();
        
        
        if (line == null || (nThisValue = parseInt(line)) == Integer.MIN_VALUE) {
          if (!endOfData)
            Logger.error("end of file in JvxlReader?" + " line=" + line);
          endOfData = true;
          nThisValue = 10000;
          
        }
      } 
      thisInside = !thisInside;
      ++jvxlNSurfaceInts;
      if (thisInside)
        bs.set(bsVoxelPtr, bsVoxelPtr + nThisValue);
      bsVoxelPtr += nThisValue;
    }
    return bs;
  }
  
  protected float getNextVoxelValue(StringBuffer sb) throws Exception {

    

    if (surfaceDataCount <= 0)
      return 0f; 
    while (nThisValue == 0) {
      nThisValue = parseInt();
      if (nThisValue == Integer.MIN_VALUE) {
        line = br.readLine();
        if (line == null || (nThisValue = parseInt(line)) == Integer.MIN_VALUE) {
          if (!endOfData)
            Logger.error("end of file in JvxlReader?" + " line=" + line);
          endOfData = true;
          nThisValue = 10000;
          
        } else if (sb != null) {
          sb.append(line).append('\n');
        }
      } 
      thisInside = !thisInside;
      ++jvxlNSurfaceInts;
    }
    --nThisValue;
    return (thisInside ? 1f : 0f);
  }

  public static void setSurfaceInfoFromBitSet(JvxlData jvxlData, BitSet bs,
                                              Point4f thePlane) {
    StringBuffer sb = new StringBuffer();
    int nPoints = jvxlData.nPointsX * jvxlData.nPointsY * jvxlData.nPointsZ;
    int nSurfaceInts = jvxlEncodeBitSet(bs, nPoints, sb);
    setSurfaceInfo(jvxlData, thePlane, nSurfaceInts, sb);
  }
  
  private static int jvxlEncodeBitSet(BitSet bs, int nPoints, StringBuffer sb) {
    
    int dataCount = 0;
    int n = 0;
    boolean isset = false;
    for (int i = 0; i < nPoints; ++i) {
      if (isset == bs.get(i)) {
        dataCount++;
      } else {
        sb.append(' ').append(dataCount);
        n++;
        dataCount = 1;
        isset = !isset;
      }
    }
    sb.append(' ').append(dataCount).append('\n');
    return n;
  }

  private static BitSet jvxlDecodeBitSet(String data) {
    
    BitSet bs = new BitSet();
    int dataCount = 0;
    int ptr = 0;
    boolean isset = false;
    int[] next = new int[1];
    while ((dataCount = Parser.parseInt(data, next)) != Integer.MIN_VALUE) {
      if (isset)
        bs.set(ptr, ptr + dataCount);
      ptr += dataCount;
      isset = !isset;
    }
    return bs;
  }

  protected static void setSurfaceInfo(JvxlData jvxlData, Point4f thePlane, int nSurfaceInts, StringBuffer surfaceData) {
    jvxlData.jvxlSurfaceData = surfaceData.toString();
    if (jvxlData.jvxlSurfaceData.indexOf("--") == 0)
      jvxlData.jvxlSurfaceData = jvxlData.jvxlSurfaceData.substring(2);
    jvxlData.jvxlPlane = thePlane;
    jvxlData.nSurfaceInts = nSurfaceInts;
  }
  
  protected float getSurfacePointAndFraction(float cutoff, boolean isCutoffAbsolute, float valueA,
                         float valueB, Point3f pointA, Vector3f edgeVector, 
                         float[] fReturn, Point3f ptReturn) {
    if (edgeDataCount <= 0)
      return super.getSurfacePointAndFraction(cutoff, isCutoffAbsolute, valueA, valueB,
          pointA, edgeVector, fReturn, ptReturn);
    ptReturn.scaleAdd(fReturn[0] = jvxlGetNextFraction(edgeFractionBase, edgeFractionRange, 0.5f), 
        edgeVector, pointA);
    return fReturn[0];
  }

  private int fractionPtr;
  private String strFractionTemp = "";

  private float jvxlGetNextFraction(int base, int range, float fracOffset) {
    if (fractionPtr >= strFractionTemp.length()) {
      if (!endOfData)
        Logger.error("end of file reading compressed fraction data");
      endOfData = true;
      strFractionTemp = "" + (char) base;
      fractionPtr = 0;
    }
    return jvxlFractionFromCharacter(strFractionTemp.charAt(fractionPtr++),
        base, range, fracOffset);
  }

  protected String readColorData() {
    
    

    fractionPtr = 0;
    int vertexCount = jvxlData.vertexCount = meshData.vertexCount;
    short[] colixes = meshData.vertexColixes;
    float[] vertexValues = meshData.vertexValues;
    strFractionTemp = (isJvxl ? jvxlColorDataRead : "");
    if (isJvxl && strFractionTemp.length() == 0) {
      Logger
          .error("You cannot use JVXL data to map onto OTHER data, because it only contains the data for one surface. Use ISOSURFACE \"file.jvxl\" not ISOSURFACE .... MAP \"file.jvxl\".");
      return "";
    }
    fractionPtr = 0;
    Logger.info("JVXL reading color data mapped min/max: " + params.mappedDataMin
        + "/" + params.mappedDataMax + " for " + vertexCount + " vertices."
        + " using encoding keys " + colorFractionBase + " "
        + colorFractionRange);
    Logger.info("mapping red-->blue for " + params.valueMappedToRed + " to "
        + params.valueMappedToBlue + " colorPrecision:"
        + jvxlDataIsPrecisionColor);

    float min = (params.mappedDataMin == Float.MAX_VALUE ? defaultMappedDataMin
        : params.mappedDataMin);
    float range = (params.mappedDataMin == Float.MAX_VALUE ? defaultMappedDataMax
        : params.mappedDataMax)
        - min;
    float colorRange = params.valueMappedToBlue - params.valueMappedToRed;
    float contourPlaneMinimumValue = Float.MAX_VALUE;
    float contourPlaneMaximumValue = -Float.MAX_VALUE;
    if (colixes == null || colixes.length < vertexCount)
      meshData.vertexColixes = colixes = new short[vertexCount];
    String data = jvxlColorDataRead;
    
    int cpt = 0;
    short colixNeg = 0, colixPos = 0;
    if (params.colorBySign) {
      colixPos = ColorEncoder
          .getColorIndex(params.isColorReversed ? params.colorNeg
              : params.colorPos);
      colixNeg = ColorEncoder
          .getColorIndex(params.isColorReversed ? params.colorPos
              : params.colorNeg);
    }
    int vertexIncrement = meshData.vertexIncrement;
    
    for (int i = 0; i < vertexCount; i+= vertexIncrement) {
      float fraction, value;
      if (jvxlDataIsPrecisionColor) {
        
        
        
        
        
        
        fraction = jvxlFractionFromCharacter2(data.charAt(cpt), data.charAt(cpt
            + vertexCount), colorFractionBase, colorFractionRange);
        value = min + fraction * range;
      } else {
        
        
        fraction = jvxlFractionFromCharacter(data.charAt(cpt),
            colorFractionBase, colorFractionRange, 0.5f);
        value = params.valueMappedToRed + fraction * colorRange;
      }
      vertexValues[i] = value;
      ++cpt;
      if (value < contourPlaneMinimumValue)
        contourPlaneMinimumValue = value;
      if (value > contourPlaneMaximumValue)
        contourPlaneMaximumValue = value;
      
      
      
      if (params.isContoured) {
        marchingSquares.setContourData(i, value);
      } else if (params.colorBySign) {
        colixes[i] = ((params.isColorReversed ? value > 0 : value <= 0) ? colixNeg
            : colixPos);
      } else {
        colixes[i] = getColorIndexFromPalette(value);
      }
    }
    if (params.mappedDataMin == Float.MAX_VALUE) {
      params.mappedDataMin = contourPlaneMinimumValue;
      params.mappedDataMax = contourPlaneMaximumValue;
    }
    return data + "\n";
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

  private void jvxlSkipData(int nPoints, boolean doSkipColorData)
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
      n += (isInt ? countData(line) : jvxlUncompressString(line).length());
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

  

  protected static void jvxlCreateHeaderWithoutTitleOrAtoms(VolumeData v, StringBuffer bs) {
    jvxlCreateHeader(v, Integer.MAX_VALUE, null, null, bs);
  }

  protected static void jvxlCreateHeader(VolumeData v, int nAtoms, 
                                         Point3f[] atomXyz, int[] atomNo,
                                         StringBuffer sb) {
    
    
    if (sb.length() == 0)
      sb.append("Line 1\nLine 2\n");
    sb.append(nAtoms == Integer.MAX_VALUE ? -2 : -nAtoms).append(' ')
      .append(v.volumetricOrigin.x).append(' ')
      .append(v.volumetricOrigin.y).append(' ')
      .append(v.volumetricOrigin.z).append(" ANGSTROMS\n");
    for (int i = 0; i < 3; i++)
      sb.append(v.voxelCounts[i]).append(' ')
        .append(v.volumetricVectors[i].x).append(' ')
        .append(v.volumetricVectors[i].y).append(' ')
        .append(v.volumetricVectors[i].z).append('\n');
    if (nAtoms == Integer.MAX_VALUE) {
      jvxlAddDummyAtomList(v, sb);
      return;
    }
    nAtoms = Math.abs(nAtoms);
      for (int i = 0, n = 0; i < nAtoms; i++)
        sb.append((n = Math.abs(atomNo[i])) + " " + n + ".0 "
            + atomXyz[i].x + " " + atomXyz[i].y + " " + atomXyz[i].z + "\n");
  }
  
  private static void jvxlAddDummyAtomList(VolumeData v, StringBuffer bs) {
    Point3f pt = new Point3f(v.volumetricOrigin);
    bs.append("1 1.0 ").append(pt.x).append(' ').append(pt.y).append(' ')
        .append(pt.z).append(" //BOGUS H ATOM ADDED FOR JVXL FORMAT\n");
    for (int i = 0; i < 3; i++)
      pt.scaleAdd(v.voxelCounts[i] - 1, v.volumetricVectors[i], pt);
    bs.append("2 2.0 ").append(pt.x).append(' ').append(pt.y).append(' ')
        .append(pt.z).append(" //BOGUS He ATOM ADDED FOR JVXL FORMAT\n");
  }

  public static String jvxlGetDefinitionLine(JvxlData jvxlData, boolean isInfo) {
    String definitionLine = (jvxlData.vContours == null ? "" : "#+contourlines\n")
        + jvxlData.cutoff + " ";

    
    
    
    
    
    
    
    
    
    
    
    

    

    if (jvxlData.jvxlSurfaceData == null)
      return "";
    StringBuffer info = new StringBuffer();
    int nSurfaceInts = jvxlData.nSurfaceInts;
    int bytesUncompressedEdgeData = (jvxlData.vertexDataOnly ? 0
        : jvxlData.jvxlEdgeData.length() - 1);
    int nColorData = (jvxlData.jvxlColorData.length() - 1);
    if (isInfo && !jvxlData.vertexDataOnly) {
      info.append("\n  cutoff=\"" + jvxlData.cutoff + "\"");
      info.append("\n  pointsPerAngstrom=\"" + jvxlData.pointsPerAngstrom
          + "\"");
      info.append("\n  nSurfaceInts=\"" + nSurfaceInts + "\"");
      info
          .append("\n  nBytesData=\""
              + (jvxlData.jvxlSurfaceData.length() + bytesUncompressedEdgeData + jvxlData.jvxlColorData
                  .length()) + "\"");
    }
    if (jvxlData.jvxlPlane == null) {
      if (jvxlData.isContoured) {
        if (isInfo)
          info.append("\n  contoured=\"true\"");
        else
          definitionLine += (-1 - nSurfaceInts) + " "
              + bytesUncompressedEdgeData;
      } else if (jvxlData.isBicolorMap) {
        if (isInfo)
          info.append("\n  bicolorMap=\"true\"");
        else
          definitionLine += (nSurfaceInts) + " " + (-bytesUncompressedEdgeData);

      } else {
        if (!isInfo)
          definitionLine += nSurfaceInts + " " + bytesUncompressedEdgeData;
        else if (nColorData > 0)
          info.append("\n  colorMapped=\"true\"");
      }
      if (!isInfo)
        definitionLine += " "
            + (jvxlData.isJvxlPrecisionColor && nColorData != -1 ? -nColorData
                : nColorData);
    } else {

      String s = " " + jvxlData.jvxlPlane.x + " " + jvxlData.jvxlPlane.y + " "
          + jvxlData.jvxlPlane.z + " " + jvxlData.jvxlPlane.w;
      if (!isInfo)
        definitionLine += (jvxlData.isContoured ? "-1 -2 " + (-nColorData)
            : "-1 -1 " + nColorData)
            + s;
      else if (nColorData > 0)
        info.append("\n  colorMapped=\"true\"");
      if (isInfo)
        info.append("\n  plane=\"{ " + s + " }\"");
    }
    if (jvxlData.isContoured) {
      if (isInfo)
        info.append("\n  nContours=\"" + Math.abs(jvxlData.nContours) + "\"");
      else
        definitionLine += " " + jvxlData.nContours;
    }
    
    float min = (jvxlData.mappedDataMin == Float.MAX_VALUE ? 0f
        : jvxlData.mappedDataMin);
    if (!isInfo)
      definitionLine += " " + min + " " + jvxlData.mappedDataMax + " "
          + jvxlData.valueMappedToRed + " " + jvxlData.valueMappedToBlue;

    if (isInfo && jvxlData.jvxlColorData.length() > 0 && !jvxlData.isBicolorMap) {
      info.append("\n  dataMinimum=\"" + min + "\"");
      info.append("\n  dataMaximum=\"" + jvxlData.mappedDataMax + "\"");
      info.append("\n  valueMappedToRed=\"" + jvxlData.valueMappedToRed + "\"");
      info.append("\n  valueMappedToBlue=\"" + jvxlData.valueMappedToBlue
          + "\"");
    }
    if (isInfo && jvxlData.jvxlCompressionRatio > 0)
      info.append("\n  approximateCompressionRatio=\""
          + jvxlData.jvxlCompressionRatio + ":1\"");
    if (isInfo && jvxlData.isXLowToHigh)
      info
          .append("\n  note=\"progressive JVXL+ -- X values read from low(0) to high("
              + (jvxlData.nPointsX - 1) + ")\"");
    if (jvxlData.insideOut) {
      if (isInfo)
        info.append("\n  insideOut=\"true\"");
      else
        definitionLine += " insideOut";
    }
    if (!isInfo)
      return definitionLine;
    info.append("\n  precisionColor=\"" + jvxlData.isJvxlPrecisionColor + "\"");
    info.append("\n  nColorData=\"" + nColorData + "\"");
    info.append("\n  version=\"" + jvxlData.version + "\"");
    return "<jvxlSurfaceInfo>" + info.toString() + "\n</jvxlSurfaceInfo>";
  }

  protected static String jvxlExtraLine(JvxlData jvxlData, int n) {
    return (-n) + " " + jvxlData.edgeFractionBase + " "
        + jvxlData.edgeFractionRange + " " + jvxlData.colorFractionBase + " "
        + jvxlData.colorFractionRange + " Jmol voxel format version " +  JVXL_VERSION + "\n";
    
  }

  public static String jvxlGetFile(MeshDataServer meshDataServer,
                                   JvxlData jvxlData, MeshData meshData,
                                   String[] title, String msg,
                                   boolean includeHeader, int nSurfaces,
                                   String state, String comment) {
    StringBuffer data = new StringBuffer();
    if (includeHeader) {
      String s = jvxlData.jvxlFileHeader
          + (nSurfaces > 0 ? (-nSurfaces) + jvxlData.jvxlExtraLine.substring(2)
              : jvxlData.jvxlExtraLine);
      if (s.indexOf("#JVXL") != 0)
        data.append("#JVXL").append(jvxlData.isXLowToHigh ? "+" : "").append(
            " VERSION ").append(JVXL_VERSION).append("\n");
      data.append(s);
    }
    if ("HEADERONLY".equals(msg))
      return data.toString();
    data.append("# ").append(msg).append('\n');
    if (title != null)
      for (int i = 0; i < title.length; i++)
        data.append("# ").append(title[i]).append('\n');
    data.append(jvxlData.jvxlDefinitionLine + " rendering:" + state).append(
        '\n');

    StringBuffer sb = new StringBuffer();
    if (jvxlData.vertexDataOnly && meshData != null) {
      int[] vertexIdNew = new int[meshData.vertexCount];
      sb.append("<jvxlSurfaceData>\n");
      sb.append(jvxlEncodeTriangleData(meshData.polygonIndexes,
          meshData.polygonCount, vertexIdNew));
      sb.append(jvxlEncodeVertexData(meshDataServer, jvxlData, vertexIdNew,
          meshData.vertices, meshData.vertexValues, meshData.vertexCount,
          meshData.polygonColixes, meshData.polygonCount,
          jvxlData.jvxlColorData.length() > 0));
      sb.append("</jvxlSurfaceData>\n");
    } else if (jvxlData.jvxlPlane == null) {
      
      sb.append(jvxlData.jvxlSurfaceData);
      sb.append(jvxlCompressString(jvxlData.jvxlEdgeData)).append('\n').append(
          jvxlCompressString(jvxlData.jvxlColorData)).append('\n');
    } else {
      sb.append(jvxlCompressString(jvxlData.jvxlColorData)).append('\n');
    }
    int len = sb.length();
    if (len > 0) {
      if (jvxlData.wasCubic && jvxlData.nBytes > 0)
        jvxlData.jvxlCompressionRatio = (int) (((float) jvxlData.nBytes) / len);
      else
        jvxlData.jvxlCompressionRatio = (int) (((float) (jvxlData.nPointsX
            * jvxlData.nPointsY * jvxlData.nPointsZ * 13)) / len);
    }

    data.append(sb);
    if (includeHeader) {
      if (msg != null && !jvxlData.vertexDataOnly)
        data.append("#-------end of jvxl file data-------\n");
      data.append(jvxlData.jvxlInfoLine).append('\n');
      if (jvxlData.vContours != null) {
        data.append("<jvxlContourData>\n");
        jvxlEncodeContourData(jvxlData.vContours, data);
        data.append("</jvxlContourData>\n");
      }
      if (comment != null)
        data.append("<jvxlSurfaceCommand>\n  ").append(comment).append(
            "\n</jvxlSurfaceCommand>\n");
      if (state != null)
        data.append("<jvxlSurfaceState>\n  ").append(state).append(
            "\n</jvxlSurfaceState>\n");
      if (includeHeader)
        data.append("<jvxlFileTitle>\n").append(jvxlData.jvxlFileTitle).append(
            "</jvxlFileTitle>\n");
    }
    return data.toString();
  }

  private static void jvxlEncodeContourData(Vector[] contours, StringBuffer sb) {
    for (int i = 0; i < contours.length; i++) {
      if (contours[i].size() < IsosurfaceMesh.CONTOUR_POINTS)
        continue;
      int nPolygons = ((Integer) contours[i]
          .get(IsosurfaceMesh.CONTOUR_NPOLYGONS)).intValue();
      sb.append("<jvxlContour i=\"" + i + "\"");
      sb.append(" value=\"" + contours[i].get(IsosurfaceMesh.CONTOUR_VALUE)
          + "\"");
      sb.append(" color=\""
          + Escape.escapeColor(((int[]) contours[i]
              .get(IsosurfaceMesh.CONTOUR_COLOR))[0]) + "\"");
      sb.append(" npolygons=\"" + nPolygons + "\"");
      StringBuffer sb1 = new StringBuffer();
      jvxlEncodeBitSet((BitSet) contours[i].get(IsosurfaceMesh.CONTOUR_BITSET),
          nPolygons, sb1);
      sb.append(" data=\"" + contours[i].get(IsosurfaceMesh.CONTOUR_FDATA)
          + "\">\n");
      sb.append(sb1);
      sb.append("</jvxlContour>\n");
    }
  }

  

  protected static float jvxlFractionFromCharacter(int ich, int base, int range,
                                         float fracOffset) {
    if (ich == base + range)
      return Float.NaN;
    if (ich < base)
      ich = 92; 
    float fraction = (ich - base + fracOffset) / range;
    if (fraction < 0f)
      return 0f;
    if (fraction > 1f)
      return 0.999999f;
    
    return fraction;
  }

  

  protected static float jvxlValueFromCharacter2(int ich, int ich2, float min, float max,
                                       int base, int range) {
    float fraction = jvxlFractionFromCharacter2(ich, ich2, base, range);
    return (max == min ? fraction : min + fraction * (max - min));
  }

  protected static float jvxlFractionFromCharacter2(int ich1, int ich2, int base,
                                          int range) {
    float fraction = jvxlFractionFromCharacter(ich1, base, range, 0);
    float remains = jvxlFractionFromCharacter(ich2, base, range, 0.5f);
    return fraction + remains / range;
  }

  protected static char jvxlValueAsCharacter(float value, float min, float max, int base,
                                   int range) {
    float fraction = (min == max ? value : (value - min) / (max - min));
    return jvxlFractionAsCharacter(fraction, base, range);
  }

  public static char jvxlFractionAsCharacter(float fraction) {
    return jvxlFractionAsCharacter(fraction, defaultEdgeFractionBase, defaultEdgeFractionRange);  
  }
  
  protected static char jvxlFractionAsCharacter(float fraction, int base, int range) {
    if (fraction > 0.9999f)
      fraction = 0.9999f;
    else if (Float.isNaN(fraction))
      fraction = 1.0001f;
    int ich = (int) (fraction * range + base);
    if (ich < base)
      return (char) base;
    if (ich == 92)
      return 33; 
    
    
    return (char) ich;
  }

  private static void jvxlAppendCharacter2(float value, float min, float max,
                                           int base, int range,
                                           StringBuffer list1,
                                           StringBuffer list2) {
    float fraction = (min == max ? value : (value - min) / (max - min));
    char ch1 = jvxlFractionAsCharacter(fraction, base, range);
    list1.append(ch1);
    fraction -= jvxlFractionFromCharacter(ch1, base, range, 0);
    list2.append(jvxlFractionAsCharacter(fraction * range, base, range));
  }

  public static void jvxlUpdateSurfaceData(JvxlData jvxlData, float[] vertexValues, int vertexCount, int vertexIncrement, char isNaN) { 
    char[] chars = jvxlData.jvxlEdgeData.toCharArray();
    for (int i = 0, ipt = 0; i < vertexCount; i+= vertexIncrement, ipt++)
      if (Float.isNaN(vertexValues[i]))
          chars[ipt] = isNaN;
    jvxlData.jvxlEdgeData = String.copyValueOf(chars);
  }
  
  public static void jvxlCreateColorData(JvxlData jvxlData, float[] vertexValues) {
    if (vertexValues == null) {
      jvxlData.jvxlColorData = "";
      return;
    }
    boolean writePrecisionColor = jvxlData.isJvxlPrecisionColor;
    boolean doTruncate = jvxlData.isTruncated;
    int colorFractionBase = jvxlData.colorFractionBase;
    int colorFractionRange = jvxlData.colorFractionRange;
    float valueBlue = jvxlData.valueMappedToBlue;
    float valueRed = jvxlData.valueMappedToRed;
    int vertexCount = jvxlData.vertexCount;
    float min = jvxlData.mappedDataMin;
    float max = jvxlData.mappedDataMax;
    StringBuffer list1 = new StringBuffer();
    StringBuffer list2 = new StringBuffer();
    for (int i = 0; i < vertexCount; i++) {
      float value = vertexValues[i];
      if (doTruncate)
        value = (value > 0 ? 0.999f : -0.999f);
      if (writePrecisionColor)
        jvxlAppendCharacter2(value, min, max, colorFractionBase,
            colorFractionRange, list1, list2);
      else
        list1.append(jvxlValueAsCharacter(value, valueRed, valueBlue,
            colorFractionBase, colorFractionRange));
    }
    jvxlData.jvxlColorData = list1.append(list2).append('\n').toString();
    jvxlUpdateInfoLines(jvxlData);
  }

  

  
  public static String jvxlEncodeTriangleData(int[][] triangles, int nData,
                                              int[] vertexIdNew) {
    StringBuffer list = new StringBuffer();
    StringBuffer list1 = new StringBuffer();
    int ilast = 1;
    int p = 0;
    int inew = 0;
    boolean addPlus = false;
    for (int i = 0; i < nData;) {
      int idata = triangles[i][p];
      if (vertexIdNew[idata] > 0) {
        idata = vertexIdNew[idata];
      } else {
        idata = vertexIdNew[idata] = ++inew;
      }

      if (++p % 3 == 0) {
        i++;
        p = 0;
      }
      int diff = idata - ilast;
      ilast = idata;
      if (diff == 0) {
        list1.append('!');
        addPlus = false;
      } else if (diff > 32) {
        if (addPlus)
          list1.append('+');
        list1.append(diff);
        addPlus = true;
      } else if (diff < -32) {
        list1.append(diff);
        addPlus = true;
      } else {
        list1.append((char) ('\\' + diff));
        addPlus = false;
      }
    }
    return list.append(
        "  <jvxlTriangleData len=\"" + list1.length() + "\" count=\"" + nData
            + "\">\n    ").append(list1).append("\n  </jvxlTriangleData>\n")
        .toString();
  }

  
  public static String jvxlEncodeVertexData(MeshDataServer meshDataServer,
                                            JvxlData jvxlData,
                                            int[] vertexIdNew,
                                            Point3f[] vertices,
                                            float[] vertexValues,
                                            int vertexCount,
                                            short[] polygonColixes, 
                                            int polygonCount,
                                            boolean addColorData) {
    Point3f min = new Point3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    Point3f max = new Point3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
    int colorFractionBase = jvxlData.colorFractionBase;
    int colorFractionRange = jvxlData.colorFractionRange;
    Point3f p;
    for (int i = 0; i < vertexCount; i++) {
      p = vertices[i];
      if (p.x < min.x)
        min.x = p.x;
      if (p.y < min.y)
        min.y = p.y;
      if (p.z < min.z)
        min.z = p.z;
      if (p.x > max.x)
        max.x = p.x;
      if (p.y > max.y)
        max.y = p.y;
      if (p.z > max.z)
        max.z = p.z;
    }
    StringBuffer list = new StringBuffer();
    StringBuffer list1 = new StringBuffer();
    StringBuffer list2 = new StringBuffer();
    int[] vertexIdOld = new int[vertexCount];
    for (int i = 0; i < vertexCount; i++)
      if (vertexIdNew[i] > 0) 
        vertexIdOld[vertexIdNew[i] - 1] = i;
    for (int i = 0; i < vertexCount; i++) {
      p = vertices[vertexIdOld[i]];
      jvxlAppendCharacter2(p.x, min.x, max.x, colorFractionBase,
          colorFractionRange, list1, list2);
      jvxlAppendCharacter2(p.y, min.y, max.y, colorFractionBase,
          colorFractionRange, list1, list2);
      jvxlAppendCharacter2(p.z, min.z, max.z, colorFractionBase,
          colorFractionRange, list1, list2);
    }
    list1.append(list2);
    list.append("  <jvxlVertexData len=\"" + list1.length() + "\" count=\""
        + vertexCount + "\" min=\"" + min + "\" max=\"" + max + "\">\n    ");
    list.append(list1).append("\n  </jvxlVertexData>\n");
    if (polygonColixes != null) {
      list1 = new StringBuffer();
      int count = 0;
      short colix = 0;
      boolean done = false;
      for (int i = 0; i < polygonCount || (done = true) == true; i++) {
        if (done || polygonColixes[i] != colix) {
          if (count != 0)
            list1.append(" ").append(count).append(" ").append(
                (colix == 0 ? 0 : meshDataServer.getColixArgb(colix)));
          if (done)
            break;
          colix = polygonColixes[i];
          count = 1;
        } else {
          count++;
        }
      }
      list.append("  <jvxlPolygonColorData len=\"" + list1.length() + "\" count=\""
          + polygonCount+ "\">\n    ").append(list1).append("\n  </jvxlPolygonColorData>\n");
    }
    if (!addColorData)
      return list.toString();

    

    list1 = new StringBuffer();
    list2 = new StringBuffer();
    for (int i = 0; i < vertexCount; i++) {
      float value = vertexValues[vertexIdOld[i]];
      jvxlAppendCharacter2(value, jvxlData.mappedDataMin,
          jvxlData.mappedDataMax, colorFractionBase, colorFractionRange, list1,
          list2);
    }
    String s = jvxlCompressString(list1.append(list2).toString());
    return list.append(
        "  <jvxlColorData len=\"" + s.length() + "\" count=\"" + vertexCount
            + "\" compressed=\"1\" precision=\"true\">\n    ").append(s)
        .append("\n  </jvxlColorData>\n").toString();
  }

  
  private void getEncodedVertexData() throws Exception {
    String data = getXmlData("jvxlSurfaceData", null, true);
    jvxlDecodeVertexData(getXmlData("jvxlVertexData", data, true), false);
    String polygonColorData = getXmlData("jvxlPolygonColorData", data, false);
    jvxlDecodeTriangleData(getXmlData("jvxlTriangleData", data, true), polygonColorData, false);
    Logger.info("Checking for vertex values");
    jvxlColorDataRead = jvxlUncompressString(getXmlData("jvxlColorData", data, false));
    jvxlDataIsColorMapped = (jvxlColorDataRead.length() > 0);
    jvxlDataIsPrecisionColor = (data.indexOf("precision=\"true\"") >= 0);
    jvxlDecodeContourData(getXmlData("jvxlContourData", null, false));
    Logger.info("Done");
  }

  private void jvxlDecodeContourData(String data) throws Exception {
    Vector vs = new Vector();
    int pt = -1;
    vContours = null;
    if (data == null)
      return;
    while ((pt = data.indexOf("<jvxlContour", pt + 1)) >= 0) {
      Vector v = new Vector();
      String s = getXmlData("jvxlContour", data.substring(pt), true);
      int n = parseInt(getXmlAttrib(s, "npolygons"));
      float value = parseFloat(getXmlAttrib(s, "value"));
      int color = Escape.unescapeColor(getXmlAttrib(s, "color"));
      String fData = getXmlAttrib(s, "data");
      BitSet bs = jvxlDecodeBitSet(s.substring(s.lastIndexOf("\">") + 2));
      IsosurfaceMesh.setContourVector(v, n, bs, value, color, new StringBuffer(fData));
      
      vs.add(v);
    }

    vContours = new Vector[vs.size()];
    for (int i = 0; i < vs.size(); i++)
      vContours[i] = (Vector) vs.get(i);
  }

  public static void set3dContourVector(Vector v, int[][] polygonIndexes, Point3f[] vertices) {
    
    if (v.size() < IsosurfaceMesh.CONTOUR_POINTS)
      return;
    String fData = ((StringBuffer) v.get(IsosurfaceMesh.CONTOUR_FDATA)).toString();
    BitSet bs = (BitSet) v.get(IsosurfaceMesh.CONTOUR_BITSET);
    int nPolygons = ((Integer)v.get(IsosurfaceMesh.CONTOUR_NPOLYGONS)).intValue();
    int pt = 0;
    for (int i = 0; i < nPolygons; i++) {
      if (!bs.get(i))
        continue;
      int[] vertexIndexes = polygonIndexes[i];
      int type = ((int) fData.charAt(pt++)) - 48;
      char c1 = fData.charAt(pt++);
      char c2 = fData.charAt(pt++);
      float f1 = jvxlFractionFromCharacter(c1, defaultEdgeFractionBase, defaultEdgeFractionRange, 0);
      float f2 = jvxlFractionFromCharacter(c2, defaultEdgeFractionBase, defaultEdgeFractionRange, 0);
      int i1, i2, i3, i4;
      
      if ((type & 1) == 0) { 
        i1 = vertexIndexes[1];
        i2 = i3 = vertexIndexes[2];
        i4 = vertexIndexes[0];
      } else { 
        i1 = vertexIndexes[0];
        i2 = vertexIndexes[1];
        if ((type & 2) != 0) {
          i3 = i2;
          i4 = vertexIndexes[2];
        } else {
          i3 = vertexIndexes[2];
          i4 = i1;          
        }
      }
      Point3f pa = IsosurfaceMesh.getContourPoint(vertices, i1, i2, f1);
      Point3f pb = IsosurfaceMesh.getContourPoint(vertices, i3, i4, f2);
      v.add(pa);
      v.add(pb);
    }
    v.add(new Point3f(Float.NaN,Float.NaN,Float.NaN));
  }


  
  private String getXmlData(String name, String data, boolean withTag)
      throws Exception {
    
    String closer = "</" + name + ">";
    String tag = "<" + name;
    if (data == null) {
      StringBuffer sb = new StringBuffer();
      try {
        while (line.indexOf(tag) < 0) {
          line = br.readLine();
        }
      } catch (Exception e) {
        return null;
      }
      sb.append(line);
      while (line.indexOf(closer) < 0)
        sb.append(line = br.readLine());
      data = sb.toString();
    }
    int pt1 = data.indexOf(tag);
    int pt2 = data.indexOf(closer);
    if (pt1 >= 0 && !withTag) {
      pt1 = data.indexOf(">", pt1) + 1;
      while (Character.isWhitespace(data.charAt(pt1)))
        pt1++;
    }
    if (pt1 < 0 || pt1 > pt2)
      return "";
    return data.substring(pt1, pt2);
  }

  
  public Point3f[] jvxlDecodeVertexData(String data, boolean asArray) {
    int[] next = new int[1];
    setNext(data, "count", next, 2);
    int vertexCount = Parser.parseInt(data, next);
    if (!asArray)
      Logger.info("Reading " + vertexCount + " vertices");
    next[0]++;
    Point3f min = new Point3f();
    Point3f range = new Point3f();
    setNext(data, "min", next, 3);
    min.x = Parser.parseFloat(data, next);
    next[0]++;
    min.y = Parser.parseFloat(data, next);
    next[0]++;
    min.z = Parser.parseFloat(data, next);
    setNext(data, "max", next, 3);
    range.x = Parser.parseFloat(data, next) - min.x;
    next[0]++;
    range.y = Parser.parseFloat(data, next) - min.y;
    next[0]++;
    range.z = Parser.parseFloat(data, next) - min.z;
    int colorFractionBase = jvxlData.colorFractionBase;
    int colorFractionRange = jvxlData.colorFractionRange;
    int ptCount = vertexCount * 3;
    Point3f[] vertices = (asArray ? new Point3f[vertexCount] : null);
    Point3f p = (asArray ? null : new Point3f());
    float fraction;
    setNext(data, ">", next, 0);
    int pt = next[0];
    while (Character.isWhitespace(data.charAt(pt)))
      pt++;
    pt--;
    for (int i = 0; i < vertexCount; i++) {
      if (asArray)
        p = vertices[i] = new Point3f();
      fraction = jvxlFractionFromCharacter2(data.charAt(++pt), data.charAt(pt
          + ptCount), colorFractionBase, colorFractionRange);
      p.x = min.x + fraction * range.x;
      fraction = jvxlFractionFromCharacter2(data.charAt(++pt), data.charAt(pt
          + ptCount), colorFractionBase, colorFractionRange);
      p.y = min.y + fraction * range.y;
      fraction = jvxlFractionFromCharacter2(data.charAt(++pt), data.charAt(pt
          + ptCount), colorFractionBase, colorFractionRange);
      p.z = min.z + fraction * range.z;
      if (!asArray)
        addVertexCopy(p, 0, i);
    }
    return vertices;
  }
  
  int[][] jvxlDecodeTriangleData(String data, String colorData, boolean asArray) {
    int[] next = new int[1];
    int[] nextc = new int[1];
    int nColors = (colorData == null ? -1 : 0);
    int color = 0;
    setNext(data, "count", next, 2);
    int nData = Parser.parseInt(data, next);
    if (!asArray)
      Logger.info("Reading " + nData + " triangles");
    int[][] triangles = (asArray ? new int[nData][3] : null);
    int[] triangle = (asArray ? triangles[0] : new int[3]);
    int ilast = 0;
    int p = 0;
    int b0 = (int)'\\';
    setNext(data, ">", next, -1);
    int pt = next[0];
    for (int i = 0; i < nData;) {
      char ch = data.charAt(++pt);
      int idiff;
      switch(ch) {
      case '!':
        idiff = 0;
        break;
      case '+':
      case '.':
      case ' ':
      case '\n':
      case '\r':
      case '\t':
      case ',':
        continue;
      case '-':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
        next[0] = pt;
        idiff = Parser.parseInt(data, next);
        pt = next[0] - 1;
        break;
      default:
        idiff = (int)ch - b0; 
      }
      ilast += idiff;
      if (asArray)
        triangles[i][p] = ilast;
      else
        triangle[p] = ilast;
      if (++p % 3 == 0) {
        i++;
        p = 0;
        if (!asArray) {
          if (nColors >= 0) {
            if (nColors == 0) {
              nColors = Parser.parseInt(colorData, nextc);
              color = Parser.parseInt(colorData, nextc);
              if (color == Integer.MIN_VALUE)
                color = nColors = 0;
            } 
            nColors--;
          }
          addTriangleCheck(triangle[0], triangle[1], triangle[2], 7, false, color);
        }
      }
    }
    return triangles;
  }

  private static String getXmlAttrib(String data, String what) {
    
    int[] next = new int[1];
    int pt = setNext(data, what, next, 2);
    if (pt < 2)
      return "";
    int pt1 = setNext(data, "\"", next, -1);
    return (pt1 <= 0 ? "" : data.substring(pt, pt1));
  }
  
  private static int setNext(String data, String what, int[] next, int offset) {
    return next[0] = data.indexOf(what, next[0]) + what.length() + offset;
  }
}
