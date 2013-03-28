
package net.sourceforge.pmd.dfa;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.JavaDataFlowNode;



public class Structure {

    private LinkedList<DataFlowNode> dataFlow = new LinkedList<DataFlowNode>();
    private Stack<StackObject> braceStack = new Stack<StackObject>();
    private Stack<StackObject> continueBreakReturnStack = new Stack<StackObject>();

    
    public DataFlowNode createNewNode(Node node) {
	
        return new JavaDataFlowNode(this.dataFlow, node);
    }

    public DataFlowNode createStartNode(int line) {
        return new StartOrEndDataFlowNode(this.dataFlow, line, true);
    }

    public DataFlowNode createEndNode(int line) {
        return new StartOrEndDataFlowNode(this.dataFlow, line, false);
    }

    public DataFlowNode getLast() {
        return this.dataFlow.getLast();
    }

    public DataFlowNode getFirst() {
        return this.dataFlow.getFirst();
    }




    
    protected void pushOnStack(int type, DataFlowNode node) {
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
