
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.Scope;

public class ASTScope extends AbstractEcmascriptNode<Scope> {
    public ASTScope(Scope scope) {
	super(scope);
    }

    
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
