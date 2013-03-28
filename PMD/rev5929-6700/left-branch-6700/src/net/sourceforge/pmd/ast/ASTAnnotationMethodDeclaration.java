

package net.sourceforge.pmd.ast;

public class ASTAnnotationMethodDeclaration extends SimpleJavaAccessNode {
  public ASTAnnotationMethodDeclaration(int id) {
    super(id);
  }

  public ASTAnnotationMethodDeclaration(JavaParser p, int id) {
    super(p, id);
  }


  
  public Object jjtAccept(JavaParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}

