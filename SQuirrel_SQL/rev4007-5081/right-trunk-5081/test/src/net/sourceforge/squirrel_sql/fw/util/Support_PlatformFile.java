

package net.sourceforge.squirrel_sql.fw.util;


public class Support_PlatformFile {

	private static String platformId = null;

	public static String getNewPlatformFile(String pre, String post) {
		if (platformId == null) {
			String property = System.getProperty("com.ibm.oti.configuration");
			if (property == null) {
				property = "JDK";
			}
			platformId = property
					+ System.getProperty("java.vm.version").replace('.', '-');
		}
		return pre + platformId + post;
	}

}
