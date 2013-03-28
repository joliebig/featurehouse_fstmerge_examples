package net.sourceforge.pmd.lang.java.rule.coupling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.StringMultiProperty;


public class LoosePackageCouplingRule extends AbstractJavaRule {

    private static final StringMultiProperty PACKAGES_DESCRIPTOR = new StringMultiProperty("packages", "Restricted Packages",
	    new String[] {}, 1.0f, ',');

    private static final StringMultiProperty CLASSES_DESCRIPTOR = new StringMultiProperty("classes", "Allowed Classes",
	    new String[] {}, 2.0f, ',');

    
    private String thisPackage;

    
    private List<String> restrictedPackages;

    public LoosePackageCouplingRule() {
	definePropertyDescriptor(PACKAGES_DESCRIPTOR);
	definePropertyDescriptor(CLASSES_DESCRIPTOR);

	addRuleChainVisit(ASTCompilationUnit.class);
	addRuleChainVisit(ASTPackageDeclaration.class);
	addRuleChainVisit(ASTImportDeclaration.class);
    }

    @Override
    public Object visit(ASTCompilationUnit node, Object data) {
	this.thisPackage = "";

	
	
	this.restrictedPackages = new ArrayList<String>(Arrays.asList(super.getProperty(PACKAGES_DESCRIPTOR)));
	Collections.sort(restrictedPackages, Collections.reverseOrder());

	return data;
    }

    @Override
    public Object visit(ASTPackageDeclaration node, Object data) {
	this.thisPackage = node.getPackageNameImage();
	return data;
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {

	String importPackage = node.getPackageName();

	
	for (String pkg : getRestrictedPackages()) {
	    
	    if (isContainingPackage(pkg, importPackage)) {
		
		if (pkg.equals(thisPackage) || isContainingPackage(pkg, thisPackage)) {
		    
		    break;
		} else {
		    
		    if (node.isImportOnDemand()) {
			addViolation(data, node, new Object[] { node.getImportedName(), pkg });
			break;
		    } else {
			if (!isAllowedClass(node)) {
			    addViolation(data, node, new Object[] { node.getImportedName(), pkg });
			    break;
			}
		    }
		}
	    }
	}
	return data;
    }

    protected List<String> getRestrictedPackages() {
	return restrictedPackages;
    }

    
    protected boolean isContainingPackage(String pkg1, String pkg2) {
	return pkg1.equals(pkg2)
		|| (pkg1.length() < pkg2.length() && pkg2.startsWith(pkg1) && pkg2.charAt(pkg1.length()) == '.');
    }

    protected boolean isAllowedClass(ASTImportDeclaration node) {
	String importedName = node.getImportedName();
	for (String clazz : getProperty(CLASSES_DESCRIPTOR)) {
	    if (importedName.equals(clazz)) {
		return true;
	    }

	}
	return false;
    }
}
