package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.Comment;

public class ASTComment extends AbstractEcmascriptNode<Comment> {
    public ASTComment(Comment comment) {
	super(comment);
    }

    
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }

    
}
