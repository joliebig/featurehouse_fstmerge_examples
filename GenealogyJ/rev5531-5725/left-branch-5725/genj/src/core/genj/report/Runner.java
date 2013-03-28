
package genj.report;

import genj.gedcom.Gedcom;
import genj.gedcom.UnitOfWork;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;


 class Runner implements Runnable {
  
  private final static Logger LOG = Logger.getLogger("genj.report");

  private final static long FLUSH_WAIT = 500;

  private Gedcom gedcom;
  private Object context;
  private Report report;
  private Callback callback;
  private Object result;
  
  
   Runner(Gedcom gedcom, Object context, Report report, Callback callback) {
    this.gedcom = gedcom;
    this.context= context;
    this.report = report;
    this.callback = callback;
  }
  
  public void run() {
    
    
    report.setOut(new PrintWriter(new WriterImpl()));
    
    
    try{
      if (report.isReadOnly()) {
        result = report.start(context);
      } else {
        final Object finalContext = context;
        gedcom.doUnitOfWork(new UnitOfWork() {
          public void perform(Gedcom gedcom) {
            try {
              result = report.start(finalContext);
            } catch (Throwable t) {
              throw new RuntimeException(t);
            }
          }
        });
      }    
    } catch (Throwable t) {
      if (t.getCause()!=null)
        t = t.getCause();
      result = t;
    } finally {
      
      report.flush();
      report.getOut().close();
    }
    
    
    callback.handleResult(report, result);

  }
  
  
  private class WriterImpl extends Writer {

    
    private StringBuffer buffer = new StringBuffer(4*1024);

    
    private long lastFlush = -1;
    
    
    public void close() {
      flush();
      LOG.log(Level.FINER, "close");
    }

    
    public void flush() {

      
      if (buffer.length()==0)
        return;

      
      lastFlush = System.currentTimeMillis();

      
      callback.handleOutput(report, buffer.toString());
        
      
      buffer.setLength(0);
      
      
    }

    
    
    public void write(char[] cbuf, int off, int len) throws IOException {
      
      for (int i=0;i<len;i++) {
        char c = cbuf[off+i];
        if (c!='\r') buffer.append(c);
      }
      
      if (System.currentTimeMillis()-lastFlush > FLUSH_WAIT)
        flush();
      
    }

  } 
  
  
  public interface Callback {
    
    public void handleOutput(Report report, String output);
    
    public void handleResult(Report report, Object result);
    
  }
}
