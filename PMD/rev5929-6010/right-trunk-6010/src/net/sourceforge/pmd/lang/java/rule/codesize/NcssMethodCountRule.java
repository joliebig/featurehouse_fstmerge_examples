package net.sourceforge.pmd.lang.java.rule.codesize;

import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.stat.DataPoint;


public class NcssMethodCountRule extends AbstractNcssCountRule {

  
  public NcssMethodCountRule() {
    super( ASTMethodDeclaration.class );
  }

  public Object visit(ASTMethodDeclaration node, Object data) {
    return super.visit( node, data );
  }

  protected void makeViolations(RuleContext ctx, Set<DataPoint> p) {
    for ( DataPoint point: p ) {
      addViolation( ctx, point.getNode(), new String[] {
          ( (ASTMethodDeclaration) point.getNode() ).getMethodName(),
          String.valueOf( (int) point.getScore() ) } );
    }
  }

}
