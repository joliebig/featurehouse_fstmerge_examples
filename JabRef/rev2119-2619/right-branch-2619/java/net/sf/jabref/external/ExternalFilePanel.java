package net.sf.jabref.external;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.xml.transform.TransformerException;

import net.sf.jabref.BibtexDatabase;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexFields;
import net.sf.jabref.EntryEditor;
import net.sf.jabref.FieldEditor;
import net.sf.jabref.Globals;
import net.sf.jabref.JabRefFrame;
import net.sf.jabref.MetaData;
import net.sf.jabref.OpenFileFilter;
import net.sf.jabref.UrlDragDrop;
import net.sf.jabref.Util;
import net.sf.jabref.net.URLDownload;
import net.sf.jabref.util.XMPUtil;


public class ExternalFilePanel extends JPanel {

	private static final long serialVersionUID = 3653290879640642718L;

	private JButton browseBut, download, auto, xmp;

	private EntryEditor entryEditor;

    private FieldEditor fieldEditor;

    private JabRefFrame frame;

	private OpenFileFilter off;

	private BibtexEntry entry;
	
	private BibtexDatabase database;

	private MetaData metaData;

	public ExternalFilePanel(final String fieldName, final MetaData metaData,
		final BibtexEntry entry, final FieldEditor editor, final OpenFileFilter off) {
		this(null, metaData, null, fieldName, off, null);
		this.entry = entry;
        this.entryEditor = null;
        this.fieldEditor = editor;
    }

	public ExternalFilePanel(final JabRefFrame frame, final MetaData metaData,
		final EntryEditor entryEditor, final String fieldName, final OpenFileFilter off,
		final FieldEditor editor) {

		this.frame = frame;
		this.metaData = metaData;
		this.off = off;
		this.entryEditor = entryEditor;
        this.fieldEditor = null;

        setLayout(new GridLayout(2, 2));

		browseBut = new JButton(Globals.lang("Browse"));
		download = new JButton(Globals.lang("Download"));
		auto = new JButton(Globals.lang("Auto"));
		xmp = new JButton(Globals.lang("Write XMP"));
		xmp.setToolTipText(Globals.lang("Write BibtexEntry as XMP-metadata to PDF."));

		browseBut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				browseFile(fieldName, editor);
				
				entryEditor.storeFieldAction.actionPerformed(new ActionEvent(editor, 0, ""));
			}
		});

		download.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				downLoadFile(fieldName, editor, frame);
			}
		});

		auto.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				autoSetFile(fieldName, editor);
			}
		});
		xmp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pushXMP(fieldName, editor);
			}
		});

		add(browseBut);
		add(download);
		add(auto);
		add(xmp);

		
		if (editor != null)
			((JComponent) editor).setDropTarget(new DropTarget((Component) editor,
				DnDConstants.ACTION_NONE, new UrlDragDrop(entryEditor, frame, editor)));
	}

	
	public void setEntry(BibtexEntry entry, BibtexDatabase database) {
		this.entry = entry;
		this.database = database;
	}
	
	public BibtexDatabase getDatabase(){
		return (database != null ? database : entryEditor.getDatabase());
	}

	public BibtexEntry getEntry() {
		return (entry != null ? entry : entryEditor.getEntry());
	}

	protected Object getKey() {
		return getEntry().getField(BibtexFields.KEY_FIELD);
	}

	protected void output(String s) {
		if (frame != null)
			frame.output(s);
	}

	public void pushXMP(final String fieldName, final FieldEditor editor) {


		(new Thread() {
			public void run() {

				output(Globals.lang("Looking for pdf..."));
				
				
				String dir = metaData.getFileDirectory(fieldName);
				File file = null;
				if (dir != null) {
					File tmp = Util.expandFilename(editor.getText(), new String[] { dir, "." });
					if (tmp != null)
						file = tmp;
				}

				if (file == null) {
					file = new File(editor.getText());
				}

				if (file == null) {
					output(Globals.lang("No file associated"));
					return;
				}
				
				final File finalFile = file;

		
				output(Globals.lang("Writing XMP to '%0'...", finalFile.getName()));
				try {
					XMPUtil.writeXMP(finalFile, getEntry(), getDatabase());
					output(Globals.lang("Wrote XMP to '%0'.", finalFile.getName()));
				} catch (IOException e) {
					JOptionPane.showMessageDialog(editor.getParent(), Globals.lang(
						"Error writing XMP to file: %0", e.getLocalizedMessage()), Globals
						.lang("Writing XMP"), JOptionPane.ERROR_MESSAGE);
					Globals.logger(Globals.lang("Error writing XMP to file: %0", finalFile
						.getAbsolutePath()));
					output(Globals.lang("Error writing XMP to file: %0", finalFile.getName()));
					
				} catch (TransformerException e) {
					JOptionPane.showMessageDialog(editor.getParent(), Globals.lang(
						"Error converting Bibtex to XMP: %0", e.getLocalizedMessage()), Globals
						.lang("Writing XMP"), JOptionPane.ERROR_MESSAGE);
					Globals.logger(Globals.lang("Error while converting BibtexEntry to XMP %0",
						finalFile.getAbsolutePath()));
					output(Globals.lang("Error converting XMP to '%0'...", finalFile.getName()));
				}
			}
		}).start();
	}

	public void browseFile(final String fieldName, final FieldEditor editor) {

		String directory = metaData.getFileDirectory(fieldName);
		if ((directory != null) && directory.equals(""))
			directory = null;

		String dir = editor.getText(), retVal = null;

		if ((directory == null) || !(new File(dir)).isAbsolute()) {
			if (directory != null)
				dir = directory;
			else
				dir = Globals.prefs.get(fieldName + Globals.FILETYPE_PREFS_EXT, "");
		}

		String chosenFile = Globals.getNewFile(frame, new File(dir), "." + fieldName,
			JFileChooser.OPEN_DIALOG, false);

		if (chosenFile != null) {
			File newFile = new File(chosenFile);
			String position = newFile.getParent();

			if ((directory != null) && position.startsWith(directory)) {
				
				String relPath = position.substring(directory.length(), position.length())
					+ File.separator + newFile.getName();

				
				if (relPath.startsWith(File.separator)) {
					relPath = relPath.substring(File.separator.length(), relPath.length());

					
				}

				retVal = relPath;
			} else
				retVal = newFile.getPath();

			editor.setText(retVal);
			Globals.prefs.put(fieldName + Globals.FILETYPE_PREFS_EXT, newFile.getPath());
		}
	}

	public void downLoadFile(final String fieldName, final FieldEditor fieldEditor,
		final Component parent) {

		final String res = JOptionPane.showInputDialog(parent, Globals
			.lang("Enter URL to download"));

		if (res == null || res.trim().length() == 0)
			return;

		
		final BibtexEntry targetEntry;
		if (entryEditor != null)
			targetEntry = entryEditor.getEntry();
		else
			targetEntry = entry;

		(new Thread() {

			public String getPlannedFileName(String res) {
				String suffix = off.getSuffix(res);
				if (suffix == null)
					suffix = "." + fieldName.toLowerCase();

				String plannedName = null;
				if (getKey() != null)
					plannedName = getKey() + suffix;
				else {
					plannedName = JOptionPane.showInputDialog(parent, Globals
						.lang("BibTeX key not set. Enter a name for the downloaded file"));
					if (plannedName != null && !off.accept(plannedName))
						plannedName += suffix;
				}

				
				if (Globals.ON_WIN) {
					plannedName = plannedName.replaceAll(
						"\\?|\\*|\\<|\\>|\\||\\\"|\\:|\\.$|\\[|\\]", "");
				} else if (Globals.ON_MAC) {
					plannedName = plannedName.replaceAll(":", "");
				}

				return plannedName;
			}

			public void run() {
				String originalText = fieldEditor.getText();
				fieldEditor.setEnabled(false);
				boolean updateEditor = true;

				try {
					fieldEditor.setText(Globals.lang("Downloading..."));
					output(Globals.lang("Downloading..."));
					String plannedName = getPlannedFileName(res);

					
					String directory = metaData.getFileDirectory(fieldName);

					if (!new File(directory).exists()) {
						JOptionPane.showMessageDialog(parent, Globals.lang(
							"Could not find directory for %0-files: %1", fieldName, directory),
							Globals.lang("Download file"), JOptionPane.ERROR_MESSAGE);
						Globals.logger(Globals.lang("Could not find directory for %0-files: %1",
							fieldName, directory));
						return;
					}
					File file = new File(new File(directory), plannedName);

					URL url = new URL(res);

					URLDownload udl = new URLDownload(parent, url, file);
					try {
						udl.download();
					} catch (IOException e2) {
						JOptionPane.showMessageDialog(parent, Globals.lang("Invalid URL")+": "
							+ e2.getMessage(), Globals.lang("Download file"),
							JOptionPane.ERROR_MESSAGE);
						Globals.logger("Error while downloading " + url.toString());
						return;
					}
					output(Globals.lang("Download completed"));

					String textToSet = file.getPath();
					if (textToSet.startsWith(directory)) {
						
						textToSet = textToSet.substring(directory.length(), textToSet.length());

						
						if (textToSet.startsWith(File.separator)) {
							textToSet = textToSet.substring(File.separator.length());
						}
					}

					
                    if (entryEditor == null || entryEditor.getEntry() != targetEntry) {
						
						targetEntry.setField(fieldName, textToSet);
                        if (fieldEditor != null) {
                            fieldEditor.setText(textToSet);
                            fieldEditor.setEnabled(true);
                        }
                        updateEditor = false;
					} else {
						
						fieldEditor.setText(textToSet);
						fieldEditor.setEnabled(true);
						updateEditor = false;
						SwingUtilities.invokeLater(new Thread() {
							public void run() {
								entryEditor.updateField(fieldEditor);
							}
						});
					}

				} catch (MalformedURLException e1) {
					JOptionPane.showMessageDialog(parent, Globals.lang("Invalid URL"), Globals
						.lang("Download file"), JOptionPane.ERROR_MESSAGE);
				} finally {
					
					
					if (updateEditor) {
						fieldEditor.setText(originalText);
						fieldEditor.setEnabled(true);
					}
                }
			}
		}).start();
	}

	
	public Thread autoSetFile(final String fieldName, final FieldEditor editor) {
		Object o = getKey();
		if ((o == null) || (Globals.prefs.get(fieldName + "Directory") == null)) {
			output(Globals.lang("You must set both BibTeX key and %0 directory", fieldName
				.toUpperCase())
				+ ".");
			return null;
		}
		output(Globals.lang("Searching for %0 file", fieldName.toUpperCase()) + " '" + o + "."
			+ fieldName + "'...");
		Thread t = (new Thread() {
			public void run() {
				
				LinkedList<String> list = new LinkedList<String>();
				list.add(metaData.getFileDirectory(fieldName));

				
				list.add(".");

				String found = Util.findPdf(getEntry(), fieldName, (String[]) list
					.toArray(new String[list.size()]));
                                        
                                
				
				
				

				if (found != null) {
					editor.setText(found);
					if (entryEditor != null)
						entryEditor.updateField(editor);
					output(Globals.lang("%0 field set", fieldName.toUpperCase()) + ".");
				} else {
					output(Globals.lang("No %0 found", fieldName.toUpperCase()) + ".");
				}

			}
		});

		t.start();
		return t;

	}

}