package net.sourceforge.squirrel_sql.plugins.dataimport.action;


import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.prefs.Preferences;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.gui.IOkClosePanelListener;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanel;
import net.sourceforge.squirrel_sql.client.gui.OkClosePanelEvent;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.ImportFileType;
import net.sourceforge.squirrel_sql.plugins.dataimport.gui.ImportFileDialog;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.FileImporterFactory;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter;


public class ImportTableDataCommand implements ICommand {
	private static final StringManager stringMgr =
		StringManagerFactory.getStringManager(ImportTableDataCommand.class);

	private static final String PREFS_KEY_LAST_IMPORT_DIRECTORY = "squirrelsql_dataimport_last_import_directory";


	private ISession session;
	private ITableInfo table;


	
	public ImportTableDataCommand(ISession session, ITableInfo table) {
		super();
		this.session = session;
		this.table = table;
	}

	
	public void execute() {
		
		if (JOptionPane.showConfirmDialog(session.getApplication().getMainFrame(),
				stringMgr.getString("ImportTableDataCommand.truncateWarning"),
				stringMgr.getString("ImportTableDataCommand.warning"),
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE,
				null) != JOptionPane.YES_OPTION)
			return;
			
		JFileChooser openFile = new JFileChooser(Preferences.userRoot().get(PREFS_KEY_LAST_IMPORT_DIRECTORY, System.getProperty("user.home")));

		int res = openFile.showOpenDialog(session.getApplication().getMainFrame());

		if (res == JFileChooser.APPROVE_OPTION) {
			File f = openFile.getSelectedFile();

			if(null != f.getParent()){
				Preferences.userRoot().put(PREFS_KEY_LAST_IMPORT_DIRECTORY, f.getParent());
			}

			try {
				TableColumnInfo[] columns = session.getMetaData().getColumnInfo(table);

				ImportFileType type = determineType(f);



				IFileImporter importer = FileImporterFactory.createImporter(type, f);

				if (importer.getConfigurationPanel() != null) {
					
					final JDialog dialog = new JDialog(session.getApplication().getMainFrame(), stringMgr.getString("ImportTableDataCommand.settingsDialogTitle"), true);
					StateListener dialogState = new StateListener(dialog);
					dialog.setLayout(new BorderLayout());
					dialog.add(importer.getConfigurationPanel(), BorderLayout.CENTER);
					OkClosePanel buttons = new OkClosePanel();
					
					buttons.getCloseButton().setText(stringMgr.getString("ImportTableDataCommand.cancel"));
					buttons.addListener(dialogState);
					dialog.add(buttons, BorderLayout.SOUTH);
					dialog.pack();
					GUIUtils.centerWithinParent(dialog);
					dialog.setVisible(true);
					if (!dialogState.isOkPressed()) {
						return;
					}
				}


				final ImportFileDialog importFileDialog = new ImportFileDialog(session, importer, table, columns);

				importFileDialog.setPreviewData(importer.getPreview(10));

				GUIUtils.processOnSwingEventThread(new Runnable() {
					public void run() {
						session.getApplication().getMainFrame().addInternalFrame(importFileDialog, true);
						importFileDialog.moveToFront();
						GUIUtils.centerWithinDesktop(importFileDialog);
						importFileDialog.setVisible(true);
					}
				}, true);

			} catch (SQLException e) {
				
				
				JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), stringMgr.getString("ImportTableDataCommand.sqlErrorOccured"), stringMgr.getString("ImportTableDataCommand.error"), JOptionPane.ERROR_MESSAGE);
			} catch (IOException e) {
				
				JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), stringMgr.getString("ImportTableDataCommand.ioErrorOccured"), stringMgr.getString("ImportTableDataCommand.error"), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private ImportFileType determineType(File f) {
		
		if (f.getName().toLowerCase().endsWith("xls")) {
			return ImportFileType.XLS;
		}
		return ImportFileType.CSV;
	}

	private class StateListener implements IOkClosePanelListener {
		private boolean okPressed = false;
		private JDialog dialog = null;

		
		public StateListener(JDialog dialog) {
			this.dialog = dialog;
		}

		
		public void cancelPressed(OkClosePanelEvent evt) {  }

		
		public void closePressed(OkClosePanelEvent evt) {
			okPressed = false;
			dialog.dispose();
		}

		
		public void okPressed(OkClosePanelEvent evt) {
			okPressed = true;
			dialog.dispose();
		}

		
		public boolean isOkPressed() {
			return okPressed;
		}
	}

}
