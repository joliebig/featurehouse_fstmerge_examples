




package org.openscience.jvxl.simplewriter;

import javax.vecmath.Point4f;

import org.jmol.jvxl.readers.JvxlReader;

public class JvxlData {
  public JvxlData() {    
  }
 
  

  public boolean wasJvxl;
  public boolean wasCubic;
  
  public String jvxlFileTitle;
  public String jvxlFileMessage;
  public String jvxlFileHeader;
  public String jvxlExtraLine;
  public String jvxlDefinitionLine;
  public String jvxlSurfaceData;
  public String jvxlEdgeData;
  public String jvxlColorData;
  public String jvxlInfoLine;
  
  public Point4f jvxlPlane;

  public int jvxlCompressionRatio;
  public boolean isJvxlPrecisionColor;
  public boolean jvxlDataIsColorMapped;
  public boolean jvxlDataIs2dContour;
  public boolean isColorReversed;
  
  public int edgeFractionBase = JvxlReader.defaultEdgeFractionBase;
  public int edgeFractionRange = JvxlReader.defaultEdgeFractionRange;
  public int colorFractionBase = JvxlReader.defaultColorFractionBase;
  public int colorFractionRange = JvxlReader.defaultColorFractionRange;

  public boolean isCutoffAbsolute;
  public boolean insideOut;
  public boolean isXLowToHigh;
  public boolean isContoured;
  public boolean isBicolorMap;
  public boolean isTruncated;
  public boolean vertexDataOnly;
  public float mappedDataMin;
  public float mappedDataMax;
  public float valueMappedToRed;
  public float valueMappedToBlue;
  public float cutoff;
  public float pointsPerAngstrom; 
  public int nPointsX, nPointsY, nPointsZ;
  public int nBytes;
  public int nContours;
  public int nEdges;
  public int nSurfaceInts;
  public int vertexCount;

  public short minColorIndex = -1;
  public short maxColorIndex = 0;

  public String[] title;
  public String version;
  
}

