package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.stat.DataPoint;


public class NcssMethodCountRule extends AbstractNcssCountRule {

    
    public NcssMethodCountRule() {
	super(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
	return super.visit(node, data);
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
	return new String[] { ((ASTMethodDeclaration) point.getNode()).getMethodName(),
		String.valueOf((int) point.getScore()) };
    }
}
