package net.sourceforge.squirrel_sql.plugins.laf;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.util.Iterator;

import javax.swing.JCheckBox;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

public class TonicLookAndFeelController extends DefaultLookAndFeelController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TonicLookAndFeelController.class);


	
	private static final ILogger s_log =
		LoggerController.createLogger(TonicLookAndFeelController.class);

	
	public static final String TONIC_LAF_CLASS_NAME =
		"com.digitprop.tonic.TonicLookAndFeel";

	
	private TonicPreferences _prefs;

	
	TonicLookAndFeelController(LAFPlugin plugin) throws IOException
	{
		super();

		XMLObjectCache cache = plugin.getSettingsCache();
		Iterator<?> it = cache.getAllForClass(TonicPreferences.class);
		if (it.hasNext())
		{
			_prefs = (TonicPreferences)it.next();
		}
		else
		{
			_prefs = new TonicPreferences();
			try
			{
				cache.add(_prefs);
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("TonicPreferences object already in XMLObjectCache", ex);
			}
		}
	}

	
	public void aboutToBeInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
	}

	
	public void hasBeenInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
		UIManager.getDefaults().put(
				"TabbedPane.thickBorders",
				Boolean.valueOf(_prefs.getUseTabbedPaneThickBorders()));
	}

	
	public BaseLAFPreferencesPanelComponent getPreferencesComponent()
	{
		return new TonicPrefsPanel(this);
	}

	private static final class TonicPrefsPanel extends BaseLAFPreferencesPanelComponent
	{
        private static final long serialVersionUID = 1L;

        
		interface SkinPrefsPanelI18n
		{
			
			
			
			
		}

		private TonicLookAndFeelController _ctrl;
		private JCheckBox _useThickBordersChk = new JCheckBox(s_stringMgr.getString("laf.tonicUseThickBorders"));

		TonicPrefsPanel(TonicLookAndFeelController ctrl)
		{
			super(new GridBagLayout());
			_ctrl = ctrl;
			createUserInterface();
		}

		private void createUserInterface()
		{
			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(_useThickBordersChk, gbc);
		}
	
		
		public void loadPreferencesPanel()
		{
			super.loadPreferencesPanel();
			_useThickBordersChk.setSelected(_ctrl._prefs.getUseTabbedPaneThickBorders());
		}

		
		public boolean applyChanges()
		{
			super.applyChanges();
			_ctrl._prefs.setUseTabbedPaneThickBorders(_useThickBordersChk.isSelected());
			
			
			
			return true;
		}
	}

	public static final class TonicPreferences implements IHasIdentifier
	{
		private boolean _useTabbedPaneThickBorders = false;
		private IntegerIdentifier _id = new IntegerIdentifier(1);

		public boolean getUseTabbedPaneThickBorders()
		{
			return _useTabbedPaneThickBorders;
		}

		public void setUseTabbedPaneThickBorders(boolean value)
		{
			_useTabbedPaneThickBorders = value;
		}

		
		public IIdentifier getIdentifier()
		{
			return _id;
		}
	}
}

