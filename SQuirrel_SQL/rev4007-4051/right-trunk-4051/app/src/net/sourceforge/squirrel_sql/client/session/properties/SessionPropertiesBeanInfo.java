package net.sourceforge.squirrel_sql.client.session.properties;



import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.beans.SimpleBeanInfo;


public class SessionPropertiesBeanInfo extends SimpleBeanInfo
{
   private static PropertyDescriptor[] s_dscrs;
   private static Class<SessionProperties> CLAZZ = SessionProperties.class;

   private interface IPropNames extends SessionProperties.IPropertyNames
   {
      
   }

   public SessionPropertiesBeanInfo() throws IntrospectionException
   {
      super();
      if (s_dscrs == null)
      {
         s_dscrs = new PropertyDescriptor[]
         {
            new PropertyDescriptor(IPropNames.AUTO_COMMIT,
               CLAZZ, "getAutoCommit", "setAutoCommit"),
            new PropertyDescriptor(IPropNames.COMMIT_ON_CLOSING_CONNECTION,
               CLAZZ, "getCommitOnClosingConnection", "setCommitOnClosingConnection"),
            new PropertyDescriptor(IPropNames.CONTENTS_LIMIT_ROWS,
               CLAZZ, "getContentsLimitRows", "setContentsLimitRows"),
            new PropertyDescriptor(IPropNames.CONTENTS_NBR_ROWS_TO_SHOW,
               CLAZZ, "getContentsNbrRowsToShow", "setContentsNbrRowsToShow"),
            new PropertyDescriptor(IPropNames.FONT_INFO,
               CLAZZ, "getFontInfo", "setFontInfo"),
            new PropertyDescriptor(IPropNames.META_DATA_OUTPUT_CLASS_NAME,
               CLAZZ, "getMetaDataOutputClassName", "setMetaDataOutputClassName"),
            new PropertyDescriptor(IPropNames.SHOW_ROW_COUNT,
               CLAZZ, "getShowRowCount", "setShowRowCount"),
            new PropertyDescriptor(IPropNames.SHOW_TOOL_BAR,
               CLAZZ, "getShowToolBar", "setShowToolBar"),
            new PropertyDescriptor(IPropNames.SQL_LIMIT_ROWS,
               CLAZZ, "getSQLLimitRows", "setSQLLimitRows"),
            new PropertyDescriptor(IPropNames.SQL_NBR_ROWS_TO_SHOW,
               CLAZZ, "getSQLNbrRowsToShow", "setSQLNbrRowsToShow"),
            new PropertyDescriptor(IPropNames.SQL_STATEMENT_SEPARATOR_STRING,
               CLAZZ, "getSQLStatementSeparator", "setSQLStatementSeparator"),
            new PropertyDescriptor(IPropNames.SQL_RESULTS_OUTPUT_CLASS_NAME,
               CLAZZ, "getSQLResultsOutputClassName", "setSQLResultsOutputClassName"),
            new PropertyDescriptor(IPropNames.SQL_START_OF_LINE_COMMENT,
               CLAZZ, "getStartOfLineComment", "setStartOfLineComment"),
            new PropertyDescriptor(IPropNames.REMOVE_MULTI_LINE_COMMENT,
               CLAZZ, "getRemoveMultiLineComment", "setRemoveMultiLineComment"),
            new PropertyDescriptor(IPropNames.LIMIT_SQL_ENTRY_HISTORY_SIZE,
               CLAZZ, "getLimitSQLEntryHistorySize", "setLimitSQLEntryHistorySize"),
            new PropertyDescriptor(IPropNames.SQL_ENTRY_HISTORY_SIZE,
               CLAZZ, "getSQLEntryHistorySize", "setSQLEntryHistorySize"),
            new PropertyDescriptor(IPropNames.SQL_SHARE_HISTORY,
               CLAZZ, "getSQLShareHistory", "setSQLShareHistory"),
            new PropertyDescriptor(IPropNames.MAIN_TAB_PLACEMENT,
               CLAZZ, "getMainTabPlacement", "setMainTabPlacement"),
            new PropertyDescriptor(IPropNames.OBJECT_TAB_PLACEMENT,
               CLAZZ, "getObjectTabPlacement", "setObjectTabPlacement"),
            new PropertyDescriptor(IPropNames.SQL_EXECUTION_TAB_PLACEMENT,
               CLAZZ, "getSQLExecutionTabPlacement", "setSQLExecutionTabPlacement"),
            new PropertyDescriptor(IPropNames.SQL_RESULTS_TAB_PLACEMENT,
               CLAZZ, "getSQLResultsTabPlacement", "setSQLResultsTabPlacement"),
            new PropertyDescriptor(IPropNames.TABLE_CONTENTS_OUTPUT_CLASS_NAME,
               CLAZZ, "getTableContentsOutputClassName", "setTableContentsOutputClassName"),
            new PropertyDescriptor(IPropNames.ABORT_ON_ERROR,
               CLAZZ, "getAbortOnError", "setAbortOnError"),
            new PropertyDescriptor(IPropNames.SQL_RESULT_TAB_LIMIT,
               CLAZZ, "getSqlResultTabLimit", "setSqlResultTabLimit"),
            new PropertyDescriptor(IPropNames.LIMIT_SQL_RESULT_TABS,
               CLAZZ, "getLimitSqlResultTabs", "setLimitSqlResultTabs"),
            new PropertyDescriptor(IPropNames.SCHEMA_PREFIX_LIST,
               CLAZZ, "getSchemaPrefixList", "setSchemaPrefixList"),
            new PropertyDescriptor(IPropNames.LOAD_SCHEMAS_CATALOGS,
               CLAZZ, "getLoadSchemasCatalogs", "setLoadSchemasCatalogs"),
            new PropertyDescriptor(IPropNames.SHOW_RESULTS_META_DATA,
               CLAZZ, "getShowResultsMetaData", "setShowResultsMetaData"),
            new PropertyDescriptor(IPropNames.OBJECT_FILTER,
                CLAZZ, "getObjectFilter", "setObjectFilter")
         };
      }
   }

   public PropertyDescriptor[] getPropertyDescriptors()
   {
      return s_dscrs;
   }
}
