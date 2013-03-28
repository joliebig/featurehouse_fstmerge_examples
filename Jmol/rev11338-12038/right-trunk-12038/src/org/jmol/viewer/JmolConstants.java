
package org.jmol.viewer;

import org.jmol.script.Token;
import org.jmol.util.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.BitSet;
import java.util.Properties;

import javax.vecmath.Vector3f;

final public class JmolConstants {

  public final static String copyright = "(C) 2009 Jmol Development";
  public final static String version;

  static {
    String tmpVersion = null;
    Properties props = new Properties();

    
    if (tmpVersion == null) {
      BufferedInputStream bis = null;
      InputStream is = null;
      try {
        is = JmolConstants.class.getClassLoader().getResourceAsStream("org/jmol/viewer/Jmol.properties");        
        bis = new BufferedInputStream(is);
        props.load(bis);
        tmpVersion = props.getProperty("version", tmpVersion);
      } catch (IOException e) {
        
      } finally {
        if (bis != null) {
          try {
            bis.close();
          } catch (IOException e) {
            
          }
        }
        if (is != null) {
          try {
            is.close();
          } catch (IOException e) {
            
          }
        }
      }
    }
    version = (tmpVersion != null ? tmpVersion : "(Unknown version)");
  }
    
  public final static String cvsDate = "$Date: 2009-12-21 13:51:06 +0100 (Mo, 21. Dez 2009) $"; 
  public final static String date = cvsDate.substring(7, 23);
    
  public final static boolean officialRelease = false;

  public final static String CLASSBASE_OPTIONS = "org.jmol.";

  public final static String DEFAULT_HELP_PATH = "http://chemapps.stolaf.edu/jmol/docs/index.htm";

  public final static String EMBEDDED_SCRIPT_TAG = "**** Jmol Embedded Script ****";

  public static String embedScript(String s) {
    return "\n/**" + EMBEDDED_SCRIPT_TAG + " \n" + s + "\n**/";
  }
  
  public static final String SCRIPT_EDITOR_IGNORE = "\0## EDITOR_IGNORE ##";

  public final static int CALLBACK_ANIMFRAME = 0;
  public final static int CALLBACK_ECHO = 1;
  public final static int CALLBACK_ERROR = 2;
  public final static int CALLBACK_EVAL = 3;
  public final static int CALLBACK_HOVER = 4;
  public final static int CALLBACK_LOADSTRUCT = 5;
  public final static int CALLBACK_MEASURE = 6;
  public final static int CALLBACK_MESSAGE = 7;
  public final static int CALLBACK_MINIMIZATION = 8;
  public final static int CALLBACK_PICK = 9;
  public final static int CALLBACK_RESIZE = 10;
  public final static int CALLBACK_SCRIPT = 11;
  public final static int CALLBACK_SYNC = 12;
  public final static int CALLBACK_CLICK = 13;
  public final static int CALLBACK_COUNT = 14;

  private final static String[] callbackNames = {
    "animFrameCallback",
    "echoCallback",
    "errorCallback",
    "evalCallback",
    "hoverCallback", 
    "loadStructCallback", 
    "measureCallback",
    "messageCallback", 
    "minimizationCallback", 
    "pickCallback", 
    "resizeCallback", 
    "scriptCallback",
    "syncCallback", 
    "clickCallback"
  };
  
  public static String getCallbackName(int i) {
    if (i < 0) {
      StringBuffer s = new StringBuffer();
      for (int c = 0; c < callbackNames.length; c++)
        s.append(callbackNames[c].toLowerCase()).append(";");
      return s.toString();
    }    
    return (i >= callbackNames.length ? null : callbackNames[i]);
  }
 
  public static int getCallbackId(String callbackName) {
    for (int i = 0; i < CALLBACK_COUNT; i++)
      if (getCallbackName(i).equalsIgnoreCase(callbackName)) 
        return i;
    return -1;
  }

  
  
  public final static int INFO_A = 0;
  public final static int INFO_B = 1;
  public final static int INFO_C = 2;
  public final static int INFO_ALPHA = 3;
  public final static int INFO_BETA = 4;
  public final static int INFO_GAMMA = 5;

  
  
  public final static int JMOL_DATA_RAMACHANDRAN = 0;
  public final static int JMOL_DATA_QUATERNION = 1;
  public final static int JMOL_DATA_OTHER = 2;
  

  
  
  public static final String EXPORT_DRIVER_LIST = "Idtf;Maya;Povray;Vrml;X3d;Tachyon"; 

  public final static int DRAW_MULTIPLE = -1;
  public final static int DRAW_NONE = 0;
  
  public final static int DRAW_POINT = 1;
  public final static int DRAW_LINE = 2;
  public final static int DRAW_TRIANGLE = 3;
  public final static int DRAW_PLANE = 4;
  public static final int DRAW_CYLINDER = 5;
  
  public final static int DRAW_ARROW = 15;
  public final static int DRAW_CIRCLE = 16;
  public final static int DRAW_CURVE = 17;
  public static final int DRAW_CIRCULARPLANE = 18;
  public final static int DRAW_ARC = 19;
  public final static int DRAW_LINE_SEGMENT = 20;
  
  public static String getDrawTypeName(int drawType) {
    switch (drawType) {
    case JmolConstants.DRAW_MULTIPLE:
      return "multiple";
    case JmolConstants.DRAW_POINT:
      return "point";
    case JmolConstants.DRAW_LINE:
      return "line";
    case JmolConstants.DRAW_CYLINDER:
      return "cylinder";
    case JmolConstants.DRAW_TRIANGLE:
      return "triangle";
    case JmolConstants.DRAW_PLANE:
      return "plane";
    case JmolConstants.DRAW_ARROW:
      return "arrow";
    case JmolConstants.DRAW_ARC:
      return "arc";
    case JmolConstants.DRAW_CIRCLE:
      return "circle";
    case JmolConstants.DRAW_CIRCULARPLANE:
      return "circularPlane";
    case JmolConstants.DRAW_CURVE:
      return "curve";
    }
    return "drawObject";
}

  public final static Vector3f center = new Vector3f(0, 0, 0);
  public final static Vector3f axisX = new Vector3f(1, 0, 0);
  public final static Vector3f axisY = new Vector3f(0, 1, 0);
  public final static Vector3f axisZ = new Vector3f(0, 0, 1);
  public final static Vector3f axisNX = new Vector3f(-1, 0, 0);
  public final static Vector3f axisNY = new Vector3f(0, -1, 0);
  public final static Vector3f axisNZ = new Vector3f(0, 0, -1);
  public final static Vector3f[] unitAxisVectors = {
    axisX, axisY, axisZ, axisNX, axisNY, axisNZ };

  public final static int XY_ZTOP = 100; 
  public final static int DEFAULT_PERCENT_VDW_ATOM = 20;
  public final static float DEFAULT_BOND_RADIUS = 0.15f;
  public final static short DEFAULT_BOND_MILLIANGSTROM_RADIUS = (short) (DEFAULT_BOND_RADIUS * 1000);
  
  public final static float DEFAULT_BOND_TOLERANCE = 0.45f;
  
  public final static float DEFAULT_MIN_BOND_DISTANCE = 0.4f;


  public final static int CONNECT_DELETE_BONDS     = 0;
  public final static int CONNECT_MODIFY_ONLY      = Token.modify;
  public final static int CONNECT_CREATE_ONLY      = Token.create;
  public final static int CONNECT_MODIFY_OR_CREATE = Token.modifyorcreate;
  public final static int CONNECT_AUTO_BOND        = Token.auto;
  public final static int CONNECT_IDENTIFY_ONLY    = 5;
  public final static float DEFAULT_MAX_CONNECT_DISTANCE = 100000000f;
  public final static float DEFAULT_MIN_CONNECT_DISTANCE = 0.1f;
  
  private final static String[] connectOperationStrings =
  { "delete", "modify", "create", "modifyOrCreate", "auto", "adjust" };

  public static String connectOperationName(int i) {
    return connectOperationStrings[i];
  }
  
  public static final int MOUSE_NONE = -1;
  public static final int MOUSE_ROTATE = 0;
  public static final int MOUSE_ZOOM = 1;
  public static final int MOUSE_XLATE = 2;
  public static final int MOUSE_PICK = 3;
  public static final int MOUSE_DELETE = 4;
  public static final int MOUSE_MEASURE = 5;
  public static final int MOUSE_ROTATE_Z = 6;
  public static final int MOUSE_SLAB_PLANE = 7;
  public static final int MOUSE_POPUP_MENU = 8;

  public final static byte MULTIBOND_NEVER =     0;
  public final static byte MULTIBOND_WIREFRAME = 1;
  public final static byte MULTIBOND_NOTSMALL =  2;
  public final static byte MULTIBOND_ALWAYS =    3;

  public final static short madMultipleBondSmallMaximum = 500;

  

  public final static int AXES_MODE_BOUNDBOX = 0;
  public final static int AXES_MODE_MOLECULAR = 1;
  public final static int AXES_MODE_UNITCELL = 2;

  
  
  public final static int PICKING_OFF    = 0;
  public final static int PICKING_IDENT  = 1;
  public final static int PICKING_LABEL  = 2;
  public final static int PICKING_CENTER = 3;
  public final static int PICKING_DRAW   = 4;
  public final static int PICKING_SPIN   = 5;
  
  
  public final static int PICKING_SELECT_ATOM      =  8;
  public final static int PICKING_SELECT_GROUP     =  9;
  public final static int PICKING_SELECT_CHAIN     = 10;
  public final static int PICKING_SELECT_MOLECULE  = 11;
  public final static int PICKING_SELECT_SITE      = 12;
  public final static int PICKING_SELECT_MODEL     = 13;
  public final static int PICKING_SELECT_ELEMENT   = 14;
  public final static int PICKING_MEASURE          = 15;
  public final static int PICKING_MEASURE_DISTANCE = 16;
  public final static int PICKING_MEASURE_ANGLE    = 17;
  public final static int PICKING_MEASURE_TORSION  = 18;
  public final static int PICKING_NAVIGATE         = 19;

  private final static String[] pickingModeNames = {
    "off", "identify", "label", "center", "draw", "spin",
    "coord NOT IMPLEMENTED", "bond NOT IMPLEMENTED", 
    "atom", "group", "chain", "molecule", "site", "model", "element", 
    "measure", "distance", "angle", "torsion", "navigate"
  };
 
  public final static String getPickingModeName(int pickingMode) {
    return (pickingMode < 0 || pickingMode >= pickingModeNames.length ? "off"
        : pickingModeNames[pickingMode]);
  }
  
  public final static int getPickingMode(String str) {
    for (int i = pickingModeNames.length; --i >= 0; )
      if (str.equalsIgnoreCase(pickingModeNames[i]))
        return i;
    return -1;
  }
  
  public final static int PICKINGSTYLE_SELECT_JMOL = 0;
  public final static int PICKINGSTYLE_SELECT_CHIME = 0;
  public final static int PICKINGSTYLE_SELECT_RASMOL = 1;
  public final static int PICKINGSTYLE_SELECT_PFAAT = 2;
  public final static int PICKINGSTYLE_SELECT_DRAG = 3;
  public final static int PICKINGSTYLE_MEASURE_ON = 4;
  public final static int PICKINGSTYLE_MEASURE_OFF = 5;
  
  private final static String[] pickingStyleNames = {
    "toggle", "selectOrToggle", "extendedSelect", "drag",
    "measure", "measureoff"
  };

  public final static String getPickingStyleName(int pickingStyle) {
    return (pickingStyle < 0 || pickingStyle >= pickingStyleNames.length ? "toggle"
        : pickingStyleNames[pickingStyle]);
  }
  
  public final static int getPickingStyle(String str) {
    for (int i = pickingStyleNames.length; --i >= 0; )
      if (str.equalsIgnoreCase(pickingStyleNames[i]))
        return i;
    return -1;
  }

  

  
  
  
  
  
  
  
  
  
  
  
  
  public final static short BOND_ORDER_ANY     = 0x3FFF;
  public final static short BOND_ORDER_NULL    = 0x7FFF;

  public final static short BOND_HBOND_SHIFT   = 11;
  public final static short BOND_NEW  = (short) (1 << 15);
  public final static short BOND_HYDROGEN_MASK = 0xF << 11;
  public final static short BOND_H_REGULAR     = 1 << 11;
  public final static short BOND_H_CALC_MASK   = 0xE << 11; 
  public final static short BOND_H_CALC        = 2 << 11;
  public final static short BOND_H_PLUS_2      = 3 << 11;
  public final static short BOND_H_PLUS_3      = 4 << 11;
  public final static short BOND_H_PLUS_4      = 5 << 11;
  public final static short BOND_H_PLUS_5      = 6 << 11;
  public final static short BOND_H_MINUS_3     = 7 << 11;
  public final static short BOND_H_MINUS_4     = 8 << 11;
  public final static short BOND_H_NUCLEOTIDE  = 9 << 11;
  
  public final static int[] argbsHbondType =
  {
    0xFFFF69B4, 
    0xFFFFFF00, 
    0xFFFFFF00, 
    0xFFFFFFFF, 
    0xFFFF00FF, 
    0xFFFF0000, 
    0xFFFFA500, 
    0xFF00FFFF, 
    0xFF00FF00, 
    0xFFFF8080, 
  };

  public static int getArgbHbondType(short order) {
    int argbIndex = ((order & BOND_HYDROGEN_MASK) >> BOND_HBOND_SHIFT);
    return argbsHbondType[argbIndex];
  }

  public final static short BOND_STEREO_MASK   = 0x400; 
  public final static short BOND_STEREO_NEAR   = 0x401;
  public final static short BOND_STEREO_FAR    = 0x402;

  public final static short BOND_AROMATIC_MASK   = 0x200; 
  public final static short BOND_AROMATIC_SINGLE = 0x201; 
  public final static short BOND_AROMATIC_DOUBLE = 0x202; 
  public final static short BOND_AROMATIC        = 0x203; 

  public final static short BOND_SULFUR_MASK   = 0x100; 

  public final static short BOND_PARTIAL_MASK  = 0xE0;  
  public final static short BOND_PARTIAL01     = 0x21;
  public final static short BOND_PARTIAL12     = 0x42;
  public final static short BOND_PARTIAL23     = 0x61;
  public final static short BOND_PARTIAL32     = 0x64;
  
  public final static short BOND_COVALENT_MASK = 0x3FF; 
  public final static short BOND_COVALENT_SINGLE = 1;   
  public final static short BOND_COVALENT_DOUBLE = 2;   
  public final static short BOND_COVALENT_TRIPLE = 3;   
  public final static short BOND_COVALENT_QUADRUPLE = 4;
  public final static short BOND_ORDER_UNSPECIFIED = 7;
  
  private final static String[] bondOrderNames = {
    "single", "double", "triple", "quadruple", 
    "aromatic", 
    "hbond", "partial", "partialDouble",
    "partialTriple", "partialTriple2", 
    "aromaticSingle", "aromaticDouble",
    "unspecified"
  };

  private final static String[] bondOrderNumbers = {
    "1", "2", "3", "4", 
    "1.5", 
    "1", "0.5", "1.5", 
    "2.5", "2.5", 
    "1", "2", 
    "1"
  };

  private final static short[] bondOrderValues = {
    BOND_COVALENT_SINGLE, BOND_COVALENT_DOUBLE, BOND_COVALENT_TRIPLE, BOND_COVALENT_QUADRUPLE,
    BOND_AROMATIC, 
    BOND_H_REGULAR, BOND_PARTIAL01, BOND_PARTIAL12, 
    BOND_PARTIAL23, BOND_PARTIAL32, 
    BOND_AROMATIC_SINGLE, BOND_AROMATIC_DOUBLE,
    BOND_ORDER_UNSPECIFIED
  };

  public final static short getBondOrderFromString(String bondOrderString) {
    for (int i = 0; i < bondOrderNumbers.length; i++) {
      if (bondOrderNames[i].equalsIgnoreCase(bondOrderString))
        return bondOrderValues[i];
    }
    if (bondOrderString.toLowerCase().indexOf("partial ") == 0)
      return getPartialBondOrderFromInteger(modelValue(bondOrderString.substring(8).trim()));
    return BOND_ORDER_NULL;
  }
  
  
  public final static short getPartialBondOrderFromInteger(int bondOrderInteger) {
    return (short) ((((bondOrderInteger / 1000000) % 6) << 5)
    + ((bondOrderInteger % 1000000) & 0x1F));
  }

  public final static short getPartialBondOrder(int order) {
    return (short) ((order & ~BOND_NEW) >> 5);
  }
  
  public final static int getPartialBondDotted(int order) {
    return (order & 0x1F);
  }
  
  public final static short getBondOrderFromFloat(float fOrder) {
    for (int i = 0; i < bondOrderNumbers.length; i++) {
      if (Float.valueOf(bondOrderNumbers[i]).floatValue() == Math.abs(fOrder)) {
        if (fOrder > 0)
          return bondOrderValues[i];
        fOrder = -fOrder;
      }
    }
    return BOND_ORDER_NULL;
  }
  
  public final static String getBondOrderNameFromOrder(short order) {
    order &= ~BOND_NEW;
    switch (order) {
    case BOND_ORDER_ANY:
    case BOND_ORDER_NULL:
      return "";
    case BOND_COVALENT_SINGLE:
      return "single";
    case BOND_COVALENT_DOUBLE:
      return "double";
    }
    if ((order & BOND_PARTIAL_MASK) != 0)
      return "partial " + getBondOrderNumberFromOrder(order);
    if ((order & BOND_HYDROGEN_MASK) != 0)
      return "hbond";
    if ((order & BOND_SULFUR_MASK) != 0)
      return "single";
    for (int i = bondOrderValues.length; --i >= 0;) {
      if (bondOrderValues[i] == order)
        return bondOrderNames[i];
    }
    return "?";
  }

  
  public final static String getBondOrderNumberFromOrder(short order) {
    order &= ~BOND_NEW;
    if (order == BOND_ORDER_NULL || order == BOND_ORDER_ANY)
      return "0"; 
    if ((order & BOND_HYDROGEN_MASK) != 0)
      return "1";
    if ((order & BOND_SULFUR_MASK) != 0)
      return "1";
    if ((order & BOND_PARTIAL_MASK) != 0)
      return (order >> 5) + "." + (order & 0x1F);
    for (int i = bondOrderValues.length; --i >= 0; ) {
      if (bondOrderValues[i] == order)
        return bondOrderNumbers[i];
    }
    return "?";
  }

  
  public final static float ANGSTROMS_PER_BOHR = 0.5291772f;

  
  
  public final static int FRONTLIT = Token.frontlit;
  public final static int BACKLIT = Token.backlit;
  public final static int FULLYLIT = Token.fullylit;

  
  private final static String[] elementSymbols = {
    "Xx", 
    "H",  
    "He", 
    "Li", 
    "Be", 
    "B",  
    "C",  
    "N",  
    "O",  
    "F",  
    "Ne", 
    "Na", 
    "Mg", 
    "Al", 
    "Si", 
    "P",  
    "S",  
    "Cl", 
    "Ar", 
    "K",  
    "Ca", 
    "Sc", 
    "Ti", 
    "V",  
    "Cr", 
    "Mn", 
    "Fe", 
    "Co", 
    "Ni", 
    "Cu", 
    "Zn", 
    "Ga", 
    "Ge", 
    "As", 
    "Se", 
    "Br", 
    "Kr", 
    "Rb", 
    "Sr", 
    "Y",  
    "Zr", 
    "Nb", 
    "Mo", 
    "Tc", 
    "Ru", 
    "Rh", 
    "Pd", 
    "Ag", 
    "Cd", 
    "In", 
    "Sn", 
    "Sb", 
    "Te", 
    "I",  
    "Xe", 
    "Cs", 
    "Ba", 
    "La", 
    "Ce", 
    "Pr", 
    "Nd", 
    "Pm", 
    "Sm", 
    "Eu", 
    "Gd", 
    "Tb", 
    "Dy", 
    "Ho", 
    "Er", 
    "Tm", 
    "Yb", 
    "Lu", 
    "Hf", 
    "Ta", 
    "W",  
    "Re", 
    "Os", 
    "Ir", 
    "Pt", 
    "Au", 
    "Hg", 
    "Tl", 
    "Pb", 
    "Bi", 
    "Po", 
    "At", 
    "Rn", 
    "Fr", 
    "Ra", 
    "Ac", 
    "Th", 
    "Pa", 
    "U",  
    "Np", 
    "Pu", 
    "Am", 
    "Cm", 
    "Bk", 
    "Cf", 
    "Es", 
    "Fm", 
    "Md", 
    "No", 
    "Lr", 
    "Rf", 
    "Db", 
    "Sg", 
    "Bh", 
    "Hs", 
    "Mt", 
    
  };

  
  public final static int elementNumberMax = elementSymbols.length;

  private static Hashtable htElementMap;

  
  public final static short elementNumberFromSymbol(String elementSymbol) {
    if (htElementMap == null) {
      Hashtable map = new Hashtable();
      for (int elementNumber = elementNumberMax; --elementNumber >= 0;) {
        String symbol = elementSymbols[elementNumber];
        Integer boxed = new Integer(elementNumber);
        map.put(symbol, boxed);
        if (symbol.length() == 2)
          map.put(symbol.toUpperCase(), boxed);
      }
      for (int i = altElementMax; --i >= firstIsotope;) {
        String symbol = altElementSymbols[i];
        Integer boxed = new Integer(altElementNumbers[i]);
        map.put(symbol, boxed);
        if (symbol.length() == 2)
          map.put(symbol.toUpperCase(), boxed);
      }
      htElementMap = map;
    }
    if (elementSymbol == null)
      return 0;
    Integer boxedAtomicNumber = (Integer) htElementMap.get(elementSymbol);
    if (boxedAtomicNumber != null)
      return (short) boxedAtomicNumber.intValue();
    Logger.error("'" + elementSymbol + "' is not a recognized symbol");
    return 0;
  }
  
  
  public final static String elementSymbolFromNumber(int elementNumber) {
    
    if (elementNumber >= elementNumberMax) {
      for (int j = altElementMax; --j >= 0;)
        if (elementNumber == altElementNumbers[j])
          return altElementSymbols[j];
      elementNumber %= 128;
    }
    if (elementNumber < 0 || elementNumber >= elementNumberMax)
      elementNumber = 0;
    return elementSymbols[elementNumber];
  }

  
  public final static String elementNameFromNumber(int elementNumber) {
    
    if (elementNumber >= elementNumberMax) {
      for (int j = altElementMax; --j >= 0;)
        if (elementNumber == altElementNumbers[j])
          return altElementNames[j];
      elementNumber %= 128;
    }
    if (elementNumber < 0 || elementNumber >= elementNumberMax)
      elementNumber = 0;
    return elementNames[elementNumber];
  }

  private final static String elementNames[] = {
    "unknown",       
    "hydrogen",      
    "helium",        
    "lithium",       
    "beryllium",     
    "boron",         
    "carbon",        
    "nitrogen",      
    "oxygen",        
    "fluorine",      
    "neon",          
    "sodium",        
    "magnesium",     
    "aluminum",      
    "silicon",       
    "phosphorus",    
    "sulfur",        
    "chlorine",      
    "argon",         
    "potassium",     
    "calcium",       
    "scandium",      
    "titanium",      
    "vanadium",      
    "chromium",      
    "manganese",     
    "iron",          
    "cobalt",        
    "nickel",        
    "copper",        
    "zinc",          
    "gallium",       
    "germanium",     
    "arsenic",       
    "selenium",      
    "bromine",       
    "krypton",       
    "rubidium",      
    "strontium",     
    "yttrium",       
    "zirconium",     
    "niobium",       
    "molybdenum",    
    "technetium",    
    "ruthenium",     
    "rhodium",       
    "palladium",     
    "silver",        
    "cadmium",       
    "indium",        
    "tin",           
    "antimony",      
    "tellurium",     
    "iodine",        
    "xenon",         
    "cesium",        
    "barium",        
    "lanthanum",     
    "cerium",        
    "praseodymium",  
    "neodymium",     
    "promethium",    
    "samarium",      
    "europium",      
    "gadolinium",    
    "terbium",       
    "dysprosium",    
    "holmium",       
    "erbium",        
    "thulium",       
    "ytterbium",     
    "lutetium",      
    "hafnium",       
    "tantalum",      
    "tungsten",      
    "rhenium",       
    "osmium",        
    "iridium",       
    "platinum",      
    "gold",          
    "mercury",       
    "thallium",      
    "lead",          
    "bismuth",       
    "polonium",      
    "astatine",      
    "radon",         
    "francium",      
    "radium",        
    "actinium",      
    "thorium",       
    "protactinium",  
    "uranium",       
    "neptunium",     
    "plutonium",     
    "americium",     
    "curium",        
    "berkelium",     
    "californium",   
    "einsteinium",   
    "fermium",       
    "mendelevium",   
    "nobelium",      
    "lawrencium",    
    "rutherfordium", 
    "dubnium",       
    "seaborgium",    
    "bohrium",       
    "hassium",       
    "meitnerium"     
  };

  
  public final static String altElementNameFromIndex(int i) {
    return altElementNames[i];
  }
  
  
  public final static short altElementNumberFromIndex(int i) {
    return altElementNumbers[i];
  }
  
  
  public final static String altElementSymbolFromIndex(int i) {
    return altElementSymbols[i];
  }
  
  
  public final static String altIsotopeSymbolFromIndex(int i) {
    int code = altElementNumbers[i]; 
    return (code >> 7) + elementSymbolFromNumber(code & 127);
  }
  
  
  public final static int altElementIndexFromNumber(int atomicAndIsotopeNumber) {
    for (int i = 0; i < altElementMax; i++)
      if (altElementNumbers[i] == atomicAndIsotopeNumber)
        return i;
    return 0;
  }
    
  
  private final static String naturalIsotopes = "1H,12C,14N,";

  public final static boolean isNaturalIsotope(String isotopeSymbol) {
    return (naturalIsotopes.indexOf(isotopeSymbol + ",") >= 0);      
  }

  private final static short[] altElementNumbers = {
    0,
    13,
    16,
    55,
    (2 << 7) + 1, 
    (3 << 7) + 1, 
    (11 << 7) + 6, 
    (13 << 7) + 6, 
    (14 << 7) + 6, 
    (15 << 7) + 7, 
  };

  
  private final static String[] altElementSymbols = {
    "Xx",
    "Al",
    "S",
    "Cs",
    "D",
    "T",
    "11C",
    "13C",
    "14C",
    "15N",
  };

  private final static String[] altElementNames = {
    "dummy",
    "aluminium",
    "sulphur",
    "caesium",
    "deuterium",
    "tritium",
    "",
    "",
    "",
    "",
  };
  
  public final static int[] altArgbsCpk = {
    0xFFFF1493, 
    0xFFBFA6A6, 
    0xFFFFFF30, 
    0xFF57178F, 
    0xFFFFFFC0, 
    0xFFFFFFA0, 
    0xFFD8D8D8, 
    0xFF505050, 
    0xFF404040, 
    0xFF105050, 
  };

  
  
  public final static int firstIsotope = 4;
  
  
  public final static int altElementMax = altElementNumbers.length;

  
  public final static int VDW_JMOL = 0;
  public final static int VDW_BABEL = 1; 
  public final static int VDW_RASMOL = 2; 
  public final static int VDW_USER = 3;
  final static String[] vdwLabels = {
    "Jmol", "Babel", "RasMol", "User"
   };
  
  public static int getVdwType(String label) {
    for (int i = 0; i < vdwLabels.length; i++)
      if (vdwLabels[i].equalsIgnoreCase(label))
        return i;
    return -1;
  }
  
  public static int getVanderwaalsMar(int i, int scale) {
    return vanderwaalsMars[(i << 2) + scale];
  }
  
  
  public final static short[] vanderwaalsMars = {
  
    1000,1000,1000,0, 
    1200,1200,1100,0, 
    1400,1400,2200,0, 
    1820,2200,1220,0, 
    1700,1900,628,0, 
    2080,1800,1548,0, 
    1950,1700,1548,0, 
    1850,1600,1400,0, 
    1700,1550,1348,0, 
    1730,1500,1300,0, 
    1540,1540,2020,0, 
    2270,2400,2200,0, 
    1730,2200,1500,0, 
    2050,2100,1500,0, 
    2100,2100,2200,0, 
    2080,1950,1880,0, 
    2000,1800,1808,0, 
    1970,1800,1748,0, 
    1880,1880,2768,0, 
    2750,2800,2388,0, 
    1973,2400,1948,0, 
    1700,2300,1320,0, 
    1700,2150,1948,0, 
    1700,2050,1060,0, 
    1700,2050,1128,0, 
    1700,2050,1188,0, 
    1700,2050,1948,0, 
    1700,2000,1128,0, 
    1630,2000,1240,0, 
    1400,2000,1148,0, 
    1390,2100,1148,0, 
    1870,2100,1548,0, 
    1700,2100,3996,0, 
    1850,2050,828,0, 
    1900,1900,900,0, 
    2100,1900,1748,0, 
    2020,2020,1900,0, 
    1700,2900,2648,0, 
    1700,2550,2020,0, 
    1700,2400,1608,0, 
    1700,2300,1420,0, 
    1700,2150,1328,0, 
    1700,2100,1748,0, 
    1700,2050,1800,0, 
    1700,2050,1200,0, 
    1700,2000,1220,0, 
    1630,2050,1440,0, 
    1720,2100,1548,0, 
    1580,2200,1748,0, 
    1930,2200,1448,0, 
    2170,2250,1668,0, 
    2200,2200,1120,0, 
    2060,2100,1260,0, 
    2150,2100,1748,0, 
    2160,2160,2100,0, 
    1700,3000,3008,0, 
    1700,2700,2408,0, 
    1700,2500,1828,0, 
    1700,2480,1860,0, 
    1700,2470,1620,0, 
    1700,2450,1788,0, 
    1700,2430,1760,0, 
    1700,2420,1740,0, 
    1700,2400,1960,0, 
    1700,2380,1688,0, 
    1700,2370,1660,0, 
    1700,2350,1628,0, 
    1700,2330,1608,0, 
    1700,2320,1588,0, 
    1700,2300,1568,0, 
    1700,2280,1540,0, 
    1700,2270,1528,0, 
    1700,2250,1400,0, 
    1700,2200,1220,0, 
    1700,2100,1260,0, 
    1700,2050,1300,0, 
    1700,2000,1580,0, 
    1700,2000,1220,0, 
    1720,2050,1548,0, 
    1660,2100,1448,0, 
    1550,2050,1980,0, 
    1960,2200,1708,0, 
    2020,2300,2160,0, 
    1700,2300,1728,0, 
    1700,2000,1208,0, 
    1700,2000,1120,0, 
    1700,2000,2300,0, 
    1700,2000,3240,0, 
    1700,2000,2568,0, 
    1700,2000,2120,0, 
    1700,2400,1840,0, 
    1700,2000,1600,0, 
    1860,2300,1748,0, 
    1700,2000,1708,0, 
    1700,2000,1668,0, 
    1700,2000,1660,0, 
    1700,2000,1648,0, 
    1700,2000,1640,0, 
    1700,2000,1628,0, 
    1700,2000,1620,0, 
    1700,2000,1608,0, 
    1700,2000,1600,0, 
    1700,2000,1588,0, 
    1700,2000,1580,0, 
    1700,2000,1600,0, 
    1700,2000,1600,0, 
    1700,2000,1600,0, 
    1700,2000,1600,0, 
    1700,2000,1600,0, 
    1700,2000,1600,0, 
  };

  
  private final static short[] covalentMars = {
    0,    
    230,  
    930,  
    680,  
    350,  
    830,  
    680,  
    680,  
    680,  
    640,  
    1120, 
    970,  
    1100, 
    1350, 
    1200, 
    750,  
    1020, 
    990,  
    1570, 
    1330, 
    990,  
    1440, 
    1470, 
    1330, 
    1350, 
    1350, 
    1340, 
    1330, 
    1500, 
    1520, 
    1450, 
    1220, 
    1170, 
    1210, 
    1220, 
    1210, 
    1910, 
    1470, 
    1120, 
    1780, 
    1560, 
    1480, 
    1470, 
    1350, 
    1400, 
    1450, 
    1500, 
    1590, 
    1690, 
    1630, 
    1460, 
    1460, 
    1470, 
    1400, 
    1980, 
    1670, 
    1340, 
    1870, 
    1830, 
    1820, 
    1810, 
    1800, 
    1800, 
    1990, 
    1790, 
    1760, 
    1750, 
    1740, 
    1730, 
    1720, 
    1940, 
    1720, 
    1570, 
    1430, 
    1370, 
    1350, 
    1370, 
    1320, 
    1500, 
    1500, 
    1700, 
    1550, 
    1540, 
    1540, 
    1680, 
    1700, 
    2400, 
    2000, 
    1900, 
    1880, 
    1790, 
    1610, 
    1580, 
    1550, 
    1530, 
    1510, 
    1500, 
    1500, 
    1500, 
    1500, 
    1500, 
    1500, 
    1500, 
    1500, 
    1600, 
    1600, 
    1600, 
    1600, 
    1600, 
    1600, 
  };

  

  public final static int FORMAL_CHARGE_MIN = -4;
  public final static int FORMAL_CHARGE_MAX = 7;
  
  private final static short[] cationLookupTable = {
    (3 << 4) + (1 + 4),   680,  
    (4 << 4) + (1 + 4),   440,  
    (4 << 4) + (2 + 4),   350,  
    (5 << 4) + (1 + 4),   350,  
    (5 << 4) + (3 + 4),   230,  
    (6 << 4) + (4 + 4),   160,  
    (7 << 4) + (1 + 4),   680,  
    (7 << 4) + (3 + 4),   160,  
    (7 << 4) + (5 + 4),   130,  
    (8 << 4) + (1 + 4),   220,  
    (8 << 4) + (6 + 4),   90,   
    (9 << 4) + (7 + 4),   80,   
    (10 << 4) + (1 + 4),  1120, 
    (11 << 4) + (1 + 4),  970,  
    (12 << 4) + (1 + 4),  820,  
    (12 << 4) + (2 + 4),  660,  
    (13 << 4) + (3 + 4),  510,  
    (14 << 4) + (1 + 4),  650,  
    (14 << 4) + (4 + 4),  420,  
    (15 << 4) + (3 + 4),  440,  
    (15 << 4) + (5 + 4),  350,  
    (16 << 4) + (2 + 4),  2190, 
    (16 << 4) + (4 + 4),  370,  
    (16 << 4) + (6 + 4),  300,  
    (17 << 4) + (5 + 4),  340,  
    (17 << 4) + (7 + 4),  270,  
    (18 << 4) + (1 + 4),  1540, 
    (19 << 4) + (1 + 4),  1330, 
    (20 << 4) + (1 + 4),  1180, 
    (20 << 4) + (2 + 4),  990,  
    (21 << 4) + (3 + 4),  732,  
    (22 << 4) + (1 + 4),  960,  
    (22 << 4) + (2 + 4),  940,  
    (22 << 4) + (3 + 4),  760,  
    (22 << 4) + (4 + 4),  680,  
    (23 << 4) + (2 + 4),  880,  
    (23 << 4) + (3 + 4),  740,  
    (23 << 4) + (4 + 4),  630,  
    (23 << 4) + (5 + 4),  590,  
    (24 << 4) + (1 + 4),  810,  
    (24 << 4) + (2 + 4),  890,  
    (24 << 4) + (3 + 4),  630,  
    (24 << 4) + (6 + 4),  520,  
    (25 << 4) + (2 + 4),  800,  
    (25 << 4) + (3 + 4),  660,  
    (25 << 4) + (4 + 4),  600,  
    (25 << 4) + (7 + 4),  460,  
    (26 << 4) + (2 + 4),  740,  
    (26 << 4) + (3 + 4),  640,  
    (27 << 4) + (2 + 4),  720,  
    (27 << 4) + (3 + 4),  630,  
    (28 << 4) + (2 + 4),  690,  
    (29 << 4) + (1 + 4),  960,  
    (29 << 4) + (2 + 4),  720,  
    (30 << 4) + (1 + 4),  880,  
    (30 << 4) + (2 + 4),  740,  
    (31 << 4) + (1 + 4),  810,  
    (31 << 4) + (3 + 4),  620,  
    (32 << 4) + (2 + 4),  730,  
    (32 << 4) + (4 + 4),  530,  
    (33 << 4) + (3 + 4),  580,  
    (33 << 4) + (5 + 4),  460,  
    (34 << 4) + (1 + 4),  660,  
    (34 << 4) + (4 + 4),  500,  
    (34 << 4) + (6 + 4),  420,  
    (35 << 4) + (5 + 4),  470,  
    (35 << 4) + (7 + 4),  390,  
    (37 << 4) + (1 + 4),  1470, 
    (38 << 4) + (2 + 4),  1120, 
    (39 << 4) + (3 + 4),  893,  
    (40 << 4) + (1 + 4),  1090, 
    (40 << 4) + (4 + 4),  790,  
    (41 << 4) + (1 + 4),  1000, 
    (41 << 4) + (4 + 4),  740,  
    (41 << 4) + (5 + 4),  690,  
    (42 << 4) + (1 + 4),  930,  
    (42 << 4) + (4 + 4),  700,  
    (42 << 4) + (6 + 4),  620,  
    (43 << 4) + (7 + 4),  979,  
    (44 << 4) + (4 + 4),  670,  
    (45 << 4) + (3 + 4),  680,  
    (46 << 4) + (2 + 4),  800,  
    (46 << 4) + (4 + 4),  650,  
    (47 << 4) + (1 + 4),  1260, 
    (47 << 4) + (2 + 4),  890,  
    (48 << 4) + (1 + 4),  1140, 
    (48 << 4) + (2 + 4),  970,  
    (49 << 4) + (3 + 4),  810,  
    (50 << 4) + (2 + 4),  930,  
    (50 << 4) + (4 + 4),  710,  
    (51 << 4) + (3 + 4),  760,  
    (51 << 4) + (5 + 4),  620,  
    (52 << 4) + (1 + 4),  820,  
    (52 << 4) + (4 + 4),  700,  
    (52 << 4) + (6 + 4),  560,  
    (53 << 4) + (5 + 4),  620,  
    (53 << 4) + (7 + 4),  500,  
    (55 << 4) + (1 + 4),  1670, 
    (56 << 4) + (1 + 4),  1530, 
    (56 << 4) + (2 + 4),  1340, 
    (57 << 4) + (1 + 4),  1390, 
    (57 << 4) + (3 + 4),  1016, 
    (58 << 4) + (1 + 4),  1270, 
    (58 << 4) + (3 + 4),  1034, 
    (58 << 4) + (4 + 4),  920,  
    (59 << 4) + (3 + 4),  1013, 
    (59 << 4) + (4 + 4),  900,  
    (60 << 4) + (3 + 4),  995,  
    (61 << 4) + (3 + 4),  979,  
    (62 << 4) + (3 + 4),  964,  
    (63 << 4) + (2 + 4),  1090, 
    (63 << 4) + (3 + 4),  950,  
    (64 << 4) + (3 + 4),  938,  
    (65 << 4) + (3 + 4),  923,  
    (65 << 4) + (4 + 4),  840,  
    (66 << 4) + (3 + 4),  908,  
    (67 << 4) + (3 + 4),  894,  
    (68 << 4) + (3 + 4),  881,  
    (69 << 4) + (3 + 4),  870,  
    (70 << 4) + (2 + 4),  930,  
    (70 << 4) + (3 + 4),  858,  
    (71 << 4) + (3 + 4),  850,  
    (72 << 4) + (4 + 4),  780,  
    (73 << 4) + (5 + 4),  680,  
    (74 << 4) + (4 + 4),  700,  
    (74 << 4) + (6 + 4),  620,  
    (75 << 4) + (4 + 4),  720,  
    (75 << 4) + (7 + 4),  560,  
    (76 << 4) + (4 + 4),  880,  
    (76 << 4) + (6 + 4),  690,  
    (77 << 4) + (4 + 4),  680,  
    (78 << 4) + (2 + 4),  800,  
    (78 << 4) + (4 + 4),  650,  
    (79 << 4) + (1 + 4),  1370, 
    (79 << 4) + (3 + 4),  850,  
    (80 << 4) + (1 + 4),  1270, 
    (80 << 4) + (2 + 4),  1100, 
    (81 << 4) + (1 + 4),  1470, 
    (81 << 4) + (3 + 4),  950,  
    (82 << 4) + (2 + 4),  1200, 
    (82 << 4) + (4 + 4),  840,  
    (83 << 4) + (1 + 4),  980,  
    (83 << 4) + (3 + 4),  960,  
    (83 << 4) + (5 + 4),  740,  
    (84 << 4) + (6 + 4),  670,  
    (85 << 4) + (7 + 4),  620,  
    (87 << 4) + (1 + 4),  1800, 
    (88 << 4) + (2 + 4),  1430, 
    (89 << 4) + (3 + 4),  1180, 
    (90 << 4) + (4 + 4),  1020, 
    (91 << 4) + (3 + 4),  1130, 
    (91 << 4) + (4 + 4),  980,  
    (91 << 4) + (5 + 4),  890,  
    (92 << 4) + (4 + 4),  970,  
    (92 << 4) + (6 + 4),  800,  
    (93 << 4) + (3 + 4),  1100, 
    (93 << 4) + (4 + 4),  950,  
    (93 << 4) + (7 + 4),  710,  
    (94 << 4) + (3 + 4),  1080, 
    (94 << 4) + (4 + 4),  930,  
    (95 << 4) + (3 + 4),  1070, 
    (95 << 4) + (4 + 4),  920,  
  };
  
  
  
  private final static short[] anionLookupTable = {
    (1 << 4) + (-1 + 4),  1540, 
    (6 << 4) + (-4 + 4),  2600, 
    (7 << 4) + (-3 + 4),  1710, 
    (8 << 4) + (-2 + 4),  1360, 
    (8 << 4) + (-1 + 4),   680, 
    (9 << 4) + (-1 + 4),  1330, 
  
  
    (15 << 4) + (-3 + 4), 2120, 
    (16 << 4) + (-2 + 4), 1840, 
    (17 << 4) + (-1 + 4), 1810, 
    (32 << 4) + (-4 + 4), 2720, 
    (33 << 4) + (-3 + 4), 2220, 
    (34 << 4) + (-2 + 4), 1980, 
  
    (35 << 4) + (-1 + 4), 1960, 
    (50 << 4) + (-4 + 4), 2940, 
    (50 << 4) + (-1 + 4), 3700, 
    (51 << 4) + (-3 + 4), 2450, 
    (52 << 4) + (-2 + 4), 2110, 
    (52 << 4) + (-1 + 4), 2500, 
    (53 << 4) + (-1 + 4), 2200, 
  };
  
  static BitSet bsCations = new BitSet();
  static BitSet bsAnions = new BitSet();
  static {
    for (int i = 0; i < anionLookupTable.length; i+=2)
      bsAnions.set(anionLookupTable[i]>>4);
    for (int i = 0; i < cationLookupTable.length; i+=2)
      bsCations.set(cationLookupTable[i]>>4);
  }

  public static short getBondingMar(int atomicNumber, int charge) {
    if (charge > 0 && bsCations.get(atomicNumber))
      return getBondingMar(atomicNumber, charge, cationLookupTable);
    if (charge < 0 && bsAnions.get(atomicNumber))
      return getBondingMar(atomicNumber, charge, anionLookupTable);
    return (short) covalentMars[atomicNumber];
  }
  
  public static short getBondingMar(int atomicNumber, int charge, short[] table) {
    
    
    
    short ionic = (short) ((atomicNumber << 4) + (charge + 4)); 
    int iVal = 0, iMid = 0, iMin = 0, iMax = table.length / 2;
    while (iMin != iMax) {
      iMid = (iMin + iMax) / 2;
      iVal = table[iMid<<1];
      if (iVal > ionic)
        iMax = iMid;
      else if (iVal < ionic)
        iMin = iMid + 1;
      else
        return table[(iMid << 1) + 1];
    }
    
    if (iVal > ionic) 
      iMid--; 
    iVal = table[iMid << 1];
    if (atomicNumber != (iVal >> 4)) 
      iMid++; 
    return table[(iMid << 1) + 1];
  }

  
  
  
  
  public final static int MAXIMUM_AUTO_BOND_COUNT = 20;
  
  public static byte pidOf(Object value) {
    return (value instanceof Byte ? ((Byte) value).byteValue()
        : PALETTE_UNKNOWN);
  }

  public final static byte PALETTE_VOLATILE = 0x40; 
  public final static byte PALETTE_STATIC = 0x3F;
  public final static byte PALETTE_UNKNOWN = (byte) 0xFF; 
  
  public final static byte PALETTE_NONE = 0;
  public final static byte PALETTE_CPK = 1;
  public final static byte PALETTE_PARTIAL_CHARGE = 2;
  public final static byte PALETTE_FORMAL_CHARGE = 3;
  public final static byte PALETTE_TEMP = 4 | PALETTE_VOLATILE;
  
  public final static byte PALETTE_FIXEDTEMP = 5;
  public final static byte PALETTE_SURFACE = 6 | PALETTE_VOLATILE;
  public final static byte PALETTE_STRUCTURE = 7;
  public final static byte PALETTE_AMINO = 8;
  
  public final static byte PALETTE_SHAPELY = 9;
  public final static byte PALETTE_CHAIN = 10;
  
  
  
  
  public final static byte PALETTE_GROUP = 11 | PALETTE_VOLATILE; 
  public final static byte PALETTE_MONOMER = 12 | PALETTE_VOLATILE;
  public final static byte PALETTE_MOLECULE = 13 | PALETTE_VOLATILE;
  public final static byte PALETTE_ALTLOC = 14;
  
  public final static byte PALETTE_INSERTION = 15;
  public final static byte PALETTE_JMOL = 16;
  public final static byte PALETTE_RASMOL = 17;
  public final static byte PALETTE_TYPE = 18;  
  public final static byte PALETTE_ENERGY = 19;
  public final static byte PALETTE_PROPERTY = 20 | PALETTE_VOLATILE;
  public final static byte PALETTE_VARIABLE = 21 | PALETTE_VOLATILE;

  public final static byte PALETTE_STRAIGHTNESS = 22 | PALETTE_VOLATILE;

  private final static String[] paletteNames = {
     "none", "cpk", "partialcharge", "formalcharge", "temperature",  
     "fixedtemperature", "surfacedistance", "structure", "amino", 
     "shapely", "chain", "group", "monomer", "molecule", "altloc", 
     "insertion", "jmol", "rasmol", 
     "type", "energy" , 
     "property", "variable", "straightness" 
   };
   
  private final static byte[] paletteIDs = {
    PALETTE_NONE, 
    PALETTE_CPK,    
    PALETTE_PARTIAL_CHARGE, 
    PALETTE_FORMAL_CHARGE,    
    PALETTE_TEMP,

    PALETTE_FIXEDTEMP,
    PALETTE_SURFACE,
    PALETTE_STRUCTURE,
    PALETTE_AMINO,

    PALETTE_SHAPELY,
    PALETTE_CHAIN,
    PALETTE_GROUP,
    PALETTE_MONOMER,
    PALETTE_MOLECULE,
    PALETTE_ALTLOC,

    PALETTE_INSERTION,
    PALETTE_JMOL,
    PALETTE_RASMOL,
    PALETTE_TYPE,
    PALETTE_ENERGY,
    
    PALETTE_PROPERTY,
    PALETTE_VARIABLE,
    PALETTE_STRAIGHTNESS,
    };
   
  
  private final static int paletteCount = paletteNames.length;
  
  public static boolean isPaletteVariable(byte pid) {
    return ((pid & PALETTE_VOLATILE) != 0);  
  }
  
  public final static byte getPaletteID(String paletteName) {
    for (int i = 0; i < paletteCount; i++)
      if (paletteNames[i].equals(paletteName))
        return paletteIDs[i];
    return (paletteName.indexOf("property_") == 0 ? PALETTE_PROPERTY
        : PALETTE_UNKNOWN);
  }
  
  public final static String getPaletteName(byte pid) {
    if (pid == PALETTE_UNKNOWN)
      return null;
    for (int i = 0; i < paletteCount; i++)
      if (paletteIDs[i] == pid)
        return paletteNames[i];
    return null;
  }
  
  
  public final static int[] argbsCpk = {
    0xFFFF1493, 
    0xFFFFFFFF, 
    0xFFD9FFFF, 
    0xFFCC80FF, 
    0xFFC2FF00, 
    0xFFFFB5B5, 
    0xFF909090, 
    0xFF3050F8, 
    0xFFFF0D0D, 
    0xFF90E050, 
    0xFFB3E3F5, 
    0xFFAB5CF2, 
    0xFF8AFF00, 
    0xFFBFA6A6, 
    0xFFF0C8A0, 
    0xFFFF8000, 
    0xFFFFFF30, 
    0xFF1FF01F, 
    0xFF80D1E3, 
    0xFF8F40D4, 
    0xFF3DFF00, 
    0xFFE6E6E6, 
    0xFFBFC2C7, 
    0xFFA6A6AB, 
    0xFF8A99C7, 
    0xFF9C7AC7, 
    0xFFE06633, 
    0xFFF090A0, 
    0xFF50D050, 
    0xFFC88033, 
    0xFF7D80B0, 
    0xFFC28F8F, 
    0xFF668F8F, 
    0xFFBD80E3, 
    0xFFFFA100, 
    0xFFA62929, 
    0xFF5CB8D1, 
    0xFF702EB0, 
    0xFF00FF00, 
    0xFF94FFFF, 
    0xFF94E0E0, 
    0xFF73C2C9, 
    0xFF54B5B5, 
    0xFF3B9E9E, 
    0xFF248F8F, 
    0xFF0A7D8C, 
    0xFF006985, 
    0xFFC0C0C0, 
    0xFFFFD98F, 
    0xFFA67573, 
    0xFF668080, 
    0xFF9E63B5, 
    0xFFD47A00, 
    0xFF940094, 
    0xFF429EB0, 
    0xFF57178F, 
    0xFF00C900, 
    0xFF70D4FF, 
    0xFFFFFFC7, 
    0xFFD9FFC7, 
    0xFFC7FFC7, 
    0xFFA3FFC7, 
    0xFF8FFFC7, 
    0xFF61FFC7, 
    0xFF45FFC7, 
    0xFF30FFC7, 
    0xFF1FFFC7, 
    0xFF00FF9C, 
    0xFF00E675, 
    0xFF00D452, 
    0xFF00BF38, 
    0xFF00AB24, 
    0xFF4DC2FF, 
    0xFF4DA6FF, 
    0xFF2194D6, 
    0xFF267DAB, 
    0xFF266696, 
    0xFF175487, 
    0xFFD0D0E0, 
    0xFFFFD123, 
    0xFFB8B8D0, 
    0xFFA6544D, 
    0xFF575961, 
    0xFF9E4FB5, 
    0xFFAB5C00, 
    0xFF754F45, 
    0xFF428296, 
    0xFF420066, 
    0xFF007D00, 
    0xFF70ABFA, 
    0xFF00BAFF, 
    0xFF00A1FF, 
    0xFF008FFF, 
    0xFF0080FF, 
    0xFF006BFF, 
    0xFF545CF2, 
    0xFF785CE3, 
    0xFF8A4FE3, 
    0xFFA136D4, 
    0xFFB31FD4, 
    0xFFB31FBA, 
    0xFFB30DA6, 
    0xFFBD0D87, 
    0xFFC70066, 
    0xFFCC0059, 
    0xFFD1004F, 
    0xFFD90045, 
    0xFFE00038, 
    0xFFE6002E, 
    0xFFEB0026, 
};

  public final static int[] argbsCpkRasmol = {
    0x00FF1493 + ( 0 << 24), 
    0x00FFFFFF + ( 1 << 24), 
    0x00FFC0CB + ( 2 << 24), 
    0x00B22222 + ( 3 << 24), 
    0x0000FF00 + ( 5 << 24), 
    0x00C8C8C8 + ( 6 << 24), 
    0x008F8FFF + ( 7 << 24), 
    0x00F00000 + ( 8 << 24), 
    0x00DAA520 + ( 9 << 24), 
    0x000000FF + (11 << 24), 
    0x00228B22 + (12 << 24), 
    0x00808090 + (13 << 24), 
    0x00DAA520 + (14 << 24), 
    0x00FFA500 + (15 << 24), 
    0x00FFC832 + (16 << 24), 
    0x0000FF00 + (17 << 24), 
    0x00808090 + (20 << 24), 
    0x00808090 + (22 << 24), 
    0x00808090 + (24 << 24), 
    0x00808090 + (25 << 24), 
    0x00FFA500 + (26 << 24), 
    0x00A52A2A + (28 << 24), 
    0x00A52A2A + (29 << 24), 
    0x00A52A2A + (30 << 24), 
    0x00A52A2A + (35 << 24), 
    0x00808090 + (47 << 24), 
    0x00A020F0 + (53 << 24), 
    0x00FFA500 + (56 << 24), 
    0x00DAA520 + (79 << 24), 
  };

  static {
    
    
    if ((elementNames.length != elementNumberMax) ||
        (vanderwaalsMars.length / 4 != elementNumberMax) ||
        (covalentMars.length  != elementNumberMax) ||
        (argbsCpk.length != elementNumberMax)) {
      Logger.error("ERROR!!! Element table length mismatch:" +
                         "\n elementSymbols.length=" + elementSymbols.length +
                         "\n elementNames.length=" + elementNames.length +
                         "\n vanderwaalsMars.length=" + vanderwaalsMars.length+
                         "\n covalentMars.length=" +
                         covalentMars.length +
                         "\n argbsCpk.length=" + argbsCpk.length);
    }
  }

  
  public final static byte PROTEIN_STRUCTURE_NONE = 0;
  public final static byte PROTEIN_STRUCTURE_TURN = 1;
  public final static byte PROTEIN_STRUCTURE_SHEET = 2;
  public final static byte PROTEIN_STRUCTURE_HELIX = 3;
  public final static byte PROTEIN_STRUCTURE_DNA = 4;
  public final static byte PROTEIN_STRUCTURE_RNA = 5;

  public final static String[] proteinStructureNames = {
    "none", "turn", "sheet", "helix", "dna", "rna"
  };
  
  public final static String getProteinStructureName(int itype) {
    return (itype >= 0 && itype <= 5 ? proteinStructureNames[itype] : "");
  }
  
  
  public final static int[] argbsStructure = {
    0xFFFFFFFF, 
    0xFF6080FF, 
    0xFFFFC800, 
    0xFFFF0080, 
    0xFFAE00FE, 
    0xFFFD0162, 
  };

  public final static int[] argbsAmino = {
    0xFFBEA06E, 
    
    0xFFC8C8C8, 
    0xFF145AFF, 
    0xFF00DCDC, 
    0xFFE60A0A, 
    0xFFE6E600, 
    0xFF00DCDC, 
    0xFFE60A0A, 
    0xFFEBEBEB, 
    0xFF8282D2, 
    0xFF0F820F, 
    0xFF0F820F, 
    0xFF145AFF, 
    0xFFE6E600, 
    0xFF3232AA, 
    0xFFDC9682, 
    0xFFFA9600, 
    0xFFFA9600, 
    0xFFB45AB4, 
    0xFF3232AA, 
    0xFF0F820F, 

    0xFFFF69B4, 
    0xFFFF69B4, 
    0xFFBEA06E, 
  };

  
  public final static int argbShapelyBackbone = 0xFFB8B8B8;
  public final static int argbShapelySpecial =  0xFF5E005E;
  public final static int argbShapelyDefault =  0xFFFF00FF;

  

  

  public final static int[] argbsChainAtom = {
    
    0xFFffffff, 
    
    0xFFC0D0FF, 
    0xFFB0FFB0, 
    0xFFFFC0C8, 
    0xFFFFFF80, 
    0xFFFFC0FF, 
    0xFFB0F0F0, 
    0xFFFFD070, 
    0xFFF08080, 

    0xFFF5DEB3, 
    0xFF00BFFF, 
    0xFFCD5C5C, 
    0xFF66CDAA, 
    0xFF9ACD32, 
    0xFFEE82EE, 
    0xFF00CED1, 
    0xFF00FF7F, 
    0xFF3CB371, 

    0xFF00008B, 
    0xFFBDB76B, 
    0xFF006400, 
    0xFF800000, 
    0xFF808000, 
    0xFF800080, 
    0xFF008080, 
    0xFFB8860B, 
    0xFFB22222, 
  };

  public final static int[] argbsChainHetero = {
    
    0xFFffffff, 
    
    0xFFC0D0FF - 0x00303030, 
    0xFFB0FFB0 - 0x00303018, 
    0xFFFFC0C8 - 0x00303018, 
    0xFFFFFF80 - 0x00303010, 
    0xFFFFC0FF - 0x00303030, 
    0xFFB0F0F0 - 0x00303030, 
    0xFFFFD070 - 0x00303010, 
    0xFFF08080 - 0x00303010, 

    0xFFF5DEB3 - 0x00303030, 
    0xFF00BFFF - 0x00001830, 
    0xFFCD5C5C - 0x00181010, 
    0xFF66CDAA - 0x00101818, 
    0xFF9ACD32 - 0x00101808, 
    0xFFEE82EE - 0x00301030, 
    0xFF00CED1 - 0x00001830, 
    0xFF00FF7F - 0x00003010, 
    0xFF3CB371 - 0x00081810, 

    0xFF00008B + 0x00000030, 
    0xFFBDB76B - 0x00181810, 
    0xFF006400 + 0x00003000, 
    0xFF800000 + 0x00300000, 
    0xFF808000 + 0x00303000, 
    0xFF800080 + 0x00300030, 
    0xFF008080 + 0x00003030, 
    0xFFB8860B + 0x00303008, 
    0xFFB22222 + 0x00101010, 
  };

  public final static short FORMAL_CHARGE_COLIX_RED =
    (short)elementSymbols.length;
  public final static short FORMAL_CHARGE_COLIX_WHITE =
    (short)(FORMAL_CHARGE_COLIX_RED + 4);
  public final static short FORMAL_CHARGE_COLIX_BLUE =
    (short)(FORMAL_CHARGE_COLIX_WHITE + 7);
  public final static int FORMAL_CHARGE_RANGE_SIZE = 12;

  public final static int[] argbsFormalCharge = {
    0xFFFF0000, 
    0xFFFF4040, 
    0xFFFF8080, 
    0xFFFFC0C0, 
    0xFFFFFFFF, 
    0xFFD8D8FF, 
    0xFFB4B4FF, 
    0xFF9090FF, 
    0xFF6C6CFF, 
    0xFF4848FF, 
    0xFF2424FF, 
    0xFF0000FF, 
  };

  public final static int FORMAL_CHARGE_INDEX_WHITE = 4;
  public final static int FORMAL_CHARGE_INDEX_MAX = argbsFormalCharge.length;

  public final static short PARTIAL_CHARGE_COLIX_RED =
    (short)(FORMAL_CHARGE_COLIX_BLUE + 1);
  public final static short PARTIAL_CHARGE_COLIX_WHITE =
    (short)(PARTIAL_CHARGE_COLIX_RED + 15);
  public final static short PARTIAL_CHARGE_COLIX_BLUE =
    (short)(PARTIAL_CHARGE_COLIX_WHITE + 15);
  public final static int PARTIAL_CHARGE_RANGE_SIZE = 31;

  public final static int[] argbsRwbScale = {
    0xFFFF0000, 
    0xFFFF1010, 
    0xFFFF2020, 
    0xFFFF3030, 
    0xFFFF4040, 
    0xFFFF5050, 
    0xFFFF6060, 
    0xFFFF7070, 
    0xFFFF8080, 
    0xFFFF9090, 
    0xFFFFA0A0, 
    0xFFFFB0B0, 
    0xFFFFC0C0, 
    0xFFFFD0D0, 
    0xFFFFE0E0, 
    0xFFFFFFFF, 
    0xFFE0E0FF, 
    0xFFD0D0FF, 
    0xFFC0C0FF, 
    0xFFB0B0FF, 
    0xFFA0A0FF, 
    0xFF9090FF, 
    0xFF8080FF, 
    0xFF7070FF, 
    0xFF6060FF, 
    0xFF5050FF, 
    0xFF4040FF, 
    0xFF3030FF, 
    0xFF2020FF, 
    0xFF1010FF, 
    0xFF0000FF, 
  };

  public final static int[] argbsRoygbScale = {
    0xFFFF0000,
    0xFFFF2000,
    0xFFFF4000,
    0xFFFF6000,
    0xFFFF8000,
    0xFFFFA000,
    0xFFFFC000,
    0xFFFFE000,

    0xFFFFF000, 

    0xFFFFFF00,
    0xFFF0F000, 
    0xFFE0FF00,
    0xFFC0FF00,
    0xFFA0FF00,
    0xFF80FF00,
    0xFF60FF00,
    0xFF40FF00,
    0xFF20FF00,

    0xFF00FF00,
    0xFF00FF20,
    0xFF00FF40,
    0xFF00FF60,
    0xFF00FF80,
    0xFF00FFA0,
    0xFF00FFC0,
    0xFF00FFE0,

    0xFF00FFFF,
    0xFF00E0FF,
    0xFF00C0FF,
    0xFF00A0FF,
    0xFF0080FF,
    0xFF0060FF,
    0xFF0040FF,
    0xFF0020FF,

    0xFF0000FF,
  };

  

  
  
  
  
  public final static int[] argbsIsosurfacePositive = {
    0xFF5020A0,
  
  };
  
  public final static int[] argbsIsosurfaceNegative = {
    0xFFA02050,
  
  };

  public final static String[] specialAtomNames = {
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    null, 
    
    
    
    "N",   
    "CA",  
    "C",   
    "O",   
    "O1",  

    
    
    "O5'", 
    "C5'", 
    "C4'", 
    "C3'", 
    "O3'", 
    "C2'", 
    "C1'", 
    
    
    "P",   

    
    

    
    
    
    null, null,             
    null, null, null, null, 
    null, null, null, null, 
    null, null, null, null, 
    null, null, null, null, 

    
    
    "N1",   
    "C2",   
    "N3",   
    "C4",   
    "C5",   
    "C6",   
            
            

    
    
    "O2",   

    
    
    "N7",   
    "C8",   
    "N9",   
    
    
    
    
    
    "N4",  
    "N2",  
    "N6",  
    "C5M", 

    "O6",  
    "O4",  
    "S4",  

    "C7", 

    "H1",  
    "H2",  
    "H3",  
    null, null, 
    null, null, null, null, null, 
    null, null, null, null,       
    
    

    
    
    "OXT", 

    
    
    "H",   
    
    "1H",  
    "2H",  
    "3H",  
    "HA",  
    "1HA", 
    "2HA", 

    

    "H5T", 
    "O5T", 
    "O1P", 
    "OP1", 
    "O2P", 
    "OP2", 

    "O4'", 
    "O2'", 

    
    
    "1H5'", 
    "2H5'", 
    "H4'",  
    "H3'",  
    "1H2'", 
    "2H2'", 
    "2HO'", 
    "H1'",  
    "H3T",  
        
    

    "HO3'", 
    "HO5'", 
    "HA2",
    "HA3",
    "HA2", 
    "H5'", 
    "H5''",
    "H2'",
    "H2''",
    "HO2'",
    
};

  public final static int ATOMID_MAX = specialAtomNames.length;
  
  
  
  
  
  
  

  
  
  

  
  public final static byte ATOMID_AMINO_NITROGEN  = 1;
  public final static byte ATOMID_ALPHA_CARBON    = 2;
  public final static byte ATOMID_CARBONYL_CARBON = 3;
  public final static byte ATOMID_CARBONYL_OXYGEN = 4;
  public final static byte ATOMID_O1              = 5;
  
  
  public final static int ATOMID_ALPHA_ONLY_MASK = 1 << ATOMID_ALPHA_CARBON;

  
  public final static int ATOMID_PROTEIN_MASK =  0x7 << ATOMID_AMINO_NITROGEN;

  public final static byte ATOMID_O5_PRIME        = 6;
  public final static byte ATOMID_C3_PRIME        = 9;
  public final static byte ATOMID_O3_PRIME        = 10;
  
  
  public final static int ATOMID_NUCLEIC_MASK = 0x7F << ATOMID_O5_PRIME;

  public final static byte ATOMID_NUCLEIC_PHOSPHORUS = 13;
  
  
  public final static int ATOMID_PHOSPHORUS_ONLY_MASK =
    1 << ATOMID_NUCLEIC_PHOSPHORUS;

  
  public final static int ATOMID_DISTINGUISHING_ATOM_MAX = 14;
  
  public final static byte ATOMID_N1 = 32;
  public final static byte ATOMID_C2 = 33;
  public final static byte ATOMID_N3 = 34;
  public final static byte ATOMID_C4 = 35;
  public final static byte ATOMID_C5 = 36;
  public final static byte ATOMID_C6 = 37; 
  public final static byte ATOMID_O2 = 38;
  public final static byte ATOMID_N7 = 39;
  public final static byte ATOMID_C8 = 40;
  public final static byte ATOMID_N9 = 41;
  public final static byte ATOMID_N4 = 42;
  public final static byte ATOMID_N2 = 43;
  public final static byte ATOMID_N6 = 44;
  public final static byte ATOMID_C5M= 45;
  public final static byte ATOMID_O6 = 46;
  public final static byte ATOMID_O4 = 47;
  public final static byte ATOMID_S4 = 48;
  public final static byte ATOMID_C7 = 49;
  
  private final static int ATOMID_BACKBONE_MIN = 64;

  public final static byte ATOMID_TERMINATING_OXT = 64;
  public final static byte ATOMID_H5T_TERMINUS    = 72;
  public final static byte ATOMID_O5T_TERMINUS    = 73;
  public final static byte ATOMID_O1P             = 74;
  public final static byte ATOMID_OP1             = 75;
  public final static byte ATOMID_O2P             = 76;
  public final static byte ATOMID_OP2             = 77;
  public final static byte ATOMID_O2_PRIME        = 79;
  public final static byte ATOMID_H3T_TERMINUS    = 88;
  public final static byte ATOMID_HO3_PRIME       = 89;
  public final static byte ATOMID_HO5_PRIME       = 90;


  
  
  
  
  public final static int GROUPID_PROLINE          = 15;
  public final static int GROUPID_AMINO_MAX        = 24;
  
  private final static int GROUPID_WATER           = 42;
  private final static int GROUPID_SULPHATE        = 48;
  
  public final static String[] predefinedGroup3Names = {
    
    "", 
    
    "ALA", 
    "ARG",
    "ASN",
    "ASP",
    "CYS",
    "GLN",
    "GLU",
    "GLY",
    "HIS",
    "ILE",
    "LEU",
    "LYS",
    "MET",
    "PHE",
    "PRO", 
    "SER",
    "THR",
    "TRP",
    "TYR",
    "VAL",
    "ASX", 
    "GLX", 
    "UNK", 

    
    

    
    
    
    "G", 
    "C", 
    "A",
    "T", 
    "U", 
    "I", 
    
    "DG", 
    "DC",
    "DA",
    "DT",
    "DU",
    "DI",
    
    "+G", 
    "+C",
    "+A",
    "+T",
    "+U",
    "+I",
    
    
    
    "HOH", 
    "DOD", 
    "WAT", 
    "SOL", 
    "UREA", 
    "PO4", 
    "SO4", 

  };
  
  public final static int[] argbsShapely = {
    0xFFFF00FF, 
    
    0xFF8CFF8C, 
    0xFF00007C, 
    0xFFFF7C70, 
    0xFFA00042, 
    0xFFFFFF70, 
    0xFFFF4C4C, 
    0xFF660000, 
    0xFFFFFFFF, 
    0xFF7070FF, 
    0xFF004C00, 
    0xFF455E45, 
    0xFF4747B8, 
    0xFFB8A042, 
    0xFF534C52, 
    0xFF525252, 
    0xFFFF7042, 
    0xFFB84C00, 
    0xFF4F4600, 
    0xFF8C704C, 
    0xFFFF8CFF, 

    0xFFFF00FF, 
    0xFFFF00FF, 
    0xFFFF00FF, 

    0xFFFF7070, 
    0xFFFF8C4B, 
    0xFFA0A0FF, 
    0xFFA0FFA0, 
    0xFFFF8080, 
    0xFF80FFFF, 

    0xFFFF7070, 
    0xFFFF8C4B, 
    0xFFA0A0FF, 
    0xFFA0FFA0, 
    0xFFFF8080, 
    0xFF80FFFF, 
    
    0xFFFF7070, 
    0xFFFF8C4B, 
    0xFFA0A0FF, 
    0xFFA0FFA0, 
    0xFFFF8080, 
    0xFF80FFFF, 

    
    
    
  };


  
  private final static String allCarbohydrates = 
    ",[AFL],[AGC],[AHR],[ARA],[ARB],[BDF],[BDR],[BGC],[BMA]" +
    ",[FCA],[FCB],[FRU],[FUC],[FUL],[GAL],[GLA],[GLB],[GLC]" +
    ",[GUP],[LXC],[MAN],[RAA],[RAM],[RIB],[RIP],[XYP],[XYS]" +
    ",[CBI],[CT3],[CTR],[CTT],[LAT],[MAB],[MAL],[MLR],[MTT]" +
    ",[SUC],[TRE],[ASF],[GCU],[MTL],[NAG],[NAM],[RHA],[SOR]" +
    ",[XYL]";

  
  public final static boolean checkCarbohydrate(String group3) {
    return (group3 != null 
        && allCarbohydrates.indexOf("[" + group3.toUpperCase() + "]") >= 0);
  }

  private final static String getGroup3List() {
    StringBuffer s = new StringBuffer();
    
    for (int i = 1; i < GROUPID_WATER; i++)
      s.append(",[").append((predefinedGroup3Names[i]+"   ").substring(0,3)+"]");
    s.append(allCarbohydrates);
    return s.toString();
  }
  
  public final static boolean isHetero(String group3) {
    int pt = group3List.indexOf("[" + (group3 + "   ").substring(0, 3) + "]");
    return (pt < 0 || pt / 6 >= GROUPID_WATER);
  }

  public final static String group3List = getGroup3List();
  public final static int group3Count = group3List.length() / 6;
  
  public final static char[] predefinedGroup1Names = {
    
    '\0', 
    
    'A', 
    'R',
    'N',
    'D',
    'C',
    'Q',
    'E',
    'G',
    'H',
    'I',
    'L',
    'K',
    'M',
    'F',
    'P', 
    'S',
    'T',
    'W',
    'Y',
    'V',
    'A', 
    'G', 
    '?', 

    'G', 
    'C',
    'A',
    'T',
    'U',
    'I',
    
    'G', 
    'C',
    'A',
    'T',
    'U',
    'I',
    
    'G', 
    'C',
    'A',
    'T',
    'U',
    'I',
    };

  
  
  

  
  
  public static String[] predefinedVariable = {
    
    
    
    "@_1H _H & !(_2H,_3H)",
    "@_12C _C & !(_13C,_14C)",
    "@_14N _N & !(_15N)",

    
    
    
    "@water _g>=" + GROUPID_WATER + " & _g<" + (GROUPID_WATER + 3)
        + ", oxygen & connected(2) & connected(2, hydrogen or deuterium or tritium), (hydrogen or deuterium or tritium) & connected(oxygen & connected(2) & connected(2, hydrogen or deuterium or tritium))",
    "@hoh water",
    
    
    "@turn structure=1",
    "@sheet structure=2",
    "@helix structure=3",
    "@bonded bondcount>0",
  };
  
  
  
  public static String[] predefinedStatic = {
    
    
    
    
    "@amino _g>0 & _g<=23",
    "@acidic asp,glu",
    "@basic arg,his,lys",
    "@charged acidic,basic",
    "@negative acidic",
    "@positive basic",
    "@neutral amino&!(acidic,basic)",
    "@polar amino&!hydrophobic",

    "@cyclic his,phe,pro,trp,tyr",
    "@acyclic amino&!cyclic",
    "@aliphatic ala,gly,ile,leu,val",
    "@aromatic his,phe,trp,tyr",
    

    "@buried ala,cys,ile,leu,met,phe,trp,val",
    "@surface amino&!buried",

    
    
    
    
    "@hydrophobic ala,gly,ile,leu,met,phe,pro,trp,tyr,val",
    "@ligand hetero & !solvent",
    "@mainchain backbone",
    "@small ala,gly,ser",
    "@medium asn,asp,cys,pro,thr,val",
    "@large arg,glu,gln,his,ile,leu,lys,met,phe,trp,tyr",

    
    

    
    
    "@c nucleic & within(group,_a="+ATOMID_N4+")",
    "@g nucleic & within(group,_a="+ATOMID_N2+")",
    "@cg c,g",
    "@a nucleic & within(group,_a="+ATOMID_N6+")",
    "@t nucleic & within(group,_a="+ATOMID_C5M+" | _a="+ATOMID_C7+")",
    "@at a,t",
    "@i nucleic & within(group,_a="+ATOMID_O6+") & !g",
    "@u nucleic & within(group,_a="+ATOMID_O4+") & !t",
    "@tu nucleic & within(group,_a="+ATOMID_S4+")",

    
    
    
    "@solvent _g>="+GROUPID_WATER+" & _g<="+GROUPID_SULPHATE, 
    "@ions _g>="+(GROUPID_WATER+3)+",_g<="+GROUPID_SULPHATE,

    
    
    
    "@alpha _a=2", 
    "@backbone (protein,nucleic) & _a>0 & (_a<32 || _a>="+ATOMID_BACKBONE_MIN+")",
    "@sidechain (protein,nucleic) & !backbone",
    "@base nucleic & !backbone",

    

  };

  
  
  

  public final static String DEFAULT_FONTFACE = "SansSerif";
  public final static String DEFAULT_FONTSTYLE = "Plain";

  public final static int LABEL_MINIMUM_FONTSIZE = 6;
  public final static int LABEL_MAXIMUM_FONTSIZE = 63;
  public final static int LABEL_DEFAULT_FONTSIZE = 13;
  public final static int LABEL_DEFAULT_X_OFFSET = 4;
  public final static int LABEL_DEFAULT_Y_OFFSET = 4;

  public final static int MEASURE_DEFAULT_FONTSIZE = 15;
  public final static int AXES_DEFAULT_FONTSIZE = 14;

  
  
  
  
  
  
  

  public final static int SHAPE_BALLS      = 0;
  public final static int SHAPE_STICKS     = 1;
  public final static int SHAPE_HSTICKS    = 2;  
  public final static int SHAPE_SSSTICKS   = 3;  
  public final static int SHAPE_LABELS     = 4;
  public final static int SHAPE_MEASURES   = 5;
  public final static int SHAPE_DOTS       = 6;
  public final static int SHAPE_STARS      = 7;
  public final static int SHAPE_HALOS      = 8;

  public final static int SHAPE_MIN_SECONDARY = 9; 
  
    public final static int SHAPE_BACKBONE   = 9;
    public final static int SHAPE_TRACE      = 10;
    public final static int SHAPE_CARTOON    = 11;
    public final static int SHAPE_STRANDS    = 12;
    public final static int SHAPE_MESHRIBBON = 13;
    public final static int SHAPE_RIBBONS    = 14;
    public final static int SHAPE_ROCKETS    = 15;
  
  public final static int SHAPE_MAX_SECONDARY = 16; 
  public final static int SHAPE_MIN_SPECIAL    = 16; 

    public final static int SHAPE_DIPOLES    = 16;
    public final static int SHAPE_VECTORS    = 17;
    public final static int SHAPE_GEOSURFACE = 18;
    public final static int SHAPE_ELLIPSOIDS = 19;

  public final static int SHAPE_MAX_SIZE_ZERO_ON_RESTRICT = 20; 
  
    public final static int SHAPE_POLYHEDRA  = 20;  

  public final static int SHAPE_MIN_HAS_ID          = 21; 
  public final static int SHAPE_MIN_MESH_COLLECTION = 21; 
  
    public final static int SHAPE_DRAW        = 21;
  
  public final static int SHAPE_MAX_SPECIAL = 22; 
  public final static int SHAPE_MIN_SURFACE = 22; 

    public final static int SHAPE_ISOSURFACE  = 22;
    public final static int SHAPE_LCAOCARTOON = 23;
    public final static int SHAPE_MO          = 24;  
    public final static int SHAPE_PMESH       = 25;
    public final static int SHAPE_PLOT3D      = 26;

  public final static int SHAPE_MAX_SURFACE         = 27; 
  public final static int SHAPE_MAX_MESH_COLLECTION = 27; 
  
    public final static int SHAPE_ECHO       = 27;
  
  public final static int SHAPE_MAX_HAS_ID = 28;
  
  public final static int SHAPE_AXES       = 28;
  public final static int SHAPE_BBCAGE     = 29;
  public final static int SHAPE_UCCAGE     = 30;
  public final static int SHAPE_HOVER      = 31;
  
  
  public final static int SHAPE_FRANK      = 32;
  public final static int SHAPE_MAX        = SHAPE_FRANK + 1;

  public final static boolean isShapeSecondary(int i ) {
    return i >= JmolConstants.SHAPE_MIN_SECONDARY && i < JmolConstants.SHAPE_MAX_SECONDARY;
  }
  
  
  

  public final static String[] shapeClassBases = {
    "Balls", "Sticks", "Hsticks", "Sssticks",   
    "Labels", "Measures", "Dots", "Stars", "Halos",
    "Backbone", "Trace", "Cartoon", "Strands", "MeshRibbon", "Ribbons", "Rockets", 
    "Dipoles", "Vectors", "GeoSurface", "Ellipsoids", "Polyhedra", 
    "Draw", "Isosurface", "LcaoCartoon", "MolecularOrbital", "Pmesh", "Plot3D", 
    "Echo", "Axes", "Bbcage", "Uccage", "Hover", 
    "Frank"
     };
  static {
    if (shapeClassBases.length != SHAPE_MAX) {
      Logger.error("the shapeClassBases array has the wrong length");
      throw new NullPointerException();
    }
  }

  
  
  
  

  public final static int shapeTokenIndex(int tok) {
    switch (tok) {
    case Token.atoms:
      return SHAPE_BALLS;
    case Token.bonds:
    case Token.wireframe:
      return SHAPE_STICKS;
    case Token.hbond:
      return SHAPE_HSTICKS;
    case Token.ssbond:
      return SHAPE_SSSTICKS;
    case Token.label:
      return SHAPE_LABELS;
    case Token.monitor:
      return SHAPE_MEASURES;
    case Token.dots:
      return SHAPE_DOTS;
    case Token.star:
      return SHAPE_STARS;
    case Token.halo:
      return SHAPE_HALOS;
    case Token.backbone:
      return SHAPE_BACKBONE;
    case Token.trace:
      return SHAPE_TRACE;
    case Token.cartoon:
      return SHAPE_CARTOON;
    case Token.strands:
      return SHAPE_STRANDS;
    case Token.meshRibbon:
      return SHAPE_MESHRIBBON;
    case Token.ribbon:
      return SHAPE_RIBBONS;
    case Token.rocket:
      return SHAPE_ROCKETS;
    case Token.dipole:
      return SHAPE_DIPOLES;
    case Token.vector:
      return SHAPE_VECTORS;
    case Token.geosurface:
      return SHAPE_GEOSURFACE;
    case Token.ellipsoid:
      return SHAPE_ELLIPSOIDS;
    case Token.polyhedra:
      return SHAPE_POLYHEDRA;
    case Token.draw:
      return SHAPE_DRAW;
    case Token.isosurface:
      return SHAPE_ISOSURFACE;
    case Token.lcaocartoon:
      return SHAPE_LCAOCARTOON;
    case Token.mo:
      return SHAPE_MO;
    case Token.pmesh:
      return SHAPE_PMESH;
    case Token.plot3d:
      return SHAPE_PLOT3D;
    case Token.echo:
      return SHAPE_ECHO;
    case Token.axes:
      return SHAPE_AXES;
    case Token.boundbox:
      return SHAPE_BBCAGE;
    case Token.unitcell:
      return SHAPE_UCCAGE;
    case Token.hover:
      return SHAPE_HOVER;
    case Token.frank:
      return SHAPE_FRANK;
    }
    return -1;
  }
  
  public final static String getShapeClassName(int shapeID) {
    if (shapeID < 0)
      return shapeClassBases[~shapeID];
    return CLASSBASE_OPTIONS + "shape" 
        + (shapeID >= SHAPE_MIN_SECONDARY && shapeID < SHAPE_MAX_SECONDARY 
            ? "bio."
        : shapeID >= SHAPE_MIN_SPECIAL && shapeID < SHAPE_MAX_SPECIAL 
            ? "special." 
        : shapeID >= SHAPE_MIN_SURFACE && shapeID < SHAPE_MAX_SURFACE 
            ? "surface." 
        : ".") + shapeClassBases[shapeID];
  }

  
  
  

  public final static int ATOM_IN_FRAME    = 1;

  
  
  public final static int ATOM_SLABBED     = 2;

  public final static String PREVIOUS_MESH_ID = "+PREVIOUS_MESH+";

  
  
 
  public final static int getShapeVisibilityFlag(int shapeID) {
    return (4 << shapeID);
  }

  public final static int CARTOON_VISIBILITY_FLAG = getShapeVisibilityFlag(SHAPE_CARTOON);
  public final static int ALPHA_CARBON_VISIBILITY_FLAG = CARTOON_VISIBILITY_FLAG 
      | getShapeVisibilityFlag(SHAPE_TRACE)
      | getShapeVisibilityFlag(SHAPE_STRANDS)
      | getShapeVisibilityFlag(SHAPE_MESHRIBBON)
      | getShapeVisibilityFlag(SHAPE_RIBBONS);

  
  
  
  

  public final static int DEFAULT_STEREO_DEGREES = -5;

  public final static int STEREO_UNKNOWN  = -1;
  public final static int STEREO_NONE     = 0;
  public final static int STEREO_DOUBLE   = 1;
  public final static int STEREO_REDCYAN  = 2;
  public final static int STEREO_REDBLUE  = 3;
  public final static int STEREO_REDGREEN = 4;
  public final static int STEREO_CUSTOM   = 5;
  
  private final static String[] stereoModes = 
     { "OFF", "", "REDCYAN", "REDBLUE", "REDGREEN" };

  public static int getStereoMode(String id) {
    for (int i = 0; i < STEREO_CUSTOM; i++)
      if (id.equalsIgnoreCase(stereoModes[i]))
        return i;
    return STEREO_UNKNOWN;
  }

  static String getStereoModeName(int mode) {
    return stereoModes[mode];
  }
  
  
  
  static {
    if (argbsFormalCharge.length != FORMAL_CHARGE_MAX-FORMAL_CHARGE_MIN+1) {
      Logger.error("formal charge color table length");
      throw new NullPointerException();
    }
    if (shapeClassBases.length != SHAPE_MAX) {
      Logger.error("shapeClassBases wrong length");
      throw new NullPointerException();
    }
    if (argbsAmino.length != GROUPID_AMINO_MAX) {
      Logger.error("argbsAmino wrong length");
      throw new NullPointerException();
    }
    if (argbsShapely.length != GROUPID_WATER) {
      Logger.error("argbsShapely wrong length");
      throw new NullPointerException();
    }
    if (argbsChainHetero.length != argbsChainAtom.length) {
      Logger.error("argbsChainHetero wrong length");
      throw new NullPointerException();
    }
  }

  
  
  private final static String[][] shellOrder = { 
    {"S"},
    {"X", "Y", "Z"},
    {"S", "X", "Y", "Z"},
    {"XX", "YY", "ZZ", "XY", "XZ", "YZ"},
    {"d0", "d1+", "d1-", "d2+", "d2-"},
    {"XXX", "YYY", "ZZZ", "XYY", "XXY", "XXZ", "XZZ", "YZZ", "YYZ", "XYZ"},
    {"f0", "f1+", "f1-", "f2+", "f2-", "f3+", "f3-"}
  };

  final public static String[] getShellOrder(int i) {
    return shellOrder[i];
  }
  
  final public static int SHELL_S = 0;
  final public static int SHELL_P = 1;
  final public static int SHELL_SP = 2;
  final public static int SHELL_L = 2;
  
  
  
  final public static int SHELL_D_CARTESIAN = 3;
  final public static int SHELL_D_SPHERICAL = 4;
  final public static int SHELL_F_CARTESIAN = 5;
  final public static int SHELL_F_SPHERICAL = 6;

  final private static String[] quantumShellTags = {"S", "P", "SP", "L", 
    "D", "5D", "F", "7F"};
  
  final private static int[] quantumShellIDs = {
    SHELL_S, SHELL_P, SHELL_SP, SHELL_L, 
    SHELL_D_CARTESIAN, SHELL_D_SPHERICAL,
    SHELL_F_CARTESIAN, SHELL_F_SPHERICAL
  };
  
  public static final String LOAD_ATOM_DATA_TYPES = "xyz;vxyz;vibration;temperature;occupancy;partialcharge";

  public final static int ANIMATION_ONCE = 0;
  public final static int ANIMATION_LOOP = 1;
  public final static int ANIMATION_PALINDROME = 2;
  
  public final static float radiansPerDegree = (float) (Math.PI / 180);

  
  final public static int getQuantumShellTagID(String tag) {
    for (int i = quantumShellTags.length; --i >= 0;)
      if (tag.equals(quantumShellTags[i]))
        return quantumShellIDs[i];
    return -1;
  }

  final public static int getQuantumShellTagIDSpherical(String tag) {
    final int tagID = getQuantumShellTagID(tag);
    return tagID + (tagID < SHELL_D_CARTESIAN ? 0 : tagID % 2);
  }

  final public static String getQuantumShellTag(int shell) {
    for (int i = quantumShellTags.length; --i >= 0;)
      if (shell == quantumShellIDs[i])
        return quantumShellTags[i];
    return "" + shell;
  }
  
  final public static String canonicalizeQuantumSubshellTag(String tag) {
    char firstChar = tag.charAt(0);
    if (firstChar == 'X' || firstChar == 'Y' || firstChar == 'Z') {
      char[] sorted = tag.toCharArray();
      Arrays.sort(sorted);
      return new String(sorted);
    } 
    return tag;
  }
  
  final public static int getQuantumSubshellTagID(int shell, String tag) {
    for (int iSubshell = shellOrder[shell].length; --iSubshell >= 0; )
      if (shellOrder[shell][iSubshell].equals(tag))
        return iSubshell;
    return -1;
  }
  
  final public static String getQuantumSubshellTag(int shell, int subshell) {
    return shellOrder[shell][subshell];
  }

  public static int modelValue(String strDecimal) {
    
    
    
    int pt = strDecimal.indexOf(".");
    if (pt < 1 || strDecimal.charAt(0) == '-')
      return Integer.MAX_VALUE;
    int i = 0;
    int j = 0;
    if (pt > 0) {
      try {
        i = Integer.parseInt(strDecimal.substring(0, pt));
        if (i < 0)
          i = -i;
      } catch(NumberFormatException e) {
        i = -1;
      }
    }
    if (pt < strDecimal.length() - 1)
      try {
         j = Integer.parseInt(strDecimal.substring(pt + 1));
      } catch(NumberFormatException e) {
        
      }
    i = i * 1000000 + j;
    return (i < 0 ? Integer.MAX_VALUE : i);
  }

}
