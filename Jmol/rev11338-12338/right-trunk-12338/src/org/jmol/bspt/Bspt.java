
package org.jmol.bspt;

import javax.vecmath.Point3f;





public final class Bspt {

  final static int leafCountMax = 2;
  
  final static int MAX_TREE_DEPTH = 100;
  int treeDepth;
  int dimMax;
  Element eleRoot;

  

  
  public Bspt(int dimMax) {
    this.dimMax = dimMax;
    this.eleRoot = new Leaf(this);
    treeDepth = 1;
  }

  
  public void addTuple(Point3f tuple) {
    eleRoot = eleRoot.addTuple(0, tuple);
  }

  
  public void stats() {





  }

  

  


  public CubeIterator allocateCubeIterator() {
    return new CubeIterator(this);
  }

}

