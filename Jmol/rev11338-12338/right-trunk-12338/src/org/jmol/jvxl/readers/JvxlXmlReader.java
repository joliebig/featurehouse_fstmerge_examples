
package org.jmol.jvxl.readers;

import java.io.BufferedReader;
import java.util.BitSet;
import java.util.Vector;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.jvxl.data.JvxlCoder;
import org.jmol.jvxl.data.JvxlData;
import org.jmol.shapesurface.IsosurfaceMesh;
import org.jmol.util.ColorEncoder;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.util.XmlReader;

public class JvxlXmlReader extends VolumeFileReader {

  protected String JVXL_VERSION = "2.1";
  
  protected int surfaceDataCount;
  protected int edgeDataCount;
  protected int colorDataCount;
  private int excludedTriangleCount;
  private int excludedVertexCount;
  protected boolean haveContourData;

  private XmlReader xr;
  
  protected boolean isXmlFile= true;
  JvxlXmlReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg, br);
    jvxlData.wasJvxl = isJvxl = true;
    isXLowToHigh = false;
    xr = new XmlReader(br);
  }

  protected boolean thisInside;
  
  

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
      params.bsExcluded = jvxlData.jvxlExcluded = new BitSet[4];
      if (colorDataCount > 0)
        jvxlColorDataRead = jvxlReadData("color", colorDataCount);
      if (excludedVertexCount > 0) {
        jvxlData.jvxlExcluded[0]  
            = JvxlCoder.jvxlDecodeBitSet(
                xr.getXmlData("jvxlExcludedVertexData", null, false));
        if (xr.isNext("jvxlExcludedPlaneData"))
          jvxlData.jvxlExcluded[2]  
                                = JvxlCoder.jvxlDecodeBitSet(
                                    xr.getXmlData("jvxlExcludedPlaneData", null, false));          
      }
      if (excludedTriangleCount > 0)
        jvxlData.jvxlExcluded[3]  
            = JvxlCoder.jvxlDecodeBitSet(
                xr.getXmlData("jvxlExcludedTriangleData", null, false));
      if (haveContourData)
        jvxlDecodeContourData(jvxlData, xr.getXmlData("jvxlContourData", null, false));
    } catch (Exception e) {
      Logger.error(e.toString());
      return false;
    }
    return true;
  }
  
  String tempDataXml; 
  
  protected void readParameters() throws Exception {
    String s = xr.getXmlData("jvxlFileTitle", null, false);
    jvxlFileHeaderBuffer = new StringBuffer(s);
    xr.toTag("jvxlVolumeData");
    String data = tempDataXml = xr.getXmlData("jvxlVolumeData", null, true);
    volumetricOrigin.set(xr.getXmlPoint(data, "origin"));
   isAngstroms = true;
   readVector(0);
   readVector(1);
   readVector(2);
   line = xr.toTag("jvxlSurfaceSet");
   nSurfaces = parseInt(XmlReader.getXmlAttrib(line, "count"));
   Logger.info("jvxl file surfaces: " + nSurfaces);
   Logger.info("using default edge fraction base and range");
   Logger.info("using default color fraction base and range");
   cJvxlEdgeNaN = (char) (edgeFractionBase + edgeFractionRange);
  }

  protected void readVector(int voxelVectorIndex) throws Exception {
    String data = xr.getXmlData("jvxlVolumeVector", tempDataXml, true);
    tempDataXml = tempDataXml.substring(tempDataXml.indexOf(data) + data.length());
    int n = parseInt(XmlReader.getXmlAttrib(data, "count"));
    if (n == Integer.MIN_VALUE)
      vertexDataOnly = true;
    voxelCounts[voxelVectorIndex] = (n < 0 ? 0 : n);
    volumetricVectors[voxelVectorIndex].set(xr.getXmlPoint(data, "vector"));
    if (isAnisotropic)
      setVectorAnisotropy(volumetricVectors[voxelVectorIndex]);
  }

  protected void gotoData(int n, int nPoints) throws Exception {
    if (n > 0)
      Logger.info("skipping " + n + " data sets, " + nPoints + " points each");
    vertexDataOnly = jvxlData.vertexDataOnly = (nPoints == 0);
    for (int i = 0; i < n; i++) {
      jvxlSkipData(nPoints, true);
    }
    xr.toTag("jvxlSurface");
    jvxlReadSurfaceInfo();
  }

  protected void jvxlSkipData(int nPoints, boolean doSkipColorData)
      throws Exception {
    readLine();
    xr.skipTag("jvxlSurface");
  }

  protected void jvxlReadSurfaceInfo() throws Exception {
    String s;
    String data = xr.getXmlData("jvxlSurfaceInfo", null, true);
    isXLowToHigh = XmlReader.getXmlAttrib(data, "isXLowToHigh").equals("true");
    jvxlCutoff = parseFloat(XmlReader.getXmlAttrib(data, "cutoff"));
    if (!Float.isNaN(jvxlCutoff))
      Logger.info("JVXL read: cutoff " + jvxlCutoff);
    int nContourData = parseInt(XmlReader.getXmlAttrib(data, "nContourData"));
    haveContourData = (nContourData > 0);
    params.isContoured = XmlReader.getXmlAttrib(data, "contoured").equals("true");
    if (params.isContoured) {
      int nContoursRead = parseInt(XmlReader.getXmlAttrib(data, "nContours"));
      if (nContoursRead <= 0) {
        nContoursRead = 0;
      } else {
        s = XmlReader.getXmlAttrib(data, "contourValues");
        if (s.length() > 0) {
          jvxlData.contourValues = params.contoursDiscrete = parseFloatArray(s);
          Logger.info("JVXL read: contourValues " + Escape.escapeArray(jvxlData.contourValues));            
        }
        s = XmlReader.getXmlAttrib(data, "contourColors");
        if (s.length() > 0) {
          jvxlData.contourColixes = params.contourColixes = Graphics3D.getColixArray(s);
          jvxlData.contourColors = Graphics3D.getHexCodes(jvxlData.contourColixes);
          Logger.info("JVXL read: contourColixes " +
              Graphics3D.getHexCodes(jvxlData.contourColixes));        }
        params.contourFromZero = XmlReader.getXmlAttrib(data, "contourFromZero").equals("true");
      }
      params.nContours = (haveContourData ? nContourData : nContoursRead);
      
    }
    params.isBicolorMap = XmlReader.getXmlAttrib(data, "bicolorMap").equals("true");
    if (params.isBicolorMap || params.colorBySign)
      jvxlCutoff = 0;
    jvxlDataIsColorMapped = params.isBicolorMap || XmlReader.getXmlAttrib(data, "colorMapped").equals("true");
    
    jvxlData.isJvxlPrecisionColor = XmlReader.getXmlAttrib(data, "precisionColor").equals("true");
    jvxlData.colorDensity = params.colorDensity = XmlReader.getXmlAttrib(data, "colorDensity").equals("true");
    s = XmlReader.getXmlAttrib(data, "plane");
    if (s.indexOf("{") >= 0) {
      try {
        params.thePlane = (Point4f) Escape.unescapePoint(s);
        Logger.info("JVXL read: plane " + params.thePlane);
        if (params.scale3d == 0)
          params.scale3d = parseFloat(XmlReader.getXmlAttrib(data, "scale3d"));
        if (Float.isNaN(params.scale3d))
          params.scale3d = 0;
      } catch (Exception e) {
        Logger
            .error("Error reading 4 floats for PLANE definition -- setting to 0 0 1 0  (z=0)");
        params.thePlane = new Point4f(0, 0, 1, 0);
      }
      surfaceDataCount = 0;
      edgeDataCount = 0;
    } else {
      params.thePlane = null;
      surfaceDataCount = parseInt(XmlReader.getXmlAttrib(data, "nSurfaceInts"));
      edgeDataCount = parseInt(XmlReader.getXmlAttrib(data, "nBytesUncompressedEdgeData"));
    }
    excludedVertexCount = parseInt(XmlReader.getXmlAttrib(data, "nExcludedVertexes"));
    excludedTriangleCount = parseInt(XmlReader.getXmlAttrib(data, "nExcludedTriangles"));
    colorDataCount = Math.max(0, parseInt(XmlReader.getXmlAttrib(data, "nBytesUncompressedColorData")));
    jvxlDataIs2dContour = (params.thePlane != null && jvxlDataIsColorMapped);
    if (jvxlDataIs2dContour)
      params.isContoured = true;
    
    if (params.colorBySign)
      params.isBicolorMap = true;
    boolean insideOut = XmlReader.getXmlAttrib(data, "insideOut").equals("true");
    float dataMin = Float.NaN;
    float dataMax = Float.NaN;
    float red = Float.NaN;
    float blue = Float.NaN;
    if (jvxlDataIsColorMapped) {
      dataMin = parseFloat(XmlReader.getXmlAttrib(data, "dataMinimum"));
      dataMax = parseFloat(XmlReader.getXmlAttrib(data, "dataMaximum"));
      red = parseFloat(XmlReader.getXmlAttrib(data, "valueMappedToRed"));
      blue = parseFloat(XmlReader.getXmlAttrib(data, "valueMappedToBlue"));
      if (Float.isNaN(dataMin)) {
        dataMin = red = -1f;
        dataMax = blue = 1f;
      }
    }
    jvxlSetColorRanges(dataMin, dataMax, red, blue, insideOut);
  }

  protected void jvxlSetColorRanges(float dataMin, float dataMax, float red,
                                  float blue, boolean insideOut) {
    if (jvxlDataIsColorMapped) {
    if (!Float.isNaN(dataMin) && !Float.isNaN(dataMax)) {
      if (dataMax == 0 && dataMin == 0) {
        
        dataMin = -1;
        dataMax = 1;
      }
      params.mappedDataMin = dataMin;
      params.mappedDataMax = dataMax;
      Logger.info("JVXL read: data_min/max " + params.mappedDataMin + "/"
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
    jvxlData.valueMappedToRed = params.valueMappedToRed;
    jvxlData.valueMappedToBlue = params.valueMappedToBlue;
    jvxlData.mappedDataMin = params.mappedDataMin;
    jvxlData.mappedDataMax = params.mappedDataMax;
    jvxlData.insideOut = insideOut;
    if (params.insideOut)
      jvxlData.insideOut = !jvxlData.insideOut;
    params.insideOut = jvxlData.insideOut;
  }

  protected void readSurfaceData(boolean isMapDataIgnored) throws Exception {
    thisInside = !params.isContoured;
    if (readSurfaceData())
      return;
    tempDataXml = xr.getXmlData("jvxlEdgeData", null, true);
    bsVoxelBitSet = JvxlCoder.jvxlDecodeBitSet(xr.getXmlData("jvxlEdgeData",
        tempDataXml, false));
    
    
    
    readVolumeFileSurfaceData();
  }

  protected boolean readSurfaceData() throws Exception {
    if (vertexDataOnly) {
      getEncodedVertexData();
      return true;
    } 
    if (params.thePlane != null) {
      volumeData.setDataDistanceToPlane(params.thePlane);
      setVolumeData(volumeData);
      params.cutoff = 0f;
      jvxlData.setSurfaceInfo(params.thePlane, 0, "");
      jvxlData.scale3d = params.scale3d;
      return true;
    }
    return false;
  }
  
  protected void readVolumeFileSurfaceData() throws Exception {
    super.readSurfaceData(false);
  }

  protected String jvxlReadData(String type, int nPoints) {
    String str;
    try {
      if (type.equals("edge")) {
        str = JvxlCoder.jvxlUncompressString(XmlReader.getXmlAttrib(tempDataXml, "data"));
      } else {
        String data = xr.getXmlData("jvxlColorData", null, true);
        jvxlData.isJvxlPrecisionColor = XmlReader.getXmlAttrib(data, "encoding").endsWith("2");
        str = JvxlCoder.jvxlUncompressString(XmlReader.getXmlAttrib(data, "data"));
      }
    } catch (Exception e) {
      Logger.error("Error reading " + type + " data " + e);
      throw new NullPointerException();
    }
    return str;
  }
  
  protected BitSet bsVoxelBitSet;

  protected BitSet getVoxelBitSet(int nPoints) throws Exception {
    if (bsVoxelBitSet != null)
      return bsVoxelBitSet;
    BitSet bs = new BitSet();
    int bsVoxelPtr = 0;
    if (surfaceDataCount <= 0)
      return bs; 
    int nThisValue = 0;
    while (bsVoxelPtr < nPoints) {
      nThisValue = parseInt();
      if (nThisValue == Integer.MIN_VALUE) {
        readLine();
        
        
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
  
  protected float getSurfacePointAndFraction(float cutoff,
                                             boolean isCutoffAbsolute,
                                             float valueA, float valueB,
                                             Point3f pointA,
                                             Vector3f edgeVector,
                                             float[] fReturn, Point3f ptReturn) {
    if (edgeDataCount <= 0)
      return super.getSurfacePointAndFraction(cutoff, isCutoffAbsolute, valueA,
          valueB, pointA, edgeVector, fReturn, ptReturn);
    ptReturn.scaleAdd(fReturn[0] = jvxlGetNextFraction(edgeFractionBase,
        edgeFractionRange, 0.5f), edgeVector, pointA);
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
    return JvxlCoder.jvxlFractionFromCharacter(strFractionTemp.charAt(fractionPtr++),
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
        + jvxlData.isJvxlPrecisionColor);

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
      if (jvxlData.isJvxlPrecisionColor) {
        
        
        
        
        
        
        fraction = JvxlCoder.jvxlFractionFromCharacter2(data.charAt(cpt), data.charAt(cpt
            + vertexCount), colorFractionBase, colorFractionRange);
        value = min + fraction * range;
      } else {
        
        
        fraction = JvxlCoder.jvxlFractionFromCharacter(data.charAt(cpt),
            colorFractionBase, colorFractionRange, 0.5f);
        value = params.valueMappedToRed + fraction * colorRange;
      }
      vertexValues[i] = value;
      ++cpt;
      if (value < contourPlaneMinimumValue)
        contourPlaneMinimumValue = value;
      if (value > contourPlaneMaximumValue)
        contourPlaneMaximumValue = value;
      
      
      
      if (marchingSquares != null && params.isContoured) {
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

  
  protected void getEncodedVertexData() throws Exception {
    String data = xr.getXmlData("jvxlSurfaceData", null, true);
    String tData = xr.getXmlData("jvxlTriangleData", data, true);
    jvxlDecodeVertexData(xr.getXmlData("jvxlVertexData", data, true), false);
    String polygonColorData = xr.getXmlData("jvxlPolygonColorData", data, false);
    jvxlDecodeTriangleData(tData, polygonColorData);
    Logger.info("Checking for vertex values");
    data = xr.getXmlData("jvxlColorData", data, true);
    jvxlData.isJvxlPrecisionColor = XmlReader.getXmlAttrib(data, "encoding").endsWith("2");
    jvxlColorDataRead = JvxlCoder.jvxlUncompressString(XmlReader.getXmlAttrib(data, "data"));
    if (jvxlColorDataRead.length() == 0)
      jvxlColorDataRead = xr.getXmlData("jvxlColorData", data, false);
    jvxlDataIsColorMapped = (jvxlColorDataRead.length() > 0);
    if (haveContourData)
      jvxlDecodeContourData(jvxlData, xr.getXmlData("jvxlContourData", null, false));
  }

  
  public Point3f[] jvxlDecodeVertexData(String data, boolean asArray) throws Exception {
    int vertexCount = parseInt(XmlReader.getXmlAttrib(data, "count"));
    if (!asArray)
      Logger.info("Reading " + vertexCount + " vertices");
    Point3f min = xr.getXmlPoint(data, "min");
    Point3f range = xr.getXmlPoint(data, "max");
    range.sub(min);
    int colorFractionBase = jvxlData.colorFractionBase;
    int colorFractionRange = jvxlData.colorFractionRange;
    int ptCount = vertexCount * 3;
    Point3f[] vertices = (asArray ? new Point3f[vertexCount] : null);
    Point3f p = (asArray ? null : new Point3f());
    float fraction;
    String s = JvxlCoder.jvxlUncompressString(XmlReader.getXmlAttrib(data, "data"));
    if (s.length() == 0)
      s = xr.getXmlData("jvxlVertexData", data, false); 
    for (int i = 0, pt = -1; i < vertexCount; i++) {
      if (asArray)
        p = vertices[i] = new Point3f();
      fraction = JvxlCoder.jvxlFractionFromCharacter2(s.charAt(++pt), s.charAt(pt
          + ptCount), colorFractionBase, colorFractionRange);
      p.x = min.x + fraction * range.x;
      fraction = JvxlCoder.jvxlFractionFromCharacter2(s.charAt(++pt), s.charAt(pt
          + ptCount), colorFractionBase, colorFractionRange);
      p.y = min.y + fraction * range.y;
      fraction = JvxlCoder.jvxlFractionFromCharacter2(s.charAt(++pt), s.charAt(pt
          + ptCount), colorFractionBase, colorFractionRange);
      p.z = min.z + fraction * range.z;
      if (!asArray)
        addVertexCopy(p, 0, i);
    }
    return vertices;
  }

  
  int[][] jvxlDecodeTriangleData(String data, String colorData)
      throws Exception {
    int nColors = (colorData == null ? -1 : 0);
    int color = 0;
    int nData = parseInt(XmlReader.getXmlAttrib(data, "count"));
    Logger.info("Reading " + nData + " triangles");
    int[][] triangles = null;
    int[] triangle = new int[3];
    String s = JvxlCoder.jvxlUncompressString(XmlReader.getXmlAttrib(data, "data"));
    if (s.length() == 0)
      s = xr.getXmlData("jvxlTriangleData", data, false);
    int[] nextp = new int[1];
    int[] nextc = new int[1];
    int ilast = 0;
    int p = 0;
    int b0 = (int) '\\';
    for (int i = 0, pt = -1; i < nData;) {
      char ch = s.charAt(++pt);
      int idiff;
      switch (ch) {
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
        nextp[0] = pt;
        idiff = Parser.parseInt(s, nextp);
        pt = nextp[0] - 1;
        break;
      default:
        idiff = (int) ch - b0;
      }
      ilast += idiff;
      triangle[p] = ilast;
      if (++p % 3 == 0) {
        i++;
        p = 0;
        if (nColors >= 0) {
          if (nColors == 0) {
            nColors = Parser.parseInt(colorData, nextc);
            color = Parser.parseInt(colorData, nextc);
            if (color == Integer.MIN_VALUE)
              color = nColors = 0;
          }
          nColors--;
        }
        addTriangleCheck(triangle[0], triangle[1], triangle[2], 7, 0, false,
            color);
      }
    }
    return triangles;
  }

  protected void jvxlDecodeContourData(JvxlData jvxlData, String data) throws Exception {
    Vector vs = new Vector();
    StringBuffer values = new StringBuffer();
    StringBuffer colors = new StringBuffer();
    int pt = -1;
    jvxlData.vContours = null;
    if (data == null)
      return;
    while ((pt = data.indexOf("<jvxlContour", pt + 1)) >= 0) {
      Vector v = new Vector();
      String s = xr.getXmlData("jvxlContour", data.substring(pt), true);
      float value = parseFloat(XmlReader.getXmlAttrib(s, "value"));
      values.append(" ").append(value);
      short colix = Graphics3D.getColix(Graphics3D.getArgbFromString(XmlReader.getXmlAttrib(s,
          "color")));
      int color = Graphics3D.getArgb(colix);
      colors.append(" ").append(Escape.escapeColor(color));
      String fData = JvxlCoder.jvxlUncompressString(XmlReader.getXmlAttrib(s, "data"));
      BitSet bs = JvxlCoder.jvxlDecodeBitSet(xr.getXmlData("jvxlContour", s, false));
      int n = bs.length();
      IsosurfaceMesh.setContourVector(v, n, bs, value, colix, color, new StringBuffer(
          fData));
      vs.add(v);
    }
    int n = vs.size();
    if (n > 0)
      jvxlData.vContours = new Vector[n];
    
    jvxlData.contourColixes = params.contourColixes = new short[n];
    jvxlData.contourValues = params.contoursDiscrete = new float[n];
    for (int i = 0; i < n; i++) {
      jvxlData.vContours[i] = (Vector) vs.get(i);
      jvxlData.contourValues[i] = ((Float) jvxlData.vContours[i].get(2)).floatValue();
      jvxlData.contourColixes[i] = ((short[]) jvxlData.vContours[i].get(3))[0];
    }
    Logger.info("JVXL read: " + n + " discrete contours");
    Logger.info("JVXL read: contour values: " + values);
    Logger.info("JVXL read: contour colors: " + colors);
    jvxlData.contourColors = Graphics3D.getHexCodes(jvxlData.contourColixes);
  }


}
