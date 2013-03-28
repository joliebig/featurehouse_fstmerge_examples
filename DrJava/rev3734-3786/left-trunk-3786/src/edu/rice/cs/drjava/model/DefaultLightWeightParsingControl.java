

package edu.rice.cs.drjava.model;

import java.util.List;
import java.util.LinkedList;
import java.util.Hashtable;
import javax.swing.text.*;

import edu.rice.cs.drjava.DrJava;
import edu.rice.cs.drjava.config.OptionConstants;
import edu.rice.cs.drjava.model.definitions.*;
import edu.rice.cs.util.Log;
import edu.rice.cs.util.swing.Utilities;


public class DefaultLightWeightParsingControl implements LightWeightParsingControl {
  
  private AbstractGlobalModel _model;
  
  
  private long _beginUpdates;
  
  
  private long _lastDelay = System.currentTimeMillis();
  
  
  private Hashtable<OpenDefinitionsDocument, Long> _lastUpdates = new Hashtable<OpenDefinitionsDocument, Long>();
  
  
  private Hashtable<OpenDefinitionsDocument, String> _enclosingClassNames = new Hashtable<OpenDefinitionsDocument, String>();
  
  
  private volatile boolean _running = false;
  
  
  private Object _restart = new Object();
  
  
  private LinkedList<LightWeightParsingListener> _listeners = new LinkedList<LightWeightParsingListener>();
  
  
  private Log _log = new Log("DefaultLightWeightParsingControl", false);
  
  
  private ThreadGroup _updaterThreadGroup = new ThreadGroup("Light-weight parsing updater thread group") {
    public void uncaughtException(Thread t, Throwable e) {
      _log.logTime("Uncaught exception in updater; disabled for rest of session", e);
      new edu.rice.cs.drjava.ui.DrJavaErrorHandler().handle(e);
    }
  };
  
  
  private Thread _updater = new Thread(_updaterThreadGroup, new Runnable() {
    public void run() {
      while(true) { 
        while (!_running) {
          _log.logTime("Waiting...");
          try {
            synchronized(_restart) {
              if (!_running) {
                _restart.wait();
              }
            }
          }
          catch(InterruptedException e) { }
        }
        long current = System.currentTimeMillis();
        long delta = (_beginUpdates-current);
        
        if (current>=_beginUpdates) {
          OpenDefinitionsDocument doc = _model.getActiveDocument();
          Long last = _lastUpdates.get(doc);
          if ((last==null) || (last<_lastDelay)) {
            update(doc);
            
          }
          else {
            
          }
          delta = DrJava.getConfig().getSetting(OptionConstants.DIALOG_LIGHTWEIGHT_PARSING_DELAY).intValue();
        }
        
        try {
          Thread.sleep(delta);
        }
        catch (InterruptedException e) {  }
      }
    }
  });
  
  
  public DefaultLightWeightParsingControl(AbstractGlobalModel model) {
    _model = model;
    _updater.setDaemon(true);
    _updater.start();
  }
  
  
  public synchronized void update(final OpenDefinitionsDocument doc) {
    _log.logTime("Update for "+doc);
    try {
      _lastUpdates.put(doc, System.currentTimeMillis());
      final String old = _enclosingClassNames.get(doc);
      final String updated = doc.getEnclosingClassName(doc.getCaretPosition(), true);
      if ((old==null) || (!old.equals(updated))) {
        _enclosingClassNames.put(doc, updated);
        Utilities.invokeLater(new Runnable() {
          public void run() {
            List<LightWeightParsingListener> listeners = getListeners();
            for (LightWeightParsingListener l: listeners) { l.enclosingClassNameUpdated(doc, old, updated); }
          }
        });
      }
    }
    catch(BadLocationException e) {  }
    catch(ClassNameNotFoundException e) {  }
  }
  
  
  public void setAutomaticUpdates(boolean b) {
    _log.logTime("setAutomaticUpdates("+b+")");
    _running = b;
    if (b) {
      delay();
      synchronized(_restart) {
        _restart.notify();
      }
    }
  }
  
  
  public void delay() {
    _lastDelay = System.currentTimeMillis();
    _beginUpdates = _lastDelay + (DrJava.getConfig().getSetting(OptionConstants.DIALOG_LIGHTWEIGHT_PARSING_DELAY).intValue());
  }
  
  
  public synchronized void reset() {
    for(final OpenDefinitionsDocument doc: _enclosingClassNames.keySet()) {
      final String old = _enclosingClassNames.get(doc);
      Utilities.invokeLater(new Runnable() {
        public void run() {
          List<LightWeightParsingListener> listeners = getListeners();
          for (LightWeightParsingListener l: listeners) { l.enclosingClassNameUpdated(doc, old, null); }
        }
      });
    }
    _enclosingClassNames.clear();
    _lastUpdates.clear();
  }
  
  
  public synchronized String getEnclosingClassName(OpenDefinitionsDocument doc) { return _enclosingClassNames.get(doc); }
  
  
  public synchronized void addListener(LightWeightParsingListener l) {
    _listeners.add(l);
  }
  
  
  public synchronized void removeListener(LightWeightParsingListener l) {
    _listeners.remove(l);
  }
  
  
  public synchronized void removeAllListeners() {
    _listeners.clear();
  }  
  
  
  public synchronized List<LightWeightParsingListener> getListeners() {
    return new LinkedList<LightWeightParsingListener>(_listeners);
  }
}