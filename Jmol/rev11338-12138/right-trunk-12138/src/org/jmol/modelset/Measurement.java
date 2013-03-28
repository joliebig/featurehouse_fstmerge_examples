
package org.jmol.modelset;

import org.jmol.util.Escape;
import org.jmol.util.Point3fi;
import org.jmol.util.Measure;
import org.jmol.modelset.TickInfo;

import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.Viewer;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;
import javax.vecmath.AxisAngle4f;

import java.util.Vector;

public class Measurement {

  
  private Viewer viewer;

  public ModelSet modelSet;

  protected int count;
  protected int[] countPlusIndices = new int[5];
  protected Point3fi[] pts;
  
  public int getCount() {
    return count;
  }
  
  public void setCount(int count) {
    this.count = countPlusIndices[0] = count;
  }

  public int[] getCountPlusIndices() {
    return countPlusIndices;
  }
  
  public Point3fi[] getPoints() {
    return pts;
  }

  public int getAtomIndex(int n) {
    return (n > 0 && n <= count ? countPlusIndices[n] : -1);
  }
  
  public Point3fi getAtom(int i) {
    int pt = countPlusIndices[i];
    return (pt < -1 ? pts[-2 - pt] : modelSet.getAtomAt(pt));
  }

  public int getLastIndex() {
    return (count > 0 ? countPlusIndices[count] : -1);
  }
  
  private String strMeasurement;
  
  public String getString() {
    return strMeasurement;
  }
  
  public String getString(Viewer viewer, String strFormat, String units) {
    this.viewer = viewer;
    value = getMeasurement();
    formatMeasurement(strFormat, units, true);
    if (strFormat == null)
      return getInfoAsString(units);
    return strMeasurement;
  }

  public String getStringDetail() {
    return (count == 2 ? "Distance" : count == 3 ? "Angle" : "Torsion")
        + getMeasurementScript(" - ", false) + " : " + value;  
  }
  
  private String strFormat;
  
  public String getStrFormat() {
    return strFormat;
  }
  
  protected float value;
  
  public float getValue() {
    return value;
  }
  
  private boolean isVisible = true;
  private boolean isHidden = false;
  private boolean isDynamic = false;
  private boolean isTrajectory = false;
  
  public boolean isVisible() {
    return isVisible;
  }
  public boolean isHidden() {
    return isHidden;
  }
  public boolean isDynamic() {
    return isDynamic;
  }
  
  public boolean isTrajectory() {
    return isTrajectory;
  }
  
  public void setVisible(boolean TF) {
    this.isVisible = TF;
  }
  public void setHidden(boolean TF) {
    this.isHidden = TF;
  }
  public void setDynamic(boolean TF) {
    this.isDynamic = TF;
  }
  
  private short colix;
  
  public short getColix() {
    return colix;
  }
  
  public void setColix(short colix) {
    this.colix = colix;
  }
  
  private int index;
  
  public void setIndex(int index) {
    this.index = index;
  }
  
  public int getIndex() {
    return index;
  }
  
  private AxisAngle4f aa;
  
  public AxisAngle4f getAxisAngle() {
    return aa;
  }
  
  private Point3f pointArc;

  public Point3f getPointArc() {
    return pointArc;
  }
  
  public TickInfo tickInfo;

  public TickInfo getTickInfo() {
    return tickInfo;
  }
  
  public Measurement(ModelSet modelSet, Measurement m,
                     float value, short colix, 
                     String strFormat, int index) {
    
    this.index = index;
    this.modelSet = modelSet;
    this.viewer = modelSet.viewer;
    this.colix = colix;
    this.strFormat = strFormat;
    if (m != null) {
      this.tickInfo = m.tickInfo;
      this.pts = m.pts;
    }
    if (pts == null)
      pts = new Point3fi[4];
    int[] indices = (m == null ? null : m.countPlusIndices);
    count = (indices == null ? 0 : indices[0]);
    if (count > 0) {
      System.arraycopy(indices, 0, countPlusIndices, 0, count + 1);
      isTrajectory = modelSet.isTrajectory(countPlusIndices);
    }
    this.value = (Float.isNaN(value) || isTrajectory ? getMeasurement() : value);
    formatMeasurement(null);
  }   

  public Measurement(ModelSet modelSet, int[] indices, Point3fi[] points,
      TickInfo tickInfo) {
    
    countPlusIndices = indices;
    count = indices[0];
    this.pts = (points == null ? new Point3fi[4] : points);
    this.modelSet = modelSet;
    this.tickInfo = tickInfo;
  }

  public void refresh() {
    value = getMeasurement();
    isTrajectory = modelSet.isTrajectory(countPlusIndices);
    formatMeasurement(null);
  }
  
  
  public String getMeasurementScript(String sep, boolean withModelIndex) {
    String str = "";
    
    boolean asScript = (sep.equals(" "));
    for (int i = 1; i <= count; i++)
      str += (i > 1 ? sep : " ") + getLabel(i, asScript, withModelIndex); 
    return str;  
  }
  
  public void formatMeasurement(String strFormat, String units, boolean useDefault) {
    if (strFormat != null && strFormat.length() == 0)
      strFormat = null;
    if (!useDefault && strFormat != null && strFormat.indexOf(countPlusIndices[0]+":")!=0)
      return;
    this.strFormat = strFormat; 
    formatMeasurement(units);
  }

  protected void formatMeasurement(String units) {
    strMeasurement = null;
    if (Float.isNaN(value) || count == 0)
      return;
    switch (count) {
    case 2:
      strMeasurement = formatDistance(units);
      return;
    case 3:
      if (value == 180) {
        aa = null;
        pointArc = null;
      } else {
        Vector3f vectorBA = new Vector3f();
        Vector3f vectorBC = new Vector3f();        
        float radians = Measure.computeAngle(getAtom(1), getAtom(2), getAtom(3), vectorBA, vectorBC, false);
        Vector3f vectorAxis = new Vector3f();
        vectorAxis.cross(vectorBA, vectorBC);
        aa = new AxisAngle4f(vectorAxis.x, vectorAxis.y, vectorAxis.z, radians);

        vectorBA.normalize();
        vectorBA.scale(0.5f);
        pointArc = new Point3f(vectorBA);
      }
      
    case 4:
      strMeasurement = formatAngle(value);
      return;
    }
  }
  
  public void reformatDistanceIfSelected() {
    if (count != 2)
      return;
    if (viewer.isSelected(countPlusIndices[1]) &&
        viewer.isSelected(countPlusIndices[2]))
      formatMeasurement(null);
  }

  private String formatDistance(String units) {
    if (units == null)
      units = viewer.getMeasureDistanceUnits();
    units = fixUnits(units);
    float f = fixValue(value, units);
    return formatString(f, units);
  }

  private static String fixUnits(String units) {
    if (units == "nanometers")
      return "nm";
    else if (units == "picometers")
      return "pm";
    return units;
  }
  
  private static float fixValue(float dist, String units) {
    if (units != null) {
      if (units == "nm")
        return (int) (dist * 100 + 0.5f) / 1000f;
      if (units == "pm")
        return (int) ((dist * 1000 + 0.5)) / 10f;
      if (units == "au")
        return (int) (dist / JmolConstants.ANGSTROMS_PER_BOHR * 1000 + 0.5f) / 1000f;
    }
    return (int) (dist * 100 + 0.5f) / 100f;
  }
  
  private String formatAngle(float angle) {
    angle = (int)(angle * 10 + (angle >= 0 ? 0.5f : -0.5f));
    angle /= 10;
    return formatString(angle, "\u");
  }

  private String formatString(float value, String units) {
    String s = countPlusIndices[0]+":" + "";
    String label = (strFormat != null && strFormat.indexOf(s)==0? strFormat : viewer
        .getDefaultMeasurementLabel(countPlusIndices[0]));
    if (label.indexOf(s)==0)
      label = label.substring(2);
    return LabelToken.labelFormat(viewer, this, label, value, units);
  }

  public boolean sameAs(int[] indices, Point3fi[] points) {
    if (count != indices[0]) 
      return false;
    boolean isSame = true;
    for (int i = 1; i <= count && isSame; i++)
      isSame = (countPlusIndices[i] == indices[i]);
    if (isSame)
      for (int i = 0; i < count && isSame; i++) {
        if (points[i] != null)
          isSame = (this.pts[i].distance(points[i]) < 0.01); 
      }
    if (isSame)
      return true;
    switch (count) {
    default:
      return true;
    case 2:
      return sameAs(indices, points, 1, 2) 
          && sameAs(indices, points, 2, 1);
    case 3:
      return sameAs(indices, points, 1, 3)
          && sameAs(indices, points, 2, 2)
          && sameAs(indices, points, 3, 1);
    case 4:  
      return  sameAs(indices, points, 1, 4)
          && sameAs(indices, points, 2, 3) 
          && sameAs(indices, points, 3, 2)
          && sameAs(indices, points, 4, 1);
    } 
  }

  private boolean sameAs(int[] atoms, Point3fi[] points, int i, int j) {
    int ipt = countPlusIndices[i];
    int jpt = atoms[j];
    return (ipt >= 0 || jpt >= 0 ? ipt == jpt 
        : this.pts[-2 - ipt].distance(points[-2 - jpt]) < 0.01);
  }

  public boolean sameAs(int i, int j) {
    return sameAs(countPlusIndices, pts, i, j);
  }

  public Vector toVector() {
    Vector V = new Vector();
    for (int i = 1; i <= count; i++ )
      V.addElement(getLabel(i, false, false));
    V.addElement(strMeasurement);
    return V;  
  }
  
  public float getMeasurement() {
    if (countPlusIndices == null)
      return Float.NaN;
    if (count < 2)
      return Float.NaN;
    for (int i = count; --i >= 0;)
      if (countPlusIndices[i + 1] == -1) {
        return Float.NaN;
      }
    Point3fi ptA = getAtom(1);
    Point3fi ptB = getAtom(2);
    Point3fi ptC, ptD;
    switch (count) {
    case 2:
      return ptA.distance(ptB);
    case 3:
      ptC = getAtom(3);
      return Measure.computeAngle(ptA, ptB, ptC, true);
    case 4:
      ptC = getAtom(3);
      ptD = getAtom(4);
      return Measure.computeTorsion(ptA, ptB, ptC, ptD, true);
    default:
      return Float.NaN;
    }
  }

  public String getLabel(int i, boolean asBitSet, boolean withModelIndex) {
    int atomIndex = countPlusIndices[i];
    
    
    return (atomIndex < 0 
        ? (withModelIndex ? "modelIndex " + getAtom(i).modelIndex + " " : "")
            + Escape.escape(getAtom(i))
        : asBitSet ? "(({" + atomIndex + "}))"
        : viewer.getAtomInfo(atomIndex));
  }

  public void setModelIndex(short modelIndex) {
    if (pts == null)
      return;
    for (int i = 0; i < count; i++) {
      if (pts[i] != null)
        pts[i].modelIndex = modelIndex;
    }
  }

  public boolean isValid() {
    
    return !(sameAs(1,2) || count > 2 && sameAs(1,3) || count == 4 && sameAs(2,4));
  }

  public static int find(Vector measurements, Measurement m) {
    int[] indices = m.getCountPlusIndices();
    Point3fi[] points = m.getPoints();
    for (int i = measurements.size(); --i >= 0; )
      if (((Measurement) measurements.get(i)).sameAs(indices, points))
        return i;
    return -1;
  }
  
  boolean isConnected(Atom[] atoms, int count) {
    int atomIndexLast = -1;
    for (int i = 1; i <= count; i++) {
      int atomIndex = getAtomIndex(i);
      if (atomIndex < 0)
        continue;
      if (atomIndexLast >= 0
          && !atoms[atomIndex].isBonded(atoms[atomIndexLast]))
        return false;
      atomIndexLast = atomIndex;
    }
    return true;
  }

  public String getInfoAsString(String units) {
    float f = (count == 2 ? fixValue(value, units) : value);
    StringBuffer sb = new StringBuffer();
    sb.append(count == 2 ? "distance" : count == 3 ? "angle" : "dihedral");
    sb.append(" \t").append(f);
    sb.append(" \t").append(getString());
    for (int i = 1; i <= count; i++)
      sb.append(" \t").append(getLabel(i, false, false));
    return sb.toString();
  }


}


