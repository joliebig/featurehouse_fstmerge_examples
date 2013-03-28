
package net.sourceforge.squirrel_sql.client.util;

import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;

public interface ApplicationFileWrappers
{

	void setFileWrapperFactory(FileWrapperFactory factory);

	void setApplicationFiles(ApplicationFiles files);

	FileWrapper getUserSettingsDirectory();

	FileWrapper getPluginsDirectory();

	FileWrapper getLibraryDirectory();

	FileWrapper getUpdateDirectory();

	FileWrapper getDatabaseAliasesFile();

	FileWrapper getDatabaseAliasesTreeStructureFile();

	FileWrapper getDatabaseAliasesFile_before_version_2_3();

	FileWrapper getDatabaseDriversFile();

	FileWrapper getUserPreferencesFile();

	FileWrapper getCellImportExportSelectionsFile();

	FileWrapper getDTPropertiesFile();

	FileWrapper getEditWhereColsFile();

	FileWrapper getExecutionLogFile();

	FileWrapper getJDBCDebugLogFile();

	FileWrapper getUserSQLHistoryFile();

	FileWrapper getSquirrelHomeDir();

	FileWrapper getPluginsUserSettingsDirectory();

	FileWrapper getQuickStartGuideFile();

	FileWrapper getFAQFile();

	FileWrapper getChangeLogFile();

	FileWrapper getLicenceFile();

	FileWrapper getWelcomeFile();

	FileWrapper getSQuirrelJarFile();

	FileWrapper getFwJarFile();

}