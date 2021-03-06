
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ParenthesizedExpression;

public class ASTParenthesizedExpression extends AbstractEcmascriptNode<ParenthesizedExpression> {
    public ASTParenthesizedExpression(ParenthesizedExpression parenthesizedExpression) {
	super(parenthesizedExpression);
    }

    
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
