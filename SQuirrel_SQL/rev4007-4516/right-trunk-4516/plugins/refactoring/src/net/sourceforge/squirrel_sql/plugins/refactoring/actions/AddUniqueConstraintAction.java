package net.sourceforge.squirrel_sql.plugins.refactoring.actions;



import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.AddUniqueConstraintCommand;

public class AddUniqueConstraintAction extends AbstractRefactoringAction
{
	private static final long serialVersionUID = -4767078024314053228L;

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(AddUniqueConstraintAction.class);

	private static interface i18n
	{
		String ACTION_PART = s_stringMgr.getString("AddUniqueConstraintAction.actionPart");

		String OBJECT_PART = s_stringMgr.getString("Shared.tableObject");

		String SINGLE_OBJECT_MESSAGE =
			s_stringMgr.getString("Shared.singleObjectMessage", i18n.OBJECT_PART, i18n.ACTION_PART);
	}

	public AddUniqueConstraintAction(IApplication app, Resources rsrc)
	{
		super(app, rsrc);
	}

	
	@Override
	protected ICommand getCommand(IDatabaseObjectInfo[] info)
	{
		return new AddUniqueConstraintCommand(_session, info);
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
