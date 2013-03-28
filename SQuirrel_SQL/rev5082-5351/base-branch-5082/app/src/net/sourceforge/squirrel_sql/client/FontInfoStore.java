package net.sourceforge.squirrel_sql.client;

import java.awt.Font;

import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.fw.gui.FontInfo;

public class FontInfoStore
{
	
	private final FontInfo _defaultFontInfo = new FontInfo();

	
	private FontInfo _statusBarFontInfo;

	
	public FontInfoStore()
	{
	    Font tmp = (Font)UIManager.get("Label.font");
	    if (tmp != null) {
	        Font font = tmp.deriveFont(10.0f);
	        _statusBarFontInfo = new FontInfo(font);	        
	    }
	}

	
	public FontInfo getStatusBarFontInfo()
	{
		return _statusBarFontInfo != null ? _statusBarFontInfo : _defaultFontInfo;
	}

	
	public void setStatusBarFontInfo(FontInfo fi)
	{
		_statusBarFontInfo = fi;
	}
}

