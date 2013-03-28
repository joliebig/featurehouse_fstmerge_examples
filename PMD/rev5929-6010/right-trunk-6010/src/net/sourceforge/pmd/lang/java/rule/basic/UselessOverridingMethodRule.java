
package net.sourceforge.pmd.lang.java.rule.basic;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNameList;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

import org.jaxen.JaxenException;


public class UselessOverridingMethodRule extends AbstractJavaRule {
	private List<String> exceptions;
	private static final String CLONE = "clone";
	private static final String OBJECT = "Object";

	public UselessOverridingMethodRule()
	{
        exceptions = new ArrayList<String>(1);
        exceptions.add("CloneNotSupportedException");
	}

	public Object visit(ASTImplementsList clz, Object data)
	{
		return super.visit(clz,data);
	}

    public Object visit(ASTClassOrInterfaceDeclaration clz, Object data) {
        if (clz.isInterface()) {
            return data;
        }
        return super.visit(clz, data);
    }

    
    private boolean isMethodType(ASTMethodDeclaration node,String methodType)
    {
    	boolean result = false;
    	ASTResultType type = node.getResultType();
    	if ( type != null ) {
    		List results = null;
            try {
	            results = type.findChildNodesWithXPath("./Type/ReferenceType/ClassOrInterfaceType[@Image = '" + methodType + "']");
            }
            catch (JaxenException e) {
	            e.printStackTrace();
            }
    		if ( results != null && results.size() > 0 ) {
    			result = true;
    		}
    	}
    	return result;
    }

    
    private boolean isMethodThrowingType(ASTMethodDeclaration node, List<String> exceptedExceptions) {
    	boolean result = false;
	    ASTNameList thrownsExceptions = node.getFirstChildOfType(ASTNameList.class);
	    if ( thrownsExceptions != null ) {
	    	List<ASTName> names = thrownsExceptions.findChildrenOfType(ASTName.class);
	    	for ( ASTName name : names ) {
	    		for ( String exceptedException : exceptedExceptions) {
		    		if ( exceptedException.equals(name.getImage()) )
		    			result = true;
	    		}
	    	}
	    }
	    return result;
    }

	private boolean hasArguments(ASTMethodDeclaration node) {
		boolean result = false;
		try
		{
			List parameters = node.findChildNodesWithXPath("./MethodDeclarator/FormalParameters/*");
			if ( parameters != null && parameters.size() < 0 ) {
				result = true;
			}
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		return result;
	}

    public Object visit(ASTMethodDeclaration node, Object data) {
        
        
        
        if (node.isAbstract() || node.isFinal() || node.isNative() || node.isSynchronized()) {
            return super.visit(node, data);
        }
        
        
        
        if ( CLONE.equals(node.getMethodName()) && node.isPublic() &&
        	 ! this.hasArguments(node) &&
        	 this.isMethodType(node, OBJECT) &&
        	 this.isMethodThrowingType(node,exceptions) )
        {
        	return super.visit(node,data);
        }

        ASTBlock block = node.getBlock();
        if (block == null) {
            return super.visit(node, data);
        }
        
        if (block.jjtGetNumChildren() != 1 || block.findChildrenOfType(ASTStatement.class).size() != 1)
            return super.visit(node, data);

        ASTStatement statement = (ASTStatement) block.jjtGetChild(0).jjtGetChild(0);
        if (statement.jjtGetChild(0).jjtGetNumChildren() == 0) {
            return data;     
        }
        Node statementGrandChild = statement.jjtGetChild(0).jjtGetChild(0);
        ASTPrimaryExpression primaryExpression;

        if (statementGrandChild instanceof ASTPrimaryExpression)
            primaryExpression = (ASTPrimaryExpression) statementGrandChild;
        else {
            List<ASTPrimaryExpression> primaryExpressions = findFirstDegreeChildrenOfType(statementGrandChild, ASTPrimaryExpression.class);
            if (primaryExpressions.size() != 1)
                return super.visit(node, data);
            primaryExpression = primaryExpressions.get(0);
        }

        ASTPrimaryPrefix primaryPrefix = findFirstDegreeChildrenOfType(primaryExpression, ASTPrimaryPrefix.class).get(0);
        if (!primaryPrefix.usesSuperModifier())
            return super.visit(node, data);

        ASTMethodDeclarator methodDeclarator = findFirstDegreeChildrenOfType(node, ASTMethodDeclarator.class).get(0);
        if (!primaryPrefix.hasImageEqualTo(methodDeclarator.getImage()))
            return super.visit(node, data);

        
        ASTPrimarySuffix primarySuffix = findFirstDegreeChildrenOfType(primaryExpression, ASTPrimarySuffix.class).get(0);
        ASTArguments arguments = (ASTArguments) primarySuffix.jjtGetChild(0);
        ASTFormalParameters formalParameters = (ASTFormalParameters) methodDeclarator.jjtGetChild(0);
        if (formalParameters.jjtGetNumChildren() != arguments.jjtGetNumChildren())
            return super.visit(node, data);

        if (arguments.jjtGetNumChildren() == 0) 
            addViolation(data, node, getMessage());
        else {
            ASTArgumentList argumentList = (ASTArgumentList) arguments.jjtGetChild(0);
            for (int i = 0; i < argumentList.jjtGetNumChildren(); i++) {
                Node ExpressionChild = argumentList.jjtGetChild(i).jjtGetChild(0);
                if (!(ExpressionChild instanceof ASTPrimaryExpression) || ExpressionChild.jjtGetNumChildren() != 1)
                    return super.visit(node, data); 

                ASTPrimaryExpression argumentPrimaryExpression = (ASTPrimaryExpression) ExpressionChild;
                ASTPrimaryPrefix argumentPrimaryPrefix = (ASTPrimaryPrefix) argumentPrimaryExpression.jjtGetChild(0);
                if (argumentPrimaryPrefix.jjtGetNumChildren() == 0) {
                    return super.visit(node, data); 
                }
                Node argumentPrimaryPrefixChild = argumentPrimaryPrefix.jjtGetChild(0);
                if (!(argumentPrimaryPrefixChild instanceof ASTName))
                    return super.visit(node, data); 

                if (formalParameters.jjtGetNumChildren() < i + 1) {
                    return super.visit(node, data); 
                }

                ASTName argumentName = (ASTName) argumentPrimaryPrefixChild;
                ASTFormalParameter formalParameter = (ASTFormalParameter) formalParameters.jjtGetChild(i);
                ASTVariableDeclaratorId variableId = findFirstDegreeChildrenOfType(formalParameter, ASTVariableDeclaratorId.class).get(0);
                if (!argumentName.hasImageEqualTo(variableId.getImage())) {
                    return super.visit(node, data); 
                }

            }
            addViolation(data, node, getMessage()); 
        }
        return super.visit(node, data);
    }



	public <T> List<T> findFirstDegreeChildrenOfType(Node n, Class<T> targetType) {
        List<T> l = new ArrayList<T>();
        lclFindChildrenOfType(n, targetType, l);
        return l;
    }

    private <T> void lclFindChildrenOfType(Node node, Class<T> targetType, List<T> results) {
        if (node.getClass().equals(targetType)) {
            results.add((T) node);
        }

        if (node instanceof ASTClassOrInterfaceDeclaration && ((ASTClassOrInterfaceDeclaration) node).isNested()) {
            return;
        }

        if (node instanceof ASTClassOrInterfaceBodyDeclaration && ((ASTClassOrInterfaceBodyDeclaration) node).isAnonymousInnerClass()) {
            return;
        }

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child.getClass().equals(targetType)) {
                results.add((T) child);
            }
        }
    }
}
