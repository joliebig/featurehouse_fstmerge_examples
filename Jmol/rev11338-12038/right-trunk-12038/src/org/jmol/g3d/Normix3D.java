

package org.jmol.g3d;

import org.jmol.geodesic.Geodesic;
import org.jmol.util.BitSetUtil;

import javax.vecmath.Vector3f;
import javax.vecmath.Matrix3f;
import java.util.Random;
import java.util.BitSet;
import org.jmol.util.Logger;


class Normix3D {

  final static int NORMIX_GEODESIC_LEVEL = Geodesic.standardLevel;

  private final static int normixCount = Geodesic.getVertexCount(NORMIX_GEODESIC_LEVEL);
  private final static Vector3f[] vertexVectors = Geodesic.getVertexVectors(); 
  private final static short[][] neighborVertexesArrays = Geodesic.getNeighborVertexesArrays();

  private final Vector3f[] transformedVectors;
  private final byte[] shadeIndexes;
  private final byte[] shadeIndexes2Sided;

  
  private final static boolean TIMINGS = false;
  
  

  private final Matrix3f rotationMatrix = new Matrix3f();

  Normix3D() {
    
    
    shadeIndexes = new byte[normixCount];
    shadeIndexes2Sided = new byte[normixCount];
    transformedVectors = new Vector3f[normixCount];
    for (int i = normixCount; --i >= 0; )
      transformedVectors[i] = new Vector3f();

    if (TIMINGS) {
      Logger.debug("begin timings!");
      for (int i = 0; i < normixCount; ++i) {
        short normix = getNormix(vertexVectors[i]);
        System.out.println("draw normix" + i + " {" + vertexVectors[i].x + " " + vertexVectors[i].y + " " + vertexVectors[i].z + "} {0 0 0} \""+i+"\"");
        if (normix != i)
          if (Logger.debugging) {
            Logger.debug("" + i + " -> " + normix);
          }
      }
      Random rand = new Random();
      Vector3f vFoo = new Vector3f();
      Vector3f vBar = new Vector3f();
      Vector3f vSum = new Vector3f();
      
      int runCount = 100000;
      short[] neighborVertexes = neighborVertexesArrays[NORMIX_GEODESIC_LEVEL];
      if (Logger.debugging)
        Logger.startTimer();
      for (int i = 0; i < runCount; ++i) {
        short foo = (short)(rand.nextDouble() * normixCount);
        int offsetNeighbor;
        short bar;
        do {
          offsetNeighbor = foo * 6 + (int)(rand.nextDouble() * 6);
          bar = neighborVertexes[offsetNeighbor];
        } while (bar == -1);
        vFoo.set(vertexVectors[foo]);
        vFoo.scale(rand.nextFloat());
        vBar.set(vertexVectors[bar]);
        vBar.scale(rand.nextFloat());
        vSum.add(vFoo, vBar);
        vSum.normalize();
      }
      if (Logger.debugging) {
        Logger.checkTimer("base runtime for " + runCount);
        Logger.startTimer();
      }
      for (int i = 0; i < runCount; ++i) {
        short foo = (short)(rand.nextDouble() * normixCount);
        int offsetNeighbor;
        short bar;
        do {
          offsetNeighbor = foo * 6 + (int)(rand.nextDouble() * 6);
          bar = neighborVertexes[offsetNeighbor];
        } while (bar == -1);
        vFoo.set(vertexVectors[foo]);
        vFoo.scale(rand.nextFloat());
        vBar.set(vertexVectors[bar]);
        vBar.scale(rand.nextFloat());
        vSum.add(vFoo, vBar);
        short sum = getNormix(vSum);
        if (sum != foo && sum != bar) {

          throw new NullPointerException();
        }
        short sum2 = getNormix(vSum);
        if (sum != sum2) {
          Logger.debug("normalized not the same answer?");
          throw new NullPointerException();
        }
      }
      if (Logger.debugging)
        Logger.checkTimer("normix2 runtime for " + runCount);
    }
  }

  Vector3f[] getTransformedVectors() {
    return transformedVectors;
  }

  boolean isDirectedTowardsCamera(short normix) {
    
    return (normix < 0) || (transformedVectors[normix].z > 0);
  }

  short getNormix(Vector3f v) {
    return getNormix(v.x, v.y, v.z, NORMIX_GEODESIC_LEVEL);
  }

  Vector3f getVector(short normix) {
    return vertexVectors[normix];
  }
  

  private final BitSet bsConsidered = new BitSet();

  short getNormix(double x, double y, double z, int geodesicLevel) {
    short champion;
    double t;
    if (z >= 0) {
      champion = 0;
      t = z - 1;
    } else {
      champion = 11;
      t = z - (-1);
    }
    BitSetUtil.clear(bsConsidered);
    bsConsidered.set(champion);
    double championDist2 = x*x + y*y + t*t;
    for (int lvl = 0; lvl <= geodesicLevel; ++lvl) {
      short[] neighborVertexes = neighborVertexesArrays[lvl];
      for (int offsetNeighbors = 6 * champion,
             i = offsetNeighbors + (champion < 12 ? 5 : 6);
           --i >= offsetNeighbors; ) {
        short challenger = neighborVertexes[i];
        if (bsConsidered.get(challenger))
            continue;
        bsConsidered.set(challenger);
        
        Vector3f v = vertexVectors[challenger];
        double d;
        
        
        d = v.x - x;
        double d2 = d * d;
        if (d2 >= championDist2)
          continue;
        d = v.y - y;
        d2 += d * d;
        if (d2 >= championDist2)
          continue;
        d = v.z - z;
        d2 += d * d;
        if (d2 >= championDist2)
          continue;
        champion = challenger;
        championDist2 = d2;
      }
    }


    return champion;
  }

  short[] inverseNormixes;

  void calculateInverseNormixes() {
    inverseNormixes = new short[normixCount];
    for (int n = normixCount; --n >= 0; ) {
      Vector3f v = vertexVectors[n];
      inverseNormixes[n] = getNormix(-v.x, -v.y, -v.z, NORMIX_GEODESIC_LEVEL);
      }
    
    
    
    
  }

  private static byte nullShadeIndex = 50;
  
  int getShadeIndex(short normix) {
    return (normix == ~Graphics3D.NORMIX_NULL
        || normix == Graphics3D.NORMIX_NULL ? nullShadeIndex
        : normix < 0 ? shadeIndexes2Sided[~normix] : shadeIndexes[normix]);
  }

  void setRotationMatrix(Matrix3f rotationMatrix) {
    this.rotationMatrix.set(rotationMatrix);
    for (int i = normixCount; --i >= 0; ) {
      Vector3f tv = transformedVectors[i];
      rotationMatrix.transform(vertexVectors[i], tv);
      float x = tv.x;
      float y = -tv.y;
      float z = tv.z;
      
      int shadeIndex = Shade3D.getShadeIndexNormalized(x, y, z);
      shadeIndexes[i] = (byte) shadeIndex;
      shadeIndexes2Sided[i] = (byte) (z >= 0 ? shadeIndex 
          : Shade3D.getShadeIndexNormalized(-x, -y, -z));
    }
  }

  
}
