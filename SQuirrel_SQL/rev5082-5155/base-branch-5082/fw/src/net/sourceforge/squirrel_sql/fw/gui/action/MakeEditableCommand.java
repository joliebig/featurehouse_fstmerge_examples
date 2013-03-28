package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableModel;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class MakeEditableCommand implements ICommand
{
	
	private IDataSetUpdateableModel _updateableModel = null;
	
	public MakeEditableCommand (IDataSetUpdateableModel updateableModel)
	{
		_updateableModel = updateableModel;
	}
	
	public void execute() 
	{
		
		
		_updateableModel.forceEditMode(true);
	}

}
