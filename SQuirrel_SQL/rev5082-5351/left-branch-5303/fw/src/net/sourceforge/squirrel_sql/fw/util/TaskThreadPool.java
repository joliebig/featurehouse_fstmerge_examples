package net.sourceforge.squirrel_sql.fw.util;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class TaskThreadPool
{
	
	private static ILogger s_log =
		LoggerController.createLogger(TaskThreadPool.class);

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(TaskThreadPool.class);
    
	
	private int _iFree;

	
	private int _threadCount;

	private List<Runnable> _tasks = new ArrayList<Runnable>();

	private MyCallback _callback = new MyCallback();
   private JFrame _parentForMessages = null;

	
	public synchronized void addTask(Runnable task)
		throws IllegalArgumentException
	{
		if (task == null)
		{
			throw new IllegalArgumentException("Null Runnable passed");
		}
		_tasks.add(task);
		
		if (_iFree == 0)
		{
			Thread th = new Thread(new TaskExecuter(_callback));
			th.setPriority(Thread.MIN_PRIORITY); 
			th.setDaemon(true);
			th.start();
			++_threadCount;
			s_log.debug("Creating thread nbr: " + _threadCount);
		}
		else
		{
			synchronized (_callback)
			{
				s_log.debug("Reusing existing thread");
				_callback.notify();
			}
		}
	}

   public void setParentForMessages(JFrame parentForMessages)
   {
      _parentForMessages = parentForMessages;
   }

   private final class MyCallback implements ITaskThreadPoolCallback
	{
		public void incrementFreeThreadCount()
		{
			++_iFree;
			s_log.debug("Returning thread. " + _iFree + " threads available");
		}

		public void decrementFreeThreadCount()
		{
			--_iFree;
			s_log.debug("Using a thread. " + _iFree + " threads available");
		}

		public synchronized Runnable nextTask()
		{
			if (_tasks.size() > 0)
			{
				return _tasks.remove(0);
			}
			return null;
		}

		public void showMessage(final Throwable th)
		{
         s_log.error("Error", th);
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               
               StringBuffer msg = 
                   new StringBuffer(
                       s_stringMgr.getString(
                                      "TaskThreadPool.errorDuringTaskExecMsg"));
               msg.append("\n");
               msg.append(th.getMessage());
               JOptionPane.showMessageDialog(_parentForMessages, msg.toString());
               throw new RuntimeException(th);
            }
         });
		}
	}
}
