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

   
   public final static DatabaseObjectType OTHER = createNewDatabaseObjectTypeI18n("DatabaseObjectType.other");

   
   public final static DatabaseObjectType BEST_ROW_ID = createNewDatabaseObjectTypeI18n("DatabaseObjectType.bestRowID");

   
   public final static DatabaseObjectType CATALOG = createNewDatabaseObjectTypeI18n("DatabaseObjectType.catalog");

   
   public final static DatabaseObjectType COLUMN = createNewDatabaseObjectTypeI18n("DatabaseObjectType.column");

   
   public final static DatabaseObjectType SESSION = createNewDatabaseObjectTypeI18n("DatabaseObjectType.database");


   
   public final static DatabaseObjectType DATABASE_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.DATABASE_TYPE_DBO"); 


   
   public final static DatabaseObjectType DATATYPE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.datatype");

    
    public final static DatabaseObjectType PRIMARY_KEY = createNewDatabaseObjectTypeI18n("DatabaseObjectType.primarykey");

   
   public final static DatabaseObjectType FOREIGN_KEY = createNewDatabaseObjectTypeI18n("DatabaseObjectType.foreignkey");

   
   public final static DatabaseObjectType FUNCTION = createNewDatabaseObjectTypeI18n("DatabaseObjectType.function");

      
   public static final DatabaseObjectType INDEX_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.INDEX_TYPE_DBO"); 
   
   
   public final static DatabaseObjectType INDEX = createNewDatabaseObjectTypeI18n("DatabaseObjectType.index");

   
   public final static DatabaseObjectType PROCEDURE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.storproc");

   
   public final static DatabaseObjectType PROC_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.PROC_TYPE_DBO"); 



   
   public final static DatabaseObjectType SCHEMA = createNewDatabaseObjectTypeI18n("DatabaseObjectType.schema");

      
   public static final DatabaseObjectType SEQUENCE_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.SEQUENCE_TYPE_DBO"); 
   
   
   
   public final static DatabaseObjectType SEQUENCE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.sequence");

   
   public final static DatabaseObjectType TABLE = createNewDatabaseObjectTypeI18n("DatabaseObjectType.table");

   
   public final static DatabaseObjectType TABLE_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.TABLE_TYPE_DBO"); 

   public static final DatabaseObjectType VIEW = createNewDatabaseObjectTypeI18n("DatabaseObjectType.view");

      
   public static final DatabaseObjectType TRIGGER_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.TRIGGER_TYPE_DBO"); 
   
   
   public final static DatabaseObjectType TRIGGER = createNewDatabaseObjectTypeI18n("DatabaseObjectType.catalog");

   
   public final static DatabaseObjectType UDT = createNewDatabaseObjectTypeI18n("DatabaseObjectType.udt");

   
   public final static DatabaseObjectType UDT_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.UDT_TYPE_DBO"); 

   
   public final static DatabaseObjectType UDF = createNewDatabaseObjectTypeI18n("DatabaseObjectType.udf");

   
   public final static DatabaseObjectType UDF_TYPE_DBO = createNewDatabaseObjectTypeI18n("DatabaseObjectType.UDF_TYPE_DBO"); 

   
   public final static DatabaseObjectType USER = createNewDatabaseObjectTypeI18n("DatabaseObjectType.user");

   
   private final IIdentifier _id;

   
   private final String _name;
   private String _keyForSerializationReplace;

   
   private DatabaseObjectType(String name, String keyForSerializationReplace)
   {
      _keyForSerializationReplace = keyForSerializationReplace;
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


   public String getKeyForSerializationReplace()
   {
      return _keyForSerializationReplace;
   }

   public String toString()
   {
      return getName();
   }

   public static DatabaseObjectType createNewDatabaseObjectTypeI18n(String key)
   {
      return new DatabaseObjectType(key, s_stringMgr.getString(key));
   }

   public static DatabaseObjectType createNewDatabaseObjectType(String key)
   {
      return new DatabaseObjectType(key, key);
   }

}
