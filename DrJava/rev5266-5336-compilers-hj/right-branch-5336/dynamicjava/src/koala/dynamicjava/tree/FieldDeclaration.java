

package koala.dynamicjava.tree;

import koala.dynamicjava.tree.visitor.*;



public class FieldDeclaration extends Declaration {
  
  private TypeName type;

  
  private String name;

  
  private Expression initializer;

  
  public FieldDeclaration(ModifierSet mods, TypeName type, String name, Expression init) {
    this(mods, type, name, init, SourceInfo.NONE);
  }

  
  public FieldDeclaration(ModifierSet mods, TypeName type, String name, Expression init,
                          SourceInfo si) {
    super(mods, si);

    if (type == null) throw new IllegalArgumentException("type == null");
    if (name == null) throw new IllegalArgumentException("name == null");
    this.type   = type;
    this.name   = name;
    initializer = init;

    if (type instanceof ArrayTypeName) {
      if (initializer instanceof ArrayInitializer) {
        ((ArrayInitializer)initializer).setElementType
          (((ArrayTypeName)type).getElementType());
      }
    }
  }

  
  public TypeName getType() {
    return type;
  }

  
  public void setType(TypeName t) {
    if (t == null) throw new IllegalArgumentException("t == null");
    type = t;
  }

  
  public String getName() {
    return name;
  }

  
  public void setName(String s) {
    if (s == null) throw new IllegalArgumentException("s == null");
    name = s;
  }

  
  public Expression getInitializer() {
    return initializer;
  }

  
  public void setInitializer(Expression e) {
    initializer = e;
  }

  
  public <T> T acceptVisitor(Visitor<T> visitor) {
    return visitor.visit(this);
  }
   
  public String toString() {
    return "("+getClass().getName()+": "+getModifiers()+" "+getType()+" "+getName()+" "+getInitializer()+")";
  }
}
