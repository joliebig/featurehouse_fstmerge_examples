
package net.sourceforge.pmd.lang.ecmascript.ast;

import net.sourceforge.pmd.lang.ast.Node;

public interface EcmascriptNode extends Node {

    
    Object jjtAccept(EcmascriptParserVisitor visitor, Object data);

    
    Object childrenAccept(EcmascriptParserVisitor visitor, Object data);

    boolean hasSideEffects();
}
