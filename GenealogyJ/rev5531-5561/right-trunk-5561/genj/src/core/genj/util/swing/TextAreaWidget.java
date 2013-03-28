
package genj.util.swing;

import genj.util.ChangeSupport;

import java.awt.KeyboardFocusManager;

import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;


public class TextAreaWidget extends JTextArea {

  
  private ChangeSupport changeSupport = new ChangeSupport(this);

  
  public TextAreaWidget(String text, int rows, int cols) {
    this(text, rows, cols, true, false);
  }
  
  
  public TextAreaWidget(String text, int rows, int cols, boolean editable, boolean wrap) {
    super(text, rows, cols);
    
    setAlignmentX(0);
    setEditable(editable);
    setLineWrap(wrap);
    setWrapStyleWord(true);
    setFont(new JTextField().getFont()); 

    
    
    setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
    setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
    
    getDocument().addDocumentListener(changeSupport);
  }
  
  
  public void addChangeListener(ChangeListener l) {
    changeSupport.addChangeListener(l);
  }
  
  
  public void removeChangeListener(ChangeListener l) {
    changeSupport.removeChangeListener(l);
  }
  
} 
