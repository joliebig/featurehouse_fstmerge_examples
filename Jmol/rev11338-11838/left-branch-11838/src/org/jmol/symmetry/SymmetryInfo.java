

package org.jmol.symmetry;

import javax.vecmath.Point3f;
import java.util.Hashtable;

class SymmetryInfo {

  boolean coordinatesAreFractional;
  boolean isMultiCell;
  String spaceGroup;
  String[] symmetryOperations;
  String symmetryInfoString;
  int modelIndex;
  int[] cellRange;
  private Point3f periodicOriginXyz;

  boolean isPeriodic() {
    return periodicOriginXyz != null;
  }

  SymmetryInfo() {    
  }
  
  float[] setSymmetryInfo(int modelIndex, Hashtable info) {
    this.modelIndex = modelIndex;
    cellRange = (int[]) info.get("unitCellRange");
    periodicOriginXyz = (Point3f) info.get("periodicOriginXyz");
    spaceGroup = (String) info.get("spaceGroup");
    if (spaceGroup == null || spaceGroup == "")
      spaceGroup = "spacegroup unspecified";
    int symmetryCount = info.containsKey("symmetryCount") ? 
        ((Integer) info.get("symmetryCount")).intValue() 
        : 0;
    symmetryOperations = (String[]) info.get("symmetryOperations");
    symmetryInfoString = "Spacegroup: " + spaceGroup;
    if (symmetryOperations == null) {
      symmetryInfoString += "\nNumber of symmetry operations: ?"
          + "\nSymmetry Operations: unspecified\n";
    } else {
      symmetryInfoString += "\nNumber of symmetry operations: "
          + (symmetryCount == 0 ? 1 : symmetryCount) + "\nSymmetry Operations:";
      for (int i = 0; i < symmetryCount; i++)
        symmetryInfoString += "\n" + symmetryOperations[i];
    }
    symmetryInfoString += "\n";
    coordinatesAreFractional = info.containsKey("coordinatesAreFractional") ? 
        ((Boolean) info.get("coordinatesAreFractional")).booleanValue() 
        : false;    
    isMultiCell = (coordinatesAreFractional && symmetryOperations != null);
    float[] notionalUnitcell = (float[]) info.get("notionalUnitcell");
    return (notionalUnitcell == null || notionalUnitcell[0] == 0 ? null : notionalUnitcell);
  }
}

