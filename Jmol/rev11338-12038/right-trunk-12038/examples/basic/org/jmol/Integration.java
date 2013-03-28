package org.jmol;


import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolSimpleViewer;
import org.jmol.util.Logger;



public class Integration {

  public static void main(String[] argv) {
    JFrame frame = new JFrame("Hello");
    frame.addWindowListener(new ApplicationCloser());
    Container contentPane = frame.getContentPane();
    JmolPanel jmolPanel = new JmolPanel();
    contentPane.add(jmolPanel);
    frame.setSize(300, 300);
    frame.setVisible(true);

    JmolSimpleViewer viewer = jmolPanel.getViewer();
    
    
    
    
    String strError = viewer.openFile("http://chemapps.stolaf.edu/jmol/docs/examples-11/data/caffeine.xyz");
    
    if (strError == null)
      viewer.evalString(strScript);
    else
      Logger.error(strError);
  }

  final static String strXyzHOH = 
    "3\n" +
    "water\n" +
    "O  0.0 0.0 0.0\n" +
    "H  0.76923955 -0.59357141 0.0\n" +
    "H -0.76923955 -0.59357141 0.0\n";

  final static String strScript = "delay; move 360 0 0 0 0 0 0 0 4;";

  static class ApplicationCloser extends WindowAdapter {
    public void windowClosing(WindowEvent e) {
      System.exit(0);
    }
  }

  static class JmolPanel extends JPanel {
    JmolSimpleViewer viewer;
    JmolAdapter adapter;
    JmolPanel() {
      adapter = new SmarterJmolAdapter();
      viewer = JmolSimpleViewer.allocateSimpleViewer(this, adapter);
    }

    public JmolSimpleViewer getViewer() {
      return viewer;
    }

    final Dimension currentSize = new Dimension();
    final Rectangle rectClip = new Rectangle();

    public void paint(Graphics g) {
      getSize(currentSize);
      g.getClipBounds(rectClip);
      viewer.renderScreenImage(g, currentSize, rectClip);
    }
  }
}
