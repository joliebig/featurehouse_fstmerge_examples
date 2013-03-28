

package koala.dynamicjava.tree;

import java.util.*;
import koala.dynamicjava.tree.visitor.*;



public class AmbiguousName extends PrimaryExpression implements LeftHandSide {

  
  private List<IdentifierToken> identifiers;

  
  private String representation;

  
  public AmbiguousName(List<IdentifierToken> ids) {
    this(ids, SourceInfo.NONE);
  }
  
  public AmbiguousName(IdentifierToken... ids) {
    this(Arrays.asList(ids), SourceInfo.NONE);
  }
  
  public AmbiguousName(String... ids) {
    this(makeIdList(ids), SourceInfo.NONE);
  }
  
  private static List<IdentifierToken> makeIdList(String... ids) {
    List<IdentifierToken> result = new LinkedList<IdentifierToken>();
    for (String id : ids) { result.add(new Identifier(id)); }
    return result;
  }

  
  public AmbiguousName(List<IdentifierToken> ids, SourceInfo si) {
    super(si);

    if (ids == null) throw new IllegalArgumentException("ids == null");
    if (ids.size() == 0) throw new IllegalArgumentException("ids.size() == 0");

    identifiers    = ids;
    representation = TreeUtilities.listToName(ids);
  }

  
  public String getRepresentation() {
    return representation;
  }

  
  public List<IdentifierToken> getIdentifiers() {
    return identifiers;
  }

  
  public void setIdentifier(List<IdentifierToken> l) {
    if (l == null) throw new IllegalArgumentException("l == null");
    identifiers = l;
    representation = TreeUtilities.listToName(l);
  }

  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
  
  
  public String toString() {
    return "("+getClass().getName()+": "+getRepresentation()+")";
  }
}
