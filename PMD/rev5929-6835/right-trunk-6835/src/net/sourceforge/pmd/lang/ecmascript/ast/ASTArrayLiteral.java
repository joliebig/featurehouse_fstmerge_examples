
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ArrayLiteral;

public class ASTArrayLiteral extends AbstractEcmascriptNode<ArrayLiteral> implements DestructuringNode {
    public ASTArrayLiteral(ArrayLiteral arrayLiteral) {
	super(arrayLiteral);
    }

    
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean isDestructuring() {
	return node.isDestructuring();
    }
}
