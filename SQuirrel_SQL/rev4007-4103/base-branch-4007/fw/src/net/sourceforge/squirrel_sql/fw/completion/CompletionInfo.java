
package net.sourceforge.squirrel_sql.fw.completion;



public abstract class CompletionInfo implements Comparable<CompletionInfo>
{
   private String _upperCaseCompletionString;

   public abstract String getCompareString();

   public String getCompletionString()
   {
      return getCompareString();
   }

   public int compareTo(CompletionInfo other)
   {
      if(null == _upperCaseCompletionString)
      {
         _upperCaseCompletionString = getCompareString().toUpperCase();
      }

      if(null == other._upperCaseCompletionString)
      {
         other._upperCaseCompletionString = other.getCompareString().toUpperCase();
      }

      return _upperCaseCompletionString.compareTo(other._upperCaseCompletionString);
   }

   
   public boolean upperCaseCompletionStringStartsWith(String testString)
   {
      if(null == _upperCaseCompletionString)
      {
         _upperCaseCompletionString = getCompareString().toUpperCase();
      }

      return _upperCaseCompletionString.startsWith(testString);
   }

   
   public boolean upperCaseCompletionStringEquals(String testString)
   {
      if(null == _upperCaseCompletionString)
      {
         _upperCaseCompletionString = getCompareString().toUpperCase();
      }

      return _upperCaseCompletionString.equals(testString);
   }

   
   public boolean hasColumns()
   {
      return false;
   }


   public String toString()
   {
      return getCompletionString();
   }
}
