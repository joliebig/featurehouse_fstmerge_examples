package org.jmol;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolAdapter;
import org.jmol.api.JmolViewer;

public class Test extends JPanel {

  
  public static void main(String[] args) {
    (new Test()).viewer.loadInline(strXyzHOH);
  }

  public Test() {
    adapter = new SmarterJmolAdapter();
    viewer = JmolViewer.allocateViewer(this, adapter);
    JFrame newFrame = new JFrame();
    newFrame.getContentPane().add(this);
    newFrame.setSize(300, 300);
    newFrame.setVisible(true);
  }

  private final static String strXyzHOH = 
      "3\n" +
      "water\n" +
  		"O  0.0 0.0 0.0\n" +
  		"H  0.76923955 -0.59357141 0.0\n" +
  		"H -0.76923955 -0.59357141 0.0\n";

  private JmolViewer viewer;
  private JmolAdapter adapter;
  private Dimension currentSize = new Dimension();
  private Rectangle rectClip = new Rectangle();

  public void paint(Graphics g) {
    getSize(currentSize);
    g.getClipBounds(rectClip);
    viewer.renderScreenImage(g, currentSize, rectClip);
  }

}
