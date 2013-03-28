
package net.sourceforge.squirrel_sql.client.util;

import net.sourceforge.squirrel_sql.fw.util.FileWrapper;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactory;
import net.sourceforge.squirrel_sql.fw.util.FileWrapperFactoryImpl;


public class ApplicationFileWrappersImpl implements ApplicationFileWrappers
{
	private FileWrapperFactory _fileWrapperFactory = new FileWrapperFactoryImpl();
	
	public void setFileWrapperFactory(FileWrapperFactory factory) {
		_fileWrapperFactory = factory;
	}
	
	private ApplicationFiles _appFiles = new ApplicationFiles();
	
	public void setApplicationFiles(ApplicationFiles files) {
		_appFiles = files;
	}
	
	public ApplicationFileWrappersImpl() {
	}
	
	
	public FileWrapper getUserSettingsDirectory() {
		return _fileWrapperFactory.create(_appFiles.getUserSettingsDirectory());
	}
	
	
	public FileWrapper getPluginsDirectory() {
		return _fileWrapperFactory.create(_appFiles.getPluginsDirectory());
	}
	
	
	public FileWrapper getLibraryDirectory() {
		return _fileWrapperFactory.create(_appFiles.getLibraryDirectory());
	}
	
	
	public FileWrapper getUpdateDirectory() {
		return _fileWrapperFactory.create(_appFiles.getUpdateDirectory());
	}

	
	public FileWrapper getDatabaseAliasesFile() {
		return _fileWrapperFactory.create(_appFiles.getDatabaseAliasesFile());
	}

	
	public FileWrapper getDatabaseAliasesTreeStructureFile() {
		return _fileWrapperFactory.create(_appFiles.getDatabaseAliasesTreeStructureFile());
	}
	
	
	public FileWrapper getDatabaseAliasesFile_before_version_2_3() {
		return _fileWrapperFactory.create(_appFiles.getDatabaseAliasesFile_before_version_2_3());
	}

	
	public FileWrapper getDatabaseDriversFile() {
		return _fileWrapperFactory.create(_appFiles.getDatabaseDriversFile());
	}
	
	
	public FileWrapper getUserPreferencesFile() {
		return _fileWrapperFactory.create(_appFiles.getUserPreferencesFile());
	}
	
	
	public FileWrapper getCellImportExportSelectionsFile() {
		return _fileWrapperFactory.create(_appFiles.getCellImportExportSelectionsFile());
	}
	
	
	public FileWrapper getDTPropertiesFile() {
		return _fileWrapperFactory.create(_appFiles.getDTPropertiesFile());
	}
	
	
	public FileWrapper getEditWhereColsFile() {
		return _fileWrapperFactory.create(_appFiles.getEditWhereColsFile());
	}
	
	
	public FileWrapper getExecutionLogFile() {
		return _fileWrapperFactory.create(_appFiles.getExecutionLogFile());
	}
		
	
	public FileWrapper getJDBCDebugLogFile() {
		return _fileWrapperFactory.create(_appFiles.getJDBCDebugLogFile());
	}
	
	
	public FileWrapper getUserSQLHistoryFile() {
		return _fileWrapperFactory.create(_appFiles.getUserSQLHistoryFile());
	}
	
	
	public FileWrapper getSquirrelHomeDir() {
		return _fileWrapperFactory.create(_appFiles.getSquirrelHomeDir());
	}
	
	
	public FileWrapper getPluginsUserSettingsDirectory() {
		return _fileWrapperFactory.create(_appFiles.getPluginsUserSettingsDirectory());
	}
	
	
	public FileWrapper getQuickStartGuideFile() {
		return _fileWrapperFactory.create(_appFiles.getQuickStartGuideFile());
	}

	
	public FileWrapper getFAQFile() {
		return _fileWrapperFactory.create(_appFiles.getFAQFile());
	}
	
	
	public FileWrapper getChangeLogFile() {
		return _fileWrapperFactory.create(_appFiles.getChangeLogFile());
	}
	
	
	public FileWrapper getLicenceFile() {
		return _fileWrapperFactory.create(_appFiles.getLicenceFile());
	}
	
	
	public FileWrapper getWelcomeFile() {
		return _fileWrapperFactory.create(_appFiles.getWelcomeFile());
	}
	
	
	public FileWrapper getSQuirrelJarFile() {
		return _fileWrapperFactory.create(_appFiles.getSQuirrelJarFile());
	}
	
	
	public FileWrapper getFwJarFile() {
		return _fileWrapperFactory.create(_appFiles.getFwJarFile());
	}
	
	
}
