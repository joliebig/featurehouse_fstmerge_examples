
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.InfixExpression;

public class ASTInfixExpression extends AbstractInfixEcmascriptNode<InfixExpression> {
    public ASTInfixExpression(InfixExpression infixExpression) {
	super(infixExpression);
    }

    
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
