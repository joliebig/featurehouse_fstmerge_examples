




package org.jmol.jvxl.data;

import java.util.BitSet;
import java.util.Vector;

import javax.vecmath.Point4f;



public class JvxlData {
  public JvxlData() {    
  }
 
  public boolean asXml = true;
  public boolean wasJvxl;
  public boolean wasCubic;
  
  public String jvxlFileTitle;
  public String jvxlFileMessage;
  public String jvxlFileHeader;
  public String jvxlSurfaceData;
  public String jvxlEdgeData;
  public String jvxlColorData;
  public String jvxlVolumeDataXml;
  
  public Point4f jvxlPlane;

  public boolean isJvxlPrecisionColor;
  public boolean jvxlDataIsColorMapped;
  public boolean jvxlDataIs2dContour;
  public boolean isColorReversed;
  
  public int edgeFractionBase = JvxlCoder.defaultEdgeFractionBase;
  public int edgeFractionRange = JvxlCoder.defaultEdgeFractionRange;
  public int colorFractionBase = JvxlCoder.defaultColorFractionBase;
  public int colorFractionRange = JvxlCoder.defaultColorFractionRange;

  public boolean insideOut;
  public boolean isXLowToHigh;
  public boolean isContoured;
  public boolean isBicolorMap;
  public boolean isTruncated;
  public boolean isCutoffAbsolute;
  public boolean vertexDataOnly;
  public float mappedDataMin;
  public float mappedDataMax;
  public float valueMappedToRed;
  public float valueMappedToBlue;
  public float cutoff;
  public float pointsPerAngstrom; 
  public int nPointsX, nPointsY, nPointsZ;
  public long nBytes;
  public int nContours;
  public int nEdges;
  public int nSurfaceInts;
  public int vertexCount;

  
  
  
  
  public Vector[] vContours;
  public short[] contourColixes;
  public String contourColors;
  public float[] contourValues;
  public float[] contourValuesUsed;

  public short minColorIndex = -1;
  public short maxColorIndex = 0;

  public String[] title;
  public String version;
  
  public void setSurfaceInfo(Point4f thePlane, int nSurfaceInts, String surfaceData) {
    jvxlSurfaceData = surfaceData;
    if (jvxlSurfaceData.indexOf("--") == 0)
      jvxlSurfaceData = jvxlSurfaceData.substring(2);
    jvxlPlane = thePlane;
    this.nSurfaceInts = nSurfaceInts;
  }

  public void setSurfaceInfoFromBitSet(BitSet bs, Point4f thePlane) {
    StringBuffer sb = new StringBuffer();
    int nPoints = nPointsX * nPointsY * nPointsZ;
    int nSurfaceInts = (thePlane != null ? 0 : JvxlCoder.jvxlEncodeBitSet(bs, nPoints, sb));
    setSurfaceInfo(thePlane, nSurfaceInts, sb.toString());
  }
    
  public void jvxlUpdateInfo(String[] title, long nBytes) {
    this.title = title;
    this.nBytes = nBytes;
  }

  public void updateSurfaceData(float[] vertexValues, int vertexCount, int vertexIncrement, char isNaN) { 
    if (jvxlEdgeData.length() == 0)
      return;
    char[] chars = jvxlEdgeData.toCharArray();
    for (int i = 0, ipt = 0; i < vertexCount; i+= vertexIncrement, ipt++)
      if (Float.isNaN(vertexValues[i]))
          chars[ipt] = isNaN;
    jvxlEdgeData = String.copyValueOf(chars);
  }
  
}

