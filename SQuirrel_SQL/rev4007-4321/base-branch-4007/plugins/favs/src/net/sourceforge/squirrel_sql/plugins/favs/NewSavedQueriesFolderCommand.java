package net.sourceforge.squirrel_sql.plugins.favs;

import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class NewSavedQueriesFolderCommand {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(NewSavedQueriesFolderCommand.class);

	@SuppressWarnings("unused")
    private IApplication _app;
	private QueryTree _tree;
	private TreePath _path;

	public NewSavedQueriesFolderCommand(IApplication app, QueryTree tree,
											TreePath path)
			throws IllegalArgumentException {
		super();
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (tree == null) {
			throw new IllegalArgumentException("Null QueryTree passed");
		}
		
		
		

		_app = app;
		_tree = tree;
		_path = path;
	}

	public void execute() {
		FolderNode rootNode = (FolderNode)_tree.getModel().getRoot();
		FolderNode parentNode = null;
		if (_path == null) {
			parentNode = rootNode;
		} else {
			Object obj = _path.getLastPathComponent();
			if (obj == null) {
				parentNode = rootNode;
			} else if (obj instanceof FolderNode) {
				parentNode = (FolderNode)obj;
			}
		}
		if (parentNode != null) {
			
			final Folder folder = new Folder(null, s_stringMgr.getString("favs.newFolder")); 
			final FolderNode newNode = new FolderNode(folder);
			parentNode.getFolder().addSubFolder(folder);
			parentNode.add(newNode);
			_tree.getTypedModel().nodeStructureChanged(parentNode);

			TreePath newNodePath = null;
			if (_path != null) {
				newNodePath = _path.pathByAddingChild(newNode);
			} else {
				newNodePath = new TreePath(new FolderNode[] {rootNode, newNode});
			}
			if (newNodePath != null) {
				_tree.makeVisible(newNodePath);
				_tree.expandPath(newNodePath);
				_tree.startEditingAtPath(newNodePath);
			}
		}
	}
}
