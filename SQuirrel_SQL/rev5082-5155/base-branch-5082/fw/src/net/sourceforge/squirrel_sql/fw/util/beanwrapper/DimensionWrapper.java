package net.sourceforge.squirrel_sql.fw.util.beanwrapper;

import java.awt.Dimension;

public class DimensionWrapper
{
	
	public interface IPropertyNames
	{
		
		String WIDTH = "width";

		
		String HEIGHT = "height";
	}

	
	private int _width;

	
	private int _height;

	
	public DimensionWrapper()
	{
		this(null);
	}

	
	public DimensionWrapper(Dimension dm)
	{
		super();
		setFrom(dm);
	}

	public int getWidth()
	{
		return _width;
	}

	public void setWidth(int value)
	{
		_width = value;
	}

	public int getHeight()
	{
		return _height;
	}

	public void setHeight(int value)
	{
		_height = value;
	}

	public Dimension createDimension()
	{
		return new Dimension(_width, _height);
	}

	public void setFrom(Dimension dm)
	{
		if (dm != null)
		{
			_width = (int)dm.getWidth();
			_height = (int)dm.getHeight();
		}
	}
}
