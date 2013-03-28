
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ThrowStatement;

public class ASTThrowStatement extends AbstractEcmascriptNode<ThrowStatement> {
    public ASTThrowStatement(ThrowStatement throwStatement) {
	super(throwStatement);
    }

    
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}