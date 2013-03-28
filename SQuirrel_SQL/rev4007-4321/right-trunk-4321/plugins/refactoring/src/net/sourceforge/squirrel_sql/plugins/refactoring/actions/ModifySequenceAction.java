package net.sourceforge.squirrel_sql.plugins.refactoring.actions;



import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.refactoring.commands.ModifySequenceCommand;

public class ModifySequenceAction extends AbstractRefactoringAction
{
	private static final long serialVersionUID = -8681699057991332311L;

	
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ModifySequenceAction.class);

	private static interface i18n
	{
		String ACTION_PART = s_stringMgr.getString("ModifySequenceAction.actionPart");

		String OBJECT_PART = s_stringMgr.getString("Shared.sequenceObject");

		String SINGLE_OBJECT_MESSAGE =
			s_stringMgr.getString("Shared.singleObjectMessage", OBJECT_PART, ACTION_PART);
	}

	public ModifySequenceAction(IApplication app, Resources rsrc)
	{
		super(app, rsrc);
	}

	
	@Override
	protected ICommand getCommand(IDatabaseObjectInfo[] info)
	{
		return new ModifySequenceCommand(_session, info);
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
