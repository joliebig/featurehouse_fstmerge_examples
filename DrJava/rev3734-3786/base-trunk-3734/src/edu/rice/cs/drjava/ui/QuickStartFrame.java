

package edu.rice.cs.drjava.ui;

import edu.rice.cs.drjava.platform.PlatformFactory;

import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import java.net.URL;
import java.net.MalformedURLException;


public class QuickStartFrame extends HelpFrame {
  private static final String HELP_PATH = "/edu/rice/cs/drjava/docs/quickstart/";
  protected static final URL INTRO_URL =
    HTMLFrame.class.getResource(HELP_PATH + HOME_PAGE);
 
  
  public QuickStartFrame() {
    super("QuickStart Guide to DrJava", INTRO_URL,
          QuickStartFrame.class.getResource(HELP_PATH + CONTENTS_PAGE),
          ICON);
    addHyperlinkListener(_linkListener);
  }
  
   
  private HyperlinkListener _linkListener = new HyperlinkListener() {
    public void hyperlinkUpdate(HyperlinkEvent event) {
      if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
        
        URL url = event.getURL();
        String protocol = url.getProtocol();

        if ((!"file".equals(protocol)) && (!"jar".equals(protocol))) {
          
          
          
          PlatformFactory.ONLY.openURL(url);
          return; 
        }

        
        String path = url.getPath();

        if (path.indexOf(HELP_PATH+CONTENTS_PAGE) >= 0) {
          try {
            url = new URL(url,HOME_PAGE); 
          }
          catch(MalformedURLException murle) {
          }
        }
        else if (path.indexOf(HELP_PATH) < 0) {
          
          return;
        }
        if (url.sameFile(_history.contents)) {
          return; 
        }
        jumpTo(url);
      }
    }
  };

  
}
