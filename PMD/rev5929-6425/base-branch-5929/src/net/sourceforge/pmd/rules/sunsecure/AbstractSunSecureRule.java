
package net.sourceforge.pmd.rules.sunsecure;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.List;


public abstract class AbstractSunSecureRule extends AbstractRule {

    
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

    
    protected boolean isLocalVariable(String vn, SimpleNode node) {
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

    
    protected String getFirstNameImage(SimpleNode n) {
        ASTName name = n.getFirstChildOfType(ASTName.class);
        if (name != null)
            return name.getImage();
        return null;
    }

}
