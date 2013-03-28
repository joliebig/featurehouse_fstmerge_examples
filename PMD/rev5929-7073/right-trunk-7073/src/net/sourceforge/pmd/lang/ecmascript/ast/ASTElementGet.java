
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ElementGet;

public class ASTElementGet extends AbstractEcmascriptNode<ElementGet> {
    public ASTElementGet(ElementGet elementGet) {
	super(elementGet);
    }

    
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public EcmascriptNode getTarget() {
	return (EcmascriptNode) node.getTarget();
    }

    public EcmascriptNode getElement() {
	return (EcmascriptNode) node.getElement();
    }
}
