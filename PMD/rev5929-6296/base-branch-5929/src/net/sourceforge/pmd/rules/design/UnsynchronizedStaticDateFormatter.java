
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.HashSet;
import java.util.Set;


public class UnsynchronizedStaticDateFormatter extends AbstractRule {

    private static Set<String> targets = new HashSet<String>();
    static {
        targets.add("DateFormat");
        targets.add("SimpleDateFormat");
        targets.add("java.text.DateFormat");
        targets.add("java.text.SimpleDateFormat");
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        if (!node.isStatic()) {
            return data;
        }
        ASTClassOrInterfaceType cit = node.getFirstChildOfType(ASTClassOrInterfaceType.class);
        if (cit == null || !targets.contains(cit.getImage())) {
            return data;
        }
        ASTVariableDeclaratorId var = node.getFirstChildOfType(ASTVariableDeclaratorId.class);
        for (NameOccurrence occ: var.getUsages()) {
            SimpleNode n = occ.getLocation();
            if (n.getFirstParentOfType(ASTSynchronizedStatement.class) != null) {
                continue;
            }
            ASTMethodDeclaration method = n.getFirstParentOfType(ASTMethodDeclaration.class);
            if (method != null && !method.isSynchronized()) {
                addViolation(data, n);
            }
        }
        return data;
    }
}