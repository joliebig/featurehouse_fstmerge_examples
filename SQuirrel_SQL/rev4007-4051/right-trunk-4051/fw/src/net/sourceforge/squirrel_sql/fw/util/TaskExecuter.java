package net.sourceforge.squirrel_sql.fw.util;



class TaskExecuter implements Runnable
{
	private boolean _bStopThread = false;

	private ITaskThreadPoolCallback _callback;

	TaskExecuter(ITaskThreadPoolCallback callback)
		throws IllegalArgumentException
	{
		super();
		if (callback == null)
		{
			throw new IllegalArgumentException("Null IGUIExecutionControllerCallback passed");
		}
		_callback = callback;
	}

	public void run()
	{
		while (!_bStopThread)
		{
			Runnable task = null;
			synchronized (_callback)
			{
				_callback.incrementFreeThreadCount();
				while (!_bStopThread)
				{

					task = _callback.nextTask();
					if (task != null)
					{
						_callback.decrementFreeThreadCount();
						break;
					}
					else
					{
						try
						{
							_callback.wait();
						}
						catch (InterruptedException ignore)
						{
							
						}
					}
				}
			}
			if (task != null)
			{
				try
				{
					task.run();
				}
				catch (Throwable th)
				{
					_callback.showMessage(th);
				}
			}
		}
	}
}
