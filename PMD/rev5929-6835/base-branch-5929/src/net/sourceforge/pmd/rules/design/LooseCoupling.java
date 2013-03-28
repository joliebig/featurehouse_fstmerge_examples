
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.util.CollectionUtil;

public class LooseCoupling extends AbstractRule {

	







    public LooseCoupling() {
        super();
    }

    public Object visit(ASTClassOrInterfaceType node, Object data) {
        Node parent = node.jjtGetParent().jjtGetParent().jjtGetParent();
        String typeName = node.getImage();
        if (CollectionUtil.isCollectionType(typeName, false) && (parent instanceof ASTFieldDeclaration || parent instanceof ASTFormalParameter || parent instanceof ASTResultType)) {
            addViolation(data, node, typeName);
        }
        return data;
    }
}
