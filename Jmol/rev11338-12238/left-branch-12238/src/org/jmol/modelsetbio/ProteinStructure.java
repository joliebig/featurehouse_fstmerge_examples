
package org.jmol.modelsetbio;

import java.util.Hashtable;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.util.Logger;
import org.jmol.viewer.JmolConstants;

public abstract class ProteinStructure {

  static int globalSerialID = 1000;
  AlphaPolymer apolymer;
  byte type;
  int monomerIndexFirst;
  int monomerIndexLast;
  int monomerCount;
  Point3f axisA, axisB;
  Vector3f axisUnitVector;
  final Vector3f vectorProjection = new Vector3f();
  Point3f[] segments;
  int uniqueID;
  public String structureID;
  public int serialID;
  public int strandCount;
  
  ProteinStructure(AlphaPolymer apolymer, byte type,
                   int monomerIndex, int monomerCount, int id) {
    uniqueID = ++globalSerialID;
    this.apolymer = apolymer;
    this.type = type;    
    monomerIndexFirst = monomerIndex;
    addMonomer(monomerIndex + monomerCount - 1);
    
    if(Logger.debugging)
      Logger.debug(
          "Creating ProteinStructure " + uniqueID 
          + " " + JmolConstants.getProteinStructureName(type) 
          + " from " + monomerIndexFirst + " through "+(monomerIndexLast)
          + " in polymer " + apolymer);
  }
  
  
  void addMonomer(int index) {
    monomerIndexFirst = Math.min(monomerIndexFirst, index);
    monomerIndexLast = Math.max(monomerIndexLast, index);
    monomerCount = monomerIndexLast - monomerIndexFirst + 1;
  }

  
  int removeMonomer(int monomerIndex) {
    if (monomerIndex > monomerIndexLast || monomerIndex < monomerIndexFirst)
      return 0;
    int ret = monomerIndexLast - monomerIndex;
    monomerIndexLast = Math.max(monomerIndexFirst, monomerIndex) - 1;
    monomerCount = monomerIndexLast - monomerIndexFirst + 1;
    return ret;
  }

  public void calcAxis() {
  }

  void calcSegments() {
    if (segments != null)
      return;
    calcAxis();
    segments = new Point3f[monomerCount + 1];
    segments[monomerCount] = axisB;
    segments[0] = axisA;
    Vector3f axis = new Vector3f(axisUnitVector);
    axis.scale(axisB.distance(axisA) / monomerCount);
    for (int i = 1; i < monomerCount; i++) {
      Point3f point = segments[i] = new Point3f();
      point.set(segments[i - 1]);
      point.add(axis);
      
      
      
      
      
      
      
    }
  }

  boolean lowerNeighborIsHelixOrSheet() {
    if (monomerIndexFirst == 0)
      return false;
    return apolymer.monomers[monomerIndexFirst - 1].isHelix()
        || apolymer.monomers[monomerIndexFirst - 1].isSheet();
  }

  boolean upperNeighborIsHelixOrSheet() {
    int upperNeighborIndex = monomerIndexFirst + monomerCount;
    if (upperNeighborIndex == apolymer.monomerCount)
      return false;
    return apolymer.monomers[upperNeighborIndex].isHelix()
        || apolymer.monomers[upperNeighborIndex].isSheet();
  }

  public int getMonomerCount() {
    return monomerCount;
  }
  
  public boolean isWithin(int monomerIndex) {
    return (monomerIndex > monomerIndexFirst 
        && monomerIndex < monomerIndexLast);
  }

  public int getMonomerIndex() {
    return monomerIndexFirst;
  }

  public int getIndex(Monomer monomer) {
    Monomer[] monomers = apolymer.monomers;
    int i;
    for (i = monomerCount; --i >= 0; )
      if (monomers[monomerIndexFirst + i] == monomer)
        break;
    return i;
  }

  public Point3f[] getSegments() {
    if (segments == null)
      calcSegments();
    return segments;
  }

  public Point3f getAxisStartPoint() {
    calcAxis();
    return axisA;
  }

  public Point3f getAxisEndPoint() {
    calcAxis();
    return axisB;
  }

  Point3f getStructureMidPoint(int index) {
    if (segments == null)
      calcSegments();
    return segments[index];
  }

  public void getInfo(Hashtable info) {
    info.put("type", JmolConstants.getProteinStructureName(type));
    int[] leadAtomIndices = apolymer.getLeadAtomIndices();
    int[] iArray = new int[monomerCount];
    System.arraycopy(leadAtomIndices, monomerIndexFirst, iArray, 0, monomerCount);
    info.put("leadAtomIndices", iArray);
    calcAxis();
    if (axisA == null)
      return;
    info.put("axisA", axisA);
    info.put("axisB", axisB);
    info.put("axisUnitVector", axisUnitVector);
  }

  void resetAxes() {
    axisA = null;
    segments = null;
  }
}
