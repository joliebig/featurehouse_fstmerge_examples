

package org.jmol.script;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.jmol.util.Logger;


public class Token {

  public int tok;
  public Object value;
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
  public final static int identifier =  1;
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
  
 

  
  
  
  
  
  
   
  public final static int command            = (1 << 12);
  
  
  
  final static int atomExpressionCommand  = (1 << 13) | command;
  
  
  
  final static int implicitStringCommand     = (1 << 14) | command;
  
  
  
  
  final static int mathExpressionCommand = (1 << 15) | command;
  
  
  
  
  final static int flowCommand        = (1 << 16) | mathExpressionCommand;
  
  
  
  
  
  
  
  final static int noeval         = (1 << 17);
  final static int noArgs         = (1 << 18);
  final static int defaultON      = (1 << 19);
  
  final static int expression           = (1 << 20);
  final static int predefinedset        = (1 << 21) | expression;
  
  public final static int atomproperty  = (1 << 22) | expression; 
  
  
  public final static int strproperty   = (1 << 23) | atomproperty; 
  public final static int intproperty   = (1 << 24) | atomproperty; 
  public final static int floatproperty = (1 << 25) | atomproperty; 

  public final static int PROPERTYFLAGS = strproperty | intproperty | floatproperty;
  
  final static int mathproperty         = (1 << 26) | expression; 
  final static int mathfunc             = (1 << 27) | expression;  
  final static int mathop               = (1 << 28) | expression;
  final static int comparator           = mathop | (1 << 8);
  
  
  
  
  
  final static int setparam          = (1 << 29); 
  final static int misc              = (1 << 30); 

  public final static int center       = 1 | atomExpressionCommand;
  final static int define       = 2 | atomExpressionCommand | expression | setparam;
  public final static int delete       = 3 | atomExpressionCommand;
  final static int display      = 4 | atomExpressionCommand | setparam;
  final static int hide         = 5 | atomExpressionCommand;
  final static int restrict     = 6 | atomExpressionCommand;

  final static int subset       = 7 | atomExpressionCommand | predefinedset;
  final static int zap          = 8 | atomExpressionCommand;

  final static int print        = 1 | mathExpressionCommand;
  final static int returncmd    = 2 | mathExpressionCommand;
  final static int set          = 3 | mathExpressionCommand;
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
  final static int end          = 9 | flowCommand | noeval;
  
  final static int animation    = command | 1;
  public final static int axes         = command | 2 | setparam | defaultON;
  final static int background   = command | 4 | setparam;
  final static int bondorder    = command | 5;

  final static int calculate    = command | 6;
  final static int cd           = command | 7 | implicitStringCommand;
  final static int centerAt     = command | 8;

  final static int configuration = command | 9;
  public final static int connect = command | 10;
  final static int console      = command | 11 | defaultON;

  final static int delay        = command | 12 | defaultON;
  final static int depth        = command | 13 | defaultON;
  public final static int dipole       = command | 14;
  public final static int draw         = command | 16;
  final static int exit         = command | 18 | noArgs;

  final static int font         = command | 19;
  final static int frame        = command | 20;
  public final static int frank        = command | 21 | setparam | defaultON;

  final static int gotocmd      = command | 23 | implicitStringCommand;
  public final static int hbond        = command | 25 | setparam | expression | defaultON;
  final static int history      = command | 26 | setparam;
  final static int initialize   = command | 27 | noArgs;
  final static int invertSelected = command | 28;
  public final static int isosurface   = command | 29;
  public final static int lcaocartoon  = command | 30;

  final static int loop         = command | 31 | defaultON;
  final static int minimize     = command | 33;
  public final static int mo           = command | 34;

  public final static int monitor      = command | 35 | setparam | expression | defaultON;
  final static int move         = command | 36;
  public final static int moveto       = command | 37;
  final static int navigate     = command | 38;
  public final static int pmesh        = command | 39;
  public final static int polyhedra    = command | 40;

  final static int quit         = command | 41 | noArgs;
  final static int ramachandran = command | 42 | expression;
  final static int refresh      = command | 43 | noArgs;
  final static int reset        = command | 44;
  final static int restore      = command | 45;
  public final static int resume       = command | 46 | noArgs;
  final static int rotate       = command | 48 | defaultON;
  final static int rotateSelected = command | 49;
  final static int save         = command | 50;

  final static int selectionHalo = command | 51 | setparam | defaultON;
  final static int show         = command | 52;
  final static int slab         = command | 53 | defaultON;
  
  final static int spin         = command | 55 | setparam | defaultON;
  public final static int ssbond       = command | 56 | setparam | defaultON;
  final static int step         = command | 57 | noArgs;
  final static int stereo       = command | 58 | defaultON;

  final static int sync         = command | 60;
  final static int translate    = command | 62;
  final static int translateSelected = command | 63;
  public final static int unitcell = command | 64 | setparam | expression | predefinedset | defaultON;
  public final static int vector       = command | 65;
  public final static int vibration    = command | 66;
  public final static int wireframe    = command | 67 | defaultON;
  
  final static int zoom         = command | 68;
  final static int zoomTo       = command | 69;


  

  
  
  
  
  final static int expressionBegin     = expression | 1;
  final static int expressionEnd       = expression | 2;
  final static int all                 = expression | 3;
  public final static int branch       = expression | 4;
  final static int coord               = expression | 6;
  final static int dollarsign          = expression | 7;
  final static int period              = expression | 8;
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
  final static int opToggle     = 2 | mathop | 5 << 4;

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
  public final static int boundbox  = 9 | mathproperty | setparam | command | defaultON;
  public final static int xyz       =10 | mathproperty | atomproperty | settable;
  public final static int fracXyz   =11 | mathproperty | atomproperty | settable;
  public final static int unitXyz   =12 | mathproperty | atomproperty;
  public final static int vibXyz    =13 | mathproperty | atomproperty | settable;
  
  
  
  public final static int occupancy     = intproperty | floatproperty | 2 | settable;
  public final static int radius        = intproperty | floatproperty | 1 | setparam | settable;
  public final static int structure     = intproperty | strproperty   | 3 | command;

  
  
  
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
  public final static int color         = intproperty | 6 | command | setparam | settable;
  public final static int elemno        = intproperty | 7 | settable;
  
  public final static int formalCharge  = intproperty | 8 | setparam | settable;
  public final static int groupID       = intproperty | 9;
  public final static int groupindex    = intproperty | 10;
  public final static int model         = intproperty | 11 | command;
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
  
  public final static int backbone     = floatproperty | command | 1 | predefinedset | defaultON | settable;
  public final static int cartoon      = floatproperty | command | 2 | defaultON | settable;
  public final static int dots         = floatproperty | command | 3 | defaultON;
  public final static int ellipsoid    = floatproperty | command | 4 | defaultON;
  public final static int geosurface   = floatproperty | command | 5 | defaultON;
  public final static int halo         = floatproperty | command | 6 | defaultON | settable;
  public final static int meshRibbon   = floatproperty | command | 7 | defaultON | settable;
  public final static int ribbon       = floatproperty | command | 9 | defaultON | settable;
  public final static int rocket       = floatproperty | command | 10 | defaultON | settable;
  public final static int spacefill    = floatproperty | command | 11 | defaultON | settable;
  public final static int star         = floatproperty | command | 12 | defaultON | settable;
  public final static int strands      = floatproperty | command | 13 | setparam | defaultON | settable;
  public final static int trace        = floatproperty | command | 14 | defaultON | settable;

  
  
  
  
  
  


  static int getMaxMathParams(int tokCommand) {
    return  ((tokCommand >> 9) & 0x7);
  }

  
  

  
  
  public final static int array  = 1 | 0 << 9 | mathfunc;
  public final static int format = 2 | 0 << 9 | mathfunc | mathproperty | strproperty | settable;
  public final static int label  = 3 | 0 << 9 | mathfunc | mathproperty | strproperty | settable | implicitStringCommand | defaultON | setparam;
  final static int function      = 4 | 0 << 9 | mathfunc | flowCommand | noeval;
  final static int getproperty   = 5 | 0 << 9 | mathfunc | command;
  final static int write         = 6 | 0 << 9 | mathfunc | command;

  
  
  public final static int angle = 7 | 0 << 9 | mathfunc;
  public final static int data  = 8 | 0 << 9 | mathfunc | command;
  public final static int plane        = 9 | 0 << 9 | mathfunc;
  public final static int point = 10 | 0 << 9 | mathfunc;
  final static int quaternion   = 11 | 0 << 9 | mathfunc | command;
  final static int axisangle    = 12 | 0 << 9 | mathfunc;

  
  
  final static int within           = 13 | 0 << 9 | mathfunc;
  public final static int connected = 14 | 0 << 9 | mathfunc;
  public final static int helix     = 15 | 0 << 9 | mathfunc | predefinedset;
  
  
  
  final static int substructure = 1 | 1 << 9 | mathfunc;
  final static int javascript   = 2 | 1 << 9 | mathfunc | implicitStringCommand;
  final static int acos         = 3 | 1 << 9 | mathfunc;
  final static int sin          = 4 | 1 << 9 | mathfunc;
  final static int cos          = 5 | 1 << 9 | mathfunc;
  final static int sqrt         = 6 | 1 << 9 | mathfunc;
  public final static int file  = 7 | 1 << 9 | mathfunc | intproperty | command;
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

  
  
  public final static int cross = 1 | 2 << 9 | mathfunc;
  final static int load         = 2 | 2 << 9 | mathfunc | command;
  final static int random       = 3 | 2 << 9 | mathfunc;
  final static int script       = 4 | 2 << 9 | mathfunc | command;

  

  
  
  
  
  
  
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

  

  final static int absolute     = misc |  1;
  public final static int axis  = misc |  3;
  final static int babel        = misc |  4;
  final static int back         = misc |  5;
  public final static int backlit = misc |  6;
  final static int bondset      = misc |  8;
  final static int bottom       = misc |  9;
  final static int clear        = misc | 10;
  final static int clipboard    = misc | 11;
  final static int constraint   = misc | 12;
  public final static int contourlines = misc | 112;
  final static int direction    = misc | 13;
  final static int displacement = misc | 14;
  final static int dotted       = misc | 15;
  public final static int fill  = misc | 16;
  final static int fixedtemp    = misc | 17; 
  public final static int front        = misc | 18;
  public final static int frontlit     = misc | 19;
  public final static int frontonly    = misc | 20;
  public final static int fullylit     = misc | 21;
  final static int hkl                 = misc | 120;
  final static int image               = misc | 121;  
  public final static int info         = misc | 122;
  public final static int jmol         = misc | 23;
  public final static int last         = misc | 24;
  final static int left                = misc | 25;
  final static int mep                 = misc | 26;
  public final static int mesh         = misc | 27;
  final static int mode                = misc | 28;
  final static int monomer             = misc | 29;
  public final static int next         = misc | 30;
  public final static int nocontourlines = misc | 130;
  public final static int nodots       = misc | 31;
  public final static int nofill       = misc | 32;
  public final static int nomesh       = misc | 33;
  final static int normal              = misc | 34;
  public final static int notfrontonly = misc | 35;
  public final static int notriangles  = misc | 36;
  final static int only         = misc | 136;
  final static int opaque       = misc | 37;
  final static int orientation  = misc | 38;
  final static int pdbheader    = misc | 39;
  public final static int play         = misc | 40;
  public final static int playrev      = misc | 41;
  final static int pointgroup   = misc | 411;
  final static int polymer      = misc | 42;
  public final static int prev         = misc | 43;
  final static int range               = misc | 44;
  public final static int rasmol       = misc | 45;
  public final static int residue      = misc | 46;
  public final static int rewind       = misc | 48;
  final static int right               = misc | 49;
  public final static int rotation     = misc | 50;
  final static int rubberband          = misc | 51;
  final static int sasurface           = misc | 52;
  final static int scale        = misc | 53;
  final static int shape        = misc | 54;
  final static int shapely      = misc | 55;
  public final static int sheet = misc | 55 | predefinedset;
  final static int solid        = misc | 56;
  final static int spacegroup   = misc | 57;
  final static int state        = misc | 58;
  final static int top          = misc | 59;
  final static int torsion      = misc | 60;
  final static int transform    = misc | 61;
  public final static int translation  = misc | 62;
  public final static int translucent  = misc | 63;
  public final static int triangles    = misc | 64;
  final static int url          = misc | 65; 
  final static int user         = misc | 66; 
  final static int qw           = misc | 67;

  
  
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
    "image",             new Token(image),
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
    "modelindex",        new Token(modelindex),
    "move",              new Token(move),
    "moveto",            new Token(moveto),
    "navigate",          new Token(navigate),
    "navigation",        null,
    "pause",             new Token(pause),
    "wait",              null,
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
    "structureId",       new Token(strucid),
    "subset",            new Token(subset),
    "synchronize",       new Token(sync),
    "sync",              null,
    "trace",             new Token(trace),
    "translate",         new Token(translate),
    "translateSelected", new Token(translateSelected),
    "unitcell",          new Token(unitcell),
    "var",               new Token(var),
    "vector",            new Token(vector),
    "vectors",           null,
    "vibration",         new Token(vibration),
    "volume",            new Token(volume),
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
    ".",            new Token(period),
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
    "identify",         new Token(identify),
    "ident",            null,
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
    "molecule",         new Token(molecule),
    "molecules",        null, 
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
    "vx",               new Token(vibX),
    "vy",               new Token(vibY),
    "vz",               new Token(vibZ),
    "vxyz",             new Token(vibXyz),
    "xyz",              new Token(xyz),
  };

  private static Hashtable map = new Hashtable();
  
  public static void addToken(String ident, Token token) {
    map.put(ident, token);
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
      if (map.get(lcase) != null)
        Logger.error("duplicate token definition:" + lcase);
      map.put(lcase, tokenThis);
      tokenLast = tokenThis;
    }
  }

  public static Token getTokenFromName(String name) {
    return (Token) map.get(name);  
  }
  
  public static String nameOf(int tok) {
    Enumeration e = map.elements();
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
    Enumeration e = map.keys();
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      Token token = (Token) map.get(name);
      if ((token.tok & command) != 0
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
        : type.equals("mathfunc") ? mathfunc : command);
    Vector v = new Vector();
    Enumeration e = map.keys();
    while (e.hasMoreElements()) {
      String name = (String) e.nextElement();
      Token token = (Token) map.get(name);
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

  public static String completeCommand(String strCommand, int n) {
    if (strCommand == null)
      return null;
    int i = strCommand.lastIndexOf(" ");
    if (true || i == strCommand.length() - 1)
      return strCommand;
    
    String str = strCommand.substring(i + 1, strCommand.length()).toLowerCase();
    if (str.length() > 1 && n > 1)
      str = str.substring(0, 1);
    int nFound = 0;
    int n0 = n;
    String nameFound = null;
    out:
    while (n >= 0) {
      Enumeration e = map.keys();
      while (e.hasMoreElements()) {
        String name = (String) e.nextElement();
        if (name.toLowerCase().startsWith(str)) {
          System.out.println(name);
          nameFound = name;
          nFound++;
          if (--n <= 0)
            break out;
        }
      }
      
      if (nFound == 0)
        return strCommand;
      n = n0 % nFound;
      if (false)
        break;
    }
    return strCommand.substring(0, i + 1) + nameFound;
  }

  


}
