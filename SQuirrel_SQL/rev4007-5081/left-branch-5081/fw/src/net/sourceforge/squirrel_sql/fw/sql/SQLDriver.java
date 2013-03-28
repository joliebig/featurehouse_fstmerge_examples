package net.sourceforge.squirrel_sql.fw.sql;

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.StringWrapper;

public class SQLDriver implements ISQLDriver, Cloneable, Serializable
{
    static final long serialVersionUID = 8506401259069527981L;
    
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SQLDriver.class);

	private interface IStrings
	{
		String ERR_BLANK_NAME = s_stringMgr.getString("SQLDriver.error.blankname");
		String ERR_BLANK_DRIVER = s_stringMgr.getString("SQLDriver.error.blankdriver");
		String ERR_BLANK_URL = s_stringMgr.getString("SQLDriver.error.blankurl");
	}

	
	private IIdentifier _id;

	
	private String _name;

	
	private String _jarFileName = null;

	
	private List<String> _jarFileNamesList = new ArrayList<String>();

	
	private String _driverClassName;

	
	private String _url;

	
	private boolean _jdbcDriverClassLoaded;

	
	private transient PropertyChangeReporter _propChgReporter;

    
    private String _websiteUrl;
    
	
	public SQLDriver(IIdentifier id)
	{
		super();
		_id = id;
		_name = "";
		_jarFileName = null;
		_driverClassName = null;
		_url = "";
        _websiteUrl = "";
	}

	
	public SQLDriver()
	{
		super();
	}

	
	public synchronized void assignFrom(ISQLDriver rhs)
		throws ValidationException
	{
		setName(rhs.getName());
		setJarFileNames(rhs.getJarFileNames());
		setDriverClassName(rhs.getDriverClassName());
		setUrl(rhs.getUrl());
		setJDBCDriverClassLoaded(rhs.isJDBCDriverClassLoaded());
        setWebSiteUrl(rhs.getWebSiteUrl());
	}

	
	public boolean equals(Object rhs)
	{
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass()))
		{
			rc = ((ISQLDriver) rhs).getIdentifier().equals(getIdentifier());
		}
		return rc;
	}

	
	public synchronized int hashCode()
	{
		return getIdentifier().hashCode();
	}

	
	public String toString()
	{
		return getName();
	}

	
	public Object clone()
	{
		try
		{
			final SQLDriver driver = (SQLDriver)super.clone();
			driver._propChgReporter = null;
			return driver;
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); 
		}
	}

	
	public int compareTo(ISQLDriver rhs)
	{
		return _name.compareTo(rhs.getName());
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().removePropertyChangeListener(listener);
	}

	public void setReportPropertyChanges(boolean report)
	{
		getPropertyChangeReporter().setNotify(report);
	}

	public IIdentifier getIdentifier()
	{
		return _id;
	}

	public void setIdentifier(IIdentifier id)
	{
		_id = id;
	}

	public String getDriverClassName()
	{
		return _driverClassName;
	}

	public void setDriverClassName(String driverClassName)
		throws ValidationException
	{
		String data = getString(driverClassName);
		if (data.length() == 0)
		{
			throw new ValidationException(IStrings.ERR_BLANK_DRIVER);
		}
        if (!data.equals(_driverClassName))
		{
			final String oldValue = _driverClassName;
			_driverClassName = data;
			getPropertyChangeReporter().firePropertyChange(
				ISQLDriver.IPropertyNames.DRIVER_CLASS,
				oldValue,
				_driverClassName);
		}
	}

	
	public String getJarFileName()
	{
		return _jarFileName;
	}

	public void setJarFileName(String value)
	{
		if (value == null)
		{
			value = "";
		}
		if (_jarFileName == null || !_jarFileName.equals(value))
		{
			final String oldValue = _jarFileName;
			_jarFileName = value;
			getPropertyChangeReporter().firePropertyChange(
				ISQLDriver.IPropertyNames.JARFILE_NAME,
				oldValue,
				_jarFileName);
		}
	}

	public synchronized String[] getJarFileNames()
	{
		return _jarFileNamesList.toArray(new String[_jarFileNamesList.size()]);
	}

	public synchronized void setJarFileNames(String[] values)
	{
		String[] oldValue =
            _jarFileNamesList.toArray(new String[_jarFileNamesList.size()]);
		_jarFileNamesList.clear();

		if (values == null)
		{
			values = new String[0];
		}

		for (int i = 0; i < values.length; ++i)
		{
			_jarFileNamesList.add(values[i]);
		}

		getPropertyChangeReporter().firePropertyChange(
			ISQLDriver.IPropertyNames.JARFILE_NAMES,
			oldValue,
			values);
	}
	public String getUrl()
	{
		return _url;
	}

	public void setUrl(String url) throws ValidationException
	{
		String data = getString(url);
		if (data.length() == 0)
		{
			throw new ValidationException(IStrings.ERR_BLANK_URL);
		}
		if (!data.equals(_url))
		{
			final String oldValue = _url;
			_url = data;
			getPropertyChangeReporter().firePropertyChange(
				ISQLDriver.IPropertyNames.URL,
				oldValue,
				_url);
		}
	}

	public String getName()
	{
		return _name;
	}

	public void setName(String name) throws ValidationException
	{
		String data = getString(name);
		if (data.length() == 0)
		{
			throw new ValidationException(IStrings.ERR_BLANK_NAME);
		}
        if (!data.equals(_name))
		{
			final String oldValue = _name;
			_name = data;
			getPropertyChangeReporter().firePropertyChange(
				ISQLDriver.IPropertyNames.NAME,
				oldValue,
				_name);
		}
	}

	public boolean isJDBCDriverClassLoaded()
	{
		return _jdbcDriverClassLoaded;
	}

	public void setJDBCDriverClassLoaded(boolean cl)
	{
		_jdbcDriverClassLoaded = cl;
		
		
	}

	public synchronized StringWrapper[] getJarFileNameWrappers()
	{
		StringWrapper[] wrappers = new StringWrapper[_jarFileNamesList.size()];
		for (int i = 0; i < wrappers.length; ++i)
		{
			wrappers[i] = new StringWrapper(_jarFileNamesList.get(i));
		}
		return wrappers;
	}

	public StringWrapper getJarFileNameWrapper(int idx)
		throws ArrayIndexOutOfBoundsException
	{
		return new StringWrapper(_jarFileNamesList.get(idx));
	}

	public void setJarFileNameWrappers(StringWrapper[] value)
	{
		_jarFileNamesList.clear();
		if (value != null)
		{
			for (int i = 0; i < value.length; ++i)
			{
				_jarFileNamesList.add(value[i].getString());
			}
		}
	}

	public void setJarFileNameWrapper(int idx, StringWrapper value)
		throws ArrayIndexOutOfBoundsException
	{
		_jarFileNamesList.set(idx, value.getString());
	}

	private String getString(String data)
	{
		return data != null ? data.trim() : "";
	}

	private synchronized PropertyChangeReporter getPropertyChangeReporter()
	{
		if (_propChgReporter == null)
		{
			_propChgReporter = new PropertyChangeReporter(this);
		}
		return _propChgReporter;
	}

    
    public String getWebSiteUrl() {
        return _websiteUrl;
    }

    
    public void setWebSiteUrl(String url) throws ValidationException { 
        String data = getString(url);
        if (!data.equals(_websiteUrl)) {
            final String oldValue = _websiteUrl;
            _websiteUrl = data;
            PropertyChangeReporter pcr = getPropertyChangeReporter();
            pcr.firePropertyChange(ISQLDriver.IPropertyNames.WEBSITE_URL,
                                   oldValue,
                                   _websiteUrl);            
        }
    }
    
    
}
