package net.sourceforge.squirrel_sql.client.preferences;

import java.awt.Component;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class UpdatePreferencesTab implements IGlobalPreferencesPanel
{

   
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(UpdatePreferencesTab.class);
   
   static interface i18n {
      
      
      String TITLE = s_stringMgr.getString("UpdatePreferencesTab.title");

      
      String HINT = s_stringMgr.getString("UpdatePreferencesTab.hint");

   }   
   
	
	private UpdatePreferencesPanel _myPanel;

	
	private IApplication _app;
	
	
	
	public UpdatePreferencesTab()
	{
		super();
	}

	public void initialize(IApplication app)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}

		_app = app;

		UpdatePreferencesPanel updatePanel = (UpdatePreferencesPanel)getPanelComponent();
		updatePanel.setApplication(_app);
		updatePanel.loadData(_app.getSquirrelPreferences());
	}

   public void uninitialize(IApplication app)
   {
      
   }

   public synchronized Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new UpdatePreferencesPanel();
		}
		return _myPanel;
	}

	public void applyChanges()
	{
		_myPanel.applyChanges(_app.getSquirrelPreferences());
	}

	public String getTitle()
	{
		return i18n.TITLE;
	}

	public String getHint()
	{
		return i18n.HINT;
	}


}
