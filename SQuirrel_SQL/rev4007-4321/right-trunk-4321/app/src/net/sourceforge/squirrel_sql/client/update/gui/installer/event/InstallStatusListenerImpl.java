
package net.sourceforge.squirrel_sql.client.update.gui.installer.event;

public class InstallStatusListenerImpl implements InstallStatusListener {

   
   public void handleInstallStatusEvent(InstallStatusEvent evt) {
      if (evt.getType() == InstallEventType.BACKUP_STARTED) {}
      if (evt.getType() == InstallEventType.FILE_BACKUP_STARTED) {}
      if (evt.getType() == InstallEventType.FILE_BACKUP_COMPLETE) {}
      if (evt.getType() == InstallEventType.FILE_INSTALL_STARTED) {}
      if (evt.getType() == InstallEventType.FILE_INSTALL_COMPLETE) {}
      if (evt.getType() == InstallEventType.BACKUP_COMPLETE) {}
   }
   
}