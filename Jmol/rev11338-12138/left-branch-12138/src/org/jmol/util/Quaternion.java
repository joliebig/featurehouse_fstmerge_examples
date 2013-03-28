
package org.jmol.util;

import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Point3f;
import javax.vecmath.Point4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;







public class Quaternion {
  public float q0, q1, q2, q3;
  public Matrix3f mat;

  private final static Point4f qZero = new Point4f();
  private final static Quaternion qTemp = new Quaternion(0, 0, 0, 0);
  
  public Quaternion() {
    q0 = 1;
  }

  public Quaternion(Quaternion q) {
    set(q);
  }

  public Quaternion(Tuple3f pt, float theta) {
    set(pt, theta);
  }

  public Quaternion(Matrix3f mat) {
    set(mat);
  }

  public Quaternion(AxisAngle4f a) {
    set(a);
  }

  public Quaternion(Point4f pt) {
    set(pt);
  }

  
  public Quaternion(float q0, float q1, float q2, float q3) {
    this.q0 = q0;
    this.q1 = q1;
    this.q2 = q2;
    this.q3 = q3;
  }

  public void set(Quaternion q) {
    q0 = q.q0;
    q1 = q.q1;
    q2 = q.q2;
    q3 = q.q3;
  }

  
  private void set(Point4f pt) {
    float factor = (pt == null ? 0 : pt.distance(qZero));
    if (factor == 0) {
      q0 = 1;
      return;
    }
    q0 = pt.w / factor;
    q1 = pt.x / factor;
    q2 = pt.y / factor;
    q3 = pt.z / factor;
  }

  
  public void set(Tuple3f pt, float theta) {
    if (pt.x == 0 && pt.y == 0 && pt.z == 0) {
      q0 = 1;
      return;
    }
    double fact = (Math.sin(theta / 2 * Math.PI / 180) / Math.sqrt(pt.x
        * pt.x + pt.y * pt.y + pt.z * pt.z));
    q0 = (float) (Math.cos(theta / 2 * Math.PI / 180));
    q1 = (float) (pt.x * fact);
    q2 = (float) (pt.y * fact);
    q3 = (float) (pt.z * fact);
  }

  public void set(AxisAngle4f a) {
    AxisAngle4f aa = new AxisAngle4f(a);
    if (aa.angle == 0)
      aa.y = 1;
    Matrix3f m3 = new Matrix3f();
    m3.set(aa);
    set(m3);
  }

  public void set(Matrix3f mat) {

    

    this.mat = mat;
    
    double trace = mat.m00 + mat.m11 + mat.m22;
    double temp;
    double w, x, y, z;
    if (trace >= 0.5) {
      w = Math.sqrt(1.0 + trace);
      x = (mat.m21 - mat.m12) / w;
      y = (mat.m02 - mat.m20) / w;
      z = (mat.m10 - mat.m01) / w;
    } else if ((temp = mat.m00 + mat.m00 - trace) >= 0.5) {
      x = Math.sqrt(1.0 + temp);
      w = (mat.m21 - mat.m12) / x;
      y = (mat.m10 + mat.m01) / x;
      z = (mat.m20 + mat.m02) / x;
    } else if ((temp = mat.m11 + mat.m11 - trace) >= 0.5 
        || mat.m11 > mat.m22) {
      y = Math.sqrt(1.0 + temp);
      w = (mat.m02 - mat.m20) / y;
      x = (mat.m10 + mat.m01) / y;
      z = (mat.m21 + mat.m12) / y;
    } else {
      z = Math.sqrt(1.0 + mat.m22 + mat.m22 - trace);
      w = (mat.m10 - mat.m01) / z;
      x = (mat.m20 + mat.m02) / z; 
      y = (mat.m21 + mat.m12) / z; 
    }

    q0 = (float) (w * 0.5);
    q1 = (float) (x * 0.5);
    q2 = (float) (y * 0.5);
    q3 = (float) (z * 0.5);

    
  }

  
  public void setRef(Quaternion qref) {
    if (qref == null) {
      fixQ(this);
      return;
    }
    if (dot(qref) >= 0)
      return;
    q0 *= -1;
    q1 *= -1;
    q2 *= -1;
    q3 *= -1;
  }

  public static final Quaternion getQuaternionFrame(Point3f center, Point3f x, Point3f xy) {
    Vector3f vA = new Vector3f(x);
    vA.sub(center);
    Vector3f vB = new Vector3f(xy);
    vB.sub(center);
    return getQuaternionFrame(vA, vB, null);
  }
  
  public static final Quaternion getQuaternionFrame(Vector3f vA, Vector3f vB,
                                                    Vector3f vC) {
    if (vC == null) {
      vC = new Vector3f();
      vC.cross(vA, vB);
    }
    Vector3f vBprime = new Vector3f();
    vBprime.cross(vC, vA);
    vA.normalize();
    vBprime.normalize();
    vC.normalize();
    Matrix3f mat = new Matrix3f();
    mat.setColumn(0, vA);
    mat.setColumn(1, vBprime);
    mat.setColumn(2, vC);

    

    Quaternion q = new Quaternion(mat);

     
     
    return q;
  }

  public Matrix3f getMatrix() {
    if (mat == null)
      setMatrix();
    return mat;
  }

  private void setMatrix() {
    mat = new Matrix3f();
    
    mat.m00 = q0 * q0 + q1 * q1 - q2 * q2 - q3 * q3;
    mat.m01 = 2 * q1 * q2 - 2 * q0 * q3;
    mat.m02 = 2 * q1 * q3 + 2 * q0 * q2;
    mat.m10 = 2 * q1 * q2 + 2 * q0 * q3;
    mat.m11 = q0 * q0 - q1 * q1 + q2 * q2 - q3 * q3;
    mat.m12 = 2 * q2 * q3 - 2 * q0 * q1;
    mat.m20 = 2 * q1 * q3 - 2 * q0 * q2;
    mat.m21 = 2 * q2 * q3 + 2 * q0 * q1;
    mat.m22 = q0 * q0 - q1 * q1 - q2 * q2 + q3 * q3;
  }

  public Quaternion add(float x) {
    
   return new Quaternion(getNormal(), getTheta() + x);
  }

  public Quaternion mul(float x) {
    
    return (x == 1 ? new Quaternion(q0, q1, q2, q3) : 
      new Quaternion(getNormal(), getTheta() * x));
  }

  public Quaternion mul(Quaternion p) {
    return new Quaternion(q0 * p.q0 - q1 * p.q1 - q2 * p.q2 - q3 * p.q3, q0
        * p.q1 + q1 * p.q0 + q2 * p.q3 - q3 * p.q2, q0 * p.q2 + q2 * p.q0 + q3
        * p.q1 - q1 * p.q3, q0 * p.q3 + q3 * p.q0 + q1 * p.q2 - q2 * p.q1);
  }

  public Quaternion div(Quaternion p) {
    
    return mul(p.inv());
  }

  public Quaternion divLeft(Quaternion p) {
    
    return this.inv().mul(p);
  }

  public float dot(Quaternion q) {
    return this.q0 * q.q0 + this.q1 * q.q1 + this.q2 * q.q2 + this.q3 * q.q3;
  }

  public Quaternion inv() {
    return new Quaternion(q0, -q1, -q2, -q3);
  }

  public Quaternion negate() {
    return new Quaternion(-q0, -q1, -q2, -q3);
  }

  
  private void fixQ(Quaternion qNew) {
    float f = (q0 < 0 || q0 == 0
        && (q1 < 0 || q1 == 0 && (q2 < 0 || q2 == 0 && q3 < 0)) ? -1 : 1);
    qNew.q0 = q0 * f;
    qNew.q1 = q1 * f;
    qNew.q2 = q2 * f;
    qNew.q3 = q3 * f;
  }

  public Vector3f getVector(int i) {
    return getVector(i, 1f);
  }

  private Vector3f getVector(int i, float scale) {
    if (i == -1) {
      fixQ(qTemp);
      return new Vector3f(qTemp.q1 * scale, qTemp.q2 * scale, qTemp.q3 * scale);
    }
    if (mat == null)
      setMatrix();
    Vector3f v = new Vector3f();
    mat.getColumn(i, v);
    if (scale != 1f)
      v.scale(scale);
    return v;
  }

  
  public Vector3f getNormal() {
    fixQ(qTemp);
    Vector3f v = new Vector3f(qTemp.q1, qTemp.q2, qTemp.q3);
    if (v.length() == 0)
      return new Vector3f(0, 0, 1);
    v.normalize();
    return v;
  }

  
  public float getTheta() {
    fixQ(qTemp);
    return (float) (Math.acos(qTemp.q0) * 2 * 180 / Math.PI);
  }

  public float getThetaRadians() {
    fixQ(qTemp);
    return (float) (Math.acos(qTemp.q0) * 2);
  }

  
  public Vector3f getNormalDirected(Vector3f v0) {
    Vector3f v = getNormal();
    if (v0.x * q1 + v0.y * q2 + v0.z * q3 < 0) {
      v.scale(-1);
    }
    return v;
  }

  
  public Point4f getThetaDirected(Point4f axisAngle) {
    
    float theta = getTheta();
    Vector3f v = getNormal();
    if (axisAngle.x * q1 + axisAngle.y * q2 + axisAngle.z * q3 < 0) {
      v.scale(-1);
      theta = -theta;
    }
    axisAngle.set(v.x, v.y, v.z, theta);
    return axisAngle;
  }

  public Point4f toPoint4f() {
    
    return new Point4f(q1, q2, q3, q0);
  }

  public AxisAngle4f toAxisAngle4f() {
    fixQ(qTemp);
    double theta = 2 * Math.acos(qTemp.q0);
    double sinTheta2 = Math.sin(theta/2);
    Vector3f v = getNormal();
    if (sinTheta2 < 0) {
      v.scale(-1);
      theta = Math.PI - theta;
    }
    return new AxisAngle4f(v, (float) theta);
  }

  public Point3f transform(Point3f pt) {
    if (mat == null)
      setMatrix();
    Point3f ptNew = new Point3f(pt);
    mat.transform(ptNew);
    return ptNew;
  }

  public void transform(Point3f pt, Point3f ptNew) {
    if (mat == null)
      setMatrix();
    mat.transform(pt, ptNew);
  }

  public Vector3f transform(Vector3f v) {
    if (mat == null)
      setMatrix();
    Vector3f vNew = new Vector3f(v);
    mat.transform(vNew);
    return vNew;
  }

  public Quaternion leftDifference(Quaternion q2) {
    
    Quaternion q2adjusted = (this.dot(q2) < 0 ? q2.negate() : q2);
    return inv().mul(q2adjusted);
  }

  public Quaternion rightDifference(Quaternion q2) {
    
    Quaternion q2adjusted = (this.dot(q2) < 0 ? q2.negate() : q2);
    return mul(q2adjusted.inv());
  }

  public String getInfo() {
    AxisAngle4f axis = toAxisAngle4f();
    return TextFormat.sprintf("%10.6f%10.6f%10.6f%10.6f  %6.2f  %10.5f %10.5f %10.5f",
        new Object[] { new float[] { q0, q1, q2, q3, 
            (float) (axis.angle * 180 / Math.PI), axis.x, axis.y, axis.z } });
  }

  public String draw(String prefix, String id, Point3f ptCenter, 
                     float scale) {
    String strV = " VECTOR " + Escape.escape(ptCenter) + " ";
    if (scale == 0)
      scale = 1f;
    return "draw " + prefix + "x" + id + strV
        + Escape.escape(getVector(0, scale)) + " color red\n"
        + "draw " + prefix + "y" + id + strV
        + Escape.escape(getVector(1, scale)) + " color green\n"
        + "draw " + prefix + "z" + id + strV
        + Escape.escape(getVector(2, scale)) + " color blue\n";
  }

  public String toString() {
    return "{" + q1 + " " + q2 + " " + q3 + " " + q0 + "}";
  }

  public String toString0123() {
    return q0 + " " + q1  + " " + q2 + " " + q3;
  }

}
