package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.Node;

public interface JavaNode extends Node {

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data);

    
    public Object childrenAccept(JavaParserVisitor visitor, Object data);
}
