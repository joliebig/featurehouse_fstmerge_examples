package net.sourceforge.pmd.rules.codesize;

import net.sourceforge.pmd.ast.ASTBreakStatement;
import net.sourceforge.pmd.ast.ASTCatchStatement;
import net.sourceforge.pmd.ast.ASTContinueStatement;
import net.sourceforge.pmd.ast.ASTDoStatement;
import net.sourceforge.pmd.ast.ASTFinallyStatement;
import net.sourceforge.pmd.ast.ASTForInit;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTLabeledStatement;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.ASTStatementExpressionList;
import net.sourceforge.pmd.ast.ASTSwitchLabel;
import net.sourceforge.pmd.ast.ASTSwitchStatement;
import net.sourceforge.pmd.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.ast.ASTThrowStatement;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.SimpleJavaNode;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.stat.StatisticalRule;
import net.sourceforge.pmd.util.NumericConstants;


public abstract class AbstractNcssCount extends StatisticalRule {

  private Class nodeClass;

  
  protected AbstractNcssCount(Class nodeClass) {
    this.nodeClass = nodeClass;
  }

  public Object visit(SimpleJavaNode node, Object data) {
    int numNodes = 0;

    for ( int i = 0; i < node.jjtGetNumChildren(); i++ ) {
      SimpleJavaNode simpleNode = (SimpleJavaNode) node.jjtGetChild( i );
      Integer treeSize = (Integer) simpleNode.jjtAccept( this, data );
      numNodes += treeSize.intValue();
    }

    if ( this.nodeClass.isInstance( node ) ) {
      
      numNodes++;
      DataPoint point = new DataPoint();
      point.setNode( node );
      point.setScore( 1.0 * numNodes );
      point.setMessage( getMessage() );
      addDataPoint( point );
    }

    return Integer.valueOf( numNodes );
  }

  
  protected Integer countNodeChildren(SimpleJavaNode node, Object data) {
    Integer nodeCount = null;
    int lineCount = 0;
    for ( int i = 0; i < node.jjtGetNumChildren(); i++ ) {
      nodeCount = (Integer) ( (SimpleJavaNode) node.jjtGetChild( i ) ).jjtAccept(
          this, data );
      lineCount += nodeCount.intValue();
    }
    return ++lineCount;
  }

  public Object visit(ASTForStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTDoStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTIfStatement node, Object data) {

    Integer lineCount = countNodeChildren( node, data );

    if ( node.hasElse() ) {
      lineCount++;
    }

    return lineCount;
  }

  public Object visit(ASTWhileStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTBreakStatement node, Object data) {
    return NumericConstants.ONE;
  }

  public Object visit(ASTCatchStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTContinueStatement node, Object data) {
    return NumericConstants.ONE;
  }

  public Object visit(ASTFinallyStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTReturnStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTSwitchStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTSynchronizedStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTThrowStatement node, Object data) {
    return NumericConstants.ONE;
  }

  public Object visit(ASTStatementExpression node, Object data) {

    
    if ( node.jjtGetParent() instanceof ASTStatementExpressionList ) {
      return NumericConstants.ZERO;
    }

    return NumericConstants.ONE;
  }

  public Object visit(ASTLabeledStatement node, Object data) {
    return countNodeChildren( node, data );
  }

  public Object visit(ASTLocalVariableDeclaration node, Object data) {

    
    if ( node.jjtGetParent() instanceof ASTForInit ) {
      return NumericConstants.ZERO;
    }

    

    return countNodeChildren( node, data );
  }

  public Object visit(ASTSwitchLabel node, Object data) {
    return countNodeChildren( node, data );
  }

}
