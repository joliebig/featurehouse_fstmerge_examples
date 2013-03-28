package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.PropertyChangeListener;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.StringWrapper;

public interface ISQLDriver extends IHasIdentifier, Comparable<ISQLDriver>
{
	
	public interface IPropertyNames
	{
		String DRIVER_CLASS = "driverClassName";
		String ID = "identifier";
		String JARFILE_NAME = "jarFileName";
		String JARFILE_NAMES = "jarFileNames";
		String NAME = "name";
		String URL = "url";
        String WEBSITE_URL = "websiteUrl";
	}

	
	void assignFrom(ISQLDriver rhs) throws ValidationException;

	
	int compareTo(ISQLDriver rhs);

	IIdentifier getIdentifier();

	String getDriverClassName();

	void setDriverClassName(String driverClassName)
		throws ValidationException;

	
	String getJarFileName();

	void setJarFileName(String value) throws ValidationException;

	StringWrapper[] getJarFileNameWrappers();

	StringWrapper getJarFileNameWrapper(int idx) throws ArrayIndexOutOfBoundsException;


	void setJarFileNameWrappers(StringWrapper[] value);

	void setJarFileNameWrapper(int idx, StringWrapper value) throws ArrayIndexOutOfBoundsException;

	String[] getJarFileNames();
	void setJarFileNames(String[] values);

	String getUrl();

	void setUrl(String url) throws ValidationException;

	String getName();

	void setName(String name) throws ValidationException;

	boolean isJDBCDriverClassLoaded();
	void setJDBCDriverClassLoaded(boolean cl);

	void addPropertyChangeListener(PropertyChangeListener listener);
	void removePropertyChangeListener(PropertyChangeListener listener);
    
    String getWebSiteUrl();
    
    void setWebSiteUrl(String url) throws ValidationException;
}
