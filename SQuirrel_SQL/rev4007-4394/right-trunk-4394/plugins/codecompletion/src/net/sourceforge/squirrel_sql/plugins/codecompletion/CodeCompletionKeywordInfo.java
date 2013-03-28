
package net.sourceforge.squirrel_sql.plugins.codecompletion;


public class CodeCompletionKeywordInfo extends CodeCompletionInfo
{
   private String _keyword;

   public CodeCompletionKeywordInfo(String keyword)
   {
      _keyword = keyword;
   }

   public String getCompareString()
   {
      return _keyword;
   }

   public String toString()
   {
      return _keyword;
   }
}
