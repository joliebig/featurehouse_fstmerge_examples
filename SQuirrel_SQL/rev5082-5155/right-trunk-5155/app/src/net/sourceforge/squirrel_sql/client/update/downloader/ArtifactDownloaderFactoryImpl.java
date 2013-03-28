
package net.sourceforge.squirrel_sql.client.update.downloader;

import java.util.List;

import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class ArtifactDownloaderFactoryImpl implements ArtifactDownloaderFactory
{

	
	public ArtifactDownloader create(List<ArtifactStatus> artifactStatus) {
		Utilities.checkNull("create", "artifactStatus", artifactStatus);
		if (artifactStatus.size() == 0) {
			throw new IllegalArgumentException("create: list parameter must have one or more artifacts");
		}
		return new ArtifactDownloaderImpl(artifactStatus);
	}
}
