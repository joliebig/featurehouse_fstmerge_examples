package net.sourceforge.squirrel_sql.plugins.exportconfig;

import java.io.File;
import java.io.IOException;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import net.sourceforge.squirrel_sql.client.util.ApplicationFiles;




public class ExportConfigPreferences
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ExportConfigPreferences.class);

	
	private final static ILogger s_log =
		LoggerController.createLogger(ExportConfigPreferences.class);

	
	private boolean _exportPreferences = true;

	
	private boolean _exportDrivers = true;

	
	private boolean _exportAliases = true;

	
	private boolean _includeUserNames = true;

	
	private boolean _includePasswords = true;

	
	private String _preferencesFileName;

	
	private String _driversFileName;

	
	private String _aliasesFileName;

	
	public ExportConfigPreferences()
	{
		super();

		final File here = new File(".");
		final ApplicationFiles appFiles = new ApplicationFiles();
		_preferencesFileName = getFileName(here, appFiles.getUserPreferencesFile().getName());
		_driversFileName = getFileName(here, appFiles.getDatabaseDriversFile().getName());
		_aliasesFileName = getFileName(here, appFiles.getDatabaseAliasesFile().getName());
	}

	
	public boolean getExportPreferences()
	{
		return _exportPreferences;
	}

	
	public void setExportPreferences(boolean value)
	{
		_exportPreferences = value;
	}

	
	public boolean getExportDrivers()
	{
		return _exportDrivers;
	}

	
	public void setExportDrivers(boolean value)
	{
		_exportDrivers = value;
	}

	
	public boolean getExportAliases()
	{
		return _exportAliases;
	}

	
	public void setExportAliases(boolean value)
	{
		_exportAliases = value;
	}

	
	public boolean getIncludeUserNames()
	{
		return _includeUserNames;
	}

	
	public void setIncludeUserNames(boolean value)
	{
		_includeUserNames = value;
	}

	
	public boolean getIncludePasswords()
	{
		return _includePasswords;
	}

	
	public void setIncludePasswords(boolean value)
	{
		_includePasswords = value;
	}

	
	public String getPreferencesFileName()
	{
		return _preferencesFileName;
	}

	
	public void setPreferencesFileName(String value)
	{
		_preferencesFileName = value;
	}

	
	public String getDriversFileName()
	{
		return _driversFileName;
	}

	
	public void setDriversFileName(String value)
	{
		_driversFileName = value;
	}

	
	public String getAliasesFileName()
	{
		return _aliasesFileName;
	}

	
	public void setAliasesFileName(String value)
	{
		_aliasesFileName = value;
	}

	private String getFileName(File dir, String name)
	{
		
		return getFileName(new File(dir, name));
	}

	private String getFileName(File file)
	{
		try
		{
			return file.getCanonicalPath();
		}
		catch (IOException ex)
		{
			
			s_log.error(s_stringMgr.getString("exportconfig.errorResolvingFile"), ex);
		}
		return file.getAbsolutePath();
	}
}
