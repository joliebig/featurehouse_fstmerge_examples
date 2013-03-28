

package org.jmol.util;





public class Eigen {

  private double[] d, e;
  private double[][] Vx;
  private double[][] Vo;

  public Eigen(double[][] A) {

    Vx = new double[3][3];
    Vo = new double[3][3];
    d = new double[3];
    e = new double[3];

    for (int i = 0; i < 3; i++)
      for (int j = 0; j < 3; j++)
        Vx[i][j] = Vo[i][j] = A[i][j];

    
    tred2();

    
    tql2();

    

    for (int i = 0; i < 2; i++) {
      int k = i;
      double p = d[i];
      for (int j = i + 1; j < 3; j++) {
        if (d[j] < p) {
          k = j;
          p = d[j];
        }
      }
      if (k != i) {
        d[k] = d[i];
        d[i] = p;
        for (int j = 0; j < 3; j++) {
          p = Vx[i][j];
          Vx[i][j] = Vx[k][j];
          Vx[k][j] = p;
        }
      }
    }
    

    
  }

  public double[][] getEigenvectors() {
    return Vx;
  }

  public double[] getEigenvalues() {
    return d;
  }

  

  
  private void tred2() {

    
    
    
    

    for (int j = 0; j < 3; j++) {
      d[j] = Vx[j][2];
    }

    

    for (int i = 2; i > 0; i--) {

      

      double scale = 0.0;
      double h = 0.0;
      for (int k = 0; k < i; k++) {
        scale = scale + Math.abs(d[k]);
      }
      if (scale == 0.0) {
        e[i] = d[i - 1];
        for (int j = 0; j < i; j++) {
          d[j] = Vx[j][i - 1];
          Vx[j][i] = 0.0;
          Vx[i][j] = 0.0;
        }
      } else {

        

        for (int k = 0; k < i; k++) {
          d[k] /= scale;
          h += d[k] * d[k];
        }
        double f = d[i - 1];
        double g = Math.sqrt(h);
        if (f > 0) {
          g = -g;
        }
        e[i] = scale * g;
        h = h - f * g;
        d[i - 1] = f - g;
        for (int j = 0; j < i; j++) {
          e[j] = 0.0;
        }

        

        for (int j = 0; j < i; j++) {
          f = d[j];
          Vx[i][j] = f;
          g = e[j] + Vx[j][j] * f;
          for (int k = j + 1; k <= i - 1; k++) {
            g += Vx[j][k] * d[k];
            e[k] += Vx[j][k] * f;
          }
          e[j] = g;
        }
        f = 0.0;
        for (int j = 0; j < i; j++) {
          e[j] /= h;
          f += e[j] * d[j];
        }
        double hh = f / (h + h);
        for (int j = 0; j < i; j++) {
          e[j] -= hh * d[j];
        }
        for (int j = 0; j < i; j++) {
          f = d[j];
          g = e[j];
          for (int k = j; k <= i - 1; k++) {
            Vx[j][k] -= (f * e[k] + g * d[k]);
          }
          d[j] = Vx[j][i - 1];
          Vx[j][i] = 0.0;
        }
      }
      d[i] = h;
    }

    

    for (int i = 0; i < 2; i++) {
      Vx[i][2] = Vx[i][i];
      Vx[i][i] = 1.0;
      double h = d[i + 1];
      if (h != 0.0) {
        for (int k = 0; k <= i; k++) {
          d[k] = Vx[i + 1][k] / h;
        }
        for (int j = 0; j <= i; j++) {
          double g = 0.0;
          for (int k = 0; k <= i; k++) {
            g += Vx[i + 1][k] * Vx[j][k];
          }
          for (int k = 0; k <= i; k++) {
            Vx[j][k] -= g * d[k];
          }
        }
      }
      for (int k = 0; k <= i; k++) {
        Vx[i + 1][k] = 0.0;
      }
    }
    for (int j = 0; j < 3; j++) {
      d[j] = Vx[j][2];
      Vx[j][2] = 0.0;
    }
    Vx[2][2] = 1.0;
    e[0] = 0.0;
  }

  

  private void tql2() {

    
    
    
    

    for (int i = 1; i < 3; i++) {
      e[i - 1] = e[i];
    }
    e[2] = 0.0;

    double f = 0.0;
    double tst1 = 0.0;
    double eps = Math.pow(2.0, -52.0);
    for (int l = 0; l < 3; l++) {

      

      tst1 = Math.max(tst1, Math.abs(d[l]) + Math.abs(e[l]));
      int m = l;
      while (m < 3) {
        if (Math.abs(e[m]) <= eps * tst1) {
          break;
        }
        m++;
      }

      
      

      if (m > l) {
        int iter = 0;
        do {
          iter = iter + 1; 

          

          double g = d[l];
          double p = (d[l + 1] - g) / (2.0 * e[l]);
          double r = hypot(p, 1.0);
          if (p < 0) {
            r = -r;
          }
          d[l] = e[l] / (p + r);
          d[l + 1] = e[l] * (p + r);
          double dl1 = d[l + 1];
          double h = g - d[l];
          for (int i = l + 2; i < 3; i++) {
            d[i] -= h;
          }
          f = f + h;

          

          p = d[m];
          double c = 1.0;
          double c2 = c;
          double c3 = c;
          double el1 = e[l + 1];
          double s = 0.0;
          double s2 = 0.0;
          for (int i = m - 1; i >= l; i--) {
            c3 = c2;
            c2 = c;
            s2 = s;
            g = c * e[i];
            h = c * p;
            r = hypot(p, e[i]);
            e[i + 1] = s * r;
            s = e[i] / r;
            c = p / r;
            p = c * d[i] - s * g;
            d[i + 1] = h + s * (c * g + s * d[i]);

            

            for (int k = 0; k < 3; k++) {
              h = Vx[i + 1][k];
              Vx[i + 1][k] = s * Vx[i][k] + c * h;
              Vx[i][k] = c * Vx[i][k] - s * h;
            }
          }
          p = -s * s2 * c3 * el1 * e[l] / dl1;
          e[l] = s * p;
          d[l] = c * p;

          

        } while (Math.abs(e[l]) > eps * tst1);
      }
      d[l] = d[l] + f;
      e[l] = 0.0;
    }
  }

  private static double hypot(double a, double b) {

    

    double r;
    if (Math.abs(a) > Math.abs(b)) {
      r = b / a;
      r = Math.abs(a) * Math.sqrt(1 + r * r);
    } else if (b != 0) {
      r = a / b;
      r = Math.abs(b) * Math.sqrt(1 + r * r);
    } else {
      r = 0.0;
    }
    return r;
  }

  public void dump() {
    System.out.println("-----Eigen input-----");
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++)
        System.out.print(Vo[i][j] + "\t");
      System.out.println();
    }
    dump(0);
    dump(1);
    dump(2);
  }

  private void dump(int i) {
    System.out.println("Eigen: lambda = " + d[i] + " for " + Vx[i][0] + " " + Vx[i][1] + " " + Vx[i][2]);
  }
  
  public static float[][] toFloat3x3(double[][] d) {
    float[][] f = new float[3][3];
    for (int i = 3; --i >= 0; )
      for (int j = 3; --j >= 0; )
        f[i][j] = (float) d[i][j];
    return f;
  }
  
  public static float[] toFloat(double[] d) {
    float[] f = new float[d.length];
    for (int i = d.length; --i >= 0; )
        f[i] = (float) d[i];
    return f;
  }
  

}
