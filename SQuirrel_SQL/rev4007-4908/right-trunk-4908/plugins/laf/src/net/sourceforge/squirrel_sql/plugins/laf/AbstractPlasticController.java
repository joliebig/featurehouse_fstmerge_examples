package net.sourceforge.squirrel_sql.plugins.laf;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.LookAndFeel;
import javax.swing.SwingConstants;
import javax.swing.plaf.metal.MetalTheme;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifier;
import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

abstract class AbstractPlasticController extends DefaultLookAndFeelController
{
	
	private static ILogger s_log =
		LoggerController.createLogger(AbstractPlasticController.class);

   
	protected interface i18n
	{
		String THEME = "Theme:";
	}

	
	protected static final String[] PLASTIC_THEME_CLASS_NAMES = new String[]
	{
		"com.jgoodies.looks.plastic.theme.BrownSugar",
		"com.jgoodies.looks.plastic.theme.DarkStar",
		"com.jgoodies.looks.plastic.theme.DesertBlue",
		"com.jgoodies.looks.plastic.theme.DesertBluer",
		"com.jgoodies.looks.plastic.theme.DesertGreen",
		"com.jgoodies.looks.plastic.theme.DesertRed",
		"com.jgoodies.looks.plastic.theme.DesertYellow",
		"com.jgoodies.looks.plastic.theme.ExperienceBlue",
		"com.jgoodies.looks.plastic.theme.ExperienceGreen",
        "com.jgoodies.looks.plastic.theme.ExperienceRoyale",
        "com.jgoodies.looks.plastic.theme.LightGray",
		"com.jgoodies.looks.plastic.theme.Silver",
		"com.jgoodies.looks.plastic.theme.SkyBlue",
		"com.jgoodies.looks.plastic.theme.SkyBluer",
		"com.jgoodies.looks.plastic.theme.SkyGreen",
		"com.jgoodies.looks.plastic.theme.SkyKrupp",
		"com.jgoodies.looks.plastic.theme.SkyPink",
		"com.jgoodies.looks.plastic.theme.SkyRed",
		"com.jgoodies.looks.plastic.theme.SkyYellow",
	};

   public static final String DEFAULT_PLASTIC_THEME_CLASS_NAME = PLASTIC_THEME_CLASS_NAMES[11]; 

	
	private final LAFPlugin _lafPlugin;

	
	private final LAFRegister _lafRegister;

	
	private Map<String, MetalTheme> _themes = new TreeMap<String, MetalTheme>();

	
	AbstractPlasticController(LAFPlugin plugin,
								LAFRegister lafRegister)
	{
		super();
		_lafPlugin = plugin;
		_lafRegister = lafRegister;
	}

	
	public void initialize()
	{
		_themes.clear();

		ClassLoader cl = getLAFRegister().getLookAndFeelClassLoader();
		MetalTheme[] extras = getExtraThemes();
		if (extras == null)
		{
			extras = new MetalTheme[0];
		}
		
		for (int i = 0; i < extras.length; ++i)
		{
			_themes.put(extras[i].getName(), extras[i]);
		}

		for (int i = 0; i < PLASTIC_THEME_CLASS_NAMES.length; ++i)
		{
			try
			{
				
				Class<?> clazz = 
					Class.forName(PLASTIC_THEME_CLASS_NAMES[i], false, cl);
				MetalTheme theme = (MetalTheme)clazz.newInstance();
				_themes.put(theme.getName(), theme); 
			}
			catch (Throwable th)
			{
				s_log.error("Error loading theme " + PLASTIC_THEME_CLASS_NAMES[i], th);
			}
		}
	}

	
	public void aboutToBeInstalled(LAFRegister lafRegister, LookAndFeel laf)
	{
		final String name = getCurrentThemeName();
		if (name != null)
		{
			MetalTheme theme = getThemeForName(name);
			if (theme != null)
			{
				try
				{
					installCurrentTheme(laf, theme);
				}
				catch (BaseException ex)
				{
					s_log.error("Error installing a Theme", ex);
				}
			}
		}
	}

	
	public BaseLAFPreferencesPanelComponent getPreferencesComponent()
	{
		return new PrefsPanel(this);
	}


	
	abstract String getCurrentThemeName();

	
	abstract void setCurrentThemeName(String name);

	
	abstract void installCurrentTheme(LookAndFeel laf, MetalTheme theme)
			throws BaseException;

	final MetalTheme getThemeForName(String name)
	{
		return _themes.get(name);
	}

	
	MetalTheme[] getExtraThemes()
	{
		return new MetalTheme[0];
	}

	
	LAFRegister getLAFRegister()
	{
		return _lafRegister;
	}

	
	LAFPlugin getLAFPlugin()
	{
		return _lafPlugin;
	}

	Iterator<MetalTheme> themesIterator()
	{
		return _themes.values().iterator();
	}

	
	final static class PrefsPanel extends BaseLAFPreferencesPanelComponent
	{
        private static final long serialVersionUID = 1L;
        private AbstractPlasticController _ctrl;
		private JComboBox _themeCmb;
		private int _origSelThemeIdx;

		PrefsPanel(AbstractPlasticController ctrl)
		{
			super();
			_ctrl = ctrl;
			createUserInterface();
		}

		private void createUserInterface()
		{
			setLayout(new GridBagLayout());
			_themeCmb = new JComboBox();

			final GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.WEST;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.insets = new Insets(4, 4, 4, 4);

			gbc.gridx = 0;
			gbc.gridy = 0;
			add(new JLabel(i18n.THEME, SwingConstants.RIGHT), gbc);

			++gbc.gridx;
			add(_themeCmb, gbc);
		}

		
		public void loadPreferencesPanel()
		{
			super.loadPreferencesPanel();
			loadThemesCombo();
		}

		
		public boolean applyChanges()
		{
			super.applyChanges();
			if (_origSelThemeIdx != _themeCmb.getSelectedIndex())
			{
				_ctrl.setCurrentThemeName((String)_themeCmb.getSelectedItem());
				return true;
			}
			return false;
		}

		private void loadThemesCombo()
		{
			_themeCmb.removeAllItems();

			for(Iterator<MetalTheme> it = _ctrl.themesIterator(); it.hasNext();)
			{
				_themeCmb.addItem((it.next()).getName());
			}
	
			if (_themeCmb.getModel().getSize() > 0)
			{
				String selThemeName = _ctrl.getCurrentThemeName();
				if (selThemeName != null && selThemeName.length() > 0)
				{
					_themeCmb.setSelectedItem(selThemeName);
				}
				if (_themeCmb.getSelectedIndex() == -1)
				{
					_themeCmb.setSelectedIndex(0);
				}
			}
			_origSelThemeIdx = _themeCmb.getSelectedIndex();
		}
	}

	public static abstract class ThemePreferences implements IHasIdentifier
	{
		private String _themeName;
		private IntegerIdentifier _id = new IntegerIdentifier(1);

		public String getThemeName()
		{
			return _themeName;
		}

		public void setThemeName(String value)
		{
			_themeName = value;
		}

		
		public IIdentifier getIdentifier()
		{
			return _id;
		}
	}
}
