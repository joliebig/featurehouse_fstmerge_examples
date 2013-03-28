
package org.jmol.jvxl.readers;

import java.util.BitSet;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

import org.jmol.util.*;
import org.jmol.g3d.Graphics3D;
import org.jmol.jvxl.data.*;
import org.jmol.jvxl.api.MeshDataServer;
import org.jmol.jvxl.api.VertexDataServer;
import org.jmol.jvxl.calc.*;

public abstract class SurfaceReader implements VertexDataServer {

  

  protected SurfaceGenerator sg;
  protected MeshDataServer meshDataServer;

  protected ColorEncoder colorEncoder;

  protected Parameters params;
  protected MeshData meshData;
  protected JvxlData jvxlData;
  protected VolumeData volumeData;
  private String edgeData;

  protected boolean isProgressive = false;
  protected boolean isXLowToHigh = false; 
  private float assocCutoff = 0.3f;

  boolean vertexDataOnly;
  boolean hasColorData;

  SurfaceReader(SurfaceGenerator sg) {
    this.sg = sg;
    colorEncoder = sg.getColorEncoder();
    params = sg.getParams();
    marchingSquares = sg.getMarchingSquares();
    assocCutoff = params.assocCutoff;
    isXLowToHigh = params.isXLowToHigh;
    meshData = sg.getMeshData();
    jvxlData = sg.getJvxlData();
    setVolumeData(sg.getVolumeData());
    meshDataServer = sg.getMeshDataServer();
    cJvxlEdgeNaN = (char) (JvxlCoder.defaultEdgeFractionBase + JvxlCoder.defaultEdgeFractionRange);
  }

  final static float ANGSTROMS_PER_BOHR = 0.5291772f;
  final static float defaultMappedDataMin = 0f;
  final static float defaultMappedDataMax = 1.0f;
  final static float defaultCutoff = 0.02f;

  private int edgeCount;

  protected Point3f volumetricOrigin;
  protected Vector3f[] volumetricVectors;
  protected int[] voxelCounts;
  protected float[][][] voxelData;
  


  void setVolumeData(VolumeData v) {
    nBytes = 0;
    volumetricOrigin = v.volumetricOrigin;
    volumetricVectors = v.volumetricVectors;
    voxelCounts = v.voxelCounts;
    voxelData = v.voxelData;
    volumeData = v;
    
  }

  abstract boolean readVolumeParameters();

  abstract boolean readVolumeData(boolean isMapData);

  
  
  

  protected long nBytes;
  protected int nDataPoints;
  protected int nPointsX, nPointsY, nPointsZ;

  protected boolean isJvxl, isApbsDx;

  protected int edgeFractionBase;
  protected int edgeFractionRange;
  protected int colorFractionBase;
  protected int colorFractionRange;

  protected StringBuffer jvxlFileHeaderBuffer;
  protected StringBuffer fractionData;
  protected String jvxlEdgeDataRead = "";
  protected String jvxlColorDataRead = "";
  protected BitSet jvxlVoxelBitSet;
  protected boolean jvxlDataIsColorMapped;
  protected boolean jvxlDataIsPrecisionColor;
  protected boolean jvxlDataIs2dContour;
  protected float jvxlCutoff;
  protected int jvxlNSurfaceInts;
  protected char cJvxlEdgeNaN;

  protected int contourVertexCount;

  void jvxlUpdateInfo() {
    jvxlData.jvxlUpdateInfo(params.title, nBytes);
  }

  boolean createIsosurface(boolean justForPlane) {
    resetIsosurface();
    if (!readVolumeParameters())
      return false;
    nPointsX = voxelCounts[0];
    nPointsY = voxelCounts[1];
    nPointsZ = voxelCounts[2];
    jvxlData.insideOut = params.insideOut;
    jvxlData.nPointsX = nPointsX;
    jvxlData.nPointsY = nPointsY;
    jvxlData.nPointsZ = nPointsZ;
    jvxlData.jvxlVolumeDataXml = volumeData.xmlData;
    if (justForPlane) {
      float[][][] voxelDataTemp =  volumeData.voxelData;
      volumeData.setDataDistanceToPlane(params.thePlane);
      if (meshDataServer != null)
        meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_VERTICES);
      params.setMapRanges(this);
      generateSurfaceData();
      volumeData.voxelData = voxelDataTemp;
    } else {
      if (!readVolumeData(false))
        return false;
      generateSurfaceData();
    }
    String s = jvxlFileHeaderBuffer.toString();
    int i = s.indexOf('\n', s.indexOf('\n',s.indexOf('\n') + 1) + 1) + 1;
    jvxlData.jvxlFileTitle = s.substring(0, i);
    jvxlData.jvxlFileHeader = s;
    jvxlData.cutoff = (isJvxl ? jvxlCutoff : params.cutoff);
    jvxlData.isCutoffAbsolute = params.isCutoffAbsolute;
    jvxlData.pointsPerAngstrom = 1f/volumeData.volumetricVectorLengths[0];
    jvxlData.jvxlColorData = "";
    jvxlData.jvxlPlane = params.thePlane;
    jvxlData.jvxlEdgeData = edgeData;
    jvxlData.isBicolorMap = params.isBicolorMap;
    jvxlData.isContoured = params.isContoured;
    if (jvxlData.vContours != null)
      params.nContours = jvxlData.vContours.length;
    jvxlData.nContours = (params.contourFromZero 
        ? params.nContours : -1 - params.nContours);
    jvxlData.nEdges = edgeCount;
    jvxlData.edgeFractionBase = edgeFractionBase;
    jvxlData.edgeFractionRange = edgeFractionRange;
    jvxlData.colorFractionBase = colorFractionBase;
    jvxlData.colorFractionRange = colorFractionRange;
    jvxlData.jvxlDataIs2dContour = jvxlDataIs2dContour;
    jvxlData.jvxlDataIsColorMapped = jvxlDataIsColorMapped;
    jvxlData.isXLowToHigh = isXLowToHigh;
    jvxlData.vertexDataOnly = vertexDataOnly;

    if (jvxlDataIsColorMapped) {
      if (meshDataServer != null) {
        meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_VERTICES);
        meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_COLOR_INDEXES);
      }
      jvxlData.jvxlColorData = readColorData();
      updateSurfaceData();
      if (meshDataServer != null)
        meshDataServer.notifySurfaceMappingCompleted();
    }
    return true;
  }

  void resetIsosurface() {
    meshData = new MeshData();
    if (meshDataServer != null)
      meshDataServer.fillMeshData(null, 0);
    contourVertexCount = 0;
    if (params.cutoff == Float.MAX_VALUE)
      params.cutoff = defaultCutoff;
    jvxlData.jvxlSurfaceData = "";
    jvxlData.jvxlEdgeData = "";
    jvxlData.jvxlColorData = "";
    
    edgeCount = 0;
    edgeFractionBase = JvxlCoder.defaultEdgeFractionBase;
    edgeFractionRange = JvxlCoder.defaultEdgeFractionRange;
    colorFractionBase = JvxlCoder.defaultColorFractionBase;
    colorFractionRange = JvxlCoder.defaultColorFractionRange;
    params.mappedDataMin = Float.MAX_VALUE;
  }

  void discardTempData(boolean discardAll) {
    if (!discardAll)
      return;
    voxelData = null;
    sg.setMarchingSquares(marchingSquares = null);
    marchingCubes = null;
  }

  protected void initializeVolumetricData() {
    nPointsX = voxelCounts[0];
    nPointsY = voxelCounts[1];
    nPointsZ = voxelCounts[2];
    volumeData.setUnitVectors();
    setVolumeData(volumeData);
  }

  
  abstract protected void readSurfaceData(boolean isMapData) throws Exception;

  protected boolean gotoAndReadVoxelData(boolean isMapData) {
    
    initializeVolumetricData();
    if (nPointsX > 0 && nPointsY > 0 && nPointsZ > 0)
      try {
        gotoData(params.fileIndex - 1, nPointsX * nPointsY * nPointsZ);
        readSurfaceData(isMapData);
      } catch (Exception e) {
        Logger.error(e.toString());
        return false;
      }
    return true;
  }

  protected void gotoData(int n, int nPoints) throws Exception {
    
  }

  protected String readColorData() {
    
    return "";
  }

  
  
  

  protected MarchingSquares marchingSquares;
  private MarchingCubes marchingCubes;

  public float getValue(int x, int y, int z) {
    return volumeData.voxelData[x][y][z];
  }

  private void generateSurfaceData() {
    edgeData = "";
    if (vertexDataOnly) {
      try {
        readSurfaceData(false);
      } catch (Exception e) {
        e.printStackTrace();
        Logger.error("Exception in SurfaceReader::readSurfaceData: "
            + e.getMessage());
      }
      return;
    }
    contourVertexCount = 0;
    int contourType = -1;
    marchingSquares = null;

    if (params.thePlane != null || params.isContoured) {
      marchingSquares = new MarchingSquares(this, volumeData, params.thePlane,
          params.contoursDiscrete, params.nContours, params.thisContour,
          params.contourFromZero);
      contourType = marchingSquares.getContourType();
      marchingSquares.setMinMax(params.valueMappedToRed,
          params.valueMappedToBlue);
    }
    marchingCubes = new MarchingCubes(this, volumeData, jvxlVoxelBitSet,
        params.isContoured, contourType, params.cutoff,
        params.isCutoffAbsolute, params.isSquared, isXLowToHigh);
    String data = marchingCubes.getEdgeData();
    if (params.thePlane == null)
      edgeData = data;
    jvxlData.setSurfaceInfoFromBitSet(marchingCubes.getBsVoxels(),
        params.thePlane);
    if (isJvxl)
      edgeData = jvxlEdgeDataRead;
  }

  

  protected final Point3f ptTemp = new Point3f();

  public int getSurfacePointIndexAndFraction(float cutoff, boolean isCutoffAbsolute,
                                  int x, int y, int z, Point3i offset, int vA,
                                  int vB, float valueA, float valueB,
                                  Point3f pointA, Vector3f edgeVector,
                                  boolean isContourType, float[] fReturn) {
    float thisValue = getSurfacePointAndFraction(cutoff, isCutoffAbsolute, valueA,
        valueB, pointA, edgeVector, fReturn, ptTemp);
    

    if (marchingSquares != null && params.isContoured)
      return isContourType ? marchingSquares.addContourVertex(x, y, z, offset,
          ptTemp, cutoff) : Integer.MAX_VALUE;
    int assocVertex = (assocCutoff > 0 ? (fReturn[0] < assocCutoff ? vA
        : fReturn[0] > 1 - assocCutoff ? vB : MarchingSquares.CONTOUR_POINT)
        : MarchingSquares.CONTOUR_POINT);
    if (assocVertex >= 0)
      assocVertex = marchingCubes.getLinearOffset(x, y, z, assocVertex);
    int n = addVertexCopy(ptTemp, thisValue, assocVertex);
    if (params.iAddGridPoints) {
      marchingCubes.calcVertexPoint(x, y, z, vB, ptTemp);
      addVertexCopy(valueA < valueB ? pointA : ptTemp, Float.NaN,
          MarchingSquares.EDGE_POINT);
      addVertexCopy(valueA < valueB ? ptTemp : pointA, Float.NaN,
          MarchingSquares.EDGE_POINT);
    }
    return n;
  }

  protected float getSurfacePointAndFraction(float cutoff, boolean isCutoffAbsolute,
                                   float valueA, float valueB, Point3f pointA,
                                   Vector3f edgeVector, float[] fReturn,
                                   Point3f ptReturn) {

    

    float diff = valueB - valueA;
    float fraction = (cutoff - valueA) / diff;
    if (isCutoffAbsolute && (fraction < 0 || fraction > 1))
      fraction = (-cutoff - valueA) / diff;

    if (fraction < 0 || fraction > 1) {
      
      
      fraction = Float.NaN;
    }
    fReturn[0] = fraction;
    ptReturn.scaleAdd(fraction, edgeVector, pointA);
    
    return valueA + fraction * diff;
  }

  public int addVertexCopy(Point3f vertexXYZ, float value, int assocVertex) {
    if (meshDataServer == null)
      return meshData.addVertexCopy(vertexXYZ, value, assocVertex);
    return meshDataServer.addVertexCopy(vertexXYZ, value, assocVertex);
  }

  private int lastColor;
  private short lastColix;

  public int addTriangleCheck(int iA, int iB, int iC, int check, int check2,
                              boolean isAbsolute, int color) {
    
    if (meshDataServer == null) {
      if (isAbsolute
          && !MeshData.checkCutoff(iA, iB, iC, meshData.vertexValues))
        return -1;
      int i = meshData.addTriangleCheck(iA, iB, iC, check, check2);
      if (i >= 0 && color != 0) {
        if (i == 0)
          lastColor = 0;
        short colix = (color == lastColor ? lastColix : (lastColix = Graphics3D
            .getColix(lastColor = color)));
        meshData.addPolygonColix(i, colix);
      }
      return i;
    }
    return meshDataServer.addTriangleCheck(iA, iB, iC, check, check2,
        isAbsolute, color);
  }

  

  
  
  

  void colorIsosurface() {
    if (params.isSquared)
      volumeData.filterData(true, Float.NaN);

    if (meshDataServer != null) {
      meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_VERTICES);
    }

    if (params.isContoured && marchingSquares != null) {
      params.setMapRanges(this);
      marchingSquares.setMinMax(params.valueMappedToRed,
          params.valueMappedToBlue);
      contourVertexCount = marchingSquares
          .generateContourData(jvxlDataIs2dContour);
      jvxlData.contourValuesUsed = marchingSquares.getContourValues();
      if (meshDataServer != null)
        meshDataServer.notifySurfaceGenerationCompleted();
    }

    applyColorScale();
    jvxlData.nContours = (params.contourFromZero 
        ? params.nContours : -1 - params.nContours);
    jvxlData.jvxlFileMessage = "mapped: min = " + params.valueMappedToRed
        + "; max = " + params.valueMappedToBlue;
  }

  void applyColorScale() {
    colorFractionBase = jvxlData.colorFractionBase = JvxlCoder.defaultColorFractionBase;
    colorFractionRange = jvxlData.colorFractionRange = JvxlCoder.defaultColorFractionRange;
    if (params.colorPhase == 0)
      params.colorPhase = 1;
    if (meshDataServer == null) {
      meshData.vertexColixes = new short[meshData.vertexCount];
    } else {
      meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_VERTICES);
      meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_COLOR_INDEXES);
    }
    params.setMapRanges(this);
    
    
    boolean saveColorData = (params.isBicolorMap || params.colorBySign || !params.colorByPhase);
    
    jvxlData.isJvxlPrecisionColor = true;
    jvxlData.valueMappedToRed = params.valueMappedToRed;
    jvxlData.valueMappedToBlue = params.valueMappedToBlue;
    jvxlData.mappedDataMin = params.mappedDataMin;
    jvxlData.mappedDataMax = params.mappedDataMax;
    jvxlData.vertexCount = (contourVertexCount > 0 ? contourVertexCount
        : meshData.vertexCount);
    jvxlData.minColorIndex = -1;
    jvxlData.maxColorIndex = 0;
    jvxlData.contourValues = params.contoursDiscrete;
    jvxlData.isColorReversed = params.isColorReversed;
    if (params.isBicolorMap && !params.isContoured || params.colorBySign) {
      jvxlData.minColorIndex = ColorEncoder
          .getColorIndex(params.isColorReversed ? params.colorPos
              : params.colorNeg);
      jvxlData.maxColorIndex = ColorEncoder
          .getColorIndex(params.isColorReversed ? params.colorNeg
              : params.colorPos);
    }
    jvxlData.isTruncated = (jvxlData.minColorIndex >= 0 && !params.isContoured);
    boolean useMeshDataValues =
    
    vertexDataOnly || params.isBicolorMap && !params.isContoured;
    float value;
    if (!useMeshDataValues)
      for (int i = meshData.vertexCount; --i >= 0;) {
        
        if (params.colorBySets)
          value = meshData.vertexSets[i];
        else if (params.colorByPhase)
          value = getPhase(meshData.vertices[i]);
        else if (jvxlDataIs2dContour)
          value = marchingSquares
              .getInterpolatedPixelValue(meshData.vertices[i]);
        else
          value = volumeData.lookupInterpolatedVoxelValue(meshData.vertices[i]);
        meshData.vertexValues[i] = value;
      }
    colorData();

    JvxlCoder.jvxlCreateColorData(jvxlData,
        (saveColorData ? meshData.vertexValues : null));

    if (meshDataServer != null && params.colorBySets)
      meshDataServer.fillMeshData(meshData, MeshData.MODE_PUT_SETS);
  }

  private void colorData() {

    float[] vertexValues = meshData.vertexValues;
    short[] vertexColixes = meshData.vertexColixes;
    meshData.polygonColixes = null;
    float valueBlue = jvxlData.valueMappedToBlue;
    float valueRed = jvxlData.valueMappedToRed;
    short minColorIndex = jvxlData.minColorIndex;
    short maxColorIndex = jvxlData.maxColorIndex;

    for (int i = meshData.vertexCount; --i >= 0;) {
      float value = vertexValues[i];
      if (minColorIndex >= 0) {
        if (value <= 0)
          vertexColixes[i] = minColorIndex;
        else if (value > 0)
          vertexColixes[i] = maxColorIndex;
      } else {
        if (value < valueRed)
          value = valueRed;
        if (value >= valueBlue)
          value = valueBlue;
        vertexColixes[i] = getColorIndexFromPalette(value);
      }
    }

    if ((params.nContours > 0 || jvxlData.contourValues != null) && jvxlData.contourColixes == null) {
      int n = (jvxlData.contourValues == null ? params.nContours : jvxlData.contourValues.length);
      short[] colors = jvxlData.contourColixes = new short[n];
      float dv = (valueBlue - valueRed) / (n + 1);
      
      for (int i = 0; i < n; i++) {
        float v = (jvxlData.contourValues == null ? valueRed + (i + 1) * dv : jvxlData.contourValues[i]);
        colors[i] = Graphics3D.getColix(getArgbFromPalette(v));
      }
      jvxlData.contourColors = Graphics3D.getHexCodes(colors);
    }
  }
  
  private final static String[] colorPhases = { "_orb", "x", "y", "z", "xy",
      "yz", "xz", "x2-y2", "z2" };

  static int getColorPhaseIndex(String color) {
    int colorPhase = -1;
    for (int i = colorPhases.length; --i >= 0;)
      if (color.equalsIgnoreCase(colorPhases[i])) {
        colorPhase = i;
        break;
      }
    return colorPhase;
  }

  private float getPhase(Point3f pt) {
    switch (params.colorPhase) {
    case 0:
    case -1:
    case 1:
      return (pt.x > 0 ? 1 : -1);
    case 2:
      return (pt.y > 0 ? 1 : -1);
    case 3:
      return (pt.z > 0 ? 1 : -1);
    case 4:
      return (pt.x * pt.y > 0 ? 1 : -1);
    case 5:
      return (pt.y * pt.z > 0 ? 1 : -1);
    case 6:
      return (pt.x * pt.z > 0 ? 1 : -1);
    case 7:
      return (pt.x * pt.x - pt.y * pt.y > 0 ? 1 : -1);
    case 8:
      return (pt.z * pt.z * 2f - pt.x * pt.x - pt.y * pt.y > 0 ? 1 : -1);
    }
    return 1;
  }

  float getMinMappedValue() {
    if (params.colorBySets)
      return 0;
    int vertexCount = (contourVertexCount > 0 ? contourVertexCount
        : meshData.vertexCount);
    Point3f[] vertexes = meshData.vertices;
    float min = Float.MAX_VALUE;
    for (int i = 0; i < vertexCount; i++) {
      float challenger;
      if (vertexDataOnly)
        challenger = meshData.vertexValues[i];
      else if (jvxlDataIs2dContour)
        challenger = marchingSquares.getInterpolatedPixelValue(vertexes[i]);
      else
        challenger = volumeData.lookupInterpolatedVoxelValue(vertexes[i]);
      if (challenger < min)
        min = challenger;
    }
    return min;
  }

  float getMaxMappedValue() {
    if (params.colorBySets)
      return Math.max(meshData.nSets - 1, 0);
    int vertexCount = (contourVertexCount > 0 ? contourVertexCount
        : meshData.vertexCount);
    Point3f[] vertexes = meshData.vertices;
    float max = -Float.MAX_VALUE;
    int incr = 1;
    for (int i = 0; i < vertexCount; i += incr) {
      float challenger;
      if (vertexDataOnly)
        challenger = meshData.vertexValues[i];
      else if (jvxlDataIs2dContour)
        challenger = marchingSquares.getInterpolatedPixelValue(vertexes[i]);
      else
        challenger = volumeData.lookupInterpolatedVoxelValue(vertexes[i]);
      if (challenger == Float.MAX_VALUE)
        challenger = 0; 
      if (challenger > max && challenger != Float.MAX_VALUE)
        max = challenger;
    }
    return max;
  }

  protected short getColorIndexFromPalette(float value) {
    if (params.isColorReversed)
      return colorEncoder.getColorIndexFromPalette(-value,
          -params.valueMappedToBlue, -params.valueMappedToRed);
    return colorEncoder.getColorIndexFromPalette(value,
        params.valueMappedToRed, params.valueMappedToBlue);
  }

  protected int getArgbFromPalette(float value) {
    if (params.isColorReversed)
      return colorEncoder.getArgbFromPalette(-value,
          -params.valueMappedToBlue, -params.valueMappedToRed);
    return colorEncoder.getArgbFromPalette(value,
        params.valueMappedToRed, params.valueMappedToBlue);
  }

  void updateTriangles() {
    if (meshDataServer == null) {
      meshData.invalidateTriangles();
    } else {
      meshDataServer.invalidateTriangles();
    }
  }

  void updateSurfaceData() {
    updateTriangles();
    jvxlData.updateSurfaceData(meshData.vertexValues,
        meshData.vertexCount, meshData.vertexIncrement, cJvxlEdgeNaN);
  }

  public void selectPocket(boolean doExclude) {
    
  }

  void excludeMinimumSet() {
    if (meshDataServer != null)
      meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_VERTICES);
    meshData.getSurfaceSet();
    BitSet bs;
    for (int i = meshData.nSets; --i >= 0;)
      if ((bs = meshData.surfaceSet[i]) != null) {
        int n = 0;
        for (int j = bs.size(); --j >= 0;)
          
          if (bs.get(j))
            n++;
        if (n < params.minSet)
          meshData.invalidateSurfaceSet(i);
      }
    updateSurfaceData();
    if (meshDataServer != null)
      meshDataServer.fillMeshData(meshData, MeshData.MODE_PUT_SETS);
  }

  void excludeMaximumSet() {
    if (meshDataServer != null)
      meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_VERTICES);
    meshData.getSurfaceSet();
    BitSet bs;
    for (int i = meshData.nSets; --i >= 0;)
      if ((bs = meshData.surfaceSet[i]) != null) {
        int n = 0;
        for (int j = bs.size(); --j >= 0;)
          
          if (bs.get(j))
            n++;
        if (n > params.maxSet)
          meshData.invalidateSurfaceSet(i);
      }
    updateSurfaceData();
    if (meshDataServer != null)
      meshDataServer.fillMeshData(meshData, MeshData.MODE_PUT_SETS);
  }
  
}
