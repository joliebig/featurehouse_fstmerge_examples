
package genj.window;

import genj.util.Registry;
import genj.util.swing.Action2;
import genj.util.swing.TextAreaWidget;
import genj.util.swing.TextFieldWidget;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;


public abstract class WindowManager {

  private final static Object WINDOW_MANAGER_KEY = WindowManager.class;
  
  private static WeakHashMap window2manager = new WeakHashMap();
  
  
  public static final int  
    ERROR_MESSAGE = JOptionPane.ERROR_MESSAGE,
    INFORMATION_MESSAGE = JOptionPane.INFORMATION_MESSAGE,
    WARNING_MESSAGE = JOptionPane.WARNING_MESSAGE,
    QUESTION_MESSAGE = JOptionPane.QUESTION_MESSAGE,
    PLAIN_MESSAGE = JOptionPane.PLAIN_MESSAGE;
  
  
  protected Registry registry;

  
  private int temporaryKeyCounter = 0;  

  
  private Map key2window = new HashMap();
  
  
  private List listeners = new ArrayList();
  
  
  private boolean muteBroadcasts = false;
  
  
   final static Logger LOG = Logger.getLogger("genj.window");
  
  
  protected WindowManager(Registry regiStry) {
    registry = regiStry;
  }
  
  
  public void addBroadcastListener(WindowBroadcastListener listener) {
    listeners.add(listener);
  }
  
  
  public void removeBroadcastListener(WindowBroadcastListener listener) {
    listeners.remove(listener);
  }

  
  public static void broadcast(WindowBroadcastEvent event) {
    
    
    WindowManager instance = WindowManager.getInstanceImpl(event.getSource());
    if (instance==null) 
      return;

    
    instance.broadcastImpl(event);
  }
  
  private void broadcastImpl(WindowBroadcastEvent event) {
    
    
    if (muteBroadcasts)
      return;
    
    
    event.setBroadcasted();

    try {
      muteBroadcasts = true;
     
      
      Set visited = new HashSet();
      Component cursor = event.getSource();
      while (cursor!=null) {
        
        
        if (cursor instanceof WindowBroadcastListener) {
          visited.add(cursor);
          try {
            if (!((WindowBroadcastListener)cursor).handleBroadcastEvent(event))
              return;
          } catch (Throwable t) {
            LOG.log(Level.WARNING, "broadcast listener threw throwable - cancelling broadcast", t);
            return;
          }
        }
        
        
        cursor = cursor.getParent();
      }
      
      
      event.setInbound();
      
      
      for (Iterator ls = listeners.iterator(); ls.hasNext();) {
        WindowBroadcastListener l = (WindowBroadcastListener) ls.next();
        try {
          l.handleBroadcastEvent(event);
        } catch (Throwable t) {
          LOG.log(Level.WARNING, "broadcast listener threw throwable - continuing broadcast", t);
        }
      }
      
      
      String[] keys = recallKeys();
      for (int i = 0; i < keys.length; i++) {
        broadcastImpl(event, recall(keys[i]), visited);
      }
      
    } finally {
      muteBroadcasts = false;
    }
    
  }
  
  private static void broadcastImpl(WindowBroadcastEvent event, Component component, Set dontRevisit) {
    
    
    if (dontRevisit.contains(component))
      return;
    
    
    if (component instanceof WindowBroadcastListener) {
      try {
        if (!((WindowBroadcastListener)component).handleBroadcastEvent(event))
          return;
      } catch (Throwable t) {
        LOG.log(Level.WARNING, "broadcast listener threw throwable - not recursing broadcast", t);
        return;
      }
    }
    
    
    if (component instanceof Container) {
      Component[] cs = (((Container)component).getComponents());
      for (int j = 0; j < cs.length; j++) {
        broadcastImpl(event, cs[j], dontRevisit);
      }
    }
    
    
  }
    
  
  public abstract void close(String key);
  
  
  public abstract List getRootComponents();
  
  
  public abstract JComponent getContent(String key);
  
  
  public abstract boolean show(String key);
  
  
  public abstract void setTitle(String key, String title);
  
  
  public static WindowManager getInstance(Component component) {
    WindowManager result =  getInstanceImpl(component);
    if (result==null)
      LOG.warning("Failed to find window manager for "+component);
    return result;
  }
  
  private static WindowManager getInstanceImpl(Component component) {
    
    Component window = component;
    while (window.getParent()!=null) window = window.getParent();
    
    return (WindowManager)window2manager.get(window);
  }
  
  
  public final String openWindow(String key, String title, ImageIcon image, JComponent content, JMenuBar menu, Action close) {
    
    if (key==null) 
      key = getTemporaryKey();
    
    close(key);
    
    Rectangle bounds = registry.get(key, (Rectangle)null);
    boolean maximized = registry.get(key+".maximized", false);
    
    Component window = openWindowImpl(key, title, image, content, menu, bounds, maximized, close);
    
    window2manager.put(window, this);
    key2window.put(key, window);
    
    return key;
  }
  
  
  protected abstract Component openWindowImpl(String key, String title, ImageIcon image, JComponent content, JMenuBar menu, Rectangle bounds, boolean maximized, Action onClosing);
  
  
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
    
    content.putClientProperty(WINDOW_MANAGER_KEY, this);
    
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
  
  
  protected abstract Object openDialogImpl(String key, String title,  int messageType, JComponent content, Action[] actions, Component owner, Rectangle bounds);

  
  public final String openNonModalDialog(String key, String title,  int messageType, JComponent content, Action[] actions, Component owner) {
    
    content.putClientProperty(WINDOW_MANAGER_KEY, this);
    
    if (actions==null) actions = new Action[0];
    
    if (key==null) 
      key = getTemporaryKey();
    
    close(key);
    
    Rectangle bounds = registry.get(key, (Rectangle)null);
    
    Component window = openNonModalDialogImpl(key, title, messageType, content, actions, owner, bounds);
    
    window2manager.put(window, this);
    key2window.put(key, window);
    
    return key;
  }

  
  protected abstract Component openNonModalDialogImpl(String key, String title,  int messageType, JComponent content, Action[] actions, Component owner, Rectangle bounds);

  
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
