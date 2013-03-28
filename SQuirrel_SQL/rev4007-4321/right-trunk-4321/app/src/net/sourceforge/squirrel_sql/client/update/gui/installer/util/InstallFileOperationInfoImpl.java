
package net.sourceforge.squirrel_sql.client.update.gui.installer.util;

import java.io.File;

public class InstallFileOperationInfoImpl implements InstallFileOperationInfo {

   private File fileToInstall;
   private File installDir;
   private boolean isPlugin;
   
   
   public InstallFileOperationInfoImpl(File fileToInstall, File installDir) {
      super();
      this.fileToInstall = fileToInstall;
      this.installDir = installDir;
   }
   
   
   public File getFileToInstall() {
      return fileToInstall;
   }
   
   public void setFileToInstall(File fileToInstall) {
      this.fileToInstall = fileToInstall;
   }
   
   public File getInstallDir() {
      return installDir;
   }
   
   public boolean isPlugin() {
   	return isPlugin;
   }

	
   public void setPlugin(boolean isPlugin) {
   	this.isPlugin = isPlugin;
   }

	
   public void setInstallDir(File installDir) {
      this.installDir = installDir;
   } 
}
