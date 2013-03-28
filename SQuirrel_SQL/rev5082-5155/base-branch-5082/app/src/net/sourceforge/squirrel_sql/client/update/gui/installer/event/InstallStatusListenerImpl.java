
package net.sourceforge.squirrel_sql.client.update.gui.installer.event;

import net.sourceforge.squirrel_sql.client.update.gui.installer.ProgressDialogController;
import net.sourceforge.squirrel_sql.fw.util.Utilities;

public class InstallStatusListenerImpl implements InstallStatusListener
{

	ProgressDialogController _progressDialogController = null;

	
	public InstallStatusListenerImpl(ProgressDialogController progressDialogFactory)
	{
		Utilities.checkNull("InstallStatusListenerImpl.init", "progressDialogFactory", progressDialogFactory);
		this._progressDialogController = progressDialogFactory;
	}

	
	public void handleInstallStatusEvent(InstallStatusEvent evt)
	{
		if (evt.getType() == InstallEventType.INIT_CHANGELIST_STARTED) {
			_progressDialogController.showProgressDialog("Initializing File Change List", "Processing file:",
				evt.getNumFilesToUpdate());			
		}
		if (evt.getType() == InstallEventType.FILE_INIT_CHANGELIST_STARTED) {
			_progressDialogController.setDetailMessage(evt.getArtifactName());
		}
		if (evt.getType() == InstallEventType.FILE_INIT_CHANGELIST_COMPLETE) {
			_progressDialogController.incrementProgress();
		}		
		if (evt.getType() == InstallEventType.INIT_CHANGELIST_COMPLETE) {
			_progressDialogController.setDetailMessage("");
		}
		if (evt.getType() == InstallEventType.BACKUP_STARTED)
		{
			_progressDialogController.resetProgressDialog("Backing up files to be updated", "Backing up file:",
				evt.getNumFilesToUpdate());
		}
		if (evt.getType() == InstallEventType.FILE_BACKUP_STARTED)
		{
			_progressDialogController.setDetailMessage(evt.getArtifactName());
		}
		if (evt.getType() == InstallEventType.FILE_BACKUP_COMPLETE)
		{
			_progressDialogController.incrementProgress();
		}
		if (evt.getType() == InstallEventType.BACKUP_COMPLETE)
		{
			_progressDialogController.setDetailMessage("");
		}
		
		if (evt.getType() == InstallEventType.REMOVE_STARTED) {
			_progressDialogController.resetProgressDialog("Removing file to be updated", "Removing file:", 
				evt.getNumFilesToUpdate());
		}		
		if (evt.getType() == InstallEventType.FILE_REMOVE_STARTED) {
			_progressDialogController.setDetailMessage(evt.getArtifactName());
		}
		if (evt.getType() == InstallEventType.FILE_REMOVE_STARTED) {
			_progressDialogController.incrementProgress();
		}
		if (evt.getType() == InstallEventType.REMOVE_COMPLETE) {
			_progressDialogController.setDetailMessage("");
		}
		if (evt.getType() == InstallEventType.INSTALL_STARTED)
		{
			_progressDialogController.resetProgressDialog("Installing updated files", "Installing file:", 
				evt.getNumFilesToUpdate());
		}
		if (evt.getType() == InstallEventType.FILE_INSTALL_STARTED)
		{
			_progressDialogController.setDetailMessage(evt.getArtifactName());
		}
		if (evt.getType() == InstallEventType.FILE_INSTALL_COMPLETE)
		{
			_progressDialogController.incrementProgress();
		}
		if (evt.getType() == InstallEventType.INSTALL_COMPLETE)
		{
			_progressDialogController.hideProgressDialog();
		}
	}

}