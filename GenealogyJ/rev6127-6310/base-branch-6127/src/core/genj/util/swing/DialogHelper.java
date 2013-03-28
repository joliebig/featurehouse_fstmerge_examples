
package genj.util.swing;

import genj.util.Registry;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.EventObject;
import java.util.StringTokenizer;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;


public class DialogHelper {

  
  private final static Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
  
  
  public static final int  
    ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE,
    INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE,
    WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE,
    QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE,
    PLAIN_MESSAGE = JOptionPane.PLAIN_MESSAGE;

  public static int openDialog(String title, int messageType,  String txt, Action[] actions, Component source) {
    
    
    int maxLine = 40;
    int cols = 40, rows = 1;
    StringTokenizer lines = new StringTokenizer(txt, "\n\r");
    while (lines.hasMoreTokens()) {
      String line = lines.nextToken();
      if (line.length()>maxLine) {
        cols = maxLine;
        rows += line.length()/maxLine;
      } else {
        cols = Math.max(cols, line.length());
        rows++;
      }
    }
    rows = Math.min(10, rows);
    
    
    TextAreaWidget text = new TextAreaWidget("", rows, cols);
    text.setLineWrap(true);
    text.setWrapStyleWord(true);
    text.setText(txt);
    text.setEditable(false);    
    text.setCaretPosition(0);
    text.setRequestFocusEnabled(false);

    
    JScrollPane content = new JScrollPane(text);
      
    
    return openDialog(title, messageType, content, actions, source);
  }
  
  
  public static int openDialog(String title, int messageType,  JComponent[] content, Action[] actions, Component source) {
    
    
    JPanel box = new JPanel();
    box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
    for (int i = 0; i < content.length; i++) {
      if (content[i]==null) continue;
      box.add(content[i]);
      content[i].setAlignmentX(0F);
    }
    
    return openDialog(title, messageType, box, actions, source);
  }

  
  public static String openDialog(String title, int messageType,  String txt, String value, Component source) {

    
    TextFieldWidget tf = new TextFieldWidget(value, 24);
    JLabel lb = new JLabel(txt);
    
    
    int rc = openDialog(title, messageType, new JComponent[]{ lb, tf}, Action2.okCancel(), source);
    
    
    return rc==0?tf.getText().trim():null;
  }

  public static int openDialog(String title, int messageType,  JComponent content, Action[] actions, Component source) {
    
    if (actions==null) 
      actions = Action2.okOnly();
    
    Object rc = openDialogImpl(title, messageType, content, actions, source);
    
    for (int a=0; a<actions.length; a++) 
      if (rc==actions[a]) return a;
    return -1;
  }
  
  
  private static Object openDialogImpl(String title, int messageType,  JComponent content, Action[] actions, Component source) {

    
    source = visitOwners(source, new ComponentVisitor() {
      public Component visit(Component parent, Component child) {
        return parent ==null ? child : null;
      }
    });

    
    JOptionPane optionPane = new Content(messageType, content, actions);
    
    
    final JDialog dlg = optionPane.createDialog(source, title);
    dlg.setResizable(true);
    dlg.setModal(true);
    dlg.pack();
    dlg.setMinimumSize(content.getMinimumSize());
    
    
    StackTraceElement caller = getCaller();
    final Registry registry = Registry.get(caller.getClassName());
    final String key = caller.getMethodName() + ".dialog";
    Dimension bounds = registry.get(key, (Dimension)null);
    if (bounds!=null) {
      bounds.width = Math.max(bounds.width, dlg.getWidth());
      bounds.height = Math.max(bounds.height, dlg.getHeight());
      dlg.setBounds(new Rectangle(bounds).intersection(screen));
    }
    dlg.setLocationRelativeTo(source);

    
    dlg.addComponentListener(new ComponentAdapter() {
      public void componentHidden(ComponentEvent e) {
        registry.put(key, dlg.getSize());
        dlg.dispose();
      }
    });
    
    
    dlg.setVisible(true);
    
    
    return optionPane.getValue();
  }
  
  private static StackTraceElement getCaller() {
    String clazz = DialogHelper.class.getName();
    for (StackTraceElement element : new Throwable().getStackTrace())
      if (!clazz.equals(element.getClassName()))
        return element;
    
    return new StackTraceElement("Class", "method", "file", 0);
  }

    
  private Window getWindowForComponent(Component c) {
    if (c instanceof Frame || c instanceof Dialog || c==null)
      return (Window)c;
    return getWindowForComponent(c.getParent());
  }
  
  
  private static class Content extends JOptionPane {
    
    
    private Content(int messageType, JComponent content, Action[] actions) {
      super(new JLabel(),messageType, JOptionPane.DEFAULT_OPTION, null, new String[0] );
      
      
      
      JPanel wrapper = new JPanel(new BorderLayout());
      wrapper.add(BorderLayout.CENTER, content);
      setMessage(wrapper);

      
      Option[] options = new Option[actions.length];
      for (int i=0;i<actions.length;i++)
        options[i] = new Option(actions[i]);
      setOptions(options);
      
      
      if (options.length>0) 
        setInitialValue(options[0]);
      
      
    }
   
    
    private class Option extends JButton implements ActionListener {
      
      
      private Option(Action action) {
        super(action);
        addActionListener(this);
      }
      
      
      public void actionPerformed(ActionEvent e) {
        
        setValue(getAction());
      }
      
    } 
    
  } 
  
  public static Component getComponent(EventObject event) {
    Object source = event.getSource();
    if (!(source instanceof Component))
      throw new IllegalArgumentException("Can't find component for event "+event);
    return (Component)source;
  }
  
  
  public static Component visitContainers(Component component, ComponentVisitor visitor) {
    do {
      Component parent = component.getParent();
      
      Component result = visitor.visit(parent, component);
      if (result!=null)
        return result;
      
      component = parent;
      
    } while (component!=null);
    
    return null;
  }
  
  
  public static Component visitOwners(Component component, ComponentVisitor visitor) {
    
    do {
      Component parent;
      if (component instanceof JPopupMenu) 
        parent = ((JPopupMenu)component).getInvoker();
      else if (component instanceof JMenu)
        parent = ((JMenu)component).getParent();
      else if (component instanceof JMenuItem)
        parent = ((JMenuItem)component).getParent();
      else if (component !=null)
        parent = component.getParent();
      else
        return null;

      Component result = visitor.visit(parent, component);
      if (result!=null)
        return result;
      
      component = parent;
      
    } while (component!=null);
    
    return null;
  }
    
  public static Component visitOwners(EventObject event, ComponentVisitor visitor) {
    return visitOwners((Component)event.getSource(), visitor);
  }
  
  
  public interface ComponentVisitor {
    
    
    public Component visit(Component component, Component child);
  }

  
  public static void setOpaque(Component component, boolean set) {
    if (component instanceof JComponent)
      ((JComponent)component).setOpaque(set);
    if (component instanceof Container)
      for (Component c : ((Container)component).getComponents()) 
        setOpaque(c, set);
  }

  
  public static boolean isContained(Component component, final Container container) {
    return container==visitContainers(component, new ComponentVisitor() {
      @Override
      public Component visit(Component parent, Component child) {
        if (parent==container)
          return parent;
        return null;
      }
    });
  }
  
} 
