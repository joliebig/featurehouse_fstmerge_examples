

package org.jmol.shape;

import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
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

  int lastSize = 0;
  float lastSolventRadius = 0;

  public void initShape() {
    super.initShape();
    translucentAllowed = false; 
    ec = new EnvelopeCalculation(viewer, atomCount, mads);
  }

  public void setProperty(String propertyName, Object value, BitSet bs) {

    if (Logger.debugging) {
      Logger.debug("Dots.setProperty: " + propertyName + " " + value);
    }

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

  public void setSize(int size, float fsize, BitSet bsSelected) {
    if (this.bsSelected != null)
      bsSelected = this.bsSelected;

    
    
    
    
    
    
    
    

    if (Logger.debugging) {
      Logger.debug("Dots.setSize " + size);
    }
    boolean isVisible = true;
    float addRadius = Float.MAX_VALUE;
    float setRadius = Float.MAX_VALUE;
    boolean useVanderwaalsRadius = true;
    float scale = 1;

    isActive = true;
    if (Float.isNaN(fsize)) {
      switch (size) {
      case 0:
        isVisible = false;
        break;
      case 1:
        break;
      default:
        if (size <= Short.MIN_VALUE) {
          setRadius = Short.MIN_VALUE;
          if (size < Short.MIN_VALUE)
            scale = (Short.MIN_VALUE - size) / 100f;
        } else if (size < 0) { 
          useVanderwaalsRadius = false;
        } else if (size <= 1001) {
          scale = (size - 1) / 100f;
        } else if (size <= 11002) {
          useVanderwaalsRadius = false;
          setRadius = (size - 1002) / 1000f;
        } else if (size <= 13002) {
          addRadius = (size - 11002) / 1000f;
          scale = 1;
        } else if (size >= Short.MAX_VALUE) {
          setRadius = Short.MAX_VALUE;
          if (size > Short.MAX_VALUE)
            scale = (size - Short.MAX_VALUE) / 100f;
        }
      }
    } else {
      if (size == 1) {
        addRadius = fsize;
      } else {
        useVanderwaalsRadius = false;
        setRadius = fsize;
      }
    }
    float maxRadius = (!useVanderwaalsRadius ? setRadius : modelSet
        .getMaxVanderwaalsRadius());
    float solventRadius = viewer.getCurrentSolventProbeRadius();
    if (addRadius == Float.MAX_VALUE)
      addRadius = (solventRadius != 0 ? solventRadius : 0);

    if (Logger.debugging)
      Logger.startTimer();

    
    boolean newSet = (lastSolventRadius != addRadius || size != 0
        && size != lastSize || ec.getDotsConvexMax() == 0);

    
    

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
      lastSolventRadius = addRadius;
    }
    
    int[][] dotsConvexMaps = ec.getDotsConvexMaps();
    if (isVisible && dotsConvexMaps != null) {
      for (int i = atomCount; --i >= 0;)
        if (bsOn.get(i)) {
          dotsConvexMaps[i] = null;
        }
    }
    
    if (isVisible) {
      lastSize = size;
      if (dotsConvexMaps == null) {
        colixes = new short[atomCount];
        paletteIDs = new byte[atomCount];
      }
      boolean disregardNeighbors = (viewer.getDotSurfaceFlag() == false);
      boolean onlySelectedDots = (viewer.getDotsSelectedOnlyFlag() == true);
      ec.calculate(addRadius, setRadius, scale, maxRadius, bsOn, bsIgnore,
          useVanderwaalsRadius, disregardNeighbors, onlySelectedDots,
          isSurface, true);
    }
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
