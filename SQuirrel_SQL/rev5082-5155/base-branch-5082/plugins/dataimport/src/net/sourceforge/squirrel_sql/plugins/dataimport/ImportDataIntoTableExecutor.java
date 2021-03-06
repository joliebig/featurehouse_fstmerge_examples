package net.sourceforge.squirrel_sql.plugins.dataimport;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLUtilities;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.dataimport.gui.ColumnMappingTableModel;
import net.sourceforge.squirrel_sql.plugins.dataimport.gui.ProgressBarDialog;
import net.sourceforge.squirrel_sql.plugins.dataimport.gui.SpecialColumnMapping;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.IFileImporter;
import net.sourceforge.squirrel_sql.plugins.dataimport.importer.UnsupportedFormatException;
import net.sourceforge.squirrel_sql.plugins.dataimport.prefs.DataImportPreferenceBean;
import net.sourceforge.squirrel_sql.plugins.dataimport.prefs.PreferencesManager;
import net.sourceforge.squirrel_sql.plugins.dataimport.util.DateUtils;


public class ImportDataIntoTableExecutor {
	private final static ILogger log = LoggerController.createLogger(ImportDataIntoTableExecutor.class);
	
    
    private static final StringManager stringMgr =
        StringManagerFactory.getStringManager(ImportDataIntoTableExecutor.class);
    
    
    private Thread execThread = null;
    
    private ISession session = null;
    private ITableInfo table = null;
    private TableColumnInfo[] columns = null;
    private ColumnMappingTableModel columnMapping = null;
    private IFileImporter importer = null;
    private List<String> importerColumns = null;
    private boolean skipHeader = false;

    
    public ImportDataIntoTableExecutor(ISession session, 
    		                           ITableInfo table,
    		                           TableColumnInfo[] columns,
    		                           List<String> importerColumns,
    		                           ColumnMappingTableModel mapping,
    		                           IFileImporter importer) {
    	this.session = session;
    	this.table = table;
    	this.columns = columns;
    	this.columnMapping = mapping;
    	this.importer = importer;
    	this.importerColumns = importerColumns;
    }
    
    
    public void setSkipHeader(boolean skip) {
    	skipHeader = skip;
    }
    
    
    public void execute() {
        Runnable runnable = new Runnable() {
            public void run() {
                _execute();
            }
        };
        execThread = new Thread(runnable);
        execThread.setName("Dataimport Executor Thread");
        execThread.start();
    }

    
    private void _execute() {
    	
    	
    	String columnList = createColumnList();
    	ISQLConnection conn = session.getSQLConnection();
    	
    	StringBuffer insertSQL = new StringBuffer();
    	insertSQL.append("insert into ").append(table.getQualifiedName());
    	insertSQL.append(" (").append(columnList).append(") ");
    	insertSQL.append("VALUES ");
    	insertSQL.append(" (").append(getQuestionMarks(getColumnCount())).append(")");
    	
    	PreparedStatement stmt = null;
    	boolean autoCommit = false;
		int rows = 0;
		boolean success = false;
    	try {
    		DataImportPreferenceBean settings = PreferencesManager.getPreferences();
    		importer.open();
    		if (skipHeader) 
    			importer.next();
    		autoCommit = conn.getAutoCommit();
    		conn.setAutoCommit(false);
    		
    		if (settings.isUseTruncate()) {
    			String sql = "DELETE FROM " + table.getQualifiedName();
    			stmt = conn.prepareStatement(sql);
    			stmt.execute();
    			stmt.close();
    		}
    		
    		stmt = conn.prepareStatement(insertSQL.toString());
    		
    		ProgressBarDialog.getDialog(session.getApplication().getMainFrame(), stringMgr.getString("ImportDataIntoTableExecutor.importingDataInto", table.getSimpleName()), false, null);
    		int inputLines = importer.getRows();
    		if (inputLines > 0) {
    			ProgressBarDialog.setBarMinMax(0, inputLines == -1 ? 5000 : inputLines);
    		} else {
    			ProgressBarDialog.setIndeterminate();
    		}
    	
    		while (importer.next()) {
    			rows++;
    			if (inputLines > 0) {
    				ProgressBarDialog.incrementBar(1);
    			}
    			stmt.clearParameters();
    			int i = 1;
    			for (TableColumnInfo column : columns) {
    				String mapping = getMapping(column);
    				try {
    					if (SpecialColumnMapping.SKIP.getVisibleString().equals(mapping)) {
    						continue;
    					} else if (SpecialColumnMapping.FIXED_VALUE.getVisibleString().equals(mapping)) {
    						bindFixedColumn(stmt, i++, column);
    					} else if (SpecialColumnMapping.AUTO_INCREMENT.getVisibleString().equals(mapping)) {
    						bindAutoincrementColumn(stmt, i++, column, rows);
    					} else if (SpecialColumnMapping.NULL.getVisibleString().equals(mapping)) {
    						stmt.setNull(i++, column.getDataType());
    					} else {
    						bindColumn(stmt, i++, column);
    					}
    				} catch (UnsupportedFormatException ufe) {
    					
    					JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), stringMgr.getString("ImportDataIntoTableExecutor.wrongFormat", new Object[] { rows, i-1 }));
    					throw ufe;
    				}
    			}
    			stmt.execute();
    		}
    		conn.commit();
    		conn.setAutoCommit(autoCommit);
    		importer.close();
    		
    		success = true;
    		
    	} catch (SQLException sqle) {
    		
    		
    		JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), stringMgr.getString("ImportDataIntoTableExecutor.sqlException"), stringMgr.getString("ImportDataIntoTableExecutor.error"), JOptionPane.ERROR_MESSAGE);
    		log.error("Database error", sqle);
    	} catch (UnsupportedFormatException ufe) {
    		try { 
    		    conn.rollback(); 
    		} catch (Exception e) { 
    		    log.error("Unexpected exception while attempting to rollback: "
    		              +e.getMessage(), e);
    		}
    		log.error("Unsupported format.", ufe);
    	} catch (IOException ioe) {
    		
    		JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), stringMgr.getString("ImportDataIntoTableExecutor.ioException"), stringMgr.getString("ImportDataIntoTableExecutor.error"), JOptionPane.ERROR_MESSAGE);
    		log.error("Error while reading file", ioe);
    	} finally {
    		SQLUtilities.closeStatement(stmt);
    		ProgressBarDialog.dispose();
    	}
    	
    	if (success) {
    		
    		JOptionPane.showMessageDialog(session.getApplication().getMainFrame(), stringMgr.getString("ImportDataIntoTableExecutor.success", rows));
    	}
    }
    
    private void bindAutoincrementColumn(PreparedStatement stmt, int index, TableColumnInfo column, int counter) throws SQLException, UnsupportedFormatException  {
    	long value = 0;
    	try {
    		value = Long.parseLong(getFixedValue(column));
    		value += counter;
    	} catch (NumberFormatException nfe) {
    		throw new UnsupportedFormatException();
    	}
    	switch (column.getDataType()) {
    	case Types.BIGINT:
   			stmt.setLong(index, value);
    		break;
    	case Types.INTEGER:
    	case Types.NUMERIC:
   			stmt.setInt(index, (int)value);
    		break;
    	default:
    		throw new UnsupportedFormatException();
    	}
	}

	private void bindFixedColumn(PreparedStatement stmt, int index, TableColumnInfo column) throws SQLException, IOException, UnsupportedFormatException {
    	String value = getFixedValue(column);
    	Date d = null;
    	switch (column.getDataType()) {
    	case Types.BIGINT:
    		try {
    			stmt.setLong(index, Long.parseLong(value));
    		} catch (NumberFormatException nfe) {
    			throw new UnsupportedFormatException();
    		}
    		break;
    	case Types.INTEGER:
    	case Types.NUMERIC:
    		setIntOrUnsignedInt(stmt, index, column);
    		break;
    	case Types.DATE:
    		d = DateUtils.parseSQLFormats(value);
    		if (d == null) 
    			throw new UnsupportedFormatException();
    		stmt.setDate(index, new java.sql.Date(d.getTime()));
    		break;
    	case Types.TIMESTAMP:
    		d = DateUtils.parseSQLFormats(value);
    		if (d == null) 
    			throw new UnsupportedFormatException();
    		stmt.setTimestamp(index, new java.sql.Timestamp(d.getTime()));
    		break;
    	case Types.TIME:
    		d = DateUtils.parseSQLFormats(value);
    		if (d == null) 
    			throw new UnsupportedFormatException();
    		stmt.setTime(index, new java.sql.Time(d.getTime()));
    		break;
    	default:
    		stmt.setString(index, value);
    	}
    }
    
    private void bindColumn(PreparedStatement stmt, int index, TableColumnInfo column) throws SQLException, UnsupportedFormatException, IOException {
    	int mappedColumn = getMappedColumn(column);
		switch (column.getDataType()) {
    	case Types.BIGINT:
    		stmt.setLong(index, importer.getLong(mappedColumn));
    		break;
    	case Types.INTEGER:
    	case Types.NUMERIC:
    		setIntOrUnsignedInt(stmt, index, column);
    		break;
    	case Types.DATE:
    		stmt.setDate(index, new java.sql.Date(importer.getDate(mappedColumn).getTime()));
    		break;
    	case Types.TIMESTAMP:
    		stmt.setTimestamp(index, new java.sql.Timestamp(importer.getDate(mappedColumn).getTime()));
    		break;
    	case Types.TIME:
    		stmt.setTime(index, new java.sql.Time(importer.getDate(mappedColumn).getTime()));
    		break;
    	default:
    		stmt.setString(index, importer.getString(mappedColumn));
    	}
    }
    
	    
    private void setIntOrUnsignedInt(PreparedStatement stmt, int index, TableColumnInfo column) 
    	throws SQLException, UnsupportedFormatException, IOException 
    {
   	int mappedColumn = getMappedColumn(column);
  		String columnTypeName = column.getTypeName(); 
 		if (columnTypeName != null 
 				&& (columnTypeName.endsWith("UNSIGNED") || columnTypeName.endsWith("unsigned"))) 
 		{
 			stmt.setLong(index, importer.getLong(mappedColumn));
 		}
 		
 		try {
 			stmt.setInt(index, importer.getInt(mappedColumn));
 		} catch (UnsupportedFormatException e) {
 			log.error("bindColumn: integer storage overflowed.  Exception was "+e.getMessage()+
 						 ".  Re-trying as a long.", e);
 			
 			stmt.setLong(index, importer.getLong(mappedColumn));
 		}

    }
    
    private int getMappedColumn(TableColumnInfo column) {
    	return importerColumns.indexOf(getMapping(column));
    }
    
    private String getMapping(TableColumnInfo column) {
		int pos = columnMapping.findTableColumn(column.getColumnName());
		return columnMapping.getValueAt(pos, 1).toString();
    }
    
    private String getFixedValue(TableColumnInfo column) {
		int pos = columnMapping.findTableColumn(column.getColumnName());
		return columnMapping.getValueAt(pos, 2).toString();
    }
    
    private String createColumnList() {
    	StringBuffer columnsList = new StringBuffer();
    	for (TableColumnInfo column : columns) {
    		String mapping = getMapping(column);
    		if (SpecialColumnMapping.SKIP.getVisibleString().equals(mapping)) continue;
    		
    		if (columnsList.length() != 0) {
    			columnsList.append(", ");
    		}
    		columnsList.append(column.getColumnName());
    	}
    	return columnsList.toString();
    }
    
    private int getColumnCount() {
    	int count = 0;
    	for (TableColumnInfo column : columns) {
    		int pos = columnMapping.findTableColumn(column.getColumnName());
    		String mapping = columnMapping.getValueAt(pos, 1).toString();
    		if (!SpecialColumnMapping.SKIP.getVisibleString().equals(mapping)) {
    			count++;
    		}
    	}
    	return count;
    }
    
    private String getQuestionMarks(int count) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < count; i++) {
            result.append("?");
            if (i < count-1) {
                result.append(", ");
            }
        }
        return result.toString();
    }    

}
