package net.sourceforge.squirrel_sql.fw.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

public class PropertyChangeReporter implements Serializable
{
    private static final long serialVersionUID = 1L;

    private boolean _notify = true;
	private Object _srcBean;
	private PropertyChangeSupport _propChgNotifier;

	public PropertyChangeReporter(Object srcBean)
		throws IllegalArgumentException
	{
		super();
		if (srcBean == null)
		{
			throw new IllegalArgumentException("Null srcBean passed");
		}
		_srcBean = srcBean;
	}

	public void setNotify(boolean value)
	{
		_notify = value;
	}

	public synchronized void addPropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeNotifier().addPropertyChangeListener(listener);
	}

	public synchronized void removePropertyChangeListener(PropertyChangeListener listener)
	{
		getPropertyChangeNotifier().removePropertyChangeListener(listener);
	}

	public void firePropertyChange(
		String propName,
		Object oldValue,
		Object newValue)
	{
		if (_notify)
		{
			getPropertyChangeNotifier().firePropertyChange(
				propName,
				oldValue,
				newValue);
		}
	}

	public void firePropertyChange(
		String propName,
		boolean oldValue,
		boolean newValue)
	{
		if (_notify)
		{
			getPropertyChangeNotifier().firePropertyChange(
				propName,
				oldValue,
				newValue);
		}
	}

	public void firePropertyChange(String propName, int oldValue, int newValue)
	{
		if (_notify)
		{
			getPropertyChangeNotifier().firePropertyChange(
				propName,
				oldValue,
				newValue);
		}
	}

	private PropertyChangeSupport getPropertyChangeNotifier()
	{
		if (_propChgNotifier == null)
		{
			_propChgNotifier = new PropertyChangeSupport(_srcBean);
		}
		return _propChgNotifier;
	}
}
