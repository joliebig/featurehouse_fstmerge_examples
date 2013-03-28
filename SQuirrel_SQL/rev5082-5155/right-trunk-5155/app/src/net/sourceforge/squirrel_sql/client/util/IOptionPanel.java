package net.sourceforge.squirrel_sql.client.util;

import java.awt.Component;

public interface IOptionPanel
{
	
	void applyChanges();

	
	String getTitle();

	
	String getHint();

	
	Component getPanelComponent();
}
