
package net.sourceforge.squirrel_sql.client.update.gui.installer.util;

import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

public interface InstallFileOperationInfo {

   
	FileWrapper getFileToInstall();

   
	FileWrapper getInstallDir();

	
   public boolean isPlugin();

	
   public void setPlugin(boolean isPlugin);

}