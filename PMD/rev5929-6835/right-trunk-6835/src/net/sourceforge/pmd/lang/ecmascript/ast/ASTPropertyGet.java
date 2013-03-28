
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.PropertyGet;

public class ASTPropertyGet extends AbstractInfixEcmascriptNode<PropertyGet> {
    public ASTPropertyGet(PropertyGet propertyGet) {
	super(propertyGet, false);
	super.setImage(".");
    }

    
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
