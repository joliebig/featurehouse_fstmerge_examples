
package org.openscience.jmol.app.jmolpanel;

import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

class StatusBar extends JPanel {

  JLabel[] status;

  public StatusBar() {

    status = new JLabel[3];
    setLayout(new GridLayout(1, 3));
    setPreferredSize(new Dimension(640, 30));
    status[0] = new JLabel();
    status[0].setPreferredSize(new Dimension(100, 100));
    status[0].setBorder(BorderFactory.createBevelBorder(1));
    status[0].setHorizontalAlignment(0);
    status[1] = new JLabel();
    status[1].setPreferredSize(new Dimension(100, 100));
    status[1].setBorder(BorderFactory.createBevelBorder(1));
    status[1].setHorizontalAlignment(0);
    status[2] = new JLabel();
    status[2].setPreferredSize(new Dimension(100, 100));
    status[2].setBorder(BorderFactory.createBevelBorder(1));
    status[2].setFont(new Font("Monospaced", Font.PLAIN, 12));
    status[2].setHorizontalAlignment(0);
    add(status[0]);
    add(status[1]);
    add(status[2]);
  }

  public void setStatus(int label, String text) {
    status[label - 1].setText(text);
  }
}
