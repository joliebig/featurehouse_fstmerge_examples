
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.ObjectLiteral;

public class ASTObjectLiteral extends AbstractEcmascriptNode<ObjectLiteral> implements DestructuringNode {
    public ASTObjectLiteral(ObjectLiteral objectLiteral) {
	super(objectLiteral);
    }

    
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public ASTObjectProperty getObjectProperty(int index) {
	return (ASTObjectProperty) jjtGetChild(index);
    }

    public boolean isDestructuring() {
	return node.isDestructuring();
    }
}
