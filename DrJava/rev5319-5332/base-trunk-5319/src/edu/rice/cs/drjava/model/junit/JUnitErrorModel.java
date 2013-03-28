

package edu.rice.cs.drjava.model.junit;

import edu.rice.cs.drjava.model.GlobalModel;
import edu.rice.cs.drjava.model.compiler.CompilerErrorModel;


public class JUnitErrorModel extends CompilerErrorModel {
  private boolean _testsHaveRun = false;
  
  
  public JUnitErrorModel(JUnitError[] errors, GlobalModel model, boolean testsHaveRun) {
    super(errors, model);
    _testsHaveRun = testsHaveRun;
  }
  
  
  public boolean haveTestsRun() { return _testsHaveRun; }
}
