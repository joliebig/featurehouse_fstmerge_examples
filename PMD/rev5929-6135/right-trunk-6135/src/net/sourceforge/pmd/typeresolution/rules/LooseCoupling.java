
package net.sourceforge.pmd.typeresolution.rules;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.util.CollectionUtil;


public class LooseCoupling extends AbstractJavaRule {

    public LooseCoupling() {
	super();
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
	Node parent = node.getNthParent(3);
	Class<?> clazzType = node.getType();
	boolean isType = CollectionUtil.isCollectionType(clazzType, false);
	if (isType
		&& (parent instanceof ASTFieldDeclaration || parent instanceof ASTFormalParameter || parent instanceof ASTResultType)) {
	    addViolation(data, node, node.getImage());
	}
	return data;
    }
}
