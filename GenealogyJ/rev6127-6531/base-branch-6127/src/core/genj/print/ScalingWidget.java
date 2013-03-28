
package genj.print;

import genj.util.ChangeSupport;
import genj.util.Resources;
import genj.util.swing.ChoiceWidget;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class ScalingWidget extends JPanel {

  private final static Pattern
    PERCENT = Pattern.compile("([0-9]{1,3})%"), 
    DIM     = Pattern.compile("([0-9]{1,2})x([0-9]{1,2})"); 
  
  private ChoiceWidget choice;
  private ChangeSupport changeSupport = new ChangeSupport(this);

  
  public ScalingWidget() {
    super(new BorderLayout());
    
    choice = new ChoiceWidget(new String[]{ "1x1", "50%", "75%", "100%"}, "100%" );
    choice.addChangeListener(new Validate());
    
    add(new JLabel(Resources.get(this).getString("scaling")), BorderLayout.WEST);
    add(choice, BorderLayout.CENTER);
  }
  
  
  public Object getValue() {
    
    String t = choice.getText().trim();
    
    Matcher p = PERCENT.matcher(t);
    if (p.find())
      return Integer.parseInt(p.group(1))*0.01D;
    
    Matcher d = DIM.matcher(t);
    if (d.find())
      return new Dimension(Integer.parseInt(d.group(1)), Integer.parseInt(d.group(2)));

    return null;
  }
  
  private class Validate implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
      Object value = getValue();
      Color c = value!=null ? null : Color.RED;
      choice.getEditor().getEditorComponent().setForeground(c);
      if (value!=null) changeSupport.fireChangeEvent();
    }
  }
  
  void addChangeListener(ChangeListener listener) {
    changeSupport.addChangeListener(listener);
  }
  
  void removeChangeListener(ChangeListener listener) {
    changeSupport.removeChangeListener(listener);
  }
  
}
