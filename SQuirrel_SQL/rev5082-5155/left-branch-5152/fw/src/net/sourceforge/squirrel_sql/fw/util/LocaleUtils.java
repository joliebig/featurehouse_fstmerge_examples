
package net.sourceforge.squirrel_sql.fw.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;


public class LocaleUtils
{

	
	public static Locale[] getAvailableLocales() {
      Locale[] availableLocales = Locale.getAvailableLocales();

      Arrays.sort(availableLocales, new Comparator<Locale>()
      {
         public int compare(Locale o1, Locale o2)
         {
            return o1.toString().compareTo(o2.toString());
         }
      });
      return availableLocales;
	}
	
	
	public static String[] getAvailableLocaleStrings() {
		Locale[] availableLocales = getAvailableLocales();
		String[] result = new String[availableLocales.length];
		for (int i = 0; i < availableLocales.length; i++) {
			result[i] = availableLocales[i].toString();
		}
		return result;
	}
}
