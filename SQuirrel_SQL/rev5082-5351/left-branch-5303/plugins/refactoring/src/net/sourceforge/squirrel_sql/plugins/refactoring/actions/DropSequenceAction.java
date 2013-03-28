package net.sourceforge.squirrel_sql.plugins.refactoring.actions;



import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.DropSequenceCommand;

public class DropSequenceAction extends AbstractRefactoringAction
{
	private static final long serialVersionUID = -2293313754254825620L;

	public DropSequenceAction(IApplication app, Resources rsrc)
	{
		super(app, rsrc);
	}

	
	@Override
	protected ICommand getCommand(IDatabaseObjectInfo[] info)
	{
		return new DropSequenceCommand(_session, info);
	}

	
	@Override
	protected String getErrorMessage()
	{
		return null;
	}

	
	@Override
	protected boolean isMultipleObjectAction()
	{
		return true;
	}
}
