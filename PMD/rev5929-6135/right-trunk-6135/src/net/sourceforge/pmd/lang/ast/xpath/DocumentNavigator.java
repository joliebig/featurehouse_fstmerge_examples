
package net.sourceforge.pmd.lang.ast.xpath;

import java.util.ArrayList;
import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;

import org.jaxen.DefaultNavigator;
import org.jaxen.XPath;
import org.jaxen.util.SingleObjectIterator;


public class DocumentNavigator extends DefaultNavigator {

    private final static Iterator<Node> EMPTY_ITERATOR = new ArrayList<Node>().iterator();

    public String getAttributeName(Object arg0) {
	return ((Attribute) arg0).getName();
    }

    public String getAttributeNamespaceUri(Object arg0) {
	return "";
    }

    public String getAttributeQName(Object arg0) {
	return ((Attribute) arg0).getName();
    }

    public String getAttributeStringValue(Object arg0) {
	return ((Attribute) arg0).getValue();
    }

    public String getCommentStringValue(Object arg0) {
	return "";
    }

    public String getElementName(Object node) {
	return node.toString();
    }

    public String getElementNamespaceUri(Object arg0) {
	return "";
    }

    public String getElementQName(Object arg0) {
	return getElementName(arg0);
    }

    public String getElementStringValue(Object arg0) {
	return "";
    }

    public String getNamespacePrefix(Object arg0) {
	return "";
    }

    public String getNamespaceStringValue(Object arg0) {
	return "";
    }

    public String getTextStringValue(Object arg0) {
	return "";
    }

    public boolean isAttribute(Object arg0) {
	return arg0 instanceof Attribute;
    }

    public boolean isComment(Object arg0) {
	return false;
    }

    public boolean isDocument(Object arg0) {
	return arg0 instanceof RootNode;
    }

    public boolean isElement(Object arg0) {
	return arg0 instanceof Node;
    }

    public boolean isNamespace(Object arg0) {
	return false;
    }

    public boolean isProcessingInstruction(Object arg0) {
	return false;
    }

    public boolean isText(Object arg0) {
	return false;
    }

    public XPath parseXPath(String arg0) {
	return null;
    }

    @Override
    public Object getParentNode(Object arg0) {
	if (arg0 instanceof Node) {
	    return ((Node) arg0).jjtGetParent();
	}
	return ((Attribute) arg0).getParent();
    }

    @Override
    public Iterator<Attribute> getAttributeAxisIterator(Object arg0) {
	return new AttributeAxisIterator((Node) arg0);
    }

    
    @Override
    public Iterator<Node> getChildAxisIterator(Object contextNode) {
	return new NodeIterator((Node) contextNode) {
	    @Override
	    protected Node getFirstNode(Node node) {
		return getFirstChild(node);
	    }

	    @Override
	    protected Node getNextNode(Node node) {
		return getNextSibling(node);
	    }
	};
    }

    
    @Override
    public Iterator<Node> getParentAxisIterator(Object contextNode) {
	if (isAttribute(contextNode)) {
	    return new SingleObjectIterator(((Attribute) contextNode).getParent());
	}
	Node parent = ((Node) contextNode).jjtGetParent();
	if (parent != null) {
	    return new SingleObjectIterator(parent);
	} else {
	    return EMPTY_ITERATOR;
	}
    }

    
    @Override
    public Iterator<Node> getFollowingSiblingAxisIterator(Object contextNode) {
	return new NodeIterator((Node) contextNode) {
	    @Override
	    protected Node getFirstNode(Node node) {
		return getNextNode(node);
	    }

	    @Override
	    protected Node getNextNode(Node node) {
		return getNextSibling(node);
	    }
	};
    }

    
    @Override
    public Iterator<Node> getPrecedingSiblingAxisIterator(Object contextNode) {
	return new NodeIterator((Node) contextNode) {
	    @Override
	    protected Node getFirstNode(Node node) {
		return getNextNode(node);
	    }

	    @Override
	    protected Node getNextNode(Node node) {
		return getPreviousSibling(node);
	    }
	};
    }

    
    @Override
    public Iterator<Node> getFollowingAxisIterator(Object contextNode) {
	return new NodeIterator((Node) contextNode) {
	    @Override
	    protected Node getFirstNode(Node node) {
		if (node == null) {
		    return null;
		} else {
		    Node sibling = getNextSibling(node);
		    if (sibling == null) {
			return getFirstNode(node.jjtGetParent());
		    } else {
			return sibling;
		    }
		}
	    }

	    @Override
	    protected Node getNextNode(Node node) {
		if (node == null) {
		    return null;
		} else {
		    Node n = getFirstChild(node);
		    if (n == null) {
			n = getNextSibling(node);
		    }
		    if (n == null) {
			return getFirstNode(node.jjtGetParent());
		    } else {
			return n;
		    }
		}
	    }
	};
    }

    
    @Override
    public Iterator<Node> getPrecedingAxisIterator(Object contextNode) {
	return new NodeIterator((Node) contextNode) {
	    @Override
	    protected Node getFirstNode(Node node) {
		if (node == null) {
		    return null;
		} else {
		    Node sibling = getPreviousSibling(node);
		    if (sibling == null) {
			return getFirstNode(node.jjtGetParent());
		    } else {
			return sibling;
		    }
		}
	    }

	    @Override
	    protected Node getNextNode(Node node) {
		if (node == null) {
		    return null;
		} else {
		    Node n = getLastChild(node);
		    if (n == null) {
			n = getPreviousSibling(node);
		    }
		    if (n == null) {
			return getFirstNode(node.jjtGetParent());
		    } else {
			return n;
		    }
		}
	    }
	};
    }

    @Override
    public Object getDocumentNode(Object contextNode) {
	if (isDocument(contextNode)) {
	    return contextNode;
	}
	return getDocumentNode(getParentNode(contextNode));
    }
}
