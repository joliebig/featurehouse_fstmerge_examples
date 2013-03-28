
package net.sourceforge.squirrel_sql.client.update;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloaderFactory;

public class UpdateControllerFactoryImpl implements UpdateControllerFactory
{

	private static UpdateControllerImpl _instance = null; 
	
	public UpdateController createUpdateController(IApplication app,
		ArtifactDownloaderFactory downloaderFactory, UpdateUtil util)
	{
		if (_instance == null) {
			_instance = new UpdateControllerImpl(app);
		   _instance.setArtifactDownloaderFactory(downloaderFactory);
		   _instance.setUpdateUtil(util);
		}
		return _instance;
	}

}
