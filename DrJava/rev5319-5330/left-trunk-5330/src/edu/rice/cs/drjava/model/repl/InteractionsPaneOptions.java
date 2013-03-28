

package edu.rice.cs.drjava.model.repl;

import edu.rice.cs.dynamicjava.Options;


public class InteractionsPaneOptions extends Options {
  private volatile boolean _enforceAllAccess = false;
  public boolean enforceAllAccess() { return _enforceAllAccess; }
  public void setEnforceAllAccess(boolean enforce) { _enforceAllAccess = enforce; }
  
  private volatile boolean _enforcePrivateAccess = false;
  public boolean enforcePrivateAccess() { return _enforcePrivateAccess; }
  public void setEnforcePrivateAccess(boolean enforce) { _enforcePrivateAccess = enforce; }
  
  private volatile boolean _requireSemicolon = false;
  public boolean requireSemicolon() { return _requireSemicolon; }
  public void setRequireSemicolon(boolean require) { _requireSemicolon = require; }
  
  private volatile boolean _requireVariableType = false;
  public boolean requireVariableType() { return _requireVariableType; }
  public void setRequireVariableType(boolean require) { _requireVariableType = require; }
}
