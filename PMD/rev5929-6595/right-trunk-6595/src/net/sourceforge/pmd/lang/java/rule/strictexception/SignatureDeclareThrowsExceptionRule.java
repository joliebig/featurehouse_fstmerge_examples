package net.sourceforge.pmd.lang.java.rule.strictexception;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;



public class SignatureDeclareThrowsExceptionRule extends AbstractJavaRule {

    private boolean junitImported;

    @Override
    public Object visit(ASTCompilationUnit node, Object o) {
        junitImported = false;
        return super.visit(node, o);
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
        if ((methodDeclaration.getMethodName().equals("setUp") || methodDeclaration.getMethodName().equals("tearDown")) && junitImported) {
            return super.visit(methodDeclaration, o);
        }

        if (methodDeclaration.getMethodName().startsWith("test")) {
            return super.visit(methodDeclaration, o);
        }

        List<ASTName> exceptionList = methodDeclaration.findDescendantsOfType(ASTName.class);
        if (!exceptionList.isEmpty()) {
            evaluateExceptions(exceptionList, o);
        }
        return super.visit(methodDeclaration, o);
    }


    @Override
    public Object visit(ASTConstructorDeclaration constructorDeclaration, Object o) {
        List<ASTName> exceptionList = constructorDeclaration.findDescendantsOfType(ASTName.class);
        if (!exceptionList.isEmpty()) {
            evaluateExceptions(exceptionList, o);
        }
        return super.visit(constructorDeclaration, o);
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
