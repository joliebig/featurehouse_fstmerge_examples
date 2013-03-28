
package genj.app;

import genj.Version;
import genj.gedcom.Gedcom;
import genj.option.OptionProvider;
import genj.util.EnvironmentChecker;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.window.DefaultWindowManager;
import genj.window.WindowManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import javax.swing.SwingUtilities;

import sun.rmi.log.LogHandler;


public class App {
  
   static Logger LOG;
  
   static File LOGFILE; 
  
  private static Startup startup;
  
  
  public static void main(final String[] args) {
    
    
    synchronized (App.class) {
      if (startup==null)  {
        
        startup = new Startup();
        SwingUtilities.invokeLater(startup);
      }
    }
    
    
    synchronized (startup) {
      if (startup.center==null) try {
        startup.wait();
      } catch (InterruptedException e) {
      }
    }

  
    SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        startup.center.load(args);
      }
    } );
    
  }
  
  
  private static class Startup implements Runnable {
    
    ControlCenter center;

    
    public void run() {
      
      
      try {
        
        
        File home = new File(EnvironmentChecker.getProperty(App.class, "user.home.genj", null, "determining home directory"));
        home.mkdirs();
        if (!home.exists()||!home.isDirectory()) 
          throw new IOException("Can't initialize home directoy "+home);
        
        
        LOG = Logger.getLogger("genj");
        
        
        Formatter formatter = new LogFormatter();
        Logger root = Logger.getLogger("");
        
        try {
          
          Level level = Level.parse(System.getProperty("genj.debug.level"));
          LOG.setLevel(level);
          if (Integer.MAX_VALUE!=level.intValue())
            root.setLevel(new Level("genj.debug.level+1", level.intValue()+1) {} );
        } catch (Throwable t) {
        }
        
        Handler[] handlers = root.getHandlers();
        for (int i=0;i<handlers.length;i++) root.removeHandler(handlers[i]);
        BufferedHandler bufferedLogHandler = new BufferedHandler();
        root.addHandler(bufferedLogHandler);
        root.addHandler(new FlushingHandler(new StreamHandler(System.out, formatter)));
        System.setOut(new PrintStream(new LogOutputStream(Level.INFO, "System", "out")));
        System.setErr(new PrintStream(new LogOutputStream(Level.WARNING, "System", "err")));

        
        LOG.info("Startup");
        
        
        Registry registry = new Registry("genj");
        
        
        OptionProvider.getAllOptions(registry);
        
        
        LOGFILE = new File(home, "genj.log");
        Handler handler = new FileHandler(LOGFILE.getAbsolutePath(), Options.getInstance().getMaxLogSizeKB()*1024, 1, true);
        handler.setLevel(Level.ALL);
        handler.setFormatter(formatter);
        LOG.addHandler(handler);
        root.removeHandler(bufferedLogHandler);
        bufferedLogHandler.flush(handler);
        
        
        LOG.info("version = "+Version.getInstance().getBuildString());
        LOG.info("date = "+new Date());
        EnvironmentChecker.log();
        
        
        if (EnvironmentChecker.isMac()) {
          LOG.info("Setting up MacOs adjustments");
          System.setProperty("apple.laf.useScreenMenuBar","true");
          System.setProperty("com.apple.mrj.application.apple.menu.about.name","GenealogyJ");
        }
        
        
        if (!EnvironmentChecker.isJava14(App.class)) {
          if (EnvironmentChecker.getProperty(App.class, "genj.forcevm", null, "Check force of VM")==null) {
            LOG.severe("Need Java 1.4 to run GenJ");
            System.exit(1);
            return;
          }
        }
        
        
        Resources resources = Resources.get(App.class);
  
        
        WindowManager winMgr = new DefaultWindowManager(new Registry(registry, "window"), Gedcom.getImage());
        
        
        String version = Version.getInstance().getVersionString();
        if (!version.equals(registry.get("disclaimer",""))) {
          
          registry.put("disclaimer", version);
          
          winMgr.openDialog("disclaimer", "Disclaimer", WindowManager.INFORMATION_MESSAGE, resources.getString("app.disclaimer"), Action2.okOnly(), null);    
        }
        
        
        center = new ControlCenter(registry, winMgr, new Shutdown(registry));
  
        
        winMgr.openWindow("cc", resources.getString("app.title"), Gedcom.getImage(), center, center.getMenuBar(), center.getExitAction());
  
        
        LOG.info("/Startup");
      
      } catch (Throwable t) {
        LOG.log(Level.SEVERE, "Cannot instantiate App", t);
        System.exit(1);
        return;
      }
      
      synchronized (this) {
        notifyAll();
      }
      
    }
    
  } 

  
  private static class Shutdown implements Runnable {
    
    private Registry registry;
    
    
    private Shutdown(Registry registry) {
      this.registry = registry;
    }
    
    public void run() {
      LOG.info("Shutdown");
	    
	    OptionProvider.persistAll(registry);
	    
	    Registry.persist();      
	    
      LOG.info("/Shutdown");
      
      System.exit(0);
      
    }
    
  } 
  
  
  private static class BufferedHandler extends Handler {
    
    private List<LogRecord> buffer = new ArrayList<LogRecord>();

    @Override
    public void close() throws SecurityException {
      
    }

    @Override
    public void flush() {
      
    }
    
    private void flush(Handler other) {
      for (LogRecord record : buffer)
        other.publish(record);
      buffer.clear();
    }

    @Override
    public void publish(LogRecord record) {
      buffer.add(record);
    }
    
  }

  
  private static class FlushingHandler extends Handler {
    private Handler wrapped;
    private FlushingHandler(Handler wrapped) {
      this.wrapped = wrapped;
      wrapped.setLevel(Level.ALL);
      setLevel(Level.ALL);
    }
    public void publish(LogRecord record) {
      wrapped.publish(record);
      flush();
    }
    public void flush() {
      wrapped.flush();
    }
    public void close() throws SecurityException {
      flush();
      wrapped.close();
    }
  }
  
  
  private static class LogFormatter extends Formatter {
    public String format(LogRecord record) {
      StringBuffer result = new StringBuffer(80);
      result.append(record.getLevel());
      result.append(":");
      result.append(record.getSourceClassName());
      result.append(".");
      result.append(record.getSourceMethodName());
      result.append(":");
      String msg = record.getMessage();
      Object[] parms = record.getParameters();
      if (parms==null||parms.length==0)
        result.append(record.getMessage());
      else 
        result.append(MessageFormat.format(msg, parms));
      result.append(System.getProperty("line.separator"));

      if (record.getThrown()!= null) {
        
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            record.getThrown().printStackTrace(pw);
        } catch (Throwable t) {
        }
        pw.close();
        result.append(sw.toString());
      }      
      
      return result.toString();
    }
  }
  
  
  private static class LogOutputStream extends OutputStream {
    
    private char[] buffer = new char[256];
    private int size = 0;
    private Level level;
    private String sourceClass, sourceMethod;
    
    
    public LogOutputStream(Level level, String sourceClass, String sourceMethod) {
      this.level = level;
      this.sourceClass = sourceClass;
      this.sourceMethod = sourceMethod;
    }
    
    
    public void write(int b) throws IOException {
      if (b!='\n') {
       buffer[size++] = (char)b;
       if (size<buffer.length) 
         return;
      }
      flush();
    }

    
    public void flush() throws IOException {
      if (size>0) {
        LOG.logp(level, sourceClass, sourceMethod, String.valueOf(buffer, 0, size).trim());
        size = 0;
      }
    }
  }
    
} 
