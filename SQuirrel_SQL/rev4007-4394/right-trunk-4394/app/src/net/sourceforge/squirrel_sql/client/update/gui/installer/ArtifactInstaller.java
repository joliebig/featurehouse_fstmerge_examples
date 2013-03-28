
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListener;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;


public interface ArtifactInstaller
{

	
	public abstract void addListener(InstallStatusListener listener);

	public abstract boolean backupFiles() throws FileNotFoundException, IOException;

	public abstract void installFiles() throws FileNotFoundException, IOException;

	
	public void setChangeList(ChangeListXmlBean changeList) throws FileNotFoundException;

	
	public File getChangeListFile();

	
	public void setChangeListFile(File changeListFile);


}