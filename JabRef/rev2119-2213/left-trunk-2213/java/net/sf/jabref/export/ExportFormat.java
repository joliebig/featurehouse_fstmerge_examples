package net.sf.jabref.export;

import net.sf.jabref.Globals;
import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.export.layout.Layout;
import net.sf.jabref.export.layout.LayoutHelper;

import javax.swing.filechooser.FileFilter;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.io.File;
import java.io.Reader;
import java.io.IOException;


public class ExportFormat {

	private String displayName;
	private String consoleName;
	private String lfFileName;
	private String directory;
	private String extension;
	private FileFilter fileFilter;
	private boolean customExport = false;

	
	public ExportFormat(String displayName, String consoleName,
			String lfFileName, String directory, String extension) {
		this.displayName = displayName;

		this.consoleName = consoleName;
		this.lfFileName = lfFileName;
		this.directory = directory;
		this.extension = extension;

		fileFilter = new ExportFileFilter(this);
	}

	
	public void setCustomExport(boolean custom) {
		this.customExport = custom;
	}

	public String getConsoleName() {
		return consoleName;
	}

	public String getExtension() {
		return extension;
	}

	public String getDisplayName() {
		return displayName;
	}

	
	public Reader getReader(String filename) throws IOException {
		
		String dir;
		if (customExport) {
			dir = "";
		} else {
			dir = Globals.LAYOUT_PREFIX
					+ (directory == null ? "" : directory + "/");
		}
		return FileActions.getReader(dir + filename);
	}

	
	public void performExport(final BibtexDatabase database, final String file,
			final String encoding, Set entries) throws Exception {

		File outFile = new File(file);
		SaveSession ss = getSaveSession(encoding, outFile);
		VerifyingWriter ps = ss.getWriter();
		
		Layout beginLayout = null;
		Reader reader;
		try {
			reader = getReader(lfFileName + ".begin.layout");
			LayoutHelper layoutHelper = new LayoutHelper(reader);
			beginLayout = layoutHelper
					.getLayoutFromText(Globals.FORMATTER_PACKAGE);
			reader.close();
		} catch (IOException ex) {
			
			
		}
		
		if (beginLayout != null) {
			ps.write(beginLayout.doLayout(database));
		}
		

		
		List sorted = FileActions.getSortedEntries(database, entries, false);

		
		reader = getReader(lfFileName + ".layout");

		LayoutHelper layoutHelper = new LayoutHelper(reader);
		Layout defLayout = layoutHelper
				.getLayoutFromText(Globals.FORMATTER_PACKAGE);
		reader.close();
		HashMap layouts = new HashMap();
		Layout layout;
		Iterator i = sorted.iterator();
		for (; i.hasNext();) {
			
			BibtexEntry entry = (BibtexEntry) (i.next());

			
			String type = entry.getType().getName().toLowerCase();
			if (layouts.containsKey(type))
				layout = (Layout) layouts.get(type);
			else {
				try {
					
					reader = getReader(lfFileName + "." + type + ".layout");
					layoutHelper = new LayoutHelper(reader);
					layout = layoutHelper
							.getLayoutFromText(Globals.FORMATTER_PACKAGE);
					layouts.put(type, layout);
					reader.close();
				} catch (IOException ex) {
					
					
					
					layout = defLayout;
				}
			}

			
			ps.write(layout.doLayout(entry, database));
		}

		

		
		Layout endLayout = null;
		try {
			reader = getReader(lfFileName + ".end.layout");
			layoutHelper = new LayoutHelper(reader);
			endLayout = layoutHelper
					.getLayoutFromText(Globals.FORMATTER_PACKAGE);
			reader.close();
		} catch (IOException ex) {
			
			
		}

		
		if (endLayout != null) {
			ps.write(endLayout.doLayout(database));
		}

		finalizeSaveSession(ss);
	}

	public SaveSession getSaveSession(final String encoding, final File outFile)
			throws IOException {

		SaveSession ss = new SaveSession(outFile, encoding, false);
		return ss;
	}

	public void finalizeSaveSession(final SaveSession ss) throws Exception,
			SaveException {
		ss.getWriter().flush();
		ss.getWriter().close();

		if (!ss.getWriter().couldEncodeAll()) {
			System.err.println("Could not encode...");
		}
		ss.commit();

	}

	public FileFilter getFileFilter() {
		return fileFilter;
	}

}
