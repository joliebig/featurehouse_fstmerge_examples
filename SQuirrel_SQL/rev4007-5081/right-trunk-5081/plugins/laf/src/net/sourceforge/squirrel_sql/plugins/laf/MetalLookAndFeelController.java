package net.sourceforge.squirrel_sql.plugins.laf;

import java.util.Iterator;
import java.util.Vector;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;


class MetalLookAndFeelController extends AbstractPlasticController
{
	
	private static ILogger s_log =
		LoggerController.createLogger(MetalLookAndFeelController.class);

	static final String METAL_LAF_CLASS_NAME = MetalLookAndFeel.class.getName();

	private String[] _extraThemeClassNames = new String[0];



	
	private MetalThemePreferences _currentThemePrefs;

	private MetalTheme _defaultMetalTheme;
   private static final String DEFAULT_METAL_THEME = "javax.swing.plaf.metal.OceanTheme";
   private HashMap<String, MetalTheme> _themesByName = new HashMap<String, MetalTheme>();

   
	MetalLookAndFeelController(LAFPlugin plugin,
										LAFRegister lafRegister)
	{

      super(plugin, lafRegister);

      try
      {
         _extraThemeClassNames = new String[]
            {
               DEFAULT_METAL_THEME,
               
               
               "AquaTheme",
               "CharcoalTheme",
               "ContrastTheme",
               "EmeraldTheme",
               "RubyTheme",
               
               

               
               
               
               
               
               "net.sourceforge.squirrel_sql.client.gui.laf.AllBluesBoldMetalTheme",
               
               
            };

         _defaultMetalTheme = new DefaultMetalTheme();

         XMLObjectCache cache = plugin.getSettingsCache();
         Iterator<?> it = cache.getAllForClass(MetalThemePreferences.class);
         if (it.hasNext())
         {
            _currentThemePrefs = (MetalThemePreferences) it.next();
         }
         else
         {
            _currentThemePrefs = new MetalThemePreferences();

            ClassLoader cl = getLAFRegister().getLookAndFeelClassLoader();
            Class<?> clazz = Class.forName(MetalLookAndFeelController.DEFAULT_METAL_THEME, false, cl);
            MetalTheme theme = (MetalTheme) clazz.newInstance();
            _currentThemePrefs.setThemeName(theme.getName());

            try
            {
               cache.add(_currentThemePrefs);
            }
            catch (DuplicateObjectException ex)
            {
               s_log.error("MetalThemePreferences object already in XMLObjectCache", ex);
            }
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
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

            MetalTheme metalTheme = (MetalTheme) clazz.newInstance();
            _themesByName.put(metalTheme.getName(), metalTheme);
            ret.add(metalTheme);

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


   MetalTheme getThemeForName(String name)
   {
      MetalTheme ret = super.getThemeForName(name);

      if(null == ret)
      {
         ret = _themesByName.get(name);
      }

      return ret;
   }


	void installCurrentTheme(LookAndFeel laf, MetalTheme theme)
	{
		
		
		
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		MetalLookAndFeel.setCurrentTheme(theme);
	}

	
	String getCurrentThemeName()
	{
		return _currentThemePrefs.getThemeName();
	}

	
	void setCurrentThemeName(String name)
	{
		_currentThemePrefs.setThemeName(name);
	}


	
	public static final class MetalThemePreferences
		extends AbstractPlasticController.ThemePreferences
	{
	}
}

