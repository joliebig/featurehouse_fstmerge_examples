package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import org.netbeans.editor.Syntax;
import org.netbeans.editor.ext.ExtKit;

import javax.swing.text.Document;


public class SQLKit extends ExtKit
{
   private SyntaxFactory _syntaxFactory;



   public SQLKit(SyntaxFactory syntaxFactory)
   {
      _syntaxFactory = syntaxFactory;
   }

   
   public Syntax createSyntax(Document doc)
   {
      return _syntaxFactory.getSyntax(doc);
   }











}
