

package net.sourceforge.pmd.jsp.ast;



public interface Node extends net.sourceforge.pmd.ast.Node {


    
    public Object jjtAccept(JspParserVisitor visitor, Object data);
}
