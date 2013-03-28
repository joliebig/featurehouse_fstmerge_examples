
package org.jmol.jvxl.readers;

import java.util.Hashtable;
import java.util.Vector;

import org.jmol.util.Logger;
import org.jmol.util.TextFormat;
import org.jmol.api.Interface;
import org.jmol.api.MOCalculationInterface;

class IsoMOReader extends AtomDataReader {

  IsoMOReader(SurfaceGenerator sg) {
    super(sg);
  }
  
  

  protected void setup() {
    super.setup();
    doAddHydrogens = false;
    getAtoms(params.qm_marginAngstroms, true, false);
    setHeader("MO", "calculation type: " + params.moData.get("calculationType"));
    setRangesAndAddAtoms(params.qm_ptsPerAngstrom, params.qm_gridMax, myAtomCount);
    for (int i = params.title.length; --i >= 0;)
      fixTitleLine(i, params.mo);
  }
  
  private void fixTitleLine(int iLine, Hashtable mo) {
    if (!fixTitleLine(iLine))
       return;
    String line = params.title[iLine];
    int pt = line.indexOf("%");
    if (line.length() == 0 || pt < 0)
      return;
    int rep = 0;
    if (line.indexOf("%F") >= 0)
      line = TextFormat.formatString(line, "F", params.fileName);
    if (line.indexOf("%I") >= 0)
      line = TextFormat.formatString(line, "I", "" + params.qm_moNumber);
    if (line.indexOf("%N") >= 0)
      line = TextFormat.formatString(line, "N", "" + params.qmOrbitalCount);
    if (line.indexOf("%E") >= 0)
      line = TextFormat.formatString(line, "E", "" + mo.get("energy"));
    if (line.indexOf("%U") >= 0)
      line = TextFormat.formatString(line, "U", params.moData.containsKey("energyUnits") && ++rep != 0 ? (String) params.moData.get("energyUnits") : "");
    if (line.indexOf("%S") >= 0)
      line = TextFormat.formatString(line, "S", mo.containsKey("symmetry") && ++rep != 0 ? "" + mo.get("symmetry") : "");
    if (line.indexOf("%O") >= 0)
      line = TextFormat.formatString(line, "O", mo.containsKey("occupancy") && ++rep != 0  ? "" + mo.get("occupancy") : "");
    if (line.indexOf("%T") >= 0)
      line = TextFormat.formatString(line, "T", mo.containsKey("type") && ++rep != 0  ? "" + mo.get("type") : "");
    boolean isOptional = (line.indexOf("?") == 0);
    params.title[iLine] = (!isOptional ? line : rep > 0 ? line.substring(1) : "");
  }
  
  protected void generateCube() {
    volumeData.voxelData = voxelData = new float[nPointsX][nPointsY][nPointsZ];
    MOCalculationInterface q = (MOCalculationInterface) Interface.getOptionInterface("quantum.MOCalculation");
    Hashtable moData = params.moData;
    float[] coef = params.moCoefficients; 
    
    if (coef == null) {
      
      Vector mos = (Vector) (moData.get("mos"));
      for (int i = params.qm_moNumber; --i >= 0; ) {
        Logger.info(" generating isosurface data for MO " + (i + 1));
        Hashtable mo = (Hashtable) mos.get(i);
        coef = (float[]) mo.get("coefficients");
        getData(q, moData, coef, params.theProperty);
      }
    } else {
      getData(q, moData, coef, null);
    }
  }
  
  private void getData(MOCalculationInterface q, Hashtable moData,
                       float[] coef, float[] nuclearCharges) {
    switch (params.qmOrbitalType) {
    case Parameters.QM_TYPE_GAUSSIAN:
      q.calculate(volumeData, bsMySelected, (String) moData
          .get("calculationType"), atomData.atomXyz, atomData.firstAtomIndex,
          (Vector) moData.get("shells"), (float[][]) moData.get("gaussians"),
          (Hashtable) moData.get("atomicOrbitalOrder"), null, null, coef,
          nuclearCharges);
      break;
    case Parameters.QM_TYPE_SLATER:
      q.calculate(volumeData, bsMySelected, (String) moData
          .get("calculationType"), atomData.atomXyz, atomData.firstAtomIndex,
          null, null, null, (int[][]) moData.get("slaterInfo"),
          (float[][]) moData.get("slaterData"), coef, nuclearCharges);
      break;
    default:
    }

  }
}
