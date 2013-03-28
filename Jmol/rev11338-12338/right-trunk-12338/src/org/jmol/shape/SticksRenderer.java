

package org.jmol.shape;

import org.jmol.g3d.*;
import org.jmol.modelset.Atom;
import org.jmol.modelset.Bond;
import org.jmol.viewer.JmolConstants;

public class SticksRenderer extends ShapeRenderer {

  protected boolean showMultipleBonds;
  protected byte modeMultipleBond;
  
  protected byte endcaps;

  protected boolean ssbondsBackbone;
  protected boolean hbondsBackbone;
  protected boolean bondsBackbone;
  protected boolean hbondsSolid;
  
  protected Atom atomA, atomB;
  protected Bond bond;
  int xA, yA, zA;
  int xB, yB, zB;
  int dx, dy;
  int mag2d;
  protected short colixA, colixB;
  protected int width;
  protected int bondOrder;
  private boolean renderWireframe;
  private boolean isAntialiased;
  private boolean slabbing;
  private boolean slabByAtom;


  protected void render() {
    slabbing = viewer.getSlabEnabled();
    slabByAtom = viewer.getSlabByAtom();          
    endcaps = Graphics3D.ENDCAPS_SPHERICAL;
    showMultipleBonds = viewer.getShowMultipleBonds();
    modeMultipleBond = viewer.getModeMultipleBond();
    renderWireframe = viewer.getInMotion() && viewer.getWireframeRotation();
    ssbondsBackbone = viewer.getSsbondsBackbone();
    hbondsBackbone = viewer.getHbondsBackbone();
    bondsBackbone = hbondsBackbone | ssbondsBackbone;
    hbondsSolid = viewer.getHbondsSolid();
    isAntialiased = g3d.isAntialiased();
    Bond[] bonds = modelSet.getBonds();
    for (int i = modelSet.getBondCount(); --i >= 0; ) {
      bond = bonds[i];
      if ((bond.getShapeVisibilityFlags() & myVisibilityFlag) != 0) 
        renderBond();
    }
  }

  protected void renderBond() {
    mad = bond.getMad();
    atomA = bond.getAtom1();
    atomB = bond.getAtom2();
    if (!atomA.isInFrame() || !atomB.isInFrame()
        || !g3d.isInDisplayRange(atomA.screenX, atomA.screenY)
        || !g3d.isInDisplayRange(atomB.screenX, atomB.screenY)
        || modelSet.isAtomHidden(atomA.getIndex())
        || modelSet.isAtomHidden(atomB.getIndex()))
      return;

    if (slabbing) {
      if (g3d.isClippedZ(atomA.screenZ) && g3d.isClippedZ(atomB.screenZ))
        return;
      if(slabByAtom && 
          (g3d.isClippedZ(atomA.screenZ) || g3d.isClippedZ(atomB.screenZ)))
        return;          
    }
    colixA = atomA.getColix();
    colixB = atomB.getColix();
    if (((colix = bond.getColix()) & Graphics3D.OPAQUE_MASK) == Graphics3D.USE_PALETTE) {
      colix = (short) (colix & ~Graphics3D.OPAQUE_MASK);
      colixA = Graphics3D.getColixInherited((short) (colix | viewer
          .getColixAtomPalette(atomA, JmolConstants.PALETTE_CPK)), colixA);
      colixB = Graphics3D.getColixInherited((short) (colix | viewer
          .getColixAtomPalette(atomB, JmolConstants.PALETTE_CPK)), colixB);
    } else {
      colixA = Graphics3D.getColixInherited(colix, colixA);
      colixB = Graphics3D.getColixInherited(colix, colixB);
    }
    int order = bond.getOrder() & ~JmolConstants.BOND_NEW;
    if (bondsBackbone) {
      if (ssbondsBackbone && (order & JmolConstants.BOND_SULFUR_MASK) != 0) {
        
        
        

        atomA = atomA.getGroup().getLeadAtom(atomA);
        atomB = atomB.getGroup().getLeadAtom(atomB);
      } else if (hbondsBackbone
          && (order & JmolConstants.BOND_HYDROGEN_MASK) != 0) {
        atomA = atomA.getGroup().getLeadAtom(atomA);
        atomB = atomB.getGroup().getLeadAtom(atomB);
      }
    }
    xA = atomA.screenX;
    yA = atomA.screenY;
    zA = atomA.screenZ;
    xB = atomB.screenX;
    yB = atomB.screenY;
    zB = atomB.screenZ;
    if (zA == 1 || zB == 1)
      return;
    dx = xB - xA;
    dy = yB - yA;
    width = viewer.scaleToScreen((zA + zB) / 2, mad);
    if (renderWireframe && width > 0)
      width = 1;
    bondOrder = getRenderBondOrder(order);
    switch (bondOrder) {
    case 1:
    case 2:
    case 3:
    case 4:
      drawBond(0);
      break;
    case JmolConstants.BOND_ORDER_UNSPECIFIED:
    case JmolConstants.BOND_AROMATIC_SINGLE:
      bondOrder = 1;
      drawBond(order == JmolConstants.BOND_AROMATIC_SINGLE ? 0 : 1);
      break;
    case JmolConstants.BOND_AROMATIC:
    case JmolConstants.BOND_AROMATIC_DOUBLE:
      bondOrder = 2;
      drawBond(order == JmolConstants.BOND_AROMATIC ? getAromaticDottedBondMask()
          : 0);
      break;
    
    
      
      
    default:
      if ((bondOrder & JmolConstants.BOND_PARTIAL_MASK) != 0) {
        bondOrder = JmolConstants.getPartialBondOrder(order);
        drawBond(JmolConstants.getPartialBondDotted(order));
      } else if ((bondOrder & JmolConstants.BOND_HYDROGEN_MASK) != 0) {
        if (hbondsSolid) {
          bondOrder = 1;
          drawBond(0);
        } else {
          renderHbondDashed();
        }
        break;
      } else if (bondOrder == JmolConstants.BOND_STRUT) {
        bondOrder = 1;
        drawBond(0);
      }
    }
  }
    
  int getRenderBondOrder(int order) {
    order &= ~JmolConstants.BOND_NEW; 
    if ((order & JmolConstants.BOND_PARTIAL_MASK) != 0)
      return order;
    if ((order & JmolConstants.BOND_SULFUR_MASK) != 0)
      order &= ~JmolConstants.BOND_SULFUR_MASK;
    if ((order & JmolConstants.BOND_COVALENT_MASK) != 0) {
      if (!showMultipleBonds ||
          modeMultipleBond == JmolConstants.MULTIBOND_NEVER ||
          (modeMultipleBond == JmolConstants.MULTIBOND_NOTSMALL &&
           mad > JmolConstants.madMultipleBondSmallMaximum)) {
        return 1;
      }
    }
    return order;
  }

  protected boolean lineBond;
  
  protected void drawBond(int dottedMask) {
    if (exportType == Graphics3D.EXPORT_CARTESIAN && bondOrder == 1) {
      
      g3d.drawBond(atomA, atomB, colixA, colixB, endcaps, mad);
      return;
    }
    lineBond = (width <= 1);
    if (lineBond && (isAntialiased || exportType != Graphics3D.EXPORT_NOT)) {
      width = 3;
      lineBond = false;
    }
      
    if (dx == 0 && dy == 0) {
      
      if (! lineBond) {
        int space = width / 8 + 3;
        int step = width + space;
        int y = yA - (bondOrder - 1) * step / 2;
        do {
          fillCylinder(colixA, colixA, endcaps,
                           width, xA, y, zA, xA, y, zA);
          y += step;
        } while (--bondOrder > 0);
      }
      return;
    }
    if (bondOrder == 1) {
      if ((dottedMask & 1) != 0)
        drawDashed(xA, yA, zA, xB, yB, zB);
      else
        fillCylinder(colixA, colixB, endcaps,
                           width, xA, yA, zA, xB, yB, zB);
      return;
    }
    int dxB = dx * dx;
    int dyB = dy * dy;
    int mag2d2 = dxB + dyB;
    
      
    mag2d = (int)(Math.sqrt(mag2d2) + 0.5);
    resetAxisCoordinates();
    while (true) {
      if ((dottedMask & 1) != 0)
        drawDashed(xAxis1, yAxis1, zA, xAxis2, yAxis2, zB);
      else
        fillCylinder(colixA, colixB, endcaps, width,
                           xAxis1, yAxis1, zA, xAxis2, yAxis2, zB);
      dottedMask >>= 1;
      if (--bondOrder <= 0)
        break;
      stepAxisCoordinates();
    }
  }

  int xAxis1, yAxis1, xAxis2, yAxis2, dxStep, dyStep;

  void resetAxisCoordinates() {
    int space = mag2d >> 3;
    int step = width + space;
    dxStep = step * dy / mag2d; dyStep = step * -dx / mag2d;

    xAxis1 = xA; yAxis1 = yA; 
    xAxis2 = xB; yAxis2 = yB; 

    if (bondOrder > 1) {
      int f = (bondOrder - 1);
      xAxis1 -= dxStep * f / 2; yAxis1 -= dyStep * f / 2;
      xAxis2 -= dxStep * f / 2; yAxis2 -= dyStep * f / 2;
    }
  }


  void stepAxisCoordinates() {
    xAxis1 += dxStep; yAxis1 += dyStep;
    xAxis2 += dxStep; yAxis2 += dyStep;
  }

  

  

  
  
  private int getAromaticDottedBondMask() {
    Atom atomC = atomB.findAromaticNeighbor(atomA.getIndex());
    if (atomC == null)
      return 1;
    
    int dxAC = atomC.screenX - xA;
    int dyAC = atomC.screenY - yA;
    return ((dx * dyAC - dy * dxAC) < 0 ? 2 : 1);
  }

  void drawDashed(int xA, int yA, int zA, int xB, int yB, int zB) {
    int dx = xB - xA;
    int dy = yB - yA;
    int dz = zB - zA;
    int i = 2;
    while (i <= 9) {
      int xS = xA + (dx * i) / 12;
      int yS = yA + (dy * i) / 12;
      int zS = zA + (dz * i) / 12;
      i += 3;
      int xE = xA + (dx * i) / 12;
      int yE = yA + (dy * i) / 12;
      int zE = zA + (dz * i) / 12;
      i += 2;
      fillCylinder(colixA, colixB, Graphics3D.ENDCAPS_FLAT, width,
                         xS, yS, zS, xE, yE, zE);
    }
  }

  void renderHbondDashed() {
   int dx = xB - xA;
    int dy = yB - yA;
    int dz = zB - zA;
    int i = 1;
    while (i < 10) {
      int xS = xA + (dx * i) / 10;
      int yS = yA + (dy * i) / 10;
      int zS = zA + (dz * i) / 10;
      short colixS = i < 5 ? colixA : colixB;
      i += 2;
      int xE = xA + (dx * i) / 10;
      int yE = yA + (dy * i) / 10;
      int zE = zA + (dz * i) / 10;
      short colixE = i < 5 ? colixA : colixB;
      ++i;
      fillCylinder(colixS, colixE, Graphics3D.ENDCAPS_FLAT, width,
                         xS, yS, zS, xE, yE, zE);
    }
  }
  
  
  
  protected void fillCylinder(short colixA, short colixB, byte endcaps,
                              int diameter, int xA, int yA, int zA, int xB,
                              int yB, int zB) {
    if (lineBond)
      g3d.drawLine(colixA, colixB, xA, yA, zA, xB, yB, zB);
    else {
      if (exportType != Graphics3D.EXPORT_NOT && mad != 1)
        diameter = mad;
      g3d.fillCylinder(colixA, colixB, endcaps, diameter, xA, yA, zA, xB, yB,
          zB);
    }
  }

}
