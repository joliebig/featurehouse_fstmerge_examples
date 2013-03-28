
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.Token;
import org.mozilla.javascript.ast.KeywordLiteral;

public class ASTKeywordLiteral extends AbstractEcmascriptNode<KeywordLiteral> {
    public ASTKeywordLiteral(KeywordLiteral keywordLiteral) {
	super(keywordLiteral);
	super.setImage(Token.typeToName(keywordLiteral.getType()).toLowerCase());
    }

    
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean isBoolean() {
	return node.isBooleanLiteral();
    }
    
    public boolean isThis() {
	return node.getType() == Token.THIS;
    }
    
    public boolean isNull() {
	return node.getType() == Token.NULL;
    }
    
    public boolean isDebugger() {
	return node.getType() == Token.DEBUGGER;
    }
}
