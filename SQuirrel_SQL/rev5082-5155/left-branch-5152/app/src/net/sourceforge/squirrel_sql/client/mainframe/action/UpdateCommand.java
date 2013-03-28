package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.update.UpdateController;
import net.sourceforge.squirrel_sql.client.update.UpdateControllerFactory;
import net.sourceforge.squirrel_sql.client.update.UpdateControllerFactoryImpl;
import net.sourceforge.squirrel_sql.client.update.UpdateUtilImpl;
import net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloaderFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class UpdateCommand implements ICommand
{
	
	private IApplication _app;

	
	private UpdateControllerFactory updateControllerFactory = new UpdateControllerFactoryImpl();
	
	
	public UpdateCommand(IApplication app)
	{
		super();
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
	}

	
	public void execute()
	{
	   UpdateController updateController = 
	   	updateControllerFactory.createUpdateController(_app,  new ArtifactDownloaderFactoryImpl(),
	   		new UpdateUtilImpl());
	   updateController.showUpdateDialog();
	}
}
