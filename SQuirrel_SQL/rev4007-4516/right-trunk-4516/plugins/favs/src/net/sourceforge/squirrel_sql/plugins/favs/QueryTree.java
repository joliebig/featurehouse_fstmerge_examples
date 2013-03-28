package net.sourceforge.squirrel_sql.plugins.favs;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.ActionCollection;
import net.sourceforge.squirrel_sql.fw.gui.BasePopupMenu;

final class QueryTree extends JTree {
    private static final long serialVersionUID = 1L;

    private IApplication _app;
    
    @SuppressWarnings("unused")
	private QueryTreeModel _model;

	
	private MyPopupMenu _popupMenu = new MyPopupMenu();

	private List<BaseFavouriteAction> _actions = 
        new ArrayList<BaseFavouriteAction>();

	public QueryTree(IApplication app, FoldersCache cache) throws IllegalArgumentException {
		super(new QueryTreeModel(app, cache));
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (cache == null) {
			throw new IllegalArgumentException("Null FolderCache passed");
		}
		_app = app;
		_model = (QueryTreeModel)getModel();
		setRootVisible(false);
		
		setLayout(new BorderLayout());
		setShowsRootHandles(true);
		setEditable(true);

		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					displayPopupMenu(evt);
				}
			}
			public void mouseReleased(MouseEvent evt) {
				if (evt.isPopupTrigger()) {
					displayPopupMenu(evt);
				}
			}
		});



		
		
		ToolTipManager.sharedInstance().registerComponent(this);


	}

	
	public String getToolTipText(MouseEvent evt) {
		String tip = null;
		final TreePath path = getPathForLocation(evt.getX(), evt.getY());
		if (path != null) {
			tip = path.getLastPathComponent().toString();
		} else {
			tip = super.getToolTipText();
		}
		return tip;
	}

	QueryTreeModel getTypedModel() {		return (QueryTreeModel)getModel();
	}

	
	private void displayPopupMenu(MouseEvent evt) {
		int x = evt.getX();
		int y = evt.getY();
		TreePath path = getPathForLocation(x, y);
		_popupMenu.show(evt, path);
	}

	
	private class MyPopupMenu extends BasePopupMenu {
        private static final long serialVersionUID = 1L;

        
		private boolean _built = false;

		
		public void show(MouseEvent evt, TreePath path) {
			if (!_built) {
				ActionCollection actColl = QueryTree.this._app.getActionCollection();
				add(actColl.get(NewSavedQueriesFolderAction.class));
				addSeparator();
				add(actColl.get(RenameSavedQueriesFolderAction.class));
				addSeparator();
				add(actColl.get(DeleteSavedQueriesFolderAction.class));
				_built = true;
			}
			for (Iterator<BaseFavouriteAction> it = QueryTree.this._actions.iterator(); it.hasNext();) {
				(it.next()).setTreePath(path);
			}
			super.show(evt);
		}

		public JMenuItem add(Action action) {			if (action instanceof BaseFavouriteAction) {
				((BaseFavouriteAction)action).setQueryTree(QueryTree.this);
				_actions.add((BaseFavouriteAction)action);
			}
			return super.add(action);
		}
	}
}
