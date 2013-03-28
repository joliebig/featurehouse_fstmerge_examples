
package genj.util.swing;

import genj.util.ChangeSupport;
import genj.util.EnvironmentChecker;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;
import javax.swing.text.StringContent;


public class ChoiceWidget extends JComboBox {
  
  private final static boolean IS_JAVA_15 = EnvironmentChecker.isJava15(null);
  
  private boolean blockAutoComplete = false;
  
  
  private Model model = new Model();
  
  
  private boolean isIgnoreCase = false;
  
  
  private ChangeSupport changeSupport = new ChangeSupport(this);
  
  
  private AutoCompleteSupport autoComplete;
  
  
  public ChoiceWidget() {
    this(new Object[0], null);
  }
  
  
  public ChoiceWidget(List values) {
    this(values.toArray(), null);
  }
  
       
  public ChoiceWidget(Object[] values, Object selection) {

    
    setEditable(true);

    
    setMaximumRowCount(8);

    
    setModel(model);

    
    model.setValues(values);
       
    
    setAlignmentX(LEFT_ALIGNMENT);
    
    
    setSelectedItem(selection);
    
    
  }
  
  
  public void addChangeListener(ChangeListener l) {
    changeSupport.addChangeListener(l);
  }
  
  
  public void removeChangeListener(ChangeListener l) {
    changeSupport.removeChangeListener(l);
  }
  
  
  public void setValues(List values) {
    setValues(values.toArray());
  }

  
  public void setValues(Object[] set) {
    try {
      blockAutoComplete = true;
      model.setValues(set);
    } finally {
      blockAutoComplete = false;
    }
  }

  
  public Dimension getPreferredSize() {
    Dimension result = super.getPreferredSize();
    result.width = Math.min(128, result.width);
    return result;
  }
      
  
  public Dimension getMaximumSize() {
    
    return new Dimension(Integer.MAX_VALUE, super.getPreferredSize().height);
  }
    
  
  public void setSelectAllOnFocus(boolean set) {
    
  }
    
  
  public String getText() {
    if (isEditable()) 
      return getEditor().getItem().toString();
    return super.getSelectedItem().toString();
  }
  
  
  public void setText(String text) {
    if (!isEditable) 
      throw new IllegalArgumentException("setText && !isEditable n/a");
    model.setSelectedItem(null);
    try {
      blockAutoComplete = true;
      
      try {
        getTextEditor().setText(text);
      } catch (Throwable t) {
        
        try {
          
          Document doc = new PlainDocument(new StringContent(255));
          doc.insertString(0, text, null);
          getTextEditor().setDocument(doc);
        } catch (Throwable retry) {
          Logger.getLogger("genj.util.swing").log(Level.FINE, "Couldn't retry "+getTextEditor().getClass()+".setText("+text+")", retry);
          
          Logger.getLogger("genj.util.swing").log(Level.WARNING, "Couldn't call "+getTextEditor().getClass()+".setText("+text+") - giving up", t);
          
          
          
          
          
          
          
          
          
          
          
          
          
          
          
        }
        

      }
      
    } finally {
      blockAutoComplete = false;
    }
    
  }
  
  
  public JTextField getTextEditor() {
    return (JTextField)getEditor().getEditorComponent();
  }
  
  
  public void setIgnoreCase(boolean set) {
    isIgnoreCase = set;
  }
  
  
  public void setPopupVisible(boolean v) {
    
    super.setPopupVisible(v);
    
    if (v) { 
      
      String pre = getText();
      for (int i=0; i<getItemCount(); i++) {
        String item = getItemAt(i).toString();
        if (item.regionMatches(true, 0, pre, 0, pre.length())) {
          setSelectedIndex(i);
          break;
        }
      }
    }
    
  }

  
  public void requestFocus() {
    if (isEditable())
      getEditor().getEditorComponent().requestFocus();
    else
      super.requestFocus();
  }
  
  
  public boolean requestFocusInWindow() {
    if (isEditable())
      return getEditor().getEditorComponent().requestFocusInWindow();
    return super.requestFocusInWindow();
  }
  
  
  public void setEditor(ComboBoxEditor set) {

    
    if (!(set.getEditorComponent() instanceof JTextField))
      throw new IllegalArgumentException("Only JTextEditor editor components are allowed");
    
    
    super.setEditor(set);
    
    
    if (autoComplete==null) 
      autoComplete = new AutoCompleteSupport();
    autoComplete.attach(getTextEditor());
    
    
  }
  
  
  public void addActionListener(ActionListener l) {
    getEditor().addActionListener(l);
  }
      
  
  public void removeActionListener(ActionListener l) {
    getEditor().removeActionListener(l);
  }

  
  private class AutoCompleteSupport extends KeyAdapter implements DocumentListener, ActionListener, FocusListener {

    private JTextField text;
    private Timer timer = new Timer(250, this);
    
    
    private AutoCompleteSupport() {
      
      timer.setRepeats(false);
      
    }
    
    private void attach(JTextField set) {
      
      
      if (text!=null) {
        text.getDocument().removeDocumentListener(this);
        text.removeFocusListener(this);
        text.removeKeyListener(this);
      }
      
      
      text = set;
      text.getDocument().addDocumentListener(this);
      text.addFocusListener(this);
      text.addKeyListener(this);
    }
    
    
    public void removeUpdate(DocumentEvent e) {
      changeSupport.fireChangeEvent();
      
      if (!blockAutoComplete&&isEditable())
        timer.start();
    }
      
    
    public void changedUpdate(DocumentEvent e) {
      changeSupport.fireChangeEvent();
      
      if (!blockAutoComplete&&isEditable())
        timer.start();
    }
      
    
    public void insertUpdate(DocumentEvent e) {
      changeSupport.fireChangeEvent();
      
      if (!blockAutoComplete&&isEditable())
        timer.start();
    }
      
    
    public void actionPerformed(ActionEvent e) {
      
      
      String prefix = text.getText();
      if (prefix.length()==0)
        return;

      
      int caretPos = text.getCaretPosition();
      String match = model.setSelectedPrefix(prefix);
      
      
      if (match.length()==0) {
        hidePopup();
        return;
      }
      
      
      blockAutoComplete = true;
      text.setText(prefix);
      blockAutoComplete = false;
      text.setCaretPosition(caretPos);
      
      
      if (match.length()>=prefix.length()) {
        
        
        
        
        
        
        
        
        
        
        if (isShowing())
          showPopup();
      } 
      
      
    }
    
    
    public void focusGained(FocusEvent e) {
      if (text.getDocument() != null) {
        text.setCaretPosition(text.getDocument().getLength());
        text.moveCaretPosition(0);
      }
    }
    
    public void focusLost(FocusEvent e) {
      
      
      
      if (IS_JAVA_15)
        setPopupVisible(false);
    }

    
    public void keyPressed(KeyEvent e) {
      
      if (e.getKeyCode()==KeyEvent.VK_ENTER&&isPopupVisible()) {
        model.setSelectedItem(model.getSelectedItem());
        setPopupVisible(false);
      }
      
    }
  } 
  
  
  private class Model extends AbstractListModel implements ComboBoxModel {
    
    
    private Object[] values = new Object[0];
    
    
    private Object selection = null;

    
    private void setValues(Object[] set) {
      selection = null;
      
      if (values.length>0) 
        fireIntervalRemoved(this, 0, values.length-1);
      values = set;
      if (values.length>0) 
        fireIntervalAdded(this, 0, values.length-1);
      
    }

    
    public Object getSelectedItem() {
      return selection;
    }
    
    
    private String setSelectedPrefix(String prefix) {
      
      if (isIgnoreCase)
        prefix = prefix.toLowerCase();
      
      
      for (int i=0;i<values.length;i++) {
        String value = values[i].toString();
        if ((isIgnoreCase ? value.toLowerCase() : value).startsWith(prefix)) {
          setSelectedItem(value);
          return value;        
        }
      }
      
      
      return "";
    }

    
    public void setSelectedItem(Object seLection) {
      
      selection = seLection;
      
      blockAutoComplete = true;
      getEditor().setItem(selection);
      blockAutoComplete = false;
      
      fireItemStateChanged(new ItemEvent(ChoiceWidget.this, ItemEvent.ITEM_STATE_CHANGED, selection, ItemEvent.SELECTED));
      
      
      
      
      
      fireContentsChanged(this, -1, -1);
      
    }

    
    public Object getElementAt(int index) {
      return values[index];
    }

    
    public int getSize() {
      return values.length;
    }

  } 

} 
