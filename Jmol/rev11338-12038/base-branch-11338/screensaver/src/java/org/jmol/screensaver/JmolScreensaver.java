

















package org.jmol.screensaver;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import org.jdesktop.jdic.screensaver.ScreensaverContext;
import org.jdesktop.jdic.screensaver.SimpleScreensaver;
import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolViewer;


public class JmolScreensaver extends SimpleScreensaver
{
  
  private JmolAdapter adapter = null;
  private JmolViewer viewer = null;
  private final Dimension currentSize = new Dimension();

  
  public void init() {
    ScreensaverContext context = getContext();
    Component c = context.getComponent();
    adapter = new SmarterJmolAdapter();
    viewer = JmolViewer.allocateViewer(c, adapter);
    viewer.evalStringQuiet(
        "load C:\\Program Files\\Folding@Home\\v1\\work\\current.xyz");
  }

  
  public void paint( Graphics g ) {
    Component c = getContext().getComponent();
    viewer.setScreenDimension(c.getSize(currentSize));
    Rectangle rectClip = new Rectangle();
    g.getClipBounds(rectClip);
    viewer.renderScreenImage(g, currentSize, rectClip);
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      
    }
  }
}
