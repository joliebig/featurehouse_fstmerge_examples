package net.sourceforge.squirrel_sql.plugins.favs;

import java.awt.event.ActionEvent;

import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;

class RenameSavedQueriesFolderAction extends BaseFavouriteAction {

	public RenameSavedQueriesFolderAction(IApplication app, Resources rsrc) {
		super(app, rsrc);
	}

	public void actionPerformed(ActionEvent evt) {
		TreePath path = getTreePath();
		if (path != null) {
			getQueryTree().startEditingAtPath(path);
		}
	}
}
