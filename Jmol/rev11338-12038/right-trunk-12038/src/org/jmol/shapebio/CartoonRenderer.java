

package org.jmol.shapebio;

import org.jmol.g3d.Graphics3D;
import org.jmol.modelsetbio.NucleicMonomer;
import org.jmol.modelsetbio.ProteinStructure;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;

public class CartoonRenderer extends RocketsRenderer {

  private boolean newRockets = true;
  private boolean renderAsRockets;
  
  protected void renderBioShape(BioShape bioShape) {
    if (bioShape.wingVectors == null || isCarbohydrate)
      return;
    calcScreenControlPoints();
    if (isNucleic) {
      renderNucleic();
      return;
    }
    boolean val = viewer.getCartoonRocketFlag();
    if (renderAsRockets != val) {
      for (int i = 0; i < monomerCount; i++)
        bioShape.falsifyMesh(i, false);
      renderAsRockets = val;
    }
    val = viewer.getRocketBarrelFlag();
    if (renderAsBarrels != val) {
      for (int i = 0; i < monomerCount; i++)
        bioShape.falsifyMesh(i, false);
      renderAsBarrels = val;
    }
    ribbonTopScreens = calcScreens(0.5f);
    ribbonBottomScreens = calcScreens(-0.5f);
    calcRopeMidPoints(newRockets);
    if (renderAsBarrels) {
      calcScreenControlPoints(cordMidPoints);
      controlPoints = cordMidPoints;
    }
    render1();
    viewer.freeTempPoints(cordMidPoints);
    viewer.freeTempScreens(ribbonTopScreens);
    viewer.freeTempScreens(ribbonBottomScreens);
  }

  Point3i ptConnect = new Point3i();
  void renderNucleic() {
    boolean isTraceAlpha = viewer.getTraceAlpha();
      for (int i = monomerCount; --i >= 0;)
        if (bsVisible.get(i)) {
          if (isTraceAlpha) {
            ptConnect.set((controlPointScreens[i].x + controlPointScreens[i + 1].x)/2,
                (controlPointScreens[i].y + controlPointScreens[i + 1].y)/2,
                (controlPointScreens[i].z + controlPointScreens[i + 1].z)/2);
          } else {
            ptConnect.set(controlPointScreens[i + 1]);
          }
          renderHermiteConic(i, false);
          colix = getLeadColix(i);
          if (g3d.setColix(colix))
            renderNucleicBaseStep((NucleicMonomer) monomers[i], mads[i],
                ptConnect);
        }
  }

  protected void render1() {
    boolean lastWasSheet = false;
    boolean lastWasHelix = false;
    ProteinStructure previousStructure = null;
    ProteinStructure thisStructure;

    
    

    
    

    for (int i = monomerCount; --i >= 0;) {
      
      thisStructure = monomers[i].getProteinStructure();
      if (thisStructure != previousStructure) {
        lastWasHelix = false;
        lastWasSheet = false;
      }
      previousStructure = thisStructure;
      boolean isHelix = isHelix(i);
      boolean isSheet = isSheet(i);
      boolean isHelixRocket = (renderAsRockets || renderAsBarrels ? isHelix : false);
      if (bsVisible.get(i)) {
        if (isHelixRocket) {
          
        } else if (isSheet || isHelix) {
          if (lastWasSheet && isSheet || lastWasHelix && isHelix)
            
            renderHermiteRibbon(true, i, true);
          else
            renderHermiteArrowHead(i);
        } else if (i != monomerCount - 1) {
          renderHermiteConic(i, true);
        }
      }
      lastWasSheet = isSheet;
      lastWasHelix = isHelix;
    }

    if (renderAsRockets || renderAsBarrels)
      renderRockets();
  }

  private void renderRockets() {
    

    
    

    
    
    tPending = false;
    for (int i = 0; i < monomerCount; ++i)
      if (bsVisible.get(i) && isHelix(i)) {
        renderSpecialSegment(monomers[i], getLeadColix(i), mads[i]);
      }
    renderPending();
  }
  
  
  
  private final Point3f[] ring6Points = new Point3f[6];
  private final Point3i[] ring6Screens = new Point3i[6];
  private final Point3f[] ring5Points = new Point3f[5];
  private final Point3i[] ring5Screens = new Point3i[5];

  {
    ring6Screens[5] = new Point3i();
    for (int i = 5; --i >= 0; ) {
      ring5Screens[i] = new Point3i();
      ring6Screens[i] = new Point3i();
    }
  }

  private void renderNucleicBaseStep(NucleicMonomer nucleotide,
                             short thisMad, Point3i backboneScreen) {
    nucleotide.getBaseRing6Points(ring6Points);
    viewer.transformPoints(ring6Points, ring6Screens);
    renderRing6();
    boolean hasRing5 = nucleotide.maybeGetBaseRing5Points(ring5Points);
    Point3i stepScreen;
    if (hasRing5) {
      viewer.transformPoints(ring5Points, ring5Screens);
      renderRing5();
      stepScreen = ring5Screens[3];
    } else {
      stepScreen = ring6Screens[2];
    }
    mad = (short) (thisMad > 1 ? thisMad / 2 : thisMad);
    g3d.fillCylinder(Graphics3D.ENDCAPS_SPHERICAL,
                     viewer.scaleToScreen(backboneScreen.z,
                                          mad),
                     backboneScreen, stepScreen);
    --ring6Screens[5].z;
    for (int i = 5; --i > 0; ) {
      --ring6Screens[i].z;
      if (hasRing5)
        --ring5Screens[i].z;
    }
    for (int i = 6; --i > 0; )
      g3d.fillCylinder(Graphics3D.ENDCAPS_SPHERICAL, 3,
                       ring6Screens[i], ring6Screens[i - 1]);
    if (hasRing5) {
      for (int i = 5; --i > 0; )
        g3d.fillCylinder(Graphics3D.ENDCAPS_SPHERICAL, 3,
                         ring5Screens[i], ring5Screens[i - 1]);
    } else {
      g3d.fillCylinder(Graphics3D.ENDCAPS_SPHERICAL, 3,
                       ring6Screens[5], ring6Screens[0]);
    }
  }

  private void renderRing6() {
    g3d.setNoisySurfaceShade(ring6Screens[0], ring6Screens[2], ring6Screens[4]);
    g3d.fillTriangle(ring6Screens[0], ring6Screens[2], ring6Screens[4]);
    g3d.fillTriangle(ring6Screens[0], ring6Screens[1], ring6Screens[2]);
    g3d.fillTriangle(ring6Screens[0], ring6Screens[4], ring6Screens[5]);
    g3d.fillTriangle(ring6Screens[2], ring6Screens[3], ring6Screens[4]);
  }

  private void renderRing5() {
    
    g3d.fillTriangle(ring5Screens[0], ring5Screens[2], ring5Screens[3]);
    g3d.fillTriangle(ring5Screens[0], ring5Screens[1], ring5Screens[2]);
    g3d.fillTriangle(ring5Screens[0], ring5Screens[3], ring5Screens[4]);
  }  
  
}
