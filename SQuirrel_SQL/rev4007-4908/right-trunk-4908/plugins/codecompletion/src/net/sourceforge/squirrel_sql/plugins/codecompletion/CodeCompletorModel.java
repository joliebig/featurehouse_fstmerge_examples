
package net.sourceforge.squirrel_sql.plugins.codecompletion;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;
import net.sourceforge.squirrel_sql.fw.completion.CompletionCandidates;
import net.sourceforge.squirrel_sql.fw.completion.ICompletorModel;


public class CodeCompletorModel implements ICompletorModel
{
   private StandardCompletorModel _standardCompletorModel;
   private CompletionFunctionsModel _completionFunctionsModel;
   private boolean _functionsAdded;

   CodeCompletorModel(ISession session, CodeCompletionPlugin plugin, CodeCompletionInfoCollection codeCompletionInfos, IIdentifier sqlEntryPanelIdentifier)
   {
      _completionFunctionsModel = new CompletionFunctionsModel(session);
      _standardCompletorModel = new StandardCompletorModel(session, plugin, codeCompletionInfos, sqlEntryPanelIdentifier);

   }

   public CompletionCandidates getCompletionCandidates(String textTillCarret)
   {
      if(false == _functionsAdded)
      {
         
         
         _functionsAdded = _standardCompletorModel.getCodeCompletionInfoCollection().addCompletionsAtListBegin(null, null, _completionFunctionsModel.getCompletions());
      }

      CompletionCandidates functionResult = _completionFunctionsModel.getCompletionCandidates(textTillCarret);

      if(null == functionResult)
      {
         return _standardCompletorModel.getCompletionCandidates(textTillCarret);
      }
      else
      {
         return functionResult;
      }
   }

   public SQLTokenListener getSQLTokenListener()
   {
      return _standardCompletorModel.getSQLTokenListener();
   }

}
