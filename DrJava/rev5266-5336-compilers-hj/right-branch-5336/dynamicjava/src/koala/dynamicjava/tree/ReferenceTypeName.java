

package koala.dynamicjava.tree;

import java.util.*;

import koala.dynamicjava.tree.visitor.*;



public class ReferenceTypeName extends TypeName {
  
  
  
  
  private String representation;
  
  
  private List<? extends IdentifierToken> identifiers;
  

  
  public ReferenceTypeName(List<? extends IdentifierToken> ids) {
    this(ids, SourceInfo.NONE);
  }
  
  public ReferenceTypeName(IdentifierToken... ids) {
    this(Arrays.asList(ids));
  }

  public ReferenceTypeName(String... names) {
    this(stringsToIdentifiers(names));
  }
  
  public ReferenceTypeName(String[] names, SourceInfo si) {
    this(Arrays.asList(stringsToIdentifiers(names)), si);
  }

  private static IdentifierToken[] stringsToIdentifiers(String[] names) {
    IdentifierToken[] ids = new IdentifierToken[names.length];
    for (int i = 0; i < names.length; i++) {
      ids[i] = new Identifier(names[i]);
    }
    return ids;
  }
  
  
  public ReferenceTypeName(List<? extends IdentifierToken> ids, SourceInfo si) {
    super(si);

    if (ids == null) throw new IllegalArgumentException("ids == null");
    if (ids.size() == 0) throw new IllegalArgumentException("ids.size() == 0");
    identifiers = ids;
    representation = TreeUtilities.listToName(ids);
  }

  
  public String getRepresentation() {
    return representation;
  }
  
  
  public List<? extends IdentifierToken> getIdentifiers() {
    return identifiers;
  }

  
  public void setIdentifiers(List<? extends IdentifierToken> ids) {
    if (ids == null) throw new IllegalArgumentException("ids == null");
    if (ids.size() == 0) throw new IllegalArgumentException("ids.size() == 0");
    identifiers = ids;
    representation = TreeUtilities.listToName(ids);
  }

  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
    
  public String toString() {
    return "("+getClass().getName()+": "+toStringHelper()+")";
  }

  protected String toStringHelper() {
   return getRepresentation();
  }
}
