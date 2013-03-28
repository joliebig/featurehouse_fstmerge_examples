

package org.jmol.util;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;


public class Quadric {

  
  
  
  
  
  
  
  
  

  public static void getAxesForEllipsoid(double[] coef, Vector3f[] unitVectors, float[] lengths) {
    
    
    
    
    double[][] mat = new double[3][3];
    mat[0][0] = coef[0]; 
    mat[1][1] = coef[1]; 
    mat[2][2] = coef[2]; 
    mat[0][1] = mat[1][0] = coef[3] / 2; 
    mat[0][2] = mat[2][0] = coef[4] / 2; 
    mat[1][2] = mat[2][1] = coef[5] / 2; 
    Eigen eigen = new Eigen(mat);
    float[][] eigenVectors = Eigen.toFloat3x3(eigen.getEigenvectors());
    double[] eigenValues = eigen.getEigenvalues();
    for (int i = 0; i < 3; i++)
      lengths[i] = (float) (1/Math.sqrt(eigenValues[i]));
    for (int i = 0; i < 3; i++)
      unitVectors[i].set(eigenVectors[i]);
  }

  public static Matrix3f setEllipsoidMatrix(Vector3f[] unitAxes, float[] lengths, Vector3f vTemp, Matrix3f mat) {
    
    
    for (int i = 0; i < 3; i++) {
      vTemp.set(unitAxes[i]);
      vTemp.scale(lengths[i]);
      mat.setColumn(i, vTemp);
    }
    mat.invert(mat);
    return mat;
  }

  public static void getEquationForQuadricWithCenter(float x, float y, float z, Matrix3f mToElliptical, 
                                             Vector3f vTemp, Matrix3f mTemp, double[] coef, Matrix4f mDeriv) {
    
    
    vTemp.set(x, y, z);
    mToElliptical.transform(vTemp);
    double f = 1 - vTemp.dot(vTemp); 
    mTemp.transpose(mToElliptical);
    mTemp.transform(vTemp);
    mTemp.mul(mToElliptical);
    coef[0] = mTemp.m00 / f;     
    coef[1] = mTemp.m11 / f;     
    coef[2] = mTemp.m22 / f;     
    coef[3] = mTemp.m01 * 2 / f; 
    coef[4] = mTemp.m02 * 2 / f; 
    coef[5] = mTemp.m12 * 2 / f; 
    coef[6] = -2 * vTemp.x / f;  
    coef[7] = -2 * vTemp.y / f;  
    coef[8] = -2 * vTemp.z / f;  
    coef[9] = -1;                
    
    
    
    if (mDeriv == null)
      return;
    mDeriv.setIdentity();
    mDeriv.m00 = (float) (2 * coef[0]);
    mDeriv.m11 = (float) (2 * coef[1]);
    mDeriv.m22 = (float) (2 * coef[2]);
  
    mDeriv.m01 = mDeriv.m10 = (float) coef[3];
    mDeriv.m02 = mDeriv.m20 = (float) coef[4];
    mDeriv.m12 = mDeriv.m21 = (float) coef[5];
  
    mDeriv.m03 = (float) coef[6];
    mDeriv.m13 = (float) coef[7];
    mDeriv.m23 = (float) coef[8];
  }

  public static boolean getQuardricZ(double x, double y, 
                                   double[] coef, double[] zroot) {
    
    
    
    double b_2a = (coef[4] * x + coef[5] * y + coef[8]) / coef[2] / 2;
    double c_a = (coef[0] * x * x + coef[1] * y * y + coef[3] * x * y 
        + coef[6] * x + coef[7] * y - 1) / coef[2];
    double f = b_2a * b_2a - c_a;
    if (f < 0)
      return false;
    f = Math.sqrt(f);
    zroot[0] = (-b_2a - f);
    zroot[1] = (-b_2a + f);
    return true;
  }

  public static int getOctant(Point3f pt) {
    int i = 0;
    if (pt.x < 0)
      i += 1;
    if (pt.y < 0)
      i += 2;
    if (pt.z < 0)
      i += 4;
    return i;
  }

}
