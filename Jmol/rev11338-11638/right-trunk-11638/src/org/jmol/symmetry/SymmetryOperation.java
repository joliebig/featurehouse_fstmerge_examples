

package org.jmol.symmetry;

import java.util.Vector;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Point4f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector3f;

import org.jmol.api.Interface;
import org.jmol.api.SymmetryInterface;
import org.jmol.api.TriangleServer;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Measure;
import org.jmol.util.Parser;
import org.jmol.util.Quaternion;
import org.jmol.script.Token;



class SymmetryOperation extends Matrix4f {
  String xyzOriginal;
  String xyz;
  boolean doNormalize = true;
  boolean isFinalized;

  SymmetryOperation() {
  }

  SymmetryOperation(boolean doNormalize) {
    this.doNormalize = doNormalize;
  }

  SymmetryOperation(SymmetryOperation op, Point3f[] atoms,
                           int atomIndex, int count, boolean doNormalize) {
    
    this.doNormalize = doNormalize;
    xyzOriginal = op.xyzOriginal;
    xyz = op.xyz;
    set(op); 
    doFinalize();
    if (doNormalize)
      setOffset(atoms, atomIndex, count);
  }

  void doFinalize() {
    m03 /= 12f;
    m13 /= 12f;
    m23 /= 12f;
    isFinalized = true;
  }
  
  String getXyz(boolean normalized) {
    return (normalized || xyzOriginal == null ? xyz : xyzOriginal);
  }

  private Point4f temp = new Point4f();
  void newPoint(Point3f atom1, Point3f atom2,
                       int transX, int transY, int transZ) {
    temp.set(atom1.x, atom1.y, atom1.z, 1);
    transform(temp, temp);
    atom2.set(temp.x + transX, temp.y + transY, temp.z + transZ);
  }

  String dumpInfo() {
    return "\n" + xyz + "\ninternal matrix representation:\n"
        + ((Matrix4f) this).toString();
  }

  final static String dumpSeitz(Matrix4f s) {
    return (new StringBuffer("{\t")).append((int) s.m00).append("\t").append((int) s.m01)
        .append("\t").append((int) s.m02).append("\t").append(twelfthsOf(s.m03)).append("\t}\n")
        .append("{\t").append((int) s.m10).append("\t").append((int) s.m11).append("\t").append((int) s.m12)
        .append("\t").append(twelfthsOf(s.m13)).append("\t}\n")
        .append("{\t").append((int) s.m20).append("\t").append((int) s.m21).append("\t").append((int) s.m22)
        .append("\t").append(twelfthsOf(s.m23)).append("\t}\n").append("{\t0\t0\t0\t1\t}\n").toString();
  }
  
  final static String dumpCanonicalSeitz(Matrix4f s) {
    return (new StringBuffer()).append("{\t").append((int) s.m00).append("\t").append((int) s.m01)
        .append("\t").append((int) s.m02).append("\t").append(twelfthsOf(s.m03+12)).append("\t}\n")
        .append("{\t").append((int) s.m10).append("\t").append((int) s.m11).append("\t").append((int) s.m12)
        .append("\t").append(twelfthsOf(s.m13+12)).append("\t}\n").append("{\t").append((int) s.m20)
        .append("\t").append((int) s.m21).append("\t")
        .append((int) s.m22).append("\t").append(twelfthsOf(s.m23+12)).append("\t}\n")
        .append("{\t0\t0\t0\t1\t}\n").toString();
  }
  
  boolean setMatrixFromXYZ(String xyz) {
    
    if (xyz == null)
      return false;
    xyzOriginal = xyz;
    xyz = xyz.toLowerCase();
    float[] temp = new float[16];
    boolean isDenominator = false;
    boolean isDecimal = false;
    boolean isNegative = false;
    boolean isReverse = (xyz.startsWith("!"));
    if (isReverse)
      xyz = xyz.substring(1);
    char ch;
    int x = 0;
    int y = 0;
    int z = 0;
    float iValue = 0;
    String strOut = "";
    String strT;
    int rowPt = -1;
    float decimalMultiplier = 1f;
    if (xyz.indexOf("xyz matrix:") == 0) {
      
      this.xyz = xyz;
      Parser.parseFloatArray(xyz, null, temp);
      for (int i = 0; i < 16; i++) {
        if (Float.isNaN(temp[i]))
          return false;
        float v = temp[i];
        if (Math.abs(v) < 0.00001f)
          v = 0;
        if (i % 4 == 3)
          v = normalizeTwelfths((v < 0 ? -1 : 1) * Math.round(Math.abs(v * 12)));
        temp[i] = v;
      }
      temp[15] = 1;
      set(temp);
      isFinalized = true;
      if (isReverse)
        invert(this);
      this.xyz = getXYZFromMatrix(this, true, false, false);
      return true;
    }
    if (xyz.indexOf("[[") == 0) {
      xyz = xyz.replace('[',' ').replace(']',' ').replace(',',' ');
      Parser.parseFloatArray(xyz, null, temp);
      for (int i = 0; i < 16; i++) {
        if (Float.isNaN(temp[i]))
          return false;
      }
      set(temp);
      isFinalized = true;
      if (isReverse)
        invert(this);
      this.xyz = getXYZFromMatrix(this, false, false, false);
      
      return true;
    }
    xyz += ",";
    for (int i = 0; i < xyz.length(); i++) {
      ch = xyz.charAt(i);
      switch (ch) {
      case '\'':
      case ' ':
      case '{':
      case '}':
      case '!':
        continue;
      case '-':
        isNegative = true;
        continue;
      case '+':
        isNegative = false;
        continue;
      case '/':
        isDenominator = true;
        continue;
      case 'X':
      case 'x':
        x = (isNegative ? -1 : 1);
        break;
      case 'Y':
      case 'y':
        y = (isNegative ? -1 : 1);
        break;
      case 'Z':
      case 'z':
        z = (isNegative ? -1 : 1);
        break;
      case ',':
        if (++rowPt > 2) {
          Logger.warn("Symmetry Operation? " + xyz);
          return false;
        }
        int tpt = rowPt * 4;
        
        iValue = normalizeTwelfths(iValue);
        temp[tpt++] = x;
        temp[tpt++] = y;
        temp[tpt++] = z;
        temp[tpt] = iValue;
        strT = "";
        strT += (x == 0 ? "" : x < 0 ? "-x" : strT.length() == 0 ? "x" : "+x");
        strT += (y == 0 ? "" : y < 0 ? "-y" : strT.length() == 0 ? "y" : "+y");
        strT += (z == 0 ? "" : z < 0 ? "-z" : strT.length() == 0 ? "z" : "+z");
        strT += xyzFraction(iValue, false, true);
        strOut += (strOut == "" ? "" : ",") + strT;
        
        if (rowPt == 2) {
          temp[15] = 1;    
          set(temp);
          if (isReverse) {
            invert(this);
            this.xyz = getXYZFromMatrix(this, true, false, false);
          } else {
            this.xyz = strOut;
          }
          if (Logger.debugging)
            Logger.debug("" + (Matrix4f)this);
          rowPt = 0;
          return true;
        }
        x = y = z = 0;
        iValue = 0;
        break;
      case '.':
        isDecimal = true;
        decimalMultiplier = 1f;
        continue;
      case '0':
        if (!isDecimal)
          continue;
      
      default:
        
        int ich = ch - '0';
        if (isDecimal && ich >= 0 && ich <= 9) {
          decimalMultiplier /= 10f;
          if (iValue < 0)
            isNegative = true;
          iValue += decimalMultiplier * ich * (isNegative ? -1 : 1);
          continue;
        }
        if (ich >= 1 && ich <= 9) {
          if (isDenominator) {
            iValue /= ich;
          } else {
            iValue = (isNegative ? -1f : 1f) * ich;
          }
        } else {
          Logger.warn("symmetry character?" + ch);
        }
      }
      isDecimal = isDenominator = isNegative = false;
    }
    return false;
  }

  private float normalizeTwelfths(float iValue) {
    iValue *= 12f;
    if (doNormalize) {
      while (iValue > 6)
        iValue -= 12;
      while (iValue <= -6)
        iValue += 12;
    }
    return iValue;
  }

  final static String getXYZFromMatrix(Matrix4f mat, boolean is12ths,
                                       boolean allPositive, boolean halfOrLess) {
    String str = "";
    float[] row = new float[4];
    for (int i = 0; i < 3; i++) {
      mat.getRow(i, row);
      String term = "";
      if (row[0] != 0)
        term += (row[0] < 0 ? "-" : "+") + "x";
      if (row[1] != 0)
        term += (row[1] < 0 ? "-" : "+") + "y";
      if (row[2] != 0)
        term += (row[2] < 0 ? "-" : "+") + "z";
      term += xyzFraction((is12ths ? row[3] : row[3] * 12), allPositive,
          halfOrLess);
      if (term.length() > 0 && term.charAt(0) == '+')
        term = term.substring(1);
      str += "," + term;
    }
    return str.substring(1);
  }

  private final static String twelfthsOf(float n12ths) {
    String str = "";
    int i12ths = Math.round(n12ths);
    if (i12ths < 0) {
      i12ths = -i12ths;
      if (i12ths % 12 != 0)
        str = "-";
    }
    return str + twelfths[i12ths % 12];  
  }
  
  private final static String[] twelfths = { "0", "1/12", "1/6", "1/4", "1/3",
      "5/12", "1/2", "7/12", "2/3", "3/4", "5/6", "11/12" };

  private final static String xyzFraction(float n12ths, boolean allPositive, boolean halfOrLess) {
    n12ths = Math.round(n12ths);
    if (allPositive) {
      while (n12ths < 0)
        n12ths += 12f;
    } else if (halfOrLess && n12ths > 6f) {
      n12ths -= 12f;
    }
    String s = twelfthsOf(n12ths);
    return (s.charAt(0) == '0' ? "" : n12ths > 0 ? "+" + s : s);
  }

  Point3f atomTest = new Point3f();

  private void setOffset(Point3f[] atoms, int atomIndex, int count) {
    
    int i1 = atomIndex;
    int i2 = i1 + count;
    float x = 0;
    float y = 0;
    float z = 0;
    for (int i = i1; i < i2; i++) {
      newPoint(atoms[i], atomTest, 0, 0, 0);
      x += atomTest.x;
      y += atomTest.y;
      z += atomTest.z;
    }
    
    while (x < -0.001 || x >= count + 0.001) {
      m03 += (x < 0 ? 1 : -1);
      x += (x < 0 ? count : -count);
    }
    while (y < -0.001 || y >= count + 0.001) {
      m13 += (y < 0 ? 1 : -1);
      y += (y < 0 ? count : -count);
    }
    while (z < -0.001 || z >= count + 0.001) {
      m23 += (z < 0 ? 1 : -1);
      z += (z < 0 ? count : -count);
    }
  }

  private void transformCartesian(UnitCell unitcell, Point3f pt) {
    unitcell.toFractional(pt);
    transform(pt);
    unitcell.toCartesian(pt);

  }
  
  Vector3f[] rotateEllipsoid(Point3f cartCenter, Vector3f[] vectors,
                                    UnitCell unitcell, Point3f ptTemp1, Point3f ptTemp2) {
    Vector3f[] vRot = new Vector3f[3];
    ptTemp2.set(cartCenter);
    transformCartesian(unitcell, ptTemp2);
    for (int i = vectors.length; --i >= 0;) {
      ptTemp1.set(cartCenter);
      ptTemp1.add(vectors[i]);
      transformCartesian(unitcell, ptTemp1);
      vRot[i] = new Vector3f(ptTemp1);
      vRot[i].sub(ptTemp2);
    }
    return vRot;
  }
  
  
  public Object[] getDescription(SymmetryInterface uc, Point3f pt00, String id) {
    if (!isFinalized)
      doFinalize();
    return getDescription(this, xyzOriginal, uc, pt00, id);
  }
  
  private static Object[] getDescription(Matrix4f m, String xyzOriginal,
                                         SymmetryInterface uc, Point3f pt00,
                                         String id) {
    Vector3f vtemp = new Vector3f();
    Point3f ptemp = new Point3f();
    Vector3f ftrans = new Vector3f();
    String xyz = getXYZFromMatrix(m, false, false, false);
    boolean typeOnly = (id == null);
    if (pt00 == null || Float.isNaN(pt00.x))
      pt00 = new Point3f();

    Point3f pt01 = new Point3f(1, 0, 0);
    Point3f pt02 = new Point3f(0, 1, 0);
    Point3f pt03 = new Point3f(0, 0, 1);
    pt01.add(pt00);
    pt02.add(pt00);
    pt03.add(pt00);

    Point3f p0 = new Point3f(pt00);
    Point3f p1 = new Point3f(pt01);
    Point3f p2 = new Point3f(pt02);
    Point3f p3 = new Point3f(pt03);

    uc.toFractional(p0);
    uc.toFractional(p1);
    uc.toFractional(p2);
    uc.toFractional(p3);
    m.transform(p0, p0);
    m.transform(p1, p1);
    m.transform(p2, p2);
    m.transform(p3, p3);
    uc.toCartesian(p0);
    uc.toCartesian(p1);
    uc.toCartesian(p2);
    uc.toCartesian(p3);

    Vector3f v01 = new Vector3f();
    v01.sub(p1, p0);
    Vector3f v02 = new Vector3f();
    v02.sub(p2, p0);
    Vector3f v03 = new Vector3f();
    v03.sub(p3, p0);

    vtemp.cross(v01, v02);
    boolean haveinversion = (vtemp.dot(v03) < 0);

    
    

    if (haveinversion) {

      

      p1.scaleAdd(-2, v01, p1);
      p2.scaleAdd(-2, v02, p2);
      p3.scaleAdd(-2, v03, p3);

    }

    
    
    
    
    

    Object[] info;
    info = (Object[]) Measure.computeHelicalAxis(null, Token.array, pt00, p0,
        Quaternion.getQuaternionFrame(p0, p1, p2).div(
            Quaternion.getQuaternionFrame(pt00, pt01, pt02)));
    Point3f pa1 = (Point3f) info[0];
    Vector3f ax1 = (Vector3f) info[1];
    int ang1 = (int) Math.abs(approx(((Point3f) info[3]).x, 1));
    float pitch1 = approx(((Point3f) info[3]).y);

    if (haveinversion) {

      

      p1.scaleAdd(2, v01, p1);
      p2.scaleAdd(2, v02, p2);
      p3.scaleAdd(2, v03, p3);

    }

    Vector3f trans = new Vector3f(p0);
    trans.sub(pt00);
    if (trans.length() < 0.1f)
      trans = null;

    

    Point3f ptinv = null; 
    Point3f ipt = null; 
    Point3f pt0 = null; 

    boolean istranslation = (ang1 == 0);
    boolean isrotation = !istranslation;
    boolean isinversion = false;
    boolean ismirrorplane = false;

    if (isrotation || haveinversion)
      trans = null;

    

    if (haveinversion && istranslation) {

      

      ipt = new Point3f(pt00);
      ipt.add(p0);
      ipt.scale(0.5f);
      ptinv = p0;
      isinversion = true;
    } else if (haveinversion) {

      

      Vector3f d = (pitch1 == 0 ? new Vector3f() : ax1);
      float f = 0;
      switch (ang1) {
      case 60: 
        f = 2f / 3f;
        break;
      case 120: 
        f = 2;
        break;
      case 90: 
        f = 1;
        break;
      case 180: 
        
        
        pt0 = new Point3f();
        pt0.set(pt00);
        pt0.add(d);
        pa1.scaleAdd(0.5f, d, pt00);
        if (pt0.distance(p0) > 0.1f) {
          trans = new Vector3f(p0);
          trans.sub(pt0);
          ptemp.set(trans);
          uc.toFractional(ptemp);
          ftrans.set(ptemp);
        } else {
          trans = null;
        }
        isrotation = false;
        haveinversion = false;
        ismirrorplane = true;
      }
      if (f != 0) {
        

        vtemp.set(pt00);
        vtemp.sub(pa1);
        vtemp.add(p0);
        vtemp.sub(pa1);
        vtemp.sub(d);
        vtemp.scale(f);
        pa1.add(vtemp);
        ipt = new Point3f();
        ipt.scaleAdd(0.5f, d, pa1);
        ptinv = new Point3f();
        ptinv.scaleAdd(-2, ipt, pt00);
        ptinv.scale(-1);
      }

    } else if (trans != null) {

      
      

      ptemp.set(trans);
      uc.toFractional(ptemp);
      if (approx(ptemp.x) == 1) {
        ptemp.x = 0;
      }
      if (approx(ptemp.y) == 1) {
        ptemp.y = 0;
      }
      if (approx(ptemp.z) == 1) {
        ptemp.z = 0;
      }
      ftrans.set(ptemp);
      uc.toCartesian(ptemp);
      trans.set(ptemp);
    }

    
    
    int ang = ang1;
    approx(ax1);
    
    if (isrotation) {

      Point3f pt1 = new Point3f();

      vtemp.set(ax1);

      

      int ang2 = ang1;
      if (haveinversion) {
        pt1.set(pa1);
        pt1.add(vtemp);
        ang2 = (int) Measure.computeTorsion(ptinv, pa1, pt1, p0, true);
      } else if (pitch1 == 0) {
        pt1.set(pa1);
        ptemp.scaleAdd(1, pt1, vtemp);
        ang2 = (int) Measure.computeTorsion(pt00, pa1, ptemp, p0, true);
      } else {
        ptemp.set(pa1);
        ptemp.add(vtemp);
        pt1.scaleAdd(0.5f, vtemp, pa1);
        ang2 = (int) Measure.computeTorsion(pt00, pa1, ptemp, p0, true);
      }

      if (ang2 != 0)
        ang1 = ang2;
    }

    if (isrotation && !haveinversion && pitch1 == 0) {
      if (ax1.z < 0 || ax1.z == 0 && (ax1.y < 0 || ax1.y == 0 && ax1.x < 0)) {
        ax1.scale(-1);
        ang1 = -ang1;
      }
    }
    
    

    String info1 = "identity";
    StringBuffer draw1 = new StringBuffer();
    String drawid;

    if (isinversion) {
      ptemp.set(ipt);
      uc.toFractional(ptemp);
      info1 = "inversion center|" + fcoord(ptemp);
    } else if (isrotation) {
      if (haveinversion) {
        info1 = "" + (360 / ang) + "-bar axis";
      } else if (pitch1 != 0) {
        info1 = "" + (360 / ang) + "-fold screw axis";
        ptemp.set(ax1);
        uc.toFractional(ptemp);
        info1 += "|translation: " + fcoord(ptemp);
      } else {
        info1 = "C" + (360 / ang) + " axis";
      }
    } else if (trans != null) {
      String s = " " + fcoord(ftrans);
      if (istranslation) {
        info1 = "translation:" + s;
      } else if (ismirrorplane) {
        float fx = approx(ftrans.x);
        float fy = approx(ftrans.y);
        float fz = approx(ftrans.z);
        s = " " + fcoord(ftrans);
        if (fx != 0 && fy != 0 && fz != 0)
          info1 = "d-";
        else if (fx != 0 && fy != 0 || fy != 0 && fz != 0 || fz != 0 && fx != 0)
          info1 = "n-";
        else if (fx != 0)
          info1 = "a-";
        else if (fy != 0)
          info1 = "b-";
        else
          info1 = "c-";
        info1 += "glide plane |translation:" + s;
      }
    } else if (ismirrorplane) {
      info1 = "mirror plane";
    }

    if (haveinversion && !isinversion) {
      ptemp.set(ipt);
      uc.toFractional(ptemp);
      info1 += "|inversion center at " + fcoord(ptemp);
    }

    Logger.info(xyz + ": " + info1);
    String cmds = null;
    if (!typeOnly) {
      drawid = "\ndraw ID " + id + "_";

      

      draw1 = new StringBuffer();
      draw1.append("// " + xyzOriginal + "|" + xyz + "|" + info1 + "\n");
      draw1.append(drawid).append("* delete");

      

      drawLine(draw1, drawid + "frame1X", 0.15f, pt00, pt01, "red");
      drawLine(draw1, drawid + "frame1Y", 0.15f, pt00, pt02, "green");
      drawLine(draw1, drawid + "frame1Z", 0.15f, pt00, pt03, "blue");

      
      

      ptemp.set(p1);
      ptemp.sub(p0);
      ptemp.scaleAdd(0.9f, ptemp, p0);
      drawLine(draw1, drawid + "frame2X", 0.2f, p0, ptemp, "red");
      ptemp.set(p2);
      ptemp.sub(p0);
      ptemp.scaleAdd(0.9f, ptemp, p0);
      drawLine(draw1, drawid + "frame2Y", 0.2f, p0, ptemp, "green");
      ptemp.set(p3);
      ptemp.sub(p0);
      ptemp.scaleAdd(0.9f, ptemp, p0);
      drawLine(draw1, drawid + "frame2Z", 0.2f, p0, ptemp, "purple");

      String color;

      if (isrotation) {

        Point3f pt1 = new Point3f();

        color = "red";

        ang = ang1;
        float scale = 1.0f;
        vtemp.set(ax1);

        

        if (haveinversion) {
          pt1.set(pa1);
          pt1.add(vtemp);
          if (pitch1 == 0) {
            pt1.set(ipt);
            vtemp.scale(3);
            ptemp.scaleAdd(-1, vtemp, pa1);
            draw1.append(drawid).append("rotVector2 diameter 0.1 ")
                .append(Escape.escape(pa1)).append(Escape.escape(ptemp)).append(
                    " color red");
          }
          scale = p0.distance(pt1);
          draw1.append(drawid).append("rotLine1 ").append(Escape.escape(pt1))
              .append(Escape.escape(ptinv)).append(" color red");
          draw1.append(drawid).append("rotLine2 ").append(Escape.escape(pt1))
              .append(Escape.escape(p0)).append(" color red");
        } else if (pitch1 == 0) {
          boolean isSpecial = (pt00.distance(p0) < 0.2f);
          if (!isSpecial) {
            draw1.append(drawid).append("rotLine1 ")
                .append(Escape.escape(pt00)).append(Escape.escape(pa1)).append(
                    " color red");
            draw1.append(drawid).append("rotLine2 ").append(Escape.escape(p0))
                .append(Escape.escape(pa1)).append(" color red");
          }
          vtemp.scale(3);
          ptemp.scaleAdd(-1, vtemp, pa1);
          draw1.append(drawid).append("rotVector2 diameter 0.1 ")
              .append(Escape.escape(pa1)).append(Escape.escape(ptemp)).append(
                  " color red");
          pt1.set(pa1);
          if (pitch1 == 0 && pt00.distance(p0) < 0.2)
            pt1.scaleAdd(0.5f, pt1, vtemp);
        } else {
          
          color = "orange";
          draw1.append(drawid).append("rotLine1 ").append(Escape.escape(pt00))
              .append(Escape.escape(pa1)).append(" color red");
          ptemp.set(pa1);
          ptemp.add(vtemp);
          draw1.append(drawid).append("rotLine2 ").append(Escape.escape(p0))
              .append(Escape.escape(ptemp)).append(" color red");
          pt1.scaleAdd(0.5f, vtemp, pa1);
        }

        

        ptemp.set(pt1);
        ptemp.add(vtemp);
        if (haveinversion && pitch1 != 0) {
          draw1.append(drawid).append("rotRotLine1").append(Escape.escape(pt1))
              .append(Escape.escape(ptinv)).append(" color red");
          draw1.append(drawid).append("rotRotLine2").append(Escape.escape(pt1))
              .append(Escape.escape(p0)).append(" color red");
        }
        draw1.append(drawid).append(
            "rotRotArrow arrow width 0.10 scale " + scale + " arc ").append(
            Escape.escape(pt1)).append(Escape.escape(ptemp));
        if (haveinversion)
          ptemp.set(ptinv);
        else
          ptemp.set(pt00);
        if (ptemp.distance(p0) < 0.1f)
          ptemp.set((float) Math.random(), (float) Math.random(), (float) Math
              .random());
        draw1.append(Escape.escape(ptemp));
        ptemp.set(0, ang, 0);
        draw1.append(Escape.escape(ptemp)).append(" color red");
        

        draw1.append(drawid).append("rotVector1 vector diameter 0.1 ").append(
            Escape.escape(pa1)).append(Escape.escape(vtemp)).append("color ")
            .append(color);
      }

      if (ismirrorplane) {

        

        if (pt00.distance(pt0) > 0.2)
          draw1.append(drawid).append("planeVector arrow ").append(
              Escape.escape(pt00)).append(Escape.escape(pt0)).append(
              " color indigo");

        

        if (trans != null) {
          ptemp.scaleAdd(-1, p0, p1);
          ptemp.add(pt0);
          drawLine(draw1, drawid + "planeFrameX", 0.15f, pt0, ptemp,
              "translucent red");
          ptemp.scaleAdd(-1, p0, p2);
          ptemp.add(pt0);
          drawLine(draw1, drawid + "planeFrameY", 0.15f, pt0, ptemp,
              "translucent green");
          ptemp.scaleAdd(-1, p0, p3);
          ptemp.add(pt0);
          drawLine(draw1, drawid + "planeFrameZ", 0.15f, pt0, ptemp,
              "translucent blue");
        }

        color = (trans == null ? "green" : "blue");

        
        
        
        
        

        Point3f[] vertices = new Point3f[8];
        TriangleServer ts = (TriangleServer) Interface
            .getOptionInterface("jvxl.calc.TriangleData");
        Point3i[] offsets = ts.getCubeVertexOffsets();
        for (int i = 0; i < 8; i++) {
          ptemp.set(offsets[i].x == 0 ? -0.05f : 1.05f,
              offsets[i].y == 0 ? -0.05f : 1.05f, offsets[i].z == 0 ? -0.05f
                  : 1.05f);
          uc.toCartesian(ptemp);
          vertices[i] = new Point3f(ptemp);
        }
        vtemp.set(ax1);
        vtemp.normalize();
        
        
        float w = -vtemp.x * pa1.x - vtemp.y * pa1.y - vtemp.z * pa1.z;
        Point4f plane = new Point4f(vtemp.x, vtemp.y, vtemp.z, w);
        Vector v = ts.intersectPlane(plane, vertices, 3);
        
        if (v != null)
          for (int i = v.size(); --i >= 0;) {
            Point3f[] pts = (Point3f[]) v.get(i);
            draw1.append(drawid).append("planep").append(i).append(
                Escape.escape(pts[0])).append(Escape.escape(pts[1]));
            if (pts.length == 3)
              draw1.append(Escape.escape(pts[2]));
            draw1.append(" color translucent ").append(color);
          }

        

        if (v == null || v.size() == 0) {
          ptemp.set(pa1);
          ptemp.add(ax1);
          draw1.append(drawid).append("planeCircle scale 2.0 circle ").append(
              Escape.escape(pa1)).append(Escape.escape(ptemp)).append(
              " color translucent ").append(color).append(" mesh fill");
        }
      }

      if (haveinversion) {

        

        draw1.append(drawid).append("invPoint diameter 0.4 ").append(
            Escape.escape(ipt));
        draw1.append(drawid).append("invArrow arrow ").append(
            Escape.escape(pt00)).append(Escape.escape(ptinv)).append(
            " color indigo");
        if (!isinversion) {
          ptemp.set(ptinv);
          ptemp.add(pt00);
          ptemp.sub(pt01);
          drawLine(draw1, drawid + "invFrameX", 0.15f, ptinv, ptemp,
              "translucent red");
          ptemp.set(ptinv);
          ptemp.add(pt00);
          ptemp.sub(pt02);
          drawLine(draw1, drawid + "invFrameY", 0.15f, ptinv, ptemp,
              "translucent green");
          ptemp.set(ptinv);
          ptemp.add(pt00);
          ptemp.sub(pt03);
          drawLine(draw1, drawid + "invFrameZ", 0.15f, ptinv, ptemp,
              "translucent blue");
        }
      }

      

      if (trans != null) {
        if (pt0 == null)
          pt0 = new Point3f(pt00);
        draw1.append(drawid).append("transVector vector ").append(
            Escape.escape(pt0)).append(Escape.escape(trans));
      }

      

      draw1.append("\nvar pt00 = " + Escape.escape(pt00));
      draw1.append("\nvar p0 = " + Escape.escape(p0));
      draw1.append("\nif (within(0.2,p0).length == 0) {");
      draw1.append("\nvar set2 = within(0.2,p0.uxyz.xyz)");
      draw1.append("\nif (set2) {");
      draw1.append(drawid)
          .append("cellOffsetVector arrow @p0 @set2 color grey");
      draw1.append(drawid).append(
          "offsetFrameX diameter 0.20 @{set2.xyz} @{set2.xyz + ").append(
          Escape.escape(v01)).append("*0.9} color red");
      draw1.append(drawid).append(
          "offsetFrameY diameter 0.20 @{set2.xyz} @{set2.xyz + ").append(
          Escape.escape(v02)).append("*0.9} color green");
      draw1.append(drawid).append(
          "offsetFrameZ diameter 0.20 @{set2.xyz} @{set2.xyz + ").append(
          Escape.escape(v03)).append("*0.9} color purple");
      draw1.append("\n}}\n");

      cmds = draw1.toString();
      draw1 = null;
      drawid = null;
    }
    if (trans == null)
      ftrans = null;
    if (isrotation) {
      if (haveinversion) {
      } else if (pitch1 == 0) {
      } else {
        
        trans = new Vector3f(ax1);
        ptemp.set(trans);
        uc.toFractional(ptemp);
        ftrans = new Vector3f(ptemp);
      }
      if (haveinversion && pitch1 != 0) {
      }
    }
    if (ismirrorplane) {
      if (trans != null) {
      }
      ang1 = 0;
    }
    if (haveinversion) {
      if (isinversion) {
        pa1 = null;
        ax1 = null;
        trans = null;
        ftrans = null;
      }
    } else if (istranslation) {
      pa1 = null;
      ax1 = null;
    }
 
    
    if (ax1 != null)
      ax1.normalize();
    return new Object[] { 
        xyz, xyzOriginal, info1, 
        cmds, approx(ftrans), approx(trans), 
        approx(ipt), approx(pa1), approx(ax1), 
        new Integer(ang1), m };
  }

  private static void drawLine(StringBuffer s, String id, float diameter, Point3f pt0, Point3f pt1,
                        String color) {
    s.append(id).append(" diameter ").append(diameter)
        .append(Escape.escape(pt0)).append(Escape.escape(pt1))
        .append(" color ").append(color);
  }

  private static String fcoord(Tuple3f p) {
    return fc(p.x) + " " + fc(p.y) + " " + fc(p.z);
  }

  private static String fc(float x) {
    float xabs = Math.abs(x);
    int x24 = (int) approx(xabs * 24);
    String m = (x < 0 ? "-" : "");
    if (x24%8 != 0)
      return m + twelfthsOf(x24 >> 1);
    return (x24 == 0 ? "0" : x24 == 24 ? m + "1" : m + (x24/8) + "/3");
  }

  private static Tuple3f approx(Tuple3f pt) {
    if (pt != null) {
      if (Math.abs(pt.x) < 0.0001f)
        pt.x = 0;
      if (Math.abs(pt.y) < 0.0001f)
        pt.y = 0;
      if (Math.abs(pt.z) < 0.0001f)
        pt.z = 0;
    }
    return pt;
  }
  
  private static float approx(float f) {
    return approx(f, 100);
  }

  private static float approx(float f, float n) {
    return ((int) (f * n + 0.5f * (f < 0 ? -1 : 1)) / n);
  }


}
