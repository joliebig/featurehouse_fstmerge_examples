 

package edu.rice.cs.drjava.model.junit;

import java.util.List;
import edu.rice.cs.drjava.model.EventNotifier;
import edu.rice.cs.drjava.model.OpenDefinitionsDocument;
import edu.rice.cs.util.classloader.ClassFileError;
import edu.rice.cs.util.swing.Utilities;


class JUnitEventNotifier extends EventNotifier<JUnitListener> implements JUnitListener {

  
  public void nonTestCase(boolean isTestAll) {
    _lock.startRead();
    try { for (JUnitListener jul : _listeners) { jul.nonTestCase(isTestAll); } }
    finally { _lock.endRead(); }
  }

  public void classFileError(ClassFileError e) {
    _lock.startRead();
    try { for (JUnitListener jul : _listeners) { jul.classFileError(e); } }
    finally { _lock.endRead(); }
  }
  
 
  public void compileBeforeJUnit() {

    _lock.startRead();
    try { for (JUnitListener jul : _listeners) { jul.compileBeforeJUnit(); } }
    finally { _lock.endRead(); }
  }
  
  
  public void junitStarted() {
    _lock.startRead();
    try { for (JUnitListener jul : _listeners) { jul.junitStarted(); } }
    finally { _lock.endRead(); }
  }

  
  public void junitClassesStarted() {
    _lock.startRead();
    try { for (JUnitListener jul : _listeners) { jul.junitClassesStarted(); } }
    finally { _lock.endRead(); }
  }

  
  public void junitSuiteStarted(int numTests) {
    _lock.startRead();
    try { for (JUnitListener jul : _listeners) { jul.junitSuiteStarted(numTests); } }
    finally { _lock.endRead(); }
  }

  
  public void junitTestStarted(String name) {
    _lock.startRead();
    try { for (JUnitListener jul : _listeners) { jul.junitTestStarted(name); } }
    finally { _lock.endRead(); }
  }

  
  public void junitTestEnded(String name, boolean wasSuccessful, boolean causedError) {
    _lock.startRead();
    try { for (JUnitListener jul : _listeners) { jul.junitTestEnded(name, wasSuccessful, causedError); } }
    finally { _lock.endRead(); }
  }

  
  public void junitEnded() {

    _lock.startRead();

    try { for(JUnitListener jul : _listeners) { jul.junitEnded(); } }
    finally { _lock.endRead(); }
  }
}

