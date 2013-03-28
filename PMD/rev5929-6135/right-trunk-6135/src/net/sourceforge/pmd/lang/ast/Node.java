

package net.sourceforge.pmd.lang.ast;

import java.util.List;

import net.sourceforge.pmd.dfa.DataFlowNode;
import net.sourceforge.pmd.symboltable.Scope;

import org.jaxen.JaxenException;
import org.w3c.dom.Document;



public interface Node {

    
    public void jjtOpen();

    
    public void jjtClose();

    
    public void jjtSetParent(Node parent);

    public Node jjtGetParent();

    
    public void jjtAddChild(Node child, int index);

    
    public Node jjtGetChild(int index);

    
    public int jjtGetNumChildren();

    public int jjtGetId();

    String getImage();

    void setImage(String image);

    boolean hasImageEqualTo(String image);

    int getBeginLine();

    int getBeginColumn();

    int getEndLine();

    int getEndColumn();

    Scope getScope();

    void setScope(Scope scope);

    DataFlowNode getDataFlowNode();

    void setDataFlowNode(DataFlowNode dataFlowNode);

    boolean isFindBoundary();

    Node getNthParent(int n);

    <T> T getFirstParentOfType(Class<T> parentType);

    <T> List<T> getParentsOfType(Class<T> parentType);

    <T> List<T> findChildrenOfType(Class<T> targetType);

    <T> void findChildrenOfType(Class<T> targetType, List<T> results);

    <T> void findChildrenOfType(Class<T> targetType, List<T> results, boolean crossFindBoundaries);

    
    <T> T getFirstChildOfType(Class<T> childType);

    
    <T> boolean containsChildOfType(Class<T> type);

    List findChildNodesWithXPath(String xpathString) throws JaxenException;

    
    Document getAsXml();
}
