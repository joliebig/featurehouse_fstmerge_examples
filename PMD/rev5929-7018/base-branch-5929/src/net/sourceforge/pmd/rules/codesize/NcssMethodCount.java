package net.sourceforge.pmd.rules.codesize;

import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.stat.DataPoint;


public class NcssMethodCount extends AbstractNcssCount {

  
  public NcssMethodCount() {
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
