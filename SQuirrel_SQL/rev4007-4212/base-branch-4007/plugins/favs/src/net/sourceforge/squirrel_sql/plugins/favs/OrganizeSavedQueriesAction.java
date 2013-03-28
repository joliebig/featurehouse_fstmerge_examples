package net.sourceforge.squirrel_sql.plugins.favs;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.client.IApplication;

public class OrganizeSavedQueriesAction extends BaseFavouriteAction {
	private FoldersCache _cache;

	public OrganizeSavedQueriesAction(IApplication app, Resources rsrc,
										FoldersCache cache)
			throws IllegalArgumentException {
		super(app, rsrc);
		if (cache == null) {
			throw new IllegalArgumentException("Null FoldersCache passed");
		}
		_cache = cache;
	}

	public void actionPerformed(ActionEvent evt) {
		OrganizeSavedQueriesCommand cmd = new OrganizeSavedQueriesCommand(
							getApplication(), _cache, getParentFrame(evt));
		cmd.execute();
	}
}

