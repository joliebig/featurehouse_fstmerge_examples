

package net.sourceforge.pmd.lang.ast;

import java.util.List;

import net.sourceforge.pmd.lang.dfa.DataFlowNode;

import org.jaxen.JaxenException;
import org.w3c.dom.Document;



public interface Node {

    
    void jjtOpen();

    
    void jjtClose();

    
    void jjtSetParent(Node parent);

    Node jjtGetParent();

    
    void jjtAddChild(Node child, int index);

    
    Node jjtGetChild(int index);

    
    int jjtGetNumChildren();

    int jjtGetId();

    String getImage();

    void setImage(String image);

    boolean hasImageEqualTo(String image);

    int getBeginLine();

    int getBeginColumn();

    int getEndLine();

    int getEndColumn();

    DataFlowNode getDataFlowNode();

    void setDataFlowNode(DataFlowNode dataFlowNode);

    boolean isFindBoundary();

    Node getNthParent(int n);

    <T> T getFirstParentOfType(Class<T> parentType);

    <T> List<T> getParentsOfType(Class<T> parentType);

    
    <T> List<T> findChildrenOfType(Class<T> childType);

    
    <T> List<T> findDescendantsOfType(Class<T> targetType);

    
    <T> void findDescendantsOfType(Class<T> targetType, List<T> results, boolean crossFindBoundaries);

    
    <T> T getFirstChildOfType(Class<T> childType);

    
    <T> T getFirstDescendantOfType(Class<T> descendantType);

    
    <T> boolean hasDescendantOfType(Class<T> type);

    List<Node> findChildNodesWithXPath(String xpathString) throws JaxenException;

    
    Document getAsXml();
}
