
package net.sourceforge.squirrel_sql.plugins.codecompletion;


public class CodeCompletionSchemaInfo extends CodeCompletionInfo
{
   private String _schema;

   public CodeCompletionSchemaInfo(String schema)
   {
      _schema = schema;
   }

   public String getCompareString()
   {
      return _schema;
   }

   public String toString()
   {
      return _schema;
   }
}