
package genj.util.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.swing.JProgressBar;
import javax.swing.Timer;


public class HeapStatusWidget extends JProgressBar {
  
  private final static NumberFormat FORMAT = new DecimalFormat("0.0");
  
  private MessageFormat tooltip = new MessageFormat("Heap: {0}MB used {1}MB free {2}MB max");

  
  public HeapStatusWidget() {
    super(0,100);
    setValue(0);
    setBorderPainted(false);
    setStringPainted(true);
    new Timer(3000, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        update();
      }
    }).start();
  }
  
  
  private void update() {
    
    
    Runtime r = Runtime.getRuntime();
    long max = r.maxMemory();
    long free = r.freeMemory();
    long total = r.totalMemory();
    long used = total-free;
    int percent = (int)Math.round(used*100D/max);
    
    
    setValue(percent);
    setString(format(used, true)+"MB ("+percent+"%)");

    
    super.setToolTipText(null);
    super.setToolTipText(tooltip.format(new String[]{ format(used, false), format(free, false), format(max, false)}));
    
    
  }
  
  private String format(long mb, boolean decimals) {
    double val = mb/1000000D;
    return decimals ? FORMAT.format(mb/1000000D) : Integer.toString((int)Math.round(val));
  }

  
  public void setToolTipText(String text) {
    
    this.tooltip = new MessageFormat(text);
    
    super.setToolTipText("");
  }
  
}
