

package org.jmol.shape;

import org.jmol.script.Token;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.atomdata.RadiusData;
import org.jmol.g3d.Graphics3D;
import org.jmol.geodesic.EnvelopeCalculation;
import org.jmol.modelset.Atom;

import java.util.BitSet;
import java.util.Hashtable;

public class Dots extends AtomShape {

  public EnvelopeCalculation ec;
  public boolean isSurface = false;

  final static float SURFACE_DISTANCE_FOR_CALCULATION = 10f;

  BitSet bsOn = new BitSet();
  private BitSet bsSelected, bsIgnore;

  static int MAX_LEVEL = EnvelopeCalculation.MAX_LEVEL;

  int thisAtom;
  float thisRadius;
  int thisArgb;

  RadiusData rdLast = new RadiusData();

  public void initShape() {
    super.initShape();
    translucentAllowed = false; 
    ec = new EnvelopeCalculation(viewer, atomCount, mads);
  }

  public int getSize(int atomIndex) {
    
    return (mads == null ? (int) (ec.getRadius(atomIndex) * 2000) : mads[atomIndex]*2);
  }
  
  public void setProperty(String propertyName, Object value, BitSet bs) {

    if ("init" == propertyName) {
      initialize();
      return;
    }

    if ("translucency" == propertyName) {
      if (!translucentAllowed)
        return; 
    }

    if ("ignore" == propertyName) {
      bsIgnore = (BitSet) value;
      return;
    }

    if ("select" == propertyName) {
      bsSelected = (BitSet) value;
      return;
    }

    
    if ("radius" == propertyName) {
      thisRadius = ((Float) value).floatValue();
      if (thisRadius > Atom.RADIUS_MAX)
        thisRadius = Atom.RADIUS_MAX;
      return;
    }
    if ("colorRGB" == propertyName) {
      thisArgb = ((Integer) value).intValue();
      return;
    }
    if ("atom" == propertyName) {
      thisAtom = ((Integer) value).intValue();
      atoms[thisAtom].setShapeVisibility(myVisibilityFlag, true);
      ec.allocDotsConvexMaps(atomCount);
      return;
    }
    if ("dots" == propertyName) {
      isActive = true;
      ec.setFromBits(thisAtom, (BitSet) value);
      atoms[thisAtom].setShapeVisibility(myVisibilityFlag, true);
      if (mads == null) {
        ec.setMads(null);
        mads = new short[atomCount];
        for (int i = 0; i < atomCount; i++)          
          if (atoms[i].isInFrame() && atoms[i].isShapeVisible(myVisibilityFlag)) 
            
            mads[i] = (short) (ec.getAppropriateRadius(i) * 1000);
        ec.setMads(mads);
      }
      mads[thisAtom] = (short) (thisRadius * 1000f);
      if (colixes == null) {
        colixes = new short[atomCount];
        paletteIDs = new byte[atomCount];
      }
      colixes[thisAtom] = Graphics3D.getColix(thisArgb);
      bsOn.set(thisAtom);
      
      return;
    }

    if ("refreshTrajectories" == propertyName) {
      bsSelected = null;
      setSize(0, bs);
      return;
    }

    if (propertyName == "deleteModelAtoms") {
      int firstAtomDeleted = ((int[])((Object[])value)[2])[1];
      int nAtomsDeleted = ((int[])((Object[])value)[2])[2];
      BitSetUtil.deleteBits(bsOn, bs);
      ec.deleteAtoms(firstAtomDeleted, nAtomsDeleted, bs);
      
    }

    super.setProperty(propertyName, value, bs);
  }

  void initialize() {
    bsSelected = null;
    bsIgnore = null;
    isActive = false;
    if (ec == null)
      ec = new EnvelopeCalculation(viewer, atomCount, mads);
  }

  public void setSize(RadiusData rd, BitSet bsSelected) {
    if (this.bsSelected != null)
      bsSelected = this.bsSelected;

    
    
    
    
    
    
    
    

    if (Logger.debugging) {
      Logger.debug("Dots.setSize " + rd.value);
    }

    boolean isVisible = true;
    float setRadius = Float.MAX_VALUE;
    isActive = true;

    switch (rd.type) {
    case RadiusData.TYPE_ABSOLUTE:
      if (rd.value == 0)
        isVisible = false;
      setRadius = rd.value;
      break;
    case RadiusData.TYPE_OFFSET:
      break;
    }

    if (rd.type != RadiusData.TYPE_OFFSET)
      rd.valueExtended = viewer.getCurrentSolventProbeRadius();

    float maxRadius;
    switch (rd.vdwType) {
    case Token.adpmin:
    case Token.adpmax:
    case Token.temperature:
      maxRadius = setRadius;
      break;
    case Token.ionic:
      maxRadius = modelSet.getMaxVanderwaalsRadius() * 2; 
      break;
    default:
      maxRadius = modelSet.getMaxVanderwaalsRadius();
    }

    if (Logger.debugging)
      Logger.startTimer();

    
    boolean newSet = (rdLast.value != rd.value
        || rdLast.valueExtended != rd.valueExtended || rdLast.type != rd.type
        || rdLast.vdwType != rd.vdwType || ec.getDotsConvexMax() == 0);

    
    

    if (isVisible) {
      for (int i = atomCount; --i >= 0;)
        if (bsSelected.get(i) && !bsOn.get(i)) {
          bsOn.set(i);
          newSet = true;
        }
    } else {
      for (int i = atomCount; --i >= 0;)
        if (bsSelected.get(i))
          bsOn.set(i, false);
    }

    for (int i = atomCount; --i >= 0;) {
      atoms[i].setShapeVisibility(myVisibilityFlag, bsOn.get(i));
    }
    if (newSet) {
      mads = null;
      ec.newSet();
    }
    
    int[][] dotsConvexMaps = ec.getDotsConvexMaps();
    if (isVisible && dotsConvexMaps != null) {
      for (int i = atomCount; --i >= 0;)
        if (bsOn.get(i)) {
          dotsConvexMaps[i] = null;
        }
    }
    
    if (isVisible) {
      if (dotsConvexMaps == null) {
        colixes = new short[atomCount];
        paletteIDs = new byte[atomCount];
      }
      ec.calculate(rd, maxRadius, bsOn, bsIgnore, !viewer.getDotSurfaceFlag(),
          viewer.getDotsSelectedOnlyFlag(), isSurface, true);
    }
    
    rdLast = rd;
    
    if (Logger.debugging)
      Logger.checkTimer("dots generation time");
  }

  public void setModelClickability() {
    for (int i = atomCount; --i >= 0;) {
      Atom atom = atoms[i];
      if ((atom.getShapeVisibilityFlags() & myVisibilityFlag) == 0
          || modelSet.isAtomHidden(i))
        continue;
      atom.setClickable(myVisibilityFlag);
    }
  }

  public String getShapeState() {
    int[][] dotsConvexMaps = ec.getDotsConvexMaps();
    if (dotsConvexMaps == null || ec.getDotsConvexMax() == 0)
      return "";
    StringBuffer s = new StringBuffer();
    Hashtable temp = new Hashtable();
  
    int atomCount = viewer.getAtomCount();
    String type = (isSurface ? "geoSurface " : "dots ");
  
    for (int i = 0; i < atomCount; i++) {
      if (dotsConvexMaps[i] == null || !bsOn.get(i))
        continue;
      if (bsColixSet != null && bsColixSet.get(i))
        setStateInfo(temp, i, getColorCommand(type, paletteIDs[i], colixes[i]));
      BitSet bs = new BitSet();
      int[] map = dotsConvexMaps[i];
      int iDot = map.length << 5;
      int n = 0;
      while (--iDot >= 0)
        if (EnvelopeCalculation.getBit(map, iDot)) {
          n++;
          bs.set(iDot);
        }
      
      if (n > 0) {
        float r = ec.getAppropriateRadius(i);
        appendCmd(s, type + i + " radius " + r + " "
            + Escape.escape(bs));
  
  
  
  
      }
    }
    s.append(getShapeCommands(temp, null, atomCount));
   
   
   
   
    return s.toString();
  }

}
