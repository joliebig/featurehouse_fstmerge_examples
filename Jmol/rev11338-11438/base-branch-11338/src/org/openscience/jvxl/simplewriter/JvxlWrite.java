
package org.openscience.jvxl.simplewriter;

import java.util.BitSet;
import java.util.Vector;

import javax.vecmath.Point3f;

public class JvxlWrite {

  
  private final static String JVXL_VERSION = "2.0";

  final private static int defaultEdgeFractionBase = 35; 
  final private static int defaultEdgeFractionRange = 90;

  public JvxlWrite() {
  }

  public static String jvxlGetData(VoxelDataCreator vdc, JvxlData jvxlData,
                                   VolumeData volumeData, String title,
                                   Vector surfacePointsReturn,
                                   float[] areaVolumeReturn) {
    
    
    StringBuffer sb = new StringBuffer();
    if (title != null)
      sb.append(title);
    Point3f[] atomXYZ = null;
    int[] atomNo = null;
    int nAtoms = Integer.MAX_VALUE;
    jvxlCreateHeader(volumeData, nAtoms, atomXYZ, atomNo,
        jvxlData.isXLowToHigh, sb);
    jvxlData.jvxlFileHeader = sb.toString();
    int[] counts = volumeData.getVoxelCounts();
    jvxlData.nPointsX = counts[0];
    jvxlData.nPointsY = counts[1];
    jvxlData.nPointsZ = counts[2];
    boolean doCalcArea = (areaVolumeReturn != null);
    SimpleMarchingCubes mc = new SimpleMarchingCubes(vdc, volumeData,
        jvxlData.cutoff, jvxlData.isCutoffAbsolute, jvxlData.isXLowToHigh,
        surfacePointsReturn, doCalcArea);
    jvxlData.jvxlEdgeData = mc.getEdgeData();
    setSurfaceInfoFromBitSet(jvxlData, mc.getBsVoxels());
    jvxlData.jvxlDefinitionLine = jvxlGetDefinitionLine(jvxlData);
    if (doCalcArea) {
      areaVolumeReturn[0] = mc.getCalculatedArea();
      areaVolumeReturn[1] = mc.getCalculatedVolume();
    }
    return jvxlGetFile(jvxlData);
  }

  public static void setSurfaceInfoFromBitSet(JvxlData jvxlData, BitSet bs) {
    boolean inside = false;
    int dataCount = 0;
    StringBuffer sb = new StringBuffer();
    int nSurfaceInts = 0;
    int nPoints = jvxlData.nPointsX * jvxlData.nPointsY * jvxlData.nPointsZ;
    for (int i = 0; i < nPoints; ++i) {
      if (inside == bs.get(i)) {
        dataCount++;
      } else {
        sb.append(' ').append(dataCount);
        nSurfaceInts++;
        dataCount = 1;
        inside = !inside;
      }
    }
    sb.append(' ').append(dataCount).append('\n');
    setSurfaceInfo(jvxlData, nSurfaceInts, sb);
  }
  
  static char jvxlFractionAsCharacter(float fraction) {
    
      
    
        
    return jvxlFractionAsCharacter(fraction, defaultEdgeFractionBase,
        defaultEdgeFractionRange);
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


  protected static void setSurfaceInfo(JvxlData jvxlData, int nSurfaceInts, StringBuffer surfaceData) {
    jvxlData.jvxlSurfaceData = surfaceData.toString();
    if (jvxlData.jvxlSurfaceData.indexOf("--") == 0)
      jvxlData.jvxlSurfaceData = jvxlData.jvxlSurfaceData.substring(2);
    jvxlData.nSurfaceInts = nSurfaceInts;
  }
  
  private static String jvxlGetFile(JvxlData jvxlData) {
    StringBuffer data = new StringBuffer();
    String s = jvxlData.jvxlFileHeader + jvxlExtraLine(jvxlData, 1);
    if (s.indexOf("#JVXL") != 0)
      data.append("#JVXL").append(jvxlData.isXLowToHigh ? "+" : "").append(
          " VERSION ").append(JVXL_VERSION).append("\n");
    data.append(s);
    data.append("# ").append('\n');
    data.append(jvxlData.jvxlDefinitionLine).append('\n');
    StringBuffer sb = new StringBuffer();
    sb.append(jvxlData.jvxlSurfaceData);
    sb.append(jvxlCompressString(jvxlData.jvxlEdgeData));
    data.append(sb);
    return data.toString();
  }

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  


  

  private static void jvxlCreateHeader(VolumeData v, int nAtoms,
                                       Point3f[] atomXyz, int[] atomNo,
                                       boolean isXLowToHigh,
                                       StringBuffer sb) {
    
    
    if (sb.length() == 0)
      sb.append("Line 1\nLine 2\n");
    sb.append(isXLowToHigh ? "+" : "-");
    sb.append(nAtoms == Integer.MAX_VALUE ? 2 : Math.abs(nAtoms));
    sb.append(' ').append(
        v.volumetricOrigin.x).append(' ').append(v.volumetricOrigin.y).append(
        ' ').append(v.volumetricOrigin.z).append(" ANGSTROMS\n");
    for (int i = 0; i < 3; i++)
      sb.append(v.voxelCounts[i]).append(' ').append(v.volumetricVectors[i].x)
          .append(' ').append(v.volumetricVectors[i].y).append(' ').append(
              v.volumetricVectors[i].z).append('\n');
    if (nAtoms == Integer.MAX_VALUE) {
      jvxlAddDummyAtomList(v, sb);
      return;
    }
    nAtoms = Math.abs(nAtoms);
    for (int i = 0, n = 0; i < nAtoms; i++)
      sb.append((n = Math.abs(atomNo[i])) + " " + n + ".0 " + atomXyz[i].x
          + " " + atomXyz[i].y + " " + atomXyz[i].z + "\n");
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

  private static String jvxlGetDefinitionLine(JvxlData jvxlData) {
    String definitionLine = jvxlData.cutoff + " ";

    
    
    
    
    
    
    
    
    
    

    

    if (jvxlData.jvxlSurfaceData == null)
      return "";
    int nSurfaceInts = jvxlData.nSurfaceInts;
    int bytesUncompressedEdgeData = jvxlData.jvxlEdgeData.length() - 1;
    definitionLine += nSurfaceInts + " " + bytesUncompressedEdgeData
        + " -1 0 0 0 0";
    return definitionLine;
  }

  private static String jvxlExtraLine(JvxlData jvxlData, int n) {
    return (-n) + " " + jvxlData.edgeFractionBase + " "
        + jvxlData.edgeFractionRange + " " + jvxlData.colorFractionBase + " "
        + jvxlData.colorFractionRange + " Jmol voxel format version "
        + JVXL_VERSION + "\n";
  }

  

  private static char jvxlFractionAsCharacter(float fraction, int base,
                                              int range) {
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

  static String jvxlCompressString(String data) {
    
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
        if (nLast < 4 || chLast == '~' || chLast == ' ' || chLast == '\t')
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

}
