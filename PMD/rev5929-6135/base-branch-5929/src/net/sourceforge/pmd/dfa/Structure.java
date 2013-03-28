
package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.ast.SimpleNode;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;



public class Structure {

    private LinkedList<DataFlowNode> dataFlow = new LinkedList<DataFlowNode>();
    private Stack<StackObject> braceStack = new Stack<StackObject>();
    private Stack<StackObject> continueBreakReturnStack = new Stack<StackObject>();

    
    public IDataFlowNode createNewNode(SimpleNode node) {
        return new DataFlowNode(node, this.dataFlow);
    }

    public IDataFlowNode createStartNode(int line) {
        return new StartOrEndDataFlowNode(this.dataFlow, line, true);
    }

    public IDataFlowNode createEndNode(int line) {
        return new StartOrEndDataFlowNode(this.dataFlow, line, false);
    }

    public IDataFlowNode getLast() {
        return this.dataFlow.getLast();
    }

    public IDataFlowNode getFirst() {
        return this.dataFlow.getFirst();
    }




    
    protected void pushOnStack(int type, IDataFlowNode node) {
        StackObject obj = new StackObject(type, node);
        if (type == NodeType.RETURN_STATEMENT
        		|| type == NodeType.BREAK_STATEMENT
        		|| type == NodeType.CONTINUE_STATEMENT
        		|| type == NodeType.THROW_STATEMENT) {
            
            continueBreakReturnStack.push(obj);
        } else {
            braceStack.push(obj);
        }
        ((DataFlowNode) node).setType(type);
    }

    public List getBraceStack() {
        return braceStack;
    }

    public List getContinueBreakReturnStack() {
        return continueBreakReturnStack;
    }

}
