
package genj.util.swing;


import genj.util.MnemonicAndText;
import genj.util.Resources;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


public class Action2 extends AbstractAction implements Runnable, Cloneable {
  
  private final static String 
    KEY_TEXT = Action.NAME,
    KEY_OLDTEXT = Action.NAME+".old",
    KEY_SHORT_TEXT = "shortname",
    KEY_TIP = Action.SHORT_DESCRIPTION,
    KEY_ENABLED = "enabled",
    KEY_MNEMONIC = Action.MNEMONIC_KEY,
    KEY_ICON = Action.SMALL_ICON;
    
  private final static Logger LOG = Logger.getLogger("genj.actions");
  
  
  public static final Action2 NOOP = new ActionNOOP();
  
  
  public static final int 
    ASYNC_NOT_APPLICABLE = 0,
    ASYNC_SAME_INSTANCE  = 1,
    ASYNC_NEW_INSTANCE   = 2;
  
  
  private Component target;
  private KeyStroke accelerator;
  
  
  private int async = ASYNC_NOT_APPLICABLE;
  
  
  private Thread thread;
  private Object threadLock = new Object();

  
  public final static String
    TXT_YES         = UIManager.getString("OptionPane.yesButtonText"),
    TXT_NO          = UIManager.getString("OptionPane.noButtonText"),
    TXT_OK          = UIManager.getString("OptionPane.okButtonText"),
    TXT_CANCEL  = UIManager.getString("OptionPane.cancelButtonText");
  
  
  public Action2() {
  }
  
  
  public Action2(Resources resources, String text) {
    this(resources.getString(text));
  }
  
  
  public Action2(String text) {
    setText(text);
  }
  
  
  public Action2(String text, boolean enabled) {
    this(text);
    setEnabled(enabled);
  }
  
  
  public final void actionPerformed(ActionEvent e) {
    trigger();
  }
  
  
  public final void run() {
    trigger();
  }

  
  public final boolean trigger() {
    
    
    if (!isEnabled()) 
      throw new IllegalArgumentException("trigger() while !isEnabled");

    
    if (async==ASYNC_NEW_INSTANCE) {
      try {
        Action2 ad = (Action2)clone();
        ad.setAsync(ASYNC_SAME_INSTANCE);
        return ad.trigger();
      } catch (Throwable t) {
        t.printStackTrace();
        handleThrowable("trigger", new RuntimeException("Couldn't clone instance of "+getClass().getName()+" for ASYNC_NEW_INSTANCE"));
      }
      return false;
    }
    
    
    boolean preExecuteResult;
    try {
      preExecuteResult = preExecute();
    } catch (Throwable t) {
      handleThrowable("preExecute",t);
      preExecuteResult = false;
    }
    
    
    if (preExecuteResult) try {
      
      if (async!=ASYNC_NOT_APPLICABLE) {
        
        synchronized (threadLock) {
          getThread().start();
        }
        
      } else {
        execute();
      }
      
    } catch (Throwable t) {
      handleThrowable("execute(sync)", t);
     
      
      
      
      preExecuteResult = false;
    }
    
    
    if (async==ASYNC_NOT_APPLICABLE||!preExecuteResult) try {
      postExecute(preExecuteResult);
    } catch (Throwable t) {
      handleThrowable("postExecute", t);
    }
    
    
    return preExecuteResult;
  }
  
  
  protected void setAsync(int set) {
    async=set;
  }
  
  
  public void cancel(boolean wait) {

    Thread cancel;
    synchronized (threadLock) {
      if (thread==null||!thread.isAlive()) 
        return;
      cancel = thread;      
      cancel.interrupt();
    }

    if (wait) try {
      cancel.join();
    } catch (InterruptedException e) {
    }

    
  }
  
  
  protected Thread getThread() {
    if (async!=ASYNC_SAME_INSTANCE) return null;
    synchronized (threadLock) {
      if (thread==null) {
        thread = new Thread(new CallAsyncExecute());
        thread.setPriority(Thread.NORM_PRIORITY);
      }
      return thread;
    }
  }
  
  
  protected boolean preExecute() {
    
    return true;
  }
  
  
  protected void execute() {
    
  }
  
  
  protected final void sync() {
    if (SwingUtilities.isEventDispatchThread())
      syncExecute();
    else
      SwingUtilities.invokeLater(new CallSyncExecute());
  }
  
  
  protected void syncExecute() {
  }
  
  
  protected void postExecute(boolean preExecuteResult) {
    
  }
  
  
  protected void handleThrowable(String phase, Throwable t) {
    LogRecord record = new  LogRecord(Level.WARNING, "Action failed in "+phase);
    record.setThrown(t);
    record.setSourceClassName(getClass().getName());
    record.setSourceMethodName(phase);
    LOG.log(record); 
  }
  
  
  public Object getValue(String key) {
    if (KEY_TEXT.equals(key))
      return getText();
    if (KEY_ICON.equals(key))
      return getImage();
    if (KEY_TIP.equals(key))
      return getTip();
    return super.getValue(key);
  }
  
  
  public Action2 setTarget(Component t) {
    
    target = t;
    return this;
  }
  
  
  public Component getTarget() {
    return target;
  }
  
  
  public Action2 setAccelerator(String s) {
    accelerator = KeyStroke.getKeyStroke(s);
    return this;
  }
  
  
  public Action2 setAccelerator(KeyStroke accelerator) {
    this.accelerator = accelerator;
    return this;
  }
  
  
  public Action2 setImage(Icon icon) {
    super.putValue(KEY_ICON, icon);
    return this;
  }
  
  
  public Action2 restoreText() {
    setText((String)super.getValue(KEY_OLDTEXT));
    return this;
  }
  
  
  public Action2 setText(String txt) {
    return setText(null, txt);
  }
    
  
  public Action2 setText(Resources resources, String txt) {
    
    
    if (resources!=null)
      txt = resources.getString(txt);
      
    
    super.putValue(KEY_OLDTEXT, getText());
    
    
    if (txt!=null&&txt.length()>0)  {
        
        MnemonicAndText mat = new MnemonicAndText(txt);
        txt  = mat.getText();
        
        setMnemonic(mat.getMnemonic());
    }
    
    
    super.putValue(KEY_TEXT, txt);
    
    return this;
  }
  
  
  public Action2 setMnemonic(char c) {
    super.putValue(KEY_MNEMONIC, c==0 ? null : new Integer(c));
    return this;
  }

  
  public String getText() {
    return (String)super.getValue(KEY_TEXT);
  }
  
  
  public Action2 setTip(String tip) {
    return setTip(null, tip);
  }
  
  
  public Action2 setTip(Resources resources, String tip) {
    if (resources!=null) tip = resources.getString(tip);
    super.putValue(KEY_TIP, tip);
    return this;
  }
  
  
  public String getTip() {
    return (String)super.getValue(KEY_TIP);
  }

  
  public Icon getImage() {
    return (Icon)super.getValue(KEY_ICON);
  }

  
  public KeyStroke getAccelerator() {
    return accelerator;
  }
  
  
  public void install(JComponent into, int condition) {
    
    if (accelerator==null)
      return;
    
    InputMap inputs = into.getInputMap(condition);
    inputs.put(accelerator, this);
    into.getActionMap().put(this, this);
      
  }

  
  public static Action yes() {
    return new Action2(Action2.TXT_YES);
  }

  
  public static Action no() {
    return new Action2(Action2.TXT_NO);
  }

  
  public static Action ok() {
    return new Action2(Action2.TXT_OK);
  }

  
  public static Action cancel() {
    return new Action2(Action2.TXT_CANCEL);
  }

  
  public static Action[] yesNo() {
    return new Action[]{ yes(), no() };
  }
  
  
  public static Action[] yesNoCancel() {
    return new Action[]{ yes(), no(), cancel() };
  }
  
  
  public static Action[] okCancel() {
    return new Action[]{ ok(), cancel() };
  }
  
  
  public static Action[] okAnd(Action action) {
    return new Action[]{ ok(), action };
  }
  
  
  public static Action[] okOnly() {
    return new Action[]{ ok() };
  }
  
  
  public static Action[] cancelOnly() {
    return new Action[]{ cancel() };
  }
  
  
  private class CallAsyncExecute implements Runnable {
    public void run() {
      
      Throwable thrown = null;
      try {
        execute();
      } catch (Throwable t) {
        thrown = t;
      }
      
      
      synchronized (threadLock) {
        thread = null;
      }
      
      
      if (thrown!=null)
        SwingUtilities.invokeLater(new CallSyncHandleThrowable(thrown));
      
      
      SwingUtilities.invokeLater(new CallSyncPostExecute());
    }
  } 
  
  
  private class CallSyncPostExecute implements Runnable {
    public void run() {
      try {
        postExecute(true);
      } catch (Throwable t) {
        handleThrowable("postExecute", t);
      }
    }
  } 
  
  
  private class CallSyncExecute implements Runnable {
    public void run() {
      try {
        syncExecute();
      } catch (Throwable t) {
        handleThrowable("syncExecute", t);
      }
    }
  } 
  
  
  private class CallSyncHandleThrowable implements Runnable {
    private Throwable t;
    protected CallSyncHandleThrowable(Throwable set) {
      t=set;
    }
    public void run() {
      
      try {
        handleThrowable("execute(async)",t);
      } catch (Throwable t) {
      }
    }
  } 
  
  
  private static class ActionNOOP extends Action2 {
    
    protected void execute() {
      
    }
  } 
  
  
  public static class Group extends ArrayList {
    
    
    private String name;
    private ImageIcon icon;
    
    
    public Group(String name, ImageIcon imageIcon) {
      this.icon = imageIcon;
      this.name = name;
    }
    public Group(String name) {
    	this(name,null);
    }
    
    
    public String getName() {
      return name;
    }
	public ImageIcon getIcon() {
		return icon;
	}
  }

} 

