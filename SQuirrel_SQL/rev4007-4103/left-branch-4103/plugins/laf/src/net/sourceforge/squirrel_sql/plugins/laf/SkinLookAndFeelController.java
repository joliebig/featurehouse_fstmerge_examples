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

public class SkinLookAndFeelController extends DefaultLookAndFeelController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SkinLookAndFeelController.class);


	
	private static final ILogger s_log =
		LoggerController.createLogger(SkinLookAndFeelController.class);

	
	public static final String SKINNABLE_LAF_CLASS_NAME =
		"com.l2fprod.gui.plaf.skin.SkinLookAndFeel";

	
	public static final String SKIN_CLASS_NAME = "com.l2fprod.gui.plaf.skin.Skin";

	
	private SkinPreferences _prefs;

	
	SkinLookAndFeelController(LAFPlugin plugin) throws IOException
	{
		super();

		XMLObjectCache cache = plugin.getSettingsCache();
		Iterator<?> it = cache.getAllForClass(SkinPreferences.class);
		if (it.hasNext())
		{
			_prefs = (SkinPreferences)it.next();
		}
		else
		{
			_prefs = new SkinPreferences();
			try
			{
				cache.add(_prefs);
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("SkinPreferences object already in XMLObjectCache", ex);
			}
		}

		
		File themePackDir = new File(plugin.getPluginAppSettingsFolder(), "skinlf-theme-packs");
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
					Class<?> skinLafClass = 
						Class.forName(SKINNABLE_LAF_CLASS_NAME, false, cl);
					Class<?> skinClass = 
						Class.forName(SKIN_CLASS_NAME, false, cl);

					Method loadThemePack =
						skinLafClass.getMethod("loadThemePack",
											new Class[] { String.class });
					Method setSkin =
						skinLafClass.getMethod(
							"setSkin", new Class[] { skinClass });

					Object[] parms = new Object[] { dir + "/" + name };
					Object skin = loadThemePack.invoke(skinLafClass, parms);
					setSkin.invoke(skinLafClass, new Object[] { skin });
				}
			}
		}
		catch (Throwable th)
		{
			s_log.error("Error loading a Skinnable Look and Feel", th);
		}

	}

	
	public void hasBeenInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
	}

	
	public BaseLAFPreferencesPanelComponent getPreferencesComponent()
	{
		return new SkinPrefsPanel(this);
	}

	private static final class SkinPrefsPanel extends BaseLAFPreferencesPanelComponent
	{
        private static final long serialVersionUID = 1L;

        
		interface SkinPrefsPanelI18n
		{
			
			String THEME_PACK = s_stringMgr.getString("laf.skinThemPack");
			
			String THEMEPACK_LOC = s_stringMgr.getString("laf.skinThemePackDir");
		}

		private SkinLookAndFeelController _ctrl;
		private DirectoryListComboBox _themePackCmb = new DirectoryListComboBox();

		SkinPrefsPanel(SkinLookAndFeelController ctrl)
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
			add(new JLabel(SkinPrefsPanelI18n.THEME_PACK, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			add(_themePackCmb, gbc);

			gbc.gridx = 0;
			++gbc.gridy;
			add(new JLabel(SkinPrefsPanelI18n.THEMEPACK_LOC, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			final String themePackDir = _ctrl._prefs.getThemePackDirectory();
			add(new OutputLabel(themePackDir), gbc);
		}
	
		
		public void loadPreferencesPanel()
		{
			super.loadPreferencesPanel();
			final String themePackDir = _ctrl._prefs.getThemePackDirectory();
			
			final FileExtensionFilter filter = new FileExtensionFilter(s_stringMgr.getString("laf.jarZip"), new String[] { ".jar", ".zip" });
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
			
			
			
			return true;
		}
	}

	public static final class SkinPreferences implements IHasIdentifier
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

