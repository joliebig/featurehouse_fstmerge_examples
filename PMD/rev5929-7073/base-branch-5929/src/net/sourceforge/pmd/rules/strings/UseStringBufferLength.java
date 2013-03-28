package net.sourceforge.pmd.rules.strings;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameDeclaration;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.typeresolution.TypeHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;



public class UseStringBufferLength extends AbstractRule {

    
    
    private Set<VariableNameDeclaration> alreadySeen = new HashSet<VariableNameDeclaration>();

    public Object visit(ASTCompilationUnit acu, Object data) {
        alreadySeen.clear();
        return super.visit(acu, data);
    }

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

        SimpleNode parent = (SimpleNode) decl.jjtGetParent().jjtGetParent();
        for (int jx = 0; jx < parent.jjtGetNumChildren(); jx++) {
            SimpleNode achild = (SimpleNode) parent.jjtGetChild(jx);
            if (isViolation(parent, achild)) {
                addViolation(data, decl);
            }
        }

        return data;
    }

    
    private boolean isViolation(SimpleNode parent, SimpleNode achild) {
        if ("equals".equals(achild.getImage())) {
            List literals = parent.findChildrenOfType(ASTLiteral.class);
            return (!literals.isEmpty() && "\"\"".equals(((SimpleNode) literals.get(0)).getImage()));
        } else if ("length".equals(achild.getImage())) {
            return true;
        }
        return false;
    }


}
