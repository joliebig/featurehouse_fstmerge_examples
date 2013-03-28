
package genj.util.swing;

import genj.util.ChangeSupport;

import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;
import javax.swing.event.ChangeListener;


public class TextFieldWidget extends JTextField {

  
  private boolean isTemplate = false;
  
  
  private boolean isSelectAllOnFocus = false;
  
  
  private ChangeSupport changeSupport = new ChangeSupport(this) {
    public void fireChangeEvent() {
      
      isTemplate = false;
      
      super.fireChangeEvent();
    }
  };
  
  
  public TextFieldWidget() {
    this("", 0);
  }
  
  
  public TextFieldWidget(String text) {
    this(text, 0);
  }

  
  public TextFieldWidget(String text, int cols) {
    super(text, cols);
    setAlignmentX(0);

    getDocument().addDocumentListener(changeSupport);
  }
  
  
  public void addChangeListener(ChangeListener l) {
    changeSupport.addChangeListener(l);
  }
  
  
  public void removeChangeListener(ChangeListener l) {
    changeSupport.removeChangeListener(l);
  }
  
  
  public TextFieldWidget setTemplate(boolean set) {
    isTemplate = set;
    return this;
  }
  
  
  public Dimension getMaximumSize() {
    return new Dimension(super.getMaximumSize().width, super.getPreferredSize().height);
  }
  
  
  public void setSelectAllOnFocus(boolean set) {
    isSelectAllOnFocus = set;
  }
  
  
  protected void processFocusEvent(FocusEvent e) {
    
    
    
    if (e.getID()==FocusEvent.FOCUS_GAINED) {
      if (isTemplate||isSelectAllOnFocus) 
        selectAll();
    }
    
    
    super.processFocusEvent(e);
  }
  
  private boolean blockSystemSelectionAccessViaToolkit = false;
  
  public Toolkit getToolkit() {
    if (blockSystemSelectionAccessViaToolkit)
      throw new HeadlessException("no access to system selection atm");
    return super.getToolkit();
  }
  
  
  public void selectAll() {
    
    
    
    if (getDocument() != null) {
      
      
      setCaretPosition(getDocument().getLength());
      
      
      try {
        
        
        
        if (getToolkit().getSystemSelection()!=null)
          blockSystemSelectionAccessViaToolkit = true;
        moveCaretPosition(0);
      } finally {
        blockSystemSelectionAccessViaToolkit = false;
      }
    }
  }
    
  
  public String getText() {
    if (isTemplate) 
      return "";
    return super.getText();
  }
  
  
  public boolean isEmpty() {
    return getText().trim().length()==0;
  }
  
  
  public void setText(String txt) {
    super.setText(txt);
    
    
    
    setCaretPosition(0);
  }
  
} 
