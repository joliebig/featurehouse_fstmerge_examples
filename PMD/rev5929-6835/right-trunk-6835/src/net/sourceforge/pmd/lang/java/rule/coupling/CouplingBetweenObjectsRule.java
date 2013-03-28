
package net.sourceforge.pmd.lang.java.rule.coupling;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;



public class CouplingBetweenObjectsRule extends AbstractJavaRule {

    private int couplingCount;
    private Set<String> typesFoundSoFar;

    private static final IntegerProperty THRESHOLD_DESCRIPTOR = new IntegerProperty(
    	"threshold", "Unique type reporting threshold", 2, 100, 20, 1.0f
    	);

    public CouplingBetweenObjectsRule() {
	definePropertyDescriptor(THRESHOLD_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTCompilationUnit cu, Object data) {
        typesFoundSoFar = new HashSet<String>();
        couplingCount = 0;

        Object returnObj = cu.childrenAccept(this, data);

        if (couplingCount > getProperty(THRESHOLD_DESCRIPTOR)) {
            addViolation(data, cu, "A value of " + couplingCount + " may denote a high amount of coupling within the class");
        }

        return returnObj;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTResultType node, Object data) {
        for (int x = 0; x < node.jjtGetNumChildren(); x++) {
            Node tNode = node.jjtGetChild(x);
            if (tNode instanceof ASTType) {
        	Node reftypeNode = tNode.jjtGetChild(0);
                if (reftypeNode instanceof ASTReferenceType) {
                    Node classOrIntType = reftypeNode.jjtGetChild(0);
                    if (classOrIntType instanceof ASTClassOrInterfaceType) {
                	Node nameNode = classOrIntType;
                        this.checkVariableType(nameNode, nameNode.getImage());
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        handleASTTypeChildren(node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFormalParameter node, Object data) {
        handleASTTypeChildren(node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        for (int x = 0; x < node.jjtGetNumChildren(); ++x) {
            Node firstStmt = node.jjtGetChild(x);
            if (firstStmt instanceof ASTType) {
                ASTType tp = (ASTType) firstStmt;
                Node nd = tp.jjtGetChild(0);
                checkVariableType(nd, nd.getImage());
            }
        }

        return super.visit(node, data);
    }

    
    private void handleASTTypeChildren(Node node) {
        for (int x = 0; x < node.jjtGetNumChildren(); x++) {
            Node sNode = node.jjtGetChild(x);
            if (sNode instanceof ASTType) {
        	Node nameNode = sNode.jjtGetChild(0);
                checkVariableType(nameNode, nameNode.getImage());
            }
        }
    }

    
    private void checkVariableType(Node nameNode, String variableType) {
        
        if (nameNode.getParentsOfType(ASTClassOrInterfaceDeclaration.class).isEmpty()) {
            return;
        }
        
        
        ClassScope clzScope = ((JavaNode)nameNode).getScope().getEnclosingClassScope();
        if (!clzScope.getClassName().equals(variableType) && !this.filterTypes(variableType) && !this.typesFoundSoFar.contains(variableType)) {
            couplingCount++;
            typesFoundSoFar.add(variableType);
        }
    }

    
    private boolean filterTypes(String variableType) {
        return variableType != null && (variableType.startsWith("java.lang.") || variableType.equals("String") || filterPrimitivesAndWrappers(variableType));
    }

    
    private boolean filterPrimitivesAndWrappers(String variableType) {
        return variableType.equals("int") || variableType.equals("Integer") || variableType.equals("char") || variableType.equals("Character") || variableType.equalsIgnoreCase("double") || variableType.equalsIgnoreCase("long") || variableType.equalsIgnoreCase("short") || variableType.equalsIgnoreCase("float") || variableType.equalsIgnoreCase("byte") || variableType.equalsIgnoreCase("boolean");
    }
}
