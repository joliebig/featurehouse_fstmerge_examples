package net.sourceforge.squirrel_sql.client.gui.db;

import java.io.Serializable;


public class SchemaLoadInfo implements Serializable
{
   public SchemaLoadInfo(String[] tableTypes)
   {
      this.tableTypes = tableTypes;
   }

   
   public String schemaName;

   
   public String[] tableTypes;

   public boolean loadProcedures = true;
}
