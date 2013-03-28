
package net.sourceforge.squirrel_sql.client.update;

import static net.sourceforge.squirrel_sql.client.update.UpdateUtil.RELEASE_XML_FILENAME;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.mainframe.MainFrame;
import net.sourceforge.squirrel_sql.client.plugin.PluginInfo;
import net.sourceforge.squirrel_sql.client.plugin.PluginManager;
import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesActionListener;
import net.sourceforge.squirrel_sql.client.preferences.GlobalPreferencesSheet;
import net.sourceforge.squirrel_sql.client.preferences.UpdatePreferencesPanel;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactAction;
import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;
import net.sourceforge.squirrel_sql.client.update.gui.CheckUpdateListener;
import net.sourceforge.squirrel_sql.client.update.gui.UpdateManagerDialog;
import net.sourceforge.squirrel_sql.client.update.gui.UpdateSummaryDialog;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChannelXmlBean;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.UpdateSettings;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;


public class UpdateControllerImpl implements UpdateController,
      CheckUpdateListener {

   
   private static final ILogger s_log =
      LoggerController.createLogger(UpdateControllerImpl.class);
   
   
   private IApplication _app = null;

   
   private UpdateUtil _util = null;

   
   private ChannelXmlBean _currentChannelBean = null;

   
   private ChannelXmlBean _installedChannelBean = null;
   
   
   private long _timeOfLastCheck = -1;

   
   private static GlobalPrefsListener listener = null;
   
   
   
   public UpdateControllerImpl(IApplication app) {
      _app = app;
      if (listener == null) {
         listener = new GlobalPrefsListener();
         GlobalPreferencesSheet.addGlobalPreferencesActionListener(listener);
      }
   }

   
   public void setUpdateUtil(UpdateUtil util) {
      this._util = util;
      _util.setPluginManager(_app.getPluginManager());
   }
   
   
   public void showUpdateDialog() {
      JFrame parent = _app.getMainFrame();
      UpdateSettings settings = getUpdateSettings();
      boolean isRemoteUpdateSite = settings.isRemoteUpdateSite();
      UpdateManagerDialog dialog = 
         new UpdateManagerDialog(parent, isRemoteUpdateSite);
      if (isRemoteUpdateSite) {
         dialog.setUpdateServerName(settings.getUpdateServer());
         dialog.setUpdateServerPort(settings.getUpdateServerPort());
         dialog.setUpdateServerPath(settings.getUpdateServerPath());
         dialog.setUpdateServerChannel(settings.getUpdateServerChannel());
      } else {
         dialog.setLocalUpdatePath(settings.getFileSystemUpdatePath());
      }
      dialog.addCheckUpdateListener(this);
      dialog.setVisible(true);
   }

   
   public boolean isUpToDate() throws Exception {
      boolean result = true;
      UpdateSettings settings = getUpdateSettings();
      
      
      String releaseFilename = _util.getLocalReleaseFile();

      
      _installedChannelBean = _util.getLocalReleaseInfo(releaseFilename);

      
      String channelName = _installedChannelBean.getName();

      StringBuilder releasePath = new StringBuilder("/");
      releasePath.append(getUpdateServerPath());
      releasePath.append("/");
      releasePath.append(channelName);
      releasePath.append("/");

      
      
      if (settings.isRemoteUpdateSite()) {

         _currentChannelBean = _util.downloadCurrentRelease(getUpdateServerName(),
                                                            getUpdateServerPortAsInt(),
                                                            releasePath.toString(),
                                                            RELEASE_XML_FILENAME);
      } else {
         _currentChannelBean = 
            _util.loadUpdateFromFileSystem(settings.getFileSystemUpdatePath());
      }

      _timeOfLastCheck = System.currentTimeMillis();

      
      
      return _currentChannelBean.equals(_installedChannelBean);
   }

   
   public Set<String> getInstalledPlugins() {
      Set<String> result = new HashSet<String>();
      PluginManager pmgr = _app.getPluginManager();
      PluginInfo[] infos = pmgr.getPluginInformation();
      for (PluginInfo info : infos) {
         result.add(info.getInternalName());
      }
      return result;
   }

   
   public void pullDownUpdateFiles(List<ArtifactStatus> artifactStatusList,
         DownloadStatusListener listener) {

      List<ArtifactStatus> newartifactsList = 
         new ArrayList<ArtifactStatus>();
      
      for (ArtifactStatus status : artifactStatusList) {
         if (status.getArtifactAction() == ArtifactAction.INSTALL) {
            newartifactsList.add(status);
         }
      }
      
      ArtifactDownloader downloader = new ArtifactDownloader(newartifactsList);
      downloader.setUtil(_util);
      downloader.setIsRemoteUpdateSite(isRemoteUpdateSite());
      downloader.setHost(getUpdateServerName());
      downloader.setPath(getUpdateServerPath());
      downloader.setFileSystemUpdatePath(getUpdateSettings().getFileSystemUpdatePath());
      downloader.addDownloadStatusListener(listener);
      downloader.start();
   }

   
   public String getUpdateServerChannel() {
      return getUpdateSettings().getUpdateServerChannel();
   }

   
   public String getUpdateServerName() {
      return getUpdateSettings().getUpdateServer();
   }

   
   public boolean isRemoteUpdateSite() {
      return getUpdateSettings().isRemoteUpdateSite();
   }
   
   
   public String getUpdateServerPath() {
      return getUpdateSettings().getUpdateServerPath();
   }
   
   
   public String getUpdateServerPort() {
      return getUpdateSettings().getUpdateServerPort();
   }

   
   public int getUpdateServerPortAsInt() {
      return Integer.parseInt(getUpdateServerPort());
   }

   
   public void showMessage(String title, String msg) {
      JOptionPane.showMessageDialog(_app.getMainFrame(),
                                    msg,
                                    title,
                                    JOptionPane.INFORMATION_MESSAGE);

   }

   
   public void showErrorMessage(String title, String msg, Exception e) {
      s_log.error(msg, e);
      JOptionPane.showMessageDialog(_app.getMainFrame(),
                                    msg,
                                    title,
                                    JOptionPane.ERROR_MESSAGE);
      
   }

   
   public void showErrorMessage(String title, String msg) {
      showErrorMessage(title, msg, null);
   }
   
   
   public void checkUpToDate() {
      
      try {
         if (isUpToDate()) {
            showMessage("Update Check", "Software is the latest version.");
         } else {
            List<ArtifactStatus> artifactStatusItems = 
               this._util.getArtifactStatus(_currentChannelBean);
            UpdateSummaryDialog dialog = new UpdateSummaryDialog(_app.getMainFrame(),
                                                                 artifactStatusItems,
                                                                 this);
            String installedVersion = 
               _installedChannelBean.getCurrentRelease().getVersion();
            dialog.setInstalledVersion(installedVersion);
            
            String currentVersion =
               _currentChannelBean.getCurrentRelease().getVersion();
            dialog.setAvailableVersion(currentVersion);
            
            GUIUtils.centerWithinParent(_app.getMainFrame());
            dialog.setVisible(true);
         }
      } catch (Exception e) {
         showErrorMessage("Update Check Failed", "Exception was - "
               + e.getClass().getName() + ":" + e.getMessage(), e);
      }
   }

   
   public void applyChanges(List<ArtifactStatus> artifactStatusList) {
      try {
         
         _util.saveChangeList(artifactStatusList);
      
         
         
         pullDownUpdateFiles(artifactStatusList,
                             new DownloadStatusEventHandler());
         
      } catch (Exception e) {
         showErrorMessage("Update Failed", "Exception was - "
                          + e.getClass().getName() + ":" + e.getMessage(), e);         
      }
      
   }

   
   public void showPreferences() {
      
      listener.setWaitingForOk(true);
      
      
      GlobalPreferencesSheet.showSheet(_app, UpdatePreferencesPanel.class);
   
   }
   
   
   private UpdateSettings getUpdateSettings() {
      return _app.getSquirrelPreferences().getUpdateSettings();      
   }
   
   private class GlobalPrefsListener implements GlobalPreferencesActionListener {
      
      private boolean waitingForOk = false;
      
      public void onDisplayGlobalPreferences() {}
      public void onPerformClose() {
         showDialog();        
      }
      public void onPerformOk() {
         showDialog();
      }
      
      
      private void showDialog() {
         
         if (waitingForOk) {
            waitingForOk = false;
            showUpdateDialog();
         }         
      }
      
      
      public void setWaitingForOk(boolean waitingForOk) {
         this.waitingForOk = waitingForOk;
      }      
   }
   
   
   private class DownloadStatusEventHandler implements DownloadStatusListener {

      ProgressMonitor progressMonitor = null;
      int currentFile = 0;
      int totalFiles = 0;
      
      public void handleDownloadStatusEvent(DownloadStatusEvent evt) {
         
         if (evt.getType() == DownloadEventType.DOWNLOAD_STARTED) {
            totalFiles = evt.getFileCountTotal();
            handleDownloadStarted();
         }
         if (evt.getType() == DownloadEventType.DOWNLOAD_FILE_STARTED) {
            setNote("File: "+evt.getFilename());
         }
         
         if (evt.getType() == DownloadEventType.DOWNLOAD_FILE_COMPLETED) {
            setProgress(++currentFile);
         }
         
         if (evt.getType() == DownloadEventType.DOWNLOAD_STOPPED) {
            setProgress(totalFiles);
         }
         
         
         
         if (evt.getType() == DownloadEventType.DOWNLOAD_COMPLETED) {
            showMessage("Update Download Complete",
                        "Requested updates will be installed when SQuirreL "
                              + "is restarted");
            setProgress(totalFiles);
         }
         if (evt.getType() == DownloadEventType.DOWNLOAD_FAILED) {
            showErrorMessage("Update Download Failed",
                             "Please consult the log for details");
            
            setProgress(totalFiles);
         }
      }
      
      private void setProgress(final int value) {
         GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
               progressMonitor.setProgress(value);     
            }
         });
      }
      
      private void setNote(final String note) {
         GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
               progressMonitor.setNote(note);     
            }
         });
      }
      
      private void handleDownloadStarted() {
         GUIUtils.processOnSwingEventThread(new Runnable() {
            public void run() {
               final MainFrame frame = 
                  UpdateControllerImpl.this._app.getMainFrame();
               progressMonitor = 
                  new ProgressMonitor(frame,
                                      "Downloading Updates",
                                      "Downloading Updates", 
                                      0, 
                                      totalFiles);
               setProgress(0);
            }
         });
      }
      
      
   }
}
