package net.sourceforge.squirrel_sql.client.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import net.sourceforge.squirrel_sql.fw.util.IJavaPropertyNames;

import net.sourceforge.squirrel_sql.client.ApplicationArguments;

public class ApplicationFiles
{
	
	private String _userSettingsDir;

	
	private final File _squirrelHomeDir;

	
	private String _squirrelPluginsDir;

	
	private String _documentationDir;

	
	private static boolean needExecutionLogCleanup = true;

	
	private static boolean needDebugLogCleanup = true;

	
	public ApplicationFiles()
	{
		super();
		ApplicationArguments args = ApplicationArguments.getInstance();

		final String homeDir = args.getSquirrelHomeDirectory();
		_squirrelHomeDir = new File(homeDir != null ? homeDir : System.getProperty(IJavaPropertyNames.USER_DIR));
		_squirrelPluginsDir = _squirrelHomeDir.getPath() + File.separator + "plugins";
		_documentationDir = _squirrelHomeDir.getPath() + File.separator + "doc";

		_userSettingsDir = args.getUserSettingsDirectoryOverride();
		if (_userSettingsDir == null)
		{
			_userSettingsDir = System.getProperty(IJavaPropertyNames.USER_HOME)
											+ File.separator + ".squirrel-sql";
		}
		try
		{
			new File(_userSettingsDir).mkdirs();
		}
		catch (Exception ex)
		{
			System.out.println(
				"Error creating user settings directory: " + _userSettingsDir);
			System.out.println(ex.toString());
		}
		try
		{
			final File logsDir = getExecutionLogFile().getParentFile();
			logsDir.mkdirs();
		}
		catch (Exception ex)
		{
			System.out.println("Error creating logs directory");
			System.out.println(ex.toString());
		}
	}

	public File getUserSettingsDirectory()
	{
		return new File(_userSettingsDir);
	}

	public File getPluginsDirectory()
	{
		return new File(_squirrelPluginsDir);
	}

	
	public File getDatabaseAliasesFile()
	{
		return new File(_userSettingsDir + File.separator + "SQLAliases23.xml");
	}

   public File getDatabaseAliasesFile_before_version_2_3()
   {
      return new File(_userSettingsDir + File.separator + "SQLAliases.xml");
   }


   
	public File getDatabaseDriversFile()
	{
		return new File(_userSettingsDir + File.separator + "SQLDrivers.xml");
	}

	
	public File getUserPreferencesFile()
	{
		return new File(_userSettingsDir + File.separator + "prefs.xml");
	}

	
	public File getCellImportExportSelectionsFile()
	{
		return new File(_userSettingsDir + File.separator + "cellImportExport.xml");
	}

	
	public File getDTPropertiesFile()
	{
		return new File(_userSettingsDir + File.separator + "DTproperties.xml");
	}

	
	public File getEditWhereColsFile()
	{
		return new File(_userSettingsDir + File.separator + "editWhereCols.xml");
	}

	
	public File getExecutionLogFile()
	{
		final String dirPath = _userSettingsDir + File.separator + "logs";
		final String logBaseName = "squirrel-sql.log";

		if (needExecutionLogCleanup) {
			
			
			deleteOldFiles(dirPath, logBaseName);
			needExecutionLogCleanup = false;
		}
		return new File(dirPath	+ File.separator + logBaseName);
	}

	
	public File getJDBCDebugLogFile()
	{
		final String dirPath = _userSettingsDir + File.separator + "logs";
		final String logBaseName = "jdbcdebug.log";

		if (needDebugLogCleanup) {
			
			
			deleteOldFiles(dirPath, logBaseName);
			needDebugLogCleanup = false;
		}
		return new File(dirPath	+ File.separator + logBaseName);
	}

	





 	
 	public File getUserSQLHistoryFile()
 	{
 		return new File(_userSettingsDir + File.separator + "sql_history.xml");
 	}

	public File getSquirrelHomeDir()
	{
		return _squirrelHomeDir;
	}


	
	public File getPluginsUserSettingsDirectory()
	{
		return new File(_userSettingsDir + File.separator + "plugins");
	}

	
	public File getQuickStartGuideFile()
	{
		return new File(_documentationDir + File.separator + "quick_start.html");
	}

	
	public File getFAQFile()
	{
		return new File(_documentationDir + File.separator + "faq.html");
	}

	
	public File getChangeLogFile()
	{
		return new File(_documentationDir + File.separator + "changes.txt");
	}

	
	public File getLicenceFile()
	{
		return new File(_documentationDir + File.separator + "licences/squirrel_licence.txt");
	}

 	
 	public File getWelcomeFile()
 	{
 		return new File(_documentationDir + File.separator + "welcome.html");
 	}

	
	private void deleteOldFiles(String dirPath, String fileBase) {

		
		final int numberToKeep = 3;

		
		class OldFileNameFilter implements FilenameFilter {
			String fBase;
			OldFileNameFilter(String fileBase) {
				fBase = fileBase;
			}
			public boolean accept (File dir, String name) {
				if (name.startsWith(fBase))
					return true;
				return false;
			}
		}

		
		File dir = new File(dirPath);

		
		OldFileNameFilter fileFilter = new OldFileNameFilter(fileBase);

		
		String fileNames[] = dir.list(fileFilter);
		if (fileNames == null || fileNames.length <= numberToKeep)
			return;	

		
		

		
		Arrays.sort(fileNames);

		
		
		
		
		int startIndex = 0;
		int endIndex = fileNames.length - numberToKeep;
		if (fileNames[0].equals(fileBase)) {
			
			
			startIndex = 1;
			endIndex++;
		}

		for (int i = startIndex; i < endIndex; i++) {
			
			File oldFile = new File(dirPath + File.separator + fileNames[i]);
			oldFile.delete();
		}
	}

	public File getSQuirrelJarFile()
	{
		File ret = new File(_squirrelHomeDir.getPath() + File.separator + "lib" + File.separator + "squirrel-sql.jar");

		if(false == ret.exists())
		{
			ret = new File(_squirrelHomeDir.getPath() +  File.separator + "squirrel-sql.jar");
		}
		return ret;
	}

	public File getFwJarFile()
	{
		return new File(_squirrelHomeDir.getPath() + File.separator + "lib" + File.separator + "fw.jar");		
	}
}
