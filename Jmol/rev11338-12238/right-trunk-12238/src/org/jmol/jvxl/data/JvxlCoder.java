
package org.jmol.jvxl.data;

import javax.vecmath.Point3f;
import java.util.BitSet;
import java.util.Vector;

import org.jmol.util.BitSetUtil;
import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.util.Escape;
import org.jmol.util.TextFormat;
import org.jmol.util.XmlUtil;

public class JvxlCoder {

  
  
  final public static String JVXL_VERSION1 = "2.0";
  final public static String JVXL_VERSION_XML = "2.1";
  
  
  
  
  
  
  
  public static String jvxlGetFile(VolumeData volumeData, JvxlData jvxlData,
                                   String[] title) {
    
    int[] counts = volumeData.getVoxelCounts();
    jvxlData.nPointsX = counts[0];
    jvxlData.nPointsY = counts[1];
    jvxlData.nPointsZ = counts[2];
    jvxlData.jvxlVolumeDataXml = volumeData.setVolumetricXml();
    return jvxlGetFile(jvxlData, null, title, null, true, 1, null, null);
  }

  public static String jvxlGetFile(JvxlData jvxlData, MeshData meshData,
                                   String[] title, String msg,
                                   boolean includeHeader, int nSurfaces,
                                   String state, String comment) {
    return jvxlGetFileXml(jvxlData, meshData, title, msg, includeHeader, nSurfaces, state, comment);
    
    
    
    
    
    
    
    
  }

  private static String jvxlGetFileXml(JvxlData jvxlData, MeshData meshData,
                                       String[] title, String msg,
                                       boolean includeHeader, int nSurfaces,
                                       String state, String comment) {
    StringBuffer data = new StringBuffer();
    if ("TRAILERONLY".equals(msg)) {
      XmlUtil.closeTag(data, "jvxlSurfaceSet");
      XmlUtil.closeTag(data, "jvxl");
      return data.toString();
    }
    boolean isHeaderOnly = ("HEADERONLY".equals(msg));
    if (includeHeader) {
      XmlUtil.openDocument(data);
      XmlUtil.openTag(data, "jvxl", new String[] {
          "version", JVXL_VERSION_XML,
          "jmolVersion", jvxlData.version });
      if (jvxlData.jvxlFileTitle != null)
        XmlUtil.appendCdata(data, "jvxlFileTitle", null, "\n" + jvxlData.jvxlFileTitle);
      if (jvxlData.moleculeXml != null)
        data.append(jvxlData.moleculeXml);
      if (jvxlData.jvxlVolumeDataXml == null)
        jvxlData.jvxlVolumeDataXml = (new VolumeData()).setVolumetricXml();
      data.append(jvxlData.jvxlVolumeDataXml);
      XmlUtil.openTag(data,"jvxlSurfaceSet", 
          new String[] { "count", "" + (nSurfaces > 0 ? nSurfaces : 1) });
      if (isHeaderOnly)
        return data.toString();
    }
    StringBuffer sb;
    String type = (jvxlData.vertexDataOnly ? "pmesh"
        : jvxlData.jvxlPlane == null ? "isosurface" : "plane");
    
    if (jvxlData.jvxlColorData != null && jvxlData.jvxlColorData.length() > 0)
      type = "mapped " + type;
    XmlUtil.openTag(data, "jvxlSurface", new String[] { "type", type });
    data.append(jvxlGetInfo(jvxlData, true));
    jvxlAppendCommandState(data, comment, state);
    if (title != null || msg != null && msg.length() > 0) {
      sb = new StringBuffer();
      if (msg != null && msg.length() > 0)
        sb.append(msg).append("\n");
      if (title != null)
        for (int i = 0; i < title.length; i++)
          sb.append(title[i]).append('\n');
      XmlUtil.appendCdata(data, "jvxlSurfaceTitle", null, sb.toString());
    }
    sb = new StringBuffer();
    XmlUtil.openTag(sb, "jvxlSurfaceData", (jvxlData.jvxlPlane == null ? null :
      new String[] { "plane", Escape.escape(jvxlData.jvxlPlane) }));
    if (jvxlData.vertexDataOnly) {
      jvxlAppendMeshXml(sb, jvxlData, meshData, true);
    } else if (jvxlData.jvxlPlane == null) {
      appendXmlEdgeData(sb, jvxlData);
      appendXmlColorData(sb, "jvxlColorData", jvxlData.jvxlColorData,
          jvxlData.isJvxlPrecisionColor, jvxlData.valueMappedToRed,
          jvxlData.valueMappedToBlue);
    } else {
      appendXmlColorData(sb, "jvxlColorData", jvxlData.jvxlColorData,
          jvxlData.isJvxlPrecisionColor, jvxlData.valueMappedToRed,
          jvxlData.valueMappedToBlue);
    }
    if (jvxlData.excludedVertexCount > 0) {
      appendEncodedBitSetTag(sb, "jvxlExcludedVertexData", jvxlData.jvxlExcluded[0], jvxlData.excludedVertexCount);
      appendEncodedBitSetTag(sb, "jvxlExcludedPlaneData", jvxlData.jvxlExcluded[2], -1);
    }
    appendEncodedBitSetTag(sb, "jvxlExcludedTriangleData", jvxlData.jvxlExcluded[3], jvxlData.excludedTriangleCount);
    XmlUtil.closeTag(sb, "jvxlSurfaceData");
    int len = sb.length();
    data.append(sb);
    if (jvxlData.vContours != null && jvxlData.vContours.length > 0) {
      jvxlEncodeContourData(jvxlData.vContours, data);
    }
    XmlUtil.closeTag(data, "jvxlSurface");
    if (includeHeader) {
      XmlUtil.closeTag(data, "jvxlSurfaceSet");
      XmlUtil.closeTag(data, "jvxl");
    }
    return jvxlSetCompressionRatio(data, jvxlData, len);
  }

  private static void appendEncodedBitSetTag(StringBuffer sb, String name, BitSet bs, int count) {
    if (count < 0)
      count = BitSetUtil.cardinalityOf(bs);
    if (count == 0)
      return;
    StringBuffer sb1 = new StringBuffer("\n ");
    jvxlEncodeBitSet(bs, -1, sb1);
    XmlUtil.appendTag(sb, name, new String[] {
        "bsEncoding", "base90+35",
        "count", "" + count,
        "len", "" + bs.length() }, 
        jvxlCompressString(sb1.toString(), true));
  }

  private static String jvxlSetCompressionRatio(StringBuffer data,
                                                JvxlData jvxlData, int len) {
    String s = data.toString();
    int r = (int) (jvxlData.nBytes > 0 ? ((float) jvxlData.nBytes) / len
        : ((float) (jvxlData.nPointsX
          * jvxlData.nPointsY * jvxlData.nPointsZ * 13)) / len);
    return TextFormat.simpleReplace(s, "#RATIO#", (r > 0 ? "" + r : "?"));
  }

  private static void appendXmlEdgeData(StringBuffer sb, JvxlData jvxlData) {
    XmlUtil.appendTag(sb, "jvxlEdgeData", new String[] {
        "count", "" + (jvxlData.jvxlEdgeData.length() - 1),
        "encoding", "base90f1",
        "bsEncoding", "base90+35c",
        "isXLowToHigh", "" + jvxlData.isXLowToHigh,
        "data", jvxlCompressString(jvxlData.jvxlEdgeData, true) }, "\n" 
        + jvxlCompressString(jvxlData.jvxlSurfaceData, true));
  }

  private static void jvxlAppendCommandState(StringBuffer data, String cmd,
                                             String state) {
    if (cmd != null)
      XmlUtil.appendCdata(data, "jvxlIsosurfaceCommand", null,
          "\n" + (cmd.indexOf("#") < 0 ? cmd : cmd.substring(0, cmd.indexOf("#"))) + "\n");
    if (state != null) {
      if (state.indexOf("** XML ** ") >=0) {
        state = TextFormat.split(state, "** XML **")[1].trim(); 
        XmlUtil.appendTag(data, "jvxlIsosurfaceState",  "\n" + state + "\n");
      } else {
        XmlUtil.appendCdata(data, "jvxlIsosurfaceState", null, "\n" + state + "\n");
      }
    }
  }

  private static void appendXmlColorData(StringBuffer sb, String key, 
                                         String data,
                                         boolean isPrecisionColor,
                                         float value1,
                                         float value2) {
    int n;
    if (data == null || (n = data.length() - 1) < 0)
      return;
    if (isPrecisionColor)
      n /= 2;
    XmlUtil.appendTag(sb, key, new String[] {
        "count", "" + n, 
        "encoding", "base90f" + (isPrecisionColor ? "2" : "1"),
        "min", "" + value1,
        "max", "" + value2,
        "data", jvxlCompressString(data, true) }, null);
  }

  
  public static String jvxlGetInfo(JvxlData jvxlData, boolean notVersion1) {
    if (jvxlData.jvxlSurfaceData == null)
      return "";
    Vector attribs = new Vector();
     
    int nSurfaceInts = jvxlData.nSurfaceInts;
    int bytesUncompressedEdgeData = (jvxlData.vertexDataOnly ? 0
        : jvxlData.jvxlEdgeData.length() - 1);
    int nColorData = (jvxlData.jvxlColorData == null ? -1 : (jvxlData.jvxlColorData.length() - 1));
    if (!jvxlData.vertexDataOnly) {
      
      addAttrib(attribs, "\n  cutoff", "" + jvxlData.cutoff);
      addAttrib(attribs, "\n  isCutoffAbsolute", "" + jvxlData.isCutoffAbsolute);
      addAttrib(attribs, "\n  pointsPerAngstrom", "" + jvxlData.pointsPerAngstrom);
      int n = jvxlData.jvxlSurfaceData.length() 
          + bytesUncompressedEdgeData + nColorData + 1;
      if (n > 0)
        addAttrib(attribs, "\n  nBytesData", "" + n);

      
      addAttrib(attribs, "\n  isXLowToHigh", "" + jvxlData.isXLowToHigh);
      if (jvxlData.jvxlPlane == null) {
        addAttrib(attribs, "\n  nSurfaceInts", "" + nSurfaceInts);
        addAttrib(attribs, "\n  nBytesUncompressedEdgeData", "" + bytesUncompressedEdgeData);
      }
      if (nColorData > 0)
        addAttrib(attribs, "\n  nBytesUncompressedColorData", "" + nColorData); 
    }
    
    if (jvxlData.isJvxlPrecisionColor)
      addAttrib(attribs, "\n  precisionColor", "true");
    if (jvxlData.colorDensity)
      addAttrib(attribs, "\n  colorDensity", "true");
    if (jvxlData.jvxlPlane == null) {
      if (jvxlData.isContoured) {
        addAttrib(attribs, "\n  contoured", "true"); 
        addAttrib(attribs, "\n  colorMapped", "true");
      } else if (jvxlData.isBicolorMap) {
        addAttrib(attribs, "\n  bicolorMap", "true");
      } else if (nColorData > 0) {
        addAttrib(attribs, "\n  colorMapped", "true");
      }
      if (jvxlData.vContours != null && jvxlData.vContours.length > 0)
        addAttrib(attribs, "\n  nContourData", "" + jvxlData.vContours.length);
    } else {
      if (jvxlData.scale3d != 0)
        addAttrib(attribs, "\n  scale3d", "" + jvxlData.scale3d);
      if (nColorData > 0)
        addAttrib(attribs, "\n  colorMapped", "true");
      addAttrib(attribs, "\n  plane", Escape.escape(jvxlData.jvxlPlane));
    }
    jvxlData.excludedVertexCount = BitSetUtil.cardinalityOf(jvxlData.jvxlExcluded[0]);
    jvxlData.excludedTriangleCount = BitSetUtil .cardinalityOf(jvxlData.jvxlExcluded[3]);
    if (jvxlData.excludedVertexCount > 0)
      addAttrib(attribs, "\n  nExcludedVertexes", "" + jvxlData.excludedVertexCount);
    if (jvxlData.excludedTriangleCount > 0)
      addAttrib(attribs, "\n  nExcludedTriangles", "" + jvxlData.excludedTriangleCount);
    if (jvxlData.isContoured) {
      if (jvxlData.contourValues == null || jvxlData.contourColixes == null) {
        if (jvxlData.vContours == null)
          addAttrib(attribs, "\n  nContours", "" + Math.abs(jvxlData.nContours));
      } else {
        if (jvxlData.jvxlPlane != null)
          addAttrib(attribs, "\n  contoured", "true");
        addAttrib(attribs, "\n  nContours", "" + jvxlData.contourValues.length);
        addAttrib(attribs, "\n  contourValues", Escape.escapeArray(jvxlData.contourValues));
        addAttrib(attribs, "\n  contourColors", jvxlData.contourColors);
      }
    }
    
    
    float min = (jvxlData.mappedDataMin == Float.MAX_VALUE ? 0f
        : jvxlData.mappedDataMin);
    if (jvxlData.jvxlColorData != null && jvxlData.jvxlColorData.length() > 0 && !jvxlData.isBicolorMap) {
      addAttrib(attribs, "\n  dataMinimum", "" + min);
      addAttrib(attribs, "\n  dataMaximum", "" + jvxlData.mappedDataMax);
      addAttrib(attribs, "\n  valueMappedToRed", "" + jvxlData.valueMappedToRed);
      addAttrib(attribs, "\n  valueMappedToBlue", "" + jvxlData.valueMappedToBlue);
    }
    
    if (jvxlData.insideOut)
      addAttrib(attribs, "\n  insideOut", "true");
    
    
    if (jvxlData.isXLowToHigh)
      addAttrib(attribs, "\n  note", "progressive JVXL+ -- X values read from low(0) to high("
              + (jvxlData.nPointsX - 1) + ")");
    addAttrib(attribs, "\n  xyzMin", Escape.escape(jvxlData.boundingBox[0]));
    addAttrib(attribs, "\n  xyzMax", Escape.escape(jvxlData.boundingBox[1]));
    addAttrib(attribs, "\n  approximateCompressionRatio", "#RATIO#:1");
    addAttrib(attribs, "\n  jmolVersion", jvxlData.version);
    
    StringBuffer info = new StringBuffer();
    XmlUtil.openTag(info, "jvxlSurfaceInfo", attribs.toArray());
    XmlUtil.closeTag(info, "jvxlSurfaceInfo");
    return info.toString();
  }
  
  private static void addAttrib(Vector attribs, String name, String value) {
    attribs.add(new String[] { name, value });
  }

  public static final int CONTOUR_NPOLYGONS = 0;
  public static final int CONTOUR_BITSET = 1;
  public static final int CONTOUR_VALUE = 2;
  public static final int CONTOUR_COLIX = 3;
  public static final int CONTOUR_COLOR = 4;
  public static final int CONTOUR_FDATA = 5;
  public static final int CONTOUR_POINTS = 6; 

  
  private static void jvxlEncodeContourData(Vector[] contours, StringBuffer sb) {
    XmlUtil.openTag(sb, "jvxlContourData", new String[] { "count", "" + contours.length });
    for (int i = 0; i < contours.length; i++) {
      if (contours[i].size() < CONTOUR_POINTS)
        continue;
      int nPolygons = ((Integer) contours[i]
          .get(CONTOUR_NPOLYGONS)).intValue();
      StringBuffer sb1 = new StringBuffer("\n");
      BitSet bs = (BitSet) contours[i].get(CONTOUR_BITSET);
      jvxlEncodeBitSet(bs, nPolygons, sb1);
      XmlUtil.appendTag(sb, "jvxlContour", new String[] {
          "index", "" + i,
          "value", "" + contours[i].get(CONTOUR_VALUE),
          "color", Escape.escapeColor(((int[]) contours[i]
              .get(CONTOUR_COLOR))[0]),
          "count", "" + bs.length(),
          "encoding", "base90iff1",
          "bsEncoding", "base90+35c",
          "data", jvxlCompressString(contours[i].get(CONTOUR_FDATA).toString(), true) }, 
          jvxlCompressString(sb1.toString(), true));
    }
    XmlUtil.closeTag(sb, "jvxlContourData");
  }

  
  public static void set3dContourVector(Vector v, int[][] polygonIndexes, Point3f[] vertices) {
    
    if (v.size() < CONTOUR_POINTS)
      return;
    StringBuffer fData = (StringBuffer) v.get(CONTOUR_FDATA);
    BitSet bs = (BitSet) v.get(CONTOUR_BITSET);
    int nPolygons = ((Integer)v.get(CONTOUR_NPOLYGONS)).intValue();
    int pt = 0;
    int nBuf = fData.length();
    int type = 0;
    char c1 = ' ';
    char c2 = ' ';
    for (int i = 0; i < nPolygons; i++) {
      if (!bs.get(i))
        continue;
      int[] vertexIndexes = polygonIndexes[i];
      while (pt < nBuf && !Character.isDigit(c1 = fData.charAt(pt++))) {
        
      }
      type = ((int) c1) - 48;
      while (pt < nBuf && Character.isWhitespace(c1 = fData.charAt(pt++))) {
        
      }
      while (pt < nBuf && Character.isWhitespace(c2 = fData.charAt(pt++))) {
        
      }
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
      v.add(getContourPoint(vertices, i1, i2, f1));
      v.add(getContourPoint(vertices, i3, i4, f2));
    }
  }

  private static Point3f getContourPoint(Point3f[] vertices, int i, int j, float f) {
    Point3f pt = new Point3f();
    pt.set(vertices[j]);
    pt.sub(vertices[i]);
    pt.scale(f);
    pt.add(vertices[i]);
    return pt;
  }

  
  public static void appendContourTriangleIntersection(int type, float f1, float f2, StringBuffer fData) {
    fData.append(type);
    fData.append(jvxlFractionAsCharacter(f1));
    fData.append(jvxlFractionAsCharacter(f2));    
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
  }

  

  private static void jvxlAppendMeshXml(StringBuffer sb, 
                                        JvxlData jvxlData, MeshData meshData, boolean escapeXml) {
    int[] vertexIdNew = new int[meshData.vertexCount];
    if (appendXmlTriangleData(sb, meshData.polygonIndexes,
        meshData.polygonCount, vertexIdNew, escapeXml))
      appendXmlVertexData(sb, jvxlData, vertexIdNew,
          meshData.vertices, meshData.vertexValues, meshData.vertexCount,
          meshData.polygonColorData, meshData.polygonCount,
          jvxlData.jvxlColorData.length() > 0, escapeXml);
  }

  
  private static boolean appendXmlTriangleData(StringBuffer sb, int[][] triangles, int nData,
                                              int[] vertexIdNew, boolean escapeXml) {
    StringBuffer list1 = new StringBuffer();
    int ilast = 1;
    int p = 0;
    int inew = 0;
    boolean addPlus = false;
    int nTri = 0;
    for (int i = 0; i < nData;) {
      if (triangles[i] == null) {
        i++;
        continue;
      }
      int idata = triangles[i][p];
      if (vertexIdNew[idata] > 0) {
        idata = vertexIdNew[idata];
      } else {
        idata = vertexIdNew[idata] = ++inew;
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
      if (++p % 3 == 0) {
        p = 0;
        i++;
        nTri++;
      }
    }
    if (list1.length() == 0)
      return false;
    XmlUtil.appendTag(sb, "jvxlTriangleData", new String[] {
        "count", "" + nTri,
        "encoding", "jvxltdiff",
        "data" , jvxlCompressString(list1.toString(), escapeXml) }, null);
    return true;
  }

  
  private static void appendXmlVertexData(StringBuffer sb,
                                            JvxlData jvxlData,
                                            int[] vertexIdNew,
                                            Point3f[] vertices,
                                            float[] vertexValues,
                                            int vertexCount,
                                            String polygonColorData, 
                                            int polygonCount,
                                            boolean addColorData, boolean escapeXml) {
    int colorFractionBase = jvxlData.colorFractionBase;
    int colorFractionRange = jvxlData.colorFractionRange;
    Point3f p;
    Point3f min = jvxlData.boundingBox[0];
    Point3f max = jvxlData.boundingBox[1];
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
    XmlUtil.appendTag(sb, "jvxlVertexData", new String[] {
        "count", "" + vertexCount,
        "min", Escape.escape(min),
        "max", Escape.escape(max),
        "encoding", "base90xyz2",
        "data", jvxlCompressString(list1.toString(), escapeXml) }, null);
    if (polygonColorData != null)
      XmlUtil.appendTag(sb, "jvxlPolygonColorData", new String[] {
          "encoding", "jvxlnc" }, "\n" + polygonColorData);
    if (!addColorData)
      return;

    

    list1 = new StringBuffer();
    list2 = new StringBuffer();
    for (int i = 0; i < vertexCount; i++) {
      float value = vertexValues[vertexIdOld[i]];
      jvxlAppendCharacter2(value, jvxlData.mappedDataMin,
          jvxlData.mappedDataMax, colorFractionBase, colorFractionRange, list1,
          list2);
    }
    appendXmlColorData(sb, "jvxlColorData", list1.append(list2).append("\n").toString(), true, 
        jvxlData.valueMappedToRed, jvxlData.valueMappedToBlue);
  }

  
  
  
  
  final public static int defaultEdgeFractionBase = 35; 
  final public static int defaultEdgeFractionRange = 90;
  final public static int defaultColorFractionBase = 35;
  final public static int defaultColorFractionRange = 90;

  
  public static char jvxlFractionAsCharacter(float fraction) {
    return jvxlFractionAsCharacter(fraction, defaultEdgeFractionBase, defaultEdgeFractionRange);  
  }
  
  public static char jvxlFractionAsCharacter(float fraction, int base, int range) {
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

  public static float jvxlFractionFromCharacter(int ich, int base, int range,
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

  public static float jvxlFractionFromCharacter2(int ich1, int ich2, int base,
                                          int range) {
    float fraction = jvxlFractionFromCharacter(ich1, base, range, 0);
    float remains = jvxlFractionFromCharacter(ich2, base, range, 0.5f);
    return fraction + remains / range;
  }

  public static char jvxlValueAsCharacter(float value, float min, float max, int base,
                                   int range) {
    float fraction = (min == max ? value : (value - min) / (max - min));
    return jvxlFractionAsCharacter(fraction, base, range);
  }

  protected static float jvxlValueFromCharacter2(int ich, int ich2, float min,
                                                 float max, int base, int range) {
    float fraction = jvxlFractionFromCharacter2(ich, ich2, base, range);
    return (max == min ? fraction : min + fraction * (max - min));
  }

  

  public static int jvxlEncodeBitSet0(BitSet bs, int nPoints, StringBuffer sb) {
    
    
    
    int dataCount = 0;
    int prevCount = -1;
    int nPrev = 0;
    if (nPoints < 0)
      nPoints = bs.length();
    int n = 0;
    boolean isset = false;
    int lastPoint = nPoints - 1;
    
    for (int i = 0; i < nPoints; ++i) {
      if (isset == bs.get(i)) {
        dataCount++;
      } else {
        if (dataCount == prevCount && i != lastPoint) {
          nPrev++;
        } else {
          if (nPrev > 0) {
            sb.append(' ').append(-nPrev);
            nPrev = 0;
            n++;
          }
          sb.append(' ').append(dataCount);
          n++;
          prevCount = dataCount;
        }
        dataCount = 1;
        isset = !isset;
      }
    }
    sb.append(' ').append(dataCount).append('\n');
    return n;
  }
  
    
  public static int jvxlEncodeBitSet(BitSet bs, int nPoints, StringBuffer sb) {
    if (false)
      return jvxlEncodeBitSet0(bs, nPoints, sb);
    int dataCount = 0;
    int n = 0;
    boolean isset = false;
    if (nPoints < 0)
      nPoints = bs.size();
    if (nPoints == 0)
      return 0;
    sb.append("-");
    for (int i = 0; i < nPoints; ++i) {
      if (isset == bs.get(i)) {
        dataCount++;
      } else {
         jvxlAppendEncodedNumber(sb, dataCount, defaultEdgeFractionBase, defaultEdgeFractionRange);
        n++;
        dataCount = 1;
        isset = !isset;
      }
    }
    jvxlAppendEncodedNumber(sb, dataCount, defaultEdgeFractionBase, defaultEdgeFractionRange);
    sb.append('\n');
    return n;
  }

  public static void jvxlAppendEncodedNumber(StringBuffer sb, int n, int base, int range) {
    boolean isInRange = (n < range);
    if (!isInRange)
      sb.append((char)(base + range));
    while (n > 0) {
      int n1 = n / range;
      int x = base + n - n1 * range;
      if (x == 92)
        x = 33;  
      sb.append((char) x);
      n = n1;
    }
    if (!isInRange)
      sb.append(" ");
  }

  public static BitSet jvxlDecodeBitSet(String data, int base, int range) {
    BitSet bs = new BitSet();
    int dataCount = 0;
    int ptr = 0;
    boolean isset = false;
    int[] next = new int[1];
    while ((dataCount = jvxlParseEncodedInt(data, base, range, next)) != Integer.MIN_VALUE) {
      if (isset)
        bs.set(ptr, ptr + dataCount);
      ptr += dataCount;
      isset = !isset;
    }
    return bs;
  }

  public static int jvxlParseEncodedInt(String str, int offset, int base, int[] next) {
    boolean digitSeen = false;
    int value = 0;
    int ich = next[0];
    int ichMax = str.length();
    if (ich < 0)
      return Integer.MIN_VALUE;
    while (ich < ichMax && Character.isWhitespace(str.charAt(ich)))
      ++ich;
    if (ich >= ichMax)
      return Integer.MIN_VALUE;
    int factor = 1;
    boolean isLong = (str.charAt(ich) == (offset + base));
    if (isLong)
      ich++;
    while (ich < ichMax && !Character.isWhitespace(str.charAt(ich))) {
      int i = str.charAt(ich);
      if (i < offset)
        i = 92;   
      value += (i - offset) * factor;
      digitSeen = true;
      ++ich;
      if (!isLong)
        break;
      factor *= base;
    }
    if (!digitSeen)
      value = Integer.MIN_VALUE;
    next[0] = ich;
    return value;
  }

  public static BitSet jvxlDecodeBitSet(String data) {
    if (data.startsWith("-"))
      return jvxlDecodeBitSet(jvxlUncompressString(data.substring(1)), defaultEdgeFractionBase, defaultEdgeFractionRange);
    
    BitSet bs = new BitSet();
    int dataCount = 0;
    int lastCount = 0;
    int nPrev = 0;
    int ptr = 0;
    boolean isset = false;
    int[] next = new int[1];
    while (true) {
      dataCount = (nPrev++ < 0 ? dataCount : Parser.parseInt(data, next));
      if (dataCount == Integer.MIN_VALUE) 
        break;
      if (dataCount < 0) {
        nPrev = dataCount;
        dataCount = lastCount;
        continue;
      }
      if (isset)
        bs.set(ptr, ptr + dataCount);
      ptr += dataCount;
      lastCount = dataCount;
      isset = !isset;
    }
    return bs;
  }
  
  
  
  public static String jvxlCompressString(String data, boolean escapeXml) {
    
    
    
    if (data.indexOf("~") >= 0)
      return data;
    StringBuffer dataOut = new StringBuffer();
    char chLast = '\0';
    boolean escaped = false;
    boolean lastEscaped = false;
    int nLast = 0;
    int n = data.length();
    for (int i = 0; i <= n; i++) {
      char ch = (i == n ? '\0' : data.charAt(i));
      switch (ch) {
      case '\n':
      case '\r':
        continue;
      case '&':
      case '<':
        escaped = escapeXml;
        break;
      default:
        escaped = false;
      }
      if (ch == chLast) {
        ++nLast;
        ch = '\0';
      } else if (nLast > 0 || lastEscaped) {
        if (nLast < 4 && !lastEscaped || chLast == ' '
            || chLast == '\t') {
          while (--nLast >= 0)
            dataOut.append(chLast);
        } else {
          if (lastEscaped)
            lastEscaped = false;
          else
            dataOut.append('~');
          dataOut.append(nLast);
          dataOut.append(' ');
        }
        nLast = 0;
      }
      if (ch != '\0') {
        if (escaped) {
          lastEscaped = true;
          escaped = false;
          dataOut.append('~');
          chLast = ch;
          --ch;
        } else {
          chLast = ch;          
        }
        dataOut.append(ch);
      }
    }
    
    return dataOut.toString();
  }

  public static String jvxlUncompressString(String data) {
    if (data.indexOf("~") < 0)
      return data;
    StringBuffer dataOut = new StringBuffer();
    char chLast = '\0';
    int[] next = new int[1];
    for (int i = 0; i < data.length(); i++) {
      char ch = data.charAt(i);
      if (ch == '~') {
        next[0] = ++i;
        switch (ch = data.charAt(i)) {
        case ';':
        case '%':
          next[0]++;
          dataOut.append(chLast = ++ch);
          
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
          int nChar = Parser.parseInt(data, next);
          for (int c = 0; c < nChar; c++)
            dataOut.append(chLast);
          i = next[0];
          continue;
        case '~':
          --i;
          break;
        default:
          Logger.error("Error uncompressing string " + data.substring(0, i) + "?");
        }
      }
      dataOut.append(ch);
      chLast = ch;
    }
    return dataOut.toString();
  }

  
  
  public static void jvxlCreateHeaderWithoutTitleOrAtoms(VolumeData v, StringBuffer bs) {
    jvxlCreateHeader(v, Integer.MAX_VALUE, null, null,  bs);
  }

  public static void jvxlCreateHeader(VolumeData v, int nAtoms, 
                                         Point3f[] atomXyz, int[] atomNo,
                                         StringBuffer sb) {
    
    
    v.setVolumetricXml();
    if (sb.length() == 0)
      sb.append("Line 1\nLine 2\n");
    sb.append(nAtoms == Integer.MIN_VALUE ? "+2" 
        : nAtoms == Integer.MAX_VALUE ? "-2" : "" + (-nAtoms))
      .append(' ')
      .append(v.volumetricOrigin.x).append(' ')
      .append(v.volumetricOrigin.y).append(' ')
      .append(v.volumetricOrigin.z).append(" ANGSTROMS\n");
    for (int i = 0; i < 3; i++)
      sb.append(v.voxelCounts[i]).append(' ')
        .append(v.volumetricVectors[i].x).append(' ')
        .append(v.volumetricVectors[i].y).append(' ')
        .append(v.volumetricVectors[i].z).append('\n');
    if (nAtoms != Integer.MAX_VALUE && nAtoms != Integer.MIN_VALUE) {
      nAtoms = Math.abs(nAtoms);
      for (int i = 0, n = 0; i < nAtoms; i++)
        sb.append((n = Math.abs(atomNo[i])) + " " + n + ".0 "
            + atomXyz[i].x + " " + atomXyz[i].y + " " + atomXyz[i].z + "\n");
      return;
    }
    Point3f pt = new Point3f(v.volumetricOrigin);
    sb.append("1 1.0 ").append(pt.x).append(' ').append(pt.y).append(' ')
        .append(pt.z).append(" //BOGUS H ATOM ADDED FOR JVXL FORMAT\n");
    for (int i = 0; i < 3; i++)
      pt.scaleAdd(v.voxelCounts[i] - 1, v.volumetricVectors[i], pt);
    sb.append("2 2.0 ").append(pt.x).append(' ').append(pt.y).append(' ')
        .append(pt.z).append(" //BOGUS He ATOM ADDED FOR JVXL FORMAT\n");
  }

  

}
