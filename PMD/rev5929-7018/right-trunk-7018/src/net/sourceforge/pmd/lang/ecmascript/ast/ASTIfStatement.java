
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.IfStatement;

public class ASTIfStatement extends AbstractEcmascriptNode<IfStatement> {
    public ASTIfStatement(IfStatement ifStatement) {
	super(ifStatement);
    }

    
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean hasElse() {
	return node.getElsePart() != null;
    }

    public EcmascriptNode getCondition() {
	return (EcmascriptNode) jjtGetChild(0);
    }

    public EcmascriptNode getThen() {
	return (EcmascriptNode) jjtGetChild(1);
    }

    public EcmascriptNode getElse() {
	return (EcmascriptNode) jjtGetChild(2);
    }
}
