package net.sourceforge.squirrel_sql.plugins.dbinfo;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

class ShowDBInfoFilesAction extends SquirrelAction {

    public ShowDBInfoFilesAction(IApplication app, Resources rsrc) {
        super(app, rsrc);
    }

    public void actionPerformed(ActionEvent evt) {
        new ShowDBInfoFilesCommand(getApplication()).execute();
    }
}
