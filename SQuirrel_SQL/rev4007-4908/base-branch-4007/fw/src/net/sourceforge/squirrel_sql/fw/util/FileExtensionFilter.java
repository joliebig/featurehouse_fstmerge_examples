package net.sourceforge.squirrel_sql.fw.util;

import java.io.File;
import java.io.FilenameFilter;

public class FileExtensionFilter
				extends javax.swing.filechooser.FileFilter
				implements java.io.FileFilter, FilenameFilter {

	private String _description;
	private String[] _exts;

	public FileExtensionFilter(String description, String[] exts) {
		super();
		_exts = exts;
		StringBuffer buf = new StringBuffer(description);
		buf.append(" (");
		for (int i = 0; i < _exts.length; ++i) {
			buf.append("*").append(_exts[i]);
			if (i != (_exts.length - 1)) {
				buf.append(", ");
			}
		}
		buf.append(")");
		_description = buf.toString();
	}

	public boolean accept(File dir, String name) {
		return checkFileName(name.toLowerCase());
	}

	public boolean accept(File file) {
		if (file.isDirectory()) {
			return true;
		}
		return checkFileName(file.getName().toLowerCase());
	}

	public String getDescription() {
		return _description;
	}

	private boolean checkFileName(String name) {
		for (int i = 0; i < _exts.length; ++i) {
			if (name.endsWith(_exts[i])) {
				return true;
			}
		}
		return false;
	}
}