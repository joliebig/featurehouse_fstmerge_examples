package net.sourceforge.pmd.lang.dfa.report;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractReportNode {
    private List<AbstractReportNode> childNodes = new ArrayList<AbstractReportNode>();
    private AbstractReportNode parentNode = null;

    
    private int numberOfViolations;

    
    public abstract boolean equalsNode(AbstractReportNode arg0);

    
    public AbstractReportNode getFirstChild() {
        if (this.isLeaf()) {
            return null;
        }
        return this.childNodes.get(0);
    }

    
    public AbstractReportNode getNextSibling() {
        if (this.parentNode == null) {
            return null;
        }
        int index = this.parentNode.getChildIndex(this);
        if (index < 0) {
            return null;
        }
        if (index >= this.parentNode.childNodes.size() - 1) {
            return null;
        }
        return this.parentNode.childNodes.get(index + 1);
    }

    
    private int getChildIndex(AbstractReportNode child) {
        for (int i = 0; i < this.childNodes.size(); i++) {
            if (this.childNodes.get(i).equals(child)) {
                return i;
            }
        }
        return -1;
    }

    
    public void addFirst(AbstractReportNode child) {
        this.childNodes.add(0, child);
        child.parentNode = this;
    }

    
    public void add(AbstractReportNode child) {
        this.childNodes.add(child);
        child.parentNode = this;
    }

    public void addNumberOfViolation(int number) {
        this.numberOfViolations += number;
    }

    
    public int getNumberOfViolations() {
        return numberOfViolations;
    }

    
    
    public void childrenAccept(ReportVisitor visitor) {
        for (int i = 0; i < childNodes.size(); i++) {
            AbstractReportNode node = childNodes.get(i);
            node.accept(visitor);
        }
    }

    public void accept(ReportVisitor visitor) {
        visitor.visit(this);
    }

    public AbstractReportNode getChildAt(int arg0) {
        if (arg0 >= 0 && arg0 <= this.childNodes.size() - 1) {
            return this.childNodes.get(arg0);
        }
        return null;
    }

    public int getChildCount() {
        return this.childNodes.size();
    }

    public AbstractReportNode getParent() {
        return this.parentNode;
    }

    public boolean isLeaf() {
        return this.childNodes.isEmpty();
    }

}
