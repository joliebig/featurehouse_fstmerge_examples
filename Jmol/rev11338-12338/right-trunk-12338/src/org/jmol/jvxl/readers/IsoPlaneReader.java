
package org.jmol.jvxl.readers;

class IsoPlaneReader extends AtomDataReader {

  IsoPlaneReader(SurfaceGenerator sg) {
    super(sg);
    precalculateVoxelData = false;
  }

  protected void setup() {
    super.setup();
    doAddHydrogens = false;
    getAtoms(params.mep_marginAngstroms, false, false);
    setHeader("PLANE", params.thePlane.toString());
    setRangesAndAddAtoms(params.solvent_ptsPerAngstrom, params.solvent_gridMax, Math.min(myAtomCount, 100)); 
    params.cutoff = 0;
  }

  public float getValue(int x, int y, int z, int ptyz) {    
    return  volumeData.calcVoxelPlaneDistance(x, y, z);
  }

}
