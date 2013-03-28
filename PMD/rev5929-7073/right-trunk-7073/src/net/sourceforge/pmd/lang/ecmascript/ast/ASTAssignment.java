
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.Assignment;

public class ASTAssignment extends AbstractInfixEcmascriptNode<Assignment> {
    public ASTAssignment(Assignment asssignment) {
	super(asssignment);
    }

    
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
