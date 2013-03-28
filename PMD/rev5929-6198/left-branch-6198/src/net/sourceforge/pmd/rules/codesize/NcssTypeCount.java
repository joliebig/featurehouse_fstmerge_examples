package net.sourceforge.pmd.rules.codesize;

import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTInitializer;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;


public class NcssTypeCount extends AbstractNcssCount {

  
  public NcssTypeCount() {
    super( ASTTypeDeclaration.class );
  }

  public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {

    if ( !node.isNested() ) {
      return super.visit( node, data );
    }

    return countNodeChildren( node, data );
  }

  public Object visit(ASTConstructorDeclaration node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTExplicitConstructorInvocation node, Object data) {
    return NumericConstants.ONE;
  }

  public Object visit(ASTEnumDeclaration node, Object data) {
    
    if ( node.jjtGetParent() instanceof ASTTypeDeclaration ) {
      Integer nodeCount = countNodeChildren( node, data );
      int count = nodeCount.intValue() - 1;
      return Integer.valueOf( count );
    }
    return countNodeChildren( node, data );
  }

  public Object visit(ASTMethodDeclaration node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTInitializer node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTFieldDeclaration node, Object data) {
    return NumericConstants.ONE;
  }

  protected void makeViolations(RuleContext ctx, Set<DataPoint> p) {
    for ( DataPoint point: p ) {
      addViolation( ctx, point.getNode(),
          String.valueOf( (int) point.getScore() ) );
    }
  }

}
