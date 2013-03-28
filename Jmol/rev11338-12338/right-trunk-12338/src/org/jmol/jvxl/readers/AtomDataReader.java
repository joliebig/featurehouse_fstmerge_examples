
package org.jmol.jvxl.readers;

import java.util.BitSet;
import java.util.Date;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;

import org.jmol.util.Logger;
import org.jmol.util.TextFormat;
import org.jmol.viewer.JmolConstants;

import org.jmol.atomdata.AtomData;
import org.jmol.atomdata.AtomDataServer;
import org.jmol.atomdata.RadiusData;
import org.jmol.jvxl.data.JvxlCoder;

abstract class AtomDataReader extends VolumeDataReader {

  protected AtomDataServer atomDataServer;

  AtomDataReader(SurfaceGenerator sg) {
    super(sg);
    precalculateVoxelData = true;
    atomDataServer = sg.getAtomDataServer();
  }

  protected String fileName;
  protected String fileDotModel;
  protected int modelIndex;

  protected AtomData atomData = new AtomData();
  
  protected Point3f[] atomXyz;
  protected float[] atomRadius;
  protected float[] atomProp;
  protected int[] atomNo;
  protected int[] atomIndex;
  protected int[] myIndex;
  protected int atomCount;
  protected int myAtomCount;
  protected int nearbyAtomCount;
  protected int firstNearbyAtom;
  protected BitSet bsMySelected, bsMyIgnored;

  protected boolean doAddHydrogens;
  protected boolean doUsePlane;
  protected boolean doUseIterator;


  protected void setup() {
    
    params.iUseBitSets = true;
    doAddHydrogens = (atomDataServer != null && params.addHydrogens); 
    modelIndex = params.modelIndex;
    bsMySelected = new BitSet();
    bsMyIgnored = (params.bsIgnore == null ? new BitSet() : params.bsIgnore);
    
    doUsePlane = (params.thePlane != null);
    if (doUsePlane)
      volumeData.setPlaneParameters(params.thePlane);
  }

  protected void getAtoms(float marginAtoms, boolean doGetAllAtoms,
                          boolean addNearbyAtoms) {

    if (params.atomRadiusData == null)
      params.atomRadiusData = new RadiusData(1, RadiusData.TYPE_FACTOR, JmolConstants.VDW_AUTO);
    atomData.radiusData = params.atomRadiusData;
    if (doAddHydrogens)
      atomData.radiusData.vdwType = JmolConstants.VDW_NOJMOL;
    atomData.modelIndex = modelIndex; 
    atomData.bsSelected = (doUseIterator ? null : params.bsSelected);
    atomData.bsIgnored = bsMyIgnored;
    atomDataServer.fillAtomData(atomData, AtomData.MODE_FILL_COORDS_AND_RADII);
    atomCount = atomData.atomCount;
    modelIndex = atomData.firstModelIndex;
    int nSelected = 0;
    boolean needRadius = false;
    boolean isAll = (params.bsSelected == null); 
    int i0 = (isAll ? atomCount - 1 : params.bsSelected.nextSetBit(0));
    for (int i = i0; i >= 0; i = (isAll ? i - 1 : params.bsSelected.nextSetBit(i + 1))) {
      if (!bsMyIgnored.get(i)) {
        if (doUsePlane
            && Math.abs(volumeData.distancePointToPlane(atomData.atomXyz[i])) > 2 * (atomData.atomRadius[i] = getWorkingRadius(
                i, marginAtoms)))
          continue;
        bsMySelected.set(i);
        nSelected++;
        needRadius = !doUsePlane;
      }
      if (addNearbyAtoms || needRadius) {
        atomData.atomRadius[i] = getWorkingRadius(i, marginAtoms);
      }
    }
    float rH = (doAddHydrogens ? getWorkingRadius(-1, marginAtoms) : 0);
    myAtomCount = BitSetUtil.cardinalityOf(bsMySelected);
    BitSet atomSet = BitSetUtil.copy(bsMySelected);
    int nH = 0;
    atomProp = null;
    if (myAtomCount > 0) {
      Point3f[] hAtoms = null;
      if (doAddHydrogens) {
        atomData.bsSelected = atomSet;
        atomDataServer.fillAtomData(atomData,
            AtomData.MODE_GET_ATTACHED_HYDROGENS);
        hAtoms = new Point3f[nH = atomData.hydrogenAtomCount];
        for (int i = 0; i < atomData.hAtoms.length; i++)
          if (atomData.hAtoms[i] != null)
            for (int j = atomData.hAtoms[i].length; --j >= 0;)
              hAtoms[--nH] = atomData.hAtoms[i][j];
        nH = hAtoms.length;
        Logger.info(nH + " attached hydrogens added");
      }
      int n = nH + myAtomCount;
      atomRadius = new float[n];
      atomXyz = new Point3f[n];
      if (params.theProperty != null)
        atomProp = new float[n];
      atomNo = new int[n];
      if (doUseIterator) {
        atomIndex = new int[n];
        myIndex = new int[atomCount];
      }

      for (int i = 0; i < nH; i++) {
        atomRadius[i] = rH;
        atomXyz[i] = hAtoms[i];
        atomNo[i] = -1;
        if (atomProp != null)
          atomProp[i] = Float.NaN;
        
        
        
      }
      myAtomCount = nH;
      float[] props = params.theProperty;
      for (int i = atomSet.nextSetBit(0); i >= 0; i = atomSet.nextSetBit(i+1)) {
        if (atomProp != null)
          atomProp[myAtomCount] = (props != null && i < props.length ? props[i]
              : Float.NaN);
        atomXyz[myAtomCount] = atomData.atomXyz[i];
        atomNo[myAtomCount] = atomData.atomicNumber[i];
        if (doUseIterator) {
          atomIndex[myAtomCount] = i;
          myIndex[i] = myAtomCount;
        }
        atomRadius[myAtomCount++] = atomData.atomRadius[i];
      }
    }
    firstNearbyAtom = myAtomCount;
    Logger.info(myAtomCount + " atoms will be used in the surface calculation");

    for (int i = 0; i < myAtomCount; i++)
      setBoundingBox(atomXyz[i], atomRadius[i]);
    if (!Float.isNaN(params.scale)) {
      Vector3f v = new Vector3f(xyzMax);
      v.sub(xyzMin);
      v.scale(0.5f);
      xyzMin.add(v);
      v.scale(params.scale);
      xyzMax.set(xyzMin);
      xyzMax.add(v);
      xyzMin.sub(v);
    }

    

    if (!addNearbyAtoms)
      return;
    Point3f pt = new Point3f();

    BitSet bsNearby = new BitSet();
    BitSet bs = new BitSet();
    bs.or(atomSet);
    BitSetUtil.andNot(bs, bsMyIgnored);
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i+1)) {      float rA = atomData.atomRadius[i];
      if (params.thePlane != null
          && Math.abs(volumeData.distancePointToPlane(atomData.atomXyz[i])) > 2 * rA)
        continue;
      pt = atomData.atomXyz[i];
      if (pt.x + rA > xyzMin.x && pt.x - rA < xyzMax.x && pt.y + rA > xyzMin.y
          && pt.y - rA < xyzMax.y && pt.z + rA > xyzMin.z
          && pt.z - rA < xyzMax.z) {
        bsNearby.set(i);
        nearbyAtomCount++;
      }
    }
    int nAtoms = myAtomCount;
    if (nearbyAtomCount != 0) {
      nAtoms += nearbyAtomCount;
      atomRadius = (float[]) ArrayUtil.setLength(atomRadius, nAtoms);
      atomXyz = (Point3f[]) ArrayUtil.setLength(atomXyz, nAtoms);
      for (int i = bsNearby.nextSetBit(0); i >= 0; i = bsNearby.nextSetBit(i+1)) {
        atomXyz[myAtomCount] = atomData.atomXyz[i];
        atomRadius[myAtomCount++] = atomData.atomRadius[i];
      }
    }
  }

  private float getWorkingRadius(int i, float marginAtoms) {
    float r = (i < 0 ? atomData.hAtomRadius : atomData.atomRadius[i]);
    if (!Float.isNaN(marginAtoms))
      return r + marginAtoms;
    switch (params.atomRadiusData.type) {
    case RadiusData.TYPE_ABSOLUTE:
      r = params.atomRadiusData.value;
      break;
    case RadiusData.TYPE_OFFSET:
      r += params.atomRadiusData.value;
      break;
    case RadiusData.TYPE_FACTOR:
      r *= params.atomRadiusData.value;
      break;
    }
    r += params.solventExtendedAtomRadius;
    if (r < 0.1)
      r = 0.1f;
    return r;
  }

  protected void setHeader(String calcType, String line2) {
    jvxlFileHeaderBuffer = new StringBuffer();
    if (atomData.programInfo != null)
      jvxlFileHeaderBuffer.append("#created by ").append(atomData.programInfo).append(" on ").append(new Date()).append("\n");
    jvxlFileHeaderBuffer.append(calcType).append("\n").append(line2).append("\n");
  }

  protected void setRangesAndAddAtoms(float ptsPerAngstrom, int maxGrid,
                                      int nWritten) {
    if (xyzMin == null)
      return;
    setVoxelRange(0, xyzMin.x, xyzMax.x, ptsPerAngstrom, maxGrid);
    setVoxelRange(1, xyzMin.y, xyzMax.y, ptsPerAngstrom, maxGrid);
    setVoxelRange(2, xyzMin.z, xyzMax.z, ptsPerAngstrom, maxGrid);
    JvxlCoder.jvxlCreateHeader(volumeData, nWritten, atomXyz,
        atomNo, jvxlFileHeaderBuffer);
  }
  
  protected boolean fixTitleLine(int iLine) {
    if (params.title == null)
      return false;
    String line = params.title[iLine];
    if (line.indexOf("%F") > 0)
      line = params.title[iLine] = TextFormat.formatString(line, "F", atomData.fileName);
    if (line.indexOf("%M") > 0)
      params.title[iLine] = TextFormat.formatString(line, "M", atomData.modelName);
    return true;
  }
}
