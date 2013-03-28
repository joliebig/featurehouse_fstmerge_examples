
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.CollectionUtil;


public class UnsynchronizedStaticDateFormatterRule extends AbstractJavaRule {

    private static Set<String> targets = CollectionUtil.asSet(new String[] {
    	"DateFormat", "SimpleDateFormat", "java.text.DateFormat","java.text.SimpleDateFormat"
    	});

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (!node.isStatic()) {
            return data;
        }
        ASTClassOrInterfaceType cit = node.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        if (cit == null || !targets.contains(cit.getImage())) {
            return data;
        }
        ASTVariableDeclaratorId var = node.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        for (NameOccurrence occ: var.getUsages()) {
            Node n = occ.getLocation();
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