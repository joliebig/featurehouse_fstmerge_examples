package net.sourceforge.squirrel_sql.client.session;

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.Statement;

public class CancelStatementThread extends Thread
{
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(CancelStatementThread.class);

   private static final ILogger s_log = LoggerController.createLogger(CancelStatementThread.class);


   private Statement _stmt;
   private IMessageHandler _messageHandler;
   private boolean _threadFinished;
   private boolean _joinReturned;

   public CancelStatementThread(Statement stmt, IMessageHandler messageHandler)
   {
      _stmt = stmt;
      _messageHandler = messageHandler;
   }

   public void tryCancel()
   {
      try
      {
         start();
         join(1500);

         synchronized (this)
         {
            _joinReturned = true;
            if(false == _threadFinished)
            {
               
               String msg = s_stringMgr.getString("CancelStatementThread.cancelTimedOut");
               _messageHandler.showErrorMessage(msg);
               s_log.error(msg);
            }
         }
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }


   public void run()
   {
      String msg;

      boolean cancelSucceeded = false;
      boolean closeSucceeded = false;

      try
      {
          if (_stmt != null) {
              _stmt.cancel();
          }
         cancelSucceeded = true;
      }
      catch (Throwable t)
      {
         
         msg = s_stringMgr.getString("CancelStatementThread.cancelFailed", t);
         _messageHandler.showErrorMessage(msg);
         s_log.error(msg, t);
      }


      try
      {
         
         
         
         Thread.sleep(500);
         if (_stmt != null) {
             _stmt.close();
         }
         closeSucceeded = true;
      }
      catch (Throwable t)
      {
         
         msg = s_stringMgr.getString("CancelStatementThread.closeFailed", t);
         _messageHandler.showErrorMessage(msg);
         s_log.error(msg, t);
      }


      synchronized (this)
      {

         if (cancelSucceeded && closeSucceeded)
         {
            if (_joinReturned)
            {
               
               msg = s_stringMgr.getString("CancelStatementThread.cancelSucceededLate");
               _messageHandler.showMessage(msg);
            }
            else
            {
               
               msg = s_stringMgr.getString("CancelStatementThread.cancelSucceeded");
               _messageHandler.showMessage(msg);
            }
         }

         _threadFinished = true;
      }

   }
}
