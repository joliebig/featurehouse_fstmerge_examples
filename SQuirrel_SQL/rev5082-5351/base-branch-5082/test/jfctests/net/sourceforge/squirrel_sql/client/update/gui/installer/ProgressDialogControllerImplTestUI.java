
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.File;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;
import net.sourceforge.squirrel_sql.fw.util.Utilities;



public class ProgressDialogControllerImplTestUI
{

	public static void main(String[] args) {
		ApplicationArguments.initialize(new String[0]);
		ProgressDialogControllerImpl controller = new ProgressDialogControllerImpl();
		
		File coreDownloadsDir = new File("/opt/squirrel/eclipse_build/update/downloads/core");
		String[] fileList = coreDownloadsDir.list();
		
		
		controller.showProgressDialog("File Backup", "Backing up file:", fileList.length);
		int count = 0;
		while (count < fileList.length) {
			controller.setDetailMessage(fileList[count++]);
			Utilities.sleep(500);
			controller.incrementProgress();
			Utilities.sleep(500);
		}
		controller.hideProgressDialog();
		System.exit(1);
	}

}
