
package org.jmol.bspt;

import javax.vecmath.Point3f;


public class CubeIterator {
  Bspt bspt;

  Element[] stack;
  int sp;
  int leafIndex;
  Leaf leaf;

  
  float radius;
  
  float[] centerValues;
  private float cx, cy, cz;
  protected float dx, dy, dz;

  
  
  boolean tHemisphere;

  CubeIterator(Bspt bspt) {
    this.bspt = bspt;
    centerValues = new float[bspt.dimMax];
    stack = new Element[bspt.treeDepth];
  }

  
  public void initialize(Point3f center, float radius) {
    
    this.radius = radius;
    tHemisphere = false;
    cx = centerValues[0] = center.x;
    cy = centerValues[1] = center.y;
    cz = centerValues[2] = center.z;
    leaf = null;
    stack[0] = bspt.eleRoot;
    sp = 1;
    findLeftLeaf();
  }

  
  public void initializeHemisphere(Point3f center, float radius) {
    initialize(center, radius);
    tHemisphere = true;
  }

  
  public void release() {
    for (int i = bspt.treeDepth; --i >= 0; )
      stack[i] = null;
  }

  
  public boolean hasMoreElements() {
    while (leaf != null) {
      for ( ; leafIndex < leaf.count; ++leafIndex)
        if (isWithinRadius(leaf.tuples[leafIndex]))
          return true;
      findLeftLeaf();
    }
    return false;
  }

  
  public Point3f nextElement() {
    return leaf.tuples[leafIndex++];
  }

  
  public float foundDistance2() {
    return dx * dx + dy * dy + dz * dz;
  }
  
  
  private void findLeftLeaf() {
    leaf = null;
    if (sp == 0)
      return;
    Element ele = stack[--sp];
    while (ele instanceof Node) {
      Node node = (Node)ele;
      float centerValue = centerValues[node.dim];
      float maxValue = centerValue + radius;
      float minValue = centerValue;
      if (! tHemisphere || node.dim != 0)
        minValue -= radius;
      if (minValue <= node.maxLeft && maxValue >= node.minLeft) {
        if (maxValue >= node.minRight && minValue <= node.maxRight)
          stack[sp++] = node.eleRight;
        ele = node.eleLeft;
      } else if (maxValue >= node.minRight && minValue <= node.maxRight) {
        ele = node.eleRight;
      } else {
        if (sp == 0)
          return;
        ele = stack[--sp];
      }
    }
    leaf = (Leaf)ele;
    leafIndex = 0;
  }

  
  protected boolean isWithinRadius(Point3f t) {
    dx = t.x - cx;
    return (!tHemisphere || dx >= 0)        
    && (dx = Math.abs(dx)) <= radius
    && (dy = Math.abs(t.y - cy)) <= radius
    && (dz = Math.abs(t.z - cz)) <= radius;
  }
    
}
