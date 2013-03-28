package net.sourceforge.squirrel_sql.client.action;

import java.awt.event.KeyEvent;




public class ActionKeys
{
	private String _actionClassName;
	private String _accelerator;
	private int _mnemonic;

	public ActionKeys()
	{
		super();
		_accelerator = "";
		_mnemonic = KeyEvent.VK_UNDEFINED;
	}

	
	public ActionKeys(String actionClassName, String accelerator, int mnemonic)
	{
		super();
		setActionClassName(actionClassName);
		setAccelerator(accelerator);
		setMnemonic(mnemonic);
	}

	public String getActionClassName()
	{
		return _actionClassName;
	}

	public int getMnemonic()
	{
		return _mnemonic;
	}

	public String getAccelerator()
	{
		return _accelerator;
	}

	
	public void setActionClassName(String value)
	{
		if (value == null)
		{
			throw new IllegalArgumentException("ActionClassName == null");
		}
		_actionClassName = value;
	}

	public void setAccelerator(String value)
	{
		_accelerator = value != null ? value : "";
	}

	public void setMnemonic(int value)
	{
		_mnemonic = value;
	}
}
