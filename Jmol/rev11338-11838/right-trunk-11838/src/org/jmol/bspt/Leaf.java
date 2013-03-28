
package org.jmol.bspt;

import javax.vecmath.Point3f;


class Leaf extends Element {
  Point3f[] tuples;
    
  Leaf(Bspt bspt) {
    this.bspt = bspt;
    count = 0;
    tuples = new Point3f[Bspt.leafCountMax];
  }
    
  Leaf(Bspt bspt, Leaf leaf, int countToKeep) {
    this(bspt);
    for (int i = countToKeep; i < Bspt.leafCountMax; ++i) {
      tuples[count++] = leaf.tuples[i];
      leaf.tuples[i] = null;
    }
    leaf.count = countToKeep;
  }

  void sort(int dim) {
    for (int i = count; --i > 0; ) { 
      Point3f champion = tuples[i];
      float championValue = Node.getDimensionValue(champion, dim);
      for (int j = i; --j >= 0; ) {
        Point3f challenger = tuples[j];
        float challengerValue = Node.getDimensionValue(challenger, dim);
        if (challengerValue > championValue) {
          tuples[i] = challenger;
          tuples[j] = champion;
          champion = challenger;
          championValue = challengerValue;
        }
      }
    }
  }

  Element addTuple(int level, Point3f tuple) {
    if (count < Bspt.leafCountMax) {
      tuples[count++] = tuple;
      return this;
    }
    Node node = new Node(bspt, level, this);
    return node.addTuple(level, tuple);
  }
    
  

}
