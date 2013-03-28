package net.sourceforge.pmd.lang.ast.xpath.saxon;

import net.sf.saxon.Configuration;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.om.Axis;
import net.sf.saxon.om.AxisIterator;
import net.sf.saxon.om.DocumentInfo;
import net.sf.saxon.om.FastStringBuffer;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SiblingCountingNode;
import net.sf.saxon.om.VirtualNode;
import net.sf.saxon.om.Navigator.AxisFilter;
import net.sf.saxon.pattern.NodeTest;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.Value;


public class AbstractNodeInfo implements VirtualNode, SiblingCountingNode {
    
    public String getSystemId() {
	throw createUnsupportedOperationException("Source.getSystemId()");
    }

    
    public void setSystemId(String systemId) {
	throw createUnsupportedOperationException("Source.setSystemId(String)");
    }

    
    public String getStringValue() {
	throw createUnsupportedOperationException("ValueRepresentation.getStringValue()");
    }

    
    public CharSequence getStringValueCS() {
	throw createUnsupportedOperationException("ValueRepresentation.getStringValueCS()");
    }

    
    public SequenceIterator getTypedValue() throws XPathException {
	throw createUnsupportedOperationException("Item.getTypedValue()");
    }

    
    public Object getUnderlyingNode() {
	throw createUnsupportedOperationException("VirtualNode.getUnderlyingNode()");
    }

    
    public int getSiblingPosition() {
	throw createUnsupportedOperationException("SiblingCountingNode.getSiblingPosition()");
    }

    
    public Value atomize() throws XPathException {
	throw createUnsupportedOperationException("NodeInfo.atomize()");
    }

    
    public int compareOrder(NodeInfo other) {
	throw createUnsupportedOperationException("NodeInfo.compareOrder(NodeInfo)");
    }

    
    public void copy(Receiver receiver, int whichNamespaces, boolean copyAnnotations, int locationId)
	    throws XPathException {
	throw createUnsupportedOperationException("ValueRepresentation.copy(Receiver, int, boolean, int)");
    }

    
    @Override
    public boolean equals(Object other) {
	if (this == other) {
	    return true;
	}
	if (other instanceof ElementNode) {
	    return this.getUnderlyingNode() == ((ElementNode) other).getUnderlyingNode();
	}
	return false;
    }

    
    public void generateId(FastStringBuffer buffer) {
	throw createUnsupportedOperationException("NodeInfo.generateId(FastStringBuffer)");
    }

    
    public String getAttributeValue(int fingerprint) {
	throw createUnsupportedOperationException("NodeInfo.getAttributeValue(int)");
    }

    
    public String getBaseURI() {
	throw createUnsupportedOperationException("NodeInfo.getBaseURI()");
    }

    
    public int getColumnNumber() {
	throw createUnsupportedOperationException("NodeInfo.getColumnNumber()");
    }

    
    public Configuration getConfiguration() {
	throw createUnsupportedOperationException("NodeInfo.getConfiguration()");
    }

    
    public int[] getDeclaredNamespaces(int[] buffer) {
	throw createUnsupportedOperationException("NodeInfo.getDeclaredNamespaces(int[])");
    }

    
    public String getDisplayName() {
	throw createUnsupportedOperationException("NodeInfo.getDisplayName()");
    }

    
    public int getDocumentNumber() {
	return 0;
    }

    
    public DocumentInfo getDocumentRoot() {
	throw createUnsupportedOperationException("NodeInfo.getDocumentRoot()");
    }

    
    public int getFingerprint() {
	throw createUnsupportedOperationException("NodeInfo.getFingerprint()");
    }

    
    public int getLineNumber() {
	throw createUnsupportedOperationException("NodeInfo.getLineNumber()");
    }

    
    public String getLocalPart() {
	throw createUnsupportedOperationException("NodeInfo.getLocalPart()");
    }

    
    public int getNameCode() {
	throw createUnsupportedOperationException("NodeInfo.getNameCode()");
    }

    
    public NamePool getNamePool() {
	throw createUnsupportedOperationException("NodeInfo.getNamePool()");
    }

    
    public int getNodeKind() {
	throw createUnsupportedOperationException("NodeInfo.getNodeKind()");
    }

    
    public NodeInfo getParent() {
	throw createUnsupportedOperationException("NodeInfo.getParent()");
    }

    
    public String getPrefix() {
	throw createUnsupportedOperationException("NodeInfo.getPrefix()");
    }

    
    public NodeInfo getRoot() {
	throw createUnsupportedOperationException("NodeInfo.getRoot()");
    }

    
    public int getTypeAnnotation() {
	throw createUnsupportedOperationException("NodeInfo.getTypeAnnotation()");
    }

    
    public String getURI() {
	throw createUnsupportedOperationException("NodeInfo.getURI()");
    }

    
    public boolean hasChildNodes() {
	throw createUnsupportedOperationException("NodeInfo.hasChildNodes()");
    }

    
    public boolean isId() {
	throw createUnsupportedOperationException("NodeInfo.isId()");
    }

    
    public boolean isIdref() {
	throw createUnsupportedOperationException("NodeInfo.isIdref()");
    }

    
    public boolean isNilled() {
	throw createUnsupportedOperationException("NodeInfo.isNilled()");
    }

    
    public boolean isSameNodeInfo(NodeInfo other) {
	return this.equals(other);
    }

    
    public AxisIterator iterateAxis(byte axisNumber) {
	throw createUnsupportedOperationException("NodeInfo.iterateAxis(byte) for axis '" + Axis.axisName[axisNumber]
		+ "'");
    }

    
    public AxisIterator iterateAxis(byte axisNumber, NodeTest nodeTest) {
	AxisIterator axisIterator = iterateAxis(axisNumber);
	if (nodeTest != null) {
	    axisIterator = new AxisFilter(axisIterator, nodeTest);
	}
	return axisIterator;
    }

    
    protected UnsupportedOperationException createUnsupportedOperationException(String name) {
	return new UnsupportedOperationException(name + " is not implemented by " + this.getClass().getName());
    }
}
