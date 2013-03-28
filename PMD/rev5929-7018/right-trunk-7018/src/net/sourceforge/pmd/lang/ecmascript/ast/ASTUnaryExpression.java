
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.UnaryExpression;

public class ASTUnaryExpression extends AbstractEcmascriptNode<UnaryExpression> {
    public ASTUnaryExpression(UnaryExpression unaryExpression) {
	super(unaryExpression);
	super.setImage(AstRoot.operatorToString(unaryExpression.getOperator()));
    }

    
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean isPrefix() {
	return node.isPrefix();
    }

    public boolean isPostfix() {
	return node.isPostfix();
    }
}
