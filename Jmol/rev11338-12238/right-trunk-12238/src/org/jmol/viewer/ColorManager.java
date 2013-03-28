
package org.jmol.viewer;

import org.jmol.script.Token;
import org.jmol.util.ArrayUtil;
import org.jmol.util.Logger;
import java.util.BitSet;
import org.jmol.g3d.*;
import org.jmol.modelset.Atom;
import org.jmol.modelset.Bond;
import org.jmol.modelset.HBond;
import org.jmol.modelset.ModelSet;
import org.jmol.util.ColorEncoder;

class ColorManager {

  private Viewer viewer;
  private Graphics3D g3d;
  private int[] argbsCpk;
  private int[] altArgbsCpk;
  private float colorHi, colorLo;
  private float[] colorData;  
  private int palette = 0;
  

  ColorManager(Viewer viewer, Graphics3D g3d) {
    this.viewer = viewer;
    this.g3d = g3d;
    argbsCpk = JmolConstants.argbsCpk;
    altArgbsCpk = ArrayUtil.arrayCopy(JmolConstants.altArgbsCpk, 0, -1, false);
  }

  void clear() {
    
  }
  
  private boolean isDefaultColorRasmol;
  boolean getDefaultColorRasmol() {
    return isDefaultColorRasmol;
  }

  void resetElementColors() {
    setDefaultColors("Jmol");
  }
  
  void setDefaultColors(String colorScheme) {
    if (colorScheme.equalsIgnoreCase("Jmol")) {
      isDefaultColorRasmol = false;
      argbsCpk = JmolConstants.argbsCpk;
    } else if (colorScheme.equalsIgnoreCase("RasMol")) {
      isDefaultColorRasmol = true;
      argbsCpk = ColorEncoder.getRasmolScale(true);
    } else {
      Logger.error("unrecognized color scheme");
      return;
    }
    altArgbsCpk = ArrayUtil.arrayCopy(JmolConstants.altArgbsCpk, 0, -1, false);
    ColorEncoder.makeColorScheme(colorScheme, null, true);
    for (int i = JmolConstants.argbsCpk.length; --i >= 0; )
      g3d.changeColixArgb((short)i, argbsCpk[i]);
    for (int i = JmolConstants.altArgbsCpk.length; --i >= 0; )
      g3d.changeColixArgb((short)(JmolConstants.elementNumberMax + i), altArgbsCpk[i]);
  }

  short colixRubberband = Graphics3D.HOTPINK;
  void setRubberbandArgb(int argb) {
    colixRubberband = (argb == 0 ? 0 : Graphics3D.getColix(argb));
  }

  
  short colixBackgroundContrast;
  void setColixBackgroundContrast(int argb) {
    colixBackgroundContrast =
      ((Graphics3D.calcGreyscaleRgbFromRgb(argb) & 0xFF) < 128
       ? Graphics3D.WHITE : Graphics3D.BLACK);
  }

  short getColixBondPalette(Bond bond, byte pid) {
    int argb = 0;
    switch (pid) {
    case JmolConstants.PALETTE_ENERGY:
      return ColorEncoder.getColorIndexFromPalette(((HBond)bond).getEnergy(), 
          0.5f, 4.5f, ColorEncoder.BWR, false);
    }
    return (argb == 0 ? Graphics3D.RED : Graphics3D.getColix(argb));
  }
  
  short getColixAtomPalette(Atom atom, byte pid) {
    int argb = 0;
    int index;
    short id;
    ModelSet modelSet;
    int modelIndex;
    float lo, hi;
    switch (pid) {
    case JmolConstants.PALETTE_PROPERTY:
      return getPropertyColix(atom.getIndex());
    case JmolConstants.PALETTE_NONE:
    case JmolConstants.PALETTE_CPK:
      
      
      id = atom.getAtomicAndIsotopeNumber();
      if (id < JmolConstants.elementNumberMax)
        return g3d.getChangeableColix(id, argbsCpk[id]);
      id = (short) JmolConstants.altElementIndexFromNumber(id);
      return g3d.getChangeableColix(
          (short) (JmolConstants.elementNumberMax + id), altArgbsCpk[id]);
    case JmolConstants.PALETTE_PARTIAL_CHARGE:
      
      index = ColorEncoder.quantize(atom.getPartialCharge(), 
          -1, 1, JmolConstants.PARTIAL_CHARGE_RANGE_SIZE);
      return g3d.getChangeableColix(
          (short) (JmolConstants.PARTIAL_CHARGE_COLIX_RED + index),
          JmolConstants.argbsRwbScale[index]);
    case JmolConstants.PALETTE_FORMAL_CHARGE:
      index = atom.getFormalCharge() - JmolConstants.FORMAL_CHARGE_MIN;
      return g3d.getChangeableColix(
          (short) (JmolConstants.FORMAL_CHARGE_COLIX_RED + index),
          JmolConstants.argbsFormalCharge[index]);
    case JmolConstants.PALETTE_TEMP:
    case JmolConstants.PALETTE_FIXEDTEMP:
      if (pid == JmolConstants.PALETTE_TEMP) {
        modelSet = viewer.getModelSet();
        lo = modelSet.getBfactor100Lo();
        hi = modelSet.getBfactor100Hi();
      } else {
        lo = 0;
        hi = 100 * 100; 
      }
      return ColorEncoder.getColorIndexFromPalette(atom.getBfactor100(), 
          lo, hi, ColorEncoder.BWR, false);
    case JmolConstants.PALETTE_STRAIGHTNESS:
      return ColorEncoder.getColorIndexFromPalette(atom.getStraightness(), 
          -1, 1, ColorEncoder.BWR, false);
    case JmolConstants.PALETTE_SURFACE:
      hi = viewer.getSurfaceDistanceMax();
      return ColorEncoder.getColorIndexFromPalette(atom.getSurfaceDistance100(), 
          0, hi, ColorEncoder.BWR, false);
    case JmolConstants.PALETTE_AMINO:
      return ColorEncoder.getColorIndexFromPalette(atom
          .getGroupID(), 0, 0, ColorEncoder.AMINO, false);
    case JmolConstants.PALETTE_SHAPELY:
      return ColorEncoder.getColorIndexFromPalette(atom
          .getGroupID(), 0, 0, ColorEncoder.SHAPELY, false);
    case JmolConstants.PALETTE_GROUP:
      
      
      
      
      
      
      return ColorEncoder.getColorIndexFromPalette(
          atom.getSelectedGroupIndexWithinChain(), 0,
          atom.getSelectedGroupCountWithinChain() - 1,
          ColorEncoder.BGYOR, false);
    case JmolConstants.PALETTE_MONOMER:
      
      return ColorEncoder.getColorIndexFromPalette(
          atom.getSelectedMonomerIndexWithinPolymer(), 
          0, atom.getSelectedMonomerCountWithinPolymer() - 1,
          ColorEncoder.BGYOR, false);
    case JmolConstants.PALETTE_MOLECULE:
      modelSet = viewer.getModelSet();
      return ColorEncoder.getColorIndexFromPalette(
          modelSet.getMoleculeIndex(atom.getIndex()), 
          0, modelSet.getMoleculeCountInModel(atom.getModelIndex()) - 1, 
          ColorEncoder.ROYGB, false);
    case JmolConstants.PALETTE_ALTLOC:
      modelSet = viewer.getModelSet();
      
      modelIndex = atom.getModelIndex();
      return ColorEncoder.getColorIndexFromPalette(
          modelSet.getAltLocIndexInModel(modelIndex,
          atom.getAlternateLocationID()), 
          0, modelSet.getAltLocCountInModel(modelIndex),
          ColorEncoder.ROYGB, false);
    case JmolConstants.PALETTE_INSERTION:
      modelSet = viewer.getModelSet();
      
      modelIndex = atom.getModelIndex();
      return ColorEncoder.getColorIndexFromPalette(
          modelSet.getInsertionCodeIndexInModel(
          modelIndex, atom.getInsertionCode()), 
          0, modelSet.getInsertionCountInModel(modelIndex),
          ColorEncoder.ROYGB, false);
    case JmolConstants.PALETTE_JMOL:
      id = atom.getAtomicAndIsotopeNumber();
      argb = getJmolOrRasmolArgb(id, Token.jmol);
      break;
    case JmolConstants.PALETTE_RASMOL:
      id = atom.getAtomicAndIsotopeNumber();
      argb = getJmolOrRasmolArgb(id, Token.rasmol);
      break;
    case JmolConstants.PALETTE_STRUCTURE:
      argb = JmolConstants.argbsStructure[atom.getProteinStructureType()];
      break;
    case JmolConstants.PALETTE_CHAIN:
      int chain = atom.getChainID() & 0x1F;
      if (chain < 0)
        chain = 0;
      if (chain >= JmolConstants.argbsChainAtom.length)
        chain = chain % JmolConstants.argbsChainAtom.length;
      argb = (atom.isHetero() ? JmolConstants.argbsChainHetero
          : JmolConstants.argbsChainAtom)[chain];
      break;
    }
    return (argb == 0 ? Graphics3D.HOTPINK : Graphics3D.getColix(argb));
  }

  private short getPropertyColix(int iAtom) {
    if (colorData == null || iAtom >= colorData.length)
      return Graphics3D.GRAY;
    return getColixForPropertyValue(colorData[iAtom]);    
  }

  private static int getJmolOrRasmolArgb(int id, int argb) {
    switch (argb) {
    case Token.jmol:
      if (id >= JmolConstants.elementNumberMax)
        break;
      return ColorEncoder.getArgbFromPalette(id, 0, 0, ColorEncoder.JMOL);
    case Token.rasmol:
      if (id >= JmolConstants.elementNumberMax)
        break;
      return ColorEncoder.getArgbFromPalette(id, 0, 0, ColorEncoder.RASMOL);
    default:
      return argb;
    }
    return JmolConstants.altArgbsCpk[JmolConstants
        .altElementIndexFromNumber(id)];
  }

  void setElementArgb(int id, int argb) {
    if (argb == Token.jmol && argbsCpk == JmolConstants.argbsCpk)
      return;
    argb = getJmolOrRasmolArgb(id, argb);
    if (argbsCpk == JmolConstants.argbsCpk) {
      argbsCpk = ArrayUtil.arrayCopy(JmolConstants.argbsCpk, 0, -1, false);
      altArgbsCpk = ArrayUtil.arrayCopy(JmolConstants.altArgbsCpk, 0, -1, false);
    }
    if (id < JmolConstants.elementNumberMax) {
      argbsCpk[id] = argb;
      g3d.changeColixArgb((short)id, argb);
      return;
    }
    id = JmolConstants.altElementIndexFromNumber(id);
    altArgbsCpk[id] = argb;
    g3d.changeColixArgb((short) (JmolConstants.elementNumberMax + id), argb);
  }

  int setColorScheme(String colorScheme, boolean isOverloaded) {
    palette = ColorEncoder.getColorScheme(colorScheme, isOverloaded);
    Logger.info("ColorManager: color scheme now \"" + ColorEncoder.getColorSchemeName(palette) + "\" color value range: " + colorLo + " to " + colorHi);
    return palette;
  }

  float[] getCurrentColorRange() {
    return new float[] {colorLo, colorHi};
  }

  void setCurrentColorRange(float[] data, BitSet bs, String colorScheme) {
    colorData = data;
    palette = ColorEncoder.getColorScheme(colorScheme, false);
    colorHi = Float.MIN_VALUE;
    colorLo = Float.MAX_VALUE;
    if (data == null)
      return;
    for (int i = data.length; --i >= 0;)
      if (bs == null || bs.get(i)) {
        float d = data[i];
        if (Float.isNaN(d))
          continue;
        colorHi = Math.max(colorHi, d);
        colorLo = Math.min(colorLo, d);
      }
    setCurrentColorRange(colorLo, colorHi);
  }  

  void setCurrentColorRange(float min, float max) {
    colorHi = max;
    colorLo = min;
    Logger.info("color \"" + ColorEncoder.getColorSchemeName(palette) + "\" range " + colorLo + " " + colorHi);
  }

  static String getState(StringBuffer sfunc) {
    return ColorEncoder.getState(sfunc);
  }
  
  static void setUserScale(int[] scale) {
    ColorEncoder.setUserScale(scale);
  }
  
  int[] getColorSchemeArray(String colorScheme) {
    return ColorEncoder.getColorSchemeArray(colorScheme == null || colorScheme.length() == 0 ? palette : ColorEncoder.getColorScheme(colorScheme, false));  
  }
  
  String getColorSchemeList(String colorScheme, boolean ifDefault) {
    if (!ifDefault && ColorEncoder.getColorScheme(colorScheme, false) >= 0)
      return "";
    return ColorEncoder.getColorSchemeList(getColorSchemeArray(colorScheme));
  }
  
  short getColixForPropertyValue(float val) {
    return (colorLo < colorHi ? 
        ColorEncoder.getColorIndexFromPalette(val, colorLo, colorHi, palette, false)
        :ColorEncoder.getColorIndexFromPalette(-val, -colorLo, -colorHi, palette, false));    
  }

}
