
package genj.util.swing;

import genj.util.Trackable;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;


public class ProgressWidget extends JPanel {
  
  private final static ImageIcon IMG_CANCEL = new ImageIcon(ProgressWidget.class, "Cancel.png");

  
  private JProgressBar  progress = new JProgressBar(0, 100);

  
  private Trackable     track;
  
  
  private Timer timer;
  
  private Dimension minPreferredSize;
  
  
  public ProgressWidget(Trackable trackable) {

    super(new BorderLayout());
    
    JButton cancel = new JButton(new Cancel());
    cancel.setRequestFocusEnabled(false);
    cancel.setFocusable(false);
    cancel.setMargin(new Insets(0,0,0,0));
    
    add(progress, BorderLayout.CENTER);
    add(cancel, BorderLayout.EAST);

    progress.setStringPainted(true);
    track = trackable;

    
    timer = new Timer(100, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        progress.setValue(track.getProgress());
        progress.setString(track.getState());
        revalidate();
        repaint();
      }
    });
       
    
  }
  
  @Override
  public Dimension getPreferredSize() {
    Dimension oldMin = minPreferredSize;
    minPreferredSize = super.getPreferredSize();
    if (oldMin!=null) {
      minPreferredSize.width = Math.max(minPreferredSize.width+16, oldMin.width);
      minPreferredSize.height= Math.max(minPreferredSize.height, oldMin.height);
    }
    return minPreferredSize;
  }
  
  
  public void addNotify() {
    
    timer.start();
    
    super.addNotify();
  }
  
  
  public void removeNotify() {
    
    timer.stop();
    
    super.removeNotify();
  }
  
  private class Cancel extends Action2 {
    private Cancel() {
      setImage(IMG_CANCEL);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
      track.cancelTrackable();
    }
  }

} 
