
package net.sourceforge.squirrel_sql.client.update.gui.installer.util;

import java.io.File;

public interface InstallFileOperationInfoFactory {

   InstallFileOperationInfo create(File fileToCopy, File installDir);
}