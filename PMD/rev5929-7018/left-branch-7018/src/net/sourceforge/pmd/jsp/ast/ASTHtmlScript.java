

package net.sourceforge.pmd.jsp.ast;

public class ASTHtmlScript extends SimpleNode {
  public ASTHtmlScript(int id) {
    super(id);
  }

  public ASTHtmlScript(JspParser p, int id) {
    super(p, id);
  }


  
  public Object jjtAccept(JspParserVisitor visitor, Object data) {
    return visitor.visit(this, data);
  }
}

