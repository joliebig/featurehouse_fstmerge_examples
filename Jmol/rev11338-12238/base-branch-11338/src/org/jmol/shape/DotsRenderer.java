

package org.jmol.shape;

import org.jmol.g3d.Graphics3D;
import org.jmol.geodesic.EnvelopeCalculation;
import org.jmol.geodesic.Geodesic;
import org.jmol.modelset.Atom;

import javax.vecmath.Vector3f;


public class DotsRenderer extends ShapeRenderer {

  public boolean iShowSolid;
  
  Vector3f[] verticesTransformed;
  public int screenLevel;
  public int screenDotCount;
  public int[] screenCoordinates;
  public int[] faceMap = null; 
  
  protected void initRenderer() {
    screenLevel = Dots.MAX_LEVEL;
    screenDotCount = Geodesic.getVertexCount(Dots.MAX_LEVEL);
    verticesTransformed = new Vector3f[screenDotCount];
    for (int i = screenDotCount; --i >= 0; )
      verticesTransformed[i] = new Vector3f();
    screenCoordinates = new int[3 * screenDotCount];
  }

  protected void render() {
    Dots dots = (Dots) shape;
    render1(dots);
  }

  protected void render1(Dots dots) {
    
    if (!iShowSolid && !g3d.setColix(Graphics3D.BLACK)) 
      return;
    int sppa = (int) viewer.getScalePixelsPerAngstrom(true);
    screenLevel = (iShowSolid || sppa > 20 ? 3 : sppa > 10 ? 2 : sppa > 5 ? 1
        : 0);
    if (!iShowSolid) {
      screenLevel += viewer.getDotDensity() - 3;
      screenLevel = Math.max(Math.min(screenLevel, 3), 0);
    }
    screenDotCount = Geodesic.getVertexCount(screenLevel);
    for (int i = screenDotCount; --i >= 0;)
      viewer.transformVector(Geodesic.getVertexVector(i),
          verticesTransformed[i]);
    int[][] maps = dots.ec.getDotsConvexMaps();
    for (int i = dots.ec.getDotsConvexMax(); --i >= 0;) {
      Atom atom = modelSet.atoms[i];
      int[] map = maps[i];
      if (map == null || !atom.isVisible(myVisibilityFlag)
          || !g3d.isInDisplayRange(atom.screenX, atom.screenY))
        continue;
      int nPoints = calcScreenPoints(map, dots.ec.getAppropriateRadius(i),
          atom.screenX, atom.screenY, atom.screenZ);
      if (nPoints != 0)
        renderConvex(Graphics3D.getColixInherited(dots.colixes[i],
            atom.getColix()), map, nPoints);
    }
    
    
  }
  
  
  private int calcScreenPoints(int[] visibilityMap, float radius, int x, int y, int z) {
    int nPoints = 0;
    int i = 0;
    float scaledRadius = viewer.scaleToPerspective(z, radius);
    int iDot = Math.min(visibilityMap.length << 5, screenDotCount);
    while (--iDot >= 0) {
      if (!EnvelopeCalculation.getBit(visibilityMap, iDot))
        continue;
      Vector3f vertex = verticesTransformed[iDot];
      if (faceMap != null)
        faceMap[iDot] = i;
      screenCoordinates[i++] = x
          + (int) ((scaledRadius * vertex.x) + (vertex.x < 0 ? -0.5 : 0.5));
      screenCoordinates[i++] = y
          + (int) ((scaledRadius * vertex.y) + (vertex.y < 0 ? -0.5 : 0.5));
      screenCoordinates[i++] = z
          + (int) ((scaledRadius * vertex.z) + (vertex.z < 0 ? -0.5 : 0.5));
      ++nPoints;
    }
    return nPoints;
  }

  
  protected void renderConvex(short colix, int[] map, int nPoints) {
    this.colix = Graphics3D.getColixTranslucent(colix, false, 0);
    renderDots(nPoints);
  }

  
  protected void renderDots(int nPoints) {
    g3d.setColix(colix);
    g3d.drawPoints(nPoints, screenCoordinates);
  }
}

