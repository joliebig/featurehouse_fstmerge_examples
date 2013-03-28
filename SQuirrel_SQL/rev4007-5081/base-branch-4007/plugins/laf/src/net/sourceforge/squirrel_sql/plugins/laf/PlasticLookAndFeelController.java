package net.sourceforge.squirrel_sql.plugins.laf;

import java.util.Iterator;

import javax.swing.LookAndFeel;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.DuplicateObjectException;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.fw.xml.XMLObjectCache;

class PlasticLookAndFeelController extends AbstractPlasticController
{
	
	private static ILogger s_log =
		LoggerController.createLogger(PlasticLookAndFeelController.class);

	
	static final String[] LAF_CLASS_NAMES = new String[]
	{
		"com.jgoodies.looks.plastic.PlasticLookAndFeel",
		"com.jgoodies.looks.plastic.Plastic3DLookAndFeel",
		"com.jgoodies.looks.plastic.PlasticXPLookAndFeel",
	};

	public static final String DEFAULT_LOOK_AND_FEEL_CLASS_NAME = LAF_CLASS_NAMES[1];

	
	private static final String THEME_BASE_CLASS = "com.jgoodies.looks.plastic.PlasticTheme";

	
	private PlasticThemePreferences _prefs;

   
	PlasticLookAndFeelController(LAFPlugin plugin,
								LAFRegister lafRegister)
	{
      super(plugin, lafRegister);
      try
      {

         XMLObjectCache cache = plugin.getSettingsCache();
         Iterator<?> it = cache.getAllForClass(PlasticThemePreferences.class);
         if (it.hasNext())
         {
            _prefs = (PlasticThemePreferences) it.next();
         }
         else
         {
            _prefs = new PlasticThemePreferences();

            ClassLoader cl = getLAFRegister().getLookAndFeelClassLoader();
            Class<?> clazz = 
            	Class.forName(AbstractPlasticController.DEFAULT_PLASTIC_THEME_CLASS_NAME, false, cl);
            MetalTheme theme = (MetalTheme) clazz.newInstance();
            _prefs.setThemeName(theme.getName());

            try
            {
               cache.add(_prefs);
            }
            catch (DuplicateObjectException ex)
            {
               s_log.error("PlasticThemePreferences object already in XMLObjectCache", ex);
            }
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

	
	String getCurrentThemeName()
	{
		return _prefs.getThemeName();
	}

	
	void setCurrentThemeName(String name)
	{
		_prefs.setThemeName(name);
	}

	void installCurrentTheme(LookAndFeel laf, MetalTheme theme)
		throws BaseException
	{
		try
		{
			ClassLoader cl = getLAFRegister().getLookAndFeelClassLoader();
			Class<?> themeBaseClass;
			try
			{
				themeBaseClass = Class.forName(THEME_BASE_CLASS, false, cl);
			}
			catch (Throwable th)
			{
				s_log.error("Error loading theme base class " + THEME_BASE_CLASS, th);
				throw new BaseException(th);
			}

			
			if (!themeBaseClass.isAssignableFrom(theme.getClass()))
			{
				throw new BaseException("NonPlastic Theme passed in");
			}


         












          MetalLookAndFeel.setCurrentTheme(theme);
      }
		catch (Throwable th)
		{
			throw new BaseException(th);
		}
	}

	
	public static final class PlasticThemePreferences
		extends AbstractPlasticController.ThemePreferences
	{
	}
}

