
package net.sourceforge.pmd.lang.dfa;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.ast.Node;


public class Structure {

    private final DataFlowHandler dataFlowHandler;
    private LinkedList<DataFlowNode> dataFlow = new LinkedList<DataFlowNode>();
    private Stack<StackObject> braceStack = new Stack<StackObject>();
    private Stack<StackObject> continueBreakReturnStack = new Stack<StackObject>();
    
    public Structure(DataFlowHandler dataFlowHandler) {
	this.dataFlowHandler = dataFlowHandler;
    }

    
    public DataFlowNode createNewNode(Node node) {
	return dataFlowHandler.createDataFlowNode(dataFlow, node);
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

    
    

    
    public void pushOnStack(int type, DataFlowNode node) {
	StackObject obj = new StackObject(type, node);
	if (type == NodeType.RETURN_STATEMENT || type == NodeType.BREAK_STATEMENT
		|| type == NodeType.CONTINUE_STATEMENT || type == NodeType.THROW_STATEMENT) {
	    
	    continueBreakReturnStack.push(obj);
	} else {
	    braceStack.push(obj);
	}
	node.setType(type);
    }

    public List<StackObject> getBraceStack() {
	return braceStack;
    }

    public List<StackObject> getContinueBreakReturnStack() {
	return continueBreakReturnStack;
    }

}
