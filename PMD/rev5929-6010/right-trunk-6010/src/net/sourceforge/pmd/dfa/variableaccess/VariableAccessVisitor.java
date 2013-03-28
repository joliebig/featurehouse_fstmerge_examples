
package net.sourceforge.pmd.dfa.variableaccess;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.dfa.DataFlowNode;
import net.sourceforge.pmd.dfa.StartOrEndDataFlowNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;


public class VariableAccessVisitor extends JavaParserVisitorAdapter {

    public void compute(ASTMethodDeclaration node) {
        if (node.jjtGetParent() instanceof ASTClassOrInterfaceBodyDeclaration) {
            this.computeNow(node);
        }
    }

    public void compute(ASTConstructorDeclaration node) {
        this.computeNow(node);
    }

    private void computeNow(Node node) {
	DataFlowNode inode = node.getDataFlowNode();

        List<VariableAccess> undefinitions = markUsages(inode);

        
        DataFlowNode firstINode = inode.getFlow().get(0);
        firstINode.setVariableAccess(undefinitions);

        
        DataFlowNode lastINode = inode.getFlow().get(inode.getFlow().size() - 1);
        lastINode.setVariableAccess(undefinitions);
    }

    private List<VariableAccess> markUsages(DataFlowNode inode) {
        
        List<VariableAccess> undefinitions = new ArrayList<VariableAccess>();
        Set<Map<VariableNameDeclaration, List<NameOccurrence>>> variableDeclarations = collectDeclarations(inode);
        for (Map<VariableNameDeclaration, List<NameOccurrence>> declarations: variableDeclarations) {
            for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry: declarations.entrySet()) {
                VariableNameDeclaration vnd = entry.getKey();

                if (vnd.getAccessNodeParent() instanceof ASTFormalParameter) {
                    
                    continue;
                } else if (((Node)vnd.getAccessNodeParent()).getFirstChildOfType(ASTVariableInitializer.class) != null) {
                    
                    addVariableAccess(
                            vnd.getNode(), 
                            new VariableAccess(VariableAccess.DEFINITION, vnd.getImage()), 
                            inode.getFlow());                    
                }
                undefinitions.add(new VariableAccess(VariableAccess.UNDEFINITION, vnd.getImage()));

                for (NameOccurrence occurrence: entry.getValue()) {
                    addAccess(occurrence, inode);
                }
            }
        }
        return undefinitions;
    }

    private Set<Map<VariableNameDeclaration, List<NameOccurrence>>> collectDeclarations(DataFlowNode inode) {
        Set<Map<VariableNameDeclaration, List<NameOccurrence>>> decls = new HashSet<Map<VariableNameDeclaration, List<NameOccurrence>>>();
        Map<VariableNameDeclaration, List<NameOccurrence>> varDecls;
        for (int i = 0; i < inode.getFlow().size(); i++) {
            DataFlowNode n = inode.getFlow().get(i);
            if (n instanceof StartOrEndDataFlowNode) {
                continue;
            }
            varDecls = n.getNode().getScope().getVariableDeclarations();
            if (!decls.contains(varDecls)) {
                decls.add(varDecls);
            }
        }
        return decls;
    }

    private void addAccess(NameOccurrence occurrence, DataFlowNode inode) {
        if (occurrence.isOnLeftHandSide()) {
            this.addVariableAccess(occurrence.getLocation(), new VariableAccess(VariableAccess.DEFINITION, occurrence.getImage()), inode.getFlow());
        } else if (occurrence.isOnRightHandSide() || (!occurrence.isOnLeftHandSide() && !occurrence.isOnRightHandSide())) {
            this.addVariableAccess(occurrence.getLocation(), new VariableAccess(VariableAccess.REFERENCING, occurrence.getImage()), inode.getFlow());
        }
    }

    
    private void addVariableAccess(Node node, VariableAccess va, List flow) {
        
        for (int i = flow.size()-1; i > 0; i--) { 
            DataFlowNode inode = (DataFlowNode) flow.get(i);
            if (inode.getNode() == null) {
                continue;
            }

            List<? extends Node> children = inode.getNode().findChildrenOfType(node.getClass());
            for (Node n: children) {
                if (node.equals(n)) { 
                    List<VariableAccess> v = new ArrayList<VariableAccess>();
                    v.add(va);
                    inode.setVariableAccess(v);     
                    return;
                }
            }
        }
    }

}
