

package org.jmol.shapespecial;

import org.jmol.g3d.Graphics3D;
import org.jmol.geodesic.EnvelopeCalculation;
import org.jmol.geodesic.Geodesic;
import org.jmol.shape.DotsRenderer;

import javax.vecmath.Point3i;



public class GeoSurfaceRenderer extends DotsRenderer {
  
  protected void render() {
    GeoSurface gs = (GeoSurface) shape;
    iShowSolid = !(viewer.getInMotion() && gs.ec.getDotsConvexMax() > 100);
    if (!iShowSolid && !g3d.setColix(Graphics3D.BLACK))
      return;
    if (iShowSolid && faceMap == null)
      faceMap = new int[screenDotCount];
    render1(gs);
  }
  
 protected void renderConvex(short colix, int[] visibilityMap, int nPoints) {
    this.colix = colix;
    if (iShowSolid) {
      if (g3d.setColix(colix))       
        renderSurface(visibilityMap);
      return;
    }
    renderDots(nPoints);
  }
  
  private Point3i facePt1 = new Point3i();
  private Point3i facePt2 = new Point3i();
  private Point3i facePt3 = new Point3i();
  
  private void renderSurface(int[] points) {
    if (faceMap == null)
      return;
    short[] faces = Geodesic.getFaceVertexes(screenLevel);
    int[] coords = screenCoordinates;
    short p1, p2, p3;
    int mapMax = (points.length << 5);
    
    if (screenDotCount < mapMax)
      mapMax = screenDotCount;
    for (int f = 0; f < faces.length;) {
      p1 = faces[f++];
      p2 = faces[f++];
      p3 = faces[f++];
      if (p1 >= mapMax || p2 >= mapMax || p3 >= mapMax)
        continue;
      
      if (!EnvelopeCalculation.getBit(points, p1) || !EnvelopeCalculation.getBit(points, p2)
          || !EnvelopeCalculation.getBit(points, p3))
        continue;
      facePt1.set(coords[faceMap[p1]], coords[faceMap[p1] + 1], coords[faceMap[p1] + 2]);
      facePt2.set(coords[faceMap[p2]], coords[faceMap[p2] + 1], coords[faceMap[p2] + 2]);
      facePt3.set(coords[faceMap[p3]], coords[faceMap[p3] + 1], coords[faceMap[p3] + 2]);
      g3d.setNoisySurfaceShade(facePt1, facePt2, facePt3);
      g3d.fillTriangle(facePt1, colix, p1, facePt2, colix, p2, facePt3, colix, p3);
    }
  }  
}

