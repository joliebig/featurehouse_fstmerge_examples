
package org.jmol.shape;

import javax.vecmath.Point3f;
import java.text.NumberFormat;

import org.jmol.api.SymmetryInterface;
import org.jmol.modelset.BoxInfo;
import org.jmol.util.TextFormat;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.StateManager;

public class UccageRenderer extends CageRenderer {

  NumberFormat nf;
  byte fid;
  boolean doLocalize;
  
  protected void setEdges() {
    tickEdges = BoxInfo.uccageTickEdges;    
  }

  final Point3f[] verticesT = new Point3f[8];  
  {
    for (int i = 8; --i >= 0; ) {
      verticesT[i] = new Point3f();
    }
  }

  protected void initRenderer() {
    super.initRenderer();
    draw000 = false;
  }
  
  protected void render() {
    imageFontScaling = viewer.getImageFontScaling();
    font3d = g3d.getFont3DScaled(((Uccage)shape).font3d, imageFontScaling);
    int mad = viewer.getObjectMad(StateManager.OBJ_UNITCELL);
    colix = viewer.getObjectColix(StateManager.OBJ_UNITCELL);
    if (mad == 0 || !g3d.setColix(colix) || viewer.isJmolDataFrame())
      return;
    doLocalize = viewer.getUseNumberLocalization();
    render1(mad);
  }

  void render1(int mad) {
    SymmetryInterface[] cellInfos = modelSet.getCellInfos();
    if (cellInfos == null)
      return;
    SymmetryInterface symmetry = viewer.getCurrentUnitCell();
    if (symmetry == null)
      return;
    Point3f[] vertices = symmetry.getUnitCellVertices();
    Point3f offset = symmetry.getCartesianOffset();
    for (int i = 8; --i >= 0;)
      verticesT[i].add(vertices[i], offset);
    Point3f[] axisPoints = viewer.getAxisPoints();
    boolean drawAllLines = (viewer.getObjectMad(StateManager.OBJ_AXIS1) == 0 
        || viewer.getAxesScale() < 2 || axisPoints == null);
    
    render(mad, verticesT, axisPoints, drawAllLines ? 0 : 3);
    if (viewer.getDisplayCellParameters() && !viewer.isPreviewOnly() && !symmetry.isPeriodic())
      renderInfo(symmetry);
  }
  
  private String nfformat(float x) {
    return (doLocalize && nf != null ? nf.format(x) : TextFormat.formatDecimal(x, 3));
  }

  private void renderInfo(SymmetryInterface symmetry) {
    if (isGenerator || !g3d.setColix(viewer.getColixBackgroundContrast()))
      return;
    if (nf == null) {
      nf = NumberFormat.getInstance();
    }
    
    fid = g3d.getFontFid("Monospaced", 14 * imageFontScaling);

    if (nf != null) {
      nf.setMaximumFractionDigits(3);
      nf.setMinimumFractionDigits(3);
    }
    g3d.setFont(fid);
    
    int lineheight = (int) (15 * imageFontScaling);
    int x = (int) (5 * imageFontScaling);
    int y = lineheight;
    
    String spaceGroup = symmetry.getSpaceGroupName(); 
    if (spaceGroup != null & !spaceGroup.equals("-- [--]")) {
      y += lineheight;
      g3d.drawStringNoSlab(spaceGroup, null, x, y, 0);
    }
    y += lineheight;
    g3d.drawStringNoSlab("a=" + nfformat(symmetry.getUnitCellInfo(JmolConstants.INFO_A))
        + "\u", null, x, y, 0);
    y += lineheight;
    g3d.drawStringNoSlab("b=" + nfformat(symmetry.getUnitCellInfo(JmolConstants.INFO_B))
        + "\u", null, x, y, 0);
    y += lineheight;
    g3d.drawStringNoSlab("c=" + nfformat(symmetry.getUnitCellInfo(JmolConstants.INFO_C))
        + "\u", null, x, y, 0);
    if (nf != null)
      nf.setMaximumFractionDigits(1);
    y += lineheight;
    g3d.drawStringNoSlab("\u="
        + nfformat(symmetry.getUnitCellInfo(JmolConstants.INFO_ALPHA)) + "\u", null,
        x, y, 0);
    y += lineheight;
    g3d.drawStringNoSlab("\u="
        + nfformat(symmetry.getUnitCellInfo(JmolConstants.INFO_BETA)) + "\u", null,
        x, y, 0);
    y += lineheight;
    g3d.drawStringNoSlab("\u="
        + nfformat(symmetry.getUnitCellInfo(JmolConstants.INFO_GAMMA)) + "\u", null,
        x, y, 0);
  }

}

