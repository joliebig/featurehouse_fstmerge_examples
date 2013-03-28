

package org.jmol.shapebio;

import java.util.BitSet;
import java.util.Hashtable;

import javax.vecmath.Vector3f;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelset.Atom;
import org.jmol.modelsetbio.BioPolymer;
import org.jmol.modelsetbio.Monomer;
import org.jmol.modelsetbio.NucleicMonomer;
import org.jmol.modelsetbio.NucleicPolymer;
import org.jmol.shape.Shape;
import org.jmol.shape.Mesh;
import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;

class BioShape {

  int modelIndex;
  int modelVisibilityFlags = 0;

  BioShapeCollection shape;
  
  BioPolymer bioPolymer;
  
  Mesh[] meshes;
  boolean[] meshReady;

  short[] mads;
  short[] colixes;
  byte[] paletteIDs;

  BitSet bsColixSet;
  BitSet bsSizeSet;
  BitSet bsSizeDefault = new BitSet();
  boolean isActive;
  
  int monomerCount;
  Monomer[] monomers;

  
  
  Vector3f[] wingVectors;
  int[] leadAtomIndices;

  BioShape(BioShapeCollection shape, int modelIndex, BioPolymer bioPolymer) {
    this.shape = shape;
    this.modelIndex = modelIndex;
    this.bioPolymer = bioPolymer;
    isActive = shape.isActive;
    monomerCount = bioPolymer.getMonomerCount();
    if (monomerCount > 0) {
      colixes = new short[monomerCount];
      paletteIDs = new byte[monomerCount];
      mads = new short[monomerCount + 1];
      monomers = bioPolymer.getMonomers();
      meshReady = new boolean[monomerCount];
      meshes = new Mesh[monomerCount];
      wingVectors = bioPolymer.getWingVectors();
      leadAtomIndices = bioPolymer.getLeadAtomIndices();
      
    }
  }

  boolean hasBfactorRange = false;
  int bfactorMin, bfactorMax;
  int range;
  float floatRange;

  void calcBfactorRange() {
    bfactorMin = bfactorMax =
      monomers[0].getLeadAtom().getBfactor100();
    for (int i = monomerCount; --i > 0; ) {
      int bfactor =
        monomers[i].getLeadAtom().getBfactor100();
      if (bfactor < bfactorMin)
        bfactorMin = bfactor;
      else if (bfactor > bfactorMax)
        bfactorMax = bfactor;
    }
    range = bfactorMax - bfactorMin;
    floatRange = range;
    hasBfactorRange = true;
  }

  private final static double eightPiSquared100 = 8 * Math.PI * Math.PI * 100;
  
  short calcMeanPositionalDisplacement(int bFactor100) {
    return (short)(Math.sqrt(bFactor100/eightPiSquared100) * 1000);
  }

  void findNearestAtomIndex(int xMouse, int yMouse, Atom[] closest) {
    bioPolymer.findNearestAtomIndex(xMouse, yMouse, closest, mads,
        shape.myVisibilityFlag);
  }
  
  void setMad(short mad, BitSet bsSelected) {
    isActive = true;
    if (bsSizeSet == null)
      bsSizeSet = new BitSet();
    int flag = shape.myVisibilityFlag;
    for (int i = monomerCount; --i >= 0; ) {
      int leadAtomIndex = leadAtomIndices[i];
      if (bsSelected.get(leadAtomIndex)) {
        boolean isVisible = ((mads[i] = setMad(i, mad)) > 0);
        bsSizeSet.set(i, isVisible);
        monomers[i].setShapeVisibility(flag, isVisible);
        shape.atoms[leadAtomIndex].setShapeVisibility(flag,isVisible);
        falsifyMesh(i, true);
      }
    }
    if (monomerCount > 1)
      mads[monomerCount] = mads[monomerCount - 1];
  }

  private short setMad(int groupIndex, short mad) {
    
    bsSizeDefault.set(groupIndex, mad == -1 || mad == -2);
    if (mad >= 0)
      return mad;      
    switch (mad) {
    case -1: 
    case -2: 
      if (mad == -1 && shape.madOn >= 0)
        return shape.madOn;
      switch (monomers[groupIndex].getProteinStructureType()) {
      case JmolConstants.PROTEIN_STRUCTURE_SHEET:
      case JmolConstants.PROTEIN_STRUCTURE_HELIX:
        return shape.madHelixSheet;
      case JmolConstants.PROTEIN_STRUCTURE_DNA:
      case JmolConstants.PROTEIN_STRUCTURE_RNA:
        return shape.madDnaRna;
      default:
        return shape.madTurnRandom;
      }
    case -3: 
      {
        if (! hasBfactorRange)
          calcBfactorRange();
        Atom atom = monomers[groupIndex].getLeadAtom();
        int bfactor100 = atom.getBfactor100(); 
        int scaled = bfactor100 - bfactorMin;
        if (range == 0)
          return (short)0;
        float percentile = scaled / floatRange;
        if (percentile < 0 || percentile > 1)
          Logger.error("Que ha ocurrido? " + percentile);
        return (short)((1750 * percentile) + 250);
      }
    case -4: 
      {
        Atom atom = monomers[groupIndex].getLeadAtom();
        return 
          (short)(2 * calcMeanPositionalDisplacement(atom.getBfactor100()));
      }
    }
    Logger.error("unrecognized setMad(" + mad + ")");
    return 0;
  }

  void falsifyMesh(int index, boolean andNearby) {
    if (meshReady == null)
      return;
    meshReady[index] = false;
    if (!andNearby)
      return;
    if (index > 0)
      meshReady[index - 1] = false;
    if (index < monomerCount - 1)
      meshReady[index + 1] = false;
  }    

  void setColix(short colix, byte pid, BitSet bsSelected) {
    isActive = true;
    if (bsColixSet == null)
      bsColixSet = new BitSet();
    for (int i = monomerCount; --i >= 0;) {
      int atomIndex = leadAtomIndices[i];
      if (bsSelected.get(atomIndex)) {
        colixes[i] = shape.setColix(colix, pid, atomIndex);
        paletteIDs[i] = pid;
        bsColixSet.set(i, colixes[i] != Graphics3D.INHERIT_ALL);
      }
    }
  }
  
  void setTranslucent(boolean isTranslucent, BitSet bsSelected, float translucentLevel) {
    isActive = true;
    if (bsColixSet == null)
      bsColixSet = new BitSet();
    for (int i = monomerCount; --i >= 0; )
      if (bsSelected.get(leadAtomIndices[i])) {
        colixes[i] = Graphics3D.getColixTranslucent(colixes[i], isTranslucent, translucentLevel);
        bsColixSet.set(i, colixes[i] != Graphics3D.INHERIT_ALL);
    }
  }

  void setShapeState(Hashtable temp, Hashtable temp2) {
    if (!isActive)
      return;
    String type = JmolConstants.shapeClassBases[shape.shapeID];
    for (int i = 0; i < monomerCount; i++) {
      int atomIndex1 = monomers[i].getFirstAtomIndex();
      int atomIndex2 = monomers[i].getLastAtomIndex();
      if (bsSizeSet != null && (bsSizeSet.get(i) 
          || bsColixSet != null && bsColixSet.get(i))) {
        if (bsSizeDefault.get(i))
          Shape.setStateInfo(temp, atomIndex1, atomIndex2, type + (bsSizeSet.get(i) ? " on" : " off"));
        else
          Shape.setStateInfo(temp, atomIndex1, atomIndex2, type + " "
              + (mads[i] / 2000f));
      }
      if (bsColixSet != null && bsColixSet.get(i))
        Shape.setStateInfo(temp2, atomIndex1, atomIndex2, shape
            .getColorCommand(type, paletteIDs[i], colixes[i]));
    }
  }  

 void setModelClickability() {
    if (!isActive || wingVectors == null)
      return;
    boolean isNucleicPolymer = bioPolymer instanceof NucleicPolymer;
    for (int i = monomerCount; --i >= 0;) {
      if (mads[i] <= 0)
        continue;
      int iAtom = leadAtomIndices[i];
      if (monomers[i].getModel().isAtomHidden(iAtom))
        continue;
      shape.atoms[iAtom].setClickable(JmolConstants.ALPHA_CARBON_VISIBILITY_FLAG);
      if (isNucleicPolymer)
        ((NucleicMonomer) monomers[i]).setModelClickability();
    }
  }
  
}