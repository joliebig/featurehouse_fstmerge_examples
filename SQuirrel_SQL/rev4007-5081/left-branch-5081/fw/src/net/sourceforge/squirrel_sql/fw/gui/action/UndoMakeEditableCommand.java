package net.sourceforge.squirrel_sql.fw.gui.action;

import net.sourceforge.squirrel_sql.fw.datasetviewer.IDataSetUpdateableModel;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

public class UndoMakeEditableCommand implements ICommand
{
	
	private IDataSetUpdateableModel _updateableModel = null;
	
	public UndoMakeEditableCommand (IDataSetUpdateableModel updateableModel)
	{
		_updateableModel = updateableModel;
	}
	
	public void execute() 
	{
		
		
		
		_updateableModel.forceEditMode(false);
	}

}