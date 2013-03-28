
package net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.control;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.model.ControlFileGenerator;



public class GenerateControlFileActionListener implements ActionListener {
	private final JTextField stringDelimitatorTextField;

	private final JTextField fieldSeparatorTextField;

	private final JRadioButton appendRaddioButton;

	private final JTextField directoryTextfield;

	private static ILogger log;

	private ISession session;

	
	public GenerateControlFileActionListener(JTextField stringDelimitatorTextfield, JTextField fieldSeparatorTextfield, JRadioButton appendRadioButton, JTextField controlFileTextfield, ISession session) {
		this.stringDelimitatorTextField = stringDelimitatorTextfield;
		this.fieldSeparatorTextField = fieldSeparatorTextfield;
		this.appendRaddioButton = appendRadioButton;
		this.directoryTextfield = controlFileTextfield;
		this.session = session;
	}

	
	public void actionPerformed(ActionEvent e) {
		
		for (ITableInfo table : session.getSessionInternalFrame()
				.getObjectTreeAPI().getSelectedTables()) {
			String tableName = table.getSimpleName();
			try {
				
				ControlFileGenerator.writeControlFile(tableName,
						getColumnNames(table, session), appendRaddioButton.isSelected(), fieldSeparatorTextField.getText(), stringDelimitatorTextField.getText(), directoryTextfield.getText());
	
				
				JOptionPane.showMessageDialog((Component) e.getSource(), "SQL*Loader control file(s) created in " + directoryTextfield.getText());
			} catch (IOException e1) {
				getLog().error("I/O error while writing control file.", e1);
				e1.printStackTrace();
			} catch (SQLException e2) {
				getLog().error("Error retrieving columns from table "
						+ tableName, e2);
				e2.printStackTrace();
			}
		}
	}

	
	private String[] getColumnNames(ITableInfo table, ISession session) throws SQLException {
		final TableColumnInfo[] columns = session.getMetaData().getColumnInfo(table);
		final int columnCount = columns.length;
		String[] columnNames = new String[columnCount];

		for (int i = 0; i < columnCount; i++) {
			columnNames[i] = columns[i].getColumnName();
		}
		return columnNames;
	}

	
	private ILogger getLog() {
		 
		if (log == null) {
			log = LoggerController.createLogger(GenerateControlFileActionListener.class);
		}
		return log;
	}
}

