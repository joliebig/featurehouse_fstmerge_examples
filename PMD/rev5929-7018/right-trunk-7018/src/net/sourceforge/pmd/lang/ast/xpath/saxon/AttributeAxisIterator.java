package net.sourceforge.pmd.lang.ast.xpath.saxon;

import net.sf.saxon.om.Navigator;
import net.sf.saxon.om.SequenceIterator;
import net.sourceforge.pmd.lang.ast.xpath.Attribute;


public class AttributeAxisIterator extends Navigator.BaseEnumeration {

    protected final ElementNode startNodeInfo;
    protected final net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator iterator;

    
    public AttributeAxisIterator(ElementNode startNodeInfo) {
	this.startNodeInfo = startNodeInfo;
	this.iterator = new net.sourceforge.pmd.lang.ast.xpath.AttributeAxisIterator(startNodeInfo.node);
    }

    
    public SequenceIterator getAnother() {
	return new AttributeAxisIterator(startNodeInfo);
    }

    
    public void advance() {
	if (this.iterator.hasNext()) {
	    Attribute attribute = this.iterator.next();
	    super.current = new AttributeNode(attribute, super.position());
	} else {
	    super.current = null;
	}
    }
}
