package net.sourceforge.squirrel_sql.plugins.laf;

import javax.swing.LookAndFeel;

public interface ILookAndFeelController
{
	
	void initialize();
	
	void aboutToBeInstalled(LAFRegister lafRegister, LookAndFeel laf);
	
	void hasBeenInstalled(LAFRegister lafRegister, LookAndFeel laf);
	
	BaseLAFPreferencesPanelComponent getPreferencesComponent();
}
