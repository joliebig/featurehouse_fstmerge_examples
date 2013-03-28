

package koala.dynamicjava.tree.tiger;


import koala.dynamicjava.tree.*;
import koala.dynamicjava.tree.visitor.Visitor;

import java.util.List;



public class GenericReferenceTypeName extends ReferenceTypeName {
  
  private List<? extends List<? extends TypeName>> _typeArguments;
  
  
  public GenericReferenceTypeName(List<? extends IdentifierToken> ids, List<? extends List<? extends TypeName>> typeArgs) {
    this(ids, typeArgs, SourceInfo.NONE);
  }
  









  
  
  public GenericReferenceTypeName(List<? extends IdentifierToken> ids, List<? extends List<? extends TypeName>> typeArgs,
                                  SourceInfo si) {
    super(ids, si);
    if (ids.size() != typeArgs.size()) { throw new IllegalArgumentException("ids.size() != typeArgs.size()"); }
    _typeArguments = typeArgs;
  }
  

  public List<? extends List<? extends TypeName>> getTypeArguments(){ return _typeArguments; }
  
  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }

  public String toString() {
    return "("+getClass().getName()+": "+toStringHelper()+")";
  }
  
  public String toStringHelper() {
    String typeArgS = "";
    List<? extends List<? extends TypeName>> alltas = getTypeArguments();
    for( List<? extends TypeName> ta : alltas ){
      if(ta.size()>0)
        typeArgS = ""+ta.get(0);
      for(int i = 1; i < ta.size(); i++)
        typeArgS += " " + ta.get(i);
      typeArgS += ":";
    }    
    return super.toStringHelper()+" "+typeArgS;
  }
  
}
