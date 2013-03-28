package net.sourceforge.squirrel_sql.client.gui.builders;

import java.awt.Component;
import java.util.EventObject;

public class UIFactoryComponentCreatedEvent extends EventObject
{
	
	private UIFactory _factory;

	
	private Component _comp;

	
	UIFactoryComponentCreatedEvent(UIFactory source, Component comp)
	{
		super(checkParams(source, comp));
		_factory = source;
		_comp = comp;
	}

	
	public UIFactory getUIFactory()
	{
		return _factory;
	}

	
	public Component getComponent()
	{
		return _comp;
	}

	private static UIFactory checkParams(UIFactory source, Component comp)
	{
		if (source == null)
		{
			throw new IllegalArgumentException("UIFactory == null");
		}
		if (comp == null)
		{
			throw new IllegalArgumentException("Component == null");
		}
		return source;
	}
}
