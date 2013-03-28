package net.sourceforge.squirrel_sql.plugins.syntax.oster;

import java.util.Comparator;

class DocPositionComparator implements Comparator<DocPosition>
{
	
	public boolean equals(Object obj)
	{
		if (obj instanceof DocPositionComparator)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	
	public int compare(DocPosition d1, DocPosition d2)
	{
	    return (d1.getPosition() - d2.getPosition());
	}
}
