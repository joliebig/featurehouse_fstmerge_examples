package net.sourceforge.squirrel_sql.plugins.laf;

import java.io.Serializable;
import javax.swing.UIManager;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

public class LAFPreferences implements Cloneable, Serializable, IHasIdentifier
{
    static final long serialVersionUID = 5458252097202539743L;
    
    
	private IIdentifier _id;
	private String _lafClassName;
	private FontInfo _fiMenu;
	private FontInfo _fiStatic;
	private FontInfo _fiStatusBar;
	private FontInfo _fiOther;
	private boolean _fiMenuEnabled;
	private boolean _fiStaticEnabled;
	private boolean _fiStatusBarEnabled;
	private boolean _fiOtherEnabled;
	
	
	private boolean _canLAFSetBorders = false;
	public LAFPreferences()
	{
		super();
		_lafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
	}
	public LAFPreferences(IIdentifier id)
	{
		this();
		_id = id;
	}
	
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException ex)
		{
			throw new InternalError(ex.getMessage()); 
		}
	}

	public String getLookAndFeelClassName()
	{
		return _lafClassName;
	}
	public void setLookAndFeelClassName(String data)
	{
		_lafClassName = data;
	}
	public FontInfo getMenuFontInfo()
	{
		return _fiMenu;
	}
	public void setMenuFontInfo(FontInfo data)
	{
		_fiMenu = data;
	}
	public FontInfo getStaticFontInfo()
	{
		return _fiStatic;
	}
	public void setStaticFontInfo(FontInfo data)
	{
		_fiStatic = data;
	}
	public FontInfo getStatusBarFontInfo()
	{
		return _fiStatusBar;
	}
	public void setStatusBarFontInfo(FontInfo data)
	{
		_fiStatusBar = data;
	}
	public FontInfo getOtherFontInfo()
	{
		return _fiOther;
	}
	public void setOtherFontInfo(FontInfo data)
	{
		_fiOther = data;
	}
	public boolean isMenuFontEnabled()
	{
		return _fiMenuEnabled;
	}
	public void setMenuFontEnabled(boolean data)
	{
		_fiMenuEnabled = data;
	}
	public boolean isStaticFontEnabled()
	{
		return _fiStaticEnabled;
	}
	public void setStaticFontEnabled(boolean data)
	{
		_fiStaticEnabled = data;
	}
	public boolean isStatusBarFontEnabled()
	{
		return _fiStatusBarEnabled;
	}
	public void setStatusBarFontEnabled(boolean data)
	{
		_fiStatusBarEnabled = data;
	}
	public boolean isOtherFontEnabled()
	{
		return _fiOtherEnabled;
	}
	public void setOtherFontEnabled(boolean data)
	{
		_fiOtherEnabled = data;
	}
	
	public IIdentifier getIdentifier()
	{
		return null;
	}
	
	public IIdentifier getId()
	{
		return _id;
	}
	
	public void setId(IIdentifier _id)
	{
		this._id = _id;
	}
	
	public boolean getCanLAFSetBorder()
	{
		return _canLAFSetBorders;
	}
	
	public void setCanLAFSetBorder(boolean value)
	{
		_canLAFSetBorders = value;
	}
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + (_canLAFSetBorders ? 1231 : 1237);
        result = PRIME * result + ((_fiMenu == null) ? 0 : _fiMenu.hashCode());
        result = PRIME * result + (_fiMenuEnabled ? 1231 : 1237);
        result = PRIME * result + ((_fiOther == null) ? 0 : _fiOther.hashCode());
        result = PRIME * result + (_fiOtherEnabled ? 1231 : 1237);
        result = PRIME * result + ((_fiStatic == null) ? 0 : _fiStatic.hashCode());
        result = PRIME * result + (_fiStaticEnabled ? 1231 : 1237);
        result = PRIME * result + ((_fiStatusBar == null) ? 0 : _fiStatusBar.hashCode());
        result = PRIME * result + (_fiStatusBarEnabled ? 1231 : 1237);
        result = PRIME * result + ((_id == null) ? 0 : _id.hashCode());
        result = PRIME * result + ((_lafClassName == null) ? 0 : _lafClassName.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final LAFPreferences other = (LAFPreferences) obj;
        if (_canLAFSetBorders != other._canLAFSetBorders)
            return false;
        if (_fiMenu == null) {
            if (other._fiMenu != null)
                return false;
        } else if (!_fiMenu.equals(other._fiMenu))
            return false;
        if (_fiMenuEnabled != other._fiMenuEnabled)
            return false;
        if (_fiOther == null) {
            if (other._fiOther != null)
                return false;
        } else if (!_fiOther.equals(other._fiOther))
            return false;
        if (_fiOtherEnabled != other._fiOtherEnabled)
            return false;
        if (_fiStatic == null) {
            if (other._fiStatic != null)
                return false;
        } else if (!_fiStatic.equals(other._fiStatic))
            return false;
        if (_fiStaticEnabled != other._fiStaticEnabled)
            return false;
        if (_fiStatusBar == null) {
            if (other._fiStatusBar != null)
                return false;
        } else if (!_fiStatusBar.equals(other._fiStatusBar))
            return false;
        if (_fiStatusBarEnabled != other._fiStatusBarEnabled)
            return false;
        if (_id == null) {
            if (other._id != null)
                return false;
        } else if (!_id.equals(other._id))
            return false;
        if (_lafClassName == null) {
            if (other._lafClassName != null)
                return false;
        } else if (!_lafClassName.equals(other._lafClassName))
            return false;
        return true;
    }
}