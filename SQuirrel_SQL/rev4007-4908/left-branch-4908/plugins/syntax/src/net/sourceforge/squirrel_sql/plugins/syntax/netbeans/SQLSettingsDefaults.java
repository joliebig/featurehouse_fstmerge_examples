package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import java.awt.Color;
import java.awt.Font;
import java.util.Map;
import java.util.TreeMap;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPugin;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxStyle;

import org.netbeans.editor.Coloring;
import org.netbeans.editor.Settings;
import org.netbeans.editor.SettingsDefaults;
import org.netbeans.editor.SettingsUtil;
import org.netbeans.editor.TokenCategory;
import org.netbeans.editor.TokenContextPath;
import org.netbeans.editor.ext.ExtSettingsDefaults;
import org.netbeans.editor.ext.java.JavaLayerTokenContext;


public class SQLSettingsDefaults extends ExtSettingsDefaults
{
   
   public static final Boolean defaultJavaFormatSpaceBeforeParenthesis = Boolean.FALSE;
   public static final Boolean defaultJavaFormatSpaceAfterComma = Boolean.TRUE;




   static class SQLTokenColoringInitializer
      extends SettingsUtil.TokenColoringInitializer
   {

      Font boldFont;
      Font italicFont;
      Font normalFont;

      Settings.Evaluator boldSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.BOLD);
      Settings.Evaluator italicSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.ITALIC);
      Settings.Evaluator lightGraySubst = new SettingsUtil.ForeColorPrintColoringEvaluator(Color.lightGray);

      Coloring commentColoring = new Coloring(null, new Color(115, 115, 115), null);

      Coloring numbersColoring = new Coloring(null, new Color(120, 0, 0), null);
      private SyntaxPreferences _syntaxPreferences;

      public SQLTokenColoringInitializer(SyntaxPreferences syntaxPreferences, Font font)
      {
         super(SQLTokenContext.context);
         _syntaxPreferences = syntaxPreferences;

         boldFont =  font.deriveFont(Font.BOLD);
         italicFont = font.deriveFont(Font.ITALIC);
         normalFont = font;
      }

      public Object getTokenColoring(TokenContextPath tokenContextPath,
                                     TokenCategory tokenIDOrCategory, boolean printingSet)
      {
         if (!printingSet)
         {
            switch (tokenIDOrCategory.getNumericID())
            {
               case SQLTokenContext.IDENTIFIER_ID:
						return createColoringFromStyle(_syntaxPreferences.getIdentifierStyle());

					case SQLTokenContext.WHITESPACE_ID:
						return createColoringFromStyle(_syntaxPreferences.getWhiteSpaceStyle());


					case SQLTokenContext.OPERATORS_ID:
						return createColoringFromStyle(_syntaxPreferences.getOperatorStyle());


					case SQLTokenContext.TABLE_ID:
                  return createColoringFromStyle(_syntaxPreferences.getTableStyle());


               case SQLTokenContext.COLUMN_ID:
                  return createColoringFromStyle(_syntaxPreferences.getColumnStyle());

					case SQLTokenContext.FUNCTION_ID:
						return createColoringFromStyle(_syntaxPreferences.getFunctionStyle());

					case SQLTokenContext.DATA_TYPE_ID:
						return createColoringFromStyle(_syntaxPreferences.getDataTypeStyle());

					case SQLTokenContext.STATEMENT_SEPARATOR_ID:
						return createColoringFromStyle(_syntaxPreferences.getSeparatorStyle());

					case SQLTokenContext.ERROR_ID:
                  return createColoringFromStyle(_syntaxPreferences.getErrorStyle());

               case SQLTokenContext.ERRORS_ID:
                  return createColoringFromStyle(_syntaxPreferences.getErrorStyle());

               case SQLTokenContext.KEYWORDS_ID:
                  return createColoringFromStyle(_syntaxPreferences.getReservedWordStyle());

               case SQLTokenContext.LINE_COMMENT_ID:
               case SQLTokenContext.BLOCK_COMMENT_ID:
                  return createColoringFromStyle(_syntaxPreferences.getCommentStyle());

               case SQLTokenContext.CHAR_LITERAL_ID:
                  return createColoringFromStyle(_syntaxPreferences.getLiteralStyle());

               case SQLTokenContext.STRING_LITERAL_ID:
                  return createColoringFromStyle(_syntaxPreferences.getLiteralStyle());

               case SQLTokenContext.NUMERIC_LITERALS_ID:
                  return createColoringFromStyle(_syntaxPreferences.getLiteralStyle());




            }

         }
         else
         { 
            switch (tokenIDOrCategory.getNumericID())
            {
               case SQLTokenContext.LINE_COMMENT_ID:
               case SQLTokenContext.BLOCK_COMMENT_ID:
                  return lightGraySubst; 

               default:
                  return SettingsUtil.defaultPrintColoringEvaluator;
            }

         }

         return null;

      }

      private Coloring createColoringFromStyle(SyntaxStyle style)
      {
         if(style.isBold())
         {
            return new Coloring(boldFont, Coloring.FONT_MODE_DEFAULT, new Color(style.getTextRGB()), new Color(style.getBackgroundRGB()));
         }
         else if(style.isItalic())
         {
            return new Coloring(italicFont, Coloring.FONT_MODE_DEFAULT, new Color(style.getTextRGB()), new Color(style.getBackgroundRGB()));
         }
         else
         {
            return new Coloring(normalFont, Coloring.FONT_MODE_DEFAULT, new Color(style.getTextRGB()), new Color(style.getBackgroundRGB()));
         }
      }

   }

   static class SQLLayerTokenColoringInitializer
      extends SettingsUtil.TokenColoringInitializer
   {

      Font boldFont = SettingsDefaults.defaultFont.deriveFont(Font.BOLD);
      Settings.Evaluator italicSubst = new SettingsUtil.FontStylePrintColoringEvaluator(Font.ITALIC);

      public SQLLayerTokenColoringInitializer()
      {
         super(JavaLayerTokenContext.context);
      }

      public Object getTokenColoring(TokenContextPath tokenContextPath,
                                     TokenCategory tokenIDOrCategory, boolean printingSet)
      {
         if (!printingSet)
         {
            switch (tokenIDOrCategory.getNumericID())
            {
               case JavaLayerTokenContext.METHOD_ID:
                  return new Coloring(boldFont, Coloring.FONT_MODE_APPLY_STYLE,
                     null, null);

            }

         }
         else
         { 
            switch (tokenIDOrCategory.getNumericID())
            {
               case JavaLayerTokenContext.METHOD_ID:
                  return italicSubst;

               default:
                  return SettingsUtil.defaultPrintColoringEvaluator;
            }

         }

         return null;
      }

   }

   public static Map<String, String> getAbbrevMap(SyntaxPugin plugin)
   {
      Map<String, String> javaAbbrevMap = new TreeMap<String, String>();

      
      
      

      
      

      return javaAbbrevMap;























































   }


}
