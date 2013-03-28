

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public class ImportDeclaration extends Node {
  
  private String name;

  
  private boolean pckage;

  private boolean sttic;
  
  
  public ImportDeclaration(List<IdentifierToken> ident, boolean pkg, boolean sttc) {
    this(ident, pkg, sttc, SourceInfo.NONE);
  }

  
  public ImportDeclaration(List<IdentifierToken> ident, boolean pkg, boolean sttc,
                           SourceInfo si) {
    super(si);

    if (ident == null) throw new IllegalArgumentException("ident == null");
    pckage     = pkg;
    sttic      = sttc;
    name       = TreeUtilities.listToName(ident);
  }

  
  public ImportDeclaration(String nm, boolean pkg, boolean sttc,
                           SourceInfo si) {
    super(si);

    if (nm == null) throw new IllegalArgumentException("name == null");

    pckage     = pkg;
    sttic      = sttc;
    name       = nm;
  }

  
  public String getName() {
    return name;
  }

  
  public void setName(String s) {
    if (s == null) throw new IllegalArgumentException("s == null");
    name = s;
  }

  
  public boolean isPackage() {
    return pckage;
  }

  
  public boolean isStaticImportClass() {
    return sttic && pckage;
  }
  
  
  public void setPackage(boolean b) {
    pckage = b;
  }

  
  public boolean isStatic() {
    return sttic;
  }

  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+toStringHelper()+")";
  }
  
  public String toStringHelper(){
    return getName()+" "+isPackage()+" "+isStatic();
  }
}
