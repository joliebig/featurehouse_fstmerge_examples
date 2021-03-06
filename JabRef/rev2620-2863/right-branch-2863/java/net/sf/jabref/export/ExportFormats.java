package net.sf.jabref.export;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.*;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import net.sf.jabref.*;
import net.sf.jabref.plugin.PluginCore;
import net.sf.jabref.plugin.core.JabRefPlugin;
import net.sf.jabref.plugin.core.generated._JabRefPlugin.ExportFormatExtension;
import net.sf.jabref.plugin.core.generated._JabRefPlugin.ExportFormatProviderExtension;
import net.sf.jabref.plugin.core.generated._JabRefPlugin.ExportFormatTemplateExtension;


public class ExportFormats {

	private static Map<String,IExportFormat> exportFormats = new TreeMap<String,IExportFormat>();

    public static void initAllExports() {
        exportFormats.clear();

        
        putFormat(new ExportFormat(
                Globals.lang("HTML"), "html", "html", null, ".html"));
        putFormat(new ExportFormat(
                Globals.lang("Simple HTML"), "simplehtml", "simplehtml", null, ".html"));
        putFormat(new ExportFormat(Globals.lang("Docbook"), "docbook", "docbook", null, ".xml"));
        putFormat(new ExportFormat(Globals.lang("BibTeXML"), "bibtexml", "bibtexml", null, ".xml"));
        putFormat(new ExportFormat(Globals.lang("BibO RDF"), "bibordf", "bibordf", null, ".rdf"));
        putFormat(new ModsExportFormat());
        putFormat(new ExportFormat(Globals.lang("HTML table"),
                "tablerefs", "tablerefs", "tablerefs", ".html"));
        putFormat(new ExportFormat(Globals.lang("HTML table (with Abstract & BibTeX)"),
                "tablerefsabsbib", "tablerefsabsbib", "tablerefsabsbib", ".html"));
        putFormat(new ExportFormat(Globals.lang("Harvard RTF"), "harvard", "harvard",
                "harvard", ".rtf"));
        putFormat(new ExportFormat(Globals.lang("Endnote"), "endnote", "EndNote",
                "endnote", ".txt"));
        putFormat(new ExportFormat(Globals.lang("OpenOffice CSV"), "oocsv", "openoffice-csv",
            "openoffice", ".csv"));
        ExportFormat ef = new ExportFormat(Globals.lang("RIS"), "ris", "ris", "ris", ".ris");
        ef.encoding = "UTF-8";
        putFormat(ef);
        putFormat(new OpenOfficeDocumentCreator());
        putFormat(new OpenDocumentSpreadsheetCreator());
        putFormat(new MSBibExportFormat());
        putFormat(new MySQLExport());
    
        
        JabRefPlugin plugin = JabRefPlugin.getInstance(PluginCore.getManager());
		if (plugin != null){
			
			
			for (ExportFormatTemplateExtension e : plugin.getExportFormatTemplateExtensions()){
				ExportFormat format = PluginBasedExportFormat.getFormat(e);
				if (format != null){
					putFormat(format);
				}
			}

			
			for (final ExportFormatExtension e : plugin.getExportFormatExtensions()) {
				putFormat(new IExportFormat(){

					public String getConsoleName() {
						return e.getConsoleName();
					}

					public String getDisplayName() {
						return e.getDisplayName();
					}

					public FileFilter getFileFilter() {
						return new ExportFileFilter(this, e.getExtension());
					}

					IExportFormat wrapped;
					public void performExport(BibtexDatabase database, MetaData metaData,
						String file, String encoding, Set<String> entryIds)
						throws Exception {

						if (wrapped == null)
							wrapped = e.getExportFormat();
						wrapped.performExport(database, metaData, file, encoding, entryIds);
					}
				});
			}
		
			
			for (ExportFormatProviderExtension e : plugin.getExportFormatProviderExtensions()) {
				IExportFormatProvider formatProvider = e.getFormatProvider();
				for (IExportFormat exportFormat : formatProvider.getExportFormats()) {
					putFormat(exportFormat);
				}
			}
		}
		
        
        TreeMap<String, ExportFormat> customFormats = Globals.prefs.customExports.getCustomExportFormats();
        for (IExportFormat format : customFormats.values()){
            putFormat(format);
        }
    }

	
	public static String getConsoleExportList(int maxLineLength, int firstLineSubtr,
		String linePrefix) {
		StringBuffer sb = new StringBuffer();
		int lastBreak = -firstLineSubtr;

		for (Iterator<String> i = exportFormats.keySet().iterator(); i.hasNext();) {
			String name = i.next();
			if (sb.length() + 2 + name.length() - lastBreak > maxLineLength) {
				sb.append(",\n");
				lastBreak = sb.length();
				sb.append(linePrefix);
			} else if (sb.length() > 0)
				sb.append(", ");
			sb.append(name);
		}

		return sb.toString();
	}

    
    public static Map<String, IExportFormat> getExportFormats() {
        
        return Collections.unmodifiableMap(exportFormats);
    } 

    
	public static IExportFormat getExportFormat(String consoleName) {
		return exportFormats.get(consoleName);
	}

	
	public static AbstractAction getExportAction(JabRefFrame frame, boolean selectedOnly) {

		class ExportAction extends MnemonicAwareAction {

			private static final long serialVersionUID = 639463604530580554L;

			private JabRefFrame frame;

			private boolean selectedOnly;

			public ExportAction(JabRefFrame frame, boolean selectedOnly) {
				this.frame = frame;
				this.selectedOnly = selectedOnly;
				putValue(NAME, selectedOnly ? "Export selected entries" : "Export");
			}

			public void actionPerformed(ActionEvent e) {
				ExportFormats.initAllExports();
				JFileChooser fc = ExportFormats.createExportFileChooser(
                    Globals.prefs.get("exportWorkingDirectory"));
				fc.showSaveDialog(frame);
				File file = fc.getSelectedFile();
				if (file == null)
					return;
				FileFilter ff = fc.getFileFilter();
				if (ff instanceof ExportFileFilter) {


                    ExportFileFilter eff = (ExportFileFilter) ff;
                    String path = file.getPath();
                    if (!path.endsWith(eff.getExtension()))
                        path = path + eff.getExtension();
                    file = new File(path);
                    if (file.exists()) {
                        
                        if (JOptionPane.showConfirmDialog(frame, "'" + file.getName() + "' "
                            + Globals.lang("exists. Overwrite file?"), Globals.lang("Export"),
                            JOptionPane.OK_CANCEL_OPTION) != JOptionPane.OK_OPTION)
                            return;
                    }
                    final IExportFormat format = eff.getExportFormat();
                    Set<String> entryIds = null;
                    if (selectedOnly) {
                        BibtexEntry[] selected = frame.basePanel().getSelectedEntries();
                        entryIds = new HashSet<String>();
                        for (int i = 0; i < selected.length; i++) {
                            BibtexEntry bibtexEntry = selected[i];
                            entryIds.add(bibtexEntry.getId());
                        }
                    }

                    
                    
                    
                    Globals.prefs.fileDirForDatabase = frame.basePanel().metaData()
                            .getFileDirectory(GUIGlobals.FILE_FIELD);                    

                    
                    
                    Globals.prefs.put("lastUsedExport", format.getConsoleName());
                    Globals.prefs.put("exportWorkingDirectory", file.getParent());
                    
                    final File finFile = file;
                    final Set<String> finEntryIDs = entryIds;
                    AbstractWorker exportWorker = new AbstractWorker() {
                        String errorMessage = null;
                        public void run() {
                            try {
                                format.performExport(frame.basePanel().database(),
                                        frame.basePanel().metaData(),
                                        finFile.getPath(), frame
                                    .basePanel().getEncoding(), finEntryIDs);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                if (ex.getMessage()==null ) {
                                    errorMessage = ex.toString();
                                } else {
                                    errorMessage = ex.getMessage();
                                }
                            }
                        }

                        public void update() {
                            
                            if (errorMessage == null) {
                                frame.output(Globals.lang("%0 export successful", format.getDisplayName()));
                            }
                            
                            else {
                                frame.output(Globals.lang("Could not save file")
                                        + " - " + errorMessage);
                                
                                JOptionPane.showMessageDialog(frame, Globals.lang("Could not save file")
                                    + ".\n" + errorMessage, Globals.lang("Save database"),
                                    JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    };

                    
                    (exportWorker.getWorker()).run();
                    
                    exportWorker.update();
                }
			}
		}

		return new ExportAction(frame, selectedOnly);
	}

    
    public static JFileChooser createExportFileChooser(String currentDir) {
		String lastUsedFormat = Globals.prefs.get("lastUsedExport");
		FileFilter defaultFilter = null;
		JFileChooser fc = new JFileChooser(currentDir);
		TreeSet<FileFilter> filters = new TreeSet<FileFilter>();
		for (Map.Entry<String, IExportFormat> e : exportFormats.entrySet()) {
			String formatName = e.getKey() ;
			IExportFormat format = e.getValue();
			filters.add(format.getFileFilter());
			if (formatName.equals(lastUsedFormat))
				defaultFilter = format.getFileFilter();
		}
		for (FileFilter ff : filters) {
			fc.addChoosableFileFilter(ff);
		}
		fc.setAcceptAllFileFilterUsed(false);
		if (defaultFilter != null)
			fc.setFileFilter(defaultFilter);
		return fc;
	}

	private static void putFormat(IExportFormat format) {
		exportFormats.put(format.getConsoleName(), format);
	}

}
