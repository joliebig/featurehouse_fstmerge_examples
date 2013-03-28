
package net.sourceforge.pmd.lang.java.rule.sunsecure;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;


public abstract class AbstractSunSecureRule extends AbstractJavaRule {

    
    protected final boolean isField(String varName, ASTTypeDeclaration typeDeclaration) {
        final List<ASTFieldDeclaration> fds = typeDeclaration.findChildrenOfType(ASTFieldDeclaration.class);
        if (fds != null) {
            for (ASTFieldDeclaration fd: fds) {
                final ASTVariableDeclaratorId vid = fd.getFirstChildOfType(ASTVariableDeclaratorId.class);
                if (vid != null && vid.hasImageEqualTo(varName)) {
                    return true;
                }
            }
        }
        return false;
    }


    
    protected final String getReturnedVariableName(ASTReturnStatement ret) {
        final ASTName n = ret.getFirstChildOfType(ASTName.class);
        if (n != null)
            return n.getImage();
        final ASTPrimarySuffix ps = ret.getFirstChildOfType(ASTPrimarySuffix.class);
        if (ps != null)
            return ps.getImage();
        return null;
    }

    
    protected boolean isLocalVariable(String vn, Node node) {
        final List<ASTLocalVariableDeclaration> lvars = node.findChildrenOfType(ASTLocalVariableDeclaration.class);
        if (lvars != null) {
            for (ASTLocalVariableDeclaration lvd: lvars) {
                final ASTVariableDeclaratorId vid = lvd.getFirstChildOfType(ASTVariableDeclaratorId.class);
                if (vid != null && vid.hasImageEqualTo(vn)) {
                    return true;
                }
            }
        }
        return false;
    }

    
    protected String getFirstNameImage(Node n) {
        ASTName name = n.getFirstChildOfType(ASTName.class);
        if (name != null)
            return name.getImage();
        return null;
    }

}
