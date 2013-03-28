


package org.jmol.symmetry;



import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import org.jmol.modelset.BoxInfo;
import org.jmol.util.Quadric;
import org.jmol.util.SimpleUnitCell;

class UnitCell extends SimpleUnitCell {
  
  private Point3f[] vertices; 

  private Point3f cartesianOffset = new Point3f();
  private Point3f fractionalOffset = new Point3f();
  

  UnitCell(float[] notionalUnitcell) {
    super(notionalUnitcell);
    calcUnitcellVertices();
  }

  private final void toFractionalUnitCell(Point3f pt) {
    if (matrixCartesianToFractional == null)
      return;
    matrixCartesianToFractional.transform(pt);
    pt.x = toFractional(pt.x);
    pt.y = toFractional(pt.y);
    pt.z = toFractional(pt.z);  
  }
  
  private static float toFractional(float x) {
    
    x = (float) (x - Math.floor(x));
    if (x > 0.9999f || x < 0.0001f) 
      x = 0;
    return x;
  }
  
  final void toUnitCell(Point3f pt, Point3f offset) {
    if (matrixCartesianToFractional == null)
      return;
    toFractionalUnitCell(pt);
    
    if (offset != null)
      pt.add(offset);
    matrixFractionalToCartesian.transform(pt);
  }
  
  void setOffset(Point3f pt) {
    
    fractionalOffset.set(pt);
    matrixCartesianToFractional.m03 = -pt.x;
    matrixCartesianToFractional.m13 = -pt.y;
    matrixCartesianToFractional.m23 = -pt.z;
    cartesianOffset.set(pt);
    matrixFractionalToCartesian.m03 = 0;
    matrixFractionalToCartesian.m13 = 0;
    matrixFractionalToCartesian.m23 = 0;
    matrixFractionalToCartesian.transform(cartesianOffset);
    matrixFractionalToCartesian.m03 = cartesianOffset.x;
    matrixFractionalToCartesian.m13 = cartesianOffset.y;
    matrixFractionalToCartesian.m23 = cartesianOffset.z;
    
  }

  void setOffset(int nnn) {
    
    setOffset(ijkToPoint3f(nnn));
  }

  static Point3f ijkToPoint3f(int nnn) {
    Point3f cell = new Point3f();
    cell.x = nnn / 100 - 5;
    cell.y = (nnn % 100) / 10 - 5;
    cell.z = (nnn % 10) - 5;
    return cell;
  }
  
  final String dumpInfo(boolean isFull) {
    return "a=" + a + ", b=" + b + ", c=" + c + ", alpha=" + alpha + ", beta=" + beta + ", gamma=" + gamma
       + (isFull ? "\nfractional to cartesian: " + matrixFractionalToCartesian 
       + "\ncartesian to fractional: " + matrixCartesianToFractional : "");
  }

  Point3f[] getVertices() {
    return vertices; 
  }
  
  Point3f getCartesianOffset() {
    return cartesianOffset;
  }
  
  Point3f getFractionalOffset() {
    return fractionalOffset;
  }
  
  
  
    final static double twoP2 = 2 * Math.PI * Math.PI;
    
    Object[] getEllipsoid(float[] parBorU) {
    if (parBorU == null)
      return null;
    if (data == null)
      data = new Data();

    

    float[] lengths = new float[6]; 
    if (parBorU[0] == 0) { 
      lengths[1] = (float) Math.sqrt(parBorU[7]);
      return new Object[] { null, lengths };
    }

    int ortepType = (int) parBorU[6];
    boolean isFractional = (ortepType == 4 || ortepType == 5 || ortepType == 8 || ortepType == 9);
    double cc = 2 - (ortepType % 2);
    double dd = (ortepType == 8 || ortepType == 9 || ortepType == 10 ? twoP2
        : ortepType == 4 || ortepType == 5 ? 0.25 : ortepType == 2
            || ortepType == 3 ? Math.log(2) : 1);
    

    
    
    double B11 = parBorU[0] * dd * (isFractional ? data.a_ * data.a_ : 1);
    double B22 = parBorU[1] * dd * (isFractional ? data.b_ * data.b_ : 1);
    double B33 = parBorU[2] * dd * (isFractional ? data.c_ * data.c_ : 1);
    double B12 = parBorU[3] * dd * (isFractional ? data.a_ * data.b_ : 1) * cc;
    double B13 = parBorU[4] * dd * (isFractional ? data.a_ * data.c_ : 1) * cc;
    double B23 = parBorU[5] * dd * (isFractional ? data.b_ * data.c_ : 1) * cc;

    
    parBorU[7] = (float) Math.pow(B11 / twoP2 / data.a_ / data.a_ * B22 / twoP2
        / data.b_ / data.b_ * B33 / twoP2 / data.c_ / data.c_, 0.3333);

    double[] Bcart = new double[6];

    Bcart[0] = a * a * B11 + b * b * data.cosGamma * data.cosGamma * B22 + c
        * c * data.cosBeta * data.cosBeta * B33 + a * b * data.cosGamma * B12
        + b * c * data.cosGamma * data.cosBeta * B23 + a * c * data.cosBeta
        * B13;
    Bcart[1] = b * b * data.sinGamma * data.sinGamma * B22 + c * c * data.cA_
        * data.cA_ * B33 + b * c * data.cA_ * data.sinGamma * B23;
    Bcart[2] = c * c * data.cB_ * data.cB_ * B33;
    Bcart[3] = 2 * b * b * data.cosGamma * data.sinGamma * B22 + 2 * c * c
        * data.cA_ * data.cosBeta * B33 + a * b * data.sinGamma * B12 + b * c
        * (data.cA_ * data.cosGamma + data.sinGamma * data.cosBeta) * B23 + a
        * c * data.cA_ * B13;
    Bcart[4] = 2 * c * c * data.cB_ * data.cosBeta * B33 + b * c
        * data.cosGamma * B23 + a * c * data.cB_ * B13;
    Bcart[5] = 2 * c * c * data.cA_ * data.cB_ * B33 + b * c * data.cB_
        * data.sinGamma * B23;

    
    
    Vector3f unitVectors[] = new Vector3f[3];
    for (int i = 0; i < 3; i++)
      unitVectors[i] = new Vector3f();
    Quadric.getAxesForEllipsoid(Bcart, unitVectors, lengths);

    

    double factor = Math.sqrt(0.5) / Math.PI;
    for (int i = 0; i < 3; i++)
      lengths[i] = (float) (factor / lengths[i]);
    return new Object[] { unitVectors, lengths };
  }
    
  private void calcUnitcellVertices() {
    if (notionalUnitcell == null || notionalUnitcell[0] == 0)
      return;
    vertices = new Point3f[8];
    for (int i = 8; --i >= 0;) {
      vertices[i] = new Point3f();
      matrixFractionalToCartesian.transform(BoxInfo.unitCubePoints[i], vertices[i]);
    }
  }  
  
  public Point3f[] getCanonicalCopy(float scale) {
    Point3f[] pts = new Point3f[8];
    for (int i = 0; i < 8; i++) {
      pts[i] = new Point3f(BoxInfo.unitCubePoints[i]);
      matrixFractionalToCartesian.transform(pts[i]);
      
    }
    return BoxInfo.getCanonicalCopy(pts, scale);
  }
 
}
