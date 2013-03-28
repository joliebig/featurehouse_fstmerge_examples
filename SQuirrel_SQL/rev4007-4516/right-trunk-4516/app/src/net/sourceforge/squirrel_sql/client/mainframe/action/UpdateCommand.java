package net.sourceforge.squirrel_sql.client.mainframe.action;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.update.UpdateControllerImpl;
import net.sourceforge.squirrel_sql.client.update.UpdateUtilImpl;
import net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloaderFactory;
import net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloaderFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class UpdateCommand implements ICommand
{
	
	private IApplication _app;

	
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
	   UpdateControllerImpl updateController = new UpdateControllerImpl(_app);
	   ArtifactDownloaderFactory downloaderFactory = new ArtifactDownloaderFactoryImpl();
	   updateController.setArtifactDownloaderFactory(downloaderFactory);
	   updateController.setUpdateUtil(new UpdateUtilImpl());
	   updateController.showUpdateDialog();
	}
}
