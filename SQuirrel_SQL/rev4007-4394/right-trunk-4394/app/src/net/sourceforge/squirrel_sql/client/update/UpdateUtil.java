
package net.sourceforge.squirrel_sql.client.update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;

public interface UpdateUtil
{

	
	public static final String HTTP_PROTOCOL_PREFIX = "http";

	
	public static final String LOCAL_UPDATE_DIR_NAME = "update";

	
	public static final String BACKUP_ROOT_DIR_NAME = "backup";

	
	public static final String RELEASE_XML_FILENAME = "release.xml";

	
	public static final String CHANGE_LIST_FILENAME = "changeList.xml";

	
	public final static String DOWNLOADS_DIR_NAME = "downloads";

	
	public static final String CORE_ARTIFACT_ID = "core";

	
	public static final String PLUGIN_ARTIFACT_ID = "plugin";

	
	public static final String TRANSLATION_ARTIFACT_ID = "i18n";

	
	ChannelXmlBean downloadCurrentRelease(final String host, final int port, final String path,
		final String fileToGet) throws Exception;

	
	ChannelXmlBean loadUpdateFromFileSystem(final String path);

	
	String downloadHttpFile(String host, int port, String fileToGet, String destDir, long fileSize,
		long checksum) throws Exception;

	
	boolean downloadLocalFile(String fileToGet, String destDir) throws FileNotFoundException, IOException;

	
	void copyFile(final File from, final File to) throws FileNotFoundException, IOException;

	
	ChannelXmlBean getLocalReleaseInfo(String localReleaseFile);

	
	File getSquirrelHomeDir();

	File getSquirrelPluginsDir();

	File getSquirrelLibraryDir();

	
	File getChangeListFile();

	File checkDir(File parent, String child);

	void createZipFile(File zipFile, File[] sourceFiles) throws FileNotFoundException, IOException;

	
	File getSquirrelUpdateDir();

	
	void saveChangeList(List<ArtifactStatus> changes) throws FileNotFoundException;

	
	ChangeListXmlBean getChangeList() throws FileNotFoundException;

	
	String getLocalReleaseFile() throws FileNotFoundException;

	
	List<ArtifactStatus> getArtifactStatus(ChannelXmlBean channelXmlBean);

	List<ArtifactStatus> getArtifactStatus(ReleaseXmlBean releaseXmlBean);

	
	Set<String> getInstalledPlugins();

	
	Set<String> getInstalledTranslations();

	
	IPluginManager getPluginManager();

	
	void setPluginManager(IPluginManager manager);

	
	File getDownloadsDir();

	File getCoreDownloadsDir();

	File getPluginDownloadsDir();

	File getI18nDownloadsDir();

	ChangeListXmlBean getChangeList(File changeListFile) throws FileNotFoundException;

	boolean fileExists(File File);

	File getFile(File installDir, String artifactName);

	
	boolean deleteFile(File path);

	
	void extractZipFile(File zipFile, File outputDirectory) throws IOException;
}