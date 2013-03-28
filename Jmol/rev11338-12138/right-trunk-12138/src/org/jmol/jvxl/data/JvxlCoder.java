
package org.jmol.jvxl.data;

import javax.vecmath.Point3f;
import java.util.BitSet;
import java.util.Vector;

import org.jmol.util.Logger;
import org.jmol.util.Parser;
import org.jmol.util.Escape;
import org.jmol.util.TextFormat;

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

    String msg = null;
    if (!jvxlData.asXml) {
      StringBuffer bs = new StringBuffer();
      if (title != null)
        for (int i = 0; i < title.length; i++ ) {
          String line = title[i].replace('\n',' ');
          if (i < title.length - 2)
            bs.append("#");
          bs.append(line).append('\n');
        }
      jvxlCreateHeader(volumeData, (jvxlData.isXLowToHigh ? Integer.MIN_VALUE 
          : Integer.MAX_VALUE), null, null, bs);
      jvxlData.jvxlFileHeader = bs.toString();
      if (jvxlData.isXLowToHigh)
        msg = "note: X data read from low to high";
    }
    return jvxlGetFile(jvxlData, null, title, msg, true, 1, null, null);
  }

  public static String jvxlGetFile(JvxlData jvxlData, MeshData meshData,
                                   String[] title, String msg,
                                   boolean includeHeader, int nSurfaces,
                                   String state, String comment) {
    
    if (meshData != null || jvxlData == null || jvxlData.asXml || jvxlData.vContours != null || jvxlData.contourValues != null)
      return jvxlGetFileXml(jvxlData, meshData, title, msg, includeHeader, nSurfaces, state, comment);
    return jvxlGetFileVersion1(jvxlData, meshData, title, msg, includeHeader, nSurfaces, state, comment);
  }

  private static String jvxlGetFileXml(JvxlData jvxlData, MeshData meshData,
                                       String[] title, String msg,
                                       boolean includeHeader, int nSurfaces,
                                       String state, String comment) {
    StringBuffer data = new StringBuffer();
    if ("TRAILERONLY".equals(msg)) {
      data.append("</jvxlSurfaceSet>\n");
      data.append("</jvxl>");
      return data.toString();
    }
    boolean isHeaderOnly = ("HEADERONLY".equals(msg));
    if (includeHeader) {
      data.append("<?xml version=\"1.0\"?>\n").append("<jvxl version=\"")
          .append(JVXL_VERSION_XML).append("\" jmolVersion=\"").append(
              jvxlData.version).append("\">\n");
      if (jvxlData.jvxlFileTitle != null)
        appendTag(data, "jvxlFileTitle", null, null, jvxlData.jvxlFileTitle,
            "<![CDATA[\n");
      if (jvxlData.jvxlVolumeDataXml == null)
        jvxlData.jvxlVolumeDataXml = (new VolumeData()).setVolumetricXml();
      data.append(jvxlData.jvxlVolumeDataXml);
      data.append("<jvxlSurfaceSet count=\"" + (nSurfaces > 0 ? nSurfaces : 1)
          + "\">\n");
      if (isHeaderOnly)
        return data.toString();
    }
    StringBuffer sb;
    String type = (jvxlData.vertexDataOnly ? "pmesh"
        : jvxlData.jvxlPlane == null ? "isosurface" : "plane");
    
    if (jvxlData.jvxlColorData != null && jvxlData.jvxlColorData.length() > 0)
      type = "mapped " + type;
    data.append("<jvxlSurface type=\"").append(type).append("\">\n");
    data.append(jvxlGetInfo(jvxlData, true));
    data.append("\n");
    jvxlAppendCommandState(data, comment, state, true);
    if (title != null || msg != null && msg.length() > 0) {
      sb = new StringBuffer();
      if (msg != null && msg.length() > 0)
        sb.append(msg).append("\n");
      if (title != null)
        for (int i = 0; i < title.length; i++)
          sb.append(title[i]).append('\n');
      appendTag(data, "jvxlSurfaceTitle", null, null, sb.toString(),
          "<![CDATA[\n");
    }
    String attr = "";
    if (jvxlData.jvxlPlane != null)
      attr += " plane=\"" + Escape.escape(jvxlData.jvxlPlane) + "\"";
    sb = new StringBuffer();
    sb.append("<jvxlSurfaceData").append(attr).append(">\n");
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
    sb.append("</jvxlSurfaceData>\n");
    int len = sb.length();
    data.append(sb);
    if (jvxlData.vContours != null && jvxlData.vContours.length > 0) {
      jvxlEncodeContourData(jvxlData.vContours, data);
    }
    data.append("</jvxlSurface>\n");
    if (includeHeader) {
      data.append("</jvxlSurfaceSet>\n");
      data.append("</jvxl>");
    }
    return jvxlSetCompressionRatio(data, jvxlData, len);
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
    sb.append("  ");
    appendTag(sb, "jvxlEdgeData", " ", new String[] {
        "count", "" + (jvxlData.jvxlEdgeData.length() - 1),
        "encoding", "base90f1",
        "isXLowToHigh", "" + jvxlData.isXLowToHigh,
        "data", jvxlCompressString(jvxlData.jvxlEdgeData, true) }, jvxlData.jvxlSurfaceData, "\n");
  }

  private static void appendTag(StringBuffer sb, String key, String sep,
                                String[] attributes, String data, String term) {
    sb.append("<").append(key);
    if (attributes != null)
      for (int i = 0; i < attributes.length; i += 2)
        appendAttrib(sb, sep, attributes[i], attributes[i + 1]);
    sb.append(">");
    if (data != null) {
      sb.append(term).append(data);
      if (term.startsWith("<![CDATA["))
        sb.append("]]>");
    }
    sb.append("</").append(key).append(">\n");
  }

  
  private static void appendAttrib(StringBuffer sb, String sep, String name,
                                   String value) {
    if (value != null)
      sb.append(sep).append(name).append("=\"").append(value).append("\"");
  }

  private static void jvxlAppendCommandState(StringBuffer data, String cmd,
                                             String state, boolean asXml) {
    if (cmd != null)
      appendTag(data, "jvxlIsosurfaceCommand", null, null,
          (cmd.indexOf("#") < 0 ? cmd : cmd.substring(0, cmd.indexOf("#"))) + "\n", (asXml ? "<![CDATA[\n" : ""));
    if (state != null)
      appendTag(data, "jvxlIsosurfaceState", null, null, state + "\n",  (asXml ? "<![CDATA[\n" : ""));
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
    appendTag(sb, key, " ", new String[] {
        "count", "" + n, 
        "encoding", "base90f" + (isPrecisionColor ? "2" : "1"),
        "min", "" + value1,
        "max", "" + value2,
        "data", jvxlCompressString(data, true) }, null, null);
  }

  public static String jvxlGetInfo(JvxlData jvxlData, boolean notVersion1) {
    if (jvxlData.jvxlSurfaceData == null)
      return "";
    StringBuffer info = new StringBuffer();
    int nSurfaceInts = jvxlData.nSurfaceInts;
    int bytesUncompressedEdgeData = (jvxlData.vertexDataOnly ? 0
        : jvxlData.jvxlEdgeData.length() - 1);
    int nColorData = (jvxlData.jvxlColorData == null ? -1 : (jvxlData.jvxlColorData.length() - 1));
    
    appendAttrib(info, "\n  ", "axXML", "" + (jvxlData.asXml && notVersion1));
    if (!jvxlData.vertexDataOnly) {
      
      appendAttrib(info, "\n  ", "cutoff", "" + jvxlData.cutoff);
      appendAttrib(info, "\n  ", "isCutoffAbsolute", "" + jvxlData.isCutoffAbsolute);
      appendAttrib(info, "\n  ", "pointsPerAngstrom", "" + jvxlData.pointsPerAngstrom);
      int n = jvxlData.jvxlSurfaceData.length() 
          + bytesUncompressedEdgeData + nColorData + 1;
      if (n > 0)
        appendAttrib(info, "\n  ", "nBytesData", "" + n);

      
      appendAttrib(info, "\n  ", "isXLowToHigh", "" + jvxlData.isXLowToHigh);
      if (jvxlData.jvxlPlane == null) {
        appendAttrib(info, "\n  ", "nSurfaceInts", "" + nSurfaceInts);
        appendAttrib(info, "\n  ", "nBytesUncompressedEdgeData", "" + bytesUncompressedEdgeData);
      }
      if (nColorData > 0)
        appendAttrib(info, "\n  ", "nBytesUncompressedColorData", "" + nColorData); 
    }
    
    if (jvxlData.jvxlPlane == null) {
      if (jvxlData.isContoured) {
        appendAttrib(info, "\n  ", "contoured", "true"); 
        appendAttrib(info, "\n  ", "colorMapped", "true");
      } else if (jvxlData.isBicolorMap) {
        appendAttrib(info, "\n  ", "bicolorMap", "true");
      } else if (nColorData > 0) {
        appendAttrib(info, "\n  ", "colorMapped", "true");
      }
      if (jvxlData.vContours != null && jvxlData.vContours.length > 0)
        appendAttrib(info, "\n  ", "nContourData", "" + jvxlData.vContours.length);
    } else {
      if (jvxlData.scale3d != 0)
        appendAttrib(info, "\n  ", "scale3d", "" + jvxlData.scale3d);
      if (nColorData > 0)
        appendAttrib(info, "\n  ", "colorMapped", "true");
      appendAttrib(info, "\n  ", "plane", Escape.escape(jvxlData.jvxlPlane));
    }
    
    if (jvxlData.isJvxlPrecisionColor)
      appendAttrib(info, "\n  ", "precisionColor", "true");
    if (jvxlData.isContoured) {
      if (jvxlData.contourValues == null || jvxlData.contourColixes == null) {
        if (jvxlData.vContours == null)
          appendAttrib(info, "\n  ", "nContours", "" + Math.abs(jvxlData.nContours));
      } else {
        if (jvxlData.jvxlPlane != null)
          appendAttrib(info, "\n  ", "contoured", "true");
        appendAttrib(info, "\n  ", "nContours", "" + jvxlData.contourValues.length);
        appendAttrib(info, "\n  ", "contourValues", Escape.escapeArray(jvxlData.contourValues));
        appendAttrib(info, "\n  ", "contourColors", jvxlData.contourColors);
      }
    }
    
    
    float min = (jvxlData.mappedDataMin == Float.MAX_VALUE ? 0f
        : jvxlData.mappedDataMin);
    if (jvxlData.jvxlColorData != null && jvxlData.jvxlColorData.length() > 0 && !jvxlData.isBicolorMap) {
      appendAttrib(info, "\n  ", "dataMinimum", "" + min);
      appendAttrib(info, "\n  ", "dataMaximum", "" + jvxlData.mappedDataMax);
      appendAttrib(info, "\n  ", "valueMappedToRed", "" + jvxlData.valueMappedToRed);
      appendAttrib(info, "\n  ", "valueMappedToBlue", "" + jvxlData.valueMappedToBlue);
    }
    
    if (jvxlData.insideOut)
      appendAttrib(info, "\n  ", "insideOut", "true");
    
    
    if (jvxlData.isXLowToHigh)
      appendAttrib(info, "\n  ", "note", "progressive JVXL+ -- X values read from low(0) to high("
              + (jvxlData.nPointsX - 1) + ")");
    appendAttrib(info, "\n  ", "xyzMin", Escape.escape(jvxlData.boundingBox[0]));
    appendAttrib(info, "\n  ", "xyzMax", Escape.escape(jvxlData.boundingBox[1]));
    appendAttrib(info, "\n  ", "approximateCompressionRatio", "#RATIO#:1");
    appendAttrib(info, "\n  ", "jmolVersion", jvxlData.version);
    return "<jvxlSurfaceInfo" + info + ">\n</jvxlSurfaceInfo>";
  }
  
  public static final int CONTOUR_NPOLYGONS = 0;
  public static final int CONTOUR_BITSET = 1;
  public static final int CONTOUR_VALUE = 2;
  public static final int CONTOUR_COLIX = 3;
  public static final int CONTOUR_COLOR = 4;
  public static final int CONTOUR_FDATA = 5;
  public static final int CONTOUR_POINTS = 6; 

  
  private static void jvxlEncodeContourData(Vector[] contours, StringBuffer sb) {
    sb.append("<jvxlContourData count=\"" + contours.length
        + "\">\n");
    for (int i = 0; i < contours.length; i++) {
      if (contours[i].size() < CONTOUR_POINTS)
        continue;
      int nPolygons = ((Integer) contours[i]
          .get(CONTOUR_NPOLYGONS)).intValue();
      StringBuffer sb1 = new StringBuffer();
      BitSet bs = (BitSet) contours[i].get(CONTOUR_BITSET);
      jvxlEncodeBitSet(bs, nPolygons, sb1);
      appendTag(sb, "jvxlContour", " ", new String[] {
          "index", "" + i,
          "value", "" + contours[i].get(CONTOUR_VALUE),
          "color", Escape.escapeColor(((int[]) contours[i]
              .get(CONTOUR_COLOR))[0]),
          "count", "" + bs.length(),
          "encoding", "base90iff1",
          "data", jvxlCompressString(contours[i].get(CONTOUR_FDATA).toString(), true) }, sb1.toString(), "\n");
    }
    sb.append("</jvxlContourData>\n");
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
    appendTag(sb, "jvxlTriangleData", " ", new String[] {
        "count", "" + nTri,
        "encoding", "jvxltdiff",
        "data" , jvxlCompressString(list1.toString(), escapeXml) }, null, null);
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
    appendTag(sb, "jvxlVertexData", " ", new String[] {
        "count", "" + vertexCount,
        "min", Escape.escape(min),
        "max", Escape.escape(max),
        "encoding", "base90xyz2",
        "data", jvxlCompressString(list1.toString(), escapeXml) }, null, null);
    if (polygonColorData != null)
      appendTag(sb, "jvxlPolygonColorData", " ", new String[] {
          "encoding", "jvxlnc" }, polygonColorData, "\n");
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

  
  
  public static int jvxlEncodeBitSet(BitSet bs, int nPoints, StringBuffer sb) {
    
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

  public static BitSet jvxlDecodeBitSet(String data) {
    
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

  private static String jvxlGetFileVersion1(JvxlData jvxlData,
                                            MeshData meshData, String[] title,
                                            String msg, boolean includeHeader,
                                            int nSurfaces, String state,
                                            String comment) {
    
    if ("TRAILERONLY".equals(msg))
      return "";
    StringBuffer data = new StringBuffer();
    if (includeHeader) {
      String s = jvxlData.jvxlFileHeader
          + (nSurfaces > 0 ? -nSurfaces : -1) +" " + jvxlData.edgeFractionBase + " "
          + jvxlData.edgeFractionRange + " " + jvxlData.colorFractionBase + " "
          + jvxlData.colorFractionRange + " Jmol voxel format version " +  JVXL_VERSION1 + "\n";
      if (s.indexOf("#JVXL") != 0)
        data.append("#JVXL").append(jvxlData.isXLowToHigh ? "+" : "").append(
            " VERSION ").append(JVXL_VERSION1).append("\n");
      data.append(s);
    }
    if ("HEADERONLY".equals(msg))
      return data.toString();
    data.append("# ").append(msg).append('\n');
    if (title != null)
      for (int i = 0; i < title.length; i++)
        data.append("# ").append(title[i]).append('\n');
    state = (state == null ? "" : " rendering:" + state);
    String definitionLine = jvxlGetDefinitionLineVersion1(jvxlData);
    data.append(definitionLine).append(state).append('\n');
    StringBuffer sb = new StringBuffer();
    String colorData = (jvxlData.jvxlColorData == null ? "" : jvxlData.jvxlColorData);
     
    if (jvxlData.jvxlPlane == null) {
      if (jvxlData.jvxlEdgeData == null)
        return "";
      
      sb.append(jvxlData.jvxlSurfaceData);
      sb.append(jvxlCompressString(jvxlData.jvxlEdgeData, false)).append('\n').append(
          jvxlCompressString(colorData, false)).append('\n');
    } else if (colorData != null) {
      sb.append(jvxlCompressString(colorData, false)).append('\n');
    }
    int len = sb.length();
    data.append(sb);
    if (includeHeader) {
      if (msg != null && !jvxlData.vertexDataOnly)
        data.append("#-------end of jvxl file data-------\n");
      data.append(jvxlGetInfo(jvxlData, false)).append('\n');
        jvxlAppendCommandState(data, comment, state, false);
      if (includeHeader)
        appendTag(data, "jvxlFileTitle", null, null, jvxlData.jvxlFileTitle, "");
    }
    return jvxlSetCompressionRatio(data, jvxlData, len);
  }

  private static String jvxlGetDefinitionLineVersion1(JvxlData jvxlData) {
    String definitionLine = jvxlData.cutoff + " ";

    
    
    
    
    
    
    
    
    
    
    
    

    

    if (jvxlData.jvxlSurfaceData == null)
      return "";
    int nSurfaceInts = jvxlData.nSurfaceInts;
    int bytesUncompressedEdgeData = (jvxlData.vertexDataOnly ? 0
        : jvxlData.jvxlEdgeData.length() - 1);
    int nColorData = (jvxlData.jvxlColorData == null ? -1 : (jvxlData.jvxlColorData.length() - 1));
    if (jvxlData.jvxlPlane == null) {
      if (jvxlData.isContoured) {
        definitionLine += (-1 - nSurfaceInts) + " " + bytesUncompressedEdgeData;
      } else if (jvxlData.isBicolorMap) {
        definitionLine += (nSurfaceInts) + " " + (-bytesUncompressedEdgeData);
      } else {
        definitionLine += nSurfaceInts + " " + bytesUncompressedEdgeData;
      }
      definitionLine += " "
          + (jvxlData.isJvxlPrecisionColor && nColorData != -1 ? -nColorData
              : nColorData);
    } else {
      String s = " " + jvxlData.jvxlPlane.x + " " + jvxlData.jvxlPlane.y + " "
          + jvxlData.jvxlPlane.z + " " + jvxlData.jvxlPlane.w;
      definitionLine += (jvxlData.isContoured ? "-1 -2 " + (-nColorData)
          : "-1 -1 " + nColorData)
          + s;
    }
    if (jvxlData.isContoured) {
      if (jvxlData.contourValues == null || jvxlData.contourColixes == null) {
        definitionLine += " " + jvxlData.nContours;
      } else {
        definitionLine += " " + Escape.escapeArray(jvxlData.contourValues)
            + " \"" + jvxlData.contourColors + "\"";
      }
    }
    
    float min = (jvxlData.mappedDataMin == Float.MAX_VALUE ? 0f
        : jvxlData.mappedDataMin);
    definitionLine += " " + min + " " + jvxlData.mappedDataMax + " "
        + jvxlData.valueMappedToRed + " " + jvxlData.valueMappedToBlue;
    if (jvxlData.insideOut) {
      definitionLine += " insideOut";
    }
    return definitionLine;
  }


}
