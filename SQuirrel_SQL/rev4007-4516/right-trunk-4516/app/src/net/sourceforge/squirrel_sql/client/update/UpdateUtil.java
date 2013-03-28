
package net.sourceforge.squirrel_sql.client.update;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

public interface UpdateUtil
{

	
	public static final String LOCAL_UPDATE_DIR_NAME = "update";

	
	public static final String BACKUP_ROOT_DIR_NAME = "backup";

	
	public static final String RELEASE_XML_FILENAME = "release.xml";

	
	public static final String CHANGE_LIST_FILENAME = "changeList.xml";

	
	public final static String DOWNLOADS_DIR_NAME = "downloads";

	
	public static final String CORE_ARTIFACT_ID = "core";

	
	public static final String PLUGIN_ARTIFACT_ID = "plugin";

	
	public static final String TRANSLATION_ARTIFACT_ID = "i18n";

	
	public static final String SQUIRREL_SQL_JAR_FILENAME = "squirrel-sql.jar";

	
	ChannelXmlBean downloadCurrentRelease(final String host, final int port, final String path,
		final String fileToGet) throws Exception;

	
	ChannelXmlBean loadUpdateFromFileSystem(final String path);

	
	String downloadHttpUpdateFile(String host, int port, String fileToGet, String destDir, long fileSize,
		long checksum) throws Exception;

	
	boolean downloadLocalUpdateFile(String fileToGet, String destDir) throws FileNotFoundException, IOException;

	
	void copyFile(final FileWrapper from, final FileWrapper to) throws FileNotFoundException, IOException;

	
	void copyDir(final FileWrapper fromDir, final FileWrapper toDir) throws FileNotFoundException, IOException; 
	
	
	ChannelXmlBean getLocalReleaseInfo(String localReleaseFile);

	
	FileWrapper getSquirrelHomeDir();

		
	FileWrapper getSquirrelPluginsDir();

			
	FileWrapper getSquirrelLibraryDir();

	
	FileWrapper getChangeListFile();

	FileWrapper checkDir(FileWrapper parent, String child);

	void createZipFile(FileWrapper zipFile, FileWrapper... sourceFiles) throws FileNotFoundException, IOException;

	
	FileWrapper getSquirrelUpdateDir();

	
	void saveChangeList(List<ArtifactStatus> changes) throws FileNotFoundException;

	
	ChangeListXmlBean getChangeList() throws FileNotFoundException;

	
	FileWrapper getLocalReleaseFile() throws FileNotFoundException;

	
	List<ArtifactStatus> getArtifactStatus(ChannelXmlBean channelXmlBean);

	List<ArtifactStatus> getArtifactStatus(ReleaseXmlBean releaseXmlBean);

	
	Set<String> getInstalledPlugins();

	
	Set<String> getInstalledTranslations();

	
	IPluginManager getPluginManager();

	
	void setPluginManager(IPluginManager manager);

	
	FileWrapper getDownloadsDir();

	FileWrapper getCoreDownloadsDir();

	FileWrapper getPluginDownloadsDir();

	FileWrapper getI18nDownloadsDir();

	FileWrapper getBackupDir();
	
	FileWrapper getCoreBackupDir();
	
	FileWrapper getPluginBackupDir();
	
	FileWrapper getI18nBackupDir();
	
	
	FileWrapper getInstalledSquirrelMainJarLocation();
	
	ChangeListXmlBean getChangeList(FileWrapper changeListFile) throws FileNotFoundException;

	FileWrapper getFile(FileWrapper installDir, String artifactName);

	
	boolean deleteFile(FileWrapper path);

	
	void extractZipFile(FileWrapper zipFile, FileWrapper outputDirectory) throws IOException;
	
	
	FileWrapper getDownloadFileLocation(ArtifactStatus status);
	
	boolean isPresentInDownloadsDirectory(ArtifactStatus status);

	
	public long getCheckSum(FileWrapper f);	
}