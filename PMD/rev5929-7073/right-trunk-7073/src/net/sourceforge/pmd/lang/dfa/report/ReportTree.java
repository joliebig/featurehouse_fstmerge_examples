package net.sourceforge.pmd.lang.dfa.report;

import java.util.Iterator;

import net.sourceforge.pmd.RuleViolation;

public class ReportTree {

    private PackageNode rootNode = new PackageNode("");
    private AbstractReportNode level;

    private class TreeIterator implements Iterator<RuleViolation> {

	private AbstractReportNode iterNode = rootNode;
	private boolean hasNextFlag;

	public void remove() {
	    throw new UnsupportedOperationException();
	}

	public boolean hasNext() {
	    this.hasNextFlag = true;
	    return this.getNext() != null;
	}

	public RuleViolation next() {
	    if (!this.hasNextFlag) {
		this.getNext();
	    } else {
		this.hasNextFlag = false;
	    }

	    if (this.iterNode instanceof ViolationNode) {
		return ((ViolationNode) this.iterNode).getRuleViolation();
	    }
	    return null;
	}

	

	private AbstractReportNode getNext() {
	    AbstractReportNode node;

	    while (true) {
		if (this.iterNode.isLeaf()) {

		    while ((node = this.iterNode.getNextSibling()) == null) {

			node = this.iterNode.getParent();
			if (node == null) {
			    return null;
			} else {
			    this.iterNode = node;
			}
		    }

		    this.iterNode = node;
		    if (this.iterNode.isLeaf()) {
			return this.iterNode;
		    } else {
			continue;
		    }
		} else {
		    this.iterNode = this.iterNode.getFirstChild();
		    if (this.iterNode.isLeaf()) {
			return this.iterNode;
		    } else {
			continue;
		    }
		}
	    }
	}
    }

    public Iterator<RuleViolation> iterator() {
	return new TreeIterator();
    }

    public int size() {
	int count = 0;
	for (Iterator<RuleViolation> i = iterator(); i.hasNext();) {
	    i.next();
	    count++;
	}
	return count;
    }

    public AbstractReportNode getRootNode() {
	return rootNode;
    }

    
    public void addRuleViolation(RuleViolation violation) {
	String packageName = violation.getPackageName();
	if (packageName == null) {
	    packageName = "";
	}

	this.level = this.rootNode;

	int endIndex = packageName.indexOf('.');
	while (true) {
	    String parentPackage;
	    if (endIndex < 0) {
		parentPackage = packageName;
	    } else {
		parentPackage = packageName.substring(0, endIndex);
	    }

	    if (!this.isStringInLevel(parentPackage)) {
		PackageNode node = new PackageNode(parentPackage);
		this.level.addFirst(node);
		
		this.level = node;
	    }

	    if (endIndex < 0) {
		break;
	    }
	    endIndex = packageName.indexOf('.', endIndex + 1);
	}

	String cl = violation.getClassName();

	if (!this.isStringInLevel(cl)) {
	    ClassNode node = new ClassNode(cl);
	    this.level.addFirst(node);
	    
	    this.level = node;
	}

	
	ViolationNode tmp = new ViolationNode(violation);
	if (!this.equalsNodeInLevel(this.level, tmp)) {
	    this.level.add(tmp);
	}
    }

    
    private boolean equalsNodeInLevel(AbstractReportNode level, AbstractReportNode node) {
	for (int i = 0; i < level.getChildCount(); i++) {
	    if (level.getChildAt(i).equalsNode(node)) {
		return true;
	    }
	}
	return false;
    }

    
    private boolean isStringInLevel(String str) {

	for (int i = 0; i < this.level.getChildCount(); i++) {
	    final AbstractReportNode child = this.level.getChildAt(i);
	    final String tmp;
	    if (child instanceof PackageNode) {
		tmp = ((PackageNode) child).getPackageName();
	    } else if (child instanceof ClassNode) {
		tmp = ((ClassNode) child).getClassName();
	    } else {
		return false;
	    }

	    if (tmp != null && tmp.equals(str)) {
		
		this.level = child;
		return true;
	    }
	}
	return false;
    }

}
