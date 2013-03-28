
package net.sourceforge.squirrel_sql.client.plugin;

import java.net.URL;
import java.util.Comparator;


public class PluginLoadOrderComparator implements Comparator<URL>
{

	
	public int compare(URL plugin1, URL plugin2)
	{
		if (plugin1.toString().endsWith("refactoring.jar")) {
			return 1;
		}
		if (plugin2.toString().endsWith("refactoring.jar")) {
			return -1;
		}
		return 0;
	}

}
