package net.sourceforge.squirrel_sql.plugins.laf;

import java.awt.LayoutManager;
import javax.swing.JPanel;

abstract class BaseLAFPreferencesPanelComponent extends JPanel
{
	BaseLAFPreferencesPanelComponent()
	{
		super();
	}
	BaseLAFPreferencesPanelComponent(LayoutManager lmgr)
	{
		super(lmgr);
	}
	
	public void loadPreferencesPanel()
	{
	}
	
	public boolean applyChanges()
	{
		return false;
	}
}
