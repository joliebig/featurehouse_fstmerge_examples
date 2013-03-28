
package genj.window;

import genj.gedcom.Gedcom;
import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public class WindowManager {
  
  
  private Rectangle screen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
  
  
  private JFrame defaultFrame = new JFrame();
  
  
  private final static WindowManager INSTANCE = new WindowManager();

  
  public static final int  
    ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE,
    INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE,
    WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE,
    QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE,
    PLAIN_MESSAGE = JOptionPane.PLAIN_MESSAGE;

  
  protected Registry registry;

  
  private int temporaryKeyCounter = 0;  

  
  private Map key2window = new HashMap();
    
  
   final static Logger LOG = Logger.getLogger("genj.window");
  
  
  private WindowManager() {
    registry = new Registry("genj.window");
    defaultFrame.setIconImage(Gedcom.getImage().getImage());
  }
  
  
  public void close(String key) {

    Object framedlg = recall(key);
    
    if (framedlg instanceof JFrame) {
      JFrame frame = (JFrame)framedlg;
      frame.dispose(); 
      return;
    }

    if (framedlg instanceof JDialog) {
      JDialog dlg = (JDialog)framedlg;
      dlg.setVisible(false); 
      return;
    }

    
  }
  
  
  public JComponent getContent(String key) {
    
    Object framedlg = recall(key);
    
    if (framedlg instanceof JFrame)
      return (JComponent)((JFrame)framedlg).getContentPane().getComponent(0); 

    if (framedlg instanceof JDialog)
      return (JComponent)((JDialog)framedlg).getContentPane().getComponent(0);

    return null;
  }

  
  
  public boolean show(String key) {

    Object framedlg = recall(key);
    
    if (framedlg instanceof JFrame) {
      ((JFrame)framedlg).toFront(); 
      return true;
    }

    if (framedlg instanceof JDialog) {
      ((JDialog)framedlg).toFront();
      return true;
    }

    return false;
  }
  
  
  
  public static WindowManager getInstance(Component component) {
    return getInstance();
  }
  public static WindowManager getInstance() {
    return INSTANCE;
  }
  
  
  public final String openWindow(String key, String title, ImageIcon image, JComponent content, JMenuBar menu, Runnable onClose) {
    
    if (key==null) 
      key = getTemporaryKey();
    
    close(key);
    
    Rectangle bounds = registry.get(key, (Rectangle)null);
    boolean maximized = registry.get(key+".maximized", false);
    
    Component window = openWindowImpl(key, title, image, content, menu, bounds, maximized, onClose);
    
    key2window.put(key, window);
    
    return key;
  }
  
  
  private Component openWindowImpl(final String key, String title, ImageIcon image, JComponent content, JMenuBar menu, Rectangle bounds, boolean maximized, final Runnable onClosing) {
    
    
    final JFrame frame = new JFrame() {
      
      public void dispose() {
        
        closeNotify(key, getBounds(), getExtendedState()==MAXIMIZED_BOTH);
        
        super.dispose();
      }
    };

    
    if (title!=null) frame.setTitle(title);
    if (image!=null) frame.setIconImage(image.getImage());
    if (menu !=null) frame.setJMenuBar(menu);

    
    frame.getContentPane().add(content);

    
    if (onClosing==null) {
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    } else {
      
      frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      frame.addWindowListener(new WindowAdapter() {
        public void windowClosing(WindowEvent e) {
          onClosing.run();
        }
      });
    }

    
    if (bounds==null) {
      frame.pack();
      Dimension dim = frame.getSize();
      bounds = new Rectangle(screen.width/2-dim.width/2, screen.height/2-dim.height/2,dim.width,dim.height);
      LOG.log(Level.FINE, "Sizing window "+key+" to "+bounds+" after pack()");
    }
    frame.setBounds(bounds.intersection(screen));
    
    if (maximized)
      frame.setExtendedState(Frame.MAXIMIZED_BOTH);

    
    frame.setVisible(true);
    
    
    return frame;
  }
  
  
  
  public final int openDialog(String key, String title,  int messageType, String txt, Action[] actions, Component owner) {
    
    
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
      
    
    return openDialog(key, title, messageType, content, actions, owner);
  }
  
  
  public final int openDialog(String key, String title,  int messageType, JComponent[] content, Action[] actions, Component owner) {
    
    
    JPanel box = new JPanel();
    box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
    for (int i = 0; i < content.length; i++) {
      if (content[i]==null) continue;
      box.add(content[i]);
      content[i].setAlignmentX(0F);
    }
    
    return openDialog(key, title, messageType, box, actions, owner);
  }

  
  public final String openDialog(String key, String title,  int messageType, String txt, String value, Component owner) {

    
    TextFieldWidget tf = new TextFieldWidget(value, 24);
    JLabel lb = new JLabel(txt);
    
    
    int rc = openDialog(key, title, messageType, new JComponent[]{ lb, tf}, Action2.okCancel(), owner);
    
    
    return rc==0?tf.getText().trim():null;
  }

  
  public final int openDialog(String key, String title,  int messageType, JComponent content, Action[] actions, Component owner) {
    
    if (actions==null) 
      actions = Action2.okOnly();
    
    if (key==null) 
      key = getTemporaryKey();
    
    close(key);
    
    Rectangle bounds = registry.get(key, (Rectangle)null);
    
    Object rc = openDialogImpl(key, title, messageType, content, actions, owner, bounds);
    
    for (int a=0; a<actions.length; a++) 
      if (rc==actions[a]) return a;
    return -1;
  }
  
  
  private Object openDialogImpl(final String key, String title,  int messageType, JComponent content, Action[] actions, Component owner, Rectangle bounds) {

    
    JOptionPane optionPane = new Content(messageType, content, actions);
    
    
    final JDialog dlg = optionPane.createDialog(owner != null ? owner : defaultFrame, title);
    dlg.setResizable(true);
    dlg.setModal(true);
    if (bounds==null) {
      dlg.pack();
      if (owner!=null)
        dlg.setLocationRelativeTo(owner.getParent());
    } else {
      if (owner==null) {
        dlg.setBounds(bounds.intersection(screen));
      } else {
        dlg.setBounds(new Rectangle(bounds.getSize()).intersection(screen));
        dlg.setLocationRelativeTo(owner.getParent());
      }
    }

    
    dlg.addComponentListener(new ComponentAdapter() {
      public void componentHidden(ComponentEvent e) {
        closeNotify(key, dlg.getBounds(), false);
        dlg.dispose();
      }
    });
    
    
    dlg.setVisible(true);
    
    
    return optionPane.getValue();
  }

  
  public final String openNonModalDialog(String key, String title,  int messageType, JComponent content, Action[] actions, Component owner) {
    
    if (actions==null) actions = new Action[0];
    
    if (key==null) 
      key = getTemporaryKey();
    
    close(key);
    
    Rectangle bounds = registry.get(key, (Rectangle)null);
    
    Component window = openNonModalDialogImpl(key, title, messageType, content, actions, owner, bounds);
    
    key2window.put(key, window);
    
    return key;
  }

  
  private Component openNonModalDialogImpl(final String key, String title,  int messageType, JComponent content, Action[] actions, Component owner, Rectangle bounds) {

    
    JOptionPane optionPane = new Content(messageType, content, actions);
    
    
    final JDialog dlg = optionPane.createDialog(owner != null ? owner : defaultFrame, title);
    dlg.setResizable(true);
    dlg.setModal(false);
    if (bounds==null) {
      dlg.pack();
      if (owner!=null)
        dlg.setLocationRelativeTo(owner.getParent());
    } else {
      if (owner==null) {
        dlg.setBounds(bounds.intersection(screen));
      } else {
        dlg.setBounds(new Rectangle(bounds.getSize()).intersection(screen));
        dlg.setLocationRelativeTo(owner.getParent());
      }
    }

    
    dlg.addComponentListener(new ComponentAdapter() {
      public void componentHidden(ComponentEvent e) {
        closeNotify(key, dlg.getBounds(), false);
        dlg.dispose();
      }
    });
    
    
    dlg.setVisible(true);
    
    
    return dlg;
  }
  

  
  protected String getTemporaryKey() {
    return "_"+temporaryKeyCounter++;
  }

  
  protected String[] recallKeys() {
    return (String[])key2window.keySet().toArray(new String[0]);
  }

  
  protected Component recall(String key) {
    
    if (key==null) 
      return null;
    
    return (Component)key2window.get(key);
  }

  
  protected void closeNotify(String key, Rectangle bounds, boolean maximized) {
    
    if (key==null) 
      return;
    
    key2window.remove(key);
    
    if (key.startsWith("_")) 
      return;
    
    if (bounds!=null&&!maximized)
      registry.put(key, bounds);
    registry.put(key+".maximized", maximized);
    
  }
  
  
  public void closeAll() {

    
    String[] keys = recallKeys();
    for (int k=0; k<keys.length; k++) {
      close(keys[k]);
    }
    
    
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
