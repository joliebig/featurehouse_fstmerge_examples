

package net.sourceforge.squirrel_sql.fw.dialects;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.fw.sql.ForeignKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IndexInfo;
import net.sourceforge.squirrel_sql.fw.sql.JDBCTypeMapper;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import org.hibernate.HibernateException;


public class DialectUtils {

    
    private static final ILogger log = 
        LoggerController.createLogger(DialectUtils.class);    
    
    
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(DialectUtils.class);
    
    
    
    public static final String ALTER_COLUMN_CLAUSE = "ALTER COLUMN";
    
    public static final String MODIFY_COLUMN_CLAUSE = "MODIFY COLUMN";
    
    public static final String MODIFY_CLAUSE = "MODIFY";
    
    public static final String COLUMN_CLAUSE = "COLUMN";
    
    
    
    public static final String RENAME_COLUMN_CLAUSE = "RENAME COLUMN";
    
    public static final String RENAME_TO_CLAUSE = "RENAME TO";
    
    public static final String TO_CLAUSE = "TO";
    
    
    
    public static final String DEFAULT_CLAUSE = "DEFAULT";
    
    public static final String SET_DEFAULT_CLAUSE = "SET DEFAULT";
    
    public static final String SET_CLAUSE = "SET";
    
    public static final String ADD_DEFAULT_CLAUSE = "ADD DEFAULT";
    
    public static final String DROP_DEFAULT_CLAUSE = "DROP DEFAULT";
    
    
    
    public static final String TYPE_CLAUSE = "TYPE";
    
    public static final String SET_DATA_TYPE_CLAUSE = "SET DATA TYPE";
    
    
    
    public static final String DROP_CLAUSE = "DROP";
    
    public static final String DROP_COLUMN_CLAUSE = "DROP COLUMN";
    
    
    
    public static final String CASCADE_CLAUSE = "CASCADE";
    
    public static final String CASCADE_CONSTRAINTS_CLAUSE = "CASCADE CONSTRAINTS";
    
    
    
    public static final int COLUMN_COMMENT_ALTER_TYPE = 0;
    public static final int COLUMN_DEFAULT_ALTER_TYPE = 1;
    public static final int COLUMN_DROP_TYPE = 2;
    public static final int COLUMN_NAME_ALTER_TYPE = 3;
    public static final int COLUMN_NULL_ALTER_TYPE = 4;
    public static final int COLUMN_TYPE_ALTER_TYPE = 5;
    public static final int ADD_PRIMARY_KEY_TYPE = 6;
    public static final int DROP_PRIMARY_KEY_TYPE = 7;
    
    
    
    
    public static String getColumnAddSQL(TableColumnInfo info, 
                                         HibernateDialect dialect,
                                         boolean addDefaultClause,
                                         boolean supportsNullQualifier, 
                                         boolean addNullClause) 
        throws UnsupportedOperationException, HibernateException 
    {
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ");
        result.append(dialect.getAddColumnString().toUpperCase());
        result.append(" ");
        result.append(info.getColumnName());
        result.append(" ");
        result.append(dialect.getTypeName(info.getDataType(), 
                                                info.getColumnSize(), 
                                                info.getColumnSize(), 
                                                info.getDecimalDigits()));

        if (addDefaultClause) {
            appendDefaultClause(info, result);
        }
        if (addNullClause) {
            if (info.isNullable().equals("NO")) {
                result.append(" NOT NULL ");
            } else {
                if (supportsNullQualifier) {
                    result.append(" NULL ");
                }
            }
        }
        return result.toString();
    }
    
    public static String appendDefaultClause(TableColumnInfo info, 
                                             StringBuilder buffer) {

        if (info.getDefaultValue() != null 
                && !"".equals(info.getDefaultValue())) 
        {
            buffer.append(" DEFAULT ");
            if (JDBCTypeMapper.isNumberType(info.getDataType())) {
                buffer.append(info.getDefaultValue());
            } else {
                buffer.append("'");
                buffer.append(info.getDefaultValue());
                buffer.append("'");                
            }
        }                    
        return buffer.toString();
    }
    
    
    public static String getColumnCommentAlterSQL(String tableName, 
                                                  String columnName, 
                                                  String comment) 
    {
        StringBuilder result = new StringBuilder();
        result.append("COMMENT ON COLUMN ");
        result.append(tableName);
        result.append(".");
        result.append(columnName);
        result.append(" IS '");
        if (comment != null && !"".equals(comment)) {
            result.append(comment);
        }
        result.append("'");
        return result.toString();
    }
    
    
    public static String getColumnCommentAlterSQL(TableColumnInfo info) {
        return getColumnCommentAlterSQL(info.getTableName(), 
                                        info.getColumnName(), 
                                        info.getRemarks());
    }
   
    
    
    public static String getColumnDropSQL(String tableName, 
                                          String columnName) {
        return getColumnDropSQL(tableName, columnName, "DROP", false, null);
    }

    
    public static String getColumnDropSQL(String tableName, 
                                          String columnName,
                                          String dropClause, 
                                          boolean addConstraintClause, 
                                          String constraintClause) {
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ");
        result.append(tableName);
        result.append(" ");
        result.append(dropClause);
        result.append(" ");
        result.append(columnName);
        if (addConstraintClause) {
            result.append(" ");
            result.append(constraintClause);
        }
        return result.toString();
    }
    
    
    public static List<String> getTableDropSQL(ITableInfo iTableInfo, 
                                         boolean supportsCascade, 
                                         boolean cascadeValue, 
                                         boolean supportsMatViews, 
                                         String cascadeClause, 
                                         boolean isMatView) 
    {
        StringBuilder result = new StringBuilder();
        if (supportsMatViews && isMatView) {
            result.append("DROP MATERIALIZED VIEW ");
        } else {
            result.append("DROP TABLE ");
        }
        result.append(iTableInfo.getQualifiedName());
        if (supportsCascade && cascadeValue) {
            result.append(" ");
            result.append(cascadeClause);
        }
        return Arrays.asList(new String[] { result.toString() });
    }
    
    public static String getTypeName(TableColumnInfo info, 
                                     HibernateDialect dialect) 
    {
        return dialect.getTypeName(info.getDataType(), 
                                   info.getColumnSize(), 
                                   info.getColumnSize(), 
                                   info.getDecimalDigits());
    }
    
    
    public static String getColumnNullableAlterSQL(TableColumnInfo info, 
                                                   HibernateDialect dialect,
                                                   String alterClause,
                                                   boolean specifyType) 
    {
        boolean nullable = info.isNullable().equalsIgnoreCase("YES");
        return getColumnNullableAlterSQL(info, 
                                         nullable, 
                                         dialect, 
                                         alterClause, 
                                         specifyType);
    }
    
    
    public static String getColumnNullableAlterSQL(TableColumnInfo info,
                                                   boolean nullable,
                                                   HibernateDialect dialect,
                                                   String alterClause,
                                                   boolean specifyType) 
    {
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ");
        result.append(alterClause);
        result.append(" ");
        result.append(info.getColumnName());
        if (specifyType) {
            result.append(" ");
            result.append(getTypeName(info, dialect));
            result.append(" ");
        }
        if (nullable) { 
            result.append(" NULL");
        } else {
            result.append(" NOT NULL");
        }
        return result.toString();
    }
    
    
    public static void getMultiColNotNullSQL(TableColumnInfo[] colInfos,  
                                             HibernateDialect dialect,
                                             String alterClause,
                                             boolean specifyType,
                                             ArrayList<String> result) 
    {
        for (int i = 0; i < colInfos.length; i++) {
            StringBuilder notNullSQL = new StringBuilder();
            notNullSQL.append("ALTER TABLE ");
            notNullSQL.append(colInfos[i].getTableName());
            notNullSQL.append(" ");
            notNullSQL.append(alterClause);
            notNullSQL.append(" ");
            notNullSQL.append(colInfos[i].getColumnName());
            if (specifyType) {
                notNullSQL.append(" ");
                notNullSQL.append(DialectUtils.getTypeName(colInfos[i], dialect));
            }
            notNullSQL.append(" NOT NULL");
            result.add(notNullSQL.toString());
        }
    }
    
    
    public static String getAddPrimaryKeySQL(ITableInfo ti, 
                                             String pkName, 
                                             TableColumnInfo[] colInfos, 
                                             boolean appendConstraintName) {
        StringBuilder pkSQL = new StringBuilder();
        pkSQL.append("ALTER TABLE ");
        pkSQL.append(ti.getQualifiedName());
        pkSQL.append(" ADD CONSTRAINT ");
        if (!appendConstraintName) {
            pkSQL.append(pkName);
        }
        pkSQL.append(" PRIMARY KEY ");
        pkSQL.append(getColumnList(colInfos));
        if (appendConstraintName) {
            pkSQL.append(" CONSTRAINT ");
            pkSQL.append(pkName);
        }
        return pkSQL.toString();
    }
    
    
    private static String getColumnList(TableColumnInfo[] colInfos) {
        StringBuilder result = new StringBuilder();
        result.append("(");
        for (int i = 0; i < colInfos.length; i++) {
            result.append(colInfos[i].getColumnName());
            if (i + 1 < colInfos.length) {
                result.append(", ");
            }
        }
        result.append(")");
        return result.toString();
    }
    
    
    public static String getColumnNameAlterSQL(TableColumnInfo from, 
                                               TableColumnInfo to,
                                               String alterClause,
                                               String renameToClause) 
    {
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ");
        result.append(from.getTableName());
        result.append(" ");
        result.append(alterClause);
        result.append(" ");
        result.append(from.getColumnName());
        result.append(" ");
        result.append(renameToClause);
        result.append(" ");
        result.append(to.getColumnName());
        return result.toString();
    }
    
    
    public static String getColumnDefaultAlterSQL(HibernateDialect dialect,
                                                  TableColumnInfo info,
                                                  String alterClause, 
                                                  boolean specifyType, 
                                                  String defaultClause) {
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ");
        result.append(info.getTableName());
        result.append(" ");
        result.append(alterClause);
        result.append(" ");
        result.append(info.getColumnName());
        result.append(" ");
        if (specifyType) {
            result.append(getTypeName(info, dialect));
        }
        result.append(" ");
        result.append(defaultClause);
        result.append(" ");
        if (JDBCTypeMapper.isNumberType(info.getDataType())) {
            result.append(info.getDefaultValue());
        } else {
            result.append("'");
            result.append(info.getDefaultValue());
            result.append("'");
        }
        return result.toString();
    }
    
    
    @SuppressWarnings("unused")
    public static List<String> getColumnTypeAlterSQL(HibernateDialect dialect,
                                                     String alterClause,
                                                     String setClause,
                                                     boolean repeatColumn,
                                                     TableColumnInfo from, 
                                                     TableColumnInfo to)
        throws UnsupportedOperationException
    {
        ArrayList<String> list = new ArrayList<String>();
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ");
        result.append(to.getTableName());
        result.append(" ");
        result.append(alterClause);
        result.append(" ");
        if (repeatColumn) {
            result.append(to.getColumnName());
            result.append(" ");
        }
        result.append(to.getColumnName());
        result.append(" ");
        if (setClause != null && !"".equals(setClause)) {
            result.append(setClause);
            result.append(" ");
        }
        result.append(getTypeName(to, dialect));
        list.add(result.toString());
        return list;
    }    
    
    
    public static String getColumnRenameSQL(TableColumnInfo from, 
                                            TableColumnInfo to) {
        StringBuilder result = new StringBuilder();
        result.append("RENAME COLUMN ");
        result.append(from.getTableName());
        result.append(".");
        result.append(from.getColumnName());
        result.append(" TO ");
        result.append(to.getColumnName());
        return result.toString();
    }
    
    public static String getUnsupportedMessage(HibernateDialect dialect,
                                               int featureId) 
        throws UnsupportedOperationException
    {
        String msg = null;
        switch (featureId) {
            case COLUMN_COMMENT_ALTER_TYPE:
                
                
                msg = s_stringMgr.getString("DialectUtils.columnCommentUnsupported",
                                            dialect.getDisplayName());
                break;
            case COLUMN_DEFAULT_ALTER_TYPE:
                
                
                msg = s_stringMgr.getString("DialectUtils.columnDefaultUnsupported",
                                            dialect.getDisplayName());
                break;                
                
            case COLUMN_DROP_TYPE:
                
                
                msg = s_stringMgr.getString("DialectUtils.columnDropUnsupported",
                                            dialect.getDisplayName());
                break;                                
            case COLUMN_NAME_ALTER_TYPE:
                
                
                msg = s_stringMgr.getString("DialectUtils.columnNameUnsupported",
                                            dialect.getDisplayName());
                break;                                
            case COLUMN_NULL_ALTER_TYPE:
                
                
                msg = s_stringMgr.getString("DialectUtils.columnCommentUnsupported",
                                            dialect.getDisplayName());
                break;
            case COLUMN_TYPE_ALTER_TYPE:
                
                
                msg = s_stringMgr.getString("DialectUtils.columnTypeUnsupported",
                                            dialect.getDisplayName());
                break;
            case ADD_PRIMARY_KEY_TYPE:
                
                
                msg = s_stringMgr.getString("DialectUtils.addPrimaryKeyUnsupported",
                                            dialect.getDisplayName());
                break;
            case DROP_PRIMARY_KEY_TYPE:
                
                
                msg = s_stringMgr.getString("DialectUtils.dropPrimaryKeyUnsupported",
                                            dialect.getDisplayName());
                break;
            default:
                throw new IllegalArgumentException("Unknown featureId: "+featureId);
        }
        return msg;
    }
    
    
    public static String getDropPrimaryKeySQL(String pkName, 
                                              String tableName, 
                                              boolean useConstraintName, 
                                              boolean cascadeConstraints) {
        StringBuilder result = new StringBuilder();
        result.append("ALTER TABLE ");
        result.append(tableName);
        if (useConstraintName) {
            result.append(" DROP CONSTRAINT ");
            result.append(pkName);
        } else {
            result.append(" DROP PRIMARY KEY");
        }
        if (cascadeConstraints) {
            result.append(" CASCADE");
        }
        return result.toString();
    }
    
    
    public static String getAddIndexSQL(String indexName,
                                        boolean unique,
                                        TableColumnInfo[] columns) 
    {
        StringBuilder result = new StringBuilder();
        if (unique) {
            result.append("CREATE UNIQUE INDEX ");
        } else {
            result.append("CREATE INDEX ");
        }
        result.append(indexName);
        result.append(" ON ");
        result.append(columns[0].getTableName());
        result.append(" ");
        result.append(getColumnList(columns));
        return result.toString();
    }
   
    public static TableColumnInfo getRenamedColumn(TableColumnInfo info,
                                                   String newColumnName) 
    {
        TableColumnInfo result = 
            new TableColumnInfo(info.getCatalogName(), 
                                info.getSchemaName(), 
                                info.getTableName(),
                                newColumnName,
                                info.getDataType(), 
                                info.getTypeName(),
                                info.getColumnSize(),
                                info.getDecimalDigits(), 
                                info.getRadix(),
                                info.isNullAllowed(),
                                info.getRemarks(),
                                info.getDefaultValue(),
                                info.getOctetLength(),
                                info.getOrdinalPosition(),
                                info.isNullable()
                                );
        return result;
    }
    
    
    public static String getDropForeignKeySQL(String fkName, String tableName) {
        StringBuilder tmp = new StringBuilder();
        tmp.append("ALTER TABLE ");
        tmp.append(tableName);
        tmp.append(" DROP CONSTRAINT ");
        tmp.append(fkName);
        return tmp.toString();                    
    }
    
    public static List<String> getCreateTableSQL(List<ITableInfo> tables,
                                                 ISQLDatabaseMetaData md,
                                                 HibernateDialect dialect,
                                                 CreateScriptPreferences prefs,
                                                 boolean isJdbcOdbc)
        throws SQLException
    {
        List<String> sqls = new ArrayList<String>();
        List<String> allconstraints = new ArrayList<String>();
        
        for (ITableInfo ti : tables) {
            StringBuilder result = new StringBuilder();
            String tableName = 
                prefs.isQualifyTableNames()? ti.getQualifiedName() : ti.getSimpleName();
            result.append("CREATE TABLE ");
            result.append(tableName);
            result.append("\n(");
            
            List<PrimaryKeyInfo> pkInfos = getPrimaryKeyInfo(md, ti, isJdbcOdbc);
            List<String> pks = getPKSequenceList(pkInfos);
            TableColumnInfo[] infos = md.getColumnInfo(ti);
            for (TableColumnInfo tcInfo : infos) {
                String columnName = tcInfo.getColumnName();
                int columnSize = tcInfo.getColumnSize();
                int dataType = tcInfo.getDataType();
                int precision = dialect.getPrecisionDigits(columnSize, dataType);
                String column = dialect.getTypeName(tcInfo.getDataType(), 
                                                    tcInfo.getColumnSize(),
                                                    precision,
                                                    tcInfo.getDecimalDigits()); 
                
                result.append("\n   ");
                result.append(columnName);
                result.append(" ");
                result.append(column);
                String isNullable = tcInfo.isNullable();
                if (pks.size() == 1 && pks.get(0).equals(columnName))
                {
                   result.append(" PRIMARY KEY");
                }
                if ("NO".equalsIgnoreCase(isNullable))
                {
                   result.append(" NOT NULL");
                }
                result.append(",");                   
            }
            
            if (pks.size() > 1) {
               result.append("\n   CONSTRAINT ");
               result.append(pkInfos.get(0).getSimpleName());
               result.append(" PRIMARY KEY (");
               for (int i = 0; i < pks.size(); i++)
               {
                  result.append(pks.get(i));
                  result.append(",");
               }
               result.setLength(result.length() - 1);
               result.append("),");
            }
            result.setLength(result.length() - 1);

            result.append("\n)");
            sqls.add(result.toString());
            
            if(isJdbcOdbc) { continue; }

            List<String> constraints = 
                createConstraints(ti, tables, prefs, md);
            addConstraintsSQLs(sqls, allconstraints, constraints, prefs);
            
            List<String> indexes = createIndexes(ti, md, pkInfos);
            addConstraintsSQLs(sqls, allconstraints, indexes, prefs);
        }
        
        if (prefs.isConstraintsAtEnd()) {
            sqls.addAll(allconstraints);
        }
        return sqls;
    }
    
    private static void addConstraintsSQLs(List<String> sqls,
                                           List<String> allconstraints,
                                           List<String> sqlsToAdd,
                                           CreateScriptPreferences prefs) 
    {
        if(sqlsToAdd.size() > 0) {
           if(prefs.isConstraintsAtEnd()) {
               allconstraints.addAll(sqlsToAdd);
           } else {
               sqls.addAll(sqlsToAdd);
           }
        }        
    }
    
    
    public  static List<String> createIndexes(ITableInfo ti,
                                              ISQLDatabaseMetaData md,
                                              List<PrimaryKeyInfo> primaryKeys) 
    {
        if (ti == null) {
            throw new IllegalArgumentException("ti cannot be null");
        }
        if (md == null) {
            throw new IllegalArgumentException("md cannot be null");
        }
        List<String> result = new ArrayList<String>();
        if (ti.getDatabaseObjectType() == DatabaseObjectType.VIEW) {
            return result;
        }

        List<IndexColInfo> pkCols = new ArrayList<IndexColInfo>();
        if (primaryKeys != null) {
            for (PrimaryKeyInfo pkInfo : primaryKeys) {
               pkCols.add(new IndexColInfo(pkInfo.getColumnName()));
            }
            Collections.sort(pkCols, IndexColInfo.NAME_COMPARATOR);
        }
        
        List<IndexInfo> indexInfos = null;
        try {
            indexInfos = md.getIndexInfo(ti);
        } catch (SQLException e) {
            
            String msg = 
                s_stringMgr.getString("DialectUtils.error.getprimarykey", 
                                      ti.getSimpleName());
            log.error(msg, e);
            return result;
        }       
        
        
        Hashtable<String, TableIndexInfo> buf = new Hashtable<String, TableIndexInfo>();
        for (IndexInfo indexInfo : indexInfos) {
            String indexName = indexInfo.getSimpleName();
            if(null == indexName) {
               continue;
            }
            TableIndexInfo ixi = buf.get(indexName);
            if(null == ixi)
            {
               List<IndexColInfo> ixCols = new ArrayList<IndexColInfo>();
               
               ixCols.add(new IndexColInfo(indexInfo.getColumnName(), 
                                           indexInfo.getOrdinalPosition()));
               buf.put(indexName, 
                       new TableIndexInfo(indexInfo.getTableName(), 
                                          indexName, 
                                          ixCols,
                                          !indexInfo.isNonUnique()));
            }
            else
            {
               ixi.cols.add(new IndexColInfo(indexInfo.getColumnName(), 
                                             indexInfo.getOrdinalPosition()));
            }           
        }
        
        TableIndexInfo[] ixs = buf.values().toArray(new TableIndexInfo[buf.size()]);
        for (int i = 0; i < ixs.length; i++)
        {
           Collections.sort(ixs[i].cols, IndexColInfo.NAME_COMPARATOR);

           if(pkCols.equals(ixs[i].cols))
           {
              
              
              
              continue;
           }

           Collections.sort(ixs[i].cols, IndexColInfo.ORDINAL_POSITION_COMPARATOR);

           StringBuilder indexSQL = new StringBuilder();
           indexSQL.append("CREATE");
           indexSQL.append(ixs[i].unique ? " UNIQUE ": " ");
           indexSQL.append("INDEX ");
           indexSQL.append(ixs[i].ixName);
           indexSQL.append(" ON ");
           indexSQL.append(ixs[i].table);

           if (ixs[i].cols.size() == 1) {
               indexSQL.append("(").append(ixs[i].cols.get(0));

               for (int j = 1; j < ixs[i].cols.size(); j++) {
                   indexSQL.append(",").append(ixs[i].cols.get(j));
               }
           } else {
               indexSQL.append("\n(\n");
               for (int j = 0; j < ixs[i].cols.size(); j++) {
                   indexSQL.append("  ");
                   indexSQL.append(ixs[i].cols.get(j));
                   if (j < ixs[i].cols.size() - 1) {
                       indexSQL.append(",\n");
                   } else {
                       indexSQL.append("\n");
                   }
               }
           }
           indexSQL.append(")");
           result.add(indexSQL.toString());
        }
        return result;
    }
    
    private static List<String> createConstraints(ITableInfo ti, 
                                                  List<ITableInfo> tables, 
                                                  CreateScriptPreferences prefs,
                                                  ISQLDatabaseMetaData md)
        throws SQLException
    {

        List<String> result = new ArrayList<String>();
        StringBuffer sbToAppend = new StringBuffer();

        ConstraintInfo[] cis = getConstraintInfos(ti, md);

        for (int i = 0; i < cis.length; i++) {
            if (!prefs.isIncludeExternalReferences()) {
                boolean found = false;
                for (ITableInfo table : tables) {
                    if(table.getSimpleName().equalsIgnoreCase(cis[i].pkTable)) {
                        found = true;
                        break;
                    }
                }
                if(false == found) {
                    continue;
                }
            }

            sbToAppend.append("ALTER TABLE " + cis[i].fkTable + "\n");
            sbToAppend.append("ADD CONSTRAINT " + cis[i].fkName + "\n");


            if(cis[i].fkCols.size() == 1)
            {
                sbToAppend.append("FOREIGN KEY (").append(cis[i].fkCols.get(0));

                for (int j = 1; j < cis[i].fkCols.size(); j++)
                {
                    sbToAppend.append(",").append(cis[i].fkCols.get(j));
                }
                sbToAppend.append(")\n");

                sbToAppend.append("REFERENCES " + cis[i].pkTable + "(");
                sbToAppend.append(cis[i].pkCols.get(0));
                for (int j = 1; j < cis[i].pkCols.size(); j++)
                {
                    sbToAppend.append(",").append(cis[i].pkCols.get(j));
                }
            }
            else
            {
                sbToAppend.append("FOREIGN KEY\n");
                sbToAppend.append("(\n");
                for (int j = 0; j < cis[i].fkCols.size(); j++)
                {
                    if(j < cis[i].fkCols.size() -1)
                    {
                        sbToAppend.append("  " + cis[i].fkCols.get(j) + ",\n");
                    }
                    else
                    {
                        sbToAppend.append("  " + cis[i].fkCols.get(j) + "\n");
                    }
                }
                sbToAppend.append(")\n");

                sbToAppend.append("REFERENCES " + cis[i].pkTable + "\n");
                sbToAppend.append("(\n");
                for (int j = 0; j < cis[i].pkCols.size(); j++)
                {
                    if(j < cis[i].pkCols.size() -1)
                    {
                        sbToAppend.append("  " + cis[i].pkCols.get(j) + ",\n");
                    }
                    else
                    {
                        sbToAppend.append("  " + cis[i].pkCols.get(j) + "\n");
                    }
                }
            }

            sbToAppend.append(")");

            if (prefs.isDeleteRefAction()) {
                sbToAppend.append(" ON DELETE ");
                sbToAppend.append(prefs.getRefActionByType(prefs.getDeleteAction()));
            } else {
                switch (cis[i].deleteRule) {
                case DatabaseMetaData.importedKeyCascade:
                    sbToAppend.append(" ON DELETE CASCADE");
                    break;
                case DatabaseMetaData.importedKeySetNull:
                    sbToAppend.append(" ON DELETE SET NULL");
                    break;
                case DatabaseMetaData.importedKeySetDefault:
                    sbToAppend.append(" ON DELETE SET DEFAULT");
                    break;
                case DatabaseMetaData.importedKeyRestrict:
                case DatabaseMetaData.importedKeyNoAction:
                default:
                    sbToAppend.append(" ON DELETE NO ACTION");
                }
            }
            if (prefs.isUpdateRefAction()) {
                sbToAppend.append(" ON UPDATE ");
                sbToAppend.append(prefs.getRefActionByType(prefs.getUpdateAction()));             
            } else {
                switch (cis[i].updateRule) {
                case DatabaseMetaData.importedKeyCascade:
                    sbToAppend.append(" ON UPDATE CASCADE");
                    break;
                case DatabaseMetaData.importedKeySetNull:
                    sbToAppend.append(" ON UPDATE SET NULL");
                    break;
                case DatabaseMetaData.importedKeySetDefault:
                    sbToAppend.append(" ON UPDATE SET DEFAULT");
                    break;
                case DatabaseMetaData.importedKeyRestrict:
                case DatabaseMetaData.importedKeyNoAction:
                default:
                    sbToAppend.append(" ON UPDATE NO ACTION");
                }             
            }
            sbToAppend.append("\n");
            result.add(sbToAppend.toString());
            sbToAppend.setLength(0);
        }

        return result;
    }
    
    private static ConstraintInfo[] getConstraintInfos(ITableInfo ti, 
                                                       ISQLDatabaseMetaData md) 
        throws SQLException 
    {
        Hashtable<String, ConstraintInfo> buf = 
            new Hashtable<String, ConstraintInfo>();
        ForeignKeyInfo[] fkinfos = md.getImportedKeysInfo(ti);
        for (ForeignKeyInfo fkinfo : fkinfos) {
            ConstraintInfo ci = buf.get(fkinfo.getSimpleName());

            if(null == ci)
            {
               Vector<String> fkCols = new Vector<String>();
               Vector<String> pkCols = new Vector<String>();
               fkCols.add(fkinfo.getForeignKeyColumnName());
               pkCols.add(fkinfo.getPrimaryKeyColumnName());
               ci = new ConstraintInfo(fkinfo.getForeignKeyTableName(), 
                                       fkinfo.getPrimaryKeyTableName(), 
                                       fkinfo.getSimpleName(), 
                                       fkCols, 
                                       pkCols,
                                       (short)fkinfo.getDeleteRule(),
                                       (short)fkinfo.getUpdateRule());
               buf.put(fkinfo.getSimpleName(), ci);
            }
            else
            {
               ci.fkCols.add(fkinfo.getForeignKeyColumnName());
               ci.pkCols.add(fkinfo.getPrimaryKeyColumnName());
            }
            
        }
        return buf.values().toArray(new ConstraintInfo[buf.size()]);
    }
    
    
    private static List<PrimaryKeyInfo> getPrimaryKeyInfo(ISQLDatabaseMetaData md, 
                                                          ITableInfo ti,
                                                          boolean isJdbcOdbc) 
    {
        List<PrimaryKeyInfo> result = new ArrayList<PrimaryKeyInfo>(); 
        if (isJdbcOdbc) {
            return result;
        }
        try {
            result = Arrays.asList(md.getPrimaryKey(ti));
        } catch (SQLException e) {
            
            
            String msg = 
                s_stringMgr.getString("DialectUtils.error.getprimarykey", 
                                      ti.getSimpleName());
            log.error(msg, e);
        }
        return result;
    }
    
    private static List<String> getPKSequenceList(List<PrimaryKeyInfo> infos) {
        String[] result = new String[infos.size()];
        for (PrimaryKeyInfo info : infos) {
            int iKeySeq = info.getKeySequence() - 1;
            result[iKeySeq] = info.getColumnName();
        }
        return Arrays.asList(result);
    }
    
    
    
}
