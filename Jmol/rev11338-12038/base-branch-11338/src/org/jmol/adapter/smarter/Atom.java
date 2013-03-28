

package org.jmol.adapter.smarter;

import javax.vecmath.Point3f;
import java.util.BitSet;

public class Atom extends Point3f implements Cloneable {
  public int atomSetIndex;
  public int atomIndex;
  public BitSet bsSymmetry;
  public int atomSite;
  public String elementSymbol;
  public short elementNumber = -1;
  public String atomName;
  public int formalCharge = Integer.MIN_VALUE;
  {set(Float.NaN, Float.NaN, Float.NaN);}
  public float partialCharge = Float.NaN;
  public float vectorX = Float.NaN, vectorY = Float.NaN, vectorZ = Float.NaN;
  public float bfactor = Float.NaN;
  public int occupancy = 100;
  public float radius = Float.NaN;
  public boolean isHetero;
  public int atomSerial = Integer.MIN_VALUE;
  public char chainID = '\0';
  public char alternateLocationID = '\0';
  public String group3;
  public int sequenceNumber = Integer.MIN_VALUE;
  public char insertionCode = '\0';
  public float[] anisoBorU; 
  public Object[] ellipsoid;

  public Atom() {
  }

  public Atom cloneAtom() throws Exception {
    try {
      return (Atom)super.clone();
    } catch (Exception cnse) {
      throw new Exception("cloneAtom error: " + cnse.getMessage(), cnse);
      }
  }

  public String getElementSymbol() {
    if (elementSymbol == null)
      if (atomName != null) {
        int len = atomName.length();
        int ichFirst = 0;
        char chFirst = 0;
        while (ichFirst < len &&
               !isValidFirstSymbolChar(chFirst = atomName.charAt(ichFirst)))
          ++ichFirst;
        switch(len - ichFirst) {
        case 0:
          break;
        default:
          char chSecond = atomName.charAt(ichFirst + 1);
          if (isValidElementSymbolNoCaseSecondChar(chFirst, chSecond)) {
            elementSymbol = "" + chFirst + chSecond;
            break;
          }
          
        case 1:
          if (isValidElementSymbol(chFirst))
            elementSymbol = "" + chFirst;
          break;
        }
      }
    return elementSymbol;
  }

  public void addVibrationVector(float vectorX, float vectorY, float vectorZ) {
    this.vectorX = vectorX;
    this.vectorY = vectorY;
    this.vectorZ = vectorZ;
  }
  
 
  
  final static int[] elementCharMasks = {
    
    1 << ('c' - 'a') |
    1 << ('g' - 'a') |
    1 << ('l' - 'a') |
    1 << ('m' - 'a') |
    1 << ('r' - 'a') |
    1 << ('s' - 'a') |
    1 << ('t' - 'a') |
    1 << ('u' - 'a'),
    
    1 << 31 |
    1 << ('a' - 'a') |
    1 << ('e' - 'a') |
    1 << ('h' - 'a') |
    1 << ('i' - 'a') |
    1 << ('k' - 'a') |
    1 << ('r' - 'a'),
    
    1 << 31 |
    1 << ('a' - 'a') |
    1 << ('d' - 'a') |
    1 << ('e' - 'a') |
    1 << ('f' - 'a') |
    1 << ('l' - 'a') |
    1 << ('m' - 'a') |
    1 << ('o' - 'a') |
    1 << ('r' - 'a') |
    1 << ('s' - 'a') |
    1 << ('u' - 'a'),
    
    1 << 31 |
    1 << ('b' - 'a') |
    1 << ('y' - 'a'),
    
    1 << ('r' - 'a') |
    1 << ('s' - 'a') |
    1 << ('u' - 'a'),
    
    1 << 31 |
    1 << ('e' - 'a') |
    1 << ('m' - 'a') |
    1 << ('r' - 'a'),
    
    1 << ('a' - 'a') |
    1 << ('d' - 'a') |
    1 << ('e' - 'a'),
    
    1 << 31 |
    1 << ('e' - 'a') |
    1 << ('f' - 'a') |
    1 << ('g' - 'a') |
    1 << ('o' - 'a') |
    1 << ('s' - 'a'),
    
    1 << 31 |
    1 << ('n' - 'a') |
    1 << ('r' - 'a'),
    
    0,
    
    1 << 31 |
    1 << ('r' - 'a'),
    
    1 << ('a' - 'a') |
    1 << ('i' - 'a') |
    1 << ('r' - 'a') |
    1 << ('u' - 'a'),
    
    1 << ('d' - 'a') |
    1 << ('g' - 'a') |
    1 << ('n' - 'a') |
    1 << ('o' - 'a') |
    1 << ('t' - 'a'),
    
    1 << 31 |
    1 << ('a' - 'a') |
    1 << ('b' - 'a') |
    1 << ('d' - 'a') |
    1 << ('e' - 'a') |
    1 << ('i' - 'a') |
    1 << ('o' - 'a') |
    1 << ('p' - 'a'),
    
    1 << 31 |
    1 << ('s' - 'a'),
    
    1 << 31 |
    1 << ('a' - 'a') |
    1 << ('b' - 'a') |
    1 << ('d' - 'a') |
    1 << ('m' - 'a') |
    1 << ('o' - 'a') |
    1 << ('r' - 'a') |
    1 << ('t' - 'a') |
    1 << ('u' - 'a'),
    
    0,
    
    1 << ('a' - 'a') |
    1 << ('b' - 'a') |
    1 << ('e' - 'a') |
    1 << ('f' - 'a') |
    1 << ('h' - 'a') |
    1 << ('n' - 'a') |
    1 << ('u' - 'a'),
    
    1 << 31 |
    1 << ('b' - 'a') |
    1 << ('c' - 'a') |
    1 << ('e' - 'a') |
    1 << ('g' - 'a') |
    1 << ('i' - 'a') |
    1 << ('m' - 'a') |
    1 << ('n' - 'a') |
    1 << ('r' - 'a'),
    
    1 << 31 |
    1 << ('a' - 'a') |
    1 << ('b' - 'a') |
    1 << ('c' - 'a') |
    1 << ('e' - 'a') |
    1 << ('h' - 'a') |
    1 << ('i' - 'a') |
    1 << ('l' - 'a') |
    1 << ('m' - 'a'),
    
    1 << 31,
    
    1 << 31,
    
    1 << 31,
    
    1 << ('e' - 'a') |
    1 << ('x' - 'a'), 
    
    1 << 31 |
    1 << ('b' - 'a'),
    
    1 << ('n' - 'a') |
    1 << ('r' - 'a')
  };

  public static boolean isValidElementSymbol(char ch) {
    return ch >= 'A' && ch <= 'Z' && elementCharMasks[ch - 'A'] < 0;
  }

  public static boolean isValidElementSymbol(char chFirst, char chSecond) {
    if (chFirst < 'A' || chFirst > 'Z' || chSecond < 'a' || chSecond > 'z')
      return false;
    return ((elementCharMasks[chFirst - 'A'] >> (chSecond - 'a')) & 1) != 0;
  }

  public static boolean isValidElementSymbolNoCaseSecondChar(char chFirst,
                                                      char chSecond) {
    if (chSecond >= 'A' && chSecond <= 'Z')
      chSecond += 'a' - 'A';
    if (chFirst < 'A' || chFirst > 'Z' || chSecond < 'a' || chSecond > 'z')
      return false;
    return ((elementCharMasks[chFirst - 'A'] >> (chSecond - 'a')) & 1) != 0;
  }

  public static boolean isValidFirstSymbolChar(char ch) {
    return ch >= 'A' && ch <= 'Z' && elementCharMasks[ch - 'A'] != 0;
  }

  public static boolean isValidElementSymbolNoCaseSecondChar(String str) {
    if (str == null)
      return false;
    int length = str.length();
    if (length == 0)
      return false;
    char chFirst = str.charAt(0);
    if (length == 1)
      return isValidElementSymbol(chFirst);
    if (length > 2)
      return false;
    char chSecond = str.charAt(1);
    return isValidElementSymbolNoCaseSecondChar(chFirst, chSecond);
  }
}
