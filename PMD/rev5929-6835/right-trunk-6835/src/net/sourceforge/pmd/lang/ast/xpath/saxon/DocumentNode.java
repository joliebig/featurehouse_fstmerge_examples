package net.sourceforge.pmd.lang.ast.xpath.saxon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.Navigator;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SingleNodeIterator;
import net.sf.saxon.type.Type;
import net.sourceforge.pmd.lang.ast.Node;


public class DocumentNode extends AbstractNodeInfo implements DocumentInfo {

    
    protected final ElementNode rootNode;

    
    public final Map<Node, ElementNode> nodeToElementNode = new HashMap<Node, ElementNode>();

    
    public DocumentNode(Node node) {
	this.rootNode = new ElementNode(this, new IdGenerator(), null, node, -1);
    }

    
    public String[] getUnparsedEntity(String name) {
	throw createUnsupportedOperationException("DocumentInfo.getUnparsedEntity(String)");
    }

    
    public Iterator getUnparsedEntityNames() {
	throw createUnsupportedOperationException("DocumentInfo.getUnparsedEntityNames()");
    }

    
    public NodeInfo selectID(String id) {
	throw createUnsupportedOperationException("DocumentInfo.selectID(String)");
    }

    @Override
    public int getNodeKind() {
	return Type.DOCUMENT;
    }

    @Override
    public DocumentInfo getDocumentRoot() {
	return this;
    }

    @Override
    public boolean hasChildNodes() {
	return true;
    }

    @Override
    public AxisIterator iterateAxis(byte axisNumber) {
	switch (axisNumber) {
	case Axis.DESCENDANT:
	    return new Navigator.DescendantEnumeration(this, false, true);
	case Axis.DESCENDANT_OR_SELF:
	    return new Navigator.DescendantEnumeration(this, true, true);
	case Axis.CHILD:
	    return SingleNodeIterator.makeIterator(rootNode);
	default:
	    return super.iterateAxis(axisNumber);
	}
    }
}
