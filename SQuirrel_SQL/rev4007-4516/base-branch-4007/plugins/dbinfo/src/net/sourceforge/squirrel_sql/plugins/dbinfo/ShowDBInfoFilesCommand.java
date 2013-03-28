package net.sourceforge.squirrel_sql.plugins.dbinfo;

import javax.swing.tree.TreePath;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.IApplication;

public class ShowDBInfoFilesCommand {
    private IApplication _app;

    public ShowDBInfoFilesCommand(IApplication app)
            throws IllegalArgumentException {
        super();
        if (app == null) {
            throw new IllegalArgumentException("Null IApplication passed");
        }
        _app = app;
    }

    public void execute() {
    }
}