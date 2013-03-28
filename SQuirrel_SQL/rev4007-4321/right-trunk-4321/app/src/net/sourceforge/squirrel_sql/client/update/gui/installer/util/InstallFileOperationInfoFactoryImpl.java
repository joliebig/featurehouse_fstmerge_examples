
package net.sourceforge.squirrel_sql.client.update.gui.installer.util;

import java.io.File;

public class InstallFileOperationInfoFactoryImpl implements InstallFileOperationInfoFactory {

   
   public InstallFileOperationInfo create(File fileToCopy, File installDir) {
      return new InstallFileOperationInfoImpl(fileToCopy, installDir);
   }
}
