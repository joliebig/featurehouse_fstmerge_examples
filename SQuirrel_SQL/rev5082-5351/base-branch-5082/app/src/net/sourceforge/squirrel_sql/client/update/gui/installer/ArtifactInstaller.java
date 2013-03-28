
package net.sourceforge.squirrel_sql.client.update.gui.installer;


import java.io.FileNotFoundException;
import java.io.IOException;

import net.sourceforge.squirrel_sql.client.update.gui.installer.event.InstallStatusListener;
import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;


public interface ArtifactInstaller
{

	
	void addListener(InstallStatusListener listener);

	
	boolean backupFiles() throws FileNotFoundException, IOException;

	
	void installFiles() throws FileNotFoundException, IOException;

	
	boolean restoreBackupFiles() throws FileNotFoundException, IOException;
		
	
	void setChangeList(ChangeListXmlBean changeList) throws FileNotFoundException;

	
	FileWrapper getChangeListFile();

	
	void setChangeListFile(FileWrapper changeListFile);


}