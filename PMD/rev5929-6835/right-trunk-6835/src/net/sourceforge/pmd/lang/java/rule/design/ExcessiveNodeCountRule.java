
package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractStatisticalJavaRule;
import net.sourceforge.pmd.stat.DataPoint;



public class ExcessiveNodeCountRule extends AbstractStatisticalJavaRule {
    private Class<?> nodeClass;

    public ExcessiveNodeCountRule(Class<?> nodeClass) {
	this.nodeClass = nodeClass;
    }

    @Override
    public Object visit(JavaNode node, Object data) {
	int numNodes = 0;

	for (int i = 0; i < node.jjtGetNumChildren(); i++) {
	    Integer treeSize = (Integer) ((JavaNode) node.jjtGetChild(i)).jjtAccept(this, data);
	    numNodes += treeSize;
	}

	if (nodeClass.isInstance(node)) {
	    DataPoint point = new DataPoint();
	    point.setNode(node);
	    point.setScore(1.0 * numNodes);
	    point.setMessage(getMessage());
	    addDataPoint(point);
	}

	return Integer.valueOf(numNodes);
    }
}
