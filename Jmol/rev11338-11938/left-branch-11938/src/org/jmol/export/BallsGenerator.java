

package org.jmol.export;

import org.jmol.modelset.Atom;
import org.jmol.shape.*;

public class BallsGenerator extends BallsRenderer {

  protected void renderBall(Atom atom) {
    ((Export3D)g3d).getExporter().renderAtom(atom, atom.getColix());
  }
}
