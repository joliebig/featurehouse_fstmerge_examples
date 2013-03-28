
package net.sourceforge.pmd.lang.ecmascript.ast;

import net.sourceforge.pmd.lang.ast.Node;

import org.mozilla.javascript.ast.AstNode;

public interface EcmascriptNode<T extends AstNode> extends Node {

    
    Object jjtAccept(EcmascriptParserVisitor visitor, Object data);

    
    Object childrenAccept(EcmascriptParserVisitor visitor, Object data);
    
    
    T getNode();
    
    
    String getJsDoc();

    boolean hasSideEffects();
}
