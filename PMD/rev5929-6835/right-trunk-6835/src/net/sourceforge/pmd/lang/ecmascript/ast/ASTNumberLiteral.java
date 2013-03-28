
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.NumberLiteral;


public class ASTNumberLiteral extends AbstractEcmascriptNode<NumberLiteral> {
    public ASTNumberLiteral(NumberLiteral numberLiteral) {
	super(numberLiteral);
	super.setImage(numberLiteral.getValue());
    }

    
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public String getNormalizedImage() {
	
	return super.getImage();
    }

    public double getNumber() {
	return node.getNumber();
    }
}
