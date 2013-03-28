
package net.sourceforge.squirrel_sql.client.update.gui.installer.util;

import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

public interface InstallFileOperationInfoFactory {

   InstallFileOperationInfo create(FileWrapper fileToCopy, FileWrapper installDir);
}