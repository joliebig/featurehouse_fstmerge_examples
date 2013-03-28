package net.sourceforge.squirrel_sql.plugins.syntax;

import java.awt.Color;
import java.io.Serializable;

public class SyntaxStyle implements Serializable
{
	private static final long serialVersionUID = 5071458594077779491L;

	
	private String _name;

	
	private boolean _isItalic = false;

	
	private boolean _isBold = false;

	private int _textRGB = Color.black.getRGB();

	private int _backgroundRGB = Color.white.getRGB();

	
	public SyntaxStyle()
	{
		super();
	}

	
	public SyntaxStyle(SyntaxStyle rhs)
	{
		super();
		setName(rhs.getName());
		setItalic(rhs.isItalic());
		setBold(rhs.isBold());
		setTextRGB(rhs.getTextRGB());
		setBackgroundRGB(rhs.getBackgroundRGB());
	}

	
	public String getName()
	{
		return _name;
	}

	
	public void setName(String value)
	{
		_name = value;
	}

	
	public boolean isItalic()
	{
		return _isItalic;
	}

	
	public void setItalic(boolean value)
	{
		_isItalic = value;
	}

	
	public boolean isBold()
	{
		return _isBold;
	}

	
	public void setBold(boolean value)
	{
		_isBold = value;
	}

	
	public int getTextRGB()
	{
		return _textRGB;
	}

	
	public void setTextRGB(int value)
	{
		_textRGB = value;
	}

	
	public int getBackgroundRGB()
	{
		return _backgroundRGB;
	}

	
	public void setBackgroundRGB(int value)
	{
		_backgroundRGB = value;
	}
}
