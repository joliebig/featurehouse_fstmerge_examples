
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.FunctionNode;

public class ASTFunctionNode extends AbstractEcmascriptNode<FunctionNode> {
    public ASTFunctionNode(FunctionNode functionNode) {
	super(functionNode);
	super.setImage(functionNode.getName());
    }

    
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    public int getNumParams() {
	return node.getParams().size();
    }

    public ASTName getFunctionName() {
	if (node.getFunctionName() != null) {
	    return (ASTName) jjtGetChild(0);
	}
	return null;
    }

    public EcmascriptNode getParam(int index) {
	if (node.getFunctionName() != null) {
	    index++;
	}
	return (EcmascriptNode) jjtGetChild(index);
    }

    public EcmascriptNode getBody(int index) {
	return (EcmascriptNode) jjtGetChild(jjtGetNumChildren() - 1);
    }

    public boolean isClosure() {
	return node.isExpressionClosure();
    }

    public boolean isGetter() {
	return node.isGetter();
    }

    public boolean isSetter() {
	return node.isSetter();
    }

    public boolean isGetterOrSetter() {
	return node.isGetterOrSetter();
    }
}
