
package net.sourceforge.squirrel_sql.client.update;

import static net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus.CORE_ARTIFACT_ID;
import static net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus.PLUGIN_ARTIFACT_ID;
import static net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus.TRANSLATION_ARTIFACT_ID;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;


public class ArtifactDownloader implements Runnable {

   public final static String DOWNLOADS_DIR_NAME = "downloads";
   
   private List<ArtifactStatus> _artifactStatus = null; 
   private volatile boolean _stopped = false;
   private boolean _isRemoteUpdateSite = true;
   private String _host = null;
   private String _path = null;
   private String _fileSystemUpdatePath = null;
   private UpdateUtil _util = null;
   private List<DownloadStatusListener> listeners = 
      new ArrayList<DownloadStatusListener>();
   Thread downloadThread  = null;
   String _updatesDir = null;
   
   
   public ArtifactDownloader(List<ArtifactStatus> artifactStatus) {
      _artifactStatus = artifactStatus;
      downloadThread  = new Thread(this, "ArtifactDownloadThread");
   }
   
   public void start() {
      downloadThread.start();
   }
      
   
   public void run() {
      sendDownloadStarted(_artifactStatus.size());
      
      File downloadsDir = _util.checkDir(_util.getSquirrelUpdateDir(),
                                   DOWNLOADS_DIR_NAME);
      File coreDownloadDir = _util.checkDir(downloadsDir, CORE_ARTIFACT_ID);
      File pluginDownloadDir = _util.checkDir(downloadsDir, PLUGIN_ARTIFACT_ID);
      File i18nDownloadDir = 
         _util.checkDir(downloadsDir, TRANSLATION_ARTIFACT_ID);
            
      for (ArtifactStatus status : _artifactStatus) {
         if (_stopped) {
            sendDownloadStopped();
            break;
         }  else {
            sendDownloadFileStarted(status.getName());
         }
         String fileToGet = status.getType() + "/" + status.getName();

         String destDir = coreDownloadDir.getAbsolutePath();
         if (PLUGIN_ARTIFACT_ID.equals(status.getType())) {
            destDir = pluginDownloadDir.getAbsolutePath();
         }
         if (TRANSLATION_ARTIFACT_ID.equals(status.getType())) {
            destDir = i18nDownloadDir.getAbsolutePath();
         }
         
         
         
         boolean result = true;
         if (_isRemoteUpdateSite) {
            result = _util.downloadHttpFile(_host, _path, fileToGet, destDir);
         } else {
            fileToGet = this._fileSystemUpdatePath +"/" + fileToGet;
            result = _util.downloadLocalFile(fileToGet, destDir);
         }
         if (result == false) {
            sendDownloadFailed();
            return;
         } else {
            sendDownloadFileCompleted(status.getName());
         }
      }
      
      sendDownloadComplete();
   }
   
   
   public void stopDownload() {
      _stopped = true;
   }

   
   public List<ArtifactStatus> getArtifactStatus() {
      return _artifactStatus;
   }

   
   public void setArtifactStatus(List<ArtifactStatus> status) {
      _artifactStatus = status;
   }

   
   public boolean isRemoteUpdateSite() {
      return _isRemoteUpdateSite;
   }

   
   public void setIsRemoteUpdateSite(boolean remoteUpdateSite) {
      _isRemoteUpdateSite = remoteUpdateSite;
   }

   
   public String getHost() {
      return _host;
   }

   
   public void setHost(String host) {
      this._host = host;
   }

   
   public String getPath() {
      return _path;
   }

   
   public void setPath(String path) {
      this._path = path;
   }

   
   public UpdateUtil getUtil() {
      return _util;
   }

   
   public void setUtil(UpdateUtil util) {
      this._util = util;
   }
   
   
   public void addDownloadStatusListener(DownloadStatusListener listener) {
      listeners.add(listener);
   }
   
   
   public void removeDownloadListener(DownloadStatusListener listener) {
      listeners.remove(listener);
   }
   
   private void sendEvent(DownloadStatusEvent evt) {
      for (DownloadStatusListener listener : listeners) {
         listener.handleDownloadStatusEvent(evt);
      }
   }
   
   private void sendDownloadStarted(int totalFileCount) {
      DownloadStatusEvent evt = 
         new DownloadStatusEvent(DownloadEventType.DOWNLOAD_STARTED);
      evt.setFileCountTotal(totalFileCount);
      sendEvent(evt);      
   }

   private void sendDownloadStopped() {
      DownloadStatusEvent evt = 
         new DownloadStatusEvent(DownloadEventType.DOWNLOAD_STOPPED);
      sendEvent(evt);      
   }
   
   private void sendDownloadComplete() {
      DownloadStatusEvent evt = 
         new DownloadStatusEvent(DownloadEventType.DOWNLOAD_COMPLETED);
      sendEvent(evt);            
   }
   
   private void sendDownloadFailed() {
      DownloadStatusEvent evt = 
         new DownloadStatusEvent(DownloadEventType.DOWNLOAD_FAILED);
      sendEvent(evt);                  
   }

   private void sendDownloadFileStarted(String filename) {
      DownloadStatusEvent evt = 
         new DownloadStatusEvent(DownloadEventType.DOWNLOAD_FILE_STARTED);
      evt.setFilename(filename);
      sendEvent(evt);                  
   }

   private void sendDownloadFileCompleted(String filename) {
      DownloadStatusEvent evt = 
         new DownloadStatusEvent(DownloadEventType.DOWNLOAD_FILE_COMPLETED);
      evt.setFilename(filename);
      sendEvent(evt);                  
   }
   
   
   public String getFileSystemUpdatePath() {
      return _fileSystemUpdatePath;
   }

   
   public void setFileSystemUpdatePath(String systemUpdatePath) {
      _fileSystemUpdatePath = systemUpdatePath;
   }
}