package net.sourceforge.squirrel_sql.client.gui.builders;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.IApplication;

class SquirrelTabbedPane extends JTabbedPane
{
	private SquirrelPreferences _prefs;

	private PropsListener _prefsListener;
   
   

   
	private interface IAppPrefPropertynames
							extends SquirrelPreferences.IPropertyNames
	{
		
	}

	SquirrelTabbedPane(SquirrelPreferences prefs, IApplication app)
	{
		super();

		if (prefs == null)
		{
			throw new IllegalArgumentException("SquirrelPreferences == null");
		}
		_prefs = prefs;
      

      int tabLayoutPolicy = _prefs.getUseScrollableTabbedPanes() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT;
      setTabLayoutPolicy(tabLayoutPolicy);
	}

	
	public void addNotify()
	{
		super.addNotify();
		_prefsListener = new PropsListener();
		_prefs.addPropertyChangeListener(_prefsListener);
		propertiesHaveChanged(null);
	}

	
	public void removeNotify()
	{
		super.removeNotify();
		if (_prefsListener != null)
		{
			_prefs.removePropertyChangeListener(_prefsListener);
			_prefsListener = null;
		}
	}

	private void propertiesHaveChanged(String propName)
	{
		if (propName == null || propName.equals(IAppPrefPropertynames.SCROLLABLE_TABBED_PANES))
		{
         int tabLayoutPolicy = _prefs.getUseScrollableTabbedPanes() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT;
         setTabLayoutPolicy(tabLayoutPolicy);
		}
	}

	private final class PropsListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			propertiesHaveChanged(evt.getPropertyName());
		}
	}
}
