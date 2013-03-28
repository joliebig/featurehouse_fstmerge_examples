

package org.jmol.applet;

import org.jmol.api.*;
import org.jmol.appletwrapper.*;
import org.jmol.i18n.GT;
import org.jmol.viewer.JmolConstants;
import org.jmol.viewer.Viewer;
import org.jmol.util.Escape;
import org.jmol.util.Logger;
import org.jmol.util.Parser;

import java.awt.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.UIManager;

import netscape.javascript.JSObject;



public class Jmol implements WrappedApplet {

  boolean mayScript;
  boolean haveDocumentAccess;
  boolean loading;

  String[] callbacks = new String[JmolConstants.CALLBACK_COUNT];

  String language;
  String htmlName;
  String fullName;
  String syncId;
  String languagePath;

  AppletWrapper appletWrapper;
  protected JmolViewer viewer;

  private final static boolean REQUIRE_PROGRESSBAR = true;
  private boolean hasProgressBar;

  protected boolean doTranslate = true;

  private String statusForm;
  private String statusText;
  private String statusTextarea;

  private int paintCounter;

  

  
  public void setAppletWrapper(AppletWrapper appletWrapper) {
    this.appletWrapper = appletWrapper;
  }

  
  
  
  

  public void init() {
    htmlName = getParameter("name");
    syncId = getParameter("syncId");
    fullName = htmlName + "__" + syncId + "__";
    System.out.println("Jmol applet " + fullName + " initializing");
    setLogging();

    String ms = getParameter("mayscript");
    mayScript = (ms != null) && (!ms.equalsIgnoreCase("false"));
    JmolAppletRegistry.checkIn(fullName, appletWrapper);
    initWindows();
    initApplication();
  }

  public void destroy() {
    gRight = null;
    JmolAppletRegistry.checkOut(fullName);
    viewer.setModeMouse(JmolConstants.MOUSE_NONE);
    viewer = null;
    System.out.println("Jmol applet " + fullName + " destroyed");
  }

  String getParameter(String paramName) {
    return appletWrapper.getParameter(paramName);
  }

  public Graphics setStereoGraphics(boolean isStereo) {
    isStereoSlave = isStereo;
    return (isStereo ? appletWrapper.getGraphics() : null);
  }

  boolean isSigned;

  public void initWindows() {

    String options = "-applet";
    isSigned = getBooleanValue("signed", false) || appletWrapper.isSigned();
    if (isSigned)
      options += "-signed";
    if (getBooleanValue("useCommandThread", isSigned))
      options += "-threaded";
    if (isSigned && getBooleanValue("multiTouchSparshUI-simulated", false))
      options += "-multitouch-sparshui-simulated";
    else if (isSigned && getBooleanValue("multiTouchSparshUI", false))
      options += "-multitouch-sparshui";
    String s = getValue("MaximumSize", null);
    if (s != null)
      options += "-maximumSize " + s;
    
    s = getValue("JmolAppletProxy", null);
    if (s != null)
      options += "-appletProxy " + s;
    viewer = JmolViewer.allocateViewer(appletWrapper, null, fullName,
        appletWrapper.getDocumentBase(), appletWrapper.getCodeBase(), options,
        new MyStatusListener());
    String menuFile = getParameter("menuFile");
    if (menuFile != null)
      viewer.getProperty("DATA_API", "setMenu", viewer
          .getFileAsString(menuFile));
    try {
      UIManager.setLookAndFeel(UIManager
          .getCrossPlatformLookAndFeelClassName());
    } catch (Exception exc) {
      System.err.println("Error loading L&F: " + exc);
    }
    if (Logger.debugging) {
      Logger.debug("checking for jsoWindow mayScript=" + mayScript);
    }
    if (mayScript) {
      mayScript = haveDocumentAccess = false;
      JSObject jsoWindow = null;
      JSObject jsoDocument = null;
      try {
        jsoWindow = JSObject.getWindow(appletWrapper);
        if (Logger.debugging) {
          Logger.debug("jsoWindow=" + jsoWindow);
        }
        if (jsoWindow == null) {
          Logger
              .error("jsoWindow returned null ... no JavaScript callbacks :-(");
        } else {
          mayScript = true;
        }
        jsoDocument = (JSObject) jsoWindow.getMember("document");
        if (jsoDocument == null) {
          Logger
              .error("jsoDocument returned null ... no DOM manipulations :-(");
        } else {
          haveDocumentAccess = true;
        }
      } catch (Exception e) {
        Logger
            .error("Microsoft MSIE bug -- http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=5012558 "
                + e);
      }
      if (Logger.debugging) {
        Logger.debug("jsoWindow:" + jsoWindow + " jsoDocument:" + jsoDocument
            + " mayScript:" + mayScript + " haveDocumentAccess:"
            + haveDocumentAccess);
      }
    }
  }

  private void setLogging() {
    int iLevel = (getValue("logLevel", (getBooleanValue("debug", false) ? "5"
        : "4"))).charAt(0) - '0';
    if (iLevel != 4)
      System.out.println("setting logLevel=" + iLevel
          + " -- To change, use script \"set logLevel [0-5]\"");
    Logger.setLogLevel(iLevel);
  }

  private boolean getBooleanValue(String propertyName, boolean defaultValue) {
    String value = getValue(propertyName, defaultValue ? "true" : "");
    return (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("on") || value
        .equalsIgnoreCase("yes"));
  }

  private String getValue(String propertyName, String defaultValue) {
    String stringValue = getParameter(propertyName);
    if (stringValue != null)
      return stringValue;
    return defaultValue;
  }

  private String getValueLowerCase(String paramName, String defaultValue) {
    String value = getValue(paramName, defaultValue);
    if (value != null) {
      value = value.trim().toLowerCase();
      if (value.length() == 0)
        value = null;
    }
    return value;
  }

  public void initApplication() {
    viewer.pushHoldRepaint();
    {
      
      hasProgressBar = getBooleanValue("progressbar", false);
      String emulate = getValueLowerCase("emulate", "jmol");
      setStringProperty("defaults", emulate.equals("chime") ? "RasMol" : "Jmol");
      setStringProperty("backgroundColor", getValue("bgcolor", getValue(
          "boxbgcolor", "black")));

      viewer.setBooleanProperty("frank", true);
      loading = true;
      for (int i = 0; i < JmolConstants.CALLBACK_COUNT; i++) {
        String name = JmolConstants.getCallbackName(i);
        setValue(name, null);
      }
      loading = false;

      language = getParameter("language");
      if (language != null) {
        System.out.print("requested language=" + language + "; ");
        new GT(language);
      }
      doTranslate = (!"none".equals(language) && getBooleanValue("doTranslate",
          true));
      language = GT.getLanguage();
      System.out.println("language=" + language);

      boolean haveCallback = false;
      
      for (int i = 0; i < JmolConstants.CALLBACK_COUNT && !haveCallback; i++)
        haveCallback = (callbacks[i] != null);
      if (haveCallback || statusForm != null || statusText != null) {
        if (!mayScript)
          Logger
              .warn("MAYSCRIPT missing -- all applet JavaScript calls disabled");
      }
      if (callbacks[JmolConstants.CALLBACK_SCRIPT] == null
          && callbacks[JmolConstants.CALLBACK_ERROR] == null)
        if (callbacks[JmolConstants.CALLBACK_MESSAGE] != null
            || statusForm != null || statusText != null) {
          if (doTranslate && (getValue("doTranslate", null) == null)) {
            doTranslate = false;
            Logger
                .warn("Note -- Presence of message callback disables disable translation;"
                    + " to enable message translation use jmolSetTranslation(true) prior to jmolApplet()");
          }
          if (doTranslate)
            Logger
                .warn("Note -- Automatic language translation may affect parsing of message callbacks"
                    + " messages; use scriptCallback or errorCallback to process errors");
        }

      if (!doTranslate) {
        GT.setDoTranslate(false);
        Logger.warn("Note -- language translation disabled");
      }

      statusForm = getValue("StatusForm", null);
      statusText = getValue("StatusText", null); 
      statusTextarea = getValue("StatusTextarea", null); 

      if (statusForm != null && statusText != null) {
        Logger.info("applet text status will be reported to document."
            + statusForm + "." + statusText);
      }
      if (statusForm != null && statusTextarea != null) {
        Logger.info("applet textarea status will be reported to document."
            + statusForm + "." + statusTextarea);
      }

      
      if (!getBooleanValue("popupMenu", true))
        viewer.getProperty("DATA_API", "disablePopupMenu", null);
      loadNodeId(getValue("loadNodeId", null));

      String loadParam;
      String scriptParam = getValue("script", "");
      if ((loadParam = getValue("loadInline", null)) != null) {
        loadInlineSeparated(loadParam, (scriptParam.length() > 0 ? scriptParam
            : null));
      } else {
        if ((loadParam = getValue("load", null)) != null)
          scriptParam = "load \"" + loadParam + "\";" + scriptParam;
        if (scriptParam.length() > 0)
          scriptProcessor(scriptParam, null, SCRIPT_NOWAIT);
      }
    }
    viewer.popHoldRepaint();
  }

  private void setValue(String name, String defaultValue) {
    setStringProperty(name, getValue(name, defaultValue));
  }

  private void setStringProperty(String name, String value) {
    if (value == null)
      return;
    Logger.info(name + " = \"" + value + "\"");
    viewer.setStringProperty(name, value);
  }

  void sendJsTextStatus(String message) {
    if (!haveDocumentAccess || statusForm == null || statusText == null)
      return;
    try {
      JSObject jsoWindow = JSObject.getWindow(appletWrapper);
      JSObject jsoDocument = (JSObject) jsoWindow.getMember("document");
      JSObject jsoForm = (JSObject) jsoDocument.getMember(statusForm);
      if (statusText != null) {
        JSObject jsoText = (JSObject) jsoForm.getMember(statusText);
        jsoText.setMember("value", message);
      }
    } catch (Exception e) {
      Logger.error("error indicating status at document." + statusForm + "."
          + statusText + ":" + e.toString());
    }
  }

  void sendJsTextareaStatus(String message) {
    if (!haveDocumentAccess || statusForm == null || statusTextarea == null)
      return;
    try {
      JSObject jsoWindow = JSObject.getWindow(appletWrapper);
      JSObject jsoDocument = (JSObject) jsoWindow.getMember("document");
      JSObject jsoForm = (JSObject) jsoDocument.getMember(statusForm);
      if (statusTextarea != null) {
        JSObject jsoTextarea = (JSObject) jsoForm.getMember(statusTextarea);
        if (message == null) {
          jsoTextarea.setMember("value", "");
        } else {
          String info = (String) jsoTextarea.getMember("value");
          jsoTextarea.setMember("value", info + "\n" + message);
        }
      }
    } catch (Exception e) {
      Logger.error("error indicating status at document." + statusForm + "."
          + statusTextarea + ":" + e.toString());
    }
  }

  public boolean showPaintTime = false;
  public void paint(Graphics g) {
    
    
    update(g, "paint ");
  }

  private boolean isUpdating;

  public void update(Graphics g) {
    
    update(g, "update");
  }

  protected Graphics gRight;
  protected boolean isStereoSlave;
  
  private void update(Graphics g, String source) {
    if (viewer == null) 
      return;
    if (isUpdating)
      return;

    
    

    

    isUpdating = true;
    if (showPaintTime)
      startPaintClock();
    Dimension size = new Dimension();
    appletWrapper.getSize(size);
    viewer.setScreenDimension(size);
    
    ++paintCounter;
    if (REQUIRE_PROGRESSBAR && !isSigned && !hasProgressBar
        && paintCounter < 30 && (paintCounter & 1) == 0) {
      printProgressbarMessage(g);
      viewer.notifyViewerRepaintDone();
    } else {
      
      
      if (!isStereoSlave)
        viewer.renderScreenImage(g, gRight, size, null);
      
    }

    if (showPaintTime) {
      stopPaintClock();
      showTimes(10, 10, g);
    }
    isUpdating = false;
  }

  private final static String[] progressbarMsgs = {
      "Jmol developer alert!",
      "",
      "Please use jmol.js. You are missing the ",
      "required 'progressbar' parameter.",
      "  <param name='progressbar' value='true' />", };

  private void printProgressbarMessage(Graphics g) {
    g.setColor(Color.yellow);
    g.fillRect(0, 0, 10000, 10000);
    g.setColor(Color.black);
    for (int i = 0, y = 13; i < progressbarMsgs.length; ++i, y += 13) {
      g.drawString(progressbarMsgs[i], 10, y);
    }
  }

  public boolean handleEvent(Event e) {
    if (viewer == null)
      return false;
    return viewer.handleOldJvm10Event(e);
  }

  
  

  private int timeLast, timeCount, timeTotal;
  private long timeBegin;

  private int lastMotionEventNumber;

  private void startPaintClock() {
    timeBegin = System.currentTimeMillis();
    int motionEventNumber = viewer.getMotionEventNumber();
    if (lastMotionEventNumber != motionEventNumber) {
      lastMotionEventNumber = motionEventNumber;
      timeCount = timeTotal = 0;
      timeLast = -1;
    }
  }

  private void stopPaintClock() {
    int time = (int) (System.currentTimeMillis() - timeBegin);
    if (timeLast != -1) {
      timeTotal += timeLast;
      ++timeCount;
    }
    timeLast = time;
  }

  private String fmt(int num) {
    if (num < 0)
      return "---";
    if (num < 10)
      return "  " + num;
    if (num < 100)
      return " " + num;
    return "" + num;
  }

  private void showTimes(int x, int y, Graphics g) {
    int timeAverage = (timeCount == 0) ? -1 : (timeTotal + timeCount / 2)
        / timeCount; 
    g.setColor(Color.green);
    g.drawString(fmt(timeLast) + "ms : " + fmt(timeAverage) + "ms", x, y);
  }

  private final static int SCRIPT_CHECK = 0;
  private final static int SCRIPT_WAIT = 1;
  private final static int SCRIPT_NOWAIT = 2;

  private String scriptProcessor(String script, String statusParams,
                                 int processType) {
    
    if (script == null || script.length() == 0)
      return "";
    switch (processType) {
    case SCRIPT_CHECK:
      Object err = viewer.scriptCheck(script);
      return (err instanceof String ? (String) err : "");
    case SCRIPT_WAIT:
      if (statusParams != null)
        return viewer.scriptWaitStatus(script, statusParams).toString();
      return viewer.scriptWait(script);
    case SCRIPT_NOWAIT:
    default:
      return viewer.script(script);
    }
  }

  public void script(String script) {
    if (script == null || script.length() == 0)
      return;
    scriptProcessor(script, null, SCRIPT_NOWAIT);
  }

  public String scriptCheck(String script) {
    if (script == null || script.length() == 0)
      return "";
    return scriptProcessor(script, null, SCRIPT_CHECK);
  }

  public String scriptNoWait(String script) {
    if (script == null || script.length() == 0)
      return "";
    return scriptProcessor(script, null, SCRIPT_NOWAIT);
  }

  public String scriptWait(String script) {
    if (script == null || script.length() == 0)
      return "";
    outputBuffer = null;
    return scriptProcessor(script, null, SCRIPT_WAIT);
  }

  StringBuffer outputBuffer;
  
  public String scriptWait(String script, String statusParams) {
    if (script == null || script.length() == 0)
      return "";
    outputBuffer = null;
    return scriptProcessor(script, statusParams, SCRIPT_WAIT);
  }

  public String scriptWaitOutput(String script) {
    if (script == null || script.length() == 0)
      return "";
    outputBuffer = new StringBuffer();
    viewer.scriptWaitStatus(script, "");
    String str = (outputBuffer == null ? "" : outputBuffer.toString());
    outputBuffer = null;
    return str;
  }

  synchronized public void syncScript(String script) {
    viewer.syncScript(script, "~");
  }

  public String getAppletInfo() {
    return GT
        ._(
            "Jmol Applet version {0} {1}.\n\nAn OpenScience project.\n\nSee http://www.jmol.org for more information",
            new Object[] { JmolConstants.version, JmolConstants.date })
        + "\nhtmlName = "
        + Escape.escape(htmlName)
        + "\nsyncId = "
        + Escape.escape(syncId)
        + "\ndocumentBase = "
        + Escape.escape("" + appletWrapper.getDocumentBase())
        + "\ncodeBase = "
        + Escape.escape("" + appletWrapper.getCodeBase());
  }

  public Object getProperty(String infoType) {
    return viewer.getProperty(null, infoType, "");
  }

  public Object getProperty(String infoType, String paramInfo) {
    return viewer.getProperty(null, infoType, paramInfo);
  }

  public String getPropertyAsString(String infoType) {
    return viewer.getProperty("readable", infoType, "").toString();
  }

  public String getPropertyAsString(String infoType, String paramInfo) {
    return viewer.getProperty("readable", infoType, paramInfo).toString();
  }

  public String getPropertyAsJSON(String infoType) {
    return viewer.getProperty("JSON", infoType, "").toString();
  }

  public String getPropertyAsJSON(String infoType, String paramInfo) {
    return viewer.getProperty("JSON", infoType, paramInfo).toString();
  }

  public String loadInlineString(String strModel, String script, boolean isAppend) {
    String errMsg = viewer.loadInline(strModel, isAppend);
    if (errMsg == null)
      script(script);
    return errMsg;
  }

  public String loadInlineArray(String[] strModels, String script,
                              boolean isAppend) {
    if (strModels == null || strModels.length == 0)
      return null;
    String errMsg = viewer.loadInline(strModels, isAppend);
    if (errMsg == null)
      script(script);
    return errMsg;
  }

  
  public String loadInline(String strModel) {
    return loadInlineString(strModel, "", false);
  }

  
  public String loadInline(String strModel, String script) {
    return loadInlineString(strModel, script, false);
  }

  
  public String loadInline(String[] strModels) {
    return loadInlineArray(strModels, "", false);
  }

  
  public String loadInline(String[] strModels, String script) {
    return loadInlineArray(strModels, script, false);
  }

  private String loadInlineSeparated(String strModel, String script) {
    
    if (strModel == null)
      return null;
    String errMsg = viewer.loadInline(strModel);
    if (errMsg == null)
      script(script);
    return errMsg;
  }

  public String loadDOMNode(JSObject DOMNode) {
    
    
    
    return viewer.openDOM(DOMNode);
  }

  public String loadNodeId(String nodeId) {
    if (!haveDocumentAccess)
      return "ERROR: NO DOCUMENT ACCESS";
    if (nodeId == null)
      return null;
    
    
    Object[] idArgs = { nodeId };
    JSObject tryNode = null;
    try {
      JSObject jsoWindow = JSObject.getWindow(appletWrapper);
      JSObject jsoDocument = (JSObject) jsoWindow.getMember("document");
      tryNode = (JSObject) jsoDocument.call("getElementById", idArgs);

      
      
      if (tryNode == null) {
        Object[] searchArgs = { "http://www.xml-cml.org/schema/cml2/core",
            "cml" };
        JSObject tryNodeList = (JSObject) jsoDocument.call(
            "getElementsByTagNameNS", searchArgs);
        if (tryNodeList != null) {
          for (int i = 0; i < ((Number) tryNodeList.getMember("length"))
              .intValue(); i++) {
            tryNode = (JSObject) tryNodeList.getSlot(i);
            Object[] idArg = { "id" };
            String idValue = (String) tryNode.call("getAttribute", idArg);
            if (nodeId.equals(idValue))
              break;
            tryNode = null;
          }
        }
      }
    } catch (Exception e) {
      return "" + e;
    }
    return (tryNode == null ? "ERROR: No CML node" : loadDOMNode(tryNode));
  }

  class MyStatusListener implements JmolStatusListener {

    public boolean notifyEnabled(int type) {
      switch (type) {
      case JmolConstants.CALLBACK_ANIMFRAME:
      case JmolConstants.CALLBACK_ECHO:
      case JmolConstants.CALLBACK_ERROR:
      case JmolConstants.CALLBACK_EVAL:
      case JmolConstants.CALLBACK_LOADSTRUCT:
      case JmolConstants.CALLBACK_MEASURE:
      case JmolConstants.CALLBACK_MESSAGE:
      case JmolConstants.CALLBACK_PICK:
      case JmolConstants.CALLBACK_SYNC:
      case JmolConstants.CALLBACK_SCRIPT:
        return true;
      case JmolConstants.CALLBACK_CLICK:
      case JmolConstants.CALLBACK_HOVER:
      case JmolConstants.CALLBACK_MINIMIZATION:
      case JmolConstants.CALLBACK_RESIZE:
      }
      return (callbacks[type] != null);
    }

    private boolean haveNotifiedError;

    public void notifyCallback(int type, Object[] data) {

      String callback = (type < callbacks.length ? callbacks[type] : null);
      boolean doCallback = (callback != null && (data == null || data[0] == null));
      if (data != null)
        data[0] = htmlName;
      String strInfo = (data == null || data[1] == null ? null : data[1]
          .toString());

      
      
      switch (type) {
      case JmolConstants.CALLBACK_MINIMIZATION:
      case JmolConstants.CALLBACK_RESIZE:
      case JmolConstants.CALLBACK_EVAL:
      case JmolConstants.CALLBACK_HOVER:
      case JmolConstants.CALLBACK_ERROR:
        
        break;
      case JmolConstants.CALLBACK_CLICK:
        
        
        if ("alert".equals(callback))
          strInfo = "x=" + data[1] + " y=" + data[2] + " action=" + data[3] + " clickCount=" + data[4];
        break;
      case JmolConstants.CALLBACK_ANIMFRAME:
        
        
        
        int[] iData = (int[]) data[1];
        int frameNo = iData[0];
        int fileNo = iData[1];
        int modelNo = iData[2];
        int firstNo = iData[3];
        int lastNo = iData[4];
        boolean isAnimationRunning = (frameNo <= -2);
        int animationDirection = (firstNo < 0 ? -1 : 1);
        int currentDirection = (lastNo < 0 ? -1 : 1);

        
        if (doCallback) {
          data = new Object[] { htmlName,
              new Integer(Math.max(frameNo, -2 - frameNo)),
              new Integer(fileNo), new Integer(modelNo),
              new Integer(Math.abs(firstNo)), new Integer(Math.abs(lastNo)),
              new Integer(isAnimationRunning ? 1 : 0),
              new Integer(animationDirection), new Integer(currentDirection) };
        }
        break;
      case JmolConstants.CALLBACK_ECHO:
        boolean isScriptQueued = (((Integer) data[2]).intValue() == 1);
        boolean doOutput = true;
        if (!doCallback) {
          if (isScriptQueued) {
            consoleMessage(strInfo);
            doOutput = false;
          }
          doCallback = ((callback = callbacks[type = JmolConstants.CALLBACK_MESSAGE]) != null);
        }
        if (doOutput)
          output(strInfo);
        break;
      case JmolConstants.CALLBACK_LOADSTRUCT:
        String errorMsg = (String) data[4];
        if (errorMsg != null) {
          showStatusAndConsole((errorMsg.indexOf("NOTE:") >= 0 ? "" : GT
              ._("File Error:"))
              + errorMsg, true);
          return;
        }
        break;
      case JmolConstants.CALLBACK_MEASURE:
        
        if (!doCallback)
          doCallback = ((callback = callbacks[type = JmolConstants.CALLBACK_MESSAGE]) != null);
        String status = (String) data[3];
        if (status.indexOf("Picked") >= 0) {
          showStatusAndConsole(strInfo, true); 
        } else if (status.indexOf("Completed") >= 0) {
          strInfo = status + ": " + strInfo;
          consoleMessage(strInfo);
        }
        break;
      case JmolConstants.CALLBACK_MESSAGE:
        if (doCallback)
          output(strInfo);
        else
          consoleMessage(strInfo);
        if (strInfo == null)
          return;
        break;
      case JmolConstants.CALLBACK_PICK:
        showStatusAndConsole(strInfo, true);
        break;
      case JmolConstants.CALLBACK_SCRIPT:
        int msWalltime = ((Integer) data[3]).intValue();
        
        
        
        
        
        boolean toConsole = (msWalltime == 0);
        if (msWalltime > 0) {
          
          notifyScriptTermination();
        } else if (!doCallback) {
          
          
          
          doCallback = ((callback = callbacks[type = JmolConstants.CALLBACK_MESSAGE]) != null);
        }
        showStatusAndConsole(strInfo, toConsole);
        break;
      case JmolConstants.CALLBACK_SYNC:
        sendScript(strInfo, (String) data[2], true, doCallback);
        return;
      }
      if (!doCallback || !mayScript)
        return;
      try {
        JSObject jsoWindow = JSObject.getWindow(appletWrapper);
        if (callback.equals("alert"))
          jsoWindow.call(callback, new Object[] { strInfo });
        else if (callback.length() > 0)
          jsoWindow.call(callback, data);
      } catch (Exception e) {
        if (!haveNotifiedError)
          if (Logger.debugging) {
            Logger.debug(JmolConstants.getCallbackName(type)
                + " call error to " + callback + ": " + e);
          }
        haveNotifiedError = true;
      }
    }

    private void output(String s) {
      if (outputBuffer != null && s != null)
        outputBuffer.append(s).append('\n');
    }

    private void notifyScriptTermination() {
      
    }

    private String notifySync(String info, String appletName) {
      String syncCallback = callbacks[JmolConstants.CALLBACK_SYNC];
      if (!mayScript || syncCallback == null)
        return info;
      try {
        JSObject jsoWindow = JSObject.getWindow(appletWrapper);
        if (syncCallback.length() > 0)
          return "" + jsoWindow.call(syncCallback, new Object[] { htmlName,
              info, appletName });
      } catch (Exception e) {
        if (!haveNotifiedError)
          if (Logger.debugging) {
            Logger.debug("syncCallback call error to " + syncCallback + ": "
                + e);
          }
        haveNotifiedError = true;
      }
      return info;
    }

    public void setCallbackFunction(String callbackName, String callbackFunction) {
      
      if (callbackName.equalsIgnoreCase("language")) {
        clearDefaultConsoleMessage();
      }
      int id = JmolConstants.getCallbackId(callbackName);
      if (id >= 0 && (loading || id != JmolConstants.CALLBACK_EVAL)) {
        callbacks[id] = callbackFunction;
        return;
      }
      String s = "";
      for (int i = 0; i < JmolConstants.CALLBACK_COUNT; i++)
        s += " " + JmolConstants.getCallbackName(i);
      consoleMessage("Available callbacks include: " + s);
    }

    protected void finalize() throws Throwable {
      Logger.debug("MyStatusListener finalize " + this);
      super.finalize();
    }

    public String eval(String strEval) {
      
      int pt = strEval.indexOf("\1");
      if (pt >= 0)
        return sendScript(strEval.substring(pt + 1), strEval.substring(0, pt),
            false, false);
      if (!haveDocumentAccess)
        return "NO EVAL ALLOWED";
      JSObject jsoWindow = null;
      JSObject jsoDocument = null;
      try {
        jsoWindow = JSObject.getWindow(appletWrapper);
        jsoDocument = (JSObject) jsoWindow.getMember("document");
      } catch (Exception e) {
        if (Logger.debugging)
          Logger.debug(" error setting jsoWindow or jsoDocument:" + jsoWindow
              + ", " + jsoDocument);
        return "NO EVAL ALLOWED";
      }
      if (callbacks[JmolConstants.CALLBACK_EVAL] != null) {
        notifyCallback(JmolConstants.CALLBACK_EVAL, new Object[] { null,
            strEval });
        return "";
      }
      try {
        if (!haveDocumentAccess
            || ((Boolean) jsoDocument.eval("!!_jmol.noEval")).booleanValue())
          return "NO EVAL ALLOWED";
      } catch (Exception e) {
        Logger
            .error("# no _jmol in evaluating " + strEval + ":" + e.toString());
        return "";
      }
      try {
        return "" + jsoDocument.eval(strEval);
      } catch (Exception e) {
        Logger.error("# error evaluating " + strEval + ":" + e.toString());
      }
      return "";
    }

    
    public String createImage(String fileName, String type, Object text_or_bytes,
                              int quality) {
      return null;
    }

    public float[][] functionXY(String functionName, int nX, int nY) {
      

      
      float[][] fxy = new float[Math.abs(nX)][Math.abs(nY)];
      if (!mayScript || nX == 0 || nY == 0)
        return fxy;
      try {
        JSObject jsoWindow = JSObject.getWindow(appletWrapper);
        if (nX > 0 && nY > 0) { 
          for (int i = 0; i < nX; i++)
            for (int j = 0; j < nY; j++) {
              fxy[i][j] = ((Double) jsoWindow.call(functionName, new Object[] {
                  htmlName, new Integer(i), new Integer(j) })).floatValue();
            }
        } else if (nY > 0) { 
          String data = (String) jsoWindow.call(functionName, new Object[] {
              htmlName, new Integer(nX), new Integer(nY) });
          
          nX = Math.abs(nX);
          float[] fdata = new float[nX * nY];
          Parser.parseFloatArray(data, null, fdata);
          for (int i = 0, ipt = 0; i < nX; i++) {
            for (int j = 0; j < nY; j++, ipt++) {
              fxy[i][j] = fdata[ipt];
            }
          }
        } else { 
          jsoWindow.call(functionName, new Object[] { htmlName,
              new Integer(nX), new Integer(nY), fxy });
        }
      } catch (Exception e) {
        Logger.error("Exception " + e.getMessage() + " with nX, nY: " + nX
            + " " + nY);
      }
     
       
         
      return fxy;
    }

    public float[][][] functionXYZ(String functionName, int nX, int nY, int nZ) {
      float[][][] fxyz = new float[Math.abs(nX)][Math.abs(nY)][Math.abs(nZ)];
      if (!mayScript || nX == 0 || nY == 0 || nZ == 0)
        return fxyz;
      try {
        JSObject jsoWindow = JSObject.getWindow(appletWrapper);
       jsoWindow.call(functionName, new Object[] { htmlName,
              new Integer(nX), new Integer(nY), new Integer(nZ), fxyz }); 
      } catch (Exception e) {
        Logger.error("Exception " + e.getMessage() + " for " + functionName + " with nX, nY, nZ: " + nX
            + " " + nY + " " + nZ);
      }
     
      
        
         
      return fxyz;
    }

    public void showUrl(String urlString) {
      if (Logger.debugging) {
        Logger.debug("showUrl(" + urlString + ")");
      }
      if (urlString != null && urlString.length() > 0) {
        try {
          URL url = new URL(urlString);
          appletWrapper.getAppletContext().showDocument(url, "_blank");
        } catch (MalformedURLException mue) {
          showStatusAndConsole("Malformed URL:" + urlString, true);
        }
      }
    }

    private void showStatusAndConsole(String message, boolean toConsole) {
      try {
        appletWrapper.showStatus(message);
        sendJsTextStatus(message);
        if (toConsole)
          consoleMessage(message);
        else
          output(message);
      } catch (Exception e) {
        
      }
    }

    private String defaultMessage;

    private void clearDefaultConsoleMessage() {
      defaultMessage = null;
    }

    private void consoleMessage(String message) {
      JmolAppConsoleInterface appConsole = (JmolAppConsoleInterface) viewer.getProperty("DATA_API", "getAppConsole", null);
      if (appConsole != null) {
        if (defaultMessage == null) {
          GT.setDoTranslate(true);
          defaultMessage = GT
              ._("Messages will appear here. Enter commands in the box below. Click the console Help menu item for on-line help, which will appear in a new browser window.");
          GT.setDoTranslate(doTranslate);
        }
        if (appConsole.getText().startsWith(defaultMessage))
          appConsole.sendConsoleMessage("");
        appConsole.sendConsoleMessage(message);
        if (message == null) {
          appConsole.sendConsoleMessage(defaultMessage);
        }
      }
      output(message);
      sendJsTextareaStatus(message);
    }

    private String sendScript(String script, String appletName, boolean isSync,
                              boolean doCallback) {
      if (doCallback) {
        script = notifySync(script, appletName);
        
        
        if (script == null || script.length() == 0 || script.equals("0"))
          return "";
      }

      Vector apps = new Vector();
      JmolAppletRegistry.findApplets(appletName, syncId, fullName, apps);
      int nApplets = apps.size();
      if (nApplets == 0) {
        if (!doCallback && !appletName.equals("*"))
          Logger.error(fullName + " couldn't find applet " + appletName);
        return "";
      }
      StringBuffer sb = (isSync ? null : new StringBuffer());
      boolean getGraphics = (isSync && script.equals(Viewer.SYNC_GRAPHICS_MESSAGE));
      boolean setNoGraphics = (isSync && script.equals(Viewer.SYNC_NO_GRAPHICS_MESSAGE));
      if (getGraphics)
        gRight = null;
      for (int i = 0; i < nApplets; i++) {
        String theApplet = (String) apps.elementAt(i);
        JmolAppletInterface app = (JmolAppletInterface) JmolAppletRegistry.htRegistry
            .get(theApplet);
        if (Logger.debugging)
          Logger.debug(fullName + " sending to " + theApplet + ": " + script);
        try {
          if (getGraphics || setNoGraphics) {
            gRight = app.setStereoGraphics(getGraphics);
            return "";
          }
          if (isSync)
            app.syncScript(script);
          else
            sb.append(app.scriptWait(script, "output")).append("\n");
        } catch (Exception e) {
          String msg = htmlName + " couldn't send to " + theApplet + ": "
              + script + ": " + e;
          Logger.error(msg);
          if (!isSync)
            sb.append(msg);
        }
      }
      return (isSync ? "" : sb.toString());
    }

    public Hashtable getRegistryInfo() {
      JmolAppletRegistry.checkIn(null, null); 
      return JmolAppletRegistry.htRegistry;
    }
  }
}
