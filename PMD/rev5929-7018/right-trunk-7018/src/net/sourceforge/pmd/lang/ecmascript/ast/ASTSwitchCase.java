
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.SwitchCase;

public class ASTSwitchCase extends AbstractEcmascriptNode<SwitchCase> {
    public ASTSwitchCase(SwitchCase switchCase) {
	super(switchCase);
    }

    
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean isDefault() {
	return node.isDefault();
    }

    public EcmascriptNode getExpression() {
	if (!isDefault()) {
	    return (EcmascriptNode) jjtGetChild(0);
	} else {
	    return null;
	}
    }

    public int getNumStatements() {
	
	return node.getStatements() != null ? node.getStatements().size() : 0;
    }

    public EcmascriptNode getStatement(int index) {
	if (!isDefault()) {
	    index++;
	}
	return (EcmascriptNode) jjtGetChild(index);
    }
}
