package net.sourceforge.squirrel_sql.plugins.favs;

import java.awt.Frame;

import net.sourceforge.squirrel_sql.client.IApplication;

public class OrganizeSavedQueriesCommand {
	private IApplication _app;
	private FoldersCache _cache;
	private Frame _frame;

	public OrganizeSavedQueriesCommand(IApplication app, FoldersCache cache,
										Frame frame)
			throws IllegalArgumentException {
		super();
		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (cache == null) {
			throw new IllegalArgumentException("Null FoldersCache passed");
		}

		_app = app;
		_cache = cache;
		_frame = frame;
	}

	public void execute() {		new OrganizeSavedQueriesDialog(_app, _cache, _frame).setVisible(true);
	}
}