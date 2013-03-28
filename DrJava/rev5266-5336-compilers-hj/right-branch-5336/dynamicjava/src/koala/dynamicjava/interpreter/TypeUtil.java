package koala.dynamicjava.interpreter;

import koala.dynamicjava.tree.Node;
import koala.dynamicjava.tree.Expression;
import koala.dynamicjava.tree.SourceInfo;
import koala.dynamicjava.tree.TypeName;
import koala.dynamicjava.tree.visitor.Visitor;

public class TypeUtil {
  
  
  
  public static Expression makeEmptyExpression() {
    return new Expression(SourceInfo.NONE) {
      public <T> T acceptVisitor(Visitor<T> v) { 
        throw new IllegalArgumentException("Cannot visit an empty expression");
      }
    };
  }

  
  public static Expression makeEmptyExpression(Node location) {
    return new Expression(location.getSourceInfo()) {
      public <T> T acceptVisitor(Visitor<T> v) { 
        throw new IllegalArgumentException("Cannot visit an empty expression");
      }
    };
  }
  
  public static TypeName makeEmptyTypeName() {
    return new TypeName(SourceInfo.NONE) {
      public <T> T acceptVisitor(Visitor<T> v) { 
        throw new IllegalArgumentException("Cannot visit an empty type name");
      }
    };
  }

  
  public static TypeName makeEmptyTypeName(Node location) {
    return new TypeName(location.getSourceInfo()) {
      public <T> T acceptVisitor(Visitor<T> v) { 
        throw new IllegalArgumentException("Cannot visit an empty type name");
      }
    };
  }

}
