package net.sourceforge.squirrel_sql.plugins.favs;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;

class NewSavedQueriesFolderAction extends BaseFavouriteAction {
	public NewSavedQueriesFolderAction(IApplication app, Resources rsrc) {
		super(app, rsrc);
	}

	public void actionPerformed(ActionEvent evt) {
		new NewSavedQueriesFolderCommand(getApplication(), getQueryTree(), getTreePath()).execute();
	}
}
