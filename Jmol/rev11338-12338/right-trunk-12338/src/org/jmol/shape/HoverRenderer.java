
package org.jmol.shape;

import org.jmol.modelset.Atom;
import org.jmol.modelset.LabelToken;

public class HoverRenderer extends ShapeRenderer {
  protected void render() {
    if (viewer.isNavigating())
      return;
    Hover hover = (Hover) shape;
    boolean antialias = g3d.isAntialiased();
    if (hover.atomIndex >= 0) {
      Atom atom = modelSet.getAtomAt(hover.atomIndex);
      String label = (hover.specialLabel != null ? hover.specialLabel 
          : hover.atomFormats != null
          && hover.atomFormats[hover.atomIndex] != null ? 
              LabelToken.formatLabel(viewer, atom, hover.atomFormats[hover.atomIndex])
          : hover.labelFormat != null ? LabelToken.formatLabel(viewer, atom, fixLabel(atom, hover.labelFormat))
              : null);
      if (label == null)
        return;
      Text text = hover.hoverText;
      text.setText(label);
      text.setXY(atom.screenX, atom.screenY);
      text.render(g3d, 0, antialias ? 2 : 1, false);
    } else if (hover.text != null) {
      Text text = hover.hoverText;
      text.setText(hover.text);
      text.setXY(hover.xy.x, hover.xy.y);
      text.render(g3d, 0, antialias ? 2 : 1, false);
    }
  }
  
  String fixLabel(Atom atom, String label) {
    if (label == null)
      return null;
    return (viewer.isJmolDataFrame(atom.getModelIndex()) 
        && label.equals("%U") ?"%W" : label);
  }
}
