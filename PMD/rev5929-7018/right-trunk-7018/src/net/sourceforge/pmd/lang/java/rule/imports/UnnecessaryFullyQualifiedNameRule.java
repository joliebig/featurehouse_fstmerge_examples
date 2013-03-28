package net.sourceforge.pmd.lang.java.rule.imports;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class UnnecessaryFullyQualifiedNameRule extends AbstractJavaRule {

    private List<ASTImportDeclaration> imports = new ArrayList<ASTImportDeclaration>();
    private List<ASTImportDeclaration> matches = new ArrayList<ASTImportDeclaration>();

    public UnnecessaryFullyQualifiedNameRule() {
	super.addRuleChainVisit(ASTCompilationUnit.class);
	super.addRuleChainVisit(ASTImportDeclaration.class);
	super.addRuleChainVisit(ASTClassOrInterfaceType.class);
	super.addRuleChainVisit(ASTName.class);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
	imports.clear();
	return data;
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
	imports.add(node);
	return data;
    }

    @Override
    public Object visit(ASTClassOrInterfaceType node, Object data) {
	checkImports(node, data, false);
	return data;
    }

    @Override
    public Object visit(ASTName node, Object data) {
	if (!(node.jjtGetParent() instanceof ASTImportDeclaration)) {
	    checkImports(node, data, true);
	}
	return data;
    }

    private void checkImports(JavaNode node, Object data, boolean checkStatic) {
	String name = node.getImage();
	matches.clear();

	
	for (ASTImportDeclaration importDeclaration : imports) {
	    if (importDeclaration.isImportOnDemand()) {
		
		if (name.startsWith(importDeclaration.getImportedName())) {
		    if (name.lastIndexOf('.') == importDeclaration.getImportedName().length()) {
			matches.add(importDeclaration);
			continue;
		    }
		}
	    } else {
		
		if (name.equals(importDeclaration.getImportedName())) {
		    matches.add(importDeclaration);
		    continue;
		}
		
		if (name.startsWith(importDeclaration.getImportedName())) {
		    if (name.lastIndexOf('.') == importDeclaration.getImportedName().length()) {
			matches.add(importDeclaration);
			continue;
		    }
		}
	    }
	}

	
	
	
	
	
	
	
	
	
	
	if (matches.isEmpty() && name.indexOf('.') >= 0) {
	    for (ASTImportDeclaration importDeclaration : imports) {
		if (importDeclaration.isStatic()) {
		    String[] importParts = importDeclaration.getImportedName().split("\\.");
		    String[] nameParts = name.split("\\.");
		    boolean checkClassImport = false;
		    if (importDeclaration.isImportOnDemand()) {
			
			if (nameParts[nameParts.length - 2].equals(importParts[importParts.length - 1])) {
			    checkClassImport = true;
			}
		    } else {
			
			if (nameParts[nameParts.length - 1].equals(importParts[importParts.length - 1])
				&& nameParts[nameParts.length - 2].equals(importParts[importParts.length - 2])) {
			    checkClassImport = true;
			}
		    }
		    if (checkClassImport) {
			
			String nameEnd = "." + nameParts[nameParts.length - 2];
			for (ASTImportDeclaration importDeclaration2 : imports) {
			    if (!importDeclaration2.isStatic() && !importDeclaration2.isImportOnDemand()
				    && importDeclaration2.getImportedName().endsWith(nameEnd)) {
				matches.add(importDeclaration2);
			    }
			}
		    }
		}
	    }
	}

	if (!matches.isEmpty()) {
	    String importStr = matches.get(0).getImportedName() + (matches.get(0).isImportOnDemand() ? ".*" : "");
	    addViolation(data, node, new Object[] { node.getImage(), importStr });
	}

	matches.clear();
    }
}
