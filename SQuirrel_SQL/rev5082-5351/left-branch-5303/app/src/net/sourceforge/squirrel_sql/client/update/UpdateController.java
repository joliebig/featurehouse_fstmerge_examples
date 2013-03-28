
package net.sourceforge.squirrel_sql.client.update;

import java.util.List;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;


public interface UpdateController {

   
   

   String getUpdateServerName();

   String getUpdateServerPort();

   String getUpdateServerPath();

   String getUpdateServerChannel();

   int getUpdateServerPortAsInt();

   void showMessage(String title, String msg);

   boolean showConfirmMessage(String title, String msg);   
   
   void showErrorMessage(String title, String msg, Exception e);
   
   void showErrorMessage(String title, String msg);
   
   void showUpdateDialog();
   
   
   void checkUpToDate();
   
   
   void applyChanges(List<ArtifactStatus> artifactStatusList, boolean releaseVersionWillChange);
   
      
   boolean isRemoteUpdateSite();

   
	public boolean isTimeToCheckForUpdates();

	public void promptUserToDownloadAvailableUpdates();

	public JFrame getMainFrame();

	

      
}
