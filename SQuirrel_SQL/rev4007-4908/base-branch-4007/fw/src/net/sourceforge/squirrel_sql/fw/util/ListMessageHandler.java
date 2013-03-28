package net.sourceforge.squirrel_sql.fw.util;

import java.util.List;
import java.util.Vector;


public class ListMessageHandler implements IMessageHandler
{
   
   


   
   private List<String> _msgs = new Vector<String>();

   
   private List<Throwable> _throwables = new Vector<Throwable>();

   
   private List<String> _errMsgs = new Vector<String>();

   
   private List<Throwable> _errThrowables = new Vector<Throwable>();

   private List<String> _warningMsgs = new Vector<String>();

   
   

   
   public ListMessageHandler()
   {
      super();
   }

   
   public void showMessage(Throwable th, ExceptionFormatter formatter)
   {
      _throwables.add(th);
   }

   
   public void showMessage(String msg)
   {
      _msgs.add(msg);
   }

   
   public void showErrorMessage(Throwable th, ExceptionFormatter formatter)
   {
      _errThrowables.add(th);
   }

   
   public void showErrorMessage(String msg)
   {
      _errMsgs.add(msg);
   }

   public void showWarningMessage(String msg)
   {
      _warningMsgs.add(msg);
   }

   
   public Throwable[] getExceptions()
   {
      return _throwables.toArray(new Throwable[_throwables.size()]);
   }

   
   public Throwable[] getErrorExceptions()
   {
      return _errThrowables.toArray(new Throwable[_errThrowables.size()]);
   }

   
   public String[] getMessages()
   {
      return _msgs.toArray(new String[_msgs.size()]);
   }

   
   public String[] getErrorMessages()
   {
      return _errMsgs.toArray(new String[_errMsgs.size()]);
   }

   public String[] getWarningMessages()
   {
      return _warningMsgs.toArray(new String[_warningMsgs.size()]);
   }   
}
