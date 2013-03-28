
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.stat.StatisticalRule;



public class ExcessiveNodeCountRule extends StatisticalRule {
    private Class nodeClass;

    public ExcessiveNodeCountRule(Class nodeClass) {
        this.nodeClass = nodeClass;
    }

    public Object visit(SimpleJavaNode node, Object data) {
        int numNodes = 0;

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Integer treeSize = (Integer) ((SimpleJavaNode) node.jjtGetChild(i)).jjtAccept(this, data);
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
