
package net.sourceforge.squirrel_sql.plugins.codecompletion;


public class CodeCompletionTypeInfo extends CodeCompletionInfo
{
   private String _typeName;

   public CodeCompletionTypeInfo(String typeName)
   {
      _typeName = typeName;
   }

   public String getCompareString()
   {
      return _typeName;
   }

   public String toString()
   {
      return _typeName;
   }
}
