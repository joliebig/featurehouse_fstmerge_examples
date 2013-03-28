package net.sourceforge.squirrel_sql.client;

import javax.swing.JOptionPane;


public class Main
{
	
	private Main()
	{
		super();
	}

	
	public static void main(String[] args)
	{
		if (ApplicationArguments.initialize(args))
		{

         if(false == Version.supportsUsedJDK())
         {
            JOptionPane.showMessageDialog(null, Version.getUnsupportedJDKMessage());
            System.exit(-1);
         }

			final ApplicationArguments appArgs = ApplicationArguments.getInstance();
			if (appArgs.getShowHelp())
			{
				appArgs.printHelp();
			}
			else
			{
				new Application().startup();
			}
		}
	}
}
