

package org.jmol.shapesurface;

import java.util.BitSet;
import javax.vecmath.Vector3f;

import org.jmol.util.Escape;
import org.jmol.util.TextFormat;

public class LcaoCartoon extends Isosurface {

  

  public void initShape() {
    super.initShape();
    myType = "lcaoCartoon";
    allowMesh = false;
  }

  
  private String thisType;
  private int myColorPt;
  private String lcaoID;
  private BitSet thisSet;
  private boolean isMolecular;
  private Vector3f rotationAxis;

  
  private Float lcaoScale;
  private boolean isTranslucent;
  private float translucentLevel;
  private Integer lcaoColorPos;
  private Integer lcaoColorNeg;
  boolean isLonePair;
  boolean isRadical;

  public void setProperty(String propertyName, Object value, BitSet bs) {

    
    

    boolean setInfo = false;
    
    if ("init" == propertyName) {
      myColorPt = 0;
      lcaoID = null;
      thisSet = bs;
      isMolecular = isLonePair = isRadical = false;
      thisType = null;
      rotationAxis = null;
      
      super.setProperty("init", null, null);
      return;
    }

    

    if ("lcaoID" == propertyName) {
      lcaoID = (String) value;
      return;
    }

    if ("thisID" == propertyName) {
      lcaoID = (String) value;
      
    }


    if ("selectType" == propertyName) {
      thisType = (String) value;
      return;
    }

    if ("rotationAxis" == propertyName) {
      rotationAxis = (Vector3f) value;
      return;
    }

    if ("scale" == propertyName) {
      lcaoScale = (Float) value;
      
    }

    if ("colorRGB" == propertyName) {
      lcaoColorPos = (Integer) value;
      if (myColorPt++ == 0)
        lcaoColorNeg = lcaoColorPos;
      
    }

    if ("select" == propertyName) {
      thisSet = (BitSet) value;
      
    }

    if ("translucentLevel" == propertyName) {
      translucentLevel = ((Float) value).floatValue();
      
    }

    if ("settranslucency" == propertyName) {
      isTranslucent = (((String) value).equals("translucent"));
      return;
    }

    if ("translucency" == propertyName) {
      isTranslucent = (((String) value).equals("translucent"));
      if (lcaoID == null)
        return;
    }

    
    if ("molecular" == propertyName) {
      isMolecular = true;
      if (value == null)
        return;
      propertyName = "create";
      
    }

    if ("create" == propertyName) {
      myColorPt = 0;
      thisType = (String) value;
      createLcaoCartoon();
      return;
    }

    if ("lonePair" == propertyName) {
      isLonePair = true;
      return;
    }

    if ("lp" == propertyName) {
      isLonePair = setInfo = true;
    }

    if ("radical" == propertyName) {
      isRadical = true;
      return;
    }

    if ("rad" == propertyName) {
      isRadical = setInfo = true;
    }

    if ("delete" == propertyName) {
      deleteLcaoCartoon();
      return;
    }
    
    if ("on" == propertyName) {
      setLcaoOn(true);
      return;
    }
    
    if ("off" == propertyName) {
      setLcaoOn(false);
      return;
    }

    super.setProperty(propertyName, value, bs);
    
    
    if (setInfo || "lobe" == propertyName || "sphere" == propertyName)
      setScriptInfo();
  }

  private void setLcaoOn(boolean TF) {
    if (TextFormat.isWild(lcaoID)) {
      String key = lcaoID.toLowerCase();
      for (int i = meshCount; --i >= 0; ) {
        if (TextFormat.isMatch(meshes[i].thisID.toLowerCase(), key, true, true))
          meshes[i].visible = TF;
      }
      return;
    }
    

    int atomCount = viewer.getAtomCount();
    for (int i = atomCount; --i >= 0;)
      if (lcaoID != null || thisSet.get(i))
        setLcaoOn(i, TF);
  }

  private void setLcaoOn(int iAtom, boolean TF) {
    String id = getID(lcaoID, iAtom);
    for (int i = meshCount; --i >= 0;)
      if (meshes[i].thisID.indexOf(id) == 0)
        meshes[i].visible = TF;
  }

  private void deleteLcaoCartoon() {
    if (TextFormat.isWild(lcaoID)) {
      deleteMesh(lcaoID);
      return;
    }
    
    int atomCount = viewer.getAtomCount();
    for (int i = atomCount; --i >= 0;)
      if (lcaoID != null || thisSet.get(i))
        deleteLcaoCartoon(i);
  }

  private void deleteLcaoCartoon(int iAtom) {
    String id = getID(lcaoID, iAtom);
    for (int i = meshCount; --i >= 0;)
      if (meshes[i].thisID.indexOf(id) == 0)
        deleteMesh(i);
  }

  private void createLcaoCartoon() {
    isMolecular = (isMolecular && (thisType.indexOf("px") >= 0
        || thisType.indexOf("py") >= 0 || thisType.indexOf("pz") >= 0));
    String lcaoID0 = lcaoID;
    for (int i = thisSet.nextSetBit(0); i >= 0; i = thisSet.nextSetBit(i + 1)) {
      createLcaoCartoon(i);
      lcaoID = lcaoID0;
    }
  }

  private void createLcaoCartoon(int iAtom) {
    String id = getID(lcaoID, iAtom);
    for (int i = meshCount; --i >= 0;)
      if (meshes[i].thisID.indexOf(id) == 0)
        deleteMesh(i);
    super.setProperty("init", null, null);
    super.setProperty("thisID", id, null);
    
    if (lcaoScale != null)
      super.setProperty("scale", lcaoScale, null);
    if (lcaoColorNeg != null) {
      super.setProperty("colorRGB", lcaoColorNeg, null);
      super.setProperty("colorRGB", lcaoColorPos, null);
    }
    super.setProperty("lcaoType", thisType, null);
    super.setProperty("atomIndex", new Integer(iAtom), null);
    Vector3f[] axes = { new Vector3f(), new Vector3f(),
        new Vector3f(modelSet.atoms[iAtom]), new Vector3f() };
    if (rotationAxis != null)
      axes[3].set(rotationAxis);
    if (isMolecular) {
      if (thisType.indexOf("px") >= 0) {
        axes[0].set(0, -1, 0);
        axes[1].set(1, 0, 0);
      } else if (thisType.indexOf("py") >= 0) {
        axes[0].set(-1, 0, 0);
        axes[1].set(0, 0, 1);
      } else if (thisType.indexOf("pz") >= 0) {
        axes[0].set(0, 0, 1);
        axes[1].set(1, 0, 0);
      }
      if (thisType.indexOf("-") == 0)
        axes[0].scale(-1);
    }
    if (isMolecular
        || thisType.equalsIgnoreCase("s")
        || viewer.getHybridizationAndAxes(iAtom, axes[0], axes[1], thisType,
            true) != null) {
      super.setProperty((isRadical ? "radical" : isLonePair ? "lonePair" : "lcaoCartoon"), axes, null);
    }
    if (isTranslucent)
      for (int i = meshCount; --i >= 0;)
        if (meshes[i].thisID.indexOf(id) == 0)
          meshes[i].setTranslucent(true, translucentLevel);
  }

  private String getID(String id, int i) {
    
    
    
    return (id != null ? id : (isLonePair || isRadical ? "lp_" : "lcao_") + (i + 1) + "_")
        + (thisType == null ? "" : TextFormat.simpleReplace(thisType, "-",
            (thisType.indexOf("-p") == 0 ? "" : "_")));
  }

  public String getShapeState() {
    StringBuffer sb = new StringBuffer();
    if (lcaoScale != null)
      appendCmd(sb, "lcaoCartoon scale " + lcaoScale.floatValue());
    if (lcaoColorNeg != null)
      appendCmd(sb, "lcaoCartoon color "
          + Escape.escapeColor(lcaoColorNeg.intValue()) + " "
          + Escape.escapeColor(lcaoColorPos.intValue()));
    if (isTranslucent)
      appendCmd(sb, "lcaoCartoon translucent " + translucentLevel);
    for (int i = meshCount; --i >= 0;)
      if (!meshes[i].visible)
        appendCmd(sb, "lcaoCartoon ID " + meshes[i].thisID + " off");
    return super.getShapeState() + sb.toString();
  }
}
