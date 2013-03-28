package net.sourceforge.squirrel_sql.plugins.favs;

import net.sourceforge.squirrel_sql.fw.persist.ValidationException;

final class FolderNode extends BaseNode {

    private static final long serialVersionUID = 1L;
    private Folder _folder;
	private String _identifier;
	private String _name;

	FolderNode(Folder folder) throws IllegalArgumentException {
		super(folder != null ? folder.getName() : "?", true);
		if (folder == null) {
			throw new IllegalArgumentException("Null Folder passed");
		}
		_folder = folder;
	}

	public boolean isLeaf() {
		return false;
	}

	Folder getFolder() {		return _folder;
	}

	String getName() {
		return _folder.getName();
	}

	void setName(String name) throws ValidationException {
		_folder.setName(name);
	}
}
