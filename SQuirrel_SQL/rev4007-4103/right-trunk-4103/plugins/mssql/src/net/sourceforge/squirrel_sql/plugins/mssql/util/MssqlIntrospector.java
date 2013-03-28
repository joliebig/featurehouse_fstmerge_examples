package net.sourceforge.squirrel_sql.plugins.mssql.util;



import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.sql.IProcedureInfo;
import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.IUDTInfo;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.CheckConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.DefaultConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.ForeignKeyConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.PrimaryKeyConstraint;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.constraint.TableConstraints;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile.DatabaseFile;
import net.sourceforge.squirrel_sql.plugins.mssql.sql.dbfile.DatabaseFileInfo;

public class MssqlIntrospector {
    
    public final static int MSSQL_TABLE = 1;
    public final static int MSSQL_VIEW = 2;
    public final static int MSSQL_STOREDPROCEDURE = 3;
    public final static int MSSQL_UDF = 4;
    public final static int MSSQL_UDT = 5;
    public final static int MSSQL_RULE = 6;
    public final static int MSSQL_DEFAULT = 7;
    
    public final static int MSSQL_UNKNOWN = -1;
    
    public static TableConstraints getTableConstraints(IDatabaseObjectInfo oi, ISQLConnection conn) throws java.sql.SQLException {
        TableConstraints constraints = new TableConstraints();
        
        Connection c = conn.getConnection();

        CallableStatement stmt = c.prepareCall("{ call sp_helpconstraint ?, ? }");
        stmt.setString(1, oi.getSimpleName());
        stmt.setString(2, "nomsg");
        ResultSet rs;
        
        try {
             rs = stmt.executeQuery();
        }
        catch (java.sql.SQLException ex) {
            
            return constraints;
        }
        
        while (rs.next()) {
            String constraintType = rs.getString(1);
            String constraintName = rs.getString(2);
            
            
            
            
            String constraintKeys = rs.getString(7);
            
            if (constraintType.startsWith("DEFAULT")) {
                DefaultConstraint def = new DefaultConstraint();
                String col = constraintType.substring(18).trim();      

                def.setConstraintName(constraintName);
                def.addConstraintColumn(col);
                def.setDefaultExpression(constraintKeys);
                
                constraints.addConstraint(def);
            }
            else if (constraintType.startsWith("CHECK")) {
                CheckConstraint check = new CheckConstraint();
                String col = constraintType.substring(16).trim();       
                
                check.setConstraintName(constraintName);
                check.addConstraintColumn(col);
                check.setCheckExpression(constraintKeys);
                
                constraints.addConstraint(check);
            }
            else if (constraintType.startsWith("FOREIGN KEY")) {
                
                ForeignKeyConstraint fk = new ForeignKeyConstraint();
                
                fk.setConstraintName(constraintName);
                
                String foreignColumns[] = constraintKeys.split(", ");
                for (int i = 0; i < foreignColumns.length; i++)
                    fk.addConstraintColumn(foreignColumns[i]);
                
                rs.next();
                
                constraintKeys = rs.getString(7);
                
                constraintKeys = constraintKeys.substring(11);      
                String[] tableAndColumns = constraintKeys.split(" ",2);
                
                
                fk.setReferencedTable(tableAndColumns[0]);
                String primaryColumns[] = tableAndColumns[1].substring(1,tableAndColumns[1].length() - 2).split(",");
                for (int i = 0; i < primaryColumns.length; i++)
                    fk.addPrimaryColumn(primaryColumns[i]);
                
                constraints.addConstraint(fk);
            }
            else if (constraintType.startsWith("PRIMARY KEY")) {
                PrimaryKeyConstraint pk = new PrimaryKeyConstraint();
                
                pk.setConstraintName(constraintName);
                pk.setClustered(constraintType.endsWith("(clustered)"));
                
                String cols[] = constraintKeys.split(", ");
                for (int i = 0; i < cols.length; i++)
                    pk.addConstraintColumn(cols[i]);
                
                constraints.addConstraint(pk);
            }
        }
        
        return constraints;
    }
    
    public static DatabaseFileInfo getDatabaseFileInfo(String catalogName, ISQLConnection conn) throws java.sql.SQLException {
        DatabaseFileInfo dbInfo = new DatabaseFileInfo();
        
        Connection c = conn.getConnection();
        
        CallableStatement stmt = c.prepareCall("{ call sp_helpdb ? }");
        stmt.setString(1, catalogName);
        ResultSet rs;
        
        if (!stmt.execute())
            return null;
        rs = stmt.getResultSet();
        rs.next();
        
        dbInfo.setDatabaseName(rs.getString(1));
        dbInfo.setDatabaseSize(rs.getString(2));
        dbInfo.setOwner(rs.getString(3));
        dbInfo.setCreatedDate(rs.getString(5));
        String[] options = rs.getString(6).split(", ");
        dbInfo.setCompatibilityLevel(rs.getShort(7));
        
        
        for (int i = 0; i < options.length; i++) {
            if (options[i].indexOf('=') != -1) {
                String parts[] = options[i].split("=");
                dbInfo.setOption(parts[0],parts[1]);
            }
            else
                dbInfo.setOption(options[i],"1");
        }
        
        if (!stmt.getMoreResults())
            return dbInfo;
        
        rs = stmt.getResultSet();
        
        while (rs.next()) {
            String name = rs.getString(1).trim();
            short id = rs.getShort(2);
            String filename = rs.getString(3).trim();
            String filegroup = rs.getString(4);
            String size = rs.getString(5);
            String maxSize = rs.getString(6);
            String growth = rs.getString(7);
            String usage = rs.getString(8);
            
            DatabaseFile file = new DatabaseFile();
            file.setName(name);
            file.setId(id);
            file.setFilename(filename);
            file.setFilegroup(filegroup);
            file.setSize(size);
            file.setMaxSize(maxSize);
            file.setGrowth(growth);
            file.setUsage(usage);
            
            if (filegroup == null) 
                dbInfo.addLogFile(file);
            else
                dbInfo.addDataFile(file);
        }
        
        return dbInfo;
    }
    
    public static int getObjectInfoType(IDatabaseObjectInfo oi) {
        if (oi instanceof ITableInfo) {
            String tableType = ((ITableInfo) oi).getType();
            if (tableType.equals("TABLE"))
                return MSSQL_TABLE;
            else if (tableType.equals("VIEW"))
                return MSSQL_VIEW;
            else
                return MSSQL_UNKNOWN;
        }
        else if (oi instanceof IProcedureInfo) {
            
            String simpleName = oi.getSimpleName();
            if (simpleName.endsWith(";0"))
                return MSSQL_UDF;
            else if (simpleName.endsWith(";1"))
                return MSSQL_STOREDPROCEDURE;
            else
                return MSSQL_UNKNOWN;
        }
        else if (oi instanceof IUDTInfo) {
            return MSSQL_UDT;
        }
        else
            return MSSQL_UNKNOWN;
    }
    
    public static String generateCreateScript(IDatabaseObjectInfo oi, ISQLConnection conn, boolean withConstraints) throws java.sql.SQLException {
        StringBuffer buf = new StringBuffer();
        
        if (getObjectInfoType(oi) == MSSQL_TABLE)
            buf.append(MssqlIntrospector.generateCreateTableScript(oi,conn,withConstraints));
        else {
            Connection c = conn.getConnection();
            buf.append(getHelpTextForObject(MssqlIntrospector.getFixedVersionedObjectName(oi.getSimpleName()),c));
        }
        
        buf.append("GO\n\n");
        return buf.toString();
    }
    
    public static String getHelpTextForObject(String objectName, Connection c) throws java.sql.SQLException {
        StringBuffer buf = new StringBuffer();
        
        CallableStatement stmt = c.prepareCall("{ call sp_helptext (?) }");
        stmt.setString(1, objectName);
        
        ResultSet helpText = stmt.executeQuery();
        
        while (helpText.next()) {
            buf.append(helpText.getString(1));
        }
        return buf.toString();
    }
    
    public static String generateCreateDatabaseScript(String catalogName, ISQLConnection conn) throws java.sql.SQLException {
        StringBuffer buf = new StringBuffer();
        
        DatabaseFileInfo dbInfo = MssqlIntrospector.getDatabaseFileInfo(catalogName,conn);
        Object[] dataFiles = dbInfo.getDataFiles();
        Object[] logFiles = dbInfo.getLogFiles();
        
        buf.append("CREATE DATABASE [");
        buf.append(dbInfo.getDatabaseName());
        buf.append("]\nON ");
        
        String lastFilegroup = "";
        for (int i = 0; i < dataFiles.length; i++) {
            DatabaseFile file = (DatabaseFile) dataFiles[i];

            String thisFilegroup = file.getFilegroup();
            if (!thisFilegroup.equals(lastFilegroup)) {
                
                if (thisFilegroup.equals("PRIMARY"))
                    buf.append("PRIMARY");
                else {
                    buf.append("FILEGROUP ");
                    buf.append(thisFilegroup);
                }
                buf.append("\n");
                lastFilegroup = thisFilegroup;
            }
            
            buf.append("( NAME = ");
            buf.append(file.getName());
            buf.append(",\n\tFILENAME = '");
            buf.append(file.getFilename());
            buf.append("',\n\tSIZE = ");
            buf.append(file.getSize());
            if (!file.getMaxSize().equals("Unlimited")) {
                buf.append(",\n\tMAXSIZE = ");
                buf.append(file.getMaxSize());
            }
            buf.append(",\n\tFILEGROWTH = ");
            buf.append(file.getGrowth());
            buf.append(" )");
            
            if (i < dataFiles.length - 1)
                buf.append(",");
            buf.append("\n");
        }
        
        buf.append("LOG ON\n");
        for (int i = 0; i < logFiles.length; i++) {
            DatabaseFile file = (DatabaseFile) logFiles[i];
            
            buf.append("( NAME = ");
            buf.append(file.getName());
            buf.append(",\n\tFILENAME = '");
            buf.append(file.getFilename());
            buf.append("',\n\tSIZE = ");
            buf.append(file.getSize());
            if (!file.getMaxSize().equals("Unlimited")) {
                buf.append(",\n\tMAXSIZE = ");
                buf.append(file.getMaxSize());
            }
            buf.append(",\n\tFILEGROWTH = ");
            buf.append(file.getGrowth());
            buf.append(" )");
            
            if (i < logFiles.length - 1)
                buf.append(",");
            
            buf.append("\n");
        }
        
        buf.append("GO\n\n");
        
        return buf.toString();
    }
    
    public static String generateCreateIndexesScript(IDatabaseObjectInfo oi, ISQLConnection conn) throws java.sql.SQLException {
        Connection c = conn.getConnection();
        
        StringBuffer buf = new StringBuffer();
        
        CallableStatement stmt = c.prepareCall("{ call sp_helpindex ? }");
        stmt.setString(1, oi.getSimpleName());
        ResultSet rs;
                
        try {
            rs = stmt.executeQuery();
        }
        catch (java.sql.SQLException e) {
            
            return "";
        }
        
        while (rs.next()) {
            String indexName = rs.getString(1);
            
            String[] info = rs.getString(2).split(" located on ");
            String[] keys = rs.getString(3).split(", ");
            String[] attribs = info[0].split(", ");
            boolean isUnique = false;
            boolean isClustered = false;
            for (int i = 0; i < attribs.length; i++) {
                if (attribs[i].equals("clustered"))
                    isClustered = true;
                else if (attribs[i].equals("unique"))
                    isUnique = true;
            }
                        
            buf.append("CREATE ");
            if (isUnique)
                buf.append("UNIQUE ");
            buf.append(isClustered ? "CLUSTERED " : "NONCLUSTERED ");
            buf.append("INDEX [");
            buf.append(indexName);
            buf.append("]\n\tON [");
            buf.append(oi.getSimpleName());
            buf.append("] (");
            for (int i = 0; i < keys.length; i++) {
                boolean isDesc = false;
                String keyName = keys[i];
                if (keyName.endsWith("(-)")) {
                    isDesc = true;
                    keyName = keyName.substring(0,keyName.length() - 3);
                }
                buf.append(keyName);
                if (isDesc)
                    buf.append(" DESC");
                if (i < keys.length - 1)
                    buf.append(", ");
            }
            buf.append(")\n\tON [");
            buf.append(info[1]);
            buf.append("]\nGO\n\n");
        }
        
        return buf.toString();
    }
    
    public static String generateCreateTriggersScript(IDatabaseObjectInfo oi, ISQLConnection conn) throws java.sql.SQLException {
        Connection c = conn.getConnection();
        
        StringBuffer buf = new StringBuffer();
        
        CallableStatement stmt = c.prepareCall("{ call sp_helptrigger ? }");
        stmt.setString(1, oi.getSimpleName());
        ResultSet rs;
                
        try {
            rs = stmt.executeQuery();
        }
        catch (java.sql.SQLException e) {
            
            return "";
        }
                
        while (rs.next()) {
            String triggerName = rs.getString(1);
            buf.append(MssqlIntrospector.getHelpTextForObject(triggerName,c));
            buf.append("\nGO\n\n");
        }
        
        return buf.toString();
    }
    
    public static String generatePermissionsScript(IDatabaseObjectInfo oi, ISQLConnection conn) throws java.sql.SQLException {
        Connection c = conn.getConnection();
        
        StringBuffer buf = new StringBuffer();
        
        CallableStatement stmt = c.prepareCall("{ call sp_helprotect ? }");
        stmt.setString(1, MssqlIntrospector.getFixedVersionedObjectName(oi.getSimpleName()));
        ResultSet rs;
                
        try {
            rs = stmt.executeQuery();
        }
        catch (java.sql.SQLException e) {
            
            return "";
        }
                
        while (rs.next()) {
            
            
            String grantee = rs.getString(3);
            
            String protectType = rs.getString(5).trim();
            String action = rs.getString(6);
            

            
            
            if (protectType.equals("Grant"))
                buf.append("GRANT ");
            else if (protectType.equals("Deny"))
                buf.append("REVOKE ");
            buf.append(action.toUpperCase());
            buf.append(" ON [");
            buf.append(MssqlIntrospector.getFixedVersionedObjectName(oi.getSimpleName()));
            buf.append("] ");
            if (protectType.equals("Grant"))
                buf.append("TO ");
            else if (protectType.equals("Deny"))
                buf.append("FROM ");
            buf.append(grantee);

            buf.append("\nGO\n\n");
        }
        
        return buf.toString();
    }
    
    protected static String generateCreateTableScript(IDatabaseObjectInfo oi, 
                                                      ISQLConnection conn, 
                                                      boolean withConstraints) 
        throws java.sql.SQLException 
    {
        Connection c = conn.getConnection();
        
        StringBuffer buf = new StringBuffer();
        
        TableConstraints constraints = MssqlIntrospector.getTableConstraints(oi, conn);

        CallableStatement stmt = c.prepareCall("{ call sp_help ? }");
        stmt.setString(1, oi.getSimpleName());
        ResultSet rs;
        
        if (!stmt.execute())
            return null;

        
        rs = stmt.getResultSet();
        
        if (!rs.next())
            return null;
        buf.append("CREATE TABLE [");
        buf.append(rs.getString(2));
        buf.append("].[");
        buf.append(rs.getString(1));
        buf.append("] (");
        buf.append("\n");

        if (!stmt.getMoreResults())
            return null;
        rs = stmt.getResultSet();
        
        while (rs.next()) {
            String colName = rs.getString(1);
            String colType = rs.getString(2);
            buf.append("\t[");
            buf.append(colName);
            buf.append("] [");
            buf.append(colType);
            buf.append("] ");
            if (colType.equals("char") || colType.equals("varchar")) {
                buf.append("(");
                buf.append(rs.getInt(4));   
                buf.append(") COLLATE ");
                buf.append(rs.getString(10));       
                buf.append(" ");
            }
            if (rs.getString(7).equals("yes"))
                buf.append("NULL ");
            else
                buf.append("NOT NULL ");

            if (withConstraints) {
                List<DefaultConstraint> defs = 
                    constraints.getDefaultsForColumn(colName);
                
                
                if (defs != null && defs.size() == 1) {
                    DefaultConstraint def = defs.get(0);
                    buf.append("CONSTRAINT [");
                    buf.append(def.getConstraintName());
                    buf.append("] DEFAULT ");
                    buf.append(def.getDefaultExpression());
                    buf.append(" ");
                }
            }

            buf.append(",\n");
        }

        if (withConstraints) {
            
            List<PrimaryKeyConstraint> pks = constraints.getPrimaryKeyConstraints();
            if (pks != null && pks.size() == 1) {
                PrimaryKeyConstraint pk = pks.get(0);
                buf.append("\tCONSTRAINT [");
                buf.append(pk.getConstraintName());
                buf.append("] PRIMARY KEY ");
                buf.append(pk.isClustered() ? "CLUSTERED" : "NONCLUSTERED");
                buf.append("\n\t(\n\t\t");
                Object[] cols = pk.getConstraintColumns();
                for (int i = 0; i < cols.length; i++) {
                    buf.append("[");
                    buf.append((String) cols[i]);
                    buf.append("]");
                    if (i < cols.length - 1)
                        buf.append(", ");
                }
                buf.append("\n\t)\n");
                
            }

            List<ForeignKeyConstraint> fks = constraints.getForeignKeyConstraints();
            for (int i = 0; i < fks.size(); i++) {
                ForeignKeyConstraint fk = fks.get(i);
                buf.append("\tFOREIGN KEY\n\t(\n\t\t");
                Object[] foreignColumns = fk.getConstraintColumns();
                for (int j = 0; j < foreignColumns.length; j++) {
                    buf.append("[");
                    buf.append((String) foreignColumns[j]);
                    buf.append("]");
                    if (j < foreignColumns.length - 1)
                        buf.append(", ");
                }
                buf.append("\n\t) REFERENCES [");
                buf.append(fk.getReferencedTable());
                buf.append("] (\n\t\t");
                Object[] primaryColumns = fk.getPrimaryColumns();
                for (int j = 0; j < primaryColumns.length; j++) {
                    buf.append("[");
                    buf.append((String) primaryColumns[j]);
                    buf.append("]");
                    if (j < primaryColumns.length - 1)
                        buf.append(",\n");
                }
                buf.append("\n\t),");
            }

            for (CheckConstraint check : constraints.getCheckConstraints()) {
                buf.append("\tCONSTRAINT [");
                buf.append(check.getConstraintName());
                buf.append("] CHECK ");
                buf.append(check.getCheckExpression());
                buf.append(",\n");
            }
        }

        buf.append(")\n");
        
        
        return buf.toString();
    }
    
    @SuppressWarnings("unused")
    public static String generateUsersAndRolesScript(String catalogName, ISQLConnection conn) throws java.sql.SQLException {    
        StringBuffer buf = new StringBuffer();
        
        Connection c = conn.getConnection();

        CallableStatement stmt = c.prepareCall("{ call sp_helpuser }");
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            String userName = rs.getString(1);
            String loginName = rs.getString(3);
            
            if (userName.equals("dbo"))
                continue;
            
            buf.append("if not exists (select * from dbo.sysusers where name = N'");
            buf.append(userName);
            buf.append("' and uid < 16382)\n\tEXEC sp_grantdbaccess N'");
            buf.append(loginName);
            buf.append("', N'");
            buf.append(userName);
            buf.append("'\nGO\n\n");
        }
        
        stmt = c.prepareCall("{ call sp_helprole }");
        rs = stmt.executeQuery();
        
        while (rs.next()) {
            String roleName = rs.getString(1);
            short roleId = rs.getShort(2);
            
            if (roleId < 16400)
                continue;
            
            buf.append("if not exists (select * from dbo.sysusers where name = N'");
            buf.append(roleName);
            buf.append("' and uid > 16399)\n\tEXEC sp_addrole N'");
            buf.append(roleName);
            buf.append("'\nGO\n\n");
            
            
            CallableStatement userStmt = c.prepareCall("{ call sp_helprolemember ? }");
            userStmt.setString(1,roleName);
            ResultSet userRs = userStmt.executeQuery();
            
            while (userRs.next()) {
                String userInRole = userRs.getString(2);
                buf.append("exec sp_addrolemember N'");
                buf.append(roleName);
                buf.append("', N'");
                buf.append(userInRole);
                buf.append("'\nGO\n\n");
            }
        }
        
        return buf.toString();
    }
    
    public static String generateDropScript(IDatabaseObjectInfo oi) {    
        StringBuffer buf = new StringBuffer();
        String useThisName;
        int objectType = MssqlIntrospector.getObjectInfoType(oi);
        
        if (objectType == MSSQL_STOREDPROCEDURE || objectType == MSSQL_UDF)
            useThisName = oi.getSimpleName().split(";")[0];
        else
            useThisName = oi.getSimpleName();
            
        buf.append("IF EXISTS ( SELECT * FROM sysobjects WHERE id = OBJECT_ID('");
        buf.append(oi.getSchemaName());
        buf.append(".");
        buf.append(useThisName);
        buf.append("') )\n\tDROP ");
        switch (objectType) {
            case MssqlIntrospector.MSSQL_TABLE:
                buf.append("TABLE");
                break;
            case MssqlIntrospector.MSSQL_VIEW:
                buf.append("VIEW");
                break;
            case MssqlIntrospector.MSSQL_UDF:
                buf.append("FUNCTION");
                break;
            case MssqlIntrospector.MSSQL_STOREDPROCEDURE:
                buf.append("PROCEDURE");
                break;
        }
        buf.append(" ");
        buf.append(useThisName);
        buf.append("\nGO\n\n");
        
        return buf.toString();
    }
    
    public static String getFixedVersionedObjectName(String objectName) {
        String[] parts = objectName.split(";");
        return parts[0];
    }
    
    public static String formatDataType(String dataType, short dataLength, int dataPrec, int dataScale) {
        StringBuffer buf = new StringBuffer();
        
        if (dataType.endsWith("char")) {
            buf.append(dataType);
            buf.append("(");
            buf.append(dataLength);
            buf.append(")");
        }
        else if (dataType.equals("float")) {
            buf.append(dataType);
            buf.append("(");
            buf.append(dataPrec);
            buf.append(")");
        }
        else if (dataType.equals("decimal") || dataType.equals("numeric")) {
            buf.append(dataType);
            buf.append("(");
            buf.append(dataPrec);
            buf.append(",");
            buf.append(dataScale);
            buf.append(")");
        }
        else
            buf.append(dataType);
        
        return buf.toString();
    }
  
}
