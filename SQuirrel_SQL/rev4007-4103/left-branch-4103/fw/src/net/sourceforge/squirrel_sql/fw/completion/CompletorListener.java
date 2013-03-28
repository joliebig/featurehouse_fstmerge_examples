
package net.sourceforge.squirrel_sql.fw.completion;

import net.sourceforge.squirrel_sql.fw.completion.CompletionInfo;


public interface CompletorListener
{
   
   void completionSelected(CompletionInfo completion, int replaceBegin, int keyCode, int modifiers);
}
