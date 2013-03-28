package net.sourceforge.pmd.ast;

public interface JavaNode extends Node {

    
    public Object jjtAccept(JavaParserVisitor visitor, Object data);
}
