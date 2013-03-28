package net.sourceforge.squirrel_sql.plugins.postgres.actions;


import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.plugins.postgres.commands.VacuumDatabaseCommand;

public class VacuumDatabaseAction extends AbstractSessionAction {
    public VacuumDatabaseAction(IApplication app, Resources rsrc) {
        super(app, rsrc);
    }


    @Override
    protected ICommand getCommand() {
        return new VacuumDatabaseCommand(_session);
    }
}
