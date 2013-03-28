
package net.sourceforge.squirrel_sql.client.update.gui.installer.util;

import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

public class InstallFileOperationInfoImpl implements InstallFileOperationInfo {

   private FileWrapper fileToInstall;
   private FileWrapper installDir;
   private boolean isPlugin;
   private String artifactName;
   
   
   public InstallFileOperationInfoImpl(FileWrapper fileToInstall, FileWrapper installDir) {
      super();
      this.fileToInstall = fileToInstall;
      this.installDir = installDir;
   }
   
   
   public FileWrapper getFileToInstall() {
      return fileToInstall;
   }
   
   public void setFileToInstall(FileWrapper fileToInstall) {
      this.fileToInstall = fileToInstall;
   }
   
   public FileWrapper getInstallDir() {
      return installDir;
   }
   
   public boolean isPlugin() {
   	return isPlugin;
   }

	
   public void setPlugin(boolean isPlugin) {
   	this.isPlugin = isPlugin;
   }

	
   public void setInstallDir(FileWrapper installDir) {
      this.installDir = installDir;
   }

	
	public void setArtifactName(String artifactName)
	{
		this.artifactName = artifactName;
	}

	
	public String getArtifactName()
	{
		return artifactName;
	} 
}
