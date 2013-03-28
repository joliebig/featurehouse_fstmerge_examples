
package net.sourceforge.squirrel_sql.plugins.codecompletion;


public class CodeCompletionCatalogInfo extends CodeCompletionInfo
{
   private String _catalog;

   public CodeCompletionCatalogInfo(String catalog)
   {
      _catalog = catalog;
   }

   public String getCompareString()
   {
      return _catalog;
   }

   public String toString()
   {
      return _catalog;
   }
}