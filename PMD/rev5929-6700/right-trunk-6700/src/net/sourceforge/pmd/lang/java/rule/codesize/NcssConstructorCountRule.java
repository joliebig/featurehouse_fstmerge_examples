package net.sourceforge.pmd.lang.java.rule.codesize;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;


public class NcssConstructorCountRule extends AbstractNcssCountRule {

  
  public NcssConstructorCountRule() {
    super( ASTConstructorDeclaration.class );
    setProperty(MINIMUM_DESCRIPTOR, 100d);
  }

  public Object visit(ASTExplicitConstructorInvocation node, Object data) {
    return NumericConstants.ONE;
  }
  
  @Override
  public Object[] getViolationParameters(DataPoint point) {
    
    return new String[] {
              String.valueOf( ( (ASTConstructorDeclaration) point.getNode() ).getParameterCount() ),
              String.valueOf( (int) point.getScore() ) };
  }
}
