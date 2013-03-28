package net.sourceforge.squirrel_sql.plugins.refactoring.actions;



import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.ModifyColumnCommand;

public class ModifyColumnAction extends AbstractRefactoringAction
{
	private static final long serialVersionUID = -9019844275238785508L;

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ModifyColumnAction.class);

	private static interface i18n
	{
		String ACTION_PART = s_stringMgr.getString("ModifyColumnAction.actionPart");

		String OBJECT_PART = s_stringMgr.getString("Shared.tableObject");

		String SINGLE_OBJECT_MESSAGE =
			s_stringMgr.getString("Shared.singleObjectMessage", OBJECT_PART, ACTION_PART);
	}

	public ModifyColumnAction(IApplication app, Resources rsrc)
	{
		super(app, rsrc);
	}

	@Override
	protected ICommand getCommand(IDatabaseObjectInfo[] info)
	{
		return new ModifyColumnCommand(_session, info);
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