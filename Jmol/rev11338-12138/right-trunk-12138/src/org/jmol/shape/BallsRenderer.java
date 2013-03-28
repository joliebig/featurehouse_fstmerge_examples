

package org.jmol.shape;

import java.util.BitSet;

import org.jmol.g3d.*;
import org.jmol.modelset.Atom;
import org.jmol.modelset.Molecule;
import org.jmol.viewer.JmolConstants;

import javax.vecmath.*;
public class BallsRenderer extends ShapeRenderer {

  private int minX, minY, minZ, maxX, maxY, maxZ;
  
  protected void render() {
    
    
    
    
    boolean renderBalls = !viewer.getWireframeRotation()
        || !viewer.getInMotion();
    slabbing = viewer.getSlabEnabled();
    
    boolean renderCrosshairs = modelSet.getAtomCount() > 0
        && viewer.getShowNavigationPoint() && !isExport
        && g3d.setColix(Graphics3D.BLACK);

    Point3f navOffset = (renderCrosshairs ? new Point3f(viewer
        .getNavigationOffset()) : null);

    if (slabbing) {
      minZ = g3d.getSlab();
      maxZ = g3d.getDepth();
    }
    if (renderCrosshairs) {
      minX = Integer.MAX_VALUE;
      maxX = Integer.MIN_VALUE;
      minY = Integer.MAX_VALUE;
      maxY = Integer.MIN_VALUE;
    }

    Atom[] atoms = modelSet.atoms;
    int atomCount = modelSet.getAtomCount();
    BitSet bsOK = new BitSet();
    for (int i = atomCount; --i >= 0;) {
      Atom atom = atoms[i];
      if ((atom.getShapeVisibilityFlags() & JmolConstants.ATOM_IN_FRAME) == 0)
        continue;
      bsOK.set(i);
      atom.transform(viewer);
    }
    boolean slabByMolecule = viewer.getSlabByMolecule();
    if (slabByMolecule && slabbing) {
      Molecule[] molecules = modelSet.getMolecules();
      int moleculeCount = modelSet.getMoleculeCountInModel(-1);
      for (int i = 0; i < moleculeCount; i++) {
        Molecule m = molecules[i];
        int j = 0;
        int pt = m.firstAtomIndex;
        if (!bsOK.get(pt))
          continue;
        for (; j < m.nAtoms; j++, pt++)
          if (g3d.isClippedZ(atoms[pt].screenZ - (atoms[pt].screenDiameter >> 1)))
            break;
        if (j != m.nAtoms) {
          pt = m.firstAtomIndex;
          for (int k = 0; k < m.nAtoms; k++) {
            bsOK.clear(pt);
            atoms[pt++].screenZ = 0;  
          }
        }
      }
    }      
    for (int i = atomCount; --i >= 0;)
      if (bsOK.get(i)) {
        Atom atom = atoms[i];
        if (slabbing) {

          if (g3d.isClippedZ(atom.screenZ)) {

            atom.setClickable(0);
            
            

            int r = atom.screenDiameter / 2;
            if (atom.screenZ + r < minZ || atom.screenZ - r > maxZ)
              continue;
            if (!g3d.isInDisplayRange(atom.screenX, atom.screenY))
              continue;
          }
        }
        

        if (renderBalls && atom.screenDiameter > 0
            && (atom.getShapeVisibilityFlags() & myVisibilityFlag) != 0
            && g3d.setColix(atom.getColix())) {
          if (renderCrosshairs) {
            if (atom.screenX < minX)
              minX = atom.screenX;
            if (atom.screenX > maxX)
              maxX = atom.screenX;
            if (atom.screenY < minY)
              minY = atom.screenY;
            if (atom.screenY > maxY)
              maxY = atom.screenY;
          }
          g3d.drawAtom(atom);
        }
      }

    
    if (renderCrosshairs) {
      boolean antialiased = g3d.isAntialiased();
      float navDepth = viewer.getNavigationDepthPercent();
      g3d.setColix(navDepth < 0 ? Graphics3D.RED
          : navDepth > 100 ? Graphics3D.GREEN : Graphics3D.GOLD);
      int x = Math.max(Math.min(viewer.getScreenWidth(), (int) navOffset.x), 0);
      int y = Math
          .max(Math.min(viewer.getScreenHeight(), (int) navOffset.y), 0);
      int z = (int) navOffset.z + 1;
      
      int off = (antialiased ? 8 : 4);
      int h = (antialiased ? 20 : 10);
      int w = (antialiased ? 2 : 1);
      g3d.drawRect(x - off, y, z, 0, h, w);
      g3d.drawRect(x, y - off, z, 0, w, h);
      g3d.drawRect(x - off, y - off, z, 0, h, h);
      off = h;
      h = h >> 1;
      g3d.setColix(maxX < navOffset.x ? Graphics3D.YELLOW : Graphics3D.GREEN);
      g3d.drawRect(x - off, y, z, 0, h, w);
      g3d.setColix(minX > navOffset.x ? Graphics3D.YELLOW : Graphics3D.GREEN);
      g3d.drawRect(x + h, y, z, 0, h, w);
      g3d.setColix(maxY < navOffset.y ? Graphics3D.YELLOW : Graphics3D.GREEN);
      g3d.drawRect(x, y - off, z, 0, w, h);
      g3d.setColix(minY > navOffset.y ? Graphics3D.YELLOW : Graphics3D.GREEN);
      g3d.drawRect(x, y + h, z, 0, w, h);
    }
  }
}
