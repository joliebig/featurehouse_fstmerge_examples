package net.sourceforge.squirrel_sql.client.session.properties;

import java.awt.Font;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

import javax.swing.SwingConstants;

import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerEditableTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTablePanel;
import net.sourceforge.squirrel_sql.fw.datasetviewer.DataSetViewerTextPanel;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.util.ISessionProperties;
import net.sourceforge.squirrel_sql.fw.util.PropertyChangeReporter;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

public class SessionProperties implements Cloneable, Serializable, ISessionProperties
{
   public interface IDataSetDestinations
   {
      String TEXT = DataSetViewerTextPanel.class.getName();
      String READ_ONLY_TABLE = DataSetViewerTablePanel.class.getName();
      String EDITABLE_TABLE = DataSetViewerEditableTablePanel.class.getName();
   }

   public interface IPropertyNames
   {
      String SQL_RESULT_TAB_LIMIT = "sqlResultTabLimit";
      String ABORT_ON_ERROR = "abortOnError";
      String WRITE_SQL_ERRORS_TO_LOG = "writeSQLErrorsToLog";
      String LOAD_COLUMNS_IN_BACKGROUND = "loadColumnsInBackground";
      String AUTO_COMMIT = "autoCommit";
      String CATALOG_PREFIX_LIST = "catalogPrefixList";
      String COMMIT_ON_CLOSING_CONNECTION = "commitOnClosingConnection";
      String CONTENTS_LIMIT_ROWS = "contentsLimitRows";
      String CONTENTS_NBR_ROWS_TO_SHOW = "contentsNbrOfRowsToShow";
      String FONT_INFO = "fontInfo";
      String OBJECT_FILTER = "objectFilter";
      String LARGE_RESULT_SET_OBJECT_INFO = "largeResultSetObjectInfo";
      String LIMIT_SQL_ENTRY_HISTORY_SIZE = "limitSqlEntryHistorySize";
      String LOAD_SCHEMAS_CATALOGS = "loadCatalogsSchemas";
      String MAIN_TAB_PLACEMENT = "mainTabPlacement";
      String META_DATA_OUTPUT_CLASS_NAME = "metaDataOutputClassName";
      String OBJECT_TAB_PLACEMENT = "objectTabPlacement";
      String SCHEMA_PREFIX_LIST = "schemaPrefixList";
      String SQL_ENTRY_HISTORY_SIZE = "sqlEntryHistorySize";
      String SHOW_RESULTS_META_DATA = "showResultsMetaData";
      String SHOW_ROW_COUNT = "showRowCount";
      String SHOW_TOOL_BAR = "showToolBar";
      String SQL_SHARE_HISTORY = "sqlShareHistory";
      String SQL_EXECUTION_TAB_PLACEMENT = "sqlExecutionTabPlacement";
      String SQL_RESULTS_TAB_PLACEMENT = "sqlResultsTabPlacement";
      String SQL_LIMIT_ROWS = "sqlLimitRows";
      String SQL_NBR_ROWS_TO_SHOW = "sqlNbrOfRowsToShow";
      String SQL_RESULTS_OUTPUT_CLASS_NAME = "sqlResultsOutputClassName";
      String SQL_START_OF_LINE_COMMENT = "sqlStartOfLineComment";
      String SQL_STATEMENT_SEPARATOR_STRING = "sqlStatementSeparatorString";
      String TABLE_CONTENTS_OUTPUT_CLASS_NAME = "tableContentsOutputClassName";
      String LIMIT_SQL_RESULT_TABS = "limitSqlResultTabs";
      String REMOVE_MULTI_LINE_COMMENT = "removeMultiLineComment";
   }

   private static final FontInfo DEFAULT_FONT_INFO =
                           new FontInfo(new Font("Monospaced", 0, 12));

   
   private transient PropertyChangeReporter _propChgReporter;

   private boolean _autoCommit = true;
   private int _contentsNbrRowsToShow = 100;
   private int _sqlNbrRowsToShow = 100;

   
   private boolean _commitOnClosingConnection = false;

   private boolean _contentsLimitRows = true;
   private boolean _sqlLimitRows = true;

   
   private boolean _loadSchemasCatalogs = true;

   
   private String _schemaPrefixList = "";

   
   private String _catalogPrefixList = "";

   

   private String _objectFilter = "";

   
   private boolean _showResultsMetaData = true;

   
   private String _metaDataOutputClassName = IDataSetDestinations.READ_ONLY_TABLE;

   


   
   private String _tableContentsClassName = IDataSetDestinations.READ_ONLY_TABLE;

   
   private String _sqlResultsOutputClassName = IDataSetDestinations.READ_ONLY_TABLE;

   
   private boolean _showRowCount = false;

   
   private boolean _showToolbar = true;

   
   private String _sqlStmtSep = ";";

   
   private String _solComment = "--";

   private boolean _removeMultiLineComment = true;
   
   
   private FontInfo _fi = (FontInfo)DEFAULT_FONT_INFO.clone();

   
   private boolean _limitSqlEntryHistorySize = true;

   
   private boolean _sqlShareHistory = true;

   
   private int _sqlEntryHistorySize = 100;

   
   private int _mainTabPlacement = SwingConstants.TOP;

   
   private int _objectTabPlacement = SwingConstants.TOP;


   
   private int _sqlExecutionTabPlacement = SwingConstants.TOP;

   
   private int _sqlResultsTabPlacement = SwingConstants.TOP;

   
   private boolean _abortOnError = true;

   
   private boolean _writeSQLErrorsToLog;


   
   private boolean _loadColumnsInBackground;


   
   private boolean _limitSqlResultTabs = true;


   
   private int _sqlResultTabLimit = 15;

   
   public SessionProperties()
   {
      super();
   }

   
   public Object clone()
   {
      try
      {
         SessionProperties props = (SessionProperties)super.clone();
         props._propChgReporter = null;
         if (_fi != null)
         {
            props.setFontInfo((FontInfo)_fi.clone());
         }


         return props;
      }
      catch (CloneNotSupportedException ex)
      {
         throw new InternalError(ex.getMessage()); 
      }
   }

   
   public String getReadOnlyTableOutputClassName()
   {
      return IDataSetDestinations.READ_ONLY_TABLE;
   }

   public String getEditableTableOutputClassName()
   {
      return IDataSetDestinations.EDITABLE_TABLE;
   }

   
   public String getReadOnlySQLResultsOutputClassName()
   {
      if (_sqlResultsOutputClassName.equals(IDataSetDestinations.EDITABLE_TABLE))
         return IDataSetDestinations.READ_ONLY_TABLE;
      return _sqlResultsOutputClassName;
   }

   public void addPropertyChangeListener(PropertyChangeListener listener)
   {
      getPropertyChangeReporter().addPropertyChangeListener(listener);
   }

   public void removePropertyChangeListener(PropertyChangeListener listener)
   {
      getPropertyChangeReporter().removePropertyChangeListener(listener);
   }

   public String getMetaDataOutputClassName()
   {
      return _metaDataOutputClassName;
   }

   public void setMetaDataOutputClassName(String value)
   {
      if (value == null)
      {
         value = "";
      }
      if (!_metaDataOutputClassName.equals(value))
      {
         final String oldValue = _metaDataOutputClassName;
         _metaDataOutputClassName = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.META_DATA_OUTPUT_CLASS_NAME,
            oldValue, _metaDataOutputClassName);
      }
   }

   public String getTableContentsOutputClassName()
   {
      return _tableContentsClassName;
   }

   public void setTableContentsOutputClassName(String value)
   {
      if (value == null)
      {
         value = "";
      }
      if (!_tableContentsClassName.equals(value))
      {
         final String oldValue = _tableContentsClassName;
         _tableContentsClassName= value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.TABLE_CONTENTS_OUTPUT_CLASS_NAME,
            oldValue, _tableContentsClassName);
      }
   }

   
   public String getSQLResultsOutputClassName()
   {
      return _sqlResultsOutputClassName;
   }

   
   public void setSQLResultsOutputClassName(String value)
   {
      if (value == null)
      {
         value = "";
      }
      if (!_sqlResultsOutputClassName.equals(value))
      {
         final String oldValue = _sqlResultsOutputClassName;
         _sqlResultsOutputClassName = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.SQL_RESULTS_OUTPUT_CLASS_NAME,
            oldValue, _sqlResultsOutputClassName);
      }
   }

   














   public boolean getAutoCommit()
   {
      return _autoCommit;
   }

   public void setAutoCommit(boolean value)
   {
      if (_autoCommit != value)
      {
         _autoCommit = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.AUTO_COMMIT,
            !_autoCommit, _autoCommit);
      }
   }

   public boolean getAbortOnError()
   {
      return _abortOnError;
   }

   public void setAbortOnError(boolean value)
   {
      if (_abortOnError != value)
      {
         _abortOnError = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.ABORT_ON_ERROR,
            !_abortOnError, _abortOnError);
      }
   }

   public boolean getWriteSQLErrorsToLog()
   {
      return _writeSQLErrorsToLog;
   }

   public void setWriteSQLErrorsToLog(boolean value)
   {
      if (_writeSQLErrorsToLog != value)
      {
         _writeSQLErrorsToLog = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.WRITE_SQL_ERRORS_TO_LOG,
            !_writeSQLErrorsToLog, _writeSQLErrorsToLog);
      }
   }


   public boolean getLoadColumnsInBackground()
   {
      return _loadColumnsInBackground;
   }

   public void setLoadColumnsInBackground(boolean value)
   {
      if (_loadColumnsInBackground != value)
      {
         _loadColumnsInBackground = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.LOAD_COLUMNS_IN_BACKGROUND,
            !_loadColumnsInBackground, _loadColumnsInBackground);
      }
   }



   public boolean getLimitSQLResultTabs()
   {
      return _limitSqlResultTabs;
   }

   public void setLimitSQLResultTabs(boolean data)
   {
      final boolean oldValue = _limitSqlResultTabs;
      _limitSqlResultTabs = data;
      getPropertyChangeReporter().firePropertyChange(IPropertyNames.LIMIT_SQL_RESULT_TABS,
                           oldValue, _limitSqlResultTabs);
   }



   public int getSqlResultTabLimit()
   {
      return _sqlResultTabLimit;
   }

   public void setSqlResultTabLimit(int value)
   {
      if (_sqlResultTabLimit != value)
      {
         int oldValue = _sqlResultTabLimit;
         _sqlResultTabLimit = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.SQL_RESULT_TAB_LIMIT,
            oldValue, _sqlResultTabLimit);
      }
   }


   public boolean getShowToolBar()
   {
      return _showToolbar;
   }

   public void setShowToolBar(boolean value)
   {
      if (_showToolbar != value)
      {
         _showToolbar = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.SHOW_TOOL_BAR,
            !_showToolbar, _showToolbar);
      }
   }

   public int getContentsNbrRowsToShow()
   {
      return _contentsNbrRowsToShow;
   }

   public void setContentsNbrRowsToShow(int value)
   {
      if (_contentsNbrRowsToShow != value)
      {
         final int oldValue = _contentsNbrRowsToShow;
         _contentsNbrRowsToShow = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.CONTENTS_NBR_ROWS_TO_SHOW,
            oldValue, _contentsNbrRowsToShow);
      }
   }

   public int getSQLNbrRowsToShow()
   {
      return _sqlNbrRowsToShow;
   }

   public void setSQLNbrRowsToShow(int value)
   {
      if (_sqlNbrRowsToShow != value)
      {
         final int oldValue = _sqlNbrRowsToShow;
         _sqlNbrRowsToShow = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.SQL_NBR_ROWS_TO_SHOW,
            oldValue, _sqlNbrRowsToShow);
      }
   }

   public boolean getContentsLimitRows()
   {
      return _contentsLimitRows;
   }

   public void setContentsLimitRows(boolean value)
   {
      if (_contentsLimitRows != value)
      {
         final boolean oldValue = _contentsLimitRows;
         _contentsLimitRows = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.CONTENTS_LIMIT_ROWS,
            oldValue, _contentsLimitRows);
      }
   }

   public boolean getSQLLimitRows()
   {
      return _sqlLimitRows;
   }

   public void setSQLLimitRows(boolean value)
   {
      if (_sqlLimitRows != value)
      {
         final boolean oldValue = _sqlLimitRows;
         _sqlLimitRows = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.SQL_LIMIT_ROWS,
            oldValue, _sqlLimitRows);
      }
   }


   
   public String getSQLStatementSeparator()
   {
      return _sqlStmtSep;
   }

   
   public void setSQLStatementSeparator(String value)
   {
      
      
      if(null == value || 0 == value.trim().length())
      {
         value =";";
      }

      if (!_sqlStmtSep.equals(value))
      {
         final String oldValue = _sqlStmtSep;
         _sqlStmtSep = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.SQL_STATEMENT_SEPARATOR_STRING,
            oldValue, _sqlStmtSep);
      }
   }

   public boolean getCommitOnClosingConnection()
   {
      return _commitOnClosingConnection;
   }

   public synchronized void setCommitOnClosingConnection(boolean data)
   {
      final boolean oldValue = _commitOnClosingConnection;
      _commitOnClosingConnection = data;
      getPropertyChangeReporter().firePropertyChange(
         IPropertyNames.COMMIT_ON_CLOSING_CONNECTION,
         oldValue, _commitOnClosingConnection);
   }

   
   public boolean getShowRowCount()
   {
      return _showRowCount;
   }

   
   public synchronized void setShowRowCount(boolean data)
   {
      final boolean oldValue = _showRowCount;
      _showRowCount = data;
      getPropertyChangeReporter().firePropertyChange(
         IPropertyNames.SHOW_ROW_COUNT,
         oldValue, _showRowCount);
   }

   
   public String getStartOfLineComment()
   {
      return _solComment;
   }


   
   public synchronized void setStartOfLineComment(String data)
   {
      final String oldValue = _solComment;
      _solComment = data;
      getPropertyChangeReporter().firePropertyChange(
                           IPropertyNames.SQL_START_OF_LINE_COMMENT,
                           oldValue, _solComment);
   }

   public boolean getRemoveMultiLineComment()
   {
      return _removeMultiLineComment;
   }

   public synchronized void setRemoveMultiLineComment(boolean data)
   {
      final boolean oldValue = _removeMultiLineComment;
      _removeMultiLineComment = data;
      getPropertyChangeReporter().firePropertyChange(
         IPropertyNames.REMOVE_MULTI_LINE_COMMENT,
         oldValue, _removeMultiLineComment);
   }



   public FontInfo getFontInfo()
   {
      return _fi;
   }

   public void setFontInfo(FontInfo data)
   {
      if (_fi == null || !_fi.equals(data))
      {
         final FontInfo oldValue = _fi;
         _fi = data != null ? data : (FontInfo)DEFAULT_FONT_INFO.clone();
         getPropertyChangeReporter().firePropertyChange(
                           IPropertyNames.FONT_INFO, oldValue, _fi);
      }
   }


   public boolean getLimitSQLEntryHistorySize()
   {
      return _limitSqlEntryHistorySize;
   }

   public void setLimitSQLEntryHistorySize(boolean data)
   {
      final boolean oldValue = _limitSqlEntryHistorySize;
      _limitSqlEntryHistorySize = data;
      getPropertyChangeReporter().firePropertyChange(IPropertyNames.LIMIT_SQL_ENTRY_HISTORY_SIZE,
                           oldValue, _limitSqlEntryHistorySize);
   }

   
   public boolean getSQLShareHistory()
   {
      return _sqlShareHistory;
   }

   
   public void setSQLShareHistory(boolean data)
   {
      final boolean oldValue = _sqlShareHistory;
      _sqlShareHistory = data;
      getPropertyChangeReporter().firePropertyChange(IPropertyNames.SQL_SHARE_HISTORY,
                              oldValue, _sqlShareHistory);
   }

   public int getSQLEntryHistorySize()
   {
      return _sqlEntryHistorySize;
   }

   public void setSQLEntryHistorySize(int data)
   {
      final int oldValue = _sqlEntryHistorySize;
      _sqlEntryHistorySize = data;
      getPropertyChangeReporter().firePropertyChange(
         IPropertyNames.SQL_ENTRY_HISTORY_SIZE,
         oldValue, _sqlEntryHistorySize);
   }

   public int getMainTabPlacement()
   {
      return _mainTabPlacement;
   }

   public void setMainTabPlacement(int value)
   {
      if (_mainTabPlacement != value)
      {
         final int oldValue = _mainTabPlacement;
         _mainTabPlacement = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.MAIN_TAB_PLACEMENT,
            oldValue, _mainTabPlacement);
      }
   }

   public int getObjectTabPlacement()
   {
      return _objectTabPlacement;
   }

   public void setObjectTabPlacement(int value)
   {
      if (_objectTabPlacement != value)
      {
         final int oldValue = _objectTabPlacement;
         _objectTabPlacement = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.OBJECT_TAB_PLACEMENT,
            oldValue, _objectTabPlacement);
      }
   }

   public int getSQLExecutionTabPlacement()
   {
      return _sqlExecutionTabPlacement;
   }

   public void setSQLExecutionTabPlacement(int value)
   {
      if (_sqlExecutionTabPlacement != value)
      {
         final int oldValue = _sqlExecutionTabPlacement;
         _sqlExecutionTabPlacement = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.SQL_EXECUTION_TAB_PLACEMENT,
            oldValue, _sqlExecutionTabPlacement);
      }
   }

   public int getSQLResultsTabPlacement()
   {
      return _sqlResultsTabPlacement;
   }

   public void setSQLResultsTabPlacement(int value)
   {
      if (_sqlResultsTabPlacement != value)
      {













         final int oldValue = _sqlResultsTabPlacement;
         _sqlResultsTabPlacement = value;
         getPropertyChangeReporter().firePropertyChange(
            IPropertyNames.SQL_RESULTS_TAB_PLACEMENT,
            oldValue, _sqlResultsTabPlacement);
      }
   }

   
   public String getSchemaPrefixList()
   {
      return _schemaPrefixList;
   }

   
   public String[] getSchemaPrefixArray()
   {
      return StringUtilities.split(_schemaPrefixList, ',', true);
   }

   
   public synchronized void setSchemaPrefixList(String data)
   {
      final String oldValue = _schemaPrefixList;
      _schemaPrefixList = data;
      getPropertyChangeReporter().firePropertyChange(
         IPropertyNames.SCHEMA_PREFIX_LIST,
         oldValue,
         _schemaPrefixList);
   }

   
   public String getCatalogPrefixList()
   {
      return _catalogPrefixList;
   }

   public String getObjectFilter()
   {
      return _objectFilter;
   }

   
   public String[] getCatalogPrefixArray()
   {
      return StringUtilities.split(_catalogPrefixList, ',', true);
   }

   
   public synchronized void setCatalogPrefixList(String data)
   {
      final String oldValue = _catalogPrefixList;
      _catalogPrefixList = data;
      getPropertyChangeReporter().firePropertyChange(IPropertyNames.CATALOG_PREFIX_LIST,
                                    oldValue, _catalogPrefixList);
   }

   

   public synchronized void setObjectFilter(String data) {
      final String oldValue = _objectFilter;
      _objectFilter = data;
      getPropertyChangeReporter().firePropertyChange(IPropertyNames.OBJECT_FILTER, oldValue, _objectFilter);
   }

   
   public boolean getLoadSchemasCatalogs()
   {
      return _loadSchemasCatalogs;
   }

   
   public synchronized void setLoadSchemasCatalogs(boolean data)
   {
      final boolean oldValue = _loadSchemasCatalogs;
      _loadSchemasCatalogs = data;
      getPropertyChangeReporter().firePropertyChange(
         IPropertyNames.LOAD_SCHEMAS_CATALOGS, oldValue, _loadSchemasCatalogs);
   }

   
   public synchronized void setShowResultsMetaData(boolean data)
   {
      final boolean oldValue = _showResultsMetaData;
      _showResultsMetaData = data;
      getPropertyChangeReporter().firePropertyChange(
                     IPropertyNames.SHOW_RESULTS_META_DATA,
                     oldValue, _showResultsMetaData);
   }

   
   public boolean getShowResultsMetaData()
   {
      return _showResultsMetaData;
   }

   private synchronized PropertyChangeReporter getPropertyChangeReporter()
   {
      if (_propChgReporter == null)
      {
         _propChgReporter = new PropertyChangeReporter(this);
      }
      return _propChgReporter;
   }
}
