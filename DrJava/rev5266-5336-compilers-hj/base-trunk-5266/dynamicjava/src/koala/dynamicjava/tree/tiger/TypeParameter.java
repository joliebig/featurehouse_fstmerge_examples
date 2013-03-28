


package koala.dynamicjava.tree.tiger;

import koala.dynamicjava.tree.ReferenceTypeName;
import koala.dynamicjava.tree.IdentifierToken;
import koala.dynamicjava.tree.SourceInfo;
import java.util.List;


public class TypeParameter extends ReferenceTypeName {
  private final ReferenceTypeName _bound;
  private final List<ReferenceTypeName> _interfaceBounds;

  
  public TypeParameter(List<IdentifierToken> ids, ReferenceTypeName in_bound,
                       List<ReferenceTypeName> in_interfaceBounds, SourceInfo in_sourceInfo) {
    super(ids, in_sourceInfo);

    if (in_bound == null) {
      throw new IllegalArgumentException("Parameter 'bound' to the TypeParameter constructor was null.");
    }
    if (in_interfaceBounds == null) {
      throw new IllegalArgumentException("Parameter 'interfaceBounds' to the TypeParameter constructor was null.");
    }
    _bound = in_bound;
    _interfaceBounds = in_interfaceBounds;
}

  public ReferenceTypeName getBound() { return _bound; }

  public List<ReferenceTypeName> getInterfaceBounds() { return _interfaceBounds; }

  public String getName(){
    return super.getRepresentation();
  }

  public String toString() {
    return "("+getClass().getName()+": "+toStringHelper()+")";
  }

  protected String toStringHelper() {
    return getName()+" "+getBound()+" "+getInterfaceBounds();
  }
}
