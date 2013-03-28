package net.sourceforge.pmd.lang.java.rule.codesize;

import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;


public class NcssConstructorCountRule extends AbstractNcssCountRule {

  
  public NcssConstructorCountRule() {
    super( ASTConstructorDeclaration.class );
  }

  public Object visit(ASTExplicitConstructorInvocation node, Object data) {
    return NumericConstants.ONE;
  }

  protected void makeViolations(RuleContext ctx, Set<DataPoint> p) {
    for ( DataPoint point: p ) {
      
      addViolation(
          ctx,
          point.getNode(),
          new String[] {
              String.valueOf( ( (ASTConstructorDeclaration) point.getNode() ).getParameterCount() ),
              String.valueOf( (int) point.getScore() ) } );
    }
  }
}
