
package org.jmol.viewer;

import java.util.Vector;

import org.jmol.util.Logger;
import org.jmol.util.TextFormat;

class ScriptManager {

  Viewer viewer;
  Thread[] queueThreads = new Thread[2];
  boolean[] scriptQueueRunning = new boolean[2];
  Vector scriptQueue = new Vector();
  boolean useQueue = true;
  Thread commandWatcherThread;

  ScriptManager(Viewer viewer) {
    this.viewer = viewer;
  }

  void clear() {
    startCommandWatcher(false);
  }

  public void setQueue(boolean TF) {
    useQueue = TF;
    if (!TF)
      clearQueue();
  }

  public String addScript(String strScript) {
    return (String) addScript("string", strScript, "", false, false);
  }

  public String addScript(String strScript, boolean isScriptFile,
                          boolean isQuiet) {
    return (String) addScript("String", strScript, "", isScriptFile, isQuiet);
  }

  public Object addScript(String returnType, String strScript,
                          String statusList, boolean isScriptFile,
                          boolean isQuiet) {
    if (!useQueue) {
      clearQueue();
      viewer.haltScriptExecution();
    }
    if (commandWatcherThread == null && useCommandWatcherThread)
      startCommandWatcher(true);
    if (commandWatcherThread != null && strScript.indexOf("/*SPLIT*/") >= 0) {
      String[] scripts = TextFormat.split(strScript, "/*SPLIT*/");
      for (int i = 0; i < scripts.length; i++)
        addScript(returnType, scripts[i], statusList, isScriptFile, isQuiet);
      return "split into " + scripts.length + " sections for processing";
    }
    boolean useCommandThread = (commandWatcherThread != null && 
        (strScript.indexOf("javascript") < 0 
            || strScript.indexOf("#javascript ") >= 0));
    
    Vector scriptItem = new Vector();
    scriptItem.addElement(strScript);
    scriptItem.addElement(statusList);
    scriptItem.addElement(returnType);
    scriptItem.addElement(isScriptFile ? Boolean.TRUE : Boolean.FALSE);
    scriptItem.addElement(isQuiet ? Boolean.TRUE : Boolean.FALSE);
    scriptItem.addElement(new Integer(useCommandThread ? -1 : 1));
    scriptQueue.addElement(scriptItem);
    if (Logger.debugging)
      Logger.info(scriptQueue.size() + " scripts; added: " + strScript);
    startScriptQueue(false);
    return "pending";
  }

  public int getScriptCount() {
    return scriptQueue.size();
  }

  public void clearQueue() {
    scriptQueue.clear();
  }

  public void waitForQueue() {
    int n = 0;
    while (queueThreads[0] != null || queueThreads[1] != null) {
      try {
        Thread.sleep(100);
        if (((n++) % 10) == 0)
          if (Logger.debugging) {
            Logger.debug("...scriptManager waiting for queue: "
                + scriptQueue.size());
          }
      } catch (InterruptedException e) {
      }
    }
  }

  public synchronized void flushQueue(String command) {
    for (int i = scriptQueue.size(); --i >= 0;) {
      String strScript = (String) (((Vector) scriptQueue.elementAt(i))
          .elementAt(0));
      if (strScript.indexOf(command) == 0) {
        scriptQueue.removeElementAt(i);
        if (Logger.debugging)
          Logger.debug(scriptQueue.size() + " scripts; removed: " + strScript);
      }
    }
  }

  void startScriptQueue(boolean startedByCommandWatcher) {
    int pt = (startedByCommandWatcher ? 1 : 0);
    if (scriptQueueRunning[pt])
      return;
    
    scriptQueueRunning[pt] = true;
    queueThreads[pt] = new Thread(new ScriptQueueRunnable(
        startedByCommandWatcher, pt));
    queueThreads[pt].setName("QueueThread" + pt);
    queueThreads[pt].start();
  }

  Vector getScriptItem(boolean watching, boolean isByCommandWatcher) {
    Vector scriptItem = (Vector) scriptQueue.elementAt(0);
    int flag = (((Integer) scriptItem.elementAt(5)).intValue());
    boolean isOK = (watching ? flag < 0 
        : isByCommandWatcher ? flag == 0
        : flag == 1);
    
    return (isOK ? scriptItem : null);
  }

  
  class ScriptQueueRunnable implements Runnable {
    boolean startedByCommandThread = false;
    int pt;

    public ScriptQueueRunnable(boolean startedByCommandThread, int pt) {
      this.startedByCommandThread = startedByCommandThread;
      this.pt = pt;
      
    }

    public void run() {
        while (scriptQueue.size() != 0) {
          
          if (!runNextScript())
            try {
              Thread.sleep(100); 
            } catch (Exception e) {
              System.out.println(this + " Exception " + e.getMessage());
              break; 
            }
        }
      
      queueThreads[pt].interrupt();
      stop();
    }

    public void stop() {
      scriptQueueRunning[pt] = false;
      queueThreads[pt] = null;      
      viewer.setSyncDriver(StatusManager.SYNC_ENABLE);
    }

    private boolean runNextScript() {
      if (scriptQueue.size() == 0)
        return false;
      
      Vector scriptItem = getScriptItem(false, startedByCommandThread);
      if (scriptItem == null)
        return false;
      String script = (String) scriptItem.elementAt(0);
      String statusList = (String) scriptItem.elementAt(1);
      String returnType = (String) scriptItem.elementAt(2);
      boolean isScriptFile = ((Boolean) scriptItem.elementAt(3)).booleanValue();
      boolean isQuiet = ((Boolean) scriptItem.elementAt(4)).booleanValue();
      if (Logger.debugging) {
        Logger.info("Queue[" + pt + "][" + scriptQueue.size()
            + "] scripts; running: " + script);
      }
      
      scriptQueue.removeElementAt(0);
      
      runScript(returnType, script, statusList, isScriptFile, isQuiet);
      if (scriptQueue.size() == 0) {
        
        return false;
      }
      return true;
    }

    private void runScript(String returnType, String strScript,
                           String statusList, boolean isScriptFile,
                           boolean isQuiet) {
      
      viewer.evalStringWaitStatus(returnType, strScript, statusList,
          isScriptFile, isQuiet, true);
      
    }

  }

  boolean useCommandWatcherThread = false;
  static final String SCRIPT_COMPLETED = "Script completed";

  synchronized void startCommandWatcher(boolean isStart) {
    useCommandWatcherThread = isStart;
    if (isStart) {
      if (commandWatcherThread != null)
        return;
      commandWatcherThread = new Thread(new CommandWatcher());
      commandWatcherThread.setName("CommmandWatcherThread");
      commandWatcherThread.start();
    } else {
      if (commandWatcherThread == null)
        return;
      commandWatcherThread.interrupt();
      commandWatcherThread = null;
    }
    if (Logger.debugging) {
      Logger.info("command watcher " + (isStart ? "started" : "stopped")
          + commandWatcherThread);
    }
  }

  

  class CommandWatcher implements Runnable {
    public void run() {
      Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
      int commandDelay = 200;
      while (commandWatcherThread != null) {
        try {
          Thread.sleep(commandDelay);
          if (commandWatcherThread != null) {
            if (scriptQueue.size() > 0) {
              Vector scriptItem = getScriptItem(true, true);
              if (scriptItem != null) {
                scriptItem.setElementAt(new Integer(0), 5);
                startScriptQueue(true);
              }
            }
          }
        } catch (InterruptedException ie) {
          Logger.warn("CommandWatcher InterruptedException! " + this);
          break;
        } catch (Exception ie) {
          String s = "script processing ERROR:\n\n" + ie.toString();
          for (int i = 0; i < ie.getStackTrace().length; i++) {
            s += "\n" + ie.getStackTrace()[i].toString();
          }
          Logger.warn("CommandWatcher Exception! " + s);
          break;
        }
      }
      commandWatcherThread = null;
    }
  }

  void interruptQueueThreads() {
    for (int i = 0; i < queueThreads.length; i++) {
      if (queueThreads[i] != null)
        queueThreads[i].interrupt();
    }
  }
}
