
package net.sourceforge.squirrel_sql.client.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ArtifactXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ModuleXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.UpdateXmlSerializer;
import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;
import net.sourceforge.squirrel_sql.client.util.IOUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class UpdateUtilImpl implements UpdateUtil {

   
   private final static ILogger s_log = 
      LoggerController.createLogger(UpdateUtilImpl.class);
   
   
   private PluginManager _pluginManager = null;
   
   
   private final UpdateXmlSerializer serializer = new UpdateXmlSerializer();

   
   public ChannelXmlBean downloadCurrentRelease(final String host,
         final int port, final String path, final String fileToGet)
            throws Exception
   {
      ChannelXmlBean result = null;
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
            result = serializer.readChannelBean(is);
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
   
   
   private ChannelXmlBean downloadCurrentReleaseHttp(final String host,
         final int port, final String path, final String fileToGet)
         throws Exception {
      ChannelXmlBean result = null;
      BufferedInputStream is = null;
      String pathToFile = path + fileToGet;
      
      try {
         String server = host;
         if (server.startsWith(HTTP_PROTOCOL_PREFIX)) {
            int beginIdx = server.indexOf("://") + 3;
            server = server.substring(beginIdx, host.length());
         }
         URL url = new URL(HTTP_PROTOCOL_PREFIX, server, port, pathToFile);
         is = new BufferedInputStream(url.openStream());
         result = serializer.readChannelBean(is);
      } catch (Exception e) {
         s_log.error("downloadCurrentRelease: Unexpected exception while "
               + "attempting to open an HTTP connection to host (" + host
               + ") " + "to download a file (" + pathToFile + "): "+
               e.getMessage(), e);
         throw e;
      } finally {
         IOUtilities.closeInputStream(is);
      }
      return result;
   }
   
   
   public boolean downloadHttpFile(String host, String path, String fileToGet,
         String destDir) {
      boolean result = false;
      BufferedInputStream is = null;
      BufferedOutputStream os = null;
      try {
         URL url = new URL(HTTP_PROTOCOL_PREFIX, host, path + fileToGet);
         is = new BufferedInputStream(url.openStream());
         File localFile = new File(destDir, fileToGet);
         os = new BufferedOutputStream(new FileOutputStream(localFile));
         byte[] buffer = new byte[8192];
         int length = 0;
         while ((length = is.read(buffer)) != -1) {
            os.write(buffer, 0, length);
         }
         result = true;
      } catch (Exception e) {
         s_log.error("Exception encountered while attempting to "
               + "download file " + fileToGet + " from host " + host
               + " and path " + path + " to destDir " + destDir + ": "
               + e.getMessage(), e);
      } finally {
         IOUtilities.closeInputStream(is);
         IOUtilities.closeOutputStream(os);
      }
      return result;
   }

   
   public boolean downloadLocalFile(String fileToGet, String destDir) {
      boolean result = false;
      File fromFile = new File(fileToGet);
      if (fromFile.isFile() && fromFile.canRead()) {
         String filename = fromFile.getName();
         File toFile = new File(destDir, filename);
         result = copyFile(fromFile, toFile);
      } else {
         s_log.error("File "+fileToGet+" doesn't appear to be readable");
      }
      return result;
   }
   
   
   public boolean copyFile(final File from, final File to) {
      boolean result = true;
      if (s_log.isDebugEnabled()) {
         s_log.debug("Copying file "+from+" to file " + to);
      }
      FileInputStream in = null;
      FileOutputStream out = null;
      try {
         in = new FileInputStream(from);
         out = new FileOutputStream(to);
         byte[] buffer = new byte[8192];
         int len;
         while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);         
         }
      } catch (Exception e) {
         s_log.error("copyFile: Unexpected error while trying to "
               + "copy file " + from + " to file " + to, e);
         result = false;
      } finally {
         IOUtilities.closeInputStream(in);
         IOUtilities.closeOutputStream(out);
      }
      return result;
   }
   
   
   public ChannelXmlBean getLocalReleaseInfo(String localReleaseFile) {
      ChannelXmlBean result = null;
      if (s_log.isDebugEnabled()) {
         s_log.debug("Attempting to read local release file: "
               + localReleaseFile);
      }
      try {
         result = serializer.readChannelBean(localReleaseFile);
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
      File squirrelUpdateDir = appFiles.getUpdateDirectory();      
      if (!squirrelUpdateDir.isDirectory()) {
         if (squirrelUpdateDir.exists()) {
            
            s_log.error("SQuirreL Update Directory ("
                  + squirrelUpdateDir.getAbsolutePath()
                  + " doesn't appear to be a directory");
         } else {
            
            squirrelUpdateDir.mkdir();
         }
      }
      return squirrelUpdateDir;
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
      serializer.write(changeBean, getChangeListFile());
   }
   
   
   public ChangeListXmlBean getChangeList() throws FileNotFoundException {
       return serializer.readChangeListBean(getChangeListFile());
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
         if (filename.startsWith(" squirrel-sql_")) {
            result.add(filename);
         }
      }
      return result;      
   }
   
   
   public PluginManager getPluginManager() {
      return _pluginManager;
   }

   
   public void setPluginManager(PluginManager manager) {
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
   
   private void zipFileOs(ZipOutputStream os, File[] sourceFiles) 
      throws FileNotFoundException, IOException
   {
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

}
