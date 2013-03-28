
package net.sourceforge.pmd.ast;

public class JJTJavaParserState {
  private java.util.List<Node> nodes;
  private java.util.List<Integer> marks;

  private int sp;        
  private int mk;        
  private boolean node_created;

  public JJTJavaParserState() {
    nodes = new java.util.ArrayList<Node>();
    marks = new java.util.ArrayList<Integer>();
    sp = 0;
    mk = 0;
  }

  
  public boolean nodeCreated() {
    return node_created;
  }

  
  public void reset() {
    nodes.clear();
    marks.clear();
    sp = 0;
    mk = 0;
  }

  
  public Node rootNode() {
    return nodes.get(0);
  }

  
  public void pushNode(Node n) {
    nodes.add(n);
    ++sp;
  }

  
  public Node popNode() {
    if (--sp < mk) {
      mk = marks.remove(marks.size()-1);
    }
    return nodes.remove(nodes.size()-1);
  }

  
  public Node peekNode() {
    return nodes.get(nodes.size()-1);
  }

  
  public int nodeArity() {
    return sp - mk;
  }


  public void clearNodeScope(Node n) {
    while (sp > mk) {
      popNode();
    }
    mk = marks.remove(marks.size()-1);
  }


  public void openNodeScope(Node n) {
    marks.add(mk);
    mk = sp;
    n.jjtOpen();
  }


  
  public void closeNodeScope(Node n, int num) {
    mk = marks.remove(marks.size()-1);
    while (num-- > 0) {
      Node c = popNode();
      c.jjtSetParent(n);
      n.jjtAddChild(c, num);
    }
    n.jjtClose();
    pushNode(n);
    node_created = true;
  }


  
  public void closeNodeScope(Node n, boolean condition) {
    if (condition) {
      int a = nodeArity();
      mk = marks.remove(marks.size()-1);
      while (a-- > 0) {
        Node c = popNode();
        c.jjtSetParent(n);
        n.jjtAddChild(c, a);
      }
      n.jjtClose();
      pushNode(n);
      node_created = true;
    } else {
      mk = marks.remove(marks.size()-1);
      node_created = false;
    }
  }
}

