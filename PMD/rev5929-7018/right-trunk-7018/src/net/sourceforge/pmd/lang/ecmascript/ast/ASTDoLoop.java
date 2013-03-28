
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.DoLoop;

public class ASTDoLoop extends AbstractEcmascriptNode<DoLoop> {
    public ASTDoLoop(DoLoop doLoop) {
	super(doLoop);
    }

    
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public EcmascriptNode getBody() {
	return (EcmascriptNode) jjtGetChild(0);
    }

    public EcmascriptNode getCondition() {
	return (EcmascriptNode) jjtGetChild(1);
    }
}
