package net.sourceforge.pmd.lang.java.rule.codesize;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractStatisticalJavaRule;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;


public class NPathComplexityRule extends AbstractStatisticalJavaRule {

    private int complexityMultipleOf(JavaNode node, int npathStart, Object data) {

	int npath = npathStart;
	JavaNode n;

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    n = (JavaNode) node.jjtGetChild(i);
	    npath *= (Integer) n.jjtAccept(this, data);
	}

	return npath;
    }

    private int complexitySumOf(JavaNode node, int npathStart, Object data) {

	int npath = npathStart;
	JavaNode n;

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    n = (JavaNode) node.jjtGetChild(i);
	    npath += (Integer) n.jjtAccept(this, data);
	}

	return npath;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
	int npath = complexityMultipleOf(node, 1, data);

	DataPoint point = new DataPoint();
	point.setNode(node);
	point.setScore(1.0 * npath);
	point.setMessage(getMessage());
	addDataPoint(point);

	return Integer.valueOf(npath);
    }

    @Override
    public Object visit(JavaNode node, Object data) {
	int npath = complexityMultipleOf(node, 1, data);
	return Integer.valueOf(npath);
    }

    @Override
    public Object visit(ASTIfStatement node, Object data) {
	

	int boolCompIf = sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

	int complexity = 0;

	List<JavaNode> statementChildren = new ArrayList<JavaNode>();
	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    if (node.jjtGetChild(i).getClass() == ASTStatement.class) {
		statementChildren.add((JavaNode) node.jjtGetChild(i));
	    }
	}

	if (statementChildren.isEmpty() || statementChildren.size() == 1 && node.hasElse()
		|| statementChildren.size() != 1 && !node.hasElse()) {
	    throw new IllegalStateException("If node has wrong number of children");
	}

	
	if (!node.hasElse()) {
	    complexity++;
	}

	for (JavaNode element : statementChildren) {
	    complexity += (Integer) element.jjtAccept(this, data);
	}

	return Integer.valueOf(boolCompIf + complexity);
    }

    @Override
    public Object visit(ASTWhileStatement node, Object data) {
	

	int boolCompWhile = sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

	Integer nPathWhile = (Integer) ((JavaNode) node.getFirstChildOfType(ASTStatement.class)).jjtAccept(this, data);

	return Integer.valueOf(boolCompWhile + nPathWhile + 1);
    }

    @Override
    public Object visit(ASTDoStatement node, Object data) {
	

	int boolCompDo = sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

	Integer nPathDo = (Integer) ((JavaNode) node.getFirstChildOfType(ASTStatement.class)).jjtAccept(this, data);

	return Integer.valueOf(boolCompDo + nPathDo + 1);
    }

    @Override
    public Object visit(ASTForStatement node, Object data) {
	

	int boolCompFor = sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

	Integer nPathFor = (Integer) ((JavaNode) node.getFirstChildOfType(ASTStatement.class)).jjtAccept(this, data);

	return Integer.valueOf(boolCompFor + nPathFor + 1);
    }

    @Override
    public Object visit(ASTReturnStatement node, Object data) {
	

	ASTExpression expr = node.getFirstChildOfType(ASTExpression.class);

	if (expr == null) {
	    return NumericConstants.ONE;
	}

	List<ASTConditionalAndExpression> andNodes = expr.findChildrenOfType(ASTConditionalAndExpression.class);
	List<ASTConditionalOrExpression> orNodes = expr.findChildrenOfType(ASTConditionalOrExpression.class);
	int boolCompReturn = andNodes.size() + orNodes.size();

	if (boolCompReturn > 0) {
	    return Integer.valueOf(boolCompReturn);
	}
	return NumericConstants.ONE;
    }

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
	

	int boolCompSwitch = sumExpressionComplexity(node.getFirstChildOfType(ASTExpression.class));

	int npath = 0;
	int caseRange = 0;
	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    JavaNode n = (JavaNode) node.jjtGetChild(i);

	    
	    if (n instanceof ASTSwitchLabel) {
		npath += caseRange;
		caseRange = 1;
	    } else {
		Integer complexity = (Integer) n.jjtAccept(this, data);
		caseRange *= complexity;
	    }
	}
	
	npath += caseRange;
	return Integer.valueOf(boolCompSwitch + npath);
    }

    @Override
    public Object visit(ASTTryStatement node, Object data) {
	
	int npath = complexitySumOf(node, 0, data);

	return Integer.valueOf(npath);

    }

    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
	if (node.isTernary()) {
	    int npath = complexitySumOf(node, 0, data);

	    npath += 2;
	    return Integer.valueOf(npath);
	}
	return NumericConstants.ONE;
    }

    
    public static int sumExpressionComplexity(ASTExpression expr) {
	if (expr == null) {
	    return 0;
	}

	List<ASTConditionalAndExpression> andNodes = expr.findChildrenOfType(ASTConditionalAndExpression.class);
	List<ASTConditionalOrExpression> orNodes = expr.findChildrenOfType(ASTConditionalOrExpression.class);

	int children = 0;

	for (ASTConditionalOrExpression element : orNodes) {
	    children += element.jjtGetNumChildren();
	    children--;
	}

	for (ASTConditionalAndExpression element : andNodes) {
	    children += element.jjtGetNumChildren();
	    children--;
	}

	return children;
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
	return new String[] { ((ASTMethodDeclaration) point.getNode()).getMethodName(),
		String.valueOf((int) point.getScore()) };
    }
}
