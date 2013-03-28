package net.sourceforge.squirrel_sql.plugins.refactoring.actions;



import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.RenameTableCommand;

public class RenameTableAction extends AbstractRefactoringAction
{
	private static final long serialVersionUID = 8970571253545997433L;

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(RenameTableAction.class);

	private static interface i18n
	{
		String ACTION_PART = s_stringMgr.getString("RenameTableAction.actionPart");

		String OBJECT_PART = s_stringMgr.getString("Shared.tableObject");

		String SINGLE_OBJECT_MESSAGE =
			s_stringMgr.getString("Shared.singleObjectMessage", OBJECT_PART, ACTION_PART);
	}

	public RenameTableAction(IApplication app, Resources rsrc)
	{
		super(app, rsrc);
	}

	@Override
	protected ICommand getCommand(IDatabaseObjectInfo[] info)
	{
		return new RenameTableCommand(_session, info);
	}

	@Override
	protected String getErrorMessage()
	{
		return i18n.SINGLE_OBJECT_MESSAGE;
	}

	@Override
	protected boolean isMultipleObjectAction()
	{
		return false;
	}
}
