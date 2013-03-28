package net.sourceforge.squirrel_sql.client.db;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.persist.ValidationException;
import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class AliasGroup implements Cloneable, Serializable, Comparable<AliasGroup>
{
    private static final long serialVersionUID = 1L;

    
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AliasGroup.class);

	
	public interface IPropertyNames
	{
		String ID = "identifier";
		String NAME = "name";
	}

	
	private IIdentifier _id;

	
	private String _name;

	
	private transient PropertyChangeReporter _propChgReporter;

	
	public AliasGroup()
	{
		super();
		_name = "";
	}

	
	public synchronized boolean isValid()
	{
		return _name.length() > 0;
	}

	
	public boolean equals(Object rhs)
	{
		boolean rc = false;
		if (rhs != null && rhs.getClass().equals(getClass()))
		{
			rc = ((AliasGroup)rhs).getIdentifier().equals(getIdentifier());
		}
		return rc;
	}

	
	public Object clone()
	{
		try
		{
			final AliasGroup obj = (AliasGroup)super.clone();
			obj._propChgReporter = null;
			return obj;
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); 
		}
	}

	
	public synchronized int hashCode()
	{
		return getIdentifier().hashCode();
	}

	
	public int compareTo(AliasGroup rhs)
	{
		return _name.compareTo((rhs).getName());
	}

	
	public IIdentifier getIdentifier()
	{
		return _id;
	}

	public void setIdentifier(IIdentifier id)
	{
		_id = id;
	}

	
	public String getName()
	{
		return _name;
	}

	
	public void setName(String value)
		throws ValidationException
	{
		String data = getString(value);
		if (data.length() == 0)
		{
			throw new ValidationException(s_stringMgr.getString("AliasGroup.error.blankname"));
		}
		if (!_name.equals(data))
		{
			final String oldValue = _name;
			_name = data;
            getPropertyChangeReporter().firePropertyChange(IPropertyNames.NAME,
												           oldValue, 
                                                           _name);
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeReporter().removePropertyChangeListener(listener);
	}

	private synchronized PropertyChangeReporter getPropertyChangeReporter()
	{
		if (_propChgReporter == null)
		{
			_propChgReporter = new PropertyChangeReporter(this);
		}
		return _propChgReporter;
	}

	private String getString(String data)
	{
		return data != null ? data.trim() : "";
	}
}
