package net.sourceforge.squirrel_sql.plugins.favs;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.client.IApplication;


public class DeleteSavedQueriesFolderAction extends BaseFavouriteAction {
    
    public DeleteSavedQueriesFolderAction(IApplication app, Resources rsrc) {
        super(app, rsrc);
    }

    
    public void actionPerformed(ActionEvent evt) {
        new DeleteSavedQueriesFolderCommand(getParentFrame(evt), getQueryTree(),
                                            getTreePath()).execute();
    }
}
