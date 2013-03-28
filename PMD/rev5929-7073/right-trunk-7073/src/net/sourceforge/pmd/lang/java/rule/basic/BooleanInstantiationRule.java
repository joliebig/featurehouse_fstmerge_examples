package net.sourceforge.pmd.lang.java.rule.basic;

import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;


public class BooleanInstantiationRule extends AbstractJavaRule {

	
	private boolean customBoolean;

    @Override
    public Object visit(ASTCompilationUnit decl,Object data) {
        
        customBoolean = false;

        return super.visit(decl, data);
    }

	@Override
	public Object visit(ASTImportDeclaration decl,Object data) {
		
		if ( decl.getImportedName().endsWith("Boolean") && ! decl.getImportedName().equals("java.lang"))
		{
			customBoolean = true;
		}
		return super.visit(decl, data);
	}

    @Override
    public Object visit(ASTAllocationExpression node, Object data) {

    	if ( ! customBoolean ) {
	        if (node.hasDescendantOfType(ASTArrayDimsAndInits.class)) {
	            return super.visit(node, data);
	        }
	        if (TypeHelper.isA((ASTClassOrInterfaceType) node.jjtGetChild(0), Boolean.class)) {
                super.addViolation(data, node);
                return data;
            }
    	}
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTPrimaryPrefix node, Object data) {

    	if ( ! customBoolean )
    	{
	        if (node.jjtGetNumChildren() == 0 || !(node.jjtGetChild(0) instanceof ASTName)) {
	            return super.visit(node, data);
	        }

	        if ("Boolean.valueOf".equals(((ASTName) node.jjtGetChild(0)).getImage())
	                || "java.lang.Boolean.valueOf".equals(((ASTName) node.jjtGetChild(0)).getImage())) {
	            ASTPrimaryExpression parent = (ASTPrimaryExpression) node.jjtGetParent();
	            ASTPrimarySuffix suffix = parent.getFirstDescendantOfType(ASTPrimarySuffix.class);
	            if (suffix == null) {
	                return super.visit(node, data);
	            }
	            ASTPrimaryPrefix prefix = suffix.getFirstDescendantOfType(ASTPrimaryPrefix.class);
	            if (prefix == null) {
	                return super.visit(node, data);
	            }

	            if (prefix.hasDescendantOfType(ASTBooleanLiteral.class)) {
	                super.addViolation(data, node);
	                return data;
	            }
	            ASTLiteral literal = prefix.getFirstDescendantOfType(ASTLiteral.class);
	            if (literal != null && ("\"true\"".equals(literal.getImage()) || "\"false\"".equals(literal.getImage()))) {
	                super.addViolation(data, node);
	                return data;
	            }
	        }
    	}
        return super.visit(node, data);
    }
}
