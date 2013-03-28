package net.sourceforge.squirrel_sql.fw.util;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.io.*;
import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utilities
{
   
   private static ILogger s_log =
      LoggerController.createLogger(Utilities.class);

    private static Pattern spanStartPattern = Pattern.compile(".*\\<span\\>.*");
    private static Pattern spanStartSplitPattern = Pattern.compile("\\<span\\>");
    private static Pattern spanEndPattern = Pattern.compile(".*<\\/span\\>.*");
    private static Pattern spanEndSplitPattern = Pattern.compile("<\\/span\\>");
	
   
   
   
   private Utilities()
   {
      super();
   }

   
   public static void printStackTrace(PrintStream ps)
   {
      if (ps == null)
      {
         throw new IllegalArgumentException("PrintStream == null");
      }

      try
      {
         throw new Exception();
      }
      catch (Exception ex)
      {
         ps.println(getStackTrace(ex));
      }
   }

   
   public static String getStackTrace(Throwable th)
   {
      if (th == null)
      {
         throw new IllegalArgumentException("Throwable == null");
      }

      StringWriter sw = new StringWriter();
      try
      {
         PrintWriter pw = new PrintWriter(sw);
         try
         {
            th.printStackTrace(pw);
            return sw.toString();
         }
         finally
         {
            pw.close();
         }
      }
      finally
      {
         try
         {
            sw.close();
         }
         catch (IOException ex)
         {
            s_log.error("Unexpected error closing StringWriter", ex);
         }
      }
   }

   public static Throwable getDeepestThrowable(Throwable t)
   {
      Throwable parent = t;
      Throwable child = t.getCause();
      while(null != child)
      {
         parent = child;
         child = parent.getCause();
      }

      return parent;

   }

   
   public static String changeClassNameToFileName(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("Class Name == null");
      }
      return name.replace('.', '/').concat(".class");
   }

   
   public static String changeFileNameToClassName(String name)
   {
      if (name == null)
      {
         throw new IllegalArgumentException("File Name == null");
      }
      String className = null;
      if (name.toLowerCase().endsWith(".class"))
      {
         className = name.replace('/', '.');
         className = className.replace('\\', '.');
         className = className.substring(0, className.length() - 6);
      }
      return className;
   }

   
   public static String cleanString(String str)
   {
      return StringUtilities.cleanString(str);
   }

   
   public static boolean areStringsEqual(String s1, String s2)
   {
      return StringUtilities.areStringsEqual(s1, s2);
   }

   
   public static String getFileNameSuffix(String fileName)
   {
      if (fileName == null)
      {
         throw new IllegalArgumentException("file name == null");
      }
      int pos = fileName.lastIndexOf('.');
      if (pos > 0 && pos < fileName.length() - 1)
      {
         return fileName.substring(pos + 1);
      }
      return "";
   }


   
   public static String removeFileNameSuffix(String fileName)
   {
      if (fileName == null)
      {
         throw new IllegalArgumentException("file name == null");
      }
      int pos = fileName.lastIndexOf('.');
      if (pos > 0 && pos < fileName.length() - 1)
      {
         return fileName.substring(0, pos);
      }
      return fileName;
   }

   
   public static boolean isStringEmpty(String str)
   {
      return StringUtilities.isEmpty(str);
   }

   public static String formatSize(long longSize)
   {
      return formatSize(longSize, -1);
   }

   
   public static String formatSize(long longSize, int decimalPos)
   {
      NumberFormat fmt = NumberFormat.getNumberInstance();
      if (decimalPos >= 0)
      {
         fmt.setMaximumFractionDigits(decimalPos);
      }
      final double size = longSize;
      double val = size / (1024 * 1024);
      if (val > 1)
      {
         return fmt.format(val).concat(" MB");
      }
      val = size / 1024;
      if (val > 10)
      {
         return fmt.format(val).concat(" KB");
      }
      return fmt.format(val).concat(" bytes");
   }

   
   public static String[] splitString(String str, char delimiter)
   {
      return StringUtilities.split(str, delimiter);
   }

   
   public static String[] splitString(String str, char delimiter,
                                      boolean removeEmpty)
   {
      return StringUtilities.split(str, delimiter, removeEmpty);
   }

   
   public static Object cloneObject(Object toClone, final ClassLoader classLoader)
   {
      if(null == toClone)
      {
         return null;
      }
      else
      {
         try
         {
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ObjectOutputStream oOut = new ObjectOutputStream(bOut);
            oOut.writeObject(toClone);
            oOut.close();
            ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
            bOut.close();
            ObjectInputStream oIn = new ObjectInputStream(bIn)
            {
               protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException
               {
            	   return Class.forName(desc.getName(), false, classLoader);
               }
            };
            bIn.close();
            Object copy = oIn.readObject();
            oIn.close();

            return copy;
         }
         catch (Exception e)
         {
            throw new RuntimeException(e);
         }

      }
   }

   
   public static String replaceI18NSpanLine(String line, 
           StringManager s_stringMgr) {
       String result = line;
       Matcher start = spanStartPattern.matcher(line);
       Matcher end = spanEndPattern.matcher(line);
       if (start.matches() && end.matches()) {
           
           
           
           
           StringBuffer tmp = new StringBuffer();
           String[] startparts = spanStartSplitPattern.split(line);

           tmp.append(startparts[0]);

           
           String[] endparts = spanEndSplitPattern.split(startparts[1]);

           String key = endparts[0];

           String value = s_stringMgr.getString(key);
           tmp.append(value);
           tmp.append(endparts[1]);

           result = tmp.toString();
       }
       return result;
   }
   
   
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object x) {
	    return (T) x;
	}

	
	public static void checkNull(String methodName, Object... arguments) {
		if (arguments.length % 2 != 0) {
			throw new IllegalArgumentException("Args must be specified in name/value pairs"); 
		}
		for (int i = 0; i < arguments.length-1; i+=2) {
			String name = (String)arguments[i];
			Object value = arguments[i+1];
			if (value == null) {
				throw new IllegalArgumentException(methodName+": Argument "+name+" cannot be null");
			}
		}
	}
	
	
	public static void sleep(long millis)
	{
		if (millis == 0) {
			return;
		}
		try
		{
			Thread.sleep(millis);
		}
		catch (Exception e)
		{
			s_log.error(e);
		}
	}
	
	
	public static void garbageCollect() {
		System.gc();
	}
	
}
