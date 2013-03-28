

package org.jmol.minimize;

import java.util.Random;

import javax.vecmath.Vector3d;

public class Util {

  final public static double RAD_TO_DEG = (180.0 / Math.PI);
  final public static double DEG_TO_RAD = (Math.PI / 180.0);

  public static void sub(double[] a, double[] b, Vector3d result) {
    result.set(a[0] - b[0], a[1] - b[1], a[2] - b[2]);
  }

  public static void putCoord(Vector3d v, double[] c) {
    c[0] = v.x;
    c[1] = v.y;
    c[2] = v.z;
  }

  public static double distance2(double[] a, double[] b) {
    double dx = a[0] - b[0];
    double dy = a[1] - b[1];
    double dz = a[2] - b[2];
    return (dx * dx + dy * dy + dz * dz);
  }
  
  public static double distance2(Vector3d a, Vector3d b) {
    double dx = a.x - b.x;
    double dy = a.y - b.y;
    double dz = a.z - b.z;
    return (dx * dx + dy * dy + dz * dz);
  }

  public static double getAngleRadiansABC(double[] a, double[] b, double[] c) {
    
    
    
    
    double ab2 = distance2(a, b);
    double bc2 = distance2(b, c);
    double ac2 = distance2(a, c);
    return (isNearZero(ab2, 1e-3) || isNearZero(bc2, 1e-3) ? 0 :
        Math.acos((ab2 + bc2 - ac2 ) / 2 / Math.sqrt(ab2 * bc2)));
  }
  
  public static boolean isApprox(Vector3d a, Vector3d b, double precision) {
    return (distance2(a, b) <= precision * precision
        * Math.min(a.lengthSquared(), b.lengthSquared()));
  }

  final static double max_squarable_double = 1e150;
  final static double min_squarable_double = 1e-150;

  public static boolean canBeSquared(double x) {
    if (x == 0)
      return true;
    return ((x = Math.abs(x)) < max_squarable_double && x > min_squarable_double);
  }

  public static boolean isNegligible(double a, double b) {
    return isNegligible(a, b, 1e-11);
  }

  public static boolean isFinite(double a) {
    return !Double.isInfinite(a) && !Double.isNaN(a);
  }

  public static boolean isNegligible(double a, double b, double precision) {
    return (Math.abs(a) <= precision * Math.abs(b));
  }

  public static boolean isNear(double a, double b) {
    return isNear(a, b, 2e-6);
  }

  public static boolean isNear(double a, double b, double epsilon) {
    return (Math.abs(a - b) < epsilon);
  }
  public static boolean isNearZero(double a) {
    return isNearZero(a, 2e-6);
  }

  public static boolean isNearZero(double a, double epsilon) {
    return (Math.abs(a) < epsilon);
  }

  public static boolean canBeNormalized(Vector3d a) {
    if (a.x == 0.0 && a.y == 0.0 && a.z == 0.0)
      return false;
    return (canBeSquared(a.x) && canBeSquared(a.y) && canBeSquared(a.z));
  }

  
  public static double pointPlaneAngleRadians(Vector3d a, Vector3d b,
                                              Vector3d c, Vector3d d,
                                              Vector3d v1,Vector3d v2, 
                                              Vector3d norm) {
    v1.sub(b, c);
    v2.sub(b, d);
    norm.cross(v1, v2);
    v2.add(v1);
    v1.sub(b, a);
    double angleA_CD = vectorAngleRadians(v2, v1);
    double angleNorm = vectorAngleRadians(norm, v1);
    if (angleNorm > Math.PI / 2)
      angleNorm = Math.PI - angleNorm;
    return Math.PI / 2.0 + (angleA_CD > Math.PI / 2.0 ? -angleNorm : angleNorm) ;
  }
  
  private static double vectorAngleRadians(Vector3d v1, Vector3d v2) {
    double l1 = v1.length();
    double l2 = v2.length();
    return (isNearZero(l1) || isNearZero(l2) ? 0 :
      Math.acos(v1.dot(v2) / (l1 * l2)));
  }

  public static double getTorsionAngleRadians(double[] a, double[] b, double[] c, double[] d,
                                              Vector3d r1, Vector3d r2, Vector3d r3) {
    

    
    
    
    
    
    sub(b, a, r1); 
    sub(c, b, r2);
    r2.normalize();
    r1.cross(r1, r2); 
    sub(d, c, r3); 
    r3.cross(r2, r3); 
    double p1dotp2 = r1.dot(r3);
    r1.cross(r3, r1);
    double theta = Math.atan2(
        -r2.dot(r1), 
        p1dotp2);   
    return theta;
  }
  
  
 
  public static double restorativeForceAndDistance(Vector3d a, Vector3d b, Vector3d vab) {
    
    
    
    vab.sub(a, b);
    double rab = vab.length();
    if (rab < 0.1) {
      randomizeUnitVector(vab);
      rab = 0.1;
    }
    vab.normalize();
    a.set(vab);
    a.scale(-1); 
    b.set(vab); 

    return rab;
  }

  private static void randomizeUnitVector(Vector3d v) {
    Random ptr = new Random();

    
    
    double l;
    do {
      v.set(ptr.nextFloat() - 0.5, ptr.nextFloat() - 0.5, ptr.nextFloat() - 0.5);
      l = v.lengthSquared();
    } while ((l > 1.0) || (l < 1e-4));
    v.normalize();
  }

  public static double restorativeForceAndAngleRadians(Vector3d i, Vector3d j, Vector3d k) {
    
    
    

    

    
    i.sub(j);
    k.sub(j);

    double length1 = i.length();
    double length2 = k.length();

    
    if (isNearZero(length1) || isNearZero(length2)) {
      i.set(0, 0, 0);
      j.set(0, 0, 0);
      k.set(0, 0, 0);
      return 0.0;
    }

    
    double inverse_length_v1 = 1.0 / length1;
    double inverse_length_v2 = 1.0 / length2;
    i.scale(inverse_length_v1);
    k.scale(inverse_length_v2);

    
    
    j.cross(i, k);
    double length = j.length();
    if (isNearZero(length)) {
      i.set(0, 0, 0);
      j.set(0, 0, 0);
      k.set(0, 0, 0);
      return 0.0;
    }

    j.scale(1 / length);

    
    double costheta = i.dot(k);
    double theta;
    if (costheta > 1.0) {
      theta = 0.0;
      costheta = 1.0;
    } else if (costheta < -1.0) {
      theta = Math.PI;
      costheta = -1.0;
    } else {
      theta = Math.acos(costheta);
    }

    i.cross(i, j);
    i.normalize();
    j.cross(k, j);
    j.normalize();

    i.scale(-inverse_length_v1);
    j.scale(inverse_length_v2);
    k.set(j);
    j.add(i);
    j.scale(-1);
    return theta;
  }

  public static double restorativeForceAndOutOfPlaneAngleRadians(Vector3d i, Vector3d j, 
                           Vector3d k, Vector3d l, Vector3d an, Vector3d bn, Vector3d cn) {
    
    

    
    
    
    
    
    
    i.sub(i, j);
    k.sub(k, j);
    l.sub(l, j);
    
    
    double length_ji = i.length();
    double length_jk = k.length();
    double length_jl = l.length();
    if (isNearZero(length_ji) || isNearZero(length_jk) || isNearZero(length_jl)) {
      i.set(0, 0, 0);
      j.set(0, 0, 0);
      k.set(0, 0, 0);
      l.set(0, 0, 0);
      return 0.0;
    }
    
    
    i.normalize();
    k.normalize();
    l.normalize();

    
    double cos_theta = i.dot(k);
    double     theta = Math.acos(cos_theta);

    
    if (isNearZero(theta) || isNearZero(Math.abs(theta - Math.PI))) {
      i.set(0, 0, 0);
      j.set(0, 0, 0);
      k.set(0, 0, 0);
      l.set(0, 0, 0);
      return 0.0;
    }
    double csc_theta = 1 / Math.sin(theta);

    
    
    an.cross(i, k); 
    bn.cross(k, l); 
    cn.cross(l, i); 

    
    
    double sin_dl = an.dot(l) * csc_theta;
    double     dl = Math.asin(sin_dl);
    double cos_dl = Math.cos(dl);

    
    
    if (cos_dl < 0.0001 || isNearZero(dl) 
        || isNearZero(Math.abs(dl - Math.PI))) {
      i.set(0, 0, 0);
      j.set(0, 0, 0);
      k.set(0, 0, 0);
      l.set(0, 0, 0);
      return dl;
    }
        
    

    
    
    
    l.scaleAdd(-sin_dl / csc_theta, l, an);
    l.scale(csc_theta / length_jl);

    j.set(i); 
    
    

    
    
    
    
    i.scaleAdd(-cos_theta, k, i);
    i.scaleAdd(-sin_dl * csc_theta, i, bn);
    i.scale(csc_theta / length_ji);

    

    
    
    

    
    k.scaleAdd(-cos_theta, j, k);
    k.scaleAdd(-sin_dl * csc_theta, k, cn);
    k.scale(csc_theta / length_jk);

    

    j.set(i);
    j.add(k);
    j.add(l);
    j.scale(-1);

    return dl;
  }


  public static double restorativeForceAndTorsionAngleRadians(Vector3d i, Vector3d j,
                                                      Vector3d k, Vector3d l) {
    
    

    

    i.sub(j, i);
    j.sub(k, j);
    k.sub(l, k);

    double len_ij = i.length();
    double len_jk = j.length();
    double len_kl = k.length();

    if (isNearZero(len_ij) || isNearZero(len_jk) || isNearZero(len_kl)) {
      i.set(0, 0, 0);
      j.set(0, 0, 0);
      k.set(0, 0, 0);
      l.set(0, 0, 0);
      return 0.0;
    }

    double ang = vectorAngleRadians(i, j);
    double sin_j = Math.sin(ang);
    double cos_j = Math.cos(ang);
    
    ang = vectorAngleRadians(j, k);
    double sin_k = Math.sin(ang);
    double cos_k = Math.cos(ang);

    

    i.normalize();
    j.normalize();
    k.normalize();

    
    i.cross(i, j);  
    l.cross(j, k);  
    k.cross(i, l);  

    double theta = -Math.atan2(
        k.dot(j),  
        i.dot(l)); 
        
    i.scale(1. / len_ij / sin_j / sin_j);
   
    l.scale(-1. / len_kl / sin_k / sin_k);

    j.set(i);
    j.scale(-len_ij / len_jk * cos_j - 1.);
    k.set(l);
    k.scale(-len_kl / len_jk * cos_k);
    j.sub(k);
    
    k.set(i);
    k.add(j);
    k.add(l);
    k.scale(-1);

    return theta;
  }

}
