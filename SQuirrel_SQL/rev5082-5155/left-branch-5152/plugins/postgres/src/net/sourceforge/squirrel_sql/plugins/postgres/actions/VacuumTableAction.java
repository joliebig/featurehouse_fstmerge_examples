package net.sourceforge.squirrel_sql.plugins.postgres.actions;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.plugins.postgres.commands.VacuumTableCommand;


public class VacuumTableAction extends AbstractObjectTreeAction {
    public VacuumTableAction(IApplication app, Resources rsrc) {
        super(app, rsrc);
    }


    @Override
    protected ICommand getCommand(IDatabaseObjectInfo[] infos) {
        return new VacuumTableCommand(_tree.getSession(), infos);
    }


    @Override
    protected boolean isMultipleObjectAction() {
        return true;
    }


    @Override
    protected String getErrorMessage() {
        return null;
    }
}
