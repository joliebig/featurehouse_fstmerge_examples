
package osxadapter;

import net.sf.jabref.JabRefFrame;
import com.apple.eawt.*;

public class OSXAdapter extends ApplicationAdapter {

  
  
  private static OSXAdapter            theAdapter;
  private static com.apple.eawt.Application    theApplication;

  
  private JabRefFrame mainApp;

  private OSXAdapter (JabRefFrame inApp) {
    mainApp = inApp;
  }

  
  
  public void handleAbout(ApplicationEvent ae) {
    if (mainApp != null) {
      ae.setHandled(true);
      mainApp.about();
    } else {
      throw new IllegalStateException("handleAbout: MyApp instance detached from listener");
    }
  }

  public void handlePreferences(ApplicationEvent ae) {
    if (mainApp != null) {
      mainApp.preferences();
      ae.setHandled(true);
    } else {
      throw new IllegalStateException("handlePreferences: MyApp instance detached from listener");
    }
  }

  public void handleQuit(ApplicationEvent ae) {
    if (mainApp != null) {
      
      ae.setHandled(false);
      mainApp.quit();
    } else {
      throw new IllegalStateException("handleQuit: MyApp instance detached from listener");
    }
  }


  
  
  
  public static void registerMacOSXApplication(JabRefFrame inApp) {
    if (theApplication == null) {
      theApplication = new com.apple.eawt.Application();
    }

    if (theAdapter == null) {
      theAdapter = new OSXAdapter(inApp);
    }
    theApplication.addApplicationListener(theAdapter);
  }

  
  
  public static void enablePrefs(boolean enabled) {
    if (theApplication == null) {
      theApplication = new com.apple.eawt.Application();
    }
    theApplication.setEnabledPreferencesMenu(enabled);
  }
}
