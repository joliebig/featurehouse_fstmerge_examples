package net.sourceforge.squirrel_sql.plugins.favs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.client.IApplication;

class OrganizeSavedQueriesDialog extends JDialog {

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(OrganizeSavedQueriesDialog.class);

	private IApplication _app;
	private FoldersCache _cache;

	private JSplitPane _mainSplitPane = new JSplitPane();
	private QueryTree _queryTree;

	private static interface i18n {
		
		String TITLE = s_stringMgr.getString("favs.savedQueries");
	}

	public OrganizeSavedQueriesDialog(IApplication app, FoldersCache cache, Frame owner)
			throws IllegalArgumentException {
		super(owner, i18n.TITLE, true);
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (cache == null) {
			throw new IllegalArgumentException("Null FoldersCache passed");
		}

		_app = app;
		_cache = cache;

		createUserInterface();
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	private void createUserInterface() {
		final Container contentPane = getContentPane();
		setVisible(false);

		_queryTree = new QueryTree(_app, _cache);

		_mainSplitPane.setOneTouchExpandable(true);
		_mainSplitPane.setContinuousLayout(true);

		_queryTree.setPreferredSize(new Dimension(200, 200));
		_mainSplitPane.add(new JScrollPane(_queryTree), JSplitPane.LEFT);


		contentPane.setLayout(new BorderLayout());
		contentPane.add(_mainSplitPane, BorderLayout.CENTER);

		
		pack();
		GUIUtils.centerWithinParent(this);
		setResizable(false);
	}
}

