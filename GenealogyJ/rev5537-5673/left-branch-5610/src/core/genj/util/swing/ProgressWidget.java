
package genj.util.swing;

import genj.util.GridBagHelper;
import genj.util.Trackable;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.UIManager;


public class ProgressWidget extends JPanel {

  private final static String
    OPTION_CANCEL = UIManager.getString("OptionPane.cancelButtonText");

  
  private JProgressBar  progress;

  
  private Trackable     trackable;
  
  
  private Thread        worker;
  
  
  private JLabel        state;
  
  
  private Timer timer;
  
  
  public ProgressWidget(Trackable trAckable, Thread woRker) {

    
    trackable  = trAckable;
    worker     = woRker;

    
    GridBagHelper gh = new GridBagHelper(this)
      .setInsets(new Insets(2,2,2,2))
      .setParameter(GridBagHelper.GROW_HORIZONTAL | GridBagHelper.FILL_HORIZONTAL);

    
    state = new JLabel(" ",JLabel.CENTER);
    gh.add(state, 0, 0);

    
    progress = new JProgressBar();
    gh.add(progress, 0, 1);

    
    timer = new Timer(100, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        
        progress.setValue(trackable.getProgress());
        
        state.setText(trackable.getState());
        
        if (!worker.isAlive())
          timer.stop();
        
      }
    });
       
    
  }
  
  
  public void addNotify() {
    
    timer.start();

    
    super.addNotify();
  }
  
  
  public void removeNotify() {
    
    
    timer.stop();
    
    
    trackable.cancelTrackable();
    
    
    super.removeNotify();
  }

} 
