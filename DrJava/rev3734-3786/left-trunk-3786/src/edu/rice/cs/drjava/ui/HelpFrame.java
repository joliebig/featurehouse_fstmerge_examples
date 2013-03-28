

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.CodeStatus;
import edu.rice.cs.drjava.platform.PlatformFactory;

import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import java.net.URL;
import java.net.MalformedURLException;


public class HelpFrame extends HTMLFrame {
  private static final String HELP_PATH =  "/edu/rice/cs/drjava/docs/user/";
  protected static final String CONTENTS_PAGE = "index.html";
  protected static final String HOME_PAGE = "intro.html";
  private static final URL INTRO_URL =
    HTMLFrame.class.getResource(HELP_PATH + HOME_PAGE);
  protected static final String ICON = "DrJavaHelp.png";

  public HelpFrame() {
    super("Help on using DrJava", INTRO_URL, HelpFrame.class.getResource(HELP_PATH + CONTENTS_PAGE), ICON);
    addHyperlinkListener(_linkListener);

  }
  
  
  public HelpFrame(String frameName, URL introUrl, URL indexUrl, String iconString) {
    super(frameName, introUrl, indexUrl, iconString);
  }
  

  protected String getErrorText(URL url) {
    
    String errorText = "The Help files are currently unavailable.";
    if (CodeStatus.DEVELOPMENT) {  
      errorText += "\n\nTo generate the help files, run the \"ant docs\" target" +
        " after compiling DrJava.";
    }
    return errorText;
  }

  
  private HyperlinkListener _linkListener = new HyperlinkListener() {
    public void hyperlinkUpdate(HyperlinkEvent event) {
      if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        
        URL url = event.getURL();
        String protocol = url.getProtocol();

        if (!"file".equals(protocol) && !"jar".equals(protocol)) {
          
          
          
          PlatformFactory.ONLY.openURL(url);
          return; 
        }

        
        String path = url.getPath();

        if (path.indexOf(HELP_PATH+CONTENTS_PAGE) >= 0) {
          try { url = new URL(url,HOME_PAGE); } 
          catch(MalformedURLException murle) {
            
          }
        }
        else if (path.indexOf(HELP_PATH) < 0) return; 
          
        if (url.sameFile(_history.contents)) return; 
        jumpTo(url);
      }
    }
  };
}
