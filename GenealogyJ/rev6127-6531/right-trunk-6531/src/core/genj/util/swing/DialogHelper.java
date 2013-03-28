
package genj.util.swing;

import genj.util.Registry;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.EventObject;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class DialogHelper {

  
  private final static Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
  
  
  public static final int  
    ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE,
    INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE,
    WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE,
    QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE,
    PLAIN_MESSAGE = JOptionPane.PLAIN_MESSAGE;
  
  public static void showError(String title, String msg, Throwable t, Object source) {
    openDialog(title, DialogHelper.ERROR_MESSAGE, msg, Action2.okOnly(), source);
  }

  public static void showInfo(String title, String msg, Object source) {
    openDialog(title, DialogHelper.INFORMATION_MESSAGE, msg, Action2.okOnly(), source);
  }
  
  public static int openDialog(String title, int messageType,  String txt, Action[] actions, Object source) {
    
    
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
  
  
  public static int openDialog(String title, int messageType,  JComponent[] content, Action[] actions, Object source) {
    
    
    JPanel box = new JPanel();
    box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
    for (int i = 0; i < content.length; i++) {
      if (content[i]==null) continue;
      box.add(content[i]);
      content[i].setAlignmentX(0F);
    }
    
    return openDialog(title, messageType, box, actions, source);
  }

  
  public static String openDialog(String title, int messageType, String txt, List<String> values, Object source) {

    
    JLabel lb = new JLabel(txt);
    final JList list = new JList(values.toArray(new String[values.size()]));
    list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    final Action[] actions = Action2.okCancel();
    list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        actions[0].setEnabled(list.getSelectedIndex()>=0);
      }
    });
    
    
    int rc = openDialog(title, messageType, new JComponent[]{ lb, new JScrollPane(list)}, actions, source);
    
    
    return rc==0?(String)list.getSelectedValue() : null;
    
  }
  
  
  public static String openDialog(String title, int messageType,  String txt, String value, Object source) {

    
    JLabel lb = new JLabel(txt);
    final TextFieldWidget tf = new TextFieldWidget(value, 24);
    final Action[] actions = Action2.okCancel();
    tf.getDocument().addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
      }
      public void insertUpdate(DocumentEvent e) {
        actions[0].setEnabled(tf.getText().length()>0);
      }
      public void removeUpdate(DocumentEvent e) {
        insertUpdate(e);
      }
    });
    
    
    int rc = openDialog(title, messageType, new JComponent[]{ lb, tf}, actions, source);
    
    
    return rc==0?tf.getText().trim():null;
  }

  public static int openDialog(String title, int messageType,  JComponent content, Action[] actions, Object source) {
    
    if (actions==null) 
      actions = Action2.okOnly();
    
    Object rc = openDialogImpl(title, messageType, content, actions, source);
    
    for (int a=0; a<actions.length; a++) 
      if (rc==actions[a]) return a;
    return -1;
  }
  
  
  private static Object openDialogImpl(String title, int messageType, final JComponent content, Action[] actions, Object source) {

    
    Component parent = null;
    if (source instanceof Component)
      parent = (Component)source;
    else if (source instanceof EventObject && ((EventObject)source).getSource() instanceof Component)
      parent = visitOwners( (Component)((EventObject)source).getSource(), new ComponentVisitor() {
        public Component visit(Component parent, Component child) {
          return parent ==null ? child : null;
        }
      });
    
    
    patchOpaque(content, true);

    
    final JOptionPane optionPane = new Content(messageType, content, actions);
    
    
    final JDialog dlg = optionPane.createDialog(parent, title);
    dlg.setResizable(true);
    dlg.setModal(true);
    dlg.pack();
    dlg.setMinimumSize(content.getMinimumSize());

    
    StackTraceElement caller = getCaller();
    final Registry registry = Registry.get(caller.getClassName());
    final String key = caller.getMethodName() + (caller.getLineNumber()>0?caller.getLineNumber():"") + ".dialog";
    Dimension bounds = registry.get(key, (Dimension)null);
    if (bounds!=null) {
      bounds.width = Math.max(bounds.width, dlg.getWidth());
      bounds.height = Math.max(bounds.height, dlg.getHeight());
      dlg.setBounds(new Rectangle(bounds).intersection(screen));
    }
    dlg.setLocationRelativeTo(parent);

    
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
    
    private JDialog dlg;
    private JComponent content;
    
    
    private Content(int messageType, JComponent content, Action[] actions) {
      super(new JLabel(),messageType, JOptionPane.DEFAULT_OPTION, null, new String[0] );
      
      this.content = content;
      
      
      
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
    
    @Override
    public JDialog createDialog(Component parentComponent, String title) throws HeadlessException {
      dlg = super.createDialog(parentComponent, title);
      return dlg;
    }
    
    public void doLayout() {
      
      super.doLayout();

      
      if (dlg!=null) {
        Dimension c = content.getSize();
        Dimension m = content.getMinimumSize();
        
        Dimension size = dlg.getSize();
        boolean set = false;
        if ( (set|=m.width>c.width))
          size.width += m.width-c.width;
        if ( (set|=m.height>c.height) ) 
          size.height += m.height-c.height;

        if (set)
          dlg.setSize(size);
      }
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

  
  private static void patchOpaque(Component component, boolean set) {

    if (component instanceof JTabbedPane)
      set = false;
    
    if (component instanceof JComponent && !(component instanceof JTextField) && !(component instanceof JScrollPane)) {
      if (!set)
        ((JComponent)component).setOpaque(set);
    }
    
    if (component instanceof Container && !(component instanceof JScrollPane)) {
      for (Component c : ((Container)component).getComponents()) {
        patchOpaque(c, set);
      }
    }
    
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
