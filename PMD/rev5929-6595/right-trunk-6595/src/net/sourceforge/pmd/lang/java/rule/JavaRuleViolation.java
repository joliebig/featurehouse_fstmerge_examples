
package net.sourceforge.pmd.lang.java.rule;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.CanSuppressWarnings;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symboltable.Scope;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolation;



public class JavaRuleViolation extends AbstractRuleViolation {
    public JavaRuleViolation(Rule rule, RuleContext ctx, JavaNode node) {
	this(rule, ctx, node, rule.getMessage());
    }

    public JavaRuleViolation(Rule rule, RuleContext ctx, JavaNode node, String specificMsg) {
	super(rule, ctx, node, specificMsg);

	if (node != null) {
	    Scope scope = node.getScope();

	    
	    if (!(scope instanceof SourceFileScope)) {
		className = scope.getEnclosingClassScope().getClassName() == null ? "" : scope.getEnclosingClassScope()
			.getClassName();
	    }

	    
	    String qualifiedName = null;
	    for (ASTClassOrInterfaceDeclaration parent : node.getParentsOfType(ASTClassOrInterfaceDeclaration.class)) {
		if (qualifiedName == null) {
		    qualifiedName = parent.getScope().getEnclosingClassScope().getClassName();
		} else {
		    qualifiedName = parent.getScope().getEnclosingClassScope().getClassName() + "$" + qualifiedName;
		}
	    }
	    packageName = scope.getEnclosingSourceFileScope().getPackageName() == null ? "" : scope
		    .getEnclosingSourceFileScope().getPackageName();
	    
	    if (!(scope instanceof SourceFileScope)) {
		className = scope.getEnclosingClassScope().getClassName() == null ? "" : qualifiedName;
	    }
	    methodName = node.getFirstParentOfType(ASTMethodDeclaration.class) == null ? "" : scope
		    .getEnclosingMethodScope().getName();
	    setVariableNameIfExists(node);

	    
	    List<Node> parentTypes = new ArrayList<Node>();
	    if (node instanceof ASTTypeDeclaration || node instanceof ASTClassOrInterfaceBodyDeclaration
		    || node instanceof ASTFormalParameter || node instanceof ASTLocalVariableDeclaration) {
		parentTypes.add(node);
	    }
	    parentTypes.addAll(node.getParentsOfType(ASTTypeDeclaration.class));
	    parentTypes.addAll(node.getParentsOfType(ASTClassOrInterfaceBodyDeclaration.class));
	    parentTypes.addAll(node.getParentsOfType(ASTFormalParameter.class));
	    parentTypes.addAll(node.getParentsOfType(ASTLocalVariableDeclaration.class));
	    for (Node parentType : parentTypes) {
		CanSuppressWarnings t = (CanSuppressWarnings) parentType;
		if (t.hasSuppressWarningsAnnotationFor(getRule())) {
		    suppressed = true;
		    break;
		}
	    }
	}
    }

    private void setVariableNameIfExists(Node node) {
	variableName = "";
	if (node instanceof ASTFieldDeclaration) {
	    variableName = ((ASTFieldDeclaration) node).getVariableName();
	} else if (node instanceof ASTLocalVariableDeclaration) {
	    variableName = ((ASTLocalVariableDeclaration) node).getVariableName();
	} else if (node instanceof ASTVariableDeclaratorId) {
	    variableName = node.getImage();
	}
    }
}
