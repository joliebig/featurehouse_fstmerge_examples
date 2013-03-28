
package org.jmol.jvxl.readers;

import org.jmol.api.Interface;
import org.jmol.api.MepCalculationInterface;

class IsoMepReader extends AtomDataReader {

  IsoMepReader(SurfaceGenerator sg) {
    super(sg);
  }
    
  

  protected void setup() {
    super.setup();
    doAddHydrogens = false;
    getAtoms(params.mep_marginAngstroms, true, false);
    setHeader("MEP", "");
    setRangesAndAddAtoms(params.mep_ptsPerAngstrom, params.mep_gridMax, myAtomCount);    
  }

  protected void generateCube() {
    volumeData.voxelData = voxelData = new float[nPointsX][nPointsY][nPointsZ];
    MepCalculationInterface m = (MepCalculationInterface) Interface.getOptionInterface("quantum.MepCalculation");
    m.calculate(volumeData, bsMySelected, atomData.atomXyz,
          params.theProperty);
  }
}
