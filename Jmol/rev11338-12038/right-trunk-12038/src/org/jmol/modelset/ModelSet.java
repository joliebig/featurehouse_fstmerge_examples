  

package org.jmol.modelset;

import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Point3fi;
import org.jmol.viewer.JmolConstants;
import org.jmol.script.Token;
import org.jmol.api.Interface;
import org.jmol.api.SymmetryInterface;
import org.jmol.atomdata.AtomData;
import org.jmol.shape.Shape;

import java.util.BitSet;
import java.util.Hashtable;
import java.util.Vector;

import javax.vecmath.Point3f;


abstract public class ModelSet extends ModelCollection {

  

  protected void releaseModelSet() {
    if (shapes != null)
      for (int i = 0; i < JmolConstants.SHAPE_MAX; i++)
        shapes[i] = null;
    models = null;
    closest[0] = null;
    super.releaseModelSet();
  }

  

  private boolean selectionHaloEnabled = false;
  private boolean echoShapeActive = false;

  public void setSelectionHaloEnabled(boolean selectionHaloEnabled) {
    if (this.selectionHaloEnabled != selectionHaloEnabled) {
      this.selectionHaloEnabled = selectionHaloEnabled;
    }
  }

  public boolean getSelectionHaloEnabled() {
    return selectionHaloEnabled;
  }

  public boolean getEchoStateActive() {
    return echoShapeActive;
  }

  public void setEchoStateActive(boolean TF) {
    echoShapeActive = TF;
  }

  protected String modelSetTypeName;

  public String getModelSetTypeName() {
    return modelSetTypeName;
  }

  protected Shape[] shapes;
  
  
  private Shape allocateShape(int shapeID) {
    if (shapeID == JmolConstants.SHAPE_HSTICKS || shapeID == JmolConstants.SHAPE_SSSTICKS)
      return null;
    String className = JmolConstants.getShapeClassName(shapeID);
    try {
      Class shapeClass = Class.forName(className);
      Shape shape = (Shape) shapeClass.newInstance();
      viewer.setShapeErrorState(shapeID, "allocate");
      shape.initializeShape(viewer, g3d, this, shapeID);
      viewer.setShapeErrorState(-1, null);
      return shape;
    } catch (Exception e) {
      Logger.error("Could not instantiate shape:" + className, e);
    }
    return null;
  }

  public Shape getShape(int i) {
    
    return shapes[i];
  }
  
  float getAtomShapeValue(int atomIndex, int tok) {
    int iShape = JmolConstants.shapeTokenIndex(tok);
    if (iShape < 0 || shapes[iShape] == null) 
      return 0;
    int mad = shapes[iShape].getSize(atomIndex);
    if (mad == 0) {
      Group group = atoms[atomIndex].getGroup();
      if ((group.shapeVisibilityFlags & shapes[iShape].myVisibilityFlag) == 0)
        return 0;
      mad = shapes[iShape].getSize(group);
    }
    return mad / 2000f;
  }

  public int getModelNumberIndex(int modelNumber, boolean useModelNumber,
                                 boolean doSetTrajectory) {
    if (useModelNumber) {
      for (int i = 0; i < modelCount; i++)
        if (modelNumbers[i] == modelNumber)
          return i;
      return -1;
    }
    
    for (int i = 0; i < modelCount; i++)
      if (modelFileNumbers[i] == modelNumber) {
        if (doSetTrajectory && isTrajectory(i))
          setTrajectory(i);
        return i;
      }
    return -1;
  }

  public String getTrajectoryInfo() {
    String s = "";
    if (trajectorySteps == null)
      return "";
    for (int i = modelCount; --i >= 0; )
      if (models[i].selectedTrajectory >= 0) {
        s = " or " + getModelNumberDotted(models[i].selectedTrajectory) + s;
        i = models[i].trajectoryBaseIndex; 
      }
    if (s.length() > 0)
      s = "set trajectory {" + s.substring(4) + "}"; 
    return s;
  }

  public BitSet getBitSetTrajectories() {
    if (trajectorySteps == null)
      return null;
    BitSet bsModels = new BitSet();
    for (int i = modelCount; --i >= 0;)
      if (models[i].selectedTrajectory >= 0) {
        bsModels.set(models[i].selectedTrajectory);
        i = models[i].trajectoryBaseIndex; 
      }
    return bsModels;
  }

  public void setTrajectory(BitSet bsModels) {
    for (int i = 0; i < modelCount; i++)
      if (bsModels.get(i))
        setTrajectory(i);
  }

  public void setTrajectory(int modelIndex) {
    if (modelIndex < 0 || !models[modelIndex].isTrajectory)
      return;
    
    

    
    int iFirst = models[modelIndex].firstAtomIndex;
    if (atoms[iFirst].modelIndex == modelIndex)
      return;
    int baseModel = models[modelIndex].trajectoryBaseIndex;
    models[baseModel].selectedTrajectory = modelIndex;
    Point3f[] trajectory = (Point3f[]) trajectorySteps.get(modelIndex);
    BitSet bs = new BitSet();
    int iMax = iFirst + getAtomCountInModel(baseModel);
    for (int pt = 0, i = iFirst; i < iMax && pt < trajectory.length && trajectory[pt]!= null; i++) {
      atoms[i].setFractionalCoord(trajectory[pt++]);
      atoms[i].modelIndex = (short) modelIndex;
      bs.set(i);
    }
    
    
    initializeBspf();
    bspf.clearBspt(baseModel);
    
    
    
    
    recalculateLeadMidpointsAndWingVectors(baseModel);
    
    Integer Imodel = new Integer(baseModel);
    for (int i = 0; i < JmolConstants.SHAPE_MAX; i++)
      if (shapes[i] != null)
      setShapeProperty(i, "refreshTrajectories", Imodel, bs);
    if (models[baseModel].hasCalculatedHBonds) {
      clearCalculatedHydrogenBonds(baseModel, null);
      models[baseModel].calcHydrogenBonds(bs, bs);
    }
    int m = viewer.getCurrentModelIndex();
    if (m >= 0 && m != modelIndex 
        && models[m].fileIndex == models[modelIndex].fileIndex)
      viewer.setCurrentModelIndex(modelIndex, false);
  }  

  
  public BitSet getAtomBits(int tokType, Object specInfo) {
    switch (tokType) {
    case Token.spec_model:
      return getSpecModel(((Integer) specInfo).intValue());
    }
    return super.getAtomBits(tokType, specInfo);
  }

  public String getAtomLabel(int i) {
    return (String) getShapeProperty(JmolConstants.SHAPE_LABELS, "label", i);
  }
  
  private BitSet getSpecModel(int modelNumber) {
    int modelIndex = getModelNumberIndex(modelNumber, true, true);
    return (modelIndex < 0 && modelNumber > 0 ? new BitSet()
        : getModelAtomBitSet(modelIndex, true));
  }

  protected final Atom[] closest = new Atom[1];

  public int findNearestAtomIndex(int x, int y) {
    if (atomCount == 0)
      return -1;
    closest[0] = null;
    if (g3d.isAntialiased()) {
      x <<= 1;
      y <<= 1;
    }
    findNearestAtomIndex(x, y, closest);

    if (shapes != null)
      for (int i = 0; i < shapes.length && closest[0] == null; ++i)
        if (shapes[i] != null)
          shapes[i].findNearestAtomIndex(x, y, closest);
    int closestIndex = (closest[0] == null ? -1 : closest[0].atomIndex);
    closest[0] = null;
    return closestIndex;
  }

  public void setShapeSize(int shapeID, int size, float fsize, BitSet bsSelected) {
    if (shapes == null)
      return;
    viewer.setShapeErrorState(shapeID, "set size");
    if (size != 0)
      loadShape(shapeID);
    if (shapes[shapeID] != null) {
      if (Float.isNaN(fsize))
        shapes[shapeID].setSize(size, bsSelected);
      else
        shapes[shapeID].setSize(size, fsize, bsSelected);
    }
    viewer.setShapeErrorState(-1, null);
  }

  public Shape loadShape(int shapeID) {
    if (shapes == null)
      return null;
    if (shapes[shapeID] == null)
      shapes[shapeID] = allocateShape(shapeID);
    return shapes[shapeID];
  }

  public void setShapeProperty(int shapeID, String propertyName, Object value,
                               BitSet bsSelected) {
    if (shapes == null || shapes[shapeID] == null)
      return;
    viewer.setShapeErrorState(shapeID, "set " + propertyName);
    shapes[shapeID].setProperty(
        propertyName.intern(), value, bsSelected);
    viewer.setShapeErrorState(-1, null);
  }

  public void releaseShape(int shapeID) {
    if (shapes != null) 
      shapes[shapeID] = null;  
  }
  
  public Object getShapeProperty(int shapeID, String propertyName, int index) {
    if (shapes == null || shapes[shapeID] == null)
      return null;
    viewer.setShapeErrorState(shapeID, "get " + propertyName);
    Object result = shapes[shapeID].getProperty(propertyName, index);
    viewer.setShapeErrorState(-1, null);
    return result;
  }

  public boolean getShapeProperty(int shapeID, String propertyName, Object[] data) {
    if (shapes == null || shapes[shapeID] == null)
      return false;
    viewer.setShapeErrorState(shapeID, "get " + propertyName);
    boolean result = shapes[shapeID].getProperty(propertyName, data);
    viewer.setShapeErrorState(-1, null);
    return result;
  }

  public int getShapeIdFromObjectName(String objectName) {
    if (shapes != null)
      for (int i = JmolConstants.SHAPE_MIN_SPECIAL; i < JmolConstants.SHAPE_MAX_MESH_COLLECTION; ++i)
        if (shapes[i] != null && shapes[i].getIndexFromName(objectName) >= 0)
          return i;
    return -1;
  }

  public void setModelVisibility() {
    if (shapes == null)
      return;

    
    

    
    
    

    BitSet bs = viewer.getVisibleFramesBitSet();
    
    
    for (int i = 1; i < JmolConstants.SHAPE_MAX; i++)
      if (shapes[i] != null)
        shapes[i].setVisibilityFlags(bs);
    
    shapes[JmolConstants.SHAPE_BALLS].setVisibilityFlags(bs);

    
    for (int i = 0; i < JmolConstants.SHAPE_MAX; ++i) {
      Shape shape = shapes[i];
      if (shape != null)
        shape.setModelClickability();
    }
  }

    

  

  public void fillAtomData(AtomData atomData, int mode) {
    if (mode == AtomData.MODE_GET_ATTACHED_HYDROGENS) {
      int[] nH = new int[1];
      atomData.hAtomRadius = viewer.getVanderwaalsMar(1) / 1000f;
      atomData.hAtoms = getAdditionalHydrogens(atomData.bsSelected, nH);
      atomData.hydrogenAtomCount = nH[0];
      return;
    }
    if(atomData.modelIndex < 0)
      atomData.firstAtomIndex = Math.max(0, BitSetUtil.firstSetBit(atomData.bsSelected));
    else
      atomData.firstAtomIndex = models[atomData.modelIndex].firstAtomIndex;
    atomData.lastModelIndex = atomData.firstModelIndex = (atomCount == 0 ? 0 : atoms[atomData.firstAtomIndex].modelIndex);
    atomData.modelName = getModelNumberDotted(atomData.firstModelIndex);
    super.fillAtomData(atomData, mode);
  }


  
  
  
  public boolean frankClicked(int x, int y) {
    Shape frankShape = shapes[JmolConstants.SHAPE_FRANK];
    return (frankShape != null && frankShape.wasClicked(x, y));
  }

  public boolean checkObjectHovered(int x, int y, BitSet bsVisible) {
    Shape shape = shapes[JmolConstants.SHAPE_ECHO];
    if (shape != null && shape.checkObjectHovered(x, y, bsVisible))
      return true;
    shape = shapes[JmolConstants.SHAPE_ISOSURFACE];
    if (shape != null && shape.checkObjectHovered(x, y, bsVisible))
      return true;
    shape = shapes[JmolConstants.SHAPE_DRAW];
    if (shape != null && viewer.getDrawHover() 
        && shape.checkObjectHovered(x, y, bsVisible))
      return true;
    shape = shapes[JmolConstants.SHAPE_FRANK];
    if (viewer.getShowFrank() && shape != null  
        && shape.checkObjectHovered(x, y, bsVisible))
      return true;
    return false;
  }

  public Point3fi checkObjectClicked(int x, int y, int modifiers,
                                    BitSet bsVisible) {
    Shape shape;
    Point3fi pt = null;
    
    if (viewer.getNavigationMode() && viewer.getNavigateSurface() 
        && (shape = shapes[JmolConstants.SHAPE_ISOSURFACE]) != null && 
        (pt = shape.checkObjectClicked(x, y, modifiers, bsVisible)) != null)
      return pt;

    if (modifiers != 0 && viewer.getBondPicking()
        && (pt = shapes[JmolConstants.SHAPE_STICKS].checkObjectClicked(x, y,
            modifiers, bsVisible)) != null)
      return pt;

    if ((shape = shapes[JmolConstants.SHAPE_ECHO])!= null && modifiers != 0
        && (pt = shape.checkObjectClicked(x, y, modifiers, bsVisible)) != null)
      return pt;
    if ((shape = shapes[JmolConstants.SHAPE_DRAW]) != null && 
        (pt = shape.checkObjectClicked(x, y, modifiers, bsVisible)) != null)
      return pt;
    return null;
  }
 
  public void checkObjectDragged(int prevX, int prevY, int x, int y,
                          int modifiers, BitSet bsVisible, int iShape) {
    for (int i = iShape; i < JmolConstants.SHAPE_MAX; ++i) {
      Shape shape = shapes[i];
      if (shape != null
          && shape.checkObjectDragged(prevX, prevY, x, y, modifiers, bsVisible)
          || iShape > 0)
        break;
    }
  }

  public Hashtable getShapeInfo() {
    Hashtable info = new Hashtable();
    StringBuffer commands = new StringBuffer();
    if (shapes != null)
      for (int i = 0; i < JmolConstants.SHAPE_MAX; ++i) {
        Shape shape = shapes[i];
        if (shape != null) {
          String shapeType = JmolConstants.shapeClassBases[i];
          Vector shapeDetail = shape.getShapeDetail();
          if (shapeDetail != null)
            info.put(shapeType, shapeDetail);
        }
      }
    if (commands.length() > 0)
      info.put("shapeCommands", commands.toString());
    return info;
  }

  public void calculateStructures(BitSet bsAtoms) {
    BitSet bsAllAtoms = new BitSet();
    BitSet bsDefined = BitSetUtil.invertInPlace(modelsOf(bsAtoms, bsAllAtoms),
        modelCount);
    for (int i = 0; i < modelCount; i++)
      if (!bsDefined.get(i))
        addBioPolymerToModel(null, models[i]);
    calculatePolymers(bsDefined);
    calculateStructuresAllExcept(bsDefined, false);
    if (shapes != null)
      for (int i = 0; i < shapes.length; ++i)
        if (shapes[i] != null && shapes[i].isBioShape) {
          shapes[i].setSize(0, bsAllAtoms);
          shapes[i].setProperty("color", new Byte(JmolConstants.PALETTE_CPK),
              bsAllAtoms);
        }
    setStructureIds();
  }

  public String calculatePointGroup(BitSet bsAtoms) {
    return (String) calculatePointGroupForFirstModel(bsAtoms, false,
        false, false, null, 0, 0);
  }

  public Hashtable getPointGroupInfo(BitSet bsAtoms) {
    return (Hashtable) calculatePointGroupForFirstModel(bsAtoms, false,
        false, true, null, 0, 0);
  }
  
  public String getPointGroupAsString(BitSet bsAtoms, boolean asDraw,
                                      String type, int index, float scale) {
    return (String) calculatePointGroupForFirstModel(bsAtoms, true,
        asDraw, false, type, index, scale);
  }

  private SymmetryInterface pointGroup;
  private Object calculatePointGroupForFirstModel(BitSet bsAtoms,
                                                  boolean doAll,
                                                  boolean asDraw,
                                                  boolean asInfo, String type,
                                                  int index, float scale) {
    int modelIndex = viewer.getCurrentModelIndex();
    int iAtom = BitSetUtil.firstSetBit(bsAtoms);
    if (modelIndex < 0 && iAtom >= 0)
      modelIndex = atoms[iAtom].getModelIndex();
    if (modelIndex < 0) {
      modelIndex = BitSetUtil.firstSetBit(viewer.getVisibleFramesBitSet());
      bsAtoms = null;
    }
    BitSet bs = getModelAtomBitSet(modelIndex, true);
    if (bsAtoms != null)
      for (int i = 0; i < atomCount; i++)
        if (atoms[i].modelIndex == modelIndex)
          if (!bsAtoms.get(i))
            bs.clear(i);
    iAtom = BitSetUtil.firstSetBit(bs);
    if (iAtom < 0) {
      bs = getModelAtomBitSet(modelIndex, true);
      iAtom = BitSetUtil.firstSetBit(bs);
    }
    Object obj = getShapeProperty(JmolConstants.SHAPE_VECTORS, "mad", iAtom);
    boolean haveVibration = (obj != null && ((Integer) obj).intValue() != 0 || viewer
        .isVibrationOn());
    SymmetryInterface symmetry = (SymmetryInterface) Interface
        .getOptionInterface("symmetry.Symmetry");
    pointGroup = symmetry.setPointGroup(pointGroup, atoms, bs, haveVibration,
        viewer.getPointGroupTolerance(0), viewer.getPointGroupTolerance(1));
    if (!doAll && !asInfo)
      return pointGroup.getPointGroupName();
    Object ret = pointGroup.getPointGroupInfo(modelIndex, asDraw, asInfo, type,
        index, scale);
    if (asInfo)
      return ret;
    return (modelCount > 1 ? "frame " + getModelNumberDotted(modelIndex) + "; "
        : "") + ret;
  }

  private BitSet modelsOf(BitSet bsAtoms, BitSet bsAllAtoms) {
    BitSet bsModels = new BitSet(modelCount);
    for (int i = 0; i < atomCount; i++) {
      int modelIndex = models[atoms[i].modelIndex].trajectoryBaseIndex;
      if (bsAtoms != null && !bsAtoms.get(i) || isJmolDataFrame(modelIndex))
        continue;
      bsModels.set(modelIndex);
      bsAllAtoms.set(i);
    }
    return bsModels;
  }


  
  
  
  
  private final static boolean useRasMolHbondsCalculation = true;

  public int autoHbond(BitSet bsA, BitSet bsB, BitSet bsBonds, 
                       float maxXYDistance, float minAttachedAngle) {
    bsPseudoHBonds = new BitSet();
    if (minAttachedAngle == 0 && useRasMolHbondsCalculation && bondCount > 0) {
      calcHydrogenBonds(bsA, bsB);
      bsBonds = bsPseudoHBonds;
      return BitSetUtil.cardinalityOf(bsBonds);
    }
    initializeBspf();
    return super.autoHbond(bsA, bsB, bsBonds, maxXYDistance, minAttachedAngle);
  }
  
  protected void assignAromaticBonds(boolean isUserCalculation) {
    super.assignAromaticBonds(isUserCalculation, null);
    
    
    
    
    if (isUserCalculation)
      setShapeSize(JmolConstants.SHAPE_STICKS, Integer.MIN_VALUE, Float.NaN, bsAromatic);
  }

  public int[] makeConnections(float minDistance, float maxDistance, short order,
                             int connectOperation, BitSet bsA, BitSet bsB,
                             BitSet bsBonds, boolean isBonds) {
    if (connectOperation == JmolConstants.CONNECT_DELETE_BONDS 
        || connectOperation == JmolConstants.CONNECT_AUTO_BOND) {
      String stateScript = "connect ";
      if (minDistance != JmolConstants.DEFAULT_MIN_CONNECT_DISTANCE)
        stateScript += minDistance + " ";
      if (maxDistance != JmolConstants.DEFAULT_MAX_CONNECT_DISTANCE)
        stateScript += maxDistance + " ";
      addStateScript(stateScript, (isBonds? bsA : null), 
          (isBonds? null : bsA), (isBonds ? null : bsB),  
          (connectOperation == JmolConstants.CONNECT_DELETE_BONDS ? " delete" : " auto"), 
          false, true);
    }
    return super.makeConnections(minDistance, maxDistance, order,
        connectOperation, bsA, bsB, bsBonds, isBonds);
  }
  
  public void setPdbConectBonding(int baseAtomIndex, int baseModelIndex, BitSet bsExclude) {
    short mad = viewer.getMadBond();
    for (int i = baseModelIndex; i < modelCount; i++) {
      Vector vConnect = (Vector) getModelAuxiliaryInfo(i, "PDB_CONECT_bonds");
      if (vConnect == null)
        continue;
      int nConnect = vConnect.size();
      int[] atomInfo = (int[]) getModelAuxiliaryInfo(i, "PDB_CONECT_firstAtom_count_max");
      int firstAtom = atomInfo[0] + baseAtomIndex;
      int atomMax = firstAtom + atomInfo[1];
      int max = atomInfo[2];
      int[] serialMap = new int[max + 1];
      int iSerial;
      for (int iAtom = firstAtom; iAtom < atomMax; iAtom++)
        if ((iSerial = atomSerials[iAtom]) > 0)
          serialMap[iSerial] = iAtom + 1;
      for (int iConnect = 0; iConnect < nConnect; iConnect++) {
        int[] pair = (int[]) vConnect.get(iConnect);
        int sourceSerial = pair[0];
        int targetSerial = pair[1];
        short order = (short) pair[2];
        if (sourceSerial < 0 || targetSerial < 0 || sourceSerial > max
            || targetSerial > max)
          continue;
        int sourceIndex = serialMap[sourceSerial] - 1;
        int targetIndex = serialMap[targetSerial] - 1;
        if (sourceIndex < 0 || targetIndex < 0)
          continue;
        if (bsExclude != null) {
        if (atoms[sourceIndex].isHetero())
          bsExclude.set(sourceIndex);
        if (atoms[targetIndex].isHetero())
          bsExclude.set(targetIndex);
        }
        checkValencesAndBond(atoms[sourceIndex], atoms[targetIndex], order, 
            (order == JmolConstants.BOND_H_REGULAR ? 1 : mad), null);
      }
    }
  }
  
  public void deleteAllBonds() {
    for (int i = stateScripts.size(); --i >= 0;) 
      if (((StateScript) stateScripts.get(i)).isConnect())
        stateScripts.removeElementAt(i);
    super.deleteAllBonds();
  }

  

  public String getDefinedState(StringBuffer sfunc, boolean isAll) {
    int len = stateScripts.size();
    if (len == 0)
      return "";
    
    boolean haveDefs = false;    
    StringBuffer commands = new StringBuffer();
    String cmd;
    for (int i = 0; i < len; i++) {
      StateScript ss = (StateScript) stateScripts.get(i); 
      if (!ss.postDefinitions && (cmd = ss.toString()).length() > 0) {
        commands.append("  ").append(cmd).append("\n");
        haveDefs = true;
      }
    }

    if (!haveDefs)
      return "";
    cmd = "";
    if (isAll && sfunc != null) {
      sfunc.append("  _setDefinedState;\n");
      cmd = "function _setDefinedState() {\n\n";
    }

    if (sfunc != null)
      commands.append("\n}\n\n");
    return cmd + commands.toString();
  }
  
  public String getState(StringBuffer sfunc, boolean isAll) {
    StringBuffer commands = new StringBuffer();
    if (isAll && sfunc != null) {
      sfunc.append("  _setModelState;\n");
      commands.append("function _setModelState() {\n");
    }
    String cmd;

    

    if (isAll) {

      int len = stateScripts.size();
      for (int i = 0; i < len; i++) {
        StateScript ss = (StateScript) stateScripts.get(i);
        if (ss.postDefinitions && (cmd = ss.toString()).length() > 0)
          commands.append("  ").append(cmd).append("\n");
      }

      for (int i = 0; i < bondCount; i++) {
        if ((bonds[i].order & JmolConstants.BOND_NEW) != 0
            || bonds[i].isHydrogen()) {
          Bond bond = bonds[i];
          commands.append("  connect ").append("({").append(
              bond.atom1.atomIndex).append("}) ").append("({").append(
              bond.atom2.atomIndex).append("}) ").append(
              JmolConstants.getBondOrderNameFromOrder(bond.order))
              .append(";\n");
        }
      }
      commands.append("\n");
    }

    

    setModelVisibility();

    

    if (viewer.getBooleanProperty("saveProteinStructureState"))
      commands.append(getProteinStructureState(null, true, false, false));

    if (shapes != null)
      for (int i = 0; i < JmolConstants.SHAPE_MAX; ++i) {
        Shape shape = shapes[i];
        if (shape != null && (isAll || JmolConstants.isShapeSecondary(i))
            && (cmd = shape.getShapeState()) != null && cmd.length() > 1)
          commands.append(cmd);
      }

    if (isAll) {
      for (int i = 0; i < modelCount; i++) {
        String t = frameTitles[i];
        if (t != null && t.length() > 0)
          commands.append("  frame " + getModelNumberDotted(i)
              + "; frame title " + Escape.escape(t) + ";\n");
        if (models[i].orientation != null)
          commands.append("  frame " + getModelNumberDotted(i) + "; "
              + models[i].orientation.getMoveToText() + "\n");
      }

      commands.append("  set fontScaling " + viewer.getFontScaling() + ";\n");

    }
    if (sfunc != null)
      commands.append("\n}\n\n");
    return commands.toString();
  }

  public int getVanderwaalsMar(int i) {
    return viewer.getVanderwaalsMar(i);
  }

  private void includeAllRelatedFrames(BitSet bsModels) {
    int j;
    for (int i = 0; i < modelCount; i++) {
      if (bsModels.get(i)) {
       
         
        
          
       
        if (isTrajectory(i) && !bsModels.get(j = models[i].trajectoryBaseIndex)) {
          bsModels.set(j);
          includeAllRelatedFrames(bsModels);
          return;
        }
        continue;
      }
      if (isTrajectory(i) && bsModels.get(models[i].trajectoryBaseIndex)
          || isJmolDataFrame(i) && bsModels.get(models[i].dataSourceFrame))
        bsModels.set(i);
    }
  }
  
  public BitSet deleteModels(BitSet bsAtoms) {
    
    BitSet bsModels = getModelBitSet(bsAtoms, false);
    includeAllRelatedFrames(bsModels);
    int nAtomsDeleted = 0;

    int nModelsDeleted = BitSetUtil.cardinalityOf(bsModels);
    if (nModelsDeleted == 0)
      return null;

    

    for (int i = 0; i < modelCount; i++)
      if (bsModels.get(i))
        clearDataFrameReference(i);

    BitSet bsDeleted;
    if (nModelsDeleted == modelCount) {
      bsDeleted = getModelAtomBitSet(-1, true);
      viewer.zap(true, false);
      return bsDeleted;
    }

    

    bspf = null;

    
    
    Model[] newModels = new Model[modelCount - nModelsDeleted];
    Model[] oldModels = models;
    bsDeleted = new BitSet();
    for (int i = 0, mpt = 0; i < modelCount; i++)
      if (bsModels.get(i)) { 
        getAtomCountInModel(i);
        bsDeleted.or(getModelAtomBitSet(i, false));
      } else {
        models[i].modelIndex = mpt;
        newModels[mpt++] = models[i];
      }
    models = newModels;
    int oldModelCount = modelCount;
    
    BitSet bsBonds = getBondsForSelectedAtoms(bsDeleted, true);
    deleteBonds(bsBonds);

    
    for (int i = 0, mpt = 0; i < oldModelCount; i++) {
      if (!bsModels.get(i)) {
        mpt++;
        continue;
      }
      int nAtoms = oldModels[i].atomCount;
      if (nAtoms == 0)
        continue;
      nAtomsDeleted += nAtoms;
      BitSet bs = oldModels[i].bsAtoms;
      int firstAtomIndex = oldModels[i].firstAtomIndex;

      
      BitSetUtil.deleteBits(bsSymmetry, bs);

      
      
      deleteModel(mpt, firstAtomIndex, nAtoms, bs, bsBonds);

      
      for (int j = oldModelCount; --j > i;)
        oldModels[j].fixIndices(mpt, nAtoms, bs);

      
      Object[] value = new Object[] { newModels, atoms,
          new int[] { mpt, firstAtomIndex, nAtoms } };
      if (shapes != null)
        for (int j = 0; j < JmolConstants.SHAPE_MAX; j++)
          if (shapes[j] != null)
            setShapeProperty(j, "deleteModelAtoms", value, bs);
      modelCount--;
    }

    
    deleteModel(-1, 0, 0, null, null);
    return bsDeleted;
  }

  public void setLabel(String strLabel, BitSet bsSelection) {
    if (strLabel != null) { 
      loadShape(JmolConstants.SHAPE_LABELS);
      setShapeSize(JmolConstants.SHAPE_LABELS, 0, Float.NaN, bsSelection);
    }
    setShapeProperty(JmolConstants.SHAPE_LABELS, "label", strLabel, bsSelection);
  }
  
  public void setAtomProperty(BitSet bs, int tok, int iValue, float fValue,
                              String sValue, float[] values, String[] list) {
    switch (tok) {
    case Token.backbone:
    case Token.cartoon:
    case Token.meshRibbon:
    case Token.ribbon:
    case Token.rocket:
    case Token.strands:
    case Token.trace:
      if (fValue > Shape.RADIUS_MAX)
        fValue = Shape.RADIUS_MAX;
    case Token.halo:
    case Token.star:
      if (values != null)
        return;
      if (fValue > Atom.RADIUS_MAX)
        fValue = Atom.RADIUS_MAX;
      if (fValue < 0)
        fValue = 0;
      setShapeSize(JmolConstants.shapeTokenIndex(tok), (int) (fValue * 2000), Float.NaN, bs);
      return;
    }
    super.setAtomProperty(bs, tok, iValue, fValue, sValue, values, list);
  }
  
  public void setAtomLabel(String strLabel, int i) {
    if (shapes == null)
      return;
    loadShape(JmolConstants.SHAPE_LABELS);
    shapes[JmolConstants.SHAPE_LABELS].setProperty("label:"+strLabel, new Integer(i), null);
  }

  public Object getFileData(int modelIndex) {
    if (modelIndex < 0)
      return "";
    Hashtable fileData = (Hashtable) getModelAuxiliaryInfo(modelIndex, "fileData");
    if (fileData != null)
      return fileData;
    if (!getModelAuxiliaryInfoBoolean(modelIndex, "isCIF"))
      return getPDBHeader(modelIndex);
    fileData = viewer.getCifData(modelIndex);
    setModelAuxiliaryInfo(modelIndex, "fileData", fileData);
    return fileData;
  }

}

