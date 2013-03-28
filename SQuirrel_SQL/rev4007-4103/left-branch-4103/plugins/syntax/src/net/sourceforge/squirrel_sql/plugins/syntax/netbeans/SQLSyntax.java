package net.sourceforge.squirrel_sql.plugins.syntax.netbeans;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.SQLTokenListener;
import net.sourceforge.squirrel_sql.client.session.ISyntaxHighlightTokenMatcher;
import net.sourceforge.squirrel_sql.client.session.parser.ParserEventsAdapter;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.ErrorInfo;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;

import java.util.Arrays;
import java.util.Vector;



public class SQLSyntax extends Syntax
{
   
   @SuppressWarnings("unused")
   private static ILogger s_log = LoggerController.createLogger(SQLSyntax.class);



   
   private static final int ISI_WHITESPACE = 2; 
   private static final int ISI_LINE_COMMENT = 4; 
   private static final int ISI_BLOCK_COMMENT = 5; 
   private static final int ISI_STRING = 6; 
   private static final int ISI_STRING_A_BSLASH = 7; 
   private static final int ISI_CHAR = 8; 
   private static final int ISI_CHAR_A_BSLASH = 9; 
   private static final int ISI_IDENTIFIER = 10; 
   private static final int ISA_MINUS = 11; 
   private static final int ISA_EQ = 12; 
   private static final int ISA_GT = 13; 
   private static final int ISA_GTGT = 14; 
   private static final int ISA_GTGTGT = 15; 
   private static final int ISA_LT = 16; 
   private static final int ISA_LTLT = 17; 
   private static final int ISA_PLUS = 18; 
   private static final int ISA_SLASH = 19; 
   private static final int ISA_STAR = 20; 
   private static final int ISA_STAR_I_BLOCK_COMMENT = 21; 
   private static final int ISA_PIPE = 22; 
   private static final int ISA_PERCENT = 23; 
   private static final int ISA_AND = 24; 
   private static final int ISA_XOR = 25; 
   private static final int ISA_EXCLAMATION = 26; 
   private static final int ISA_ZERO = 27; 
   private static final int ISI_INT = 28; 
   private static final int ISI_OCTAL = 29; 
   private static final int ISI_DOUBLE = 30; 
   private static final int ISI_DOUBLE_EXP = 31; 
   private static final int ISI_HEX = 32; 
   private static final int ISA_DOT = 33; 

   private ISession _sess;
   private NetbeansSQLEditorPane _editorPane;
   private NetbeansPropertiesWrapper _props;
   private Vector<ErrorInfo> _currentErrorInfos = new Vector<ErrorInfo>();
   private boolean _parsingInitialized;

   private ISyntaxHighlightTokenMatcher _tokenMatcher;

   public SQLSyntax(ISession sess, NetbeansSQLEditorPane editorPane, NetbeansPropertiesWrapper props)
   {
      _sess = sess;
      _editorPane = editorPane;
      _props = props;
      tokenContextPath = SQLTokenContext.contextPath;

      _tokenMatcher = props.getSyntaxHighlightTokenMatcher(_sess, _editorPane);
   }

   private void initParsing()
   {
      if(false == _parsingInitialized && null != _props.getParserEventsProcessor(_editorPane.getSqlEntryPanelIdentifier(), _sess))
      {
         _parsingInitialized = true;
         _props.getParserEventsProcessor(_editorPane.getSqlEntryPanelIdentifier(), _sess).addParserEventsListener(new ParserEventsAdapter()
         {
            public void errorsFound(ErrorInfo[] errorInfos)
            {
               onErrorsFound(errorInfos);
            }
         });
      }
   }


   private void onErrorsFound(ErrorInfo[] errorInfos)
   {
      boolean errorsChanged = false;
      if(_currentErrorInfos.size() == errorInfos.length)
      {
         for (int i = 0; i < errorInfos.length; i++)
         {
            if(false == errorInfos[i].equals(_currentErrorInfos.get(i)))
            {
               errorsChanged = true;
               break;
            }
         }
      }
      else
      {
         errorsChanged = true;
      }

      if(errorsChanged)
      {
         _currentErrorInfos.clear();
         _currentErrorInfos.addAll(Arrays.asList(errorInfos));
         _editorPane.repaint();

      }
   }



   protected TokenID parseToken()
   {
      initParsing();

      char actChar;

      while (offset < stopOffset)
      {
         actChar = buffer[offset];

         switch (state)
         {
            case INIT:

               switch (actChar)
               {
                  case '\'': 
                     state = ISI_STRING;
                     break;
                  case '-':
                     state = ISA_MINUS;
                     break;
                  case '=':
                     state = ISA_EQ;
                     break;
                  case '>':
                     state = ISA_GT;
                     break;
                  case '<':
                     state = ISA_LT;
                     break;
                  case '+':
                     state = ISA_PLUS;
                     break;
                  case '/':
                     state = ISA_SLASH;
                     break;
                  case '*':
                     state = ISA_STAR;
                     break;
                  case '|':
                     state = ISA_PIPE;
                     break;
                  case '%':
                     state = ISA_PERCENT;
                     break;
                  case '&':
                     state = ISA_AND;
                     break;
                  case '^':
                     state = ISA_XOR;
                     break;
                  case '~':
                     offset++;
                     return SQLTokenContext.NEG;
                  case '!':
                     state = ISA_EXCLAMATION;
                     break;
                  case '0':
                     state = ISA_ZERO;
                     break;
                  case '.':
                     state = ISA_DOT;
                     break;
                  case ',':
                     offset++;
                     return SQLTokenContext.COMMA;
                  case ';':
                     offset++;
                     return SQLTokenContext.SEMICOLON;
                  case ':':
                     offset++;
                     return SQLTokenContext.COLON;
                  case '?':
                     offset++;
                     return SQLTokenContext.QUESTION;
                  case '(':
                     offset++;
                     return SQLTokenContext.LPAREN;
                  case ')':
                     offset++;
                     return SQLTokenContext.RPAREN;
                  case '[':
                     offset++;
                     return SQLTokenContext.LBRACKET;
                  case ']':
                     offset++;
                     return SQLTokenContext.RBRACKET;
                  case '{':
                     offset++;
                     return SQLTokenContext.LBRACE;
                  case '}':
                     offset++;
                     return SQLTokenContext.RBRACE;
                  case '@': 
                     offset++;
                     return SQLTokenContext.ANNOTATION;

                  default:
                     
                     if (Character.isWhitespace(actChar))
                     {
                        state = ISI_WHITESPACE;
                        break;
                     }

                     
                     if (Character.isDigit(actChar))
                     {
                        state = ISI_INT;
                        break;
                     }

                     
                     if (Character.isJavaIdentifierStart(actChar))
                     {
                        state = ISI_IDENTIFIER;
                        break;
                     }

                     offset++;
                     return SQLTokenContext.INVALID_CHAR;
               }
               break;

            case ISI_WHITESPACE: 
               if (!Character.isWhitespace(actChar))
               {
                  state = INIT;
                  return SQLTokenContext.WHITESPACE;
               }
               break;

            case ISI_LINE_COMMENT:
               switch (actChar)
               {
                  case '\n':
                     state = INIT;
                     return SQLTokenContext.LINE_COMMENT;
               }
               break;

            case ISI_BLOCK_COMMENT:
               switch (actChar)
               {
                  case '*':
                     state = ISA_STAR_I_BLOCK_COMMENT;
                     break;
               }
               break;

            case ISI_STRING:
               switch (actChar)
               {
                  case '\\':
                     state = ISI_STRING_A_BSLASH;
                     break;
                  case '\n':
                     state = INIT;
                     supposedTokenID = SQLTokenContext.STRING_LITERAL;

                     return supposedTokenID;
                  case '\'': 
                     offset++;
                     state = INIT;
                     return SQLTokenContext.STRING_LITERAL;
               }
               break;

            case ISI_STRING_A_BSLASH:
               switch (actChar)
               {
                  case '\'': 
                  case '\\':
                     break;
                  default:
                     offset--;
                     break;
               }
               state = ISI_STRING;
               break;

            case ISI_CHAR:
               switch (actChar)
               {
                  case '\\':
                     state = ISI_CHAR_A_BSLASH;
                     break;
                  case '\n':
                     state = INIT;
                     supposedTokenID = SQLTokenContext.CHAR_LITERAL;

                     return supposedTokenID;
                  case '\'':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.CHAR_LITERAL;
               }
               break;

            case ISI_CHAR_A_BSLASH:
               switch (actChar)
               {
                  case '\'':
                  case '\\':
                     break;
                  default:
                     offset--;
                     break;
               }
               state = ISI_CHAR;
               break;

            case ISI_IDENTIFIER:
               if (!(Character.isJavaIdentifierPart(actChar)))
               {
                  state = INIT;
                  return findMatchingTokenID();
               }
               break;

            case ISA_MINUS:
               switch (actChar)
               {
                  case '-':
                     state = ISI_LINE_COMMENT;
                     break;
                  default:
                     state = INIT;
                     return SQLTokenContext.MINUS;
               }
               break;

            case ISA_SLASH:
               switch (actChar)
               {
                  case '=':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.DIV_EQ;
                  case '*':
                     state = ISI_BLOCK_COMMENT;
                     break;
                  default:
                     state = INIT;
                     return SQLTokenContext.DIV;
               }
               break;

            case ISA_EQ:
               switch (actChar)
               {
                  case '=':
                     offset++;
                     return SQLTokenContext.EQ_EQ;
                  default:
                     state = INIT;
                     return SQLTokenContext.EQ;
               }
               

            case ISA_GT:
               switch (actChar)
               {
                  case '>':
                     state = ISA_GTGT;
                     break;
                  case '=':
                     offset++;
                     return SQLTokenContext.GT_EQ;
                  default:
                     state = INIT;
                     return SQLTokenContext.GT;
               }
               break;

            case ISA_GTGT:
               switch (actChar)
               {
                  case '>':
                     state = ISA_GTGTGT;
                     break;
                  case '=':
                     offset++;
                     return SQLTokenContext.RSSHIFT_EQ;
                  default:
                     state = INIT;
                     return SQLTokenContext.RSSHIFT;
               }
               break;

            case ISA_GTGTGT:
               switch (actChar)
               {
                  case '=':
                     offset++;
                     return SQLTokenContext.RUSHIFT_EQ;
                  default:
                     state = INIT;
                     return SQLTokenContext.RUSHIFT;
               }
               


            case ISA_LT:
               switch (actChar)
               {
                  case '<':
                     state = ISA_LTLT;
                     break;
                  case '=':
                     offset++;
                     return SQLTokenContext.LT_EQ;
                  default:
                     state = INIT;
                     return SQLTokenContext.LT;
               }
               break;

            case ISA_LTLT:
               switch (actChar)
               {
                  case '<':
                     state = INIT;
                     offset++;
                     return SQLTokenContext.INVALID_OPERATOR;
                  case '=':
                     offset++;
                     return SQLTokenContext.LSHIFT_EQ;
                  default:
                     state = INIT;
                     return SQLTokenContext.LSHIFT;
               }

            case ISA_PLUS:
               switch (actChar)
               {
                  case '+':
                     offset++;
                     return SQLTokenContext.PLUS_PLUS;
                  case '=':
                     offset++;
                     return SQLTokenContext.PLUS_EQ;
                  default:
                     state = INIT;
                     return SQLTokenContext.PLUS;
               }















            case ISA_STAR:
               switch (actChar)
               {
                  case '=':
                     offset++;
                     return SQLTokenContext.MUL_EQ;
                  case '/':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.INVALID_COMMENT_END; 
                  default:
                     state = INIT;
                     return SQLTokenContext.MUL;
               }

            case ISA_STAR_I_BLOCK_COMMENT:
               switch (actChar)
               {
                  case '/':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.BLOCK_COMMENT;
                  default:
                     offset--;
                     state = ISI_BLOCK_COMMENT;
                     break;
               }
               break;

            case ISA_PIPE:
               switch (actChar)
               {
                  case '=':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.OR_EQ;
                  case '|':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.OR_OR;
                  default:
                     state = INIT;
                     return SQLTokenContext.OR;
               }
               

            case ISA_PERCENT:
               switch (actChar)
               {
                  case '=':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.MOD_EQ;
                  default:
                     state = INIT;
                     return SQLTokenContext.MOD;
               }
               

            case ISA_AND:
               switch (actChar)
               {
                  case '=':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.AND_EQ;
                  case '&':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.AND_AND;
                  default:
                     state = INIT;
                     return SQLTokenContext.AND;
               }
               

            case ISA_XOR:
               switch (actChar)
               {
                  case '=':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.XOR_EQ;
                  default:
                     state = INIT;
                     return SQLTokenContext.XOR;
               }
               

            case ISA_EXCLAMATION:
               switch (actChar)
               {
                  case '=':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.NOT_EQ;
                  default:
                     state = INIT;
                     return SQLTokenContext.NOT;
               }
               

            case ISA_ZERO:
               switch (actChar)
               {
                  case '.':
                     state = ISI_DOUBLE;
                     break;
                  case 'x':
                  case 'X':
                     state = ISI_HEX;
                     break;
                  case 'l':
                  case 'L':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.LONG_LITERAL;
                  case 'f':
                  case 'F':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.FLOAT_LITERAL;
                  case 'd':
                  case 'D':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.DOUBLE_LITERAL;
                  case '8': 
                  case '9':
                     state = INIT;
                     offset++;
                     return SQLTokenContext.INVALID_OCTAL_LITERAL;
                  case 'e':
                  case 'E':
                     state = ISI_DOUBLE_EXP;
                     break;
                  default:
                     if (Character.isDigit(actChar))
                     { 
                        state = ISI_OCTAL;
                        break;
                     }
                     state = INIT;
                     return SQLTokenContext.INT_LITERAL;
               }
               break;

            case ISI_INT:
               switch (actChar)
               {
                  case 'l':
                  case 'L':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.LONG_LITERAL;
                  case '.':
                     state = ISI_DOUBLE;
                     break;
                  case 'f':
                  case 'F':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.FLOAT_LITERAL;
                  case 'd':
                  case 'D':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.DOUBLE_LITERAL;
                  case 'e':
                  case 'E':
                     state = ISI_DOUBLE_EXP;
                     break;
                  default:
                     if (!(actChar >= '0' && actChar <= '9'))
                     {
                        state = INIT;
                        return SQLTokenContext.INT_LITERAL;
                     }
               }
               break;

            case ISI_OCTAL:
               if (!(actChar >= '0' && actChar <= '7'))
               {

                  state = INIT;
                  return SQLTokenContext.OCTAL_LITERAL;
               }
               break;

            case ISI_DOUBLE:
               switch (actChar)
               {
                  case 'f':
                  case 'F':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.FLOAT_LITERAL;
                  case 'd':
                  case 'D':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.DOUBLE_LITERAL;
                  case 'e':
                  case 'E':
                     state = ISI_DOUBLE_EXP;
                     break;
                  default:
                     if (!((actChar >= '0' && actChar <= '9')
                        || actChar == '.'))
                     {

                        state = INIT;
                        return SQLTokenContext.DOUBLE_LITERAL;
                     }
               }
               break;

            case ISI_DOUBLE_EXP:
               switch (actChar)
               {
                  case 'f':
                  case 'F':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.FLOAT_LITERAL;
                  case 'd':
                  case 'D':
                     offset++;
                     state = INIT;
                     return SQLTokenContext.DOUBLE_LITERAL;
                  default:
                     if (!(Character.isDigit(actChar)
                        || actChar == '-' || actChar == '+'))
                     {
                        state = INIT;
                        return SQLTokenContext.DOUBLE_LITERAL;
                     }
               }
               break;

            case ISI_HEX:
               if (!((actChar >= 'a' && actChar <= 'f')
                  || (actChar >= 'A' && actChar <= 'F')
                  || Character.isDigit(actChar))
               )
               {

                  state = INIT;
                  return SQLTokenContext.HEX_LITERAL;
               }
               break;

            case ISA_DOT:
               if (Character.isDigit(actChar))
               {
                  state = ISI_DOUBLE;
               }
               else if (actChar == '.' && offset + 1 < stopOffset && buffer[offset + 1] == '.')
               {
                  offset += 2;
                  state = INIT;
                  return SQLTokenContext.ELLIPSIS;
               }
               else
               { 
                  state = INIT;
                  return SQLTokenContext.DOT;
               }
               break;

         } 

         offset++;
      } 

      

      if (lastBuffer)
      {
         switch (state)
         {
            case ISI_WHITESPACE:
               state = INIT;
               return SQLTokenContext.WHITESPACE;
            case ISI_IDENTIFIER:
               state = INIT;
               return findMatchingTokenID();
            case ISI_LINE_COMMENT:
               return SQLTokenContext.LINE_COMMENT; 
            case ISI_BLOCK_COMMENT:
            case ISA_STAR_I_BLOCK_COMMENT:
               return SQLTokenContext.BLOCK_COMMENT; 
            case ISI_STRING:
            case ISI_STRING_A_BSLASH:
               return SQLTokenContext.STRING_LITERAL; 
            case ISI_CHAR:
            case ISI_CHAR_A_BSLASH:
               return SQLTokenContext.CHAR_LITERAL; 
            case ISA_ZERO:
            case ISI_INT:
               state = INIT;
               return SQLTokenContext.INT_LITERAL;
            case ISI_OCTAL:
               state = INIT;
               return SQLTokenContext.OCTAL_LITERAL;
            case ISI_DOUBLE:
            case ISI_DOUBLE_EXP:
               state = INIT;
               return SQLTokenContext.DOUBLE_LITERAL;
            case ISI_HEX:
               state = INIT;
               return SQLTokenContext.HEX_LITERAL;
            case ISA_DOT:
               state = INIT;
               return SQLTokenContext.DOT;
            case ISA_MINUS:
               state = INIT;
               return SQLTokenContext.LINE_COMMENT;
            case ISA_SLASH:
               state = INIT;
               return SQLTokenContext.DIV;
            case ISA_EQ:
               state = INIT;
               return SQLTokenContext.EQ;
            case ISA_GT:
               state = INIT;
               return SQLTokenContext.GT;
            case ISA_GTGT:
               state = INIT;
               return SQLTokenContext.RSSHIFT;
            case ISA_GTGTGT:
               state = INIT;
               return SQLTokenContext.RUSHIFT;
            case ISA_LT:
               state = INIT;
               return SQLTokenContext.LT;
            case ISA_LTLT:
               state = INIT;
               return SQLTokenContext.LSHIFT;
            case ISA_PLUS:
               state = INIT;
               return SQLTokenContext.PLUS;



            case ISA_STAR:
               state = INIT;
               return SQLTokenContext.MUL;
            case ISA_PIPE:
               state = INIT;
               return SQLTokenContext.OR;
            case ISA_PERCENT:
               state = INIT;
               return SQLTokenContext.MOD;
            case ISA_AND:
               state = INIT;
               return SQLTokenContext.AND;
            case ISA_XOR:
               state = INIT;
               return SQLTokenContext.XOR;
            case ISA_EXCLAMATION:
               state = INIT;
               return SQLTokenContext.NOT;
         }
      }

      

      switch (state)
      {
         case ISI_WHITESPACE:
            return SQLTokenContext.WHITESPACE;
      }

      return null; 
   }

   private TokenID findMatchingTokenID()
   {
      TokenID tid = matchError(buffer, tokenOffset, offset - tokenOffset);
      tid = (tid != null) ? tid : matchKeyword(buffer, tokenOffset, offset - tokenOffset);
      tid = (tid != null) ? tid : matchTable(buffer, tokenOffset, offset - tokenOffset);
      tid = (tid != null) ? tid : matchFunction(buffer, tokenOffset, offset - tokenOffset);
      tid = (tid != null) ? tid : matchDataType(buffer, tokenOffset, offset - tokenOffset);
      tid = (tid != null) ? tid : matchStatementSeparator(buffer, tokenOffset, offset - tokenOffset);
      tid = (tid != null) ? tid : matchColumn(buffer, tokenOffset, offset - tokenOffset);
      return (tid != null) ? tid : SQLTokenContext.IDENTIFIER;
   }

   public String getStateName(int stateNumber)
   {
      switch (stateNumber)
      {
         case ISI_WHITESPACE:
            return "ISI_WHITESPACE"; 
         case ISI_LINE_COMMENT:
            return "ISI_LINE_COMMENT"; 
         case ISI_BLOCK_COMMENT:
            return "ISI_BLOCK_COMMENT"; 
         case ISI_STRING:
            return "ISI_STRING"; 
         case ISI_STRING_A_BSLASH:
            return "ISI_STRING_A_BSLASH"; 
         case ISI_CHAR:
            return "ISI_CHAR"; 
         case ISI_CHAR_A_BSLASH:
            return "ISI_CHAR_A_BSLASH"; 
         case ISI_IDENTIFIER:
            return "ISI_IDENTIFIER"; 
         case ISA_MINUS:
            return "ISA_MINUS"; 
         case ISA_EQ:
            return "ISA_EQ"; 
         case ISA_GT:
            return "ISA_GT"; 
         case ISA_GTGT:
            return "ISA_GTGT"; 
         case ISA_GTGTGT:
            return "ISA_GTGTGT"; 
         case ISA_LT:
            return "ISA_LT"; 
         case ISA_LTLT:
            return "ISA_LTLT"; 
         case ISA_PLUS:
            return "ISA_PLUS"; 
         case ISA_SLASH:
            return "ISA_SLASH"; 
         case ISA_STAR:
            return "ISA_STAR"; 
         case ISA_STAR_I_BLOCK_COMMENT:
            return "ISA_STAR_I_BLOCK_COMMENT"; 
         case ISA_PIPE:
            return "ISA_PIPE"; 
         case ISA_PERCENT:
            return "ISA_PERCENT"; 
         case ISA_AND:
            return "ISA_AND"; 
         case ISA_XOR:
            return "ISA_XOR"; 
         case ISA_EXCLAMATION:
            return "ISA_EXCLAMATION"; 
         case ISA_ZERO:
            return "ISA_ZERO"; 
         case ISI_INT:
            return "ISI_INT"; 
         case ISI_OCTAL:
            return "ISI_OCTAL"; 
         case ISI_DOUBLE:
            return "ISI_DOUBLE"; 
         case ISI_DOUBLE_EXP:
            return "ISI_DOUBLE_EXP"; 
         case ISI_HEX:
            return "ISI_HEX"; 
         case ISA_DOT:
            return "ISA_DOT"; 

         default:
            return super.getStateName(stateNumber);
      }
   }

   private TokenID matchTable(char[] buffer, int offset, int len)
   {

      if(_tokenMatcher.isTable(buffer, offset, len))
      {
         return SQLTokenContext.TABLE;
      }
      return null;
   }

   private TokenID matchFunction(char[] buffer, int offset, int len)
   {
      if(_tokenMatcher.isFunction(buffer, offset, len))
      {
         return SQLTokenContext.FUNCTION;
      }
      return null;
   }

   private TokenID matchDataType(char[] buffer, int offset, int len)
   {

      if(_tokenMatcher.isDataType(buffer, offset, len))
      {
         return SQLTokenContext.DATA_TYPE;
      }
      return null;
   }

   private TokenID matchStatementSeparator(char[] buffer, int offset, int len)
   {
      if(_tokenMatcher.isStatementSeparator(buffer, offset, len))
      {
         return SQLTokenContext.STATEMENT_SEPARATOR;
      }
      return null;
   }


   private TokenID matchColumn(char[] buffer, int offset, int len)
   {
      if(_tokenMatcher.isColumn(buffer, offset, len))
      {
         return SQLTokenContext.COLUMN;
      }
      return null;
  }


   private TokenID matchKeyword(char[] buffer, int offset, int len)
   {
      if(_tokenMatcher.isKeyword(buffer, offset, len))
      {
         return SQLTokenContext.PACKAGE;
      }
      return null;
   }

   private TokenID matchError(char[] buffer, int offset, int len)
   {








      
      
      
      
      
      
      int absolutePosition = stopPosition + offset - stopOffset;
      
      


      for (int i = 0; i < _currentErrorInfos.size(); i++)
      {
         ErrorInfo errInf = _currentErrorInfos.elementAt(i);









         if(absolutePosition <= errInf.beginPos && errInf.endPos <= absolutePosition + len)
         {
            return SQLTokenContext.ERROR;
         }

         if(absolutePosition == errInf.beginPos)
         {
            
            
            
            
            
            
            

            return SQLTokenContext.ERROR;
         }
      }
      return null;
   }


   public void addSQLTokenListener(SQLTokenListener tl)
   {
      _tokenMatcher.addSQLTokenListener(tl);
   }

   public void removeSQLTokenListener(SQLTokenListener tl)
   {
      _tokenMatcher.removeSQLTokenListener(tl);
   }
}
