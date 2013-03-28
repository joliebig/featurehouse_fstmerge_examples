
package net.sourceforge.squirrel_sql.client.update;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.update.downloader.ArtifactDownloaderFactory;

public interface UpdateControllerFactory
{
	UpdateController createUpdateController(IApplication app, 
		ArtifactDownloaderFactory downloaderFactory, UpdateUtil util);
}
