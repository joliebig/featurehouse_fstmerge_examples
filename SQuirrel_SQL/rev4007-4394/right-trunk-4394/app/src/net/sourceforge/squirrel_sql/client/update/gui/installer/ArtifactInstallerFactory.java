
package net.sourceforge.squirrel_sql.client.update.gui.installer;

import java.io.File;
import java.io.FileNotFoundException;

import net.sourceforge.squirrel_sql.client.update.xmlbeans.ChangeListXmlBean;

public interface ArtifactInstallerFactory {

	ArtifactInstaller create(ChangeListXmlBean changeList) throws FileNotFoundException;
	
	ArtifactInstaller create(File changeList) throws FileNotFoundException;
}
