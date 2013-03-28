
package net.sourceforge.pmd.lang.dfa;


public class StackObject {

    private int type;
    private DataFlowNode node;

    public StackObject(int type, DataFlowNode node) {
        this.type = type;
        this.node = node;
    }

    public DataFlowNode getDataFlowNode() {
        return this.node;
    }

    public int getType() {
        return this.type;
    }
}
