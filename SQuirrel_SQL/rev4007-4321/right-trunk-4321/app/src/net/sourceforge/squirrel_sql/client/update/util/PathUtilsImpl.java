
package net.sourceforge.squirrel_sql.client.update.util;


public class PathUtilsImpl implements PathUtils
{
	
	public String buildPath(boolean prependSlash, String... pathElements)
	{
		StringBuilder result = new StringBuilder("/");

		for (String pathElement : pathElements)
		{
			result.append(pathElement).append("/");
		}

		String retVal = result.toString().replace("//", "/").replace("//", "/");

		if (retVal.endsWith("/"))
		{
			retVal = retVal.substring(0, retVal.length() - 1);
		}

		return retVal;
	}

	
	public String getFileFromPath(String path)
	{
		int slashIndex = path.lastIndexOf("/");
		if (path.indexOf("/") == -1) { return path; }
		return path.substring(slashIndex);
	}

}
