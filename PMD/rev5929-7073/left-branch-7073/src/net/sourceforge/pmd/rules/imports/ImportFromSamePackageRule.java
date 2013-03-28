
package net.sourceforge.pmd.rules.imports;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTImportDeclaration;

public class ImportFromSamePackageRule extends AbstractRule {

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
