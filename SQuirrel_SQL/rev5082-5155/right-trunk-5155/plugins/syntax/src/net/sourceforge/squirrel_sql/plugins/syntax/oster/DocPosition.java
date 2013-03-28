package net.sourceforge.squirrel_sql.plugins.syntax.oster;



class DocPosition
{
	
	private int position;

	
	int getPosition()
	{
		return position;
	}

	
	public DocPosition(int position)
	{
		this.position = position;
	}

	
	public DocPosition adjustPosition(int adjustment)
	{
		position += adjustment;
		return this;
	}

	
	public boolean equals(Object obj)
	{
		if (obj instanceof DocPosition)
		{
			DocPosition d = (DocPosition) (obj);
			if (this.position == d.position)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	
	public String toString()
	{
		return "" + position;
	}
}
