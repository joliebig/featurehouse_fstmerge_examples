
package net.sourceforge.pmd.lang.ecmascript.ast;

import org.mozilla.javascript.ast.Block;

public class ASTBlock extends AbstractEcmascriptNode<Block> {
    public ASTBlock(Block block) {
	super(block);
    }

    
    @Override
    public Object jjtAccept(EcmascriptParserVisitor visitor, Object data) {
	return visitor.visit(this, data);
    }
}
