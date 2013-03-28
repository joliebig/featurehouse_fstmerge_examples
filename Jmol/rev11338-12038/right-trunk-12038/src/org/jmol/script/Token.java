

package org.jmol.script;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.jmol.util.ArrayUtil;
import org.jmol.util.Logger;


public class Token {

  public int tok;
  Object value;
  public int intValue = Integer.MAX_VALUE;

  public Token() {
    
  }

  public Token(int tok, int intValue, Object value) {
    this.tok = tok;
    this.intValue = intValue;
    this.value = value;
  }
 
  
  
  public Token(int tok) {
    this.tok = tok;
  }

  public Token(int tok, int intValue) {
    this.tok = tok;
    this.intValue = intValue;
  }

  public Token(int tok, Object value) {
    this.tok = tok;
    this.value = value;
  }

  public final static Token intToken(int intValue) {
    return new Token(integer, intValue);
  }

  public final static int nada       =  0;
  public final static int integer    =  2;
  public final static int decimal    =  3;
  public final static int string     =  4;
  
  final static int seqcode           =  5;
  public final static int list       =  6;
  public final static int point3f    =  7;
  public final static int point4f    =  8;  
  public final static int bitset     =  9;
  
  public final static int matrix3f   = 10;  
  public final static int matrix4f   = 11;  
  
  
  final static int listf             = 12;     
  final private static int keyword   = 13;
  

  final static String[] astrType = {
    "nada", "identifier", "integer", "decimal", "string",
    "seqcode", "array", "point", "point4", "bitset",
    "matrix3f",  "matrix4f", "listf", "keyword"
  };

  public static boolean tokAttr(int a, int b) {
    return (a & b) == b;
  }
  
  public static boolean tokAttrOr(int a, int b1, int b2) {
    return (a & b1) == b1 || (a & b2) == b2;
  }
  
 

  
  
  
  
  
  
   
  
  
  
  
  final static int setparam          = (1 << 29); 
  final static int misc              = (1 << 30); 

  public final static int identifier =  misc;

  public final static int scriptCommand            = (1 << 12);
  
  
  
  final static int atomExpressionCommand  = (1 << 13) | scriptCommand;
  
  
  
  final static int implicitStringCommand     = (1 << 14) | scriptCommand;
  
  
  
  
  final static int mathExpressionCommand = (1 << 15) | scriptCommand;
  
  
  
  
  final static int flowCommand        = (1 << 16) | mathExpressionCommand;
  
  
  
  
  
  
  
  final static int noeval         = (1 << 17);
  final static int noArgs         = (1 << 18);
  final static int defaultON      = (1 << 19);
  
  final static int expression           = (1 << 20);
  final static int predefinedset        = (1 << 21) | expression;
  
  public final static int atomproperty  = (1 << 22) | expression | misc; 
  
  
  public final static int strproperty   = (1 << 23) | atomproperty; 
  public final static int intproperty   = (1 << 24) | atomproperty; 
  public final static int floatproperty = (1 << 25) | atomproperty; 

  public final static int PROPERTYFLAGS = strproperty | intproperty | floatproperty;
  
  final static int mathproperty         = (1 << 26) | expression | misc; 
  final static int mathfunc             = (1 << 27) | expression;  
  final static int mathop               = (1 << 28) | expression;
  final static int comparator           = mathop | (1 << 8);
  
  public final static int center       = 1 | atomExpressionCommand;
  public final static int define       = 2 | atomExpressionCommand | expression | setparam;
  public final static int delete       = 3 | atomExpressionCommand;
  final static int display      = 4 | atomExpressionCommand | setparam;
  final static int hide         = 5 | atomExpressionCommand;
  final static int restrict     = 6 | atomExpressionCommand;

  final static int subset       = 7 | atomExpressionCommand | predefinedset;
  final static int zap          = 8 | atomExpressionCommand | expression;

  final static int print        = 1 | mathExpressionCommand;
  final static int returncmd    = 2 | mathExpressionCommand;
  final static int set          = 3 | mathExpressionCommand | expression;
  final static int var          = 4 | mathExpressionCommand | noeval | setparam;

  public final static int echo  = 1 | implicitStringCommand | setparam;
  final static int help         = 2 | implicitStringCommand;
  public final static int hover = 3 | implicitStringCommand | defaultON;


  final static int message      = 4 | implicitStringCommand;
  public final static int pause = 5 | implicitStringCommand;

  
  


  final static int elseif       = 2 | flowCommand;
  final static int elsecmd      = 3 | flowCommand | noArgs;
  final static int endifcmd     = 4 | flowCommand | noArgs;

  final static int whilecmd     = 6 | flowCommand;
  final static int breakcmd     = 7 | flowCommand;
  final static int continuecmd  = 8 | flowCommand;
  final static int end          = 9 | flowCommand | noeval | expression;
  
  final static int animation    = scriptCommand | 1;
  public final static int axes         = scriptCommand | 2 | setparam | defaultON;
  final static int background   = scriptCommand | 3 | setparam;
  final static int bind         = scriptCommand | 4;
  final static int bondorder    = scriptCommand | 5;

  final static int calculate    = scriptCommand | 6;
  final static int cd           = scriptCommand | 7 | implicitStringCommand | expression;
  final static int centerAt     = scriptCommand | 8;

  final static int configuration = scriptCommand | 9;
  public final static int connect = scriptCommand | 10;
  final static int console      = scriptCommand | 11 | defaultON;

  final static int delay        = scriptCommand | 12 | defaultON;
  final static int depth        = scriptCommand | 13 | defaultON;
  public final static int dipole       = scriptCommand | 14;
  public final static int draw         = scriptCommand | 16;
  final static int exit         = scriptCommand | 17 | noArgs;
  final static int exitjmol     = scriptCommand | 18 | noArgs;

  final static int font         = scriptCommand | 19;
  final static int frame        = scriptCommand | 20;
  public final static int frank        = scriptCommand | 21 | setparam | defaultON;

  final static int gotocmd      = scriptCommand | 23 | implicitStringCommand;
  public final static int hbond        = scriptCommand | 25 | setparam | expression | defaultON;
  final static int history      = scriptCommand | 26 | setparam;
  final static int initialize   = scriptCommand | 27 | noArgs;
  final static int invertSelected = scriptCommand | 28;
  public final static int isosurface   = scriptCommand | 29;
  public final static int lcaocartoon  = scriptCommand | 30;

  final static int loop         = scriptCommand | 31 | defaultON;
  final static int minimize     = scriptCommand | 32;
  public final static int mo           = scriptCommand | 33 | expression;

  public final static int monitor      = scriptCommand | 34 | setparam | expression | defaultON;
  final static int move         = scriptCommand | 35;
  public final static int moveto       = scriptCommand | 36;
  final static int navigate     = scriptCommand | 37;
  public final static int pmesh        = scriptCommand | 38;
  public final static int plot3d       = scriptCommand | 39;
  public final static int polyhedra    = scriptCommand | 40;

  final static int quit         = scriptCommand | 41 | noArgs;
  final static int ramachandran = scriptCommand | 42 | expression;
  final static int refresh      = scriptCommand | 43 | noArgs;
  final static int reset        = scriptCommand | 44;
  final static int restore      = scriptCommand | 45;
  public final static int resume       = scriptCommand | 46 | noArgs;
  final static int rotate       = scriptCommand | 48 | defaultON;
  final static int rotateSelected = scriptCommand | 49;
  final static int save         = scriptCommand | 50;

  final static int selectionHalo = scriptCommand | 51 | setparam | defaultON;
  final static int show         = scriptCommand | 52;
  final static int slab         = scriptCommand | 53 | defaultON;
  
  final static int spin         = scriptCommand | 55 | setparam | defaultON;
  public final static int ssbond       = scriptCommand | 56 | setparam | defaultON;
  final static int step         = scriptCommand | 57 | noArgs;
  final static int stereo       = scriptCommand | 58 | defaultON;

  final static int sync         = scriptCommand | 60;
  final static int translate    = scriptCommand | 61;
  final static int translateSelected = scriptCommand | 62;
  final static int unbind       = scriptCommand | 63;
  public final static int unitcell = scriptCommand | 64 | setparam | expression | predefinedset | defaultON;
  public final static int vector       = scriptCommand | 65;
  public final static int vibration    = scriptCommand | 66;
  public final static int wireframe    = scriptCommand | 67 | defaultON;
  
  final static int zoom         = scriptCommand | 68;
  final static int zoomTo       = scriptCommand | 69;


  

  
  
  
  
  final static int expressionBegin     = expression | 1;
  final static int expressionEnd       = expression | 2;
  final static int all                 = expression | 3;
  public final static int branch       = expression | 4;
  final static int coord               = expression | 6;
  final static int dollarsign          = expression | 7;
  final static int per                 = expression | 8;
  public final static int isaromatic   = expression | 9;
  final static int leftbrace           = expression | 10;
  final static int none                = expression | 11;
  public final static int off          = expression | 12; 
  public final static int on           = expression | 13; 
  final static int rightbrace          = expression | 14;
  final static int semicolon           = expression | 15;

  
  
  public final static int spec_alternate       = expression | 31;
  public final static int spec_atom            = expression | 32;
  public final static int spec_chain           = expression | 33;
  public final static int spec_model           = expression | 34;  
  final static int spec_model2                 = expression | 35;  
  public final static int spec_name_pattern    = expression | 36;
  public final static int spec_resid           = expression | 37;
  public final static int spec_seqcode         = expression | 38;
  public final static int spec_seqcode_range   = expression | 39;

  final static int amino                = predefinedset | 1;
  public final static int dna           = predefinedset | 2;
  public final static int hetero        = predefinedset | 3 | setparam;
  public final static int hydrogen      = predefinedset | 4 | setparam;
  public final static int nucleic       = predefinedset | 5;
  public final static int protein       = predefinedset | 6;
  public final static int purine        = predefinedset | 7;
  public final static int pyrimidine    = predefinedset | 8;
  public final static int rna           = predefinedset | 9;
  public final static int selected      = predefinedset | 10;
  final static int solvent              = predefinedset | 11 | setparam;
  public final static int sidechain     = predefinedset | 12;
  final static int surface              = predefinedset | 13;
  final static int thismodel            = predefinedset | 14;
  public final static int sheet         = predefinedset | 15;

  
  public final static int carbohydrate    = predefinedset | 21;
  final static int clickable              = predefinedset | 22;
  final static int displayed              = predefinedset | 23;
  final static int hidden                 = predefinedset | 24;
  public final static int specialposition = predefinedset | 25;
  final static int visible                = predefinedset | 27;

  
  static int getPrecedence(int tokOperator) {
    return ((tokOperator >> 4) & 0xF);  
  }


  final static int leftparen    = 0 | mathop | 1 << 4;
  final static int rightparen   = 1 | mathop | 1 << 4;

  final static int opIf         = 1 | mathop | 2 << 4 | setparam;
  final static int colon        = 2 | mathop | 2 << 4;

  final static int comma        = 0 | mathop | 3 << 4;

  final static int leftsquare   = 0 | mathop | 4 << 4;
  final static int rightsquare  = 1 | mathop | 4 << 4;

  final static int opOr         = 0 | mathop | 5 << 4;
  final static int opXor        = 1 | mathop | 5 << 4;
  public final static int opToggle = 2 | mathop | 5 << 4;

  final static int opAnd        = 0 | mathop | 6 << 4;
 
  final static int opNot        = 0 | mathop | 7 << 4;
 
  final static int opGT         = 0 | comparator | 8 << 4;
  final static int opGE         = 1 | comparator | 8 << 4;
  final static int opLE         = 2 | comparator | 8 << 4;
  final static int opLT         = 3 | comparator | 8 << 4;
  public final static int opEQ  = 4 | comparator | 8 << 4;
  final static int opNE         = 5 | comparator | 8 << 4;
   
  final static int minus        = 0 | mathop | 9 << 4;
  final static int plus         = 1 | mathop | 9 << 4;
 
  final static int divide         = 0 | mathop | 10 << 4;
  final static int times          = 1 | mathop | 10 << 4;
  public final static int percent = 2 | mathop | 10 << 4;
  final static int leftdivide     = 3 | mathop | 10 << 4;  
  
  final static int unaryMinus   = 0 | mathop | 11 << 4;
  final static int minusMinus   = 1 | mathop | 11 << 4;
  final static int plusPlus     = 2 | mathop | 11 << 4;
  
  
  final static int propselector = 1 | mathop | 12 << 4;

  final static int andequals    = 2 | mathop | 12 << 4;

  
  
  
  
  
  
  
  
  
  
  
  final static int minmaxmask  = 0xF << 5; 
  public final static int min         = 1 << 5;
  public final static int max         = 2 << 5;
  public final static int average     = 3 << 5;
  public final static int sum         = 4 << 5;
  public final static int sum2        = 5 << 5;
  public final static int stddev      = 6 << 5;
  public final static int allfloat    = 7 << 5; 

  final static int settable           = 1 << 11;
  
  
  
  
  
    
  public final static int atoms     = 1 | mathproperty;
  public final static int bonds     = 2 | mathproperty | setparam;
  final static int length           = 3 | mathproperty;
  final static int lines            = 4 | mathproperty;
  public final static int reverse   = 5 | mathproperty;
  final static int size             = 6 | mathproperty;
  public final static int sort      = 7 | mathproperty;
  public final static int type      = 8 | mathproperty;
  public final static int boundbox  = 9 | mathproperty | setparam | scriptCommand | defaultON;
  public final static int xyz       =10 | mathproperty | atomproperty | settable;
  public final static int fracXyz   =11 | mathproperty | atomproperty | settable;
  public final static int unitXyz   =12 | mathproperty | atomproperty;
  public final static int vibXyz    =13 | mathproperty | atomproperty | settable;
  
  
  
  public final static int occupancy     = intproperty | floatproperty | 2 | settable;
  public final static int radius        = intproperty | floatproperty | 1 | setparam | settable;
  public final static int structure     = intproperty | strproperty   | 3 | scriptCommand;

  
  
  
  public final static int atomType      = strproperty | 1 | settable;
  public final static int atomName      = strproperty | 2 | settable;
  public final static int altloc        = strproperty | 3;
  public final static int chain         = strproperty | 4;
  public final static int element       = strproperty | 5 | settable;
  public final static int group         = strproperty | 6;
  public final static int group1        = strproperty | 7;
  public final static int sequence      = strproperty | 8;
  public final static int identify      = strproperty | 9;
  public final static int insertion     = strproperty |10;
  public final static int strucid       = strproperty |11;
  public final static int symbol        = strproperty |12 | settable;
  public final static int symmetry      = strproperty |13 | predefinedset;

  public final static int atomno        = intproperty | 1;
  public final static int atomID        = intproperty | 2;
  public final static int atomIndex     = intproperty | 3;
  public final static int bondcount     = intproperty | 4;
  public final static int cell          = intproperty | 5;
  public final static int color         = intproperty | 6 | scriptCommand | setparam | settable;
  public final static int elemno        = intproperty | 7 | settable;
  
  public final static int formalCharge  = intproperty | 8 | setparam | settable;
  public final static int groupID       = intproperty | 9;
  public final static int groupindex    = intproperty | 10;
  public final static int model         = intproperty | 11 | scriptCommand;
  public final static int modelindex    = intproperty | 12;
  public final static int molecule      = intproperty | 13;
  public final static int polymerLength = intproperty | 14;
  public final static int resno         = intproperty | 15;
  public final static int site          = intproperty | 16;
  public final static int strucno       = intproperty | 18;
  public final static int valence       = intproperty | 20 | settable;

  
  
  public final static int adpmax          = floatproperty | 1;
  public final static int adpmin          = floatproperty | 2;
  public final static int atomX           = floatproperty | 3 | settable;
  public final static int atomY           = floatproperty | 4 | settable;
  public final static int atomZ           = floatproperty | 5 | settable;
  public final static int covalent        = floatproperty | 5;
  public final static int fracX           = floatproperty | 6 | settable;
  public final static int fracY           = floatproperty | 7 | settable;
  public final static int fracZ           = floatproperty | 8 | settable;
  public final static int ionic           = floatproperty | 9;
  public final static int partialCharge   = floatproperty | 10 | settable;
  public final static int phi             = floatproperty | 11;
  public final static int psi             = floatproperty | 12;
  public final static int property        = floatproperty | 13 | mathproperty | setparam | settable;
  public final static int straightness    = floatproperty | 15;
  public final static int surfacedistance = floatproperty | 16;
  public final static int temperature     = floatproperty | 17 | settable;
  public final static int unitX           = floatproperty | 18;
  public final static int unitY           = floatproperty | 19;
  public final static int unitZ           = floatproperty | 20;
  public final static int vanderwaals     = floatproperty | 21 | settable;
  public final static int vibX            = floatproperty | 22 | settable;
  public final static int vibY            = floatproperty | 23 | settable;
  public final static int vibZ            = floatproperty | 24 | settable;
  
  public final static int backbone     = floatproperty | scriptCommand | 1 | predefinedset | defaultON | settable;
  public final static int cartoon      = floatproperty | scriptCommand | 2 | defaultON | settable;
  public final static int dots         = floatproperty | scriptCommand | 3 | defaultON;
  public final static int ellipsoid    = floatproperty | scriptCommand | 4 | defaultON;
  public final static int geosurface   = floatproperty | scriptCommand | 5 | defaultON;
  public final static int halo         = floatproperty | scriptCommand | 6 | defaultON | settable;
  public final static int meshRibbon   = floatproperty | scriptCommand | 7 | defaultON | settable;
  public final static int ribbon       = floatproperty | scriptCommand | 9 | defaultON | settable;
  public final static int rocket       = floatproperty | scriptCommand | 10 | defaultON | settable;
  public final static int spacefill    = floatproperty | scriptCommand | 11 | defaultON | settable;
  public final static int star         = floatproperty | scriptCommand | 12 | defaultON | settable;
  public final static int strands      = floatproperty | scriptCommand | 13 | setparam | defaultON | settable;
  public final static int trace        = floatproperty | scriptCommand | 14 | defaultON | settable;

  
  
  
  
  
  


  static int getMaxMathParams(int tokCommand) {
    return  ((tokCommand >> 9) & 0x7);
  }

  
  

  
  
  public final static int array  = 1 | 0 << 9 | mathfunc;
  public final static int format = 2 | 0 << 9 | mathfunc | mathproperty | strproperty | settable;
  public final static int label  = 3 | 0 << 9 | mathfunc | mathproperty | strproperty | settable | implicitStringCommand | defaultON | setparam;
  final static int function      = 4 | 0 << 9 | mathfunc | flowCommand | noeval;
  final static int getproperty   = 5 | 0 << 9 | mathfunc | scriptCommand;
  final static int write         = 6 | 0 << 9 | mathfunc | scriptCommand;

  
  
  public final static int angle = 7 | 0 << 9 | mathfunc;
  public final static int data  = 8 | 0 << 9 | mathfunc | scriptCommand;
  public final static int plane = 9 | 0 << 9 | mathfunc;
  public final static int point = 10 | 0 << 9 | mathfunc;
  final static int quaternion   = 11 | 0 << 9 | mathfunc | scriptCommand;
  final static int axisangle    = 12 | 0 << 9 | mathfunc;

  
  
  final static int within           = 13 | 0 << 9 | mathfunc;
  public final static int connected = 14 | 0 << 9 | mathfunc;
  public final static int helix     = 15 | 0 << 9 | mathfunc | predefinedset;

  final static int now          = 16 | 0 << 9 | mathfunc;

  
  
  final static int substructure = 1 | 1 << 9 | mathfunc;
  final static int javascript   = 2 | 1 << 9 | mathfunc | implicitStringCommand;
  final static int acos         = 3 | 1 << 9 | mathfunc;
  final static int sin          = 4 | 1 << 9 | mathfunc;
  final static int cos          = 5 | 1 << 9 | mathfunc;
  final static int sqrt         = 6 | 1 << 9 | mathfunc;
  public final static int file  = 7 | 1 << 9 | mathfunc | intproperty | scriptCommand;
  final static int forcmd       = 8 | 1 << 9 | mathfunc | flowCommand;
  final static int ifcmd        = 9 | 1 << 9 | mathfunc | flowCommand;
  final static int abs          = 10 | 1 << 9 | mathfunc;

  
  
  
  
  
  final static int div          = 0 | 1 << 9 | mathfunc | mathproperty;
  final static int dot          = 1 | 1 << 9 | mathfunc | mathproperty;
  final static int join         = 2 | 1 << 9 | mathfunc | mathproperty;
  final static int mul          = 3 | 1 << 9 | mathfunc | mathproperty;
  final static int split        = 4 | 1 << 9 | mathfunc | mathproperty;
  final static int sub          = 5 | 1 << 9 | mathfunc | mathproperty;
  final static int trim         = 6 | 1 << 9 | mathfunc | mathproperty;  
  public final static int volume = 7 | 1 << 9 | mathfunc | mathproperty | floatproperty;  
  final static int col           = 8 | 1 << 9 | mathfunc | mathproperty;
  final static int row           = 9 | 1 << 9 | mathfunc | mathproperty;
  
  
  public final static int cross = 1 | 2 << 9 | mathfunc;
  final static int load         = 2 | 2 << 9 | mathfunc | scriptCommand;
  final static int random       = 3 | 2 << 9 | mathfunc;
  final static int script       = 4 | 2 << 9 | mathfunc | scriptCommand;

  

  
  
  
  
  
  
  final static int add          = 1 | 2 << 9 | mathfunc | mathproperty;
  final static int distance     = 2 | 2 << 9 | mathfunc | mathproperty;
  final static int replace      = 3 | 2 << 9 | mathfunc | mathproperty;
  final static int find         = 4 | 2 << 9 | mathfunc | mathproperty;

  
  
  final static int select       = 1 | 3 << 9 | mathfunc | atomExpressionCommand;

  
  
  final static int bin          = 1 | 3 << 9 | mathfunc | mathproperty;
  public final static int symop = 2 | 3 << 9 | mathfunc | mathproperty | intproperty; 

  
  
  
  
  public final static int ambient = setparam |  1;
  final static int bondmode       = setparam |  2;
  final static int fontsize       = setparam |  3;
  final static int picking        = setparam |  4;
  final static int specular       = setparam |  5;
  final static int specpercent    = setparam |  6;  
  final static int specpower      = setparam |  7;
  final static int specexponent   = setparam |  8;
  final static int transparent    = setparam |  9;
  final static int defaultColors  = setparam | 10;
  final static int scale3d        = setparam | 11;
  final static int diffuse        = setparam | 12;
  final static int pickingStyle   = setparam | 13;

  

  final static int absolute      = misc | 1;
  final static int addhydrogens  = misc | 2;
  final static int adjust        = misc | 3;
  final static int align         = misc | 4;
  final static int allconnected  = misc | 5;
  final static int angstroms     = misc | 6;
  final static int anisotropy    = misc | 7;
  final static int arc           = misc | 9 | expression;
  final static int area          = misc | 10;
  final static int aromatic      = misc | 11 | predefinedset;
  final static int arrow         = misc | 12;
  final static int atomicorbital = misc | 13;
  public final static int auto   = misc | 14;
  public final static int axis   = misc | 16;
  final static int babel         = misc | 17;
  final static int back          = misc | 18;
  public final static int backlit = misc | 19;
  final static int binary        = misc | 20;
  final static int blockdata     = misc | 21;
  final static int bondset       = misc | 22;
  final static int bottom        = misc | 23;
  final static int cap           = misc | 24 | expression;
  final static int cavity        = misc | 25;
  final static int check         = misc | 26;
  final static int circle        = misc | 27;
  final static int clear         = misc | 28;
  final static int clipboard     = misc | 29;
  final static int collapsed     = misc | 31;
  final static int colorscheme   = misc | 32;
  final static int command       = misc | 33;
  final static int commands      = misc | 34;
  final static int constraint    = misc | 35;
  final static int contour       = misc | 36;
  public final static int contourlines  = misc | 37;
  final static int contours      = misc | 38;
  final static int corners       = misc | 39;
  public final static int create = misc | 40;
  final static int crossed       = misc | 41;
  final static int curve         = misc | 42;
  final static int cutoff        = misc | 43;
  final static int cylinder      = misc | 44;
  final static int debug         = misc | 45 | setparam;
  final static int diameter       = misc | 50;
  final static int direction      = misc | 52;
  final static int discrete       = misc | 53;
  final static int displacement   = misc | 54;
  final static int distancefactor = misc | 55;
  final static int dotted         = misc | 56;
  final static int downsample     = misc | 57;
  final static int eccentricity   = misc | 58;
  final static int ed             = misc | 59 | expression;
  final static int edges          = misc | 60;
  final static int facecenteroffset = misc | 62;
  public final static int fill    = misc | 63;
  final static int filter         = misc | 64;
  public final static int first   = misc | 65;
  final static int fixed          = misc | 66;
  final static int fixedtemp      = misc | 67;
  final static int flat           = misc | 68;
  final static int fps            = misc | 69 | expression;
  final static int from           = misc | 70;
  public final static int front   = misc | 71;
  final static int frontedges     = misc | 72;
  public final static int frontlit  = misc | 73;
  public final static int frontonly = misc | 74;
  final static int fullplane        = misc | 75;
  public final static int fullylit  = misc | 76;
  final static int functionxy     = misc | 77;
  final static int functionxyz    = misc | 78;
  final static int gridpoints     = misc | 79;
  final static int hkl            = misc | 81 | expression;
  final static int homo           = misc | 82;
  final static int id             = misc | 83 | expression;
  final static int ignore         = misc | 84;
  final static int image          = misc | 85;
  final static int increment      = misc | 86;
  public final static int info    = misc | 87;
  final static int insideout      = misc | 89;
  final static int interior       = misc | 90;
  final static int internal       = misc | 91;
  final static int intersection   = misc | 92;
  public final static int jmol    = misc | 93;
  public final static int last    = misc | 94;
  final static int left           = misc | 95;
  final static int line           = misc | 96;
  final static int linedata       = misc | 97;
  final static int lobe           = misc | 98;
  final static int lonepair       = misc | 99;
  final static int lp             = misc | 100;
  final static int lumo           = misc | 101;
  final static int manifest       = misc | 102;
  final static int map            = misc | 103 | expression;
  final static int maxset         = misc | 104;
  final static int mep            = misc | 106 | expression;
  public final static int mesh    = misc | 107;
  final static int minset         = misc | 108;
  final static int mode           = misc | 109;
  public final static int modify         = misc | 110;
  public final static int modifyorcreate = misc | 111;
  final static int modelbased     = misc | 112;
  final static int molecular      = misc | 113;
  final static int monomer        = misc | 114;
  public final static int next    = misc | 115;
  public final static int nocontourlines  = misc | 116;
  final static int nocross        = misc | 117;
  final static int nodebug        = misc | 118;
  public final static int nodots  = misc | 119;
  final static int noedges        = misc | 120;
  public final static int nofill  = misc | 121;
  final static int nohead         = misc | 122;
  final static int noload         = misc | 123;
  public final static int nomesh  = misc | 124;
  final static int noplane        = misc | 125;
  final static int normal         = misc | 126;
  public final static int notfrontonly  = misc | 127;
  public final static int notriangles   = misc | 128;
  final static int obj            = misc | 129;
  final static int object         = misc | 130;
  final static int offset         = misc | 131;
  final static int offsetside     = misc | 132;
  final static int once           = misc | 133;
  final static int only           = misc | 134;
  final static int opaque         = misc | 135;
  final static int orbital        = misc | 136;
  final static int orientation    = misc | 137;
  final static int packed         = misc | 138;
  final static int palindrome     = misc | 139;
  final static int path           = misc | 140;
  final static int pdb            = misc | 141 | expression;
  final static int pdbheader      = misc | 142;
  final static int period         = misc | 143;
  final static int perp           = misc | 144;
  final static int perpendicular  = misc | 145;
  final static int phase          = misc | 146;
  public final static int play    = misc | 147;
  public final static int playrev = misc | 148;
  final static int pocket         = misc | 149;
  final static int pointgroup     = misc | 150;
  final static int pointsperangstrom = misc | 151;
  final static int polymer        = misc | 152;
  public final static int prev    = misc | 153;
  final static int qw             = misc | 154 | expression;
  final static int rad            = misc | 155;
  final static int radical        = misc | 156;
  final static int range          = misc | 157;
  public final static int rasmol  = misc | 158;
  final static int reference      = misc | 159;
  public final static int residue = misc | 161;
  final static int resolution     = misc | 162;
  final static int reversecolor   = misc | 163;
  public final static int rewind  = misc | 164;
  final static int right          = misc | 165;
  final static int rotate45       = misc | 166;
  public final static int rotation = misc | 167;
  final static int rubberband     = misc | 169;
  final static int sasurface      = misc | 170;
  final static int scale          = misc | 171;
  final static int selection      = misc | 172;
  final static int shape          = misc | 173;
  final static int shapely        = misc | 174;
  final static int sign           = misc | 177;
  final static int solid          = misc | 178;
  final static int spacegroup     = misc | 179;
  final static int sphere         = misc | 180;
  final static int squared        = misc | 181;
  final static int state          = misc | 182;
  final static int steps          = misc | 183;
  final static int stop           = misc | 184;
  final static int ticks          = misc | 185; 
  final static int title          = misc | 186;
  final static int titleformat    = misc | 187;
  final static int to             = misc | 188 | expression;
  final static int top            = misc | 189 | expression;
  final static int torsion        = misc | 190;
  final static int transform      = misc | 193;
  public final static int translation   = misc | 194;
  public final static int translucent   = misc | 195;
  public final static int triangles     = misc | 196;
  final static int url             = misc | 197 | expression;
  final static int user            = misc | 198;
  final static int val             = misc | 200;
  final static int variable        = misc | 201;
  final static int variables       = misc | 202;
  final static int vertices        = misc | 203;
  final static int width           = misc | 204;
  final static int cancel          = misc | 205;
  final static int fix             = misc | 206;
  final static int energy          = misc | 207;
  final static int criterion       = misc | 208;
  final static int in              = misc | 209;
  final static int out             = misc | 210;
  
  
  
  
  final static Token tokenOn  = new Token(on, 1, "on");
  final static Token tokenOff = new Token(off, 0, "off");
  final static Token tokenAll = new Token(all, "all");
  final static Token tokenIf = new Token(ifcmd, "if");
  public final static Token tokenAnd = new Token(opAnd, "and");
  public final static Token tokenOr  = new Token(opOr, "or");
  public final static Token tokenOpIf  = new Token(opIf, "?");
  public final static Token tokenComma = new Token(comma, ",");
  final static Token tokenPlus = new Token(plus, "+");
  final static Token tokenMinus = new Token(minus, "-");
  final static Token tokenTimes = new Token(times, "*");
  final static Token tokenDivide = new Token(divide, "/");

  public final static Token tokenLeftParen = new Token(leftparen, "(");
  public final static Token tokenRightParen = new Token(rightparen, ")");
  final static Token tokenArray = new Token(array, "[");
  final static Token tokenArraySelector = new Token(leftsquare, "[");
 
  public final static Token tokenExpressionBegin = new Token(expressionBegin, "expressionBegin");
  public final static Token tokenExpressionEnd   = new Token(expressionEnd, "expressionEnd");
  final static Token tokenCoordinateBegin = new Token(leftbrace, "{");
  final static Token tokenCoordinateEnd   = new Token(rightbrace, "}");
  final static Token tokenColon           = new Token(colon, ':');
  final static Token tokenSet             = new Token(set, '=', "");
  final static Token tokenSetArray        = new Token(set, '[', "");
  final static Token tokenSetProperty     = new Token(set, '.', "");
  final static Token tokenSetVar          = new Token(set, '=', "var");
  final static Token tokenEquals          = new Token(opEQ, "=");
    
  

  final private static Object[] arrayPairs  = {
    
    "animation",         new Token(animation),
    "anim",              null,
    "axes",              new Token(axes),
    "backbone",          new Token(backbone),
    "background",        new Token(background),
    "bind",              new Token(bind),
    "bondorder",         new Token(bondorder),
    "boundbox",          new Token(boundbox),
    "break",             new Token(breakcmd),
    "calculate",         new Token(calculate),
    "cartoon",           new Token(cartoon),
    "cartoons",          null,
    "center",            new Token(center),
    "centre",            null,
    "centerat",          new Token(centerAt),
    "color",             new Token(color),
    "colour",            null,
    "configuration",     new Token(configuration),
    "conformation",      null,
    "config",            null,
    "connect",           new Token(connect),
    "console",           new Token(console),
    "continue",          new Token(continuecmd),
    "data",              new Token(data),
    "define",            new Token(define),
    "@",                 null,
    "delay",             new Token(delay),
    "delete",            new Token(delete),
    "depth",             new Token(depth),
    "dipole",            new Token(dipole),
    "dipoles",           null,
    "cd",                new Token(cd),
    "display",           new Token(display),
    "dot",               new Token(dot),
    "dots",              new Token(dots),
    "draw",              new Token(draw),
    "echo",              new Token(echo),
    "ellipsoid",         new Token(ellipsoid),
    "ellipsoids",        null,
    "else",              new Token(elsecmd),
    "elseif",            new Token(elseif),
    "end",               new Token(end),
    "endif",             new Token(endifcmd),
    "exit",              new Token(exit),
    "file",              new Token(file),
    "font",              new Token(font),
    "for",               new Token(forcmd),
    "format",            new Token(format),
    "frame",             new Token(frame),
    "frames",            null,
    "frank",             new Token(frank),
    "function",          new Token(function),
    "functions",         null,
    "geosurface",        new Token(geosurface),
    "getproperty",       new Token(getproperty),
    "goto",              new Token(gotocmd),
    "halo",              new Token(halo),
    "halos",             null,
    "helix",             new Token(helix),
    "hbond",             new Token(hbond),
    "hbonds",            null,
    "help",              new Token(help),
    "hide",              new Token(hide),
    "history",           new Token(history),
    "hover",             new Token(hover),
    "if",                new Token(ifcmd),
    "in",                new Token(in),
    "initialize",        new Token(initialize),
    "invertSelected",    new Token(invertSelected),
    "isosurface",        new Token(isosurface),
    "javascript",        new Token(javascript),
    "label",             new Token(label),
    "labels",            null,
    "lcaocartoon",       new Token(lcaocartoon),
    "lcaocartoons",      null,
    "load",              new Token(load),
    "loop",              new Token(loop),
    "measure",           new Token(monitor),
    "measurement",       null,
    "measurements",      null,
    "measures",          null,
    "monitor",           null,
    "monitors",          null,
    "meshribbon",        new Token(meshRibbon),
    "meshribbons",       null,
    "message",           new Token(message),
    "minimize",          new Token(minimize),
    "minimization",      null,
    "mo",                new Token(mo),
    "model",             new Token(model),
    "models",            null,
    "move",              new Token(move),
    "moveto",            new Token(moveto),
    "navigate",          new Token(navigate),
    "navigation",        null,
    "out",               new Token(out),
    "pause",             new Token(pause),
    "wait",              null,
    "plot3d",            new Token(plot3d),
    "pmesh",             new Token(pmesh),
    "polyhedra",         new Token(polyhedra),
    "print",             new Token(print),
    "quaternion",        new Token(quaternion),
    "quaternions",       null,
    "quit",              new Token(quit),
    "ramachandran",      new Token(ramachandran),
    "rama",              null,
    "refresh",           new Token(refresh),
    "reset",             new Token(reset),
    "restore",           new Token(restore),
    "restrict",          new Token(restrict),
    "return",            new Token(returncmd),
    "ribbon",            new Token(ribbon),
    "ribbons",           null,
    "rocket",            new Token(rocket),
    "rockets",           null,
    "rotate",            new Token(rotate),
    "rotateSelected",    new Token(rotateSelected),
    "save",              new Token(save),
    "script",            new Token(script),
    "source",            null,
    "select",            new Token(select),
    "selectionHalos",    new Token(selectionHalo),
    "selectionHalo",     null,
    "set",               new Token(set),
    "show",              new Token(show),
    "slab",              new Token(slab),
    "spacefill",         new Token(spacefill),
    "cpk",               null,
    "spin",              new Token(spin),
    "ssbond",            new Token(ssbond),
    "ssbonds",           null,
    "star",              new Token(star),
    "stars",             null,
    "step",              new Token(step),
    "stereo",            new Token(stereo),
    "strand",            new Token(strands),
    "strands",           null,
    "structure",         new Token(structure),
    "_structure",        null,
    "strucNo",           new Token(strucno),
    "subset",            new Token(subset),
    "synchronize",       new Token(sync),
    "sync",              null,
    "trace",             new Token(trace),
    "translate",         new Token(translate),
    "translateSelected", new Token(translateSelected),
    "unbind",            new Token(unbind),
    "unitcell",          new Token(unitcell),
    "var",               new Token(var),
    "vector",            new Token(vector),
    "vectors",           null,
    "vibration",         new Token(vibration),
    "while",             new Token(whilecmd),
    "wireframe",         new Token(wireframe),
    "write",             new Token(write),
    "zap",               new Token(zap),
    "zoom",              new Token(zoom),
    "zoomTo",            new Token(zoomTo),
                          
    
    
    "bondmode",          new Token(bondmode),
    "bonds",             new Token(bonds),
    "bond",              null, 
    "fontsize",          new Token(fontsize),
    "picking",           new Token(picking),
    "pickingStyle",      new Token(pickingStyle),
    "radius",            new Token(radius),
    "scale",             new Token(scale),
    "scale3D",           new Token(scale3d),
                          
    
                          
    "ambientPercent",    new Token(ambient),
    "ambient",           null, 
    "diffusePercent",    new Token(diffuse),
    "diffuse",           null, 
    "specular",          new Token(specular),
    "specularPercent",   new Token(specpercent),
    "specularPower",     new Token(specpower),
    "specpower",         null, 
    "specularExponent",  new Token(specexponent),
                                                
    

    "atom",              new Token(atoms),
    "atoms",             null,
    "axis",              new Token(axis),
    "axisangle",         new Token(axisangle),
    "orientation",       new Token(orientation),
    "pdbheader",         new Token(pdbheader),                          
    "polymer",           new Token(polymer),
    "polymers",          null,
    "residue",           new Token(residue),
    "residues",          null,
    "rotation",          new Token(rotation),
    "row",               new Token(row),
    "sequence",          new Token(sequence),
    "shape",             new Token(shape),
    "state",             new Token(state),
    "symbol",            new Token(symbol),
    "symmetry",          new Token(symmetry),
    "spaceGroup",        new Token(spacegroup),
    "transform",         new Token(transform),
    "translation",       new Token(translation),
    "url",               new Token(url),

    
    "(",            tokenLeftParen,
    ")",            tokenRightParen,
    "and",          tokenAnd,
    "&",            null,
    "&&",           null,
    "or",           tokenOr,
    "|",            null,
    "||",           null,
    "?",            tokenOpIf,
    ",",            tokenComma,
    "+=",           new Token(andequals),
    "-=",           null,
    "*=",           null,
    "/=",           null,
    "\\=",          null,
    "&=",           null,
    "|=",           null,
    "not",          new Token(opNot),
    "!",            null,
    "xor",          new Token(opXor),


    "tog",          new Token(opToggle),
    "<",            new Token(opLT),
    "<=",           new Token(opLE),
    ">=",           new Token(opGE),
    ">",            new Token(opGT),
    "=",            tokenEquals,
    "==",           null,
    "!=",           new Token(opNE),
    "<>",           null,
    "within",       new Token(within),
    ".",            new Token(per),
    "[",            new Token(leftsquare),
    "]",            new Token(rightsquare),
    "{",            new Token(leftbrace),
    "}",            new Token(rightbrace),
    "$",            new Token(dollarsign),
    "%",            new Token(percent),
    ":",            tokenColon,
    ";",            new Token(semicolon),
    "++",           new Token(plusPlus),
    "--",           new Token(minusMinus),
    "+",            tokenPlus,
    "-",            tokenMinus,
    "*",            tokenTimes,
    "/",            tokenDivide,
    "\\",           new Token(leftdivide),

    

    "abs",              new Token(abs),
    "absolute",         new Token(absolute),
    "acos",              new Token(acos),
    "add",              new Token(add),
    "adpmax",           new Token(adpmax),
    "adpmin",           new Token(adpmin),
    "all",              tokenAll,
    "altloc",           new Token(altloc),
    "altlocs",          null,
    "amino",            new Token(amino),
    "angle",            new Token(angle),
    "array",            new Token(array),
    "atomID",           new Token(atomID),
    "_atomID",          null,
    "_a",               null, 
    "atomIndex",        new Token(atomIndex),
    "atomName",         new Token(atomName),
    "atomno",           new Token(atomno),
    "atomType",         new Token(atomType),
    "atomx",            new Token(atomX),
    "atomy",            new Token(atomY),
    "atomz",            new Token(atomZ),
    "average",          new Token(average),
    "babel",            new Token(babel),
    "back",             new Token(back),    
    "backlit",          new Token(backlit),
    "bin",              new Token(bin),
    "bondCount",        new Token(bondcount),
    "bottom",           new Token(bottom),
    "branch",           new Token(branch),
    "carbohydrate",     new Token(carbohydrate),
    "cell",             new Token(cell),
    "chain",            new Token(chain),
    "chains",           null,
    "clear",            new Token(clear),
    "clickable",        new Token(clickable),
    "clipboard",        new Token(clipboard),
    "connected",        new Token(connected),
    "constraint",       new Token(constraint),
    "contourLines",     new Token(contourlines),
    "coord",            new Token(coord),
    "coordinates",      null,
    "coords",           null,
    "cos",              new Token(cos),
    "cross",            new Token(cross),
    "covalent",         new Token(covalent),
    "defaultColors",    new Token(defaultColors),
    "direction",        new Token(direction),
    "displacement",     new Token(displacement),
    "displayed",        new Token(displayed),
    "distance",         new Token(distance),
    "div",              new Token(div),
    "DNA",              new Token(dna),
    "dotted",           new Token(dotted),
    "element",          new Token(element),
    "elemno",           new Token(elemno),
    "_e",               null,
    "fill",             new Token(fill),
    "find",             new Token(find),
    "fixedTemperature", new Token(fixedtemp),
    "formalCharge",     new Token(formalCharge),
    "charge",           null, 
    "front",            new Token(front),    
    "frontlit",         new Token(frontlit),
    "frontOnly",        new Token(frontonly),
    "fullylit",         new Token(fullylit),
    "fx",               new Token(fracX),
    "fy",               new Token(fracY),
    "fz",               new Token(fracZ),
    "fxyz",             new Token(fracXyz),
    "group",            new Token(group),
    "groups",           null,
    "group1",           new Token(group1),
    "groupID",          new Token(groupID),
    "_groupID",         null, 
    "_g",               null, 
    "groupIndex",            new Token(groupindex),
    "hetero",           new Token(hetero),
    "hidden",           new Token(hidden),
    "hkl",              new Token(hkl),
    "hydrogen",         new Token(hydrogen),
    "hydrogens",        null,
    "id",               new Token(id),
    "identify",         new Token(identify),
    "ident",            null,
    "image",            new Token(image),
    "info",             new Token(info),
    "insertion",        new Token(insertion),
    "insertions",       null, 
    "ionic",            new Token(ionic),
    "isaromatic",       new Token(isaromatic),
    "Jmol",             new Token(jmol),
    "join",             new Token(join),
    "last",             new Token(last),
    "left",             new Token(left),    
    "length",           new Token(length),
    "lines",            new Token(lines),
    "list",             new Token(list),
    "max",              new Token(max),
    "mep",              new Token(mep),
    "mesh",             new Token(mesh),
    "min",              new Token(min),
    "mode",             new Token(mode),
    "modify",           new Token(modify),
    "modifyorcreate",   new Token(modifyorcreate),
    "molecule",         new Token(molecule),
    "molecules",        null, 
    "modelIndex",        new Token(modelindex),
    "monomer",          new Token(monomer),
    "mul",              new Token(mul),
    "next",             new Token(next),
    "noDots",           new Token(nodots),
    "noFill",           new Token(nofill),
    "noMesh",           new Token(nomesh),
    "none",             new Token(none),
    "null",             null,
    "inherit",          null,
    "normal",           new Token(normal),
    "noContourLines",   new Token(nocontourlines),
    "notFrontOnly",     new Token(notfrontonly),
    "noTriangles",      new Token(notriangles),
    "now",              new Token(now),
    "nucleic",          new Token(nucleic),
    "occupancy",        new Token(occupancy),
    "off",              tokenOff, 
    "false",            null, 
    "on",               tokenOn,
    "true",             null,                           
    "only",             new Token(only),
    "opaque",           new Token(opaque),
    "partialCharge",    new Token(partialCharge),
    "phi",              new Token(phi),
    "plane",            new Token(plane),
    "play",             new Token(play),
    "playRev",          new Token(playrev),
    "point",            new Token(point),
    "pointGroup",       new Token(pointgroup),
    "polymerLength",    new Token(polymerLength),
    "previous",         new Token(prev),
    "prev",             null,
    "property",         new Token(property),
    "protein",          new Token(protein),
    "psi",              new Token(psi),
    "purine",           new Token(purine),
    "pyrimidine",       new Token(pyrimidine),
    "random",           new Token(random),
    "range",            new Token(range),
    "rasmol",           new Token(rasmol),
    "replace",          new Token(replace),
    "resno",            new Token(resno),
    "resume",           new Token(resume),
    "rewind",           new Token(rewind),
    "reverse",          new Token(reverse),
    "right",            new Token(right),    
    "RNA",              new Token(rna),
    "rubberband",       new Token(rubberband),
    "saSurface",        new Token(sasurface),
    "selected",         new Token(selected),
    "shapely",          new Token(shapely),
    "sidechain",        new Token(sidechain),
    "sin",              new Token(sin),
    "site",             new Token(site),
    "size",             new Token(size),
    "solid",            new Token(solid),
    "solvent",          new Token(solvent),
    "sort",             new Token(sort),
    "specialPosition",  new Token(specialposition),
    "sqrt",             new Token(sqrt),
    "split",            new Token(split),
    "stddev",           new Token(stddev),
    "straightness",     new Token(straightness),
    "structureId",       new Token(strucid),
    "sub",              new Token(sub),
    "substructure",     new Token(substructure),
    "sum",              new Token(sum), 
    "sum2",             new Token(sum2), 
    "surface",          new Token(surface),
    "surfaceDistance",  new Token(surfacedistance),
    "symop",            new Token(symop),
    "temperature",      new Token(temperature),
    "relativetemperature", null,
    "thisModel",        new Token(thismodel),
    "ticks",            new Token(ticks),
    "top",              new Token(top),    
    "torsion",          new Token(torsion),
    "translucent",      new Token(translucent),
    "triangles",        new Token(triangles),
    "trim",             new Token(trim),
    "type",             new Token(type),
    "ux",               new Token(unitX),
    "uy",               new Token(unitY),
    "uz",               new Token(unitZ),
    "uxyz",             new Token(unitXyz),
    "user",             new Token(user),
    "valence",          new Token(valence),
    "vanderWaals",      new Token(vanderwaals),
    "vdw",              null,
    "visible",          new Token(visible),
    "volume",            new Token(volume),
    "vx",               new Token(vibX),
    "vy",               new Token(vibY),
    "vz",               new Token(vibZ),
    "vxyz",             new Token(vibXyz),
    "xyz",              new Token(xyz),
    
    
    "addhydrogens", new Token(addhydrogens),
    "align", new Token(align),
    "allconnected", new Token(allconnected),
    "angstroms", new Token(angstroms),
    "anisotropy", new Token(anisotropy),
    "arc", new Token(arc),
    "area", new Token(area),
    "aromatic", new Token(aromatic),
    "arrow", new Token(arrow),
    "auto", new Token(auto),
    "binary", new Token(binary),
    "blockdata", new Token(blockdata),
    "cancel", new Token(cancel),
    "cap", new Token(cap),
    "cavity", new Token(cavity),
    "check", new Token(check),
    "circle", new Token(circle),
    "collapsed", new Token(collapsed),
    "col", new Token(col),
    "colorscheme", new Token(colorscheme),
    "command", new Token(command),
    "commands", new Token(commands),
    "contour", new Token(contour),
    "contours", new Token(contours),
    "corners", new Token(corners),
    "criterion", new Token(criterion),
    "create", new Token(create),
    "crossed", new Token(crossed),
    "curve", new Token(curve),
    "cutoff", new Token(cutoff),
    "cylinder", new Token(cylinder),
    "debug", new Token(debug),
    "diameter", new Token(diameter),
    "discrete", new Token(discrete),
    "distancefactor", new Token(distancefactor),
    "downsample", new Token(downsample),
    "eccentricity", new Token(eccentricity),
    "ed", new Token(ed),
    "edges", new Token(edges),
    "energy", new Token(energy),
    "exitjmol", new Token(exitjmol),
    "facecenteroffset", new Token(facecenteroffset),
    "filter", new Token(filter),
    "first", new Token(first),
    "fix", new Token(fix),
    "fixed", new Token(fixed),
    "flat", new Token(flat),
    "fps", new Token(fps),
    "from", new Token(from),
    "frontedges", new Token(frontedges),
    "fullplane", new Token(fullplane),
    "functionxy", new Token(functionxy),
    "functionxyz", new Token(functionxyz),
    "gridpoints", new Token(gridpoints),
    "homo", new Token(homo),
    "ignore", new Token(ignore),
    "increment", new Token(increment),
    "insideout", new Token(insideout),
    "interior", new Token(interior),
    "intersection", new Token(intersection),
    "internal", new Token(internal),
    "line", new Token(line),
    "linedata", new Token(linedata),
    "lobe", new Token(lobe),
    "lonepair", new Token(lonepair),
    "lp", new Token(lp),
    "lumo", new Token(lumo),
    "manifest", new Token(manifest),
    "map", new Token(map),
    "maxset", new Token(maxset),
    "minset", new Token(minset),
    "modelbased", new Token(modelbased),
    "molecular", new Token(molecular),
    "nocross", new Token(nocross),
    "nodebug", new Token(nodebug),
    "noedges", new Token(noedges),
    "nohead", new Token(nohead),
    "noload", new Token(noload),
    "noplane", new Token(noplane),
    "object", new Token(object),
    "obj", new Token(obj),
    "offset", new Token(offset),
    "offsetside", new Token(offsetside),
    "once", new Token(once),
    "orbital", new Token(orbital),
    "atomicorbital", new Token(atomicorbital),
    "packed", new Token(packed),
    "palindrome", new Token(palindrome),
    "path", new Token(path),
    "pdb", new Token(pdb),
    "period", new Token(period),
    "perpendicular", new Token(perpendicular),
    "perp", new Token(perp),
    "phase", new Token(phase),
    "pocket", new Token(pocket),
    "pointsperangstrom", new Token(pointsperangstrom),
    "radical", new Token(radical),
    "rad", new Token(rad),
    "reference", new Token(reference),
    "resolution", new Token(resolution),
    "reversecolor", new Token(reversecolor),
    "rotate45", new Token(rotate45),
    "selection", new Token(selection),
    "sign", new Token(sign),
    "sphere", new Token(sphere),
    "squared", new Token(squared),
    "steps", new Token(steps),
    "stop", new Token(stop),
    "title", new Token(title),
    "titleformat", new Token(titleformat),
    "to", new Token(to),
    "value", new Token(val),
    "variable", new Token(variable),
    "variables", new Token(variables),
    "vertices", new Token(vertices),
    "width", new Token(width),

  };
  
  private static Hashtable tokenMap = new Hashtable();
  
  public static void addToken(String ident, Token token) {
    tokenMap.put(ident, token);
  }
  
  static {
    Token tokenLast = null;
    String stringThis;
    Token tokenThis;
    String lcase;
    for (int i = 0; i + 1 < arrayPairs.length; i += 2) {
      stringThis = (String) arrayPairs[i];
      lcase = stringThis.toLowerCase();
      tokenThis = (Token) arrayPairs[i + 1];
      if (tokenThis == null)
        tokenThis = tokenLast;
      if (tokenThis.value == null)
        tokenThis.value = stringThis;
      if (tokenMap.get(lcase) != null)
        Logger.error("duplicate token definition:" + lcase);
      tokenMap.put(lcase, tokenThis);
      tokenLast = tokenThis;
    }
  }

  public static Token getTokenFromName(String name) {
    return (Token) tokenMap.get(name);  
  }
  
  public static String nameOf(int tok) {
    Enumeration e = tokenMap.elements();
    while (e.hasMoreElements()) {
      Token token = (Token)e.nextElement();
      if (token.tok == tok)
        return "" + token.value;
    }
    return "0x"+Integer.toHexString(tok);
   }
   
  public String toString() {
    return "Token["
        + astrType[tok < keyword ? tok : keyword]
        + "("+(tok%(1<<9))+"/0x" + Integer.toHexString(tok) + ")"
        + ((intValue == Integer.MAX_VALUE) ? "" : " intValue=" + intValue
            + "(0x" + Integer.toHexString(intValue) + ")")
        + ((value == null) ? "" : value instanceof String ? " value=\"" + value
            + "\"" : " value=" + value) + "]";
  }
  
  

  
  public static String getCommandSet(String strBegin) {
    String cmds = "";
    Hashtable htSet = new Hashtable();
    int nCmds = 0;
    String s = (strBegin == null || strBegin.length() == 0 ? null : strBegin
        .toLowerCase());
    boolean isMultiCharacter = (s != null && s.length() > 1);
    Enumeration e = tokenMap.keys();
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      Token token = (Token) tokenMap.get(name);
      if ((token.tok & scriptCommand) != 0
          && (s == null || name.indexOf(s) == 0)
          && (isMultiCharacter || ((String) token.value).equals(name)))
        htSet.put(name, Boolean.TRUE);
    }
    e = htSet.keys();
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      if (name.charAt(name.length() - 1) != 's'
          || !htSet.containsKey(name.substring(0, name.length() - 1)))
        cmds += (nCmds++ == 0 ? "" : ";") + name;
    }
    return cmds;
  }
  
  public static String[] getTokensLike(String type) {
    int attr = (type.equals("setparam") ? setparam 
        : type.equals("misc") ? misc 
        : type.equals("mathfunc") ? mathfunc : scriptCommand);
    Vector v = new Vector();
    Enumeration e = tokenMap.keys();
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      Token token = (Token) tokenMap.get(name);
      if (tokAttr(token.tok, attr))
        v.add(name);
    }
    String[] a = new String[v.size()];
    for (int i = 0; i < a.length; i++)
      a[i] = (String) v.get(i);
    Arrays.sort(a);
    return a;
  }

  public static int getSettableTokFromString(String s) {
    Token token = getTokenFromName(s);
    int tok;
    if (token != null)
      return (tokAttr((tok = token.tok), settable) 
          && !tokAttr(tok, mathproperty) ? token.tok : nada);
    if (s.equals("x"))
      return atomX;
    else if (s.equals("y"))
      return atomY;
    else if (s.equals("z"))
      return atomZ;
    else if (s.equals("w"))
      return qw;
    return nada;
  }

  public static String completeCommand(Hashtable map, 
                                       boolean asCommand, 
                                       String str, int n) {
    if (map == null)
      map = Token.tokenMap;
    else
      asCommand = false;
    Vector v = new Vector();
    Enumeration e = map.keys();
    String name;
    str = str.toLowerCase();
    while (e.hasMoreElements()) {
      name = (String) e.nextElement();
      if (!name.startsWith(str))
        continue;
      Token t = getTokenFromName(name);
      if ((!asCommand || tokAttr(t.tok, scriptCommand))
          )
        v.add(name);
    }
    return ArrayUtil.sortedItem(v, n);
  }



}
