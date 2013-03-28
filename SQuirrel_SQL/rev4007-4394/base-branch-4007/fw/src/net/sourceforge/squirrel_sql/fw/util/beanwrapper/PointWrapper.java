package net.sourceforge.squirrel_sql.fw.util.beanwrapper;

import java.awt.Point;

public class PointWrapper
{
	public interface IPropertyNames
	{
		String X = "x";
		String Y = "y";
	}

	private int _x;
	private int _y;

	public PointWrapper()
	{
		this(null);
	}

	public PointWrapper(Point pt)
	{
		super();
		setFrom(pt);
	}

	public int getX()
	{
		return _x;
	}

	public void setX(int value)
	{
		_x = value;
	}

	public int getY()
	{
		return _y;
	}

	public void setY(int value)
	{
		_y = value;
	}

	public Point createPoint()
	{
		return new Point(_x, _y);
	}

	public void setFrom(Point pt)
	{
		if (pt != null)
		{
			_x = pt.x;
			_y = pt.y;
		}
	}
}