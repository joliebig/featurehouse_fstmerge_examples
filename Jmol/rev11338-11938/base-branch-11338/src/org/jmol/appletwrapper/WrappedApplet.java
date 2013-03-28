

package org.jmol.appletwrapper;

import java.awt.*;

import org.jmol.api.JmolAppletInterface;

public interface WrappedApplet extends JmolAppletInterface {
  public String getAppletInfo();
  public void setAppletWrapper(AppletWrapper appletWrapper);
  public void init();
  public void update(Graphics g);
  public void paint(Graphics g);
  public boolean handleEvent(Event e);
  public void destroy();
}
