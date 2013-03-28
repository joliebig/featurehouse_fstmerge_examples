
package org.jmol.bspt;

import javax.vecmath.Point3f;


abstract class Element {
  Bspt bspt;
  int count;
  abstract Element addTuple(int level, Point3f tuple);
  
}

