
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTReferenceType;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.properties.IntegerProperty;
import net.sourceforge.pmd.symboltable.ClassScope;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;



public class CouplingBetweenObjects extends AbstractRule {

    private int couplingCount;
    private Set<String> typesFoundSoFar;

    private static final PropertyDescriptor thresholdDescriptor = new IntegerProperty(
    	"threshold", "Coupling threshold value", 2, 1.0f
    	);
    
    private static final Map<String, PropertyDescriptor> propertyDescriptorsByName = asFixedMap(thresholdDescriptor);
        
    
    public Object visit(ASTCompilationUnit cu, Object data) {
        typesFoundSoFar = new HashSet<String>();
        couplingCount = 0;

        Object returnObj = cu.childrenAccept(this, data);

        if (couplingCount > getIntProperty(thresholdDescriptor)) {
            addViolation(data, cu, "A value of " + couplingCount + " may denote a high amount of coupling within the class");
        }

        return returnObj;
    }

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    public Object visit(ASTResultType node, Object data) {
        for (int x = 0; x < node.jjtGetNumChildren(); x++) {
            SimpleNode tNode = (SimpleNode) node.jjtGetChild(x);
            if (tNode instanceof ASTType) {
                SimpleNode reftypeNode = (SimpleNode) tNode.jjtGetChild(0);
                if (reftypeNode instanceof ASTReferenceType) {
                    SimpleNode classOrIntType = (SimpleNode) reftypeNode.jjtGetChild(0);
                    if (classOrIntType instanceof ASTClassOrInterfaceType) {
                        SimpleNode nameNode = classOrIntType;
                        this.checkVariableType(nameNode, nameNode.getImage());
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        handleASTTypeChildren(node);
        return super.visit(node, data);
    }

    public Object visit(ASTFormalParameter node, Object data) {
        handleASTTypeChildren(node);
        return super.visit(node, data);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        for (int x = 0; x < node.jjtGetNumChildren(); ++x) {
            SimpleNode firstStmt = (SimpleNode) node.jjtGetChild(x);
            if (firstStmt instanceof ASTType) {
                ASTType tp = (ASTType) firstStmt;
                SimpleNode nd = (SimpleNode) tp.jjtGetChild(0);
                checkVariableType(nd, nd.getImage());
            }
        }

        return super.visit(node, data);
    }

    
    private void handleASTTypeChildren(SimpleNode node) {
        for (int x = 0; x < node.jjtGetNumChildren(); x++) {
            SimpleNode sNode = (SimpleNode) node.jjtGetChild(x);
            if (sNode instanceof ASTType) {
                SimpleNode nameNode = (SimpleNode) sNode.jjtGetChild(0);
                checkVariableType(nameNode, nameNode.getImage());
            }
        }
    }

    
    private void checkVariableType(SimpleNode nameNode, String variableType) {
        
        if (nameNode.getParentsOfType(ASTClassOrInterfaceDeclaration.class).isEmpty()) {
            return;
        }
        
        
        ClassScope clzScope = nameNode.getScope().getEnclosingClassScope();
        if (!clzScope.getClassName().equals(variableType) && (!this.filterTypes(variableType)) && !this.typesFoundSoFar.contains(variableType)) {
            couplingCount++;
            typesFoundSoFar.add(variableType);
        }
    }

    
    private boolean filterTypes(String variableType) {
        return variableType != null && (variableType.startsWith("java.lang.") || (variableType.equals("String")) || filterPrimitivesAndWrappers(variableType));
    }

    
    private boolean filterPrimitivesAndWrappers(String variableType) {
        return (variableType.equals("int") || variableType.equals("Integer") || variableType.equals("char") || variableType.equals("Character") || variableType.equalsIgnoreCase("double") || variableType.equalsIgnoreCase("long") || variableType.equalsIgnoreCase("short") || variableType.equalsIgnoreCase("float") || variableType.equalsIgnoreCase("byte") || variableType.equalsIgnoreCase("boolean"));
    }
    
    
    protected Map<String, PropertyDescriptor> propertiesByName() {
    	return propertyDescriptorsByName;
    }
}
