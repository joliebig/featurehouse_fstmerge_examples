
package net.sourceforge.squirrel_sql.client.update.gui.installer.util;

import java.io.File;

public interface InstallFileOperationInfo {

   
   File getFileToInstall();

   
   File getInstallDir();

	
   public boolean isPlugin();

	
   public void setPlugin(boolean isPlugin);

}