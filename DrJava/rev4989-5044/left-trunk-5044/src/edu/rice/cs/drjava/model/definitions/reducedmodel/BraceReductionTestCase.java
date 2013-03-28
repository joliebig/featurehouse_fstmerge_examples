

package edu.rice.cs.drjava.model.definitions.reducedmodel;

import edu.rice.cs.drjava.DrJavaTestCase;


public abstract class BraceReductionTestCase extends DrJavaTestCase {
  protected volatile ReducedModelControl model0;
  protected volatile ReducedModelControl model1;
  protected volatile ReducedModelControl model2;

  
  protected void setUp() throws Exception {
    super.setUp();
    model0 = new ReducedModelControl();
    model1 = new ReducedModelControl();
    model2 = new ReducedModelControl();
  }

  
  protected void insertGap(BraceReduction model, int size) {
    for (int i = 0; i < size; i++) {
      model.insertChar(' ');
    }
  }

  
  ReducedModelState stateOfCurrentToken(BraceReduction br) {
    return br.currentToken().getState();
  }
}