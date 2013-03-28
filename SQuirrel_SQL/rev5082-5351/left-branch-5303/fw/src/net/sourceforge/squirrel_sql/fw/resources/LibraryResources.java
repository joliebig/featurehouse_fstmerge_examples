package net.sourceforge.squirrel_sql.fw.resources;

import net.sourceforge.squirrel_sql.fw.util.Resources;

public class LibraryResources extends Resources
{
   public interface IImageNames
   {
      String TABLE_ASCENDING = "table.ascending";
      String TABLE_DESCENDING = "table.descending";
      String OPEN = "open";
   }

	public LibraryResources() throws IllegalArgumentException
	{
		super(LibraryResources.class.getName(),
				LibraryResources.class.getClassLoader());
	}
}
