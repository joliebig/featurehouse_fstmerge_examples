
package net.sourceforge.pmd.lang.dfa;

import java.util.List;

import net.sourceforge.pmd.lang.DataFlowHandler;
import net.sourceforge.pmd.lang.ast.Node;


public class Linker {

    private final DataFlowHandler dataFlowHandler;
    private List<StackObject> braceStack;
    private List<StackObject> continueBreakReturnStack;

    public Linker(DataFlowHandler dataFlowHandler, List<StackObject> braceStack, List<StackObject> continueBreakReturnStack) {
	this.dataFlowHandler = dataFlowHandler;
	this.braceStack = braceStack;
	this.continueBreakReturnStack = continueBreakReturnStack;
    }

    
    public void computePaths() throws LinkerException, SequenceException {
	
	
	SequenceChecker sc = new SequenceChecker(braceStack);
	while (!sc.run()) {
	    if (sc.getFirstIndex() < 0 || sc.getLastIndex() < 0) {
		throw new SequenceException("computePaths(): return index <  0");
	    }

	    StackObject firstStackObject = braceStack.get(sc.getFirstIndex());

	    switch (firstStackObject.getType()) {
	    case NodeType.IF_EXPR:
		int x = sc.getLastIndex() - sc.getFirstIndex();
		if (x == 2) {
		    this.computeIf(sc.getFirstIndex(), sc.getFirstIndex() + 1, sc.getLastIndex());
		} else if (x == 1) {
		    this.computeIf(sc.getFirstIndex(), sc.getLastIndex());
		} else {
		    System.out.println("Error - computePaths 1");
		}
		break;

	    case NodeType.WHILE_EXPR:
		this.computeWhile(sc.getFirstIndex(), sc.getLastIndex());
		break;

	    case NodeType.SWITCH_START:
		this.computeSwitch(sc.getFirstIndex(), sc.getLastIndex());
		break;

	    case NodeType.FOR_INIT:
	    case NodeType.FOR_EXPR:
	    case NodeType.FOR_UPDATE:
	    case NodeType.FOR_BEFORE_FIRST_STATEMENT:
		this.computeFor(sc.getFirstIndex(), sc.getLastIndex());
		break;

	    case NodeType.DO_BEFORE_FIRST_STATEMENT:
		this.computeDo(sc.getFirstIndex(), sc.getLastIndex());
		break;

	    default:
	    }

	    for (int y = sc.getLastIndex(); y >= sc.getFirstIndex(); y--) {
		braceStack.remove(y);
	    }
	}

	while (!continueBreakReturnStack.isEmpty()) {
	    StackObject stackObject = continueBreakReturnStack.get(0);
	    DataFlowNode node = stackObject.getDataFlowNode();

	    switch (stackObject.getType()) {
	    case NodeType.THROW_STATEMENT:
		
	    case NodeType.RETURN_STATEMENT:
		
		node.removePathToChild(node.getChildren().get(0));
		DataFlowNode lastNode = node.getFlow().get(node.getFlow().size() - 1);
		node.addPathToChild(lastNode);
		continueBreakReturnStack.remove(0);
		break;
	    case NodeType.BREAK_STATEMENT:
		DataFlowNode last = getNodeToBreakStatement(node);
		node.removePathToChild(node.getChildren().get(0));
		node.addPathToChild(last);
		continueBreakReturnStack.remove(0);
		break;

	    case NodeType.CONTINUE_STATEMENT:
		
		
		
		
		
		continueBreakReturnStack.remove(0); 
		break;
		default:
		    
		    break;
	    }
	}
    }

    private DataFlowNode getNodeToBreakStatement(DataFlowNode node) {
	
	List<DataFlowNode> bList = node.getFlow();
	int findEnds = 1; 

	
	int index = bList.indexOf(node);
	for (; index < bList.size() - 2; index++) {
	    DataFlowNode n = bList.get(index);
	    if (n.isType(NodeType.DO_EXPR) || n.isType(NodeType.FOR_INIT) || n.isType(NodeType.WHILE_EXPR)
		    || n.isType(NodeType.SWITCH_START)) {
		findEnds++;
	    }
	    if (n.isType(NodeType.WHILE_LAST_STATEMENT) || n.isType(NodeType.SWITCH_END) || n.isType(NodeType.FOR_END)
		    || n.isType(NodeType.DO_EXPR)) {
		if (findEnds > 1) {
		    
		    findEnds--;
		} else {
		    break;
		}
	    }

	    if (n.isType(NodeType.LABEL_LAST_STATEMENT)) {
		Node parentNode = n.getNode().getFirstParentOfType(dataFlowHandler.getLabelStatementNodeClass());
		if (parentNode == null) {
		    break;
		} else {
		    String label = node.getNode().getImage();
		    if (label == null || label.equals(parentNode.getImage())) {
			node.removePathToChild(node.getChildren().get(0));
			DataFlowNode last = bList.get(index + 1);
			node.addPathToChild(last);
			break;
		    }
		}
	    }
	}
	return node.getFlow().get(index + 1);
    }

    private void computeDo(int first, int last) {
	DataFlowNode doSt = this.braceStack.get(first).getDataFlowNode();
	DataFlowNode doExpr = this.braceStack.get(last).getDataFlowNode();
	DataFlowNode doFirst = doSt.getFlow().get(doSt.getIndex() + 1);
	if (doFirst.getIndex() != doExpr.getIndex()) {
	    doExpr.addPathToChild(doFirst);
	}
    }

    private void computeFor(int firstIndex, int lastIndex) {
	DataFlowNode fExpr = null;
	DataFlowNode fUpdate = null;
	DataFlowNode fSt = null;
	DataFlowNode fEnd = null;
	boolean isUpdate = false;

	for (int i = firstIndex; i <= lastIndex; i++) {
	    StackObject so = this.braceStack.get(i);
	    DataFlowNode node = so.getDataFlowNode();

	    if (so.getType() == NodeType.FOR_EXPR) {
		fExpr = node;
	    } else if (so.getType() == NodeType.FOR_UPDATE) {
		fUpdate = node;
		isUpdate = true;
	    } else if (so.getType() == NodeType.FOR_BEFORE_FIRST_STATEMENT) {
		fSt = node;
	    } else if (so.getType() == NodeType.FOR_END) {
		fEnd = node;
	    }
	}
	DataFlowNode end = fEnd.getFlow().get(fEnd.getIndex() + 1);

	DataFlowNode firstSt = fSt.getChildren().get(0);

	if (isUpdate) {
	    if (fSt.getIndex() != fEnd.getIndex()) {
		end.reverseParentPathsTo(fUpdate);
		fExpr.removePathToChild(fUpdate);
		fUpdate.addPathToChild(fExpr);
		fUpdate.removePathToChild(firstSt);
		fExpr.addPathToChild(firstSt);
		fExpr.addPathToChild(end);
	    } else {
		fSt.removePathToChild(end);
		fExpr.removePathToChild(fUpdate);
		fUpdate.addPathToChild(fExpr);
		fExpr.addPathToChild(fUpdate);
		fExpr.addPathToChild(end);
	    }
	} else {
	    if (fSt.getIndex() != fEnd.getIndex()) {
		end.reverseParentPathsTo(fExpr);
		fExpr.addPathToChild(end);
	    }
	}
    }

    private void computeSwitch(int firstIndex, int lastIndex) {

	int diff = lastIndex - firstIndex;
	boolean defaultStatement = false;

	DataFlowNode sStart = this.braceStack.get(firstIndex).getDataFlowNode();
	DataFlowNode sEnd = this.braceStack.get(lastIndex).getDataFlowNode();
	DataFlowNode end = sEnd.getChildren().get(0);

	for (int i = 0; i < diff - 2; i++) {
	    StackObject so = this.braceStack.get(firstIndex + 2 + i);
	    DataFlowNode node = so.getDataFlowNode();

	    sStart.addPathToChild(node.getChildren().get(0));

	    if (so.getType() == NodeType.SWITCH_LAST_DEFAULT_STATEMENT) {
		defaultStatement = true;
	    }
	}

	if (!defaultStatement) {
	    sStart.addPathToChild(end);
	}
    }

    private void computeWhile(int first, int last) {
	DataFlowNode wStart = this.braceStack.get(first).getDataFlowNode();
	DataFlowNode wEnd = this.braceStack.get(last).getDataFlowNode();

	DataFlowNode end = wEnd.getFlow().get(wEnd.getIndex() + 1);

	if (wStart.getIndex() != wEnd.getIndex()) {
	    end.reverseParentPathsTo(wStart);
	    wStart.addPathToChild(end);
	}
    }

    private void computeIf(int first, int second, int last) {
	DataFlowNode ifStart = this.braceStack.get(first).getDataFlowNode();
	DataFlowNode ifEnd = this.braceStack.get(second).getDataFlowNode();
	DataFlowNode elseEnd = this.braceStack.get(last).getDataFlowNode();

	DataFlowNode elseStart = ifEnd.getFlow().get(ifEnd.getIndex() + 1);
	DataFlowNode end = elseEnd.getFlow().get(elseEnd.getIndex() + 1);

	
	if (ifStart.getIndex() != ifEnd.getIndex() && ifEnd.getIndex() != elseEnd.getIndex()) {
	    elseStart.reverseParentPathsTo(end);
	    ifStart.addPathToChild(elseStart);
	}
	
	else if (ifStart.getIndex() == ifEnd.getIndex() && ifEnd.getIndex() != elseEnd.getIndex()) {
	    ifStart.addPathToChild(end);
	}
	
	else if (ifEnd.getIndex() == elseEnd.getIndex() && ifStart.getIndex() != ifEnd.getIndex()) {
	    ifStart.addPathToChild(end);
	}
    }

    private void computeIf(int first, int last) {
	DataFlowNode ifStart = this.braceStack.get(first).getDataFlowNode();
	DataFlowNode ifEnd = this.braceStack.get(last).getDataFlowNode();

	
	if (ifStart.getIndex() != ifEnd.getIndex()) {
	    DataFlowNode end = ifEnd.getFlow().get(ifEnd.getIndex() + 1);
	    ifStart.addPathToChild(end);
	}
    }
}
