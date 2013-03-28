
package org.jmol.bspt;

import javax.vecmath.Point3f;

import org.jmol.util.Logger;


class Node extends Element {
  int dim;
  float minLeft, maxLeft;
  Element eleLeft;
  float minRight, maxRight;
  Element eleRight;
  
  Node(Bspt bspt, int level, Leaf leafLeft) {
    this.bspt = bspt;
    if (level == bspt.treeDepth) {
      bspt.treeDepth = level + 1;
      if (bspt.treeDepth >= Bspt.MAX_TREE_DEPTH)
        Logger.error("BSPT tree depth too great:" + bspt.treeDepth);
    }
    if (leafLeft.count != Bspt.leafCountMax)
      throw new NullPointerException();
    dim = level % bspt.dimMax;
    leafLeft.sort(dim);
    Leaf leafRight = new Leaf(bspt, leafLeft, Bspt.leafCountMax / 2);
    minLeft = getDimensionValue(leafLeft.tuples[0], dim);
    maxLeft = getDimensionValue(leafLeft.tuples[leafLeft.count - 1], dim);
    minRight = getDimensionValue(leafRight.tuples[0], dim);
    maxRight = getDimensionValue(leafRight.tuples[leafRight.count - 1], dim);
    
    eleLeft = leafLeft;
    eleRight = leafRight;
    count = Bspt.leafCountMax;
  }
  
  Element addTuple(int level, Point3f tuple) {
    float dimValue = getDimensionValue(tuple, dim);
    ++count;
    boolean addLeft;
    if (dimValue < maxLeft) {
      addLeft = true;
    } else if (dimValue > minRight) {
      addLeft = false;
    } else if (dimValue == maxLeft) {
      if (dimValue == minRight) {
        if (eleLeft.count < eleRight.count)
          addLeft = true;
        else
          addLeft = false;
      } else {
        addLeft = true;
      }
    } else if (dimValue == minRight) {
      addLeft = false;
    } else {
      if (eleLeft.count < eleRight.count)
        addLeft = true;
      else
        addLeft = false;
    }
    if (addLeft) {
      if (dimValue < minLeft)
        minLeft = dimValue;
      else if (dimValue > maxLeft)
        maxLeft = dimValue;
      eleLeft = eleLeft.addTuple(level + 1, tuple);
    } else {
      if (dimValue < minRight)
        minRight = dimValue;
      else if (dimValue > maxRight)
        maxRight = dimValue;
      eleRight = eleRight.addTuple(level + 1, tuple);
    }
    return this;
  }
  
  
  
  static float getDimensionValue(Point3f pt, int dim) {
    return (dim == 0 ? pt.x : dim == 1 ? pt.y : pt.z);
  }
}
