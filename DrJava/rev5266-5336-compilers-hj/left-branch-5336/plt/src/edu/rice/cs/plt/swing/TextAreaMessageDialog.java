

package edu.rice.cs.plt.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Container;
import java.awt.SystemColor;
import javax.swing.JDialog;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.WindowConstants;
import javax.swing.BorderFactory;
import javax.swing.border.Border;


public class TextAreaMessageDialog extends JDialog {
  
  
  public static void showDialog(Component comp, String title, String message) {
    Frame frame = JOptionPane.getFrameForComponent(comp);
    TextAreaMessageDialog dialog = new TextAreaMessageDialog(frame, comp, title, message);
    SwingUtil.setPopupLoc(dialog, frame);
    dialog.setVisible(true);
  }
  
  
  private TextAreaMessageDialog(Frame frame, Component comp, String title, String message) {
    super(frame, title, true);
    setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    
    
    JButton okButton = new JButton("OK");
    okButton.addActionListener(SwingUtil.disposeAction(this));
    getRootPane().setDefaultButton(okButton);
    
    JTextArea textArea = new JTextArea(message);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(false);
    textArea.setBackground(SystemColor.window);
    
    Border emptyBorder = BorderFactory.createEmptyBorder(10, 10, 10, 10);
    textArea.setBorder(emptyBorder);
    
    Container contentPane = getContentPane();
    contentPane.add(textArea, BorderLayout.CENTER);
    contentPane.add(okButton, BorderLayout.SOUTH);
    
    Dimension parentDim = (comp!=null)?(comp.getSize()):getToolkit().getScreenSize();
    int xs = (int)parentDim.getWidth()/4;
    int ys = (int)parentDim.getHeight()/5;
    setSize(Math.max(xs,350), Math.max(ys, 250));
    setLocationRelativeTo(comp);
  }
}
