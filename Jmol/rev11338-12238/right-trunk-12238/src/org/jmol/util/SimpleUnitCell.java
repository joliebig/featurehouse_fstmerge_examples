


package org.jmol.util;


import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;

import org.jmol.viewer.JmolConstants;


public class SimpleUnitCell {
  
  protected final static float toRadians = (float) Math.PI * 2 / 360;
  protected float a, b, c, alpha, beta, gamma;
  protected boolean isPrimitive;
  protected float[] notionalUnitcell; 
  protected Matrix4f matrixCartesianToFractional;
  protected Matrix4f matrixFractionalToCartesian;

  public SimpleUnitCell(float[] notionalUnitcell) {
    setUnitCell(notionalUnitcell);
  }

  public SimpleUnitCell(float a, float b, float c, 
                        float alpha, float beta, float gamma) {
    setUnitCell(new float[] {a, b, c, alpha, beta, gamma });
  }

  public final void toCartesian(Point3f pt) {
    if (matrixFractionalToCartesian == null)
      return;
    matrixFractionalToCartesian.transform(pt);
  }
  
  public final void toFractional(Point3f pt) {
    if (matrixCartesianToFractional == null)
      return;
    matrixCartesianToFractional.transform(pt);
  }
  
  public final float[] getNotionalUnitCell() {
    return notionalUnitcell;
  }
  
  public final float getInfo(int infoType) {
    switch (infoType) {
    case JmolConstants.INFO_A:
      return a;
    case JmolConstants.INFO_B:
      return b;
    case JmolConstants.INFO_C:
      return c;
    case JmolConstants.INFO_ALPHA:
      return alpha;
    case JmolConstants.INFO_BETA:
      return beta;
    case JmolConstants.INFO_GAMMA:
      return gamma;
    }
    return Float.NaN;
  }
  
  
  
  private void setUnitCell(float[] notionalUnitcell) {
    if (notionalUnitcell == null || notionalUnitcell[0] == 0)
      return;
    this.notionalUnitcell = notionalUnitcell;

    a = notionalUnitcell[JmolConstants.INFO_A];
    b = notionalUnitcell[JmolConstants.INFO_B];
    c = notionalUnitcell[JmolConstants.INFO_C];
    alpha = notionalUnitcell[JmolConstants.INFO_ALPHA];
    beta = notionalUnitcell[JmolConstants.INFO_BETA];
    gamma = notionalUnitcell[JmolConstants.INFO_GAMMA];
    constructFractionalMatrices();
  }

  protected Data data;
  
  protected class Data {
    public double cosAlpha, sinAlpha;
    public double cosBeta, sinBeta;
    public double cosGamma, sinGamma;
    public double volume;
    public double cA_, cB_;
    public double a_;
    public double b_, c_;
    
    public Data() {
      cosAlpha = Math.cos(toRadians * alpha);
      sinAlpha = Math.sin(toRadians * alpha);
      cosBeta = Math.cos(toRadians * beta);
      sinBeta = Math.sin(toRadians * beta);
      cosGamma = Math.cos(toRadians * gamma);
      sinGamma = Math.sin(toRadians * gamma);
      double unitVolume = Math.sqrt(sinAlpha * sinAlpha + sinBeta * sinBeta
          + sinGamma * sinGamma + 2.0 * cosAlpha * cosBeta * cosGamma - 2);
      volume = a * b * c * unitVolume;
      
      cA_ = (cosAlpha - cosBeta * cosGamma) / sinGamma;
      cB_ = unitVolume / sinGamma;
      a_ = b * c * sinAlpha / volume;
      b_ = a * c * sinBeta / volume;
      c_ = a * b * sinGamma / volume;
    }
  }
  
  private void constructFractionalMatrices() {
    if (notionalUnitcell.length > 6 && !Float.isNaN(notionalUnitcell[21])) {
      float[] scaleMatrix = new float[16];
      for (int i = 0; i < 16; i++)
        scaleMatrix[i] = notionalUnitcell[6 + i];
      matrixCartesianToFractional = new Matrix4f(scaleMatrix);
      matrixFractionalToCartesian = new Matrix4f();
      matrixFractionalToCartesian.invert(matrixCartesianToFractional);
    } else if (notionalUnitcell.length > 6 && !Float.isNaN(notionalUnitcell[14])) {
      isPrimitive = true;
      Matrix4f m = matrixFractionalToCartesian = new Matrix4f();
      float[] n = notionalUnitcell;
      if (data == null)
        data = new Data();
      m.setColumn(0, n[6], n[7], n[8], 0);
      m.setColumn(1, n[9], n[10], n[11], 0);
      m.setColumn(2, n[12], n[13], n[14], 0);
      m.setColumn(3, 0, 0, 0, 1);
      matrixCartesianToFractional = new Matrix4f();
      matrixCartesianToFractional.invert(matrixFractionalToCartesian);
    } else {
      Matrix4f m = matrixFractionalToCartesian = new Matrix4f();
      if (data == null)
        data = new Data();
      
      m.setColumn(0, a, 0, 0, 0);
      
      m.setColumn(1, (float) (b * data.cosGamma), 
          (float) (b * data.sinGamma), 0, 0);
      
      
      m.setColumn(2, (float) (c * data.cosBeta), 
          (float) (c * (data.cosAlpha - data.cosBeta * data.cosGamma) / data.sinGamma), 
          (float) (data.volume / (a * b * data.sinGamma)), 0);
      m.setColumn(3, 0, 0, 0, 1);
      matrixCartesianToFractional = new Matrix4f();
      matrixCartesianToFractional.invert(matrixFractionalToCartesian);
    }
  }
}
