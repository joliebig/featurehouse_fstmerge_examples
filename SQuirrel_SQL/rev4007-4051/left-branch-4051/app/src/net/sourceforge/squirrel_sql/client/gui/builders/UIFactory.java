package net.sourceforge.squirrel_sql.client.gui.builders;

import javax.swing.JTabbedPane;
import javax.swing.event.EventListenerList;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.IApplication;

public class UIFactory
{
	
	private static UIFactory s_instance;

	
	private SquirrelPreferences _prefs;

	
	private final EventListenerList _listenerList = new EventListenerList();
   private IApplication _app;

   
	public static UIFactory getInstance()
	{
		if (s_instance == null)
		{
			throw new IllegalArgumentException("UIFactory has not been initialized");
		}

		return s_instance;
	}

	
	public synchronized static void initialize(SquirrelPreferences prefs, IApplication app)
	{
		if (s_instance != null)
		{
			throw new IllegalStateException("UIFactory has alerady been initialized");
		}
		s_instance = new UIFactory(prefs, app);
	}

	
	private UIFactory(SquirrelPreferences prefs, IApplication app)
	{
		super();
		if (prefs == null)
		{
			throw new IllegalArgumentException("SquirrelPreferences == null");
		}
		_prefs = prefs;
      _app = app;
	}

	
	public JTabbedPane createTabbedPane()
	{
		return createTabbedPane(JTabbedPane.TOP);
	}

	
	public JTabbedPane createTabbedPane(int tabPlacement)
	{
		final JTabbedPane pnl = new SquirrelTabbedPane(_prefs, _app);
		pnl.setTabPlacement(tabPlacement);
		fireTabbedPaneCreated(pnl);

		return pnl;
	}

	
	public void addListener(IUIFactoryListener lis)
	{
		_listenerList.add(IUIFactoryListener.class, lis);
	}

	
	public void removeListener(IUIFactoryListener lis)
	{
		_listenerList.remove(IUIFactoryListener.class, lis);
	}

	
	private void fireTabbedPaneCreated(JTabbedPane tabPnl)
	{
		
		Object[] listeners = _listenerList.getListenerList();
		
		
		UIFactoryComponentCreatedEvent evt = null;
		for (int i = listeners.length - 2; i >= 0; i-=2 )
		{
			if (listeners[i] == IUIFactoryListener.class)
			{
				
				if (evt == null)
				{
					evt = new UIFactoryComponentCreatedEvent(this, tabPnl);
				}
				((IUIFactoryListener)listeners[i + 1]).tabbedPaneCreated(evt);
			}
		}
	}
}
