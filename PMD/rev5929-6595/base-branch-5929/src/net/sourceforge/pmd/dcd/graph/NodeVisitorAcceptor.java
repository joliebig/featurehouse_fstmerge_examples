
package net.sourceforge.pmd.dcd.graph;


public interface NodeVisitorAcceptor {
	Object accept(NodeVisitor visitor, Object data);
}
