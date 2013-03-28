

package org.jmol.shapebio;

import java.util.BitSet;
import java.util.Hashtable;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.modelset.Model;
import org.jmol.modelsetbio.BioPolymer;
import org.jmol.shape.Shape;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.viewer.JmolConstants;

public abstract class BioShapeCollection extends Shape {

  Atom[] atoms;
  
  short madOn = -2;
  short madHelixSheet = 3000;
  short madTurnRandom = 800;
  short madDnaRna = 5000;
  boolean isActive = false;
  
  BioShape[] bioShapes;
  
  public final void initModelSet() {
    isBioShape = true;
    atoms = modelSet.atoms;
    initialize();
  }

  public void setSize(int size, BitSet bsSelected) {
    short mad = (short) size;
    initialize();
    for (int i = bioShapes.length; --i >= 0;) {
      BioShape bioShape = bioShapes[i];
      if (bioShape.monomerCount > 0)
        bioShape.setMad(mad, bsSelected);
    }
  }

  public void setProperty(String propertyName, Object value, BitSet bsSelected) {

    if (propertyName == "deleteModelAtoms") {
      atoms = (Atom[])((Object[])value)[1];
      int modelIndex = ((int[])((Object[])value)[2])[0];
      for (int i = bioShapes.length; --i >= 0; ){
        BioShape b = bioShapes[i];
        if (b.modelIndex > modelIndex) {
          b.modelIndex--;
          b.leadAtomIndices = b.bioPolymer.getLeadAtomIndices();
        } else if (b.modelIndex == modelIndex) {
          bioShapes = (BioShape[]) ArrayUtil.deleteElements(bioShapes, i, 1);
        }
      }
      return;
    }

    initialize();
    if ("color" == propertyName) {
      byte pid = JmolConstants.pidOf(value);
      short colix = Graphics3D.getColix(value);
      for (int i = bioShapes.length; --i >= 0;) {
        BioShape bioShape = bioShapes[i];
        if (bioShape.monomerCount > 0)
          bioShape.setColix(colix, pid, bsSelected);
      }
      return;
    }
    if ("translucency" == propertyName) {
      boolean isTranslucent = ("translucent".equals(value));
      for (int i = bioShapes.length; --i >= 0;) {
        BioShape bioShape = bioShapes[i];
        if (bioShape.monomerCount > 0)
          bioShape.setTranslucent(isTranslucent, bsSelected, translucentLevel);
      }
      return;
    }
    
    super.setProperty(propertyName, value, bsSelected);
  }

  public String getShapeState() {
    Hashtable temp = new Hashtable();
    Hashtable temp2 = new Hashtable();    
    for (int i = bioShapes.length; --i >= 0; ) {
      BioShape bioShape = bioShapes[i];
      if (bioShape.monomerCount > 0)
        bioShape.setShapeState(temp, temp2);
    }
    return "\n" + getShapeCommands(temp, temp2, modelSet.getAtomCount()
        , shapeID == JmolConstants.SHAPE_BACKBONE ? "Backbone" : "select");
  }

  void initialize() {
    int modelCount = modelSet.getModelCount();
    Model[] models = modelSet.getModels();
    int n = modelSet.getBioPolymerCount();
    BioShape[] shapes = new BioShape[n--];
    for (int i = modelCount; --i >= 0;)
      for (int j = modelSet.getBioPolymerCountInModel(i); --j >= 0; n--)
        shapes[n] = (bioShapes == null || bioShapes.length <= n
            || bioShapes[n] == null ? new BioShape(this, i,
            (BioPolymer) models[i].getBioPolymer(j)) : bioShapes[n]);
    bioShapes = shapes;
  }

  public void findNearestAtomIndex(int xMouse, int yMouse, Atom[] closest) {
    for (int i = bioShapes.length; --i >= 0; ){
      BioShape b = bioShapes[i];
      b.bioPolymer.findNearestAtomIndex(xMouse, yMouse, closest, bioShapes[i].mads, myVisibilityFlag);      
    }
  }

  public void setVisibilityFlags(BitSet bs) {
    if (bioShapes == null)
      return;
    bs = BitSetUtil.copy(bs);
    for (int i = modelSet.getModelCount(); --i >= 0; )
      if (bs.get(i) && modelSet.isTrajectory(i))
        bs.set(modelSet.getTrajectoryIndex(i));
    
    for (int i = bioShapes.length; --i >= 0;) {
      BioShape b = bioShapes[i];
      b.modelVisibilityFlags = (bs.get(b.modelIndex) ? myVisibilityFlag : 0);
    }
  }

  public void setModelClickability() {
    if (bioShapes == null)
      return;
    for (int i = bioShapes.length; --i >= 0; )
      bioShapes[i].setModelClickability();
  }

  int getMpsShapeCount() {
    return bioShapes.length;
  }

  BioShape getBioShape(int i) {
    return bioShapes[i];
  }  
}
