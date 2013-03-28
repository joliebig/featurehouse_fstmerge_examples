
package net.sourceforge.squirrel_sql.client.update;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ReleaseXmlBean;

public interface UpdateUtil {

   
   public static final String HTTP_PROTOCOL_PREFIX = "http";

   
   public static final String LOCAL_UPDATE_DIR_NAME = "update";

   
   public static final String RELEASE_XML_FILENAME = "release.xml";

   
   public static final String CHANGE_LIST_FILENAME = "changeList.xml";

   
   ChannelXmlBean downloadCurrentRelease(final String host, final int port,
         final String path, final String fileToGet) throws Exception;

   
   ChannelXmlBean loadUpdateFromFileSystem(final String path); 
   
   
   
   boolean downloadHttpFile(String host, String path, String fileToGet,
         String destDir);

   
   boolean downloadLocalFile(String fileToGet, String destDir);   
   
   
   boolean copyFile(final File from, final File to);
   
   
   ChannelXmlBean getLocalReleaseInfo(String localReleaseFile);

   
   File getSquirrelHomeDir();

   File getSquirrelPluginsDir();

   File getSquirrelLibraryDir();

   File getChangeListFile();
   
   File checkDir(File parent, String child);
   
   void createZipFile(File zipFile, File[] sourceFiles)
      throws FileNotFoundException, IOException;
   
   
   File getSquirrelUpdateDir();

   
   void saveChangeList(List<ArtifactStatus> changes)
         throws FileNotFoundException;

   
   ChangeListXmlBean getChangeList() throws FileNotFoundException;

   
   String getLocalReleaseFile() throws FileNotFoundException;

   List<ArtifactStatus> getArtifactStatus(ChannelXmlBean channelXmlBean);

   List<ArtifactStatus> getArtifactStatus(ReleaseXmlBean releaseXmlBean);

   
   Set<String> getInstalledPlugins();

   
   Set<String> getInstalledTranslations();

   
   PluginManager getPluginManager();

   
   void setPluginManager(PluginManager manager);

}