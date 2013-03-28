
package net.sourceforge.squirrel_sql.client.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import net.sourceforge.squirrel_sql.client.plugin.IPluginManager;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.util.PathUtils;
import net.sourceforge.squirrel_sql.client.update.util.PathUtilsImpl;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ArtifactXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ModuleXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.fw.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;


public class UpdateUtilImpl implements UpdateUtil {

   
   private final static ILogger s_log = 
      LoggerController.createLogger(UpdateUtilImpl.class);
   
   
   private IPluginManager _pluginManager = null;
   
   
   private final UpdateXmlSerializer _serializer = new UpdateXmlSerializer();

   
   private PathUtils _pathUtils = new PathUtilsImpl();
   
   
   public ChannelXmlBean downloadCurrentRelease(final String host,
         final int port, final String path, final String fileToGet)
            throws Exception
   {
      ChannelXmlBean result = null;
      if (s_log.isDebugEnabled()) {
      	s_log.debug("downloadCurrentRelease: host=" + host + " port=" + port + " path=" + path
				+ " fileToGet=" + fileToGet);
      }
      result = downloadCurrentReleaseHttp(host, port, path, fileToGet);      
      return result;
   }
   
   
   public ChannelXmlBean loadUpdateFromFileSystem(final String path) 
   {
      ChannelXmlBean result = null;
      BufferedInputStream is = null;
      try {
         File f = new File(path);
         if (!f.isDirectory()) {
            s_log.error("FileSystem path ("+path+") is not a directory.");
         } else {
            f = new File(f, RELEASE_XML_FILENAME); 
            is = new BufferedInputStream(new FileInputStream(f));
            result = _serializer.readChannelBean(is);
         }            
      } catch (IOException e) {
         s_log.error("Unexpected exception while attempting "
               + "load updates from filesystem path (" + path + "): "
               + e.getMessage(), e);
      } finally {
         IOUtilities.closeInputStream(is);
      }
     
      return result;
   }
   
   
   public boolean downloadLocalFile(String fileToGet, String destDir) throws FileNotFoundException, IOException {
      boolean result = false;
      File fromFile = new File(fileToGet);
      if (fromFile.isFile() && fromFile.canRead()) {
         String filename = fromFile.getName();
         File toFile = new File(destDir, filename);
         copyFile(fromFile, toFile);
         result = true;
      } else {
         s_log.error("File "+fileToGet+" doesn't appear to be readable");
      }
      return result;
   }
   
   
   public void copyFile(final File from, final File to) throws FileNotFoundException, IOException {
      if (!from.exists()) {
      	s_log.error("Cannot copy from file ("+from.getAbsolutePath()+") which doesn't appear to exist.");
      	return;
      }
      File destination = to;
      if (to.isDirectory()) {
      	destination = getFile(to, from.getName());
      }
   	if (s_log.isDebugEnabled()) {
         s_log.debug("Copying file "+from+" to file " + destination);
      }
      FileInputStream in = null;
      FileOutputStream out = null;
      try {
         in = new FileInputStream(from);
         out = new FileOutputStream(destination);
         byte[] buffer = new byte[8192];
         int len;
         while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);         
         }
      } finally {
         IOUtilities.closeInputStream(in);
         IOUtilities.closeOutputStream(out);
      }
   }
   
   
   public ChannelXmlBean getLocalReleaseInfo(String localReleaseFile) {
      ChannelXmlBean result = null;
      if (s_log.isDebugEnabled()) {
         s_log.debug("Attempting to read local release file: "
               + localReleaseFile);
      }
      try {
         result = _serializer.readChannelBean(localReleaseFile);
      } catch (IOException e) {
         s_log.error("Unable to read local release file: " + e.getMessage(), e);
      }
      return result;
   }

   
   public File getSquirrelHomeDir()  {
      ApplicationFiles appFiles = new ApplicationFiles();
      File squirrelHomeDir = appFiles.getSquirrelHomeDir();      
      if (!squirrelHomeDir.isDirectory()) {
         s_log.error("SQuirreL Home Directory ("
               + squirrelHomeDir.getAbsolutePath()
               + " doesn't appear to be a directory");
      }
      return squirrelHomeDir;
   }

   
   public File getSquirrelPluginsDir()  {
      ApplicationFiles appFiles = new ApplicationFiles();
      File squirrelHomeDir = appFiles.getPluginsDirectory();      
      if (!squirrelHomeDir.isDirectory()) {
         s_log.error("SQuirreL Plugins Directory ("
               + squirrelHomeDir.getAbsolutePath()
               + " doesn't appear to be a directory");
      }
      return squirrelHomeDir;
   }   

   
   public File getSquirrelLibraryDir()  {
      ApplicationFiles appFiles = new ApplicationFiles();
      File squirrelLibDir = appFiles.getLibraryDirectory();      
      if (!squirrelLibDir.isDirectory()) {
         s_log.error("SQuirreL Library Directory ("
               + squirrelLibDir.getAbsolutePath()
               + " doesn't appear to be a directory");
      }
      return squirrelLibDir;
   }   

   
   public File getSquirrelUpdateDir()  {
      ApplicationFiles appFiles = new ApplicationFiles();
      return getDir(appFiles.getUpdateDirectory(), null, true);       
   }   

   
   public File getDownloadsDir() {
   	return getDir(getSquirrelUpdateDir(), DOWNLOADS_DIR_NAME, true);
   }
   
   
   public File getCoreDownloadsDir() {
   	return getDir(getDownloadsDir(), CORE_ARTIFACT_ID, true);
   }

   
   public File getPluginDownloadsDir() {
   	return getDir(getDownloadsDir(), PLUGIN_ARTIFACT_ID, true);
   }

   
   public File getI18nDownloadsDir() {
   	return getDir(getDownloadsDir(), TRANSLATION_ARTIFACT_ID, true);
   }

      
   
   public File getChangeListFile() {
       File updateDir = getSquirrelUpdateDir();
       File changeListFile = new File(updateDir, CHANGE_LIST_FILENAME);
       return changeListFile; 
   }
   
   
   public void saveChangeList(List<ArtifactStatus> changes)
         throws FileNotFoundException {
      ChangeListXmlBean changeBean = new ChangeListXmlBean();
      changeBean.setChanges(changes);
      _serializer.write(changeBean, getChangeListFile());
   }
   
   
   public ChangeListXmlBean getChangeList() throws FileNotFoundException {
       return _serializer.readChangeListBean(getChangeListFile());
   }
   
   
   public String getLocalReleaseFile() throws FileNotFoundException {
      String result = null;
      try {
         File[] files = getSquirrelHomeDir().listFiles();
         for (File file : files) {
            if (LOCAL_UPDATE_DIR_NAME.equals(file.getName())) {
               File[] updateFiles = file.listFiles();
               for (File updateFile : updateFiles) {
                  if (RELEASE_XML_FILENAME.equals(updateFile.getName())) {
                     result = updateFile.getAbsolutePath();
                  }
               }
            }
         }
      } catch (Exception e) {
         s_log.error("getLocalReleaseFile: Exception encountered while "
               + "attempting to find "+RELEASE_XML_FILENAME+" file");
      }
      if (result == null) {
         throw new FileNotFoundException("File " + RELEASE_XML_FILENAME
               + " could not be found");
      }
      return result;
   }
   
   
   public List<ArtifactStatus> getArtifactStatus(ChannelXmlBean channelXmlBean) {
      
      ReleaseXmlBean releaseXmlBean = channelXmlBean.getCurrentRelease();
      return getArtifactStatus(releaseXmlBean);
   }
   
   
   public List<ArtifactStatus> getArtifactStatus(ReleaseXmlBean releaseXmlBean) {
      Set<String> installedPlugins = getInstalledPlugins();
      Set<String> installedTranslations = getInstalledTranslations();
      ArrayList<ArtifactStatus> result = new ArrayList<ArtifactStatus>();
      Set<ModuleXmlBean> currentModuleBeans = releaseXmlBean.getModules();
      for (ModuleXmlBean module : currentModuleBeans) {
         Set<ArtifactXmlBean> artifactBeans = module.getArtifacts();
         String moduleName = module.getName();
         for (ArtifactXmlBean artifact: artifactBeans) {
            String name = artifact.getName();
            String type = moduleName;
            boolean installed = artifact.isInstalled();
            ArtifactStatus status = new ArtifactStatus();
            status.setName(name);
            status.setType(type);
            status.setInstalled(installed);
            if (status.isCoreArtifact()) {
               status.setInstalled(true);
            }
            if (status.isPluginArtifact() && installedPlugins.contains(name)) {
               status.setInstalled(true);
            }
            if (status.isTranslationArtifact() 
                  && installedTranslations.contains(name)) 
            {
               status.setInstalled(true);  
            }
            result.add(status);
         }
      }      
      return result;
   }
   
   
   public Set<String> getInstalledPlugins() {
      HashSet<String> result = new HashSet<String>();
      
      for (PluginInfo info : _pluginManager.getPluginInformation()) {
         result.add(info.getInternalName() + ".zip");
      }
      return result;
   }

   
   public Set<String> getInstalledTranslations() {
      HashSet<String> result = new HashSet<String>();
      File libDir = getSquirrelLibraryDir();
      for (String filename : libDir.list()) {
         if (filename.startsWith("squirrel-sql_")) {
            result.add(filename);
         }
      }
      return result;      
   }
   
   
   public IPluginManager getPluginManager() {
      return _pluginManager;
   }

   
   public void setPluginManager(IPluginManager manager) {
      _pluginManager = manager;
   }
   
   
   public File checkDir(File parent, String child) {
      File dir = new File(parent, child);
      if (!dir.exists()) {
         dir.mkdir();
      }
      return dir;
   }
   
   
   public void createZipFile(File zipFile, File[] sourceFiles) 
      throws FileNotFoundException, IOException  
   {
      ZipOutputStream os = 
         new ZipOutputStream(new FileOutputStream(zipFile));
      zipFileOs(os, sourceFiles);
      os.close();
   }
   
	
	public boolean deleteFile(File path)
	{
		if (path.exists())
		{
			if (path.isDirectory())
			{
				File[] files = path.listFiles();
				for (int i = 0; i < files.length; i++)
				{
					if (files[i].isDirectory())
					{
						deleteFile(files[i]);
					}
					else
					{
						if (s_log.isInfoEnabled()) {
							s_log.info("deleteFile: deleting file = "+files[i].getAbsolutePath());
						}
						files[i].delete();
					}
				}
			}
		}
		return (path.delete());
	}   
      
   
   public void extractZipFile(File zipFile, File outputDirectory) throws IOException {
   	if (!outputDirectory.isDirectory()) {
   		s_log.error("Output directory specified (" + outputDirectory.getAbsolutePath()
				+ ") doesn't appear to be a directory");
   		return;
   	}
   	FileInputStream fis = null;
   	ZipInputStream zis = null;
   	FileOutputStream fos = null; 
   	try {
   		fis = new FileInputStream(zipFile);
	   	zis = new ZipInputStream(fis);
	   	ZipEntry zipEntry = zis.getNextEntry(); 
	   	while (zipEntry != null) {
	   		String name = zipEntry.getName();
	   		if (zipEntry.isDirectory()) {
	   			checkDir(outputDirectory, name);
	   		} else {
	   			File newFile = new File(outputDirectory, name);
	   			if (newFile.exists()) {
	   				if (s_log.isInfoEnabled()) {
	   					s_log.info("Deleting extraction file that already exists:"+newFile.getAbsolutePath());
	   				}
	   				newFile.delete();
	   			}
	   			fos = new FileOutputStream(newFile); 
	   			byte[] buffer = new byte[8192];
	   			while (zis.available() != 0) {
	   				zis.read(buffer);
	   				fos.write(buffer);
	   			}
	   		   fos.close();
	   		}
	   		zipEntry = zis.getNextEntry();
	   	}
   	} finally {
   		IOUtilities.closeOutputStream(fos);
   		IOUtilities.closeInputStream(fis);
   		IOUtilities.closeInputStream(zis);
   	}
   }
   
   
   public ChangeListXmlBean getChangeList(File changeListFile) throws FileNotFoundException {
      return _serializer.readChangeListBean(changeListFile);
   }

   
   public boolean fileExists(File file) {
      return file.exists();
   }

   
   public File getFile(File installDir, String artifactName) {
      return new File(installDir, artifactName);
   }

   
   private void zipFileOs(ZipOutputStream os, File[] sourceFiles)
         throws FileNotFoundException, IOException {
      for (File file : sourceFiles) {
         if (file.isDirectory()) {
            zipFileOs(os, file.listFiles());
         } else {
            FileInputStream fis = null;
            try {
               fis = new FileInputStream(file);
               os.putNextEntry(new ZipEntry(file.getPath()));
               IOUtilities.copyBytes(fis, os);
            } finally {
               IOUtilities.closeInputStream(fis);
            }
         }
      }
   }
   
   
   private File getDir(File parent, String dirName, boolean create) {
		File result = null;
		if (dirName != null) {
			result = new File(parent, dirName);
		} else {
			result = parent;
		}
		if (!result.isDirectory()) {
	   	if (result.exists()) {
	         
				s_log.error(dirName + " directory (" + result.getAbsolutePath()
					+ ") doesn't appear to be a directory");	   		
	   	} else {
	   	   
	   		if (create) {
	   			result.mkdir();
	   		}
	   	}
		}
		return result;
   }
   
   
   private ChannelXmlBean downloadCurrentReleaseHttp(final String host,
      final int port, final String path, final String file) throws Exception {
   	
   	ChannelXmlBean result = null;
   	InputStream is = null;
   	try {
   		String fileToGet = _pathUtils.buildPath(true, path, file); 
   		String filename = downloadHttpFile(host, port, fileToGet, getDownloadsDir().getAbsolutePath());
   		File releaseXmlFile = new File(filename);
   		if (releaseXmlFile.exists()) {
   			is = new BufferedInputStream(new FileInputStream(filename));
   			result = _serializer.readChannelBean(is);
   		} else {
   			throw new FileNotFoundException("Current release file couldn't be downloaded");
   		}
   	} finally {
   		IOUtilities.closeInputStream(is);
   	}
   	return result;
   }
   
   
   
   public String downloadHttpFile(final String host,
         final int port, final String fileToGet, String destDir)
         throws Exception {
      BufferedInputStream is = null;
      BufferedOutputStream os = null;
      URL url = null;
      HttpMethod method = null;
      int resultCode = -1;
      String result = null;
      try {
         String server = host;
         if (server.startsWith(HTTP_PROTOCOL_PREFIX)) {
            int beginIdx = server.indexOf("://") + 3;
            server = server.substring(beginIdx, host.length());
         }
         
         if (port == 80) {
         	url = new URL(HTTP_PROTOCOL_PREFIX, server, fileToGet);
         } else {
         	url = new URL(HTTP_PROTOCOL_PREFIX, server, port, fileToGet);
         }
                  
         HttpClient client = new HttpClient();
         method = new GetMethod(url.toString());
         method.setFollowRedirects(true);
         
         resultCode = client.executeMethod(method);
         if (resultCode != 200) {
         	throw new FileNotFoundException("Failed to download file from url ("+url+
         		"): HTTP Response Code="+resultCode);
         }
         InputStream mis = method.getResponseBodyAsStream();
         
         if (s_log.isDebugEnabled()) {
         	s_log.debug("response code was: "+resultCode);         	
         }
         is = new BufferedInputStream(mis);
			File resultFile = new File(destDir, _pathUtils.getFileFromPath(fileToGet));
			result = resultFile.getAbsolutePath();
			if (!resultFile.exists()) {
				resultFile.createNewFile();
			}
			os = new BufferedOutputStream(new FileOutputStream(resultFile));
			byte[] buffer = new byte[8192];
			int length = 0;
			
			if (s_log.isDebugEnabled()) {
				s_log.debug("downloadHttpFile: writing http response body to file: "+resultFile);
			}
			
			while ((length = is.read(buffer)) != -1)
			{
				os.write(buffer, 0, length);
			}
         
      } catch (Exception e) {
         s_log.error("downloadCurrentRelease: Unexpected exception while "
               + "attempting to open an HTTP connection to host (" + host
               + ") on port ("+port+") to download a file (" + fileToGet + "): "+
               e.getMessage(), e);
         s_log.error("response code was: "+resultCode);
         throw e;
      } finally {
         IOUtilities.closeInputStream(is);
         IOUtilities.closeOutputStream(os);
         method.releaseConnection();
      }
      return result;
   }
   
   public void setPathUtils(PathUtils pathUtils) {
   	this._pathUtils = pathUtils;
   }
      
}
