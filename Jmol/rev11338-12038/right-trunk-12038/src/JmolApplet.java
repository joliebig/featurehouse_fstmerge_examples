



import java.awt.Graphics;

import org.jmol.api.JmolAppletInterface;
import netscape.javascript.JSObject;

public class JmolApplet extends org.jmol.appletwrapper.AppletWrapper implements
    JmolAppletInterface {

  
  
  
  
  
  public JmolApplet() {
    super("org.jmol.applet.Jmol", "jmol75x29x8.gif", 3, preloadClasses);
    
    
  }

  private final static String[] preloadClasses = { "javax.vecmath.Point3f+",
      ".Vector3f+", ".Matrix3f+", ".Point3i+", "org.jmol.g3d.Graphics3D",
      ".Sphere3D", ".Line3D", ".Cylinder3D", ".Colix3D", ".Shade3D",
      "org.jmol.adapter.smarter.SmarterJmolAdapter",
      "org.jmol.adapter.smarter.Atom", ".Bond", ".AtomSetCollection",
      ".AtomSetCollectionReader", ".Resolver", "org.jmol.popup.JmolPopup", };

  public String getPropertyAsString(String infoType) {
    return (wrappedApplet == null ? null : ""
        + wrappedApplet.getPropertyAsString("" + infoType));
  }

  public String getPropertyAsString(String infoType, String paramInfo) {
    return (wrappedApplet == null ? null : ""
        + wrappedApplet.getPropertyAsString("" + infoType, "" + paramInfo));
  }

  public String getPropertyAsJSON(String infoType) {
    return (wrappedApplet == null ? null : ""
        + wrappedApplet.getPropertyAsJSON("" + infoType));
  }

  public String getPropertyAsJSON(String infoType, String paramInfo) {
    return (wrappedApplet == null ? null : ""
        + wrappedApplet.getPropertyAsJSON("" + infoType, "" + paramInfo));
  }

  public Object getProperty(String infoType) {
    return (wrappedApplet == null ? null : wrappedApplet.getProperty(""
        + infoType));
  }

  public Object getProperty(String infoType, String paramInfo) {
    return (wrappedApplet == null ? null : wrappedApplet.getProperty(""
        + infoType, "" + paramInfo));
  }

  public String loadInlineArray(String[] strModels, String script, boolean isAppend) {
    if (wrappedApplet == null || strModels == null || strModels.length == 0)
        return null;
      String s = "" + strModels[0];
      if (s.indexOf('\n') >= 0 || s.indexOf('\r') >= 0) {
        String[] converted = new String[strModels.length];
        for (int i = 0; i < strModels.length; ++i)
          converted[i] = "" + strModels[i];
        return wrappedApplet.loadInlineArray(converted, "" + script, isAppend);
      }
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < strModels.length; ++i)
        sb.append(strModels[i]).append('\n');
      return wrappedApplet.loadInlineString(sb.toString(), "" + script, isAppend);
  }

  public String loadInlineString(String strModel, String script, boolean isAppend) {
    return (wrappedApplet == null ? null :
      wrappedApplet.loadInlineString("" + strModel, "" + script, isAppend));
  }

  
  
  

  
  public String loadInline(String strModel) {
    return (wrappedApplet == null ? null :
      wrappedApplet.loadInline("" + strModel));
  }

  
  public String loadInline(String strModel, String script) {
    return (wrappedApplet == null ? null :
      wrappedApplet.loadInline("" + strModel, "" + script));
  }

  
  public String loadInline(String[] strModels) {
    return (wrappedApplet == null ? null :
      wrappedApplet.loadInline(strModels));
  }

  
  public String loadInline(String[] strModels, String script) {
    return (wrappedApplet == null ? null :
      wrappedApplet.loadInline(strModels, script));
  }

  public String loadNodeId(String nodeId) {
    return (wrappedApplet == null ? null :
      wrappedApplet.loadNodeId("" + nodeId));
  }

  public String loadDOMNode(JSObject DOMNode) {
    return (wrappedApplet == null ? null : wrappedApplet.loadDOMNode(DOMNode));
  }

  public void script(String script) {
    if (wrappedApplet != null)
      wrappedApplet.script("" + script);
  }

  public void syncScript(String script) {
    if (wrappedApplet != null)
      wrappedApplet.syncScript("" + script);
  }

  public Graphics setStereoGraphics(boolean isStereo) {
    return (wrappedApplet == null ? null : 
        wrappedApplet.setStereoGraphics(isStereo));
  }

  public String scriptNoWait(String script) {
    if (wrappedApplet != null)
      return "" + (wrappedApplet.scriptNoWait("" + script));
    return null;
  }

  public String scriptCheck(String script) {
    if (wrappedApplet != null)
      return "" + (wrappedApplet.scriptCheck("" + script));
    return null;
  }

  public String scriptWait(String script) {
    if (wrappedApplet != null)
      return "" + (wrappedApplet.scriptWait("" + script));
    return null;
  }

  public String scriptWait(String script, String statusParams) {
    if (statusParams == null)
      statusParams = "";
    if (wrappedApplet != null)
      return "" + (wrappedApplet.scriptWait("" + script, statusParams));
    return null;
  }
  
  public String scriptWaitOutput(String script) {
    if (wrappedApplet != null)
      return "" + (wrappedApplet.scriptWaitOutput("" + script));
    return null;
  }

}
