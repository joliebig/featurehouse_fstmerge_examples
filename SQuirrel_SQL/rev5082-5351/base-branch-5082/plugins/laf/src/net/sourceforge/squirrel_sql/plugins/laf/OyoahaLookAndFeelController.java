package net.sourceforge.squirrel_sql.plugins.laf;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;

import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.gui.DirectoryListComboBox;
import net.sourceforge.squirrel_sql.fw.gui.OutputLabel;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.FileExtensionFilter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

public class OyoahaLookAndFeelController extends DefaultLookAndFeelController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(OyoahaLookAndFeelController.class);


	
	private static ILogger s_log =
		LoggerController.createLogger(OyoahaLookAndFeelController.class);

	
	public static final String OA_LAF_CLASS_NAME =
		"com.oyoaha.swing.plaf.oyoaha.OyoahaLookAndFeel";

	
	private OyoahaPreferences _prefs;

	
	OyoahaLookAndFeelController(LAFPlugin plugin) throws IOException
	{
		super();

		XMLObjectCache cache = plugin.getSettingsCache();
		Iterator<?> it = cache.getAllForClass(OyoahaPreferences.class);
		if (it.hasNext())
		{
			_prefs = (OyoahaPreferences)it.next();
		}
		else
		{
			_prefs = new OyoahaPreferences();
			try
			{
				cache.add(_prefs);
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("OyoahaPreferences object already in XMLObjectCache", ex);
			}
		}

		
		File themePackDir = new File(plugin.getPluginAppSettingsFolder(), "oyoaha-theme-packs");
		_prefs.setThemePackDirectory(themePackDir.getAbsolutePath());
		if (!themePackDir.exists())
		{
			themePackDir.mkdirs();
		}
	}

	
	public void aboutToBeInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
		try
		{
			final String dir = _prefs.getThemePackDirectory();
			final String name = _prefs.getThemePackName();
			if (dir != null && name != null)
			{
				File themePackFile = new File(dir, name);
				if (themePackFile.exists())
				{
					ClassLoader cl = lafRegister.getLookAndFeelClassLoader();
					Class<?> oyLafClass = 
						Class.forName(OA_LAF_CLASS_NAME, false, cl);
					Method setTheme =
						oyLafClass.getMethod("setOyoahaTheme",
									new Class[] { File.class });
					Object[] parms = new Object[] { themePackFile };
					setTheme.invoke(laf, parms);
				}
			}
		}
		catch (Throwable th)
		{
			s_log.error("Error loading an Oyoaha theme", th);
		}

	}

	
	public void hasBeenInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
	}

	
	public BaseLAFPreferencesPanelComponent getPreferencesComponent()
	{
		return new OyoahaPrefsPanel(this);
	}

	private static final class OyoahaPrefsPanel extends BaseLAFPreferencesPanelComponent
	{
        private static final long serialVersionUID = 1L;

        
		interface OyoahaPrefsPanelI18n
		{
			
			String THEME_PACK = s_stringMgr.getString("laf.themePack");
			
			String THEMEPACK_LOC = s_stringMgr.getString("laf.themePacLoc");
		}

		private OyoahaLookAndFeelController _ctrl;
		private DirectoryListComboBox _themePackCmb = new DirectoryListComboBox();

		OyoahaPrefsPanel(OyoahaLookAndFeelController ctrl)
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
			add(new JLabel(OyoahaPrefsPanelI18n.THEME_PACK, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			add(_themePackCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			add(new JLabel(OyoahaPrefsPanelI18n.THEMEPACK_LOC, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			final String themePackDir = _ctrl._prefs.getThemePackDirectory();
			add(new OutputLabel(themePackDir), gbc);
		}
	
		
		public void loadPreferencesPanel()
		{
			super.loadPreferencesPanel();
			final String themePackDir = _ctrl._prefs.getThemePackDirectory();
			
			final FileExtensionFilter filter = new FileExtensionFilter(s_stringMgr.getString("laf.otmFiles"), new String[] {".otm"});
			_themePackCmb.load(new File(themePackDir), filter);
			_themePackCmb.setSelectedItem(_ctrl._prefs.getThemePackName());
			if (_themePackCmb.getSelectedIndex() == -1 &&
					_themePackCmb.getModel().getSize() > 0)
			{
				_themePackCmb.setSelectedIndex(0);
			}
		}

		
		public boolean applyChanges()
		{
			super.applyChanges();
			_ctrl._prefs.setThemePackName((String)_themePackCmb.getSelectedItem());
			return false;
		}
	}

	public static final class OyoahaPreferences implements IHasIdentifier
	{
		private String _themePackDir;
		private String _themePackName;
		private IntegerIdentifier _id = new IntegerIdentifier(1);

		public String getThemePackDirectory()
		{
			return _themePackDir;
		}

		public void setThemePackDirectory(String value)
		{
			_themePackDir = value;
		}

		public String getThemePackName()
		{
			return _themePackName;
		}

		public void setThemePackName(String value)
		{
			_themePackName = value;
		}

		
		public IIdentifier getIdentifier()
		{
			return _id;
		}
	}
}

