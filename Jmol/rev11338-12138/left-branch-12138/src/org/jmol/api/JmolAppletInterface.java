
package org.jmol.api;

import java.awt.Graphics;

import netscape.javascript.JSObject;



public interface JmolAppletInterface {

  public Graphics setStereoGraphics(boolean isStereo);
  public String getPropertyAsString(String infoType);
  public String getPropertyAsString(String infoType, String paramInfo);
  public String getPropertyAsJSON(String infoType);
  public String getPropertyAsJSON(String infoType, String paramInfo);
  public Object getProperty(String infoType);
  public Object getProperty(String infoType, String paramInfo);
  public String loadInlineString(String strModel, String script, boolean isAppend);
  public String loadInlineArray(String[] strModels, String script, boolean isAppend);
  public String loadNodeId(String nodeId);
  public String loadDOMNode(JSObject DOMNode);
  public void script(String script);
  public String scriptNoWait(String script);
  public String scriptCheck(String script);
  public String scriptWait(String script);
  public String scriptWait(String script, String statusParams);
  public String scriptWaitOutput(String script);
  public void syncScript(String script);

  
  
  
  
  
  public String loadInline(String strModel);

   
  public String loadInline(String[] strModels);

  
  public String loadInline(String strModel, String script);

  
  public String loadInline(String[] strModels, String script);
}
