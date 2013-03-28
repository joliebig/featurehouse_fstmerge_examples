package net.sourceforge.squirrel_sql.fw.util;



public class NullMessageHandler implements IMessageHandler
{
	private static NullMessageHandler s_handler = new NullMessageHandler();

	
	private NullMessageHandler()
	{
		super();
	}

	
	public static NullMessageHandler getInstance()
	{
		return s_handler;
	}

	
	public void showMessage(Throwable th, ExceptionFormatter formatter)
	{
		
	}

	
	public void showMessage(String msg)
	{
		
	}

	
	public void showErrorMessage(Throwable th, ExceptionFormatter formatter)
	{
		
	}

	
	public void showErrorMessage(String msg)
	{
		
	}

   public void showWarningMessage(String msg)
   {
      
   }
      
}
