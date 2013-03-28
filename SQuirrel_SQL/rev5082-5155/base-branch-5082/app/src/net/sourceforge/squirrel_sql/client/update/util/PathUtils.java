
package net.sourceforge.squirrel_sql.client.update.util;

public interface PathUtils
{

	
	public abstract String buildPath(boolean prependSlash, String... pathElements);

	
	public abstract String getFileFromPath(String path);

}