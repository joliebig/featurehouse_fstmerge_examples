
package net.sourceforge.squirrel_sql.client.update.gui.installer.util;

import static net.sourceforge.squirrel_sql.fw.util.Utilities.checkNull;
import net.sourceforge.squirrel_sql.fw.util.FileWrapper;

public class InstallFileOperationInfoFactoryImpl implements InstallFileOperationInfoFactory {

   
   public InstallFileOperationInfo create(FileWrapper fileToCopy, FileWrapper installDir) {
   	checkNull("InstallFileOperationInfo", "fileToCopy",fileToCopy, "installDir", installDir);
      return new InstallFileOperationInfoImpl(fileToCopy, installDir);
   }
}
