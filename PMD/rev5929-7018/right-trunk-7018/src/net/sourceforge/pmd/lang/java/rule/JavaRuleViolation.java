
package net.sourceforge.pmd.lang.java.rule;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.CanSuppressWarnings;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.symboltable.Scope;
import net.sourceforge.pmd.lang.java.symboltable.SourceFileScope;
import net.sourceforge.pmd.lang.rule.AbstractRuleViolation;


public class JavaRuleViolation extends AbstractRuleViolation {
    public JavaRuleViolation(Rule rule, RuleContext ctx, JavaNode node, String message) {
	super(rule, ctx, node, message);

	if (node != null) {
	    final Scope scope = node.getScope();
	    final SourceFileScope sourceFileScope = scope.getEnclosingSourceFileScope();

	    
	    packageName = sourceFileScope.getPackageName() == null ? "" : sourceFileScope.getPackageName();

	    
	    String qualifiedName = null;
	    for (ASTClassOrInterfaceDeclaration parent : node.getParentsOfType(ASTClassOrInterfaceDeclaration.class)) {
		if (qualifiedName == null) {
		    qualifiedName = parent.getScope().getEnclosingClassScope().getClassName();
		} else {
		    qualifiedName = parent.getScope().getEnclosingClassScope().getClassName() + "$" + qualifiedName;
		}
	    }
	    if (qualifiedName != null) {
		className = qualifiedName;
	    }
	    
	    if (node.getFirstParentOfType(ASTMethodDeclaration.class) != null) {
		methodName = scope.getEnclosingMethodScope().getName();
	    }
	    
	    setVariableNameIfExists(node);

	    
	    if (!suppressed) {
		suppressed = suppresses(node);
	    }
	    if (!suppressed && node instanceof ASTCompilationUnit) {
		for (int i = 0; !suppressed && i < node.jjtGetNumChildren(); i++) {
		    suppressed = suppresses(node.jjtGetChild(i));
		}
	    }
	    if (!suppressed) {
		Node parent = node.jjtGetParent();
		while (!suppressed && parent != null) {
		    suppressed = suppresses(parent);
		    parent = parent.jjtGetParent();
		}
	    }
	}
    }

    private boolean suppresses(final Node node) {
	return node instanceof CanSuppressWarnings
		&& ((CanSuppressWarnings) node).hasSuppressWarningsAnnotationFor(getRule());
    }

    private void setVariableNameIfExists(Node node) {
	variableName = "";
	if (node instanceof ASTFieldDeclaration) {
	    variableName = ((ASTFieldDeclaration) node).getVariableName();
	} else if (node instanceof ASTLocalVariableDeclaration) {
	    variableName = ((ASTLocalVariableDeclaration) node).getVariableName();
	} else if (node instanceof ASTVariableDeclarator) {
	    variableName = node.jjtGetChild(0).getImage();
	} else if (node instanceof ASTVariableDeclaratorId) {
	    variableName = node.getImage();
	}
    }
}
