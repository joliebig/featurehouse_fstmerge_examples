
package net.sourceforge.pmd.lang.java.typeresolution.rules;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTImplementsList;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;


public class SignatureDeclareThrowsException extends AbstractJavaRule {
	
    private static final BooleanProperty IGNORE_JUNIT_COMPLETELY_DESCRIPTOR = new BooleanProperty("IgnoreJUnitCompletely",
        "Allow all methods in a JUnit testcase to throw Exceptions", false, 1.0f);

    
    private boolean junitImported = false;
    
    public SignatureDeclareThrowsException() {
	definePropertyDescriptor(IGNORE_JUNIT_COMPLETELY_DESCRIPTOR);
    }
    
    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (junitImported == true) {
	    return super.visit(node, data);
	}

        ASTImplementsList impl = node.getFirstChildOfType(ASTImplementsList.class);
        if (impl != null && impl.jjtGetParent().equals(node)) {
            for (int ix = 0; ix < impl.jjtGetNumChildren(); ix++) {
                ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) impl.jjtGetChild(ix);
                if (isJUnitTest(type)) {
                    junitImported = true;
                    return super.visit(node, data);
                }
            }
        }
        if (node.jjtGetNumChildren() != 0 && node.jjtGetChild(0) instanceof ASTExtendsList) {
            ASTClassOrInterfaceType type = (ASTClassOrInterfaceType) node.jjtGetChild(0).jjtGetChild(0);
            if (isJUnitTest(type)) {
                junitImported = true;
                return super.visit(node, data);
            }
        }

        return super.visit(node, data);
    }

    private boolean isJUnitTest(ASTClassOrInterfaceType type) {
    	Class<?> clazz = type.getType();
        if (clazz == null) {
            if ("junit.framework.Test".equals(type.getImage())) {
            	return true;
            }
        } else if (isJUnitTest(clazz)) {
        	return true;
        } else {
        	while (clazz != null && !Object.class.equals(clazz)) {
	        	for(Class<?> intf : clazz.getInterfaces()) {
	        		if (isJUnitTest(intf)) {
	        			return true;
	        		}
	        	}
                clazz = clazz.getSuperclass();
        	}
        }
        return false;
    }

    private boolean isJUnitTest(Class<?> clazz) {
    	return clazz.getName().equals("junit.framework.Test");
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object o) {
        if (node.getImportedName().indexOf("junit") != -1) {
            junitImported = true;
        }
        return super.visit(node, o);
    }


    @Override
    public Object visit(ASTMethodDeclaration methodDeclaration, Object o) {
        if (junitImported && isAllowedMethod(methodDeclaration)) {
            return super.visit(methodDeclaration, o);
        }

        checkExceptions(methodDeclaration, o);

        return super.visit(methodDeclaration, o);
    }

    private boolean isAllowedMethod(ASTMethodDeclaration methodDeclaration) {
        if (getProperty(IGNORE_JUNIT_COMPLETELY_DESCRIPTOR)) {
	    return true;
	} else {
	    return methodDeclaration.getMethodName().equals("setUp") || methodDeclaration
                .getMethodName().equals("tearDown");
	}
    }

    @Override
    public Object visit(ASTConstructorDeclaration constructorDeclaration, Object o) {
        checkExceptions(constructorDeclaration, o);

        return super.visit(constructorDeclaration, o);
    }

    
    private void checkExceptions(Node method, Object o) {
        List<ASTName> exceptionList = method.findDescendantsOfType(ASTName.class);
        if (!exceptionList.isEmpty()) {
            evaluateExceptions(exceptionList, o);
        }
    }

    
    private void evaluateExceptions(List<ASTName> exceptionList, Object context) {
        for (ASTName exception: exceptionList) {
            if (hasDeclaredExceptionInSignature(exception)) {
                addViolation(context, exception);
            }
        }
    }

    
    private boolean hasDeclaredExceptionInSignature(ASTName exception) {
        return exception.hasImageEqualTo("Exception") && isParentSignatureDeclaration(exception);
    }

    
    private boolean isParentSignatureDeclaration(ASTName exception) {
        Node parent = exception.jjtGetParent().jjtGetParent();
        return parent instanceof ASTMethodDeclaration || parent instanceof ASTConstructorDeclaration;
    }
}