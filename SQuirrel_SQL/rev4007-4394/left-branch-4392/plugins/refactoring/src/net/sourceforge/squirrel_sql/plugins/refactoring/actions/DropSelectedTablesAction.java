package net.sourceforge.squirrel_sql.plugins.refactoring.actions;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.session.action.IObjectTreeAction;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.DropTablesCommand;

public class DropSelectedTablesAction extends AbstractRefactoringAction
										implements IObjectTreeAction
{
    private static final long serialVersionUID = 1L;

    
	public DropSelectedTablesAction(IApplication app, Resources rsrc)
	{
		super(app, rsrc);
	}

    @Override
    protected ICommand getCommand(IDatabaseObjectInfo[] info) {
        return new DropTablesCommand(_session, info);
    }

    @Override
    protected String getErrorMessage() {
        return "";
    }

    @Override
    protected boolean isMultipleObjectAction() {
        return true;
    }
}
