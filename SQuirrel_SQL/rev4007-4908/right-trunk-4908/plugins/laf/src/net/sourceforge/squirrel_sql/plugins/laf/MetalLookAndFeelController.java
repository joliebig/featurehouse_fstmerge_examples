package net.sourceforge.squirrel_sql.plugins.laf;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;
import net.sourceforge.squirrel_sql.client.Version;


class MetalLookAndFeelController extends AbstractPlasticController
{
	
	private static ILogger s_log =
		LoggerController.createLogger(MetalLookAndFeelController.class);

	static final String METAL_LAF_CLASS_NAME = MetalLookAndFeel.class.getName();

	private String[] _extraThemeClassNames = new String[0];



	
	private MetalThemePreferences _prefs;

	private MetalTheme _defaultMetalTheme;

	
	MetalLookAndFeelController(LAFPlugin plugin,
										LAFRegister lafRegister)
	{

		super(plugin, lafRegister);
		if(Version.isJDK14())
		{
			_extraThemeClassNames = new String[]
			{
				
				
				"AquaTheme",
				"CharcoalTheme",
				"ContrastTheme",
				"EmeraldTheme",
				"RubyTheme",
				
				

				
				
				
				
				
				"net.sourceforge.squirrel_sql.client.gui.laf.AllBluesBoldMetalTheme"
				
				
			};
		}
		else
		{
			_extraThemeClassNames = new String[]
			{
				"javax.swing.plaf.metal.OceanTheme", 
				
				
				"AquaTheme",
				"CharcoalTheme",
				"ContrastTheme",
				"EmeraldTheme",
				"RubyTheme",
				
				

				
				
				
				
				
				"net.sourceforge.squirrel_sql.client.gui.laf.AllBluesBoldMetalTheme"
				
				


			};
		}

		_defaultMetalTheme = new DefaultMetalTheme();

		XMLObjectCache cache = plugin.getSettingsCache();
		Iterator<?> it = 
            cache.getAllForClass(MetalThemePreferences.class);
		if (it.hasNext())
		{
			_prefs = (MetalThemePreferences)it.next();
		}
		else
		{
			_prefs = new MetalThemePreferences();
			try
			{
				cache.add(_prefs);
			}
			catch (DuplicateObjectException ex)
			{
				s_log.error("MetalThemePreferences object already in XMLObjectCache", ex);
			}
		}
	}

	
	MetalTheme[] getExtraThemes()
	{
		ClassLoader cl = getLAFRegister().getLookAndFeelClassLoader();

		Vector<MetalTheme> ret = new Vector<MetalTheme>();

		boolean defaultThemeIsIncluded = false;

		for (int i = 0; i < _extraThemeClassNames.length; ++i)
		{
			try
			{
				Class<?> clazz = Class.forName(_extraThemeClassNames[i], false, cl);
				ret.add((MetalTheme)clazz.newInstance());

				if(null != _defaultMetalTheme && _extraThemeClassNames[i].equals(_defaultMetalTheme.getClass().getName()))
				{
					defaultThemeIsIncluded = true;
				}
			}
			catch (Throwable th)
			{
				s_log.error("Error loading theme " + _extraThemeClassNames[i], th);
			}
		}

		if(false == defaultThemeIsIncluded)
		{
			ret.add(_defaultMetalTheme);
		}


		return ret.toArray(new MetalTheme[ret.size()]);
	}

	void installCurrentTheme(LookAndFeel laf, MetalTheme theme)
	{
		
		
		
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		MetalLookAndFeel.setCurrentTheme(theme);
	}

	
	String getCurrentThemeName()
	{
		return _prefs.getThemeName();
	}

	
	void setCurrentThemeName(String name)
	{
		_prefs.setThemeName(name);
	}


	
	public static final class MetalThemePreferences
		extends AbstractPlasticController.ThemePreferences
	{
	}
}

