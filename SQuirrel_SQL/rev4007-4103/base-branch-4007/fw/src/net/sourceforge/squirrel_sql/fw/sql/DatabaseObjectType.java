package net.sourceforge.squirrel_sql.fw.sql;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IntegerIdentifierFactory;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.io.Serializable;


public class DatabaseObjectType implements IHasIdentifier, Serializable
{
   static final long serialVersionUID = 2325635336825122256L;
   
   
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(DatabaseObjectType.class);

   
   private final static IntegerIdentifierFactory s_idFactory = new IntegerIdentifierFactory();

   
   public final static DatabaseObjectType OTHER = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.other"));

   
   public final static DatabaseObjectType BEST_ROW_ID = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.bestRowID"));

   
   public final static DatabaseObjectType CATALOG = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.catalog"));

   
   public final static DatabaseObjectType COLUMN = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.column"));

   
   public final static DatabaseObjectType SESSION = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.database"));


   
   public final static DatabaseObjectType DATABASE_TYPE_DBO = DatabaseObjectType.createNewDatabaseObjectType("Database Type");


   
   public final static DatabaseObjectType DATATYPE = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.datatype"));

    
    public final static DatabaseObjectType PRIMARY_KEY = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.primarykey"));

   
   public final static DatabaseObjectType FOREIGN_KEY = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.foreignkey"));

   
   public final static DatabaseObjectType FUNCTION = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.function"));

      
   public static final DatabaseObjectType INDEX_TYPE_DBO = DatabaseObjectType.createNewDatabaseObjectType("Index Type");   
   
   
   public final static DatabaseObjectType INDEX = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.index"));

   
   public final static DatabaseObjectType PROCEDURE = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.storproc"));

   
   public final static DatabaseObjectType PROC_TYPE_DBO = DatabaseObjectType.createNewDatabaseObjectType("Stored Procedure Type");



   
   public final static DatabaseObjectType SCHEMA = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.schema"));

      
   public static final DatabaseObjectType SEQUENCE_TYPE_DBO = DatabaseObjectType.createNewDatabaseObjectType("Sequence Type");   
   
   
   
   public final static DatabaseObjectType SEQUENCE = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.sequence"));

   
   public final static DatabaseObjectType TABLE = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.table"));

   
   public final static DatabaseObjectType TABLE_TYPE_DBO = DatabaseObjectType.createNewDatabaseObjectType("Table Type");

   public static final DatabaseObjectType VIEW = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.view"));

      
   public static final DatabaseObjectType TRIGGER_TYPE_DBO = DatabaseObjectType.createNewDatabaseObjectType("Trigger Type");   
   
   
   public final static DatabaseObjectType TRIGGER = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.catalog"));

   
   public final static DatabaseObjectType UDT = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.udt"));

   
   public final static DatabaseObjectType UDT_TYPE_DBO = DatabaseObjectType.createNewDatabaseObjectType("UDT Type");

   
   public final static DatabaseObjectType UDF = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.udf"));

   
   public final static DatabaseObjectType UDF_TYPE_DBO = DatabaseObjectType.createNewDatabaseObjectType("UDF Type");

   
   public final static DatabaseObjectType USER = createNewDatabaseObjectType(s_stringMgr.getString("DatabaseObjectType.user"));

   
   private final IIdentifier _id;

   
   private final String _name;

   
   private DatabaseObjectType(String name)
   {
      super();
      _id = s_idFactory.createIdentifier();
      _name = name != null ? name : _id.toString();
   }

   
   public IIdentifier getIdentifier()
   {
      return _id;
   }

   
   public String getName()
   {
      return _name;
   }

   public String toString()
   {
      return getName();
   }

   public static DatabaseObjectType createNewDatabaseObjectType(String name)
   {
      return new DatabaseObjectType(name);
   }
}
