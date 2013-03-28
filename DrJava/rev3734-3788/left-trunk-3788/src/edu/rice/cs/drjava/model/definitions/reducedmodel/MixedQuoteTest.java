

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import  junit.framework.*;


public final class MixedQuoteTest extends BraceReductionTestCase
  implements ReducedModelStates
{
  protected ReducedModelControl _model;

  
  protected void setUp() throws Exception {
    super.setUp();
    _model = new ReducedModelControl();
  }

  
  public static Test suite() {
    return  new TestSuite(MixedQuoteTest.class);
  }

  
  protected void insertGap(BraceReduction model, int size) {
    for (int i = 0; i < size; i++) {
      model.insertChar(' ');
    }
  }

  
  public void testSingleEclipsesDouble() {
    _model.insertChar('\"');
    assertEquals("#0.0", INSIDE_DOUBLE_QUOTE, _model.getStateAtCurrent());
    _model.move(-1);
    assertEquals("#0.1", FREE, stateOfCurrentToken(_model));
    _model.move(1);
    _model.insertChar('A');
    _model.move(-1);
    assertEquals("#1.0", INSIDE_DOUBLE_QUOTE, _model.getStateAtCurrent());
    assertEquals("#1.1", INSIDE_DOUBLE_QUOTE, stateOfCurrentToken(_model));
    assertTrue("#1.2", _model.currentToken().isGap());
    _model.move(-1);
    _model.insertChar('\'');
    assertEquals("#2.0", INSIDE_SINGLE_QUOTE, _model.getStateAtCurrent());
    assertEquals("#2.1", INSIDE_SINGLE_QUOTE, stateOfCurrentToken(_model));
    assertEquals("#2.2", "\"", _model.currentToken().getType());
    _model.move(1);
    assertEquals("#3.0", INSIDE_SINGLE_QUOTE, _model.getStateAtCurrent());
    assertEquals("#3.1", INSIDE_SINGLE_QUOTE, stateOfCurrentToken(_model));
    assertTrue("#3.2", _model.currentToken().isGap());
  }

  
  public void testDoubleEclipsesSingle() {
    _model.insertChar('\'');
    assertEquals("#0.0", INSIDE_SINGLE_QUOTE, _model.getStateAtCurrent());
    _model.move(-1);
    assertEquals("#0.1", FREE, stateOfCurrentToken(_model));
    _model.move(1);
    _model.insertChar('A');
    _model.move(-1);
    assertEquals("#1.0", INSIDE_SINGLE_QUOTE, _model.getStateAtCurrent());
    assertEquals("#1.1", INSIDE_SINGLE_QUOTE, stateOfCurrentToken(_model));
    assertTrue("#1.2", _model.currentToken().isGap());
    _model.move(-1);
    _model.insertChar('\"');
    assertEquals("#2.0", INSIDE_DOUBLE_QUOTE, _model.getStateAtCurrent());
    assertEquals("#2.1", INSIDE_DOUBLE_QUOTE, stateOfCurrentToken(_model));
    assertEquals("#2.2", "\'", _model.currentToken().getType());
    _model.move(1);
    assertEquals("#3.0", INSIDE_DOUBLE_QUOTE, _model.getStateAtCurrent());
    assertEquals("#3.1", INSIDE_DOUBLE_QUOTE, stateOfCurrentToken(_model));
    assertTrue("#3.2", _model.currentToken().isGap());
  }
}
