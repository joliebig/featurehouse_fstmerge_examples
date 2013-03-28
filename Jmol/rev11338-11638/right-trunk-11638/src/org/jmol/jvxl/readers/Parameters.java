



package org.jmol.jvxl.readers;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Vector3f;

import java.util.Hashtable;
import java.util.BitSet;
import java.util.Vector;

import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;

public class Parameters {

  public final static int STATE_UNINITIALIZED = 0;
  public final static int STATE_INITIALIZED = 1;
  public final static int STATE_DATA_READ = 2;
  public final static int STATE_DATA_COLORED = 3;

  int state = STATE_UNINITIALIZED;

  boolean logMessages = false;
  boolean logCompression = false;
  boolean logCube = false;
  boolean isSilent = false;
  
  float assocCutoff = 0.3f; 

  final static int NO_ANISOTROPY = 1 << 5;
  final static int IS_SILENT = 1 << 6;
  final public static int IS_SOLVENTTYPE = 1 << 7;
  final static int HAS_MAXGRID = 1 << 8;
  final static int CAN_CONTOUR = 1 << 9;
  
  public int dataType;
  int surfaceType;

  final static int SURFACE_NONE = 0;

  
  final static int SURFACE_SPHERE = 1 | IS_SILENT;
  final static int SURFACE_ELLIPSOID2 = 2 | IS_SILENT;
  final static int SURFACE_ELLIPSOID3 = 3 | IS_SILENT;
  final static int SURFACE_LOBE = 4 | IS_SILENT;
  final static int SURFACE_LCAOCARTOON = 5 | IS_SILENT;
  final static public int SURFACE_LONEPAIR = 6 | IS_SILENT;
  final static public int SURFACE_RADICAL = 7 | IS_SILENT;
  final static int SURFACE_FUNCTIONXY = 8 | CAN_CONTOUR;
  final static int SURFACE_FUNCTIONXYZ = 9;

  
  final static int SURFACE_SOLVENT = 11 | IS_SOLVENTTYPE | NO_ANISOTROPY;
  final static int SURFACE_SASURFACE = 12 | IS_SOLVENTTYPE | NO_ANISOTROPY;
  final static int SURFACE_MOLECULARORBITAL = 13 | NO_ANISOTROPY | HAS_MAXGRID;
  final static int SURFACE_ATOMICORBITAL = 14;
  final static int SURFACE_MEP = 16 | NO_ANISOTROPY | HAS_MAXGRID;
  final static int SURFACE_FILE = 17 | CAN_CONTOUR;
  final static int SURFACE_INFO = 18 | CAN_CONTOUR;
  final static int SURFACE_MOLECULAR = 19 | IS_SOLVENTTYPE | NO_ANISOTROPY;

  

  final static int SURFACE_NOMAP = 20 | IS_SOLVENTTYPE | NO_ANISOTROPY;
  final static int SURFACE_PROPERTY = 21 | IS_SOLVENTTYPE | NO_ANISOTROPY;

  void initialize() {
    addHydrogens = false;
    atomIndex = -1;
    blockCubeData = false; 
    bsIgnore = null;
    bsSelected = null;
    bsSolvent = null;
    calculationType = "";
    center = new Point3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
    cappingPlane= null;
    colorBySign = colorByPhase = colorBySets = false;
    colorNeg = defaultColorNegative;
    colorNegLCAO = defaultColorNegativeLCAO;
    colorPos = defaultColorPositive;
    colorPosLCAO = defaultColorPositiveLCAO;
    cutoff = Float.MAX_VALUE;
    distance = Float.MAX_VALUE;
    doCapIsosurface = false;
    envelopeRadius = 10f;
    fileIndex = 1;
    readAllData = true;
    fileName = "";
    functionXYinfo = null;
    iAddGridPoints = false;
    insideOut = false;
    isAngstroms = false;
    isBicolorMap = isCutoffAbsolute = isPositiveOnly = false;
    isCavity = false;
    isColorReversed = false;
    isSquared = false;
    isContoured = false;
    isEccentric = isAnisotropic = false;
    isSilent = false;
    iUseBitSets = false;
    logCube = logCompression = false;
    logMessages = Logger.debugging;
    mappedDataMin = Float.MAX_VALUE;
    minSet = 0;
    nContours = 0;
    contourIncrements = null;
    contoursDiscrete = null;
    contourColixes = null;
    pocket = null;
    rangeDefined = false;
    resolution = Float.MAX_VALUE;
    scale = Float.NaN;
    solventAtomRadiusAbsolute = 0;
    solventAtomRadiusFactor = 1;
    solventAtomRadiusOffset = 0;
    solventExtendedAtomRadius = 0;
    state = STATE_INITIALIZED;
    thePlane = null;
    theProperty = null;
    thisContour = -1;
    contourFromZero = true;
    title = null;
    useIonic = false;
    rangeAll = false;
  }
  
  String calculationType = "";

  
  boolean addHydrogens;
  float solventRadius;
  float solventExtendedAtomRadius;
  float solventAtomRadiusFactor;
  float solventAtomRadiusAbsolute;
  float solventAtomRadiusOffset;
  boolean propertySmoothing;
  boolean useIonic;
  float envelopeRadius;
  float cavityRadius;
  boolean isCavity;
  Boolean pocket; 
  int minSet;
  Point4f cappingPlane;
  boolean doCapIsosurface;

  float[] theProperty;
  

  float solvent_ptsPerAngstrom = 4f;
  int solvent_gridMax = 60;


  
  
  final static float ANGSTROMS_PER_BOHR = JmolConstants.ANGSTROMS_PER_BOHR;
  final static int defaultEdgeFractionBase = 35; 
  final static int defaultEdgeFractionRange = 90;
  final static int defaultColorFractionBase = 35;
  final static int defaultColorFractionRange = 90;
  final static float defaultMappedDataMin = 0f;
  final static float defaultMappedDataMax = 1.0f;
  final static float defaultCutoff = 0.02f;
  final static float defaultOrbitalCutoff = 0.14f;
  public final static float defaultQMOrbitalCutoff = 0.050f; 
  final static float defaultQMElectronDensityCutoff = 0.010f;
  final static int defaultContourCount = 11; 
  final static int nContourMax = 100;
  final static int defaultColorNegative = 0xFFFF0000; 
  final static int defaultColorPositive =  0xFF0000FF; 
  final static int defaultColorNegativeLCAO = 0xFF800080; 
  final static int defaultColorPositiveLCAO = 0xFFFFA500; 
  final static float defaultSolventRadius = 1.2f;
  final static float defaultMepCutoff = 0.05f;
  final static float defaultMepMin = -0.05f;
  final static float defaultMepMax = 0.05f;

  
  
  boolean colorBySign;
  boolean colorByPhase;
  boolean colorBySets;
  int colorNeg;
  int colorPos;
  int colorPosLCAO;
  int colorNegLCAO;
  int colorPhase;

   
  
  boolean iAddGridPoints;
  boolean remappable;
  
  
  
  int atomIndex; 
  
  boolean isAngstroms;
  float scale;
  
  float[] anisotropy = new float[3];
  boolean isAnisotropic;

  void setAnisotropy(Point3f pt) { 
      anisotropy[0] = pt.x;
      anisotropy[1] = pt.y;
      anisotropy[2] = pt.z;
      isAnisotropic = true;
  }
  
  Matrix3f eccentricityMatrix;
  Matrix3f eccentricityMatrixInverse;
  boolean isEccentric;
  float eccentricityScale;
  float eccentricityRatio;
  float[] aniosU;

  void setEccentricity(Point4f info) {
    
    Vector3f ecc = new Vector3f(info.x, info.y, info.z);
    float c = (scale > 0 ? scale : info.w < 0 ? 1f : ecc.length());
    float fab_c = Math.abs(info.w);
    ecc.normalize();
    Vector3f z = new Vector3f(0, 0, 1);
    ecc.add(z);
    ecc.normalize();
    if (Float.isNaN(ecc.x)) 
      ecc.set(1, 0, 0);
    eccentricityMatrix = new Matrix3f();
    eccentricityMatrix.setIdentity();
    eccentricityMatrix.set(new AxisAngle4f(ecc, (float) Math.PI));
    eccentricityMatrixInverse = new Matrix3f();
    eccentricityMatrixInverse.invert(eccentricityMatrix);
    isEccentric = isAnisotropic = true;
    eccentricityScale = c;
    eccentricityRatio = fab_c;
    if (fab_c > 1)
      eccentricityScale *= fab_c;
    anisotropy[0] = fab_c * c;
    anisotropy[1] = fab_c * c;
    anisotropy[2] = c;
    if (center.x == Float.MAX_VALUE)
      center.set(0, 0, 0);
  }

  void setPlane(Point4f plane) {
    thePlane = plane;
    if (thePlane.x == 0 && thePlane.y == 0
        && thePlane.z == 0)
      thePlane.z = 1; 
    isContoured = true;
  }

  void setSphere(float radius) {
    dataType = SURFACE_SPHERE;
    distance = radius;
    setEccentricity(new Point4f(0, 0, 1, 1));
    cutoff = Float.MIN_VALUE;
    isCutoffAbsolute = false;
    isSilent = !logMessages;
    
      
  }
  
  void setEllipsoid(Point4f v) {
    dataType = SURFACE_ELLIPSOID2;
    distance = 1f;
    setEccentricity(v);
    cutoff = Float.MIN_VALUE;
    isCutoffAbsolute = false;
    isSilent = !logMessages;
    
      
        
  }

  float[] anisoB;
  public void setEllipsoid(float[] bList) {
    anisoB = bList;
    for (int i = 0; i < 6; i++)System.out.print(bList[i] + " ");System.out.println( " in Parameters setEllipsoid" + center);
    dataType = SURFACE_ELLIPSOID3;
    distance = 0.3f * (Float.isNaN(scale) ? 1f : scale);
    cutoff = Float.MIN_VALUE;
    isCutoffAbsolute = false;
    isSilent = !logMessages;
    if (center.x == Float.MAX_VALUE)
      center.set(0, 0, 0);
    if (resolution == Float.MAX_VALUE)
      resolution = 6;
    
      
        
  }

  void setLobe(Point4f v) {
    dataType = SURFACE_LOBE;
    setEccentricity(v);
    if (cutoff == Float.MAX_VALUE) {
      cutoff = defaultOrbitalCutoff;
      if (isSquared)
        cutoff = cutoff * cutoff;
    }
    isSilent = !logMessages;
    script = " center " + Escape.escape(center)
        + (Float.isNaN(scale) ? "" : " scale " + scale) + " LOBE {" + v.x + " "
        + v.y + " " + v.z + " " + v.w + "};";
  }
  
  void setLp(Point4f v) {
    dataType = SURFACE_LONEPAIR;
    setEccentricity(v);
    if (cutoff == Float.MAX_VALUE) {
      cutoff = defaultOrbitalCutoff;
      if (isSquared)
        cutoff = cutoff * cutoff;
    }
    isSilent = !logMessages;
    script = " center " + Escape.escape(center)
        + (Float.isNaN(scale) ? "" : " scale " + scale) + " LP {" + v.x + " "
        + v.y + " " + v.z + " " + v.w + "};";
  }
  
  void setRadical(Point4f v) {
    dataType = SURFACE_RADICAL;
    setEccentricity(v);
    if (cutoff == Float.MAX_VALUE) {
      cutoff = defaultOrbitalCutoff;
      if (isSquared)
        cutoff = cutoff * cutoff;
    }
    isSilent = !logMessages;
    script = " center " + Escape.escape(center)
        + (Float.isNaN(scale) ? "" : " scale " + scale) + " RAD {" + v.x + " "
        + v.y + " " + v.z + " " + v.w + "};";
  }
  
  String lcaoType;

  void setLcao(String type, int colorPtr) {
    lcaoType = type;
    if (colorPtr == 1)
      colorPosLCAO = colorNegLCAO;
    isSilent = !logMessages;
  }
    
  void setRadius(boolean useIonic, float radius) {
    this.useIonic = useIonic;
    if (radius >= 100)
      solventAtomRadiusFactor = (radius - 100) / 100;
    else if (radius > 10)
      solventAtomRadiusAbsolute = radius - 10;
    else
      solventAtomRadiusOffset = radius;
  }

  void setSolvent(String propertyName, float radius) {
    isEccentric = isAnisotropic = false;
    
    solventRadius = radius;
    if (solventRadius < 0)
      solventRadius = defaultSolventRadius;
    dataType = ("nomap" == propertyName ? SURFACE_NOMAP
        : "molecular" == propertyName ? SURFACE_MOLECULAR
            : "sasurface" == propertyName || solventRadius == 0f ? SURFACE_SASURFACE
                : SURFACE_SOLVENT);

    switch (dataType) {
    case Parameters.SURFACE_NOMAP:
      calculationType = "unmapped plane";
      break;
    case Parameters.SURFACE_MOLECULAR:
      calculationType = "molecular surface with radius " + solventRadius;
      break;
    case Parameters.SURFACE_SOLVENT:
      calculationType = "solvent-excluded surface with radius " + solventRadius;
      break;
    case Parameters.SURFACE_SASURFACE:
      calculationType = "solvent-accessible surface with radius "
          + solventRadius;
      break;
    }

    switch (dataType) {
    case SURFACE_NOMAP:
      solventExtendedAtomRadius = solventRadius;
      solventRadius = 0f;
      isContoured = false;
      break;
    case SURFACE_MOLECULAR:
      solventExtendedAtomRadius = 0f;
      break;
    case SURFACE_SOLVENT:
      solventExtendedAtomRadius = 0f;
      if (bsIgnore == null)
        bsIgnore = bsSolvent;
      break;
    case SURFACE_SASURFACE:
      solventExtendedAtomRadius = solventRadius;
      solventRadius = 0f;
      if (bsIgnore == null)
        bsIgnore = bsSolvent;
      break;
    }
  }
  
  public Vector functionXYinfo;
  
  void setFunctionXY(Vector value) {
    dataType = SURFACE_FUNCTIONXY;
    functionXYinfo = (Vector) value;
    cutoff = Float.MIN_VALUE;
    isEccentric = isAnisotropic = false;
  }

  void setFunctionXYZ(Vector value) {
    dataType = SURFACE_FUNCTIONXYZ;
    functionXYinfo = (Vector) value;
    cutoff = Float.MIN_VALUE;
    isEccentric = isAnisotropic = false;
  }

  int psi_n = 2;
  int psi_l = 1;
  int psi_m = 1;
  float psi_Znuc = 1; 
  float psi_ptsPerAngstrom = 5f;

  boolean setAtomicOrbital(float[] nlmZ) {
    dataType = SURFACE_ATOMICORBITAL;
    psi_n = (int) nlmZ[0];
    psi_l = (int) nlmZ[1];
    psi_m = (int) nlmZ[2];
    psi_Znuc = nlmZ[3];
    psi_ptsPerAngstrom = 10;
    
    if (cutoff == Float.MAX_VALUE) {
      cutoff = defaultOrbitalCutoff;
      if (isSquared)
        cutoff = cutoff * cutoff;
    }
    isCutoffAbsolute = true;
    if (state < STATE_DATA_READ && thePlane == null) {
      if (colorBySign) {
        isBicolorMap = true;
      }
      if (resolution == Float.MAX_VALUE)
        resolution = 6;
    }
    return (psi_Znuc > 0 && Math.abs(psi_m) <= psi_l && psi_l < psi_n);
  }  
  
 
  public final static int MEP_MAX_GRID = 40;
  int mep_gridMax = MEP_MAX_GRID;
  float mep_ptsPerAngstrom = 3f;
  float mep_marginAngstroms = 1f; 

  void setMep(float[] charges, boolean isRangeDefined) {
    dataType = SURFACE_MEP;
    theProperty = charges;
    isEccentric = isAnisotropic = false;
    if (cutoff == Float.MAX_VALUE) {
      cutoff = defaultMepCutoff;
      if (isSquared)
        cutoff = cutoff * cutoff;
    }
    isCutoffAbsolute = (cutoff > 0 && !isPositiveOnly);
    contourFromZero = false; 
    
    
    if (state >= STATE_DATA_READ || thePlane != null) {
      if (!rangeDefined && !rangeAll) {
        valueMappedToRed = defaultMepMin;
        valueMappedToBlue = defaultMepMax;
        rangeDefined = true;
      }
    } else {
      colorBySign = true;
      
      
      isBicolorMap = true;
    }
  }
  
  int qmOrbitalType;
  int qmOrbitalCount;
  
  final static int QM_TYPE_UNKNOWN = 0;
  final static int QM_TYPE_GAUSSIAN = 1;
  final static int QM_TYPE_SLATER = 2;
  
  Hashtable moData, mo;
  float[] moCoefficients;
  public final static int MO_MAX_GRID = 80;
  int qm_gridMax = MO_MAX_GRID;
  float qm_ptsPerAngstrom = 10f;
  float qm_marginAngstroms = 1f; 
  int qm_nAtoms;
  int qm_moNumber = Integer.MAX_VALUE;
  
  void setMO(int iMo, boolean isRangeDefined) {
    iUseBitSets = true;
    qm_moNumber = Math.abs(iMo);
    qmOrbitalType = (moData.containsKey("gaussians") ? QM_TYPE_GAUSSIAN
        : moData.containsKey("slaterInfo") ? QM_TYPE_SLATER : QM_TYPE_UNKNOWN);
    boolean isElectronDensity = (iMo <= 0);
    if (qmOrbitalType == QM_TYPE_UNKNOWN) {
      
      Logger
          .error("MO ERROR: No basis functions found in file for MO calculation. (GAUSSIAN 'gfprint' keyword may be missing?)");
      mo = null;
      title = new String[] {"no basis functions found in file"};
    } else {
      Vector mos = (Vector) (moData.get("mos"));
      qmOrbitalCount = mos.size();
      calculationType = (String) moData.get("calculationType");
      calculationType = "Molecular orbital #" + qm_moNumber + "/"
          + qmOrbitalCount + " "
          + (calculationType == null ? "" : calculationType);
      if (!isElectronDensity) {
        
        
        if (title == null) {
          title = new String[5];
          title[0] = "%F";
          title[1] = "Model %M  MO %I/%N %T";
          title[2] = "Energy = %E %U";
          title[3] = "?Symmetry = %S";
          title[4] = "?Occupancy = %O";
        }
        mo = (Hashtable) mos.get(qm_moNumber - 1);
        moCoefficients = (float[]) mo.get("coefficients");
      }
    }
    dataType = SURFACE_MOLECULARORBITAL;
    
    
    if (cutoff == Float.MAX_VALUE) {
      cutoff = (isElectronDensity ? defaultQMElectronDensityCutoff
          : defaultQMOrbitalCutoff);
      if (isSquared)
        cutoff = cutoff * cutoff;
    }
    isEccentric = isAnisotropic = false;
    isCutoffAbsolute = (cutoff > 0 && !isPositiveOnly);
    if (state >= STATE_DATA_READ || thePlane != null)
      return;
    colorBySign = true;
    if (colorByPhase && colorPhase == 0)
      colorByPhase = false;
    isBicolorMap = true;
  }
  
  Point3f center, point;
  float distance;
  
  String script;
  
  BitSet bsSelected;
  BitSet bsIgnore;
  BitSet bsSolvent;
  
  boolean iUseBitSets = false;
  
  String[] title;
  boolean blockCubeData;
  boolean readAllData;
  int fileIndex; 
  String fileName;
  int modelIndex; 
  boolean isXLowToHigh;
  
  boolean insideOut;
  float cutoff = Float.MAX_VALUE;
  boolean isCutoffAbsolute;
  boolean isPositiveOnly;
  
  boolean rangeAll;
  public boolean rangeDefined;
  float valueMappedToRed, valueMappedToBlue;
  float mappedDataMin;
  float mappedDataMax;
  boolean isColorReversed;
  boolean isBicolorMap;
  boolean isSquared;

  Point4f thePlane;
  boolean isContoured;
  
  int nContours;
  int thisContour; 
  boolean contourFromZero;
 
  float resolution;
  int downsampleFactor;
  int maxSet;
  public float[] contoursDiscrete;
  public short[] contourColixes;
  Point3f contourIncrements;
  
  void setMapRanges(SurfaceReader surfaceReader) {
    if (colorByPhase || colorBySign || (thePlane != null || isBicolorMap) && !isContoured) {
      mappedDataMin = -1;
      mappedDataMax = 1;
    }
    if (mappedDataMin == Float.MAX_VALUE || mappedDataMin == mappedDataMax) {
      mappedDataMin = surfaceReader.getMinMappedValue();
      mappedDataMax = surfaceReader.getMaxMappedValue();
    }
    if (mappedDataMin == 0 && mappedDataMax == 0) {
      
      mappedDataMin = -1;
      mappedDataMax = 1;
    }

    if (!rangeDefined) {
      valueMappedToRed = mappedDataMin;
      valueMappedToBlue = mappedDataMax;
    }
  }

}
