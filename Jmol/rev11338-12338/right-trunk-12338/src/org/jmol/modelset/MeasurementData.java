
package org.jmol.modelset;

import java.util.BitSet;
import java.util.Vector;

import org.jmol.api.JmolMeasurementClient;
import org.jmol.util.Point3fi;
import org.jmol.viewer.Viewer;

public class MeasurementData implements JmolMeasurementClient {

  
  
  private JmolMeasurementClient client;
  private Vector measurementStrings;

  private Atom[] atoms;
  public boolean mustBeConnected;
  public boolean mustNotBeConnected;
  public TickInfo tickInfo;
  public int tokAction;
  public Vector points;
  public float[] rangeMinMax;
  public String strFormat;
  public boolean isAll;

  private String units;
  
  
  public MeasurementData(Vector points, int tokAction,
                 float[] rangeMinMax, String strFormat, String units,
                 TickInfo tickInfo,
                 boolean mustBeConnected, boolean mustNotBeConnected,
                 boolean isAll) {
    this.tokAction = tokAction;
    this.points = points;
    this.rangeMinMax = rangeMinMax;
    this.strFormat = strFormat;
    this.units = units;
    this.tickInfo = tickInfo;
    this.mustBeConnected = mustBeConnected;
    this.mustNotBeConnected = mustNotBeConnected;
    this.isAll = isAll;
  }
  
  
  public void processNextMeasure(Measurement m) {
    float value = m.getMeasurement();
    if (rangeMinMax != null && rangeMinMax[0] != Float.MAX_VALUE
        && (value < rangeMinMax[0] || value > rangeMinMax[1]))
      return;
    
    measurementStrings.add(m.getString(viewer, strFormat, units));
  }

  
  public Vector getMeasurements(Viewer viewer) {
    this.viewer = viewer;
    measurementStrings = new Vector();
    define(null, viewer.getModelSet());
    return measurementStrings;
  }
  
  private Viewer viewer;
  
  
  public void define(JmolMeasurementClient client, ModelSet modelSet) {
    this.client = (client == null ? this : client);
    atoms = modelSet.getAtoms();
    
    int nPoints = points.size();
    if (nPoints < 2)
      return;
    int modelIndex = -1;
    Point3fi[] pts = new Point3fi[4];
    int[] indices = new int[5];
    Measurement m = new Measurement(modelSet, indices, pts, null);
    m.setCount(nPoints);
    int ptLastAtom = -1;
    for (int i = 0; i < nPoints; i++) {
      Object obj = points.get(i);
      if (obj instanceof BitSet) {
        BitSet bs = (BitSet) obj;
        int nAtoms = bs.cardinality(); 
        if (nAtoms == 0)
          return;
        if (nAtoms > 1)
          modelIndex = 0;
        ptLastAtom = i;
        indices[i + 1] = bs.nextSetBit(0);
      } else {
        if (pts == null)
          pts = new Point3fi[4];
        pts[i] = (Point3fi)obj;
        indices[i + 1] = -2 - i; 
      }
    }
    nextMeasure(0, ptLastAtom, m, modelIndex);
  }

  
  private void nextMeasure(int thispt, int ptLastAtom, Measurement m, int thisModel ) {
    if (thispt > ptLastAtom) {
      if (m.isValid() 
          && (!mustBeConnected || m.isConnected(atoms, thispt))
          && (!mustNotBeConnected || !m.isConnected(atoms, thispt)))
        client.processNextMeasure(m);
      return;
    }
    BitSet bs = (BitSet) points.get(thispt);
    int[] indices = m.getCountPlusIndices();
    int thisAtomIndex = indices[thispt];
    if (thisAtomIndex < 0) {
      nextMeasure(thispt + 1, ptLastAtom, m, thisModel);
      return;
    }
    boolean haveNext = false;
    for (int i = bs.nextSetBit(0); i >= 0; i = bs.nextSetBit(i + 1)) {
      if (i == thisAtomIndex)
        continue;
      int modelIndex = atoms[i].getModelIndex();
      if (thisModel >= 0) {
        if (thispt == 0)
          thisModel = modelIndex;
        else if (thisModel != modelIndex)
          continue;
      }
      indices[thispt + 1] = i;
      haveNext = true;
      nextMeasure(thispt + 1, ptLastAtom, m, thisModel);
    }
    if (!haveNext)
      nextMeasure(thispt + 1, ptLastAtom, m, thisModel);
  }
    
}


