
package net.sourceforge.squirrel_sql.client.update;

import java.util.List;

import net.sourceforge.squirrel_sql.client.update.gui.ArtifactStatus;


public interface UpdateController {

   
   boolean isUpToDate() throws Exception;

   String getUpdateServerName();

   String getUpdateServerPort();

   String getUpdateServerPath();

   String getUpdateServerChannel();

   int getUpdateServerPortAsInt();

   void showMessage(String title, String msg);
   
   void showUpdateDialog();
   
   void showErrorMessage(String title, String msg, Exception e);
   
   void showErrorMessage(String title, String msg);
   
   
   void checkUpToDate();
   
   
   void applyChanges(List<ArtifactStatus> artifactStatusList);
   
      
   boolean isRemoteUpdateSite();
      
}
