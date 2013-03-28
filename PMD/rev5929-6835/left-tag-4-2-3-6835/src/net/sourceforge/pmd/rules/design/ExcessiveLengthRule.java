
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.stat.StatisticalRule;


public class ExcessiveLengthRule extends StatisticalRule {
    private Class nodeClass;

    public ExcessiveLengthRule(Class nodeClass) {
        this.nodeClass = nodeClass;
    }

    public Object visit(SimpleJavaNode node, Object data) {
        if (nodeClass.isInstance(node)) {
            DataPoint point = new DataPoint();
            point.setNode(node);
            point.setScore(1.0 * (node.getEndLine() - node.getBeginLine()));
            point.setMessage(getMessage());
            addDataPoint(point);
        }

        return node.childrenAccept(this, data);
    }
}


