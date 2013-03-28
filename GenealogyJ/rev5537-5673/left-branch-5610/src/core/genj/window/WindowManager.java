
package genj.window;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.TextAreaWidget;
import genj.util.swing.TextFieldWidget;

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
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;


public class WindowManager {

  private final static Registry REGISTRY = Registry.get(WindowManager.class);
  
  
  private Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
  
  
  private JFrame defaultFrame = new JFrame();
  
  
  private final static WindowManager INSTANCE = new WindowManager();

  
  public static final int  
    ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE,
    INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE,
    WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE,
    QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE,
    PLAIN_MESSAGE = JOptionPane.PLAIN_MESSAGE;

  
  private int temporaryKeyCounter = 0;  

  
   final static Logger LOG = Logger.getLogger("genj.window");
  
  
  private WindowManager() {
    defaultFrame.setIconImage(Gedcom.getImage().getImage());
  }
  
  
  public static WindowManager getInstance() {
    return INSTANCE;
  }
  
  
  public static Component getComponent(Object source) {
	  
	  if (source instanceof EventObject)
		  source = ((EventObject)source).getSource();
	  
		do {
		      if (source instanceof JPopupMenu) 
		    	  source = ((JPopupMenu)source).getInvoker();
		      else if (source instanceof JMenu)
		    	  source = ((JMenu)source).getParent();
          else if (source instanceof JMenuItem)
            source = ((JMenuItem)source).getParent();
		      else if (source instanceof Component)
		    	  return (Component)source;
		      else
		  	    throw new IllegalArgumentException("Cannot find parent for source "+source);
		    	  
	    } while (source!=null);
	    
	    throw new IllegalArgumentException("Cannot find parent for source "+source);
	}


  public final int openDialog(String key, String title,  int messageType, String txt, Action[] actions, Object source) {
    
    
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
      
    
    return openDialog(key, title, messageType, content, actions, source);
  }
  
  
  public final int openDialog(String key, String title,  int messageType, JComponent[] content, Action[] actions, Object source) {
    
    
    JPanel box = new JPanel();
    box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
    for (int i = 0; i < content.length; i++) {
      if (content[i]==null) continue;
      box.add(content[i]);
      content[i].setAlignmentX(0F);
    }
    
    return openDialog(key, title, messageType, box, actions, source);
  }

  
  public final String openDialog(String key, String title,  int messageType, String txt, String value, Object source) {

    
    TextFieldWidget tf = new TextFieldWidget(value, 24);
    JLabel lb = new JLabel(txt);
    
    
    int rc = openDialog(key, title, messageType, new JComponent[]{ lb, tf}, Action2.okCancel(), source);
    
    
    return rc==0?tf.getText().trim():null;
  }

  public final int openDialog(String key, String title,  int messageType, JComponent content, Action[] actions, Object source) {
    
    if (actions==null) 
      actions = Action2.okOnly();
    
    Rectangle bounds = key!=null ? REGISTRY.get(key, (Rectangle)null) : null;
    
    Object rc = openDialogImpl(key, title, messageType, content, actions, source, bounds);
    
    for (int a=0; a<actions.length; a++) 
      if (rc==actions[a]) return a;
    return -1;
  }
  
  
  private Object openDialogImpl(final String key, String title,  int messageType, JComponent content, Action[] actions, Object source, Rectangle bounds) {

    
    JOptionPane optionPane = new Content(messageType, content, actions);
    
    
    Component parent = source != null ? getComponent(source) : null;
    
    
    final JDialog dlg = optionPane.createDialog(parent != null ? parent : defaultFrame, title);
    dlg.setResizable(true);
    dlg.setModal(true);
    if (bounds==null) {
      dlg.pack();
      if (parent!=null)
        dlg.setLocationRelativeTo(parent.getParent());
    } else {
      if (parent==null) {
        dlg.setBounds(bounds.intersection(screen));
      } else {
        dlg.setBounds(new Rectangle(bounds.getSize()).intersection(screen));
        dlg.setLocationRelativeTo(parent.getParent());
      }
    }

    
    dlg.addComponentListener(new ComponentAdapter() {
      public void componentHidden(ComponentEvent e) {
        
        if (key!=null) {
          
          if (dlg.getBounds()!=null)
            REGISTRY.put(key, dlg.getBounds());
        }
        dlg.dispose();
      }
    });
    
    
    dlg.setVisible(true);
    
    
    return optionPane.getValue();
  }

    
  private Window getWindowForComponent(Component c) {
    if (c instanceof Frame || c instanceof Dialog || c==null)
      return (Window)c;
    return getWindowForComponent(c.getParent());
  }
  
  
  protected class Content extends JOptionPane {
    
    
    protected Content(int messageType, JComponent content, Action[] actions) {
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

    
    public void doLayout() {
      
      super.doLayout();
      
      Container container = getTopLevelAncestor();
      Dimension minimumSize = container.getMinimumSize();
      Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
      minimumSize.width = Math.min(screen.width/2, minimumSize.width);
      minimumSize.height = Math.min(screen.height/2, minimumSize.height);
      Dimension size        = container.getSize();
      if (size.width < minimumSize.width || size.height < minimumSize.height) {
        Dimension newSize = new Dimension(Math.max(minimumSize.width,  size.width),
                                          Math.max(minimumSize.height, size.height));
        container.setSize(newSize);
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
  
} 
