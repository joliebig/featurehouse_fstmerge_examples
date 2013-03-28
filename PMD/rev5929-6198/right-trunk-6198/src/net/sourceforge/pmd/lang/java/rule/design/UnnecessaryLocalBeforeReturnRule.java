package net.sourceforge.pmd.lang.java.rule.design;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;

public class UnnecessaryLocalBeforeReturnRule extends AbstractJavaRule {

    public Object visit(ASTMethodDeclaration meth, Object data) {
        
        if (meth.isVoid() || meth.isAbstract() || meth.isNative()) {
            return data;
        }
        return super.visit(meth, data);
    }

    public Object visit(ASTReturnStatement rtn, Object data) {
        
        ASTName name = rtn.getFirstChildOfType(ASTName.class);
        if (name == null) {
            return data;
        }

        
        if (rtn.findChildrenOfType(ASTExpression.class).size() > 1 || rtn.findChildrenOfType(ASTPrimaryExpression.class).size() > 1 || isMethodCall(rtn)) {
            return data;
        }

        Map<VariableNameDeclaration, List<NameOccurrence>> vars = name.getScope().getVariableDeclarations();
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry: vars.entrySet()) {
            VariableNameDeclaration key = entry.getKey();
            List<NameOccurrence> usages = entry.getValue();
            for (NameOccurrence occ: usages) {
                if (occ.getLocation().equals(name)) {
                    
                    if (key.getNode().getBeginLine() == name.getBeginLine() - 1) {
                        String var = name.getImage();
                        if (var.indexOf('.') != -1) {
                            var = var.substring(0, var.indexOf('.'));
                        }
                        addViolation(data, rtn, var);
                    }
                }
            }
        }
        return data;
    }
    
    
    private boolean isMethodCall(ASTReturnStatement rtn) {
     List<ASTPrimarySuffix> suffix = rtn.findChildrenOfType( ASTPrimarySuffix.class );
     for ( ASTPrimarySuffix element: suffix ) {
        if ( element.isArguments() ) {
          return true;
        }
      }
      return false;
    }
}
