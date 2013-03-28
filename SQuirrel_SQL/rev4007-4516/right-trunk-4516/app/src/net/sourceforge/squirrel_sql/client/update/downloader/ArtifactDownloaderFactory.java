
package net.sourceforge.squirrel_sql.client.update.downloader;

import java.util.List;

import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;

public interface ArtifactDownloaderFactory
{

	
	ArtifactDownloader create(List<ArtifactStatus> artifactStatus);

}