
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.Name;

public class ASTName extends AbstractEcmascriptNode<Name> {
    public ASTName(Name name) {
	super(name);
	super.setImage(name.getIdentifier());
    }

    
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public boolean isLocalName() {
	return node.isLocalName();
    }

    public boolean isGlobalName() {
	return !node.isLocalName();
    }

    
    public boolean isFunctionNodeName() {
	return jjtGetParent() instanceof ASTFunctionNode
		&& ((ASTFunctionNode) jjtGetParent()).getFunctionName() == this;
    }

    
    public boolean isFunctionNodeParameter() {
	if (jjtGetParent() instanceof ASTFunctionNode) {
	    ASTFunctionNode functionNode = (ASTFunctionNode) jjtGetParent();
	    for (int i = 0; i < functionNode.getNumParams(); i++) {
		if (functionNode.getParam(i) == this) {
		    return true;
		}
	    }
	}
	return false;
    }

    
    public boolean isFunctionCallName() {
	return jjtGetParent() instanceof ASTFunctionCall && ((ASTFunctionCall) jjtGetParent()).getTarget() == this;
    }

    
    public boolean isVariableDeclaration() {
	return jjtGetParent() instanceof ASTVariableInitializer
		&& ((ASTVariableInitializer) jjtGetParent()).getTarget() == this;
    }
}
