package net.sourceforge.squirrel_sql.client;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class Version
{
	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(Version.class);

	private static final String APP_NAME = s_stringMgr.getString("Version.appname");
	private static final int MAJOR_VERSION = 2;
	private static final int MINOR_VERSION = 6;
	private static final int RELEASE = 6;
        private static final String BUGFIX_VERSION=null;

	private static final String COPYRIGHT = s_stringMgr.getString("Version.copyright");

	private static final String WEB_SITE = s_stringMgr.getString("Version.website");

	public static String getApplicationName()
	{
		return APP_NAME;
	}

	public static String getShortVersion()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(MAJOR_VERSION)
			.append(".")
			.append(MINOR_VERSION);
			
		if (RELEASE != 0)
		{
            buf.append(".");
			buf.append(RELEASE);
		}
                if (BUGFIX_VERSION != null) {
		buf.append(BUGFIX_VERSION);
			buf.append(BUGFIX_VERSION);
		}
		return buf.toString();
	}

	public static String getVersion()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(APP_NAME)
			.append(" Version ")
			.append(getShortVersion());
		return buf.toString();
	}

	public static String getCopyrightStatement()
	{
		return COPYRIGHT;
	}

	public static String getWebSite()
	{
		return WEB_SITE;
	}

   public static boolean supportsUsedJDK()
   {
      String vmVer = System.getProperty("java.vm.version");

      if(   vmVer.startsWith("0")
         || vmVer.startsWith("1.0")
         || vmVer.startsWith("1.1")
         || vmVer.startsWith("1.2")
         || vmVer.startsWith("1.3")
         || vmVer.startsWith("1.4"))
      {
         return false;
      }
      else
      {
         return true;
      }
   }

   public static String getUnsupportedJDKMessage()
   {
      String[] params = new String[]
         {
            System.getProperty("java.vm.version"),
            System.getProperty("java.home")
         };

      return s_stringMgr.getString("Application.error.unsupportedJDKVersion", params);
   }

   public static boolean isJDK14()
   {
      String vmVer = System.getProperty("java.vm.version");

      if(vmVer.startsWith("1.4"))
      {
         return true;
      }
      else
      {
         return false;
      }
   }
   
   public static boolean isJDK16OrAbove()
   {
      String vmVer = System.getProperty("java.vm.version").substring(0, 3);

      if(vmVer.compareTo("1.6") >= 0)
      {
         return true;
      }
      else
      {
         return false;
      }
   }

}
