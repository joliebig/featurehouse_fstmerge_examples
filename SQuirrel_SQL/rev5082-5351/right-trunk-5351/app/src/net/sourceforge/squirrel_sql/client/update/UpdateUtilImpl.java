
package net.sourceforge.squirrel_sql.client.update;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.preferences.IUpdateSettings;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.util.PathUtils;
import net.sourceforge.squirrel_sql.client.update.util.PathUtilsImpl;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ArtifactXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ModuleXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializerImpl;
import net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappers;
import net.sourceforge.squirrel_sql.client.util.ApplicationFileWrappersImpl;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.IOUtilitiesImpl;
import net.sourceforge.squirrel_sql.fw.util.IProxySettings;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class UpdateUtilImpl implements UpdateUtil
{
	static {
		ApplicationArguments.initialize(new String[] {});
	}
	
	private final static ILogger s_log = LoggerController.createLogger(UpdateUtilImpl.class);

	
	private IPluginManager _pluginManager = null;

	
	public final static int ZIP_EXTRACTION_BUFFER_SIZE = 8192;

	
	private HashMap<String, Long> fileChecksumMap = new HashMap<String, Long>();

	
	private PathUtils _pathUtils = new PathUtilsImpl();

	public void setPathUtils(PathUtils pathUtils)
	{
		this._pathUtils = pathUtils;
	}

	
	private FileWrapperFactory _fileWrapperFactory = new FileWrapperFactoryImpl();

	public void setFileWrapperFactory(FileWrapperFactory factory)
	{
		_fileWrapperFactory = factory;
	}

	
	private ApplicationFileWrappers _appFileWrappers = new ApplicationFileWrappersImpl();

	public void setApplicationFileWrappers(ApplicationFileWrappers appFileWrappers)
	{
		_appFileWrappers = appFileWrappers;
	}

	
	private IOUtilities _iou = new IOUtilitiesImpl();

	public void setIOUtilities(IOUtilities iou)
	{
		_iou = iou;
	}

	
	private UpdateXmlSerializer _serializer = new UpdateXmlSerializerImpl();

	public void setUpdateXmlSerializer(UpdateXmlSerializer serializer)
	{
		_serializer = serializer;
	}

	
	public long getCheckSum(FileWrapper f)
	{
		String absPath = f.getAbsolutePath();

		Long result = -1L;
		if (fileChecksumMap.containsKey(absPath))
		{
			result = fileChecksumMap.get(absPath);
		}
		else
		{
			try
			{
				result = _iou.getCheckSum(f);
			}
			catch (IOException e)
			{
				s_log.error("getCheckSum: failed to compute the checksum for file (" + f.getAbsolutePath()
					+ "): " + e.getMessage(), e);
			}
			
			
			fileChecksumMap.put(absPath, result);
		}
		return result;
	}

	
	public ChannelXmlBean downloadCurrentRelease(final String host, final int port, final String path,
		final String fileToGet, final IProxySettings proxySettings) throws Exception
	{
		ChannelXmlBean result = null;
		if (s_log.isDebugEnabled())
		{
			s_log.debug("downloadCurrentRelease: host=" + host + " port=" + port + " path=" + path
				+ " fileToGet=" + fileToGet);
		}
		result = downloadCurrentReleaseHttp(host, port, path, fileToGet, proxySettings);
		return result;
	}

	
	public ChannelXmlBean loadUpdateFromFileSystem(final String path)
	{
		ChannelXmlBean result = null;
		try
		{
			FileWrapper f = _fileWrapperFactory.create(path);
			if (!f.isDirectory())
			{
				s_log.error("FileSystem path (" + path + ") is not a directory.");
			}
			else
			{
				f = _fileWrapperFactory.create(f, RELEASE_XML_FILENAME);
				result = _serializer.readChannelBean(f);
			}
		}
		catch (IOException e)
		{
			s_log.error("Unexpected exception while attempting " + "load updates from filesystem path (" + path
				+ "): " + e.getMessage(), e);
		}

		return result;
	}

	
	public boolean downloadLocalUpdateFile(String fileToGet, String destDir) throws FileNotFoundException,
		IOException
	{
		boolean result = false;
		FileWrapper fromFile = _fileWrapperFactory.create(fileToGet);
		if (fromFile.isFile() && fromFile.canRead())
		{
			String filename = fromFile.getName();
			FileWrapper toFile = _fileWrapperFactory.create(destDir, filename);
			copyFile(fromFile, toFile);
			result = true;
		}
		else
		{
			s_log.error("File " + fileToGet + " doesn't appear to be readable");
		}
		return result;
	}

	
	public void copyFile(final FileWrapper from, final FileWrapper to) throws FileNotFoundException,
		IOException
	{
		if (!from.exists())
		{
			s_log.error("Cannot copy from file (" + from.getAbsolutePath() + ") which doesn't appear to exist.");
			return;
		}
		FileWrapper toFile = to;
		
		if (to.isDirectory())
		{
			toFile = getFile(to, from.getName());
		}
		if (s_log.isDebugEnabled())
		{
			s_log.debug("Copying from file (" + from.getAbsolutePath() + ") to file ("
				+ toFile.getAbsolutePath() + ")");
		}
		if (toFile.exists())
		{
			long fromCheckSum = getCheckSum(from);
			long toCheckSum = getCheckSum(toFile);
			if (fromCheckSum == toCheckSum)
			{
				if (s_log.isInfoEnabled())
				{
					s_log.info("File to be copied(" + from.getAbsolutePath() + ") has the same checksum("
						+ fromCheckSum + ") as the file to copy to (" + toFile.getAbsolutePath()
						+ "). Skipping copy.");
				}
				return;
			}
		}
		_iou.copyFile(from, toFile);
	}

	
	@Override
	public void moveFiles(FileWrapper fromDir, String filePattern, boolean matchPattern, FileWrapper toDir)
		throws FileNotFoundException, IOException
	{
		if (StringUtilities.isEmpty(filePattern)) {
			throw new IllegalArgumentException("filePattern arg cannot be empty or null");
		}
		if (!fromDir.isDirectory()) { throw new IllegalArgumentException("Expected fromDir("
			+ fromDir.getAbsolutePath() + ") to be a directory."); }
		if (!toDir.isDirectory()) { throw new IllegalArgumentException("Expected toDir("
			+ toDir.getAbsolutePath() + ") to be a directory."); }

		List<FileWrapper> filesToMove = getFilterFileList(fromDir, filePattern, matchPattern);
		for (FileWrapper file : filesToMove) {
			copyFile(file, toDir);
			if (s_log.isDebugEnabled()) {
				s_log.debug("moveFiles: Attempting to delete file "+file.getAbsolutePath());
			}
			if (file.delete()) {
				s_log.error("moveFiles: Unable to delete file "+file.getAbsolutePath());
			}
			
		}
	}
	
	
	public void copyDir(FileWrapper fromDir, FileWrapper toDir) throws FileNotFoundException, IOException
	{
		verifyDirectory(fromDir, toDir);
		FileWrapper[] files = fromDir.listFiles();
		copyFiles(Arrays.asList(files), toDir);
	}

	private void verifyDirectory(FileWrapper fromDir, FileWrapper toDir) {
		if (!fromDir.isDirectory()) { throw new IllegalArgumentException("Expected fromDir("
			+ fromDir.getAbsolutePath() + ") to be a directory."); }
		if (!toDir.isDirectory()) { throw new IllegalArgumentException("Expected toDir("
			+ toDir.getAbsolutePath() + ") to be a directory."); }		
	}
	
	
	@Override
	public void copyDir(FileWrapper fromDir, String filePattern, boolean matchPattern, FileWrapper toDir)
		throws FileNotFoundException, IOException
	{
		if (StringUtilities.isEmpty(filePattern)) {
			throw new IllegalArgumentException("filePattern arg cannot be empty or null");
		}
		verifyDirectory(fromDir, toDir);
		List<FileWrapper> filesToCopy = getFilterFileList(fromDir, filePattern, matchPattern);		
		copyFiles(filesToCopy, toDir);
	}

	
	private List<FileWrapper> getFilterFileList(FileWrapper fromDir, String filePattern, boolean matchPattern) 
		throws FileNotFoundException, IOException
	{
		FileWrapper[] files = fromDir.listFiles();
		List<FileWrapper> filesToCopy = new ArrayList<FileWrapper>();
		
		
		for (FileWrapper sourceFile : files) {
			
			boolean fileNameMatchesPattern = sourceFile.getName().matches(filePattern);
			if (matchPattern && fileNameMatchesPattern) {
				filesToCopy.add(sourceFile);
			}
			if (!matchPattern && !fileNameMatchesPattern) {
				filesToCopy.add(sourceFile);
			}
		}		
		return filesToCopy;
	}
	
	
	private void copyFiles(List<FileWrapper> files, FileWrapper toDir) 
		throws FileNotFoundException, IOException 
	{		
		for (FileWrapper sourceFile : files)
		{
			copyFile(sourceFile, toDir);
		}		
	}
	
	
	public ChannelXmlBean getLocalReleaseInfo(String localReleaseFile)
	{
		ChannelXmlBean result = null;
		if (s_log.isDebugEnabled())
		{
			s_log.debug("Attempting to read local release file: " + localReleaseFile);
		}
		try
		{
			result = _serializer.readChannelBean(localReleaseFile);
		}
		catch (IOException e)
		{
			s_log.error("Unable to read local release file: " + e.getMessage(), e);
		}
		return result;
	}

	
	public FileWrapper getSquirrelHomeDir()
	{
		FileWrapper squirrelHomeDir = _appFileWrappers.getSquirrelHomeDir();
		if (!squirrelHomeDir.isDirectory())
		{
			s_log.error("SQuirreL Home Directory (" + squirrelHomeDir.getAbsolutePath()
				+ " doesn't appear to be a directory");
		}
		return squirrelHomeDir;
	}

	
	public FileWrapper getInstalledSquirrelMainJarLocation()
	{
		return _fileWrapperFactory.create(getSquirrelHomeDir(), SQUIRREL_SQL_JAR_FILENAME);
	}

	
	public FileWrapper getSquirrelPluginsDir()
	{
		FileWrapper squirrelHomeDir = _appFileWrappers.getPluginsDirectory();
		if (!squirrelHomeDir.isDirectory())
		{
			s_log.error("SQuirreL Plugins Directory (" + squirrelHomeDir.getAbsolutePath()
				+ " doesn't appear to be a directory");
		}
		return squirrelHomeDir;
	}

	
	public FileWrapper getSquirrelLibraryDir()
	{
		FileWrapper squirrelLibDir = _appFileWrappers.getLibraryDirectory();
		if (!squirrelLibDir.isDirectory())
		{
			s_log.error("SQuirreL Library Directory (" + squirrelLibDir.getAbsolutePath()
				+ " doesn't appear to be a directory");
		}
		return squirrelLibDir;
	}

	
	public FileWrapper getSquirrelUpdateDir()
	{
		return getDir(_appFileWrappers.getUpdateDirectory(), null, true);
	}

	
	public FileWrapper getDownloadsDir()
	{
		return getDir(getSquirrelUpdateDir(), DOWNLOADS_DIR_NAME, true);
	}

	
	public FileWrapper getCoreDownloadsDir()
	{
		return getDir(getDownloadsDir(), CORE_ARTIFACT_ID, true);
	}

	
	public FileWrapper getPluginDownloadsDir()
	{
		return getDir(getDownloadsDir(), PLUGIN_ARTIFACT_ID, true);
	}

	
	public FileWrapper getI18nDownloadsDir()
	{
		return getDir(getDownloadsDir(), TRANSLATION_ARTIFACT_ID, true);
	}

	
	public FileWrapper getBackupDir()
	{
		return getDir(getSquirrelUpdateDir(), BACKUP_ROOT_DIR_NAME, false);
	}

	
	public FileWrapper getCoreBackupDir()
	{
		return getDir(getBackupDir(), CORE_ARTIFACT_ID, false);
	}

	
	public FileWrapper getI18nBackupDir()
	{
		return getDir(getBackupDir(), TRANSLATION_ARTIFACT_ID, false);
	}

	
	public FileWrapper getPluginBackupDir()
	{
		return getDir(getBackupDir(), PLUGIN_ARTIFACT_ID, false);
	}

	
	public FileWrapper getChangeListFile()
	{
		FileWrapper updateDir = getSquirrelUpdateDir();
		FileWrapper changeListFile = _fileWrapperFactory.create(updateDir, CHANGE_LIST_FILENAME);
		return changeListFile;
	}

	
	public void saveChangeList(List<ArtifactStatus> changes) throws FileNotFoundException
	{
		ChangeListXmlBean changeBean = new ChangeListXmlBean();
		changeBean.setChanges(changes);
		_serializer.write(changeBean, getChangeListFile());
	}

	
	public ChangeListXmlBean getChangeList() throws FileNotFoundException
	{
		return _serializer.readChangeListBean(getChangeListFile());
	}

	
	public FileWrapper getLocalReleaseFile() throws FileNotFoundException
	{
		FileWrapper result = null;
		try
		{
			FileWrapper[] files = getSquirrelHomeDir().listFiles();
			for (FileWrapper file : files)
			{
				if (LOCAL_UPDATE_DIR_NAME.equals(file.getName()))
				{
					FileWrapper[] updateFiles = file.listFiles();
					for (FileWrapper updateFile : updateFiles)
					{
						if (RELEASE_XML_FILENAME.equals(updateFile.getName()))
						{
							result = updateFile;
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			s_log.error("getLocalReleaseFile: Exception encountered while " + "attempting to find "
				+ RELEASE_XML_FILENAME + " file");
		}
		if (result == null) { throw new FileNotFoundException("File " + RELEASE_XML_FILENAME
			+ " could not be found"); }
		return result;
	}

	
	public List<ArtifactStatus> getArtifactStatus(ChannelXmlBean channelXmlBean)
	{

		ReleaseXmlBean releaseXmlBean = channelXmlBean.getCurrentRelease();
		return getArtifactStatus(releaseXmlBean);
	}

	
	public List<ArtifactStatus> getArtifactStatus(ReleaseXmlBean releaseXmlBean)
	{
		Set<String> installedPlugins = getInstalledPlugins();
		Set<String> installedTranslations = getInstalledTranslations();
		ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();
		Set<ModuleXmlBean> currentModuleBeans = releaseXmlBean.getModules();
		for (ModuleXmlBean module : currentModuleBeans)
		{
			Set<ArtifactXmlBean> artifactBeans = module.getArtifacts();
			String moduleName = module.getName();
			for (ArtifactXmlBean artifact : artifactBeans)
			{
				ArtifactStatus status = new ArtifactStatus(artifact);
				status.setType(moduleName);
				if (status.isCoreArtifact())
				{
					status.setInstalled(true);
				}
				if (status.isPluginArtifact() && installedPlugins.contains(artifact.getName()))
				{
					status.setInstalled(true);
				}
				if (status.isTranslationArtifact() && installedTranslations.contains(artifact.getName()))
				{
					status.setInstalled(true);
				}
				result.add(status);
			}
		}
		return result;
	}

	
	public Set<String> getInstalledPlugins()
	{
		HashSet<String> result = new HashSet<String>();

		for (PluginInfo info : _pluginManager.getPluginInformation())
		{
			result.add(info.getInternalName() + ".zip");
		}
		return result;
	}

	
	public Set<String> getInstalledTranslations()
	{
		HashSet<String> result = new HashSet<String>();
		FileWrapper libDir = getSquirrelLibraryDir();
		for (String filename : libDir.list())
		{
			if (filename.startsWith("squirrel-sql_"))
			{
				result.add(filename);
			}
		}
		return result;
	}

	
	public IPluginManager getPluginManager()
	{
		return _pluginManager;
	}

	
	public void setPluginManager(IPluginManager manager)
	{
		_pluginManager = manager;
	}

	
	public FileWrapper checkDir(FileWrapper parent, String child)
	{
		FileWrapper dir = _fileWrapperFactory.create(parent, child);
		if (!dir.exists() && !dir.mkdir())
		{
			s_log.error("checkDir: Failed to mkdir - " + dir.getAbsolutePath());
		}
		return dir;
	}

	
	public void createZipFile(FileWrapper zipFile, FileWrapper... sourceFiles) throws FileNotFoundException,
		IOException
	{
		ZipOutputStream os = new ZipOutputStream(new FileOutputStream(zipFile.getAbsolutePath()));
		zipFileOs(os, sourceFiles);
		os.close();
	}

	
	public boolean deleteFile(FileWrapper path)
	{
		boolean result = true;
		if (path.exists())
		{
			if (path.isFile())
			{
				result = path.delete();
				if (s_log.isInfoEnabled())
				{
					if (result)
					{
						s_log.info("deleteFile: successfully deleted file = " + path.getAbsolutePath());
					}
					else
					{
						s_log.info("deleteFile: failed to delete file = " + path.getAbsolutePath());
					}
				}
			}
			else
			{
				FileWrapper[] files = path.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					result = result && deleteFile(files[i]);
				}
				result = result && path.delete();
			}
		}
		return result;
	}

	
	public void extractZipFile(FileWrapper zipFile, FileWrapper outputDirectory) throws IOException
	{
		if (!outputDirectory.isDirectory())
		{
			s_log.error("Output directory specified (" + outputDirectory.getAbsolutePath()
				+ ") doesn't appear to be a directory");
			return;
		}
		FileInputStream fis = null;
		ZipInputStream zis = null;
		FileOutputStream fos = null;
		try
		{
			fis = new FileInputStream(zipFile.getAbsolutePath());
			zis = new ZipInputStream(fis);
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null)
			{
				String name = zipEntry.getName();
				if (zipEntry.isDirectory())
				{
					checkDir(outputDirectory, name);
				}
				else
				{
					FileWrapper newFile = _fileWrapperFactory.create(outputDirectory, name);
					if (newFile.exists())
					{
						if (s_log.isInfoEnabled())
						{
							s_log.info("Deleting extraction file that already exists:" + newFile.getAbsolutePath());
						}
						newFile.delete();
					}
					fos = new FileOutputStream(newFile.getAbsolutePath());
					byte[] buffer = new byte[ZIP_EXTRACTION_BUFFER_SIZE];
					int n = 0;
					while ((n = zis.read(buffer, 0, ZIP_EXTRACTION_BUFFER_SIZE)) > -1)
					{
						fos.write(buffer, 0, n);
					}
					fos.close();
				}
				zipEntry = zis.getNextEntry();
			}
		}
		finally
		{
			_iou.closeOutputStream(fos);
			_iou.closeInputStream(fis);
			_iou.closeInputStream(zis);
		}
	}

	
	public ChangeListXmlBean getChangeList(FileWrapper changeListFile) throws FileNotFoundException
	{
		return _serializer.readChangeListBean(changeListFile);
	}

	
	public FileWrapper getFile(FileWrapper installDir, String artifactName)
	{
		return _fileWrapperFactory.create(installDir, artifactName);
	}

	
	public String downloadHttpUpdateFile(final String host, final int port, final String fileToGet,
		final String destDir, final long fileSize, final long checksum, final IProxySettings proxySettings)
		throws Exception
	{
		URL url = _iou.constructHttpUrl(host, port, fileToGet);
		String result = null;
		FileWrapper resultFile = _fileWrapperFactory.create(destDir, _pathUtils.getFileFromPath(fileToGet));
		result = resultFile.getAbsolutePath();

		if (s_log.isDebugEnabled())
		{
			s_log.debug("downloadHttpFile: writing http response body to file: " + resultFile);
		}

		int totalLength = _iou.downloadHttpFile(url, resultFile, proxySettings);

		verifySize(url, fileSize, totalLength);
		return result;
	}

	
	public FileWrapper getDownloadFileLocation(ArtifactStatus status)
	{
		FileWrapper result = null;
		if (CORE_ARTIFACT_ID.equals(status.getType()))
		{
			result = getFile(getCoreDownloadsDir(), status.getName());
		}
		if (PLUGIN_ARTIFACT_ID.equals(status.getType()))
		{
			result = getFile(getPluginDownloadsDir(), status.getName());
		}
		if (TRANSLATION_ARTIFACT_ID.equals(status.getType()))
		{
			result = getFile(getI18nDownloadsDir(), status.getName());
		}
		return result;
	}

	
	public boolean isPresentInDownloadsDirectory(ArtifactStatus status)
	{
		boolean result = false;

		FileWrapper downloadFile = getDownloadFileLocation(status);

		if (downloadFile.exists())
		{
			long checkSum = getCheckSum(downloadFile);
			if (status.getChecksum() == checkSum)
			{
				if (downloadFile.length() == status.getSize())
				{
					result = true;
				}
			}
		}
		return result;
	}

	

	
	private void verifySize(URL url, long expected, long actual) throws Exception
	{
		if (expected == -1)
		{
			if (s_log.isInfoEnabled())
			{
				s_log.info("verifySize: expected size was -1.  Skipping check for url: " + url.toString());
			}
			return;
		}
		if (expected != actual) { throw new Exception("Attempt to get file contents from url ("
			+ url.toString() + ") resulted in " + actual + " bytes downloaded, but " + expected
			+ " bytes were expected."); }
	}

	
	private void zipFileOs(ZipOutputStream os, FileWrapper[] sourceFiles) throws FileNotFoundException,
		IOException
	{
		for (FileWrapper file : sourceFiles)
		{
			if (file.isDirectory())
			{
				zipFileOs(os, file.listFiles());
			}
			else
			{
				FileInputStream fis = null;
				try
				{
					fis = new FileInputStream(file.getAbsolutePath());
					os.putNextEntry(new ZipEntry(file.getPath()));
					_iou.copyBytes(fis, os);
				}
				finally
				{
					_iou.closeInputStream(fis);
				}
			}
		}
	}

	
	private FileWrapper getDir(FileWrapper parent, String dirName, boolean create)
	{
		FileWrapper result = null;
		if (dirName != null)
		{
			result = _fileWrapperFactory.create(parent, dirName);
		}
		else
		{
			result = parent;
		}
		if (!result.isDirectory())
		{
			if (result.exists())
			{
				
				s_log.error(dirName + " directory (" + result.getAbsolutePath()
					+ ") doesn't appear to be a directory");
			}
			else
			{
				
				if (create)
				{
					result.mkdir();
				}
			}
		}
		return result;
	}

	
	private ChannelXmlBean downloadCurrentReleaseHttp(final String host, final int port, final String path,
		final String file, final IProxySettings proxySettings) throws Exception
	{

		ChannelXmlBean result = null;
		InputStream is = null;
		try
		{
			String fileToGet = _pathUtils.buildPath(true, path, file);

			
			
			String filename =
				downloadHttpUpdateFile(host, port, fileToGet, getDownloadsDir().getAbsolutePath(), -1, -1,
					proxySettings);
			FileWrapper releaseXmlFile = _fileWrapperFactory.create(filename);
			if (releaseXmlFile.exists())
			{
				result = _serializer.readChannelBean(releaseXmlFile);
			}
			else
			{
				throw new FileNotFoundException("Current release file couldn't be downloaded");
			}
		}
		finally
		{
			_iou.closeInputStream(is);
		}
		return result;
	}

	
	public UpdateCheckFrequency getUpdateCheckFrequency(IUpdateSettings settings)
	{
		UpdateCheckFrequency result = UpdateCheckFrequency.STARTUP;
		String updateCheckFrequencyStr = settings.getUpdateCheckFrequency();
		if ("weekly".equalsIgnoreCase(updateCheckFrequencyStr))
		{
			result = UpdateCheckFrequency.WEEKLY;
		}
		if ("daily".equalsIgnoreCase(updateCheckFrequencyStr))
		{
			result = UpdateCheckFrequency.DAILY;
		}
		if ("startup".equalsIgnoreCase(updateCheckFrequencyStr))
		{
			result = UpdateCheckFrequency.STARTUP;
		}
		return result;
	}

}
