package net.sourceforge.pmd.lang.jsp.ast;

import net.sourceforge.pmd.lang.ast.Node;

public interface JspNode extends Node {
    
    Object jjtAccept(JspParserVisitor visitor, Object data);

    
    Object childrenAccept(JspParserVisitor visitor, Object data);
}
