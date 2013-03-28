
package org.jmol.viewer;

import org.jmol.script.Token;
import org.jmol.util.ArrayUtil;
import org.jmol.util.BitSetUtil;
import org.jmol.util.Escape;

import org.jmol.api.JmolSelectionListener;
import org.jmol.i18n.GT;
import org.jmol.modelset.ModelSet;

import java.util.BitSet;
import java.util.Hashtable;

class SelectionManager {

  private Viewer viewer;

  private JmolSelectionListener[] listeners = new JmolSelectionListener[0];

  SelectionManager(Viewer viewer) {
    this.viewer = viewer;
  }

  private final BitSet bsHidden = new BitSet();
  final BitSet bsSelection = new BitSet();

  BitSet bsSubset; 
  BitSet bsDeleted;

  
  private final static int TRUE = 1;
  private final static int FALSE = 0;
  private final static int UNKNOWN = -1;
  private int empty = TRUE;

  private boolean hideNotSelected;

  void clear() {
    clearSelection(true);
    hide(null, true);
    setSelectionSubset(null);
    bsDeleted = null;
  }

  void hide(BitSet bs, boolean isQuiet) {
    bsHidden.clear();
    if (bs != null)
      bsHidden.or(bs);
    ModelSet modelSet = viewer.getModelSet();
    if (modelSet != null)
      modelSet.setBsHidden(bsHidden);
    if (!isQuiet)
      viewer.reportSelection(GT._("{0} atoms hidden", ""
          + bsHidden.cardinality()));
  }

  void display(BitSet bsAll, BitSet bs, boolean isQuiet) {
    if (bs == null) {
      bsHidden.clear();
    } else {
      bsHidden.or(bsAll);
      bsHidden.andNot(bs);
    }
    BitSetUtil.andNot(bsHidden, bsDeleted);
    ModelSet modelSet = viewer.getModelSet();
    if (modelSet != null)
      modelSet.setBsHidden(bsHidden);
    if (!isQuiet)
      viewer.reportSelection(GT._("{0} atoms hidden", ""
          + bsHidden.cardinality()));
  }

  BitSet getHiddenSet() {
    return bsHidden;
  }

  boolean getHideNotSelected() {
    return hideNotSelected;
  }

  void setHideNotSelected(boolean TF) {
    hideNotSelected = TF;
    if (TF)
      selectionChanged(false);
  }

  boolean isSelected(int atomIndex) {
    return (atomIndex >= 0 && bsSelection.get(atomIndex));
  }

  void select(BitSet bs, boolean isQuiet) {
    if (bs == null) {
      selectAll(true);
      if (!viewer.getRasmolSetting(Token.hydrogen))
        excludeSelectionSet(viewer.getAtomBits(Token.hydrogen, null));
      if (!viewer.getRasmolSetting(Token.hetero))
        excludeSelectionSet(viewer.getAtomBits(Token.hetero, null));
      selectionChanged(false);
    } else {
      setSelectionSet(bs);
    }
    boolean reportChime = viewer.getMessageStyleChime();
    if (!reportChime && isQuiet)
      return;
    int n = getSelectionCount();
    if (reportChime)
      viewer.reportSelection((n == 0 ? "No atoms" : n == 1 ? "1 atom" : n
          + " atoms")
          + " selected!");
    else if (!isQuiet)
      viewer.reportSelection(GT._("{0} atoms selected", n));
  }

  void selectAll(boolean isQuiet) {
    int count = viewer.getAtomCount();
    empty = (count == 0) ? TRUE : FALSE;
    for (int i = count; --i >= 0;)
      bsSelection.set(i);
    BitSetUtil.andNot(bsSelection, bsDeleted);
    selectionChanged(isQuiet);
  }

  void clearSelection(boolean isQuiet) {
    setHideNotSelected(false);
    bsSelection.clear();
    empty = TRUE;
    selectionChanged(isQuiet);
  }

  void setSelectionSet(BitSet set) {
    bsSelection.clear();
    if (set != null)
      bsSelection.or(set);
    empty = UNKNOWN;
    selectionChanged(false);
  }

  void setSelectionSubset(BitSet bs) {

    
    
    
    

    bsSubset = bs;
  }

  boolean isInSelectionSubset(int atomIndex) {
    return (atomIndex < 0 || bsSubset == null || bsSubset.get(atomIndex));
  }

  void invertSelection() {
    BitSetUtil.invertInPlace(bsSelection, viewer.getAtomCount());
    empty = (bsSelection.length() > 0 ? FALSE : TRUE);
    selectionChanged(false);
  }

  private void excludeSelectionSet(BitSet setExclude) {
    if (setExclude == null || empty == TRUE)
      return;
    bsSelection.andNot(setExclude);
    empty = UNKNOWN;
  }

  private final BitSet bsTemp = new BitSet();
  int getSelectionCount() {
    if (empty == TRUE)
      return 0;
    empty = TRUE;
    BitSet bs;
    if (bsSubset != null) {
      bsTemp.clear();
      bsTemp.or(bsSubset);
      bsTemp.and(bsSelection);
      bs = bsTemp;
    } else {
      bs = bsSelection;
    }
    int count = bs.cardinality();
    if (count > 0)
      empty = FALSE;
    return count;
  }

  void addListener(JmolSelectionListener listener) {
    for (int i = listeners.length; --i >= 0;)
      if (listeners[i] == listener) {
        listeners[i] = null;
        break;
      }
    int len = listeners.length;
    for (int i = len; --i >= 0;)
      if (listeners[i] == null) {
        listeners[i] = listener;
        return;
      }
    if (listeners.length == 0)
      listeners = new JmolSelectionListener[1];
    else
      listeners = (JmolSelectionListener[]) ArrayUtil.doubleLength(listeners);
    listeners[len] = listener;
  }

  private void selectionChanged(boolean isQuiet) {
    if (hideNotSelected)
      hide(BitSetUtil.copyInvert(bsSelection, viewer.getAtomCount()), false);
    if (isQuiet || listeners.length == 0)
      return;
    for (int i = listeners.length; --i >= 0;)
      if (listeners[i] != null)
        listeners[i].selectionChanged(bsSelection);
  }

  String getState(StringBuffer sfunc) {
    StringBuffer commands = new StringBuffer();
    if (sfunc != null) {
      sfunc.append("  _setSelectionState;\n");
      commands.append("function _setSelectionState() {\n");
    }
    StateManager.appendCmd(commands, viewer.getTrajectoryInfo());
    if (bsHidden.length() > 0)
      StateManager.appendCmd(commands, "hide " + Escape.escape(bsHidden));
    if (bsSubset != null && bsSubset.length() > 0)
      StateManager.appendCmd(commands, "subset " + Escape.escape(bsSubset));
    if (bsDeleted != null && bsDeleted.length() > 0)
      StateManager.appendCmd(commands, "delete " + Escape.escape(bsDeleted));
    String cmd = null;
    Hashtable temp = new Hashtable();
    temp.put("-", bsSelection);
    cmd = StateManager.getCommands(temp, null, viewer.getAtomCount());
    if (cmd == null)
      StateManager.appendCmd(commands, "select none");
    else
      commands.append(cmd);
    StateManager.appendCmd(commands, "set hideNotSelected " + hideNotSelected);
    commands.append(viewer.getShapeProperty(JmolConstants.SHAPE_STICKS,
        "selectionState"));
    if (viewer.getSelectionHaloEnabled())
      StateManager.appendCmd(commands, "SelectionHalos ON");
    if (sfunc != null)
      commands.append("}\n\n");
    return commands.toString();
  }

  public int deleteAtoms(BitSet bs) {
    if (bsDeleted == null) {
      bsDeleted = BitSetUtil.copy(bs);
      return bs.cardinality();
    }
    BitSet bsNew = BitSetUtil.copy(bs);
    bsNew.andNot(bsDeleted);
    bsDeleted.or(bs);
    bsHidden.andNot(bsDeleted);
    bsSelection.andNot(bsDeleted);
    return bsNew.cardinality();
  }

  BitSet getDeletedAtoms() {
    return bsDeleted;
  }

}
