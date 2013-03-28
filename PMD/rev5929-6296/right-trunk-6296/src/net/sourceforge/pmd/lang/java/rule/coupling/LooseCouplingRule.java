
package net.sourceforge.pmd.lang.java.rule.coupling;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.CollectionUtil;

public class LooseCouplingRule extends AbstractJavaRule {

	







    public Object visit(ASTClassOrInterfaceType node, Object data) {
        Node parent = node.jjtGetParent().jjtGetParent().jjtGetParent();
        String typeName = node.getImage();
        if (CollectionUtil.isCollectionType(typeName, false) && (parent instanceof ASTFieldDeclaration || parent instanceof ASTFormalParameter || parent instanceof ASTResultType)) {
            addViolation(data, node, typeName);
        }
        return data;
    }
}
