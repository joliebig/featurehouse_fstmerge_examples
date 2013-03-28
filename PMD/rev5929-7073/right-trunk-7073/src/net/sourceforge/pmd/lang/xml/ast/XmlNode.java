
package net.sourceforge.pmd.lang.xml.ast;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.xpath.AttributeNode;


public interface XmlNode extends Node, AttributeNode {
    
    org.w3c.dom.Node getNode();
}
