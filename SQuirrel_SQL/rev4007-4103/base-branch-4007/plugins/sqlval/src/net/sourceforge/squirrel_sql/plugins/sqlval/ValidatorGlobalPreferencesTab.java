package net.sourceforge.squirrel_sql.plugins.sqlval;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;

public class ValidatorGlobalPreferencesTab implements IGlobalPreferencesPanel
{
	
	private final static ILogger s_log =
		LoggerController.createLogger(ValidatorGlobalPreferencesTab.class);

    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(ValidatorGlobalPreferencesTab.class);
    
	
	private final WebServicePreferences _prefs;

	
	private PrefsPanel _myPanel;

	
	private IApplication _app;
    
	
	public ValidatorGlobalPreferencesTab(WebServicePreferences prefs)
	{
		super();
		if (prefs == null)
		{
			throw new IllegalArgumentException("WebServicePreferences == null");
		}
		_prefs = prefs;
	}

	
	public void initialize(IApplication app)
	{
		if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		_app = app;
		((PrefsPanel)getPanelComponent()).loadData();
	}

   public void uninitialize(IApplication app)
   {
      
   }

   
	public synchronized Component getPanelComponent()
	{
		if (_myPanel == null)
		{
			_myPanel = new PrefsPanel(_prefs);
		}
		return _myPanel;
	}

	
	public void applyChanges()
	{
		_myPanel.save();
	}

	
	public String getTitle()
	{
        
		return s_stringMgr.getString("ValidatorGlobalPreferencesTab.title");
	}

	
	public String getHint()
	{
        
		return s_stringMgr.getString("ValidatorGlobalPreferencesTab.hint");
	}

	static final class PrefsPanel extends JPanel
	{
		private AppPreferencesPanel _appPrefsPnl;

		private final WebServicePreferences _prefs;

		PrefsPanel(WebServicePreferences prefs)
		{
			super(new GridBagLayout());
			_prefs = prefs;
			createGUI(prefs);
		}

		private void loadData()
		{
			_appPrefsPnl.loadData();
		}

		private void save()
		{
			_appPrefsPnl.save();
		}

		private void createGUI(WebServicePreferences prefs)
		{
			_appPrefsPnl = new AppPreferencesPanel(prefs);

			setBorder(BorderFactory.createEmptyBorder());

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(1, 1, 1, 1);
			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 1;
			add(_appPrefsPnl, gbc);
		}
	}
}

