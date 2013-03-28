

package edu.rice.cs.drjava.model.definitions.reducedmodel;


public class Stutter extends ReducedModelState {
  
  public static final Stutter ONLY = new Stutter();

  
  private Stutter() { }

  ReducedModelState update(TokenList.Iterator copyCursor) {
    if (copyCursor.atStart())  copyCursor.next();
    return copyCursor.getStateAtCurrent();
  }
}
