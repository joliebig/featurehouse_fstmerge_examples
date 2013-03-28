package net.sourceforge.squirrel_sql.client.gui.builders;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import javax.swing.*;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.IApplication;

class SquirrelTabbedPane extends JTabbedPane
{
	private static final long serialVersionUID = 3663370280049413647L;

	private SquirrelPreferences _prefs;

	private PropsListener _prefsListener;

	
	private interface IAppPrefPropertynames extends SquirrelPreferences.IPropertyNames
	{
		
	}

	SquirrelTabbedPane(SquirrelPreferences prefs, IApplication app)
	{
		super();

		if (prefs == null) { throw new IllegalArgumentException("SquirrelPreferences == null"); }
		_prefs = prefs;

		int tabLayoutPolicy =
			_prefs.getUseScrollableTabbedPanes() ? JTabbedPane.SCROLL_TAB_LAYOUT : JTabbedPane.WRAP_TAB_LAYOUT;
		setTabLayoutPolicy(tabLayoutPolicy);
	}

	
	public void addNotify()
	{
		super.addNotify();
		_prefsListener = new PropsListener(_prefs, this);
		_prefs.addPropertyChangeListener(_prefsListener);
		_prefsListener.propertiesHaveChanged(null);
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


   
	private static final class PropsListener implements PropertyChangeListener
	{
      private SquirrelPreferences _prefs;

      private WeakReference<SquirrelTabbedPane> _refSquirrelTabbedPane;

      public PropsListener(SquirrelPreferences prefs, SquirrelTabbedPane squirrelTabbedPane)
      {
         _prefs = prefs;
         _refSquirrelTabbedPane = new WeakReference<SquirrelTabbedPane>(squirrelTabbedPane);
      }

      public void propertyChange(PropertyChangeEvent evt)
		{
			propertiesHaveChanged(evt.getPropertyName());
		}

      void propertiesHaveChanged(String propName)
      {
         SquirrelTabbedPane squirrelTabbedPane = _refSquirrelTabbedPane.get();

         if(null == squirrelTabbedPane)
         {
            return;
         }


         if (propName == null || propName.equals(IAppPrefPropertynames.SCROLLABLE_TABBED_PANES))
         {
            int tabLayoutPolicy =
               _prefs.getUseScrollableTabbedPanes() ? JTabbedPane.SCROLL_TAB_LAYOUT
                  : JTabbedPane.WRAP_TAB_LAYOUT;
            squirrelTabbedPane.setTabLayoutPolicy(tabLayoutPolicy);
         }
      }

	}
}
