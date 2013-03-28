package net.sourceforge.pmd.lang.java.rule.strings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;



public class UseStringBufferLengthRule extends AbstractJavaRule {

    
    
    private Set<VariableNameDeclaration> alreadySeen = new HashSet<VariableNameDeclaration>();

    @Override
    public Object visit(ASTCompilationUnit acu, Object data) {
        alreadySeen.clear();
        return super.visit(acu, data);
    }

    @Override
    public Object visit(ASTName decl, Object data) {
        if (!decl.getImage().endsWith("toString")) {
            return data;
        }
        NameDeclaration nd = decl.getNameDeclaration();
        if (!(nd instanceof VariableNameDeclaration)) {
            return data;
        }
        VariableNameDeclaration vnd = (VariableNameDeclaration) nd;
        if (alreadySeen.contains(vnd) || !TypeHelper.isA(vnd, StringBuffer.class)) {
            return data;
        }
        alreadySeen.add(vnd);

        Node parent = decl.jjtGetParent().jjtGetParent();
        for (int jx = 0; jx < parent.jjtGetNumChildren(); jx++) {
            Node achild = parent.jjtGetChild(jx);
            if (isViolation(parent, achild)) {
                addViolation(data, decl);
            }
        }

        return data;
    }

    
    private boolean isViolation(Node parent, Node achild) {
        if ("equals".equals(achild.getImage())) {
            List<ASTLiteral> literals = parent.findDescendantsOfType(ASTLiteral.class);
            return !literals.isEmpty() && "\"\"".equals(literals.get(0).getImage());
        } else if ("length".equals(achild.getImage())) {
            return true;
        }
        return false;
    }


}
