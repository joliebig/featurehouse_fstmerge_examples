



package org.jmol.jvxl.readers;

import java.io.BufferedReader;
import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import org.jmol.util.*;
import org.jmol.atomdata.AtomDataServer;
import org.jmol.g3d.Graphics3D;
import org.jmol.jvxl.data.JvxlData;
import org.jmol.jvxl.data.VolumeData;
import org.jmol.jvxl.data.MeshData;
import org.jmol.jvxl.api.MeshDataServer;
import org.jmol.jvxl.calc.MarchingSquares;

public class SurfaceGenerator {

  private ColorEncoder colorEncoder;
  private JvxlData jvxlData;
  private MeshData meshData;
  private Parameters params;
  private VolumeData volumeData;
  private MeshDataServer meshDataServer;
  private AtomDataServer atomDataServer;
  private MarchingSquares marchingSquares;
  private String version;
  private String fileType;
    
  public void setVersion(String version) {
    this.version = version;
  }
  
  SurfaceReader surfaceReader;

  public SurfaceGenerator() {
    
    setup(null, null, null, null, null);
  }

  public SurfaceGenerator(AtomDataServer atomDataServer, MeshDataServer meshDataServer,
                          ColorEncoder colorEncoder, MeshData meshData, JvxlData jvxlData) {
    
    setup(atomDataServer, meshDataServer, colorEncoder, meshData, jvxlData);
  }

  private void setup(AtomDataServer atomDataServer, MeshDataServer meshDataServer,
      ColorEncoder colorEncoder, MeshData meshData, JvxlData jvxlData) {
    this.atomDataServer = atomDataServer;
    this.meshDataServer = meshDataServer;
    params = new Parameters();
    this.colorEncoder = (colorEncoder == null ? new ColorEncoder()
        : colorEncoder);
    this.meshData = (meshData == null ? new MeshData() : meshData);
    
    this.jvxlData = (jvxlData == null ? new JvxlData() : jvxlData);
    volumeData = new VolumeData();
    initializeIsosurface();
  }
  
  public boolean isStateDataRead() {
    return params.state == Parameters.STATE_DATA_READ;
  }

  public int getDataType() {
    return params.dataType;
  }
  
  MeshDataServer getMeshDataServer() {
    return meshDataServer;
  }

  AtomDataServer getAtomDataServer() {
    return atomDataServer;
  }

  ColorEncoder getColorEncoder() {
    return colorEncoder;
  }

  public void setJvxlData(JvxlData jvxlData) {
    this.jvxlData = jvxlData;
    if (jvxlData != null)
      jvxlData.version = version;
  }

  public JvxlData getJvxlData() {
    return jvxlData;
  }

  MeshData getMeshData() {
    return meshData;
  }

  void setMarchingSquares(MarchingSquares marchingSquares) {
    this.marchingSquares = marchingSquares;  
  }
  
  MarchingSquares getMarchingSquares() {
    return marchingSquares;
  }
  
  public Parameters getParams() {
    return params;
  }

  public String getScript() {
    return params.script;
  }
  
  public String[] getTitle() {
    return params.title;
  }
  
  public BitSet getBsSelected() {
    return params.bsSelected;
  }
  
  public BitSet getBsIgnore() {
    return params.bsIgnore;
  }
  
  public Vector getFunctionXYinfo() {
    return params.functionXYinfo;
  }

  VolumeData getVolumeData() {
    return volumeData;
  }

  public Point4f getPlane() {
    return params.thePlane;
  }
  
  public int getColor(int which) {
    switch(which) {
    case -1:
      return params.colorNeg;
    case 1:
      return params.colorPos;
    }
    return 0;
  }
  
  public void setModelIndex(int modelIndex) {
    params.modelIndex = modelIndex;
  }

  public boolean getIUseBitSets() {
    return params.iUseBitSets;
  }

  public boolean getIAddGridPoints() {
    return params.iAddGridPoints;
  }

  public boolean getIsPositiveOnly() {
    return params.isPositiveOnly;
  }
  
  public boolean isInsideOut() {
    return params.insideOut;
  }

  public float getCutoff() {
    return params.cutoff;
  }
  
  public Hashtable getMoData() {
    return params.moData;
  }
  
  public boolean isCubeData() {
    return jvxlData.wasCubic;
  }
  
  

  int colorPtr;
  private boolean rangeDefined;

  

  public boolean setParameter(String propertyName, Object value) {
    return setParameter(propertyName, value, null);
  }

  public boolean setParameter(String propertyName, Object value, BitSet bs) {

    if ("debug" == propertyName) {
      boolean TF = ((Boolean) value).booleanValue();
      params.logMessages = TF;
      
      params.logCube = TF;
      return true;
    }

    if ("init" == propertyName) {
      initializeIsosurface();
      if (value instanceof Parameters)
        params = (Parameters) value;
      else
        params.script = (String) value;
      return false; 
    }

    if ("finalize" == propertyName) {
      initializeIsosurface();
      return true;
    }

    if ("commandOption" == propertyName) {
      String s = " # " + (String) value;
      if (params.script.indexOf(s) < 0)
        params.script += s;
      return true;
    }

    if ("clear" == propertyName) {
      if (surfaceReader != null)
        surfaceReader.discardTempData(true);
      return false;
    }

    if ("fileIndex" == propertyName) {
      params.fileIndex = ((Integer) value).intValue();
      if (params.fileIndex < 1)
        params.fileIndex = 1;
      params.readAllData = false;
      return true;
    }

    if ("blockData" == propertyName) {
      params.blockCubeData = ((Boolean) value).booleanValue();
      return true;
    }

    if ("bsSolvent" == propertyName) {
      params.bsSolvent = (BitSet) value;
      return true;
    }

    if ("propertySmoothing" == propertyName) {
      params.propertySmoothing = ((Boolean) value).booleanValue();
      return true;
    }

    if ("title" == propertyName) {
      if (value == null) {
        params.title = null;
        return true;
      } else if (value instanceof String[]) {
        params.title = (String[]) value;
        for (int i = 0; i < params.title.length; i++)
          if (params.title[i].length() > 0)
            Logger.info(params.title[i]);
      }
      return true;
    }

    if ("cutoff" == propertyName) {
      params.cutoff = ((Float) value).floatValue();
      params.isPositiveOnly = false;
      return true;
    }

    if ("cutoffPositive" == propertyName) {
      params.cutoff = ((Float) value).floatValue();
      params.isPositiveOnly = true;
      return true;
    }

    if ("cappingPlane" == propertyName) {
      params.cappingPlane = (Point4f) value;
      params.doCapIsosurface = (params.cappingPlane != null);
      return true;
    }

    if ("select" == propertyName) {
      params.bsSelected = (BitSet) value;
      return true;
    }

    if ("ignore" == propertyName) {
      params.bsIgnore = (BitSet) value;
      return true;
    }

    if ("scale" == propertyName) {
      params.scale = ((Float) value).floatValue();
      return true;
    }

    if ("angstroms" == propertyName) {
      params.isAngstroms = true;
      return true;
    }

    if ("resolution" == propertyName) {
      float resolution = ((Float) value).floatValue();
      params.resolution = (resolution > 0 ? resolution : Float.MAX_VALUE);
      return true;
    }

    if ("downsample" == propertyName) {
      int rate = ((Integer) value).intValue();
      params.downsampleFactor = (rate >= 0 ? rate : 0);
      return true;
    }

    if ("anisotropy" == propertyName) {
      if ((params.dataType & Parameters.NO_ANISOTROPY) == 0)
        params.setAnisotropy((Point3f) value);
      return true;
    }

    if ("eccentricity" == propertyName) {
      params.setEccentricity((Point4f) value);
      return true;
    }

    if ("addHydrogens" == propertyName) {
      params.addHydrogens = ((Boolean) value).booleanValue();
      return true;
    }

    if ("squareData" == propertyName) {
      params.isSquared = ((Boolean) value).booleanValue();
      return true;
    }

    if ("gridPoints" == propertyName) {
      params.iAddGridPoints = true;
      return true;
    }

    if ("atomIndex" == propertyName) {
      params.atomIndex = ((Integer) value).intValue();
      return true;
    }

    

    if ("remappable" == propertyName) {
      params.remappable = true;
      return true;
    }

    if ("insideOut" == propertyName) {
      params.insideOut = true;
      return true;
    }

    if ("sign" == propertyName) {
      params.isCutoffAbsolute = true;
      params.colorBySign = true;
      colorPtr = 0;
      return true;
    }

    if ("colorRGB" == propertyName) {
      int rgb = ((Integer) value).intValue();
      params.colorPos = params.colorPosLCAO = rgb;
      if (colorPtr++ == 0)
        params.colorNeg = params.colorNegLCAO = rgb;
      return true;
    }

    if ("rangeAll" == propertyName) {
      params.rangeAll = true;
      return true;
    }

    if ("red" == propertyName) {
      params.valueMappedToRed = ((Float) value).floatValue();
      return true;
    }

    if ("blue" == propertyName) {
      params.valueMappedToBlue = ((Float) value).floatValue();
      params.rangeDefined = true;
      params.rangeAll = false;
      return true;
    }

    if ("reverseColor" == propertyName) {
      params.isColorReversed = true;
      return true;
    }

    if ("setColorScheme" == propertyName) {
      String colorScheme = (String) value;
      if (colorScheme.equals("sets")) {
        propertyName = "mapColor";
      } else {
        colorEncoder.setColorScheme(colorScheme);
        if (surfaceReader != null
            && params.state == Parameters.STATE_DATA_COLORED)
          surfaceReader.applyColorScale();
        return true;
      }
    }

    if ("center" == propertyName) {
      params.center.set((Point3f) value);
      return true;
    }

    if ("withinDistance" == propertyName) {
      params.distance = ((Float) value).floatValue();
      return true;
    }

    if ("withinPoint" == propertyName) {
      params.point = (Point3f) value;
      return true;
    }

    if ("progressive" == propertyName) {
      params.isXLowToHigh = true;
      return true;
    }

    if ("phase" == propertyName) {
      String color = (String) value;
      params.isCutoffAbsolute = true;
      params.colorBySign = true;
      params.colorByPhase = true;
      params.colorPhase = SurfaceReader.getColorPhaseIndex(color);
      if (params.colorPhase < 0) {
        Logger.warn(" invalid color phase: " + color);
        params.colorPhase = 0;
      }
      params.colorByPhase = params.colorPhase != 0;
      if (params.state >= Parameters.STATE_DATA_READ) {
        params.dataType = params.surfaceType;
        params.state = Parameters.STATE_DATA_COLORED;
        params.isBicolorMap = true;
        surfaceReader.applyColorScale();
      }

      return true;
    }

    

    boolean useIonic;
    if ((useIonic = ("ionicRadius" == propertyName))
        || "vdwRadius" == propertyName) {
      params.setRadius(useIonic, ((Float) value).floatValue());
      return true;
    }

    if ("envelopeRadius" == propertyName) {
      params.envelopeRadius = ((Float) value).floatValue();
      return true;
    }

    if ("cavityRadius" == propertyName) {
      params.cavityRadius = ((Float) value).floatValue();
      return true;
    }

    if ("cavity" == propertyName) {
      params.isCavity = true;
      return true;
    }

    if ("pocket" == propertyName) {
      params.pocket = (Boolean) value;
      return true;
    }

    if ("minset" == propertyName) {
      params.minSet = ((Integer) value).intValue();
      return true;
    }

    if ("maxset" == propertyName) {
      params.maxSet = ((Integer) value).intValue();
      return true;
    }

    if ("plane" == propertyName) {
      params.setPlane((Point4f) value);
      return true;
    }

    if ("contour" == propertyName) {
      params.isContoured = true;
      int n = ((Integer) value).intValue();
      if (n == 0)
        params.nContours = MarchingSquares.defaultContourCount;
      else if (n > 0)
        params.nContours = n;
      else
        params.thisContour = -n;
      return true;
    }

    

    if ("property" == propertyName) {
      params.dataType = Parameters.SURFACE_PROPERTY;
      params.theProperty = (float[]) value;
      mapSurface(null);
      return true;
    }

    
    if ("sphere" == propertyName) {
      params.setSphere(((Float) value).floatValue());
      surfaceReader = new IsoShapeReader(this, params.distance);
      generateSurface();
      return true;
    }

    if ("ellipsoid" == propertyName) {
      if (value instanceof Point4f)
        params.setEllipsoid((Point4f) value);
      else if (value instanceof float[])
        params.setEllipsoid((float[]) value);
      else
        return true;
      surfaceReader = new IsoShapeReader(this, params.distance);
      generateSurface();
      return true;
    }

    if ("ellipsoid3" == propertyName) {
      params.setEllipsoid((float[]) value);
      surfaceReader = new IsoShapeReader(this, params.distance);
      generateSurface();
      return true;
    }

    if ("lp" == propertyName) {
      params.setLp((Point4f) value);
      surfaceReader = new IsoShapeReader(this, 3, 2, 0, 15);
      generateSurface();
      return true;
    }

    if ("rad" == propertyName) {
      params.setRadical((Point4f) value);
      surfaceReader = new IsoShapeReader(this, 3, 2, 0, 15);
      generateSurface();
      return true;
    }

    if ("lobe" == propertyName) {
      params.setLobe((Point4f) value);
      surfaceReader = new IsoShapeReader(this, 3, 2, 0, 15);
      generateSurface();
      return true;
    }

    if ("hydrogenOrbital" == propertyName) {
      if (!params.setAtomicOrbital((float[]) value))
        return true;
      surfaceReader = new IsoShapeReader(this, params.psi_n, params.psi_l,
          params.psi_m, params.psi_Znuc);
      processState();
      return true;
    }

    if ("functionXY" == propertyName) {
      params.setFunctionXY((Vector) value);
      if (params.isContoured)
        volumeData.setPlaneParameters(new Point4f(0, 0, 1, 0)); 
      if (((String) params.functionXYinfo.get(0)).indexOf("_xyz") >= 0)
        getFunctionZfromXY();
      processState();
      return true;
    }

    if ("functionXYZ" == propertyName) {
      params.setFunctionXYZ((Vector) value);
      processState();
      return true;
    }

    if ("lcaoType" == propertyName) {
      params.setLcao((String) value, colorPtr);
      return true;
    }

    if ("lcaoCartoonCenter" == propertyName) {
      if (++params.state != Parameters.STATE_DATA_READ)
        return true;
      if (params.center.x == Float.MAX_VALUE)
        params.center.set((Vector3f) value);
      return false;
    }

    if ("molecular" == propertyName || "solvent" == propertyName
        || "sasurface" == propertyName || "nomap" == propertyName) {
      params.setSolvent(propertyName, ((Float) value).floatValue());
      Logger.info(params.calculationType);
      if (params.state < Parameters.STATE_DATA_READ)
        params.cutoff = 0.0f;
      processState();
      return true;
    }

    if ("moData" == propertyName) {
      params.moData = (Hashtable) value;
      return true;
    }

    if ("mep" == propertyName) {
      params.setMep((float[]) value, rangeDefined); 
      processState();
      return true;
    }

    if ("charges" == propertyName) {
      params.theProperty = (float[]) value;
      return true;
    }

    if ("molecularOrbital" == propertyName) {
      int iMo = ((Integer) value).intValue();
      params.setMO(iMo, rangeDefined);
      Logger.info(params.calculationType);
      processState();
      return true;
    }

    if ("fileType" == propertyName) {
      fileType = (String) value;
      return true;
    }

    if ("fileName" == propertyName) {
      params.fileName = (String) value;
      return true;
    }

    if ("readFile" == propertyName) {
      if ((surfaceReader = setFileData(value)) == null) {
        Logger.error("Could not set the data");
        return true;
      }
      generateSurface();
      return true;
    }

    if ("getSurfaceSets" == propertyName) {
      getSurfaceSets();
      return true;
    }
    
    if ("mapColor" == propertyName) {
      if (value instanceof String && ((String) value).equalsIgnoreCase("sets")) {
        getSurfaceSets();
        params.colorBySets = true;
      } else {
        if ((surfaceReader = setFileData(value)) == null) {
          Logger.error("Could not set the mapping data");
          return true;
        }
      }
      mapSurface(value);
      return true;
    }

    
    return false;
  }

  private void getSurfaceSets() {
    if (meshDataServer == null) {
      meshData.getSurfaceSet();
    } else {
      meshDataServer.fillMeshData(meshData, MeshData.MODE_GET_VERTICES);
      meshData.getSurfaceSet();
      meshDataServer.fillMeshData(meshData, MeshData.MODE_PUT_SETS);
    }
  }

  private void processState() {   
    if (params.state == Parameters.STATE_INITIALIZED && params.thePlane != null)
      params.state++;
    if (params.state >= Parameters.STATE_DATA_READ) {
      mapSurface(null);
    } else {
      generateSurface();
    }
  }
  
  private boolean setReader() {
    if (surfaceReader != null)
      return !surfaceReader.vertexDataOnly;
    switch (params.dataType) {
    case Parameters.SURFACE_NOMAP:
      surfaceReader = new IsoPlaneReader(this);
      break;
    case Parameters.SURFACE_SOLVENT:
    case Parameters.SURFACE_MOLECULAR:
    case Parameters.SURFACE_SASURFACE:
    case Parameters.SURFACE_PROPERTY:
      surfaceReader = new IsoSolventReader(this);
      break;
    case Parameters.SURFACE_MOLECULARORBITAL:
      surfaceReader = new IsoMOReader(this);
      break;
    case Parameters.SURFACE_FUNCTIONXY:
      surfaceReader = new IsoFxyReader(this);
      break;
    case Parameters.SURFACE_FUNCTIONXYZ:
      surfaceReader = new IsoFxyzReader(this);
      break;
    case Parameters.SURFACE_MEP:
      surfaceReader = new IsoMepReader(this);
      break;
    }
    return true;
  }
  
  private void generateSurface() {       
    if (++params.state != Parameters.STATE_DATA_READ)
      return;
    setReader();    
    boolean haveMeshDataServer = (meshDataServer != null);
    if (params.colorBySign)
      params.isBicolorMap = true;
    if (surfaceReader == null) {
      Logger.error("surfaceReader is null for " + params.dataType);
      return;
    }
    if (!surfaceReader.createIsosurface(false)) {
      Logger.error("Could not create isosurface");
      return;
    }
    
    if (params.pocket != null && haveMeshDataServer)
      surfaceReader.selectPocket(!params.pocket.booleanValue());

    if (params.minSet > 0)
      surfaceReader.excludeMinimumSet();

    if (params.maxSet > 0)
      surfaceReader.excludeMaximumSet();

    if (haveMeshDataServer)
      meshDataServer.notifySurfaceGenerationCompleted();
    
    if (jvxlData.jvxlDataIs2dContour) {
      surfaceReader.colorIsosurface();
      params.state = Parameters.STATE_DATA_COLORED;
    }
    if (params.colorBySign || params.isBicolorMap) {
      params.state = Parameters.STATE_DATA_COLORED;
      surfaceReader.applyColorScale();
    }
    surfaceReader.jvxlUpdateInfo();
    setMarchingSquares(surfaceReader.marchingSquares);
    surfaceReader.discardTempData(false);
    params.mappedDataMin = Float.MAX_VALUE;
    if (surfaceReader.hasColorData)
      colorIsosurface();
    surfaceReader = null; 
  }

  private void mapSurface(Object value) {
    if (params.state == Parameters.STATE_INITIALIZED && params.thePlane != null)
      params.state++;
    if (++params.state != Parameters.STATE_DATA_COLORED)
      return;
    if (!setReader())
      return;    
    params.doCapIsosurface = false;
    
      
    if (params.thePlane != null) {
      params.cutoff = 0;
      surfaceReader.createIsosurface(true);
      if (meshDataServer != null)
        meshDataServer.notifySurfaceGenerationCompleted();
      if (params.dataType == Parameters.SURFACE_NOMAP) {
        
        surfaceReader.discardTempData(true);
        return;
      }
      params.mappedDataMin = Float.MAX_VALUE;
      surfaceReader.readVolumeData(true);
    } else if (!params.colorBySets) {
      surfaceReader.readVolumeParameters();
      params.mappedDataMin = Float.MAX_VALUE;
      surfaceReader.readVolumeData(true);
    }
    colorIsosurface();
  }

  void colorIsosurface() {
    surfaceReader.colorIsosurface();
    surfaceReader.jvxlUpdateInfo();
    surfaceReader.updateTriangles();
    surfaceReader.discardTempData(true);
    if (meshDataServer != null)
      meshDataServer.notifySurfaceMappingCompleted();
  }
  
  public Object getProperty(String property, int index) {
    if (property == "jvxlFileData")
      return JvxlReader.jvxlGetFile(meshDataServer, jvxlData, null, params.title, "", true,
          index, null, null);
    if (property == "jvxlFileInfo")
      return jvxlData.jvxlInfoLine; 
    return null;
  }

  private SurfaceReader setFileData(Object value) {
    String fileType = this.fileType;
    this.fileType = null;
    if (value instanceof VolumeData) {
      volumeData = (VolumeData)value;
      return new VolumeDataReader(this);
    }
    if (value instanceof Hashtable) {
      volumeData = (VolumeData)((Hashtable) value).get("volumeData");
      return new VolumeDataReader(this);
    }
    BufferedReader br = (BufferedReader) value;
    if (fileType == null)
      fileType = SurfaceFileReader.determineFileType(br);
    Logger.info("data file type was determined to be " + fileType);
    if (fileType.equals("Jvxl+"))
      return new JvxlReader(this, br);
    if (fileType.equals("Jvxl"))
      return new JvxlReader(this, br);
    if (fileType.equals("Apbs"))
      return new ApbsReader(this, br);
    if (fileType.equals("Cube"))
      return new CubeReader(this, br);
    if (fileType.equals("Jaguar"))
      return new JaguarReader(this, br);
    if (fileType.equals("Xplor"))
      return new XplorReader(this, br);
    if (fileType.equals("PltFormatted"))
      return new PltFormattedReader(this, br);
    if (fileType.startsWith("MRC")) {
      br = null;
      return new MrcBinaryReader(this, params.fileName, fileType.charAt(3) != '\0');
    }
    if (fileType.equals("Efvet"))
      return new EfvetReader(this,br);
    if (fileType.equals("Pmesh"))
      return new PmeshReader(this, params.fileName, br);
    if (fileType.equals("Obj"))
      return new ObjReader(this, params.fileName, br);
    return null;
  }

  void initializeIsosurface() {
    params.initialize();
    colorPtr = 0;
    surfaceReader = null;
    marchingSquares = null;
    initState();
  }

  public void initState() {
    params.state = Parameters.STATE_INITIALIZED;
    params.dataType = params.surfaceType = Parameters.SURFACE_NONE;
  }

  public String setLcao() {
    params.colorPos = params.colorPosLCAO;
    params.colorNeg = params.colorNegLCAO;
    return params.lcaoType;

  }

  
  private void getFunctionZfromXY() {
    Point3f origin = (Point3f) params.functionXYinfo.get(1);
    int[] counts = new int[3];
    int[] nearest = new int[3];
    Vector3f[] vectors = new Vector3f[3];
    for (int i = 0; i < 3; i++) {
      Point4f info = (Point4f) params.functionXYinfo.get(i + 2);
      counts[i] = Math.abs((int) info.x);
      vectors[i] = new Vector3f(info.y, info.z, info.w);
    }
    int nx = counts[0];
    int ny = counts[1];
    Point3f pt = new Point3f();
    Point3f pta = new Point3f();
    Point3f ptb = new Point3f();
    Point3f ptc = new Point3f();

    float[][] data = (float[][]) params.functionXYinfo.get(5);
    float[][] data2 = new float[nx][ny];
    float[] d;
    
    
      
    for (int i = 0; i < nx; i++)
      for (int j = 0; j < ny; j++) {
        pt.scaleAdd(i, vectors[0], origin);
        pt.scaleAdd(j, vectors[1], pt);
        float dist = findNearestThreePoints(pt.x, pt.y, data, nearest);
        pta.set((d = data[nearest[0]])[0], d[1], d[2]);
        if (dist < 0.00001) {
          pt.z = d[2];
        } else {
          ptb.set((d = data[nearest[1]])[0], d[1], d[2]);
          ptc.set((d = data[nearest[2]])[0], d[1], d[2]);
          pt.z = distanceVerticalToPlane(pt.x, pt.y, pta, ptb, ptc);
        }
        data2[i][j] = pt.z;
        
      }
    params.functionXYinfo.setElementAt(data2, 5);
  }

  final Vector3f vAC = new Vector3f();
  final Vector3f vAB = new Vector3f();
  final Vector3f vNorm = new Vector3f();
  final Point3f ptRef = new Point3f(0, 0, 1e15f);
  
  private float distanceVerticalToPlane(float x, float y, Point3f pta,
                                              Point3f ptb, Point3f ptc) {
    

    float d = Graphics3D.getDirectedNormalThroughPoints(pta, ptb, ptc, ptRef, vNorm, vAB, vAC);
    return (vNorm.x * x + vNorm.y * y + d) / -vNorm.z;
  }
  
  private static float findNearestThreePoints(float x, float y, float[][] xyz, int[] result) {
    
    float d, dist1, dist2, dist3;
    int i1, i2, i3;
    i1 = i2 = i3 = -1;
    dist1 = dist2 = dist3 = Float.MAX_VALUE;
    for (int i = xyz.length; --i >= 0;) {
      d = (d = xyz[i][0] - x) * d + (d = xyz[i][1] - y) * d;
      if (d < dist1) {
        dist3 = dist2;
        dist2 = dist1;
        dist1 = d;
        i3 = i2;
        i2 = i1;
        i1 = i;
      } else if (d < dist2) {
        dist3 = dist2;
        dist2 = d;
        i3 = i2;
        i2 = i;
      } else if (d < dist3) {
        dist3 = d;
        i3 = i;
      }
    }
    
    result[0] = i1;
    result[1] = i2;
    result[2] = i3;
    return dist1;
  }
}
