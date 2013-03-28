package net.sourceforge.squirrel_sql.plugins.favs;

import java.awt.Frame;
import java.text.MessageFormat;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.fw.gui.Dialogs;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DeleteSavedQueriesFolderCommand {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(DeleteSavedQueriesFolderCommand.class);

	
	private static class i18n {

		
		static String MSG_CONFIRM = s_stringMgr.getString("favs.deletelFolder");
	}

	private Frame _frame;
	private QueryTree _tree;
	private TreePath _path;

	public DeleteSavedQueriesFolderCommand(Frame frame, QueryTree tree,
														TreePath path) {
		super();
		_frame = frame;
		_tree = tree;
		_path = path;
	}

	public void execute() {
		if (_path != null) {
			Object obj = _path.getLastPathComponent();
			if (obj instanceof FolderNode) {
				FolderNode nodeToDelete = (FolderNode)obj;
				Object[] args = {nodeToDelete.getName()};
				String msg = MessageFormat.format(i18n.MSG_CONFIRM, args);
				if (Dialogs.showYesNo(_frame, msg)) {
					TreeNode parentNode = nodeToDelete.getParent();
					if (parentNode instanceof FolderNode) {
						FolderNode parentFolder = (FolderNode)parentNode;
						parentFolder.remove(nodeToDelete);
						parentFolder.getFolder().removeSubFolder(nodeToDelete.getFolder());
						_tree.getTypedModel().nodeStructureChanged(parentFolder);
					}
				}
			}
		}
	}
}
