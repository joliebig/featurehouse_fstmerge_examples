

package edu.rice.cs.util;

import java.security.*;


public class PreventExitSecurityManager extends SecurityManager {
  
  private static final Permission SET_MANAGER_PERM = new RuntimePermission("setSecurityManager");

  private final SecurityManager _parent;

  
  private boolean _exitAttempted = false;

  
  private boolean _timeToExit = false;

  
  private boolean _blockExit = false;

  
  private boolean _timeToDeactivate = false;

  
  private PreventExitSecurityManager(final SecurityManager parent) { _parent = parent; }

  
  public static PreventExitSecurityManager activate() {
    SecurityManager currentMgr = System.getSecurityManager();
    if (currentMgr instanceof PreventExitSecurityManager) return (PreventExitSecurityManager) currentMgr;
    
    PreventExitSecurityManager mgr = new PreventExitSecurityManager(System.getSecurityManager());
    System.setSecurityManager(mgr);
    return mgr;
  }

  
  public void deactivate() {
    _timeToDeactivate = true;
    System.setSecurityManager(_parent);
  }

  
  public void exitVM(int status) {

    if (! _blockExit) _timeToExit = true;
    System.exit(status);
  }

  
  public void setBlockExit(boolean b) { _blockExit = b; }

  
  public boolean exitAttempted() {
    boolean old = _exitAttempted;
    _exitAttempted = false;
    return old;
  }

  
  public void checkPermission(Permission perm) {
    if (perm.equals(SET_MANAGER_PERM)) {
      if (! _timeToDeactivate) throw new SecurityException("Can not reset security manager!");
    }
    else {
      if (_parent != null) _parent.checkPermission(perm);
    }
  }

  public void checkExit(int status) {
    if (! _timeToExit) {
      _exitAttempted = true;
      throw new ExitingNotAllowedException();
    }
  }
}


