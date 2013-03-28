
package net.sourceforge.pmd.lang.java.rule.imports;

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class ImportFromSamePackageRule extends AbstractJavaRule {

    public Object visit(ASTImportDeclaration importDecl, Object data) {
        String packageName = importDecl.getScope().getEnclosingSourceFileScope().getPackageName();

        if (packageName != null && packageName.equals(importDecl.getPackageName())) {
            addViolation(data, importDecl);
        }

        
        if (packageName == null && importDecl.getPackageName().equals("")) {
            addViolation(data, importDecl);
        }
        return data;
    }
}
