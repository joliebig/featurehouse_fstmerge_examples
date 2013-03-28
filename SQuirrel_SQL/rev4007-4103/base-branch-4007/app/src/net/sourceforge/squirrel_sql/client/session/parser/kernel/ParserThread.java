
package net.sourceforge.squirrel_sql.client.session.parser.kernel;

import net.sourceforge.squirrel_sql.client.session.parser.kernel.completions.ErrorListener;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.completions.SQLSelectStatementListener;
import net.sourceforge.squirrel_sql.client.session.parser.kernel.completions.SQLStatement;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Vector;


public final class ParserThread extends Thread
{
   private static final StringManager s_stringMgr =
      StringManagerFactory.getStringManager(ParserThread.class);



   public static final String PARSER_THREAD_NM = "SQLParserThread";

	private String _pendingString;
	private Errors _errors;
	private SQLSchema _schema;
	private SQLStatement _curSQLSelectStat;


	private Vector<TableAliasInfo> _workingTableAliasInfos = 
	    new Vector<TableAliasInfo>();
	private TableAliasInfo[] _lastRunTableAliasInfos = new TableAliasInfo[0];
	private Vector<ErrorInfo> _workingErrorInfos = new Vector<ErrorInfo>();
	private ErrorInfo[] _lastRunErrorInfos = new ErrorInfo[0];


	private boolean _exitThread;
	private ParsingFinishedListener _parsingFinishedListener;

	private int _lastParserRunOffset;
	private int _lastErrEnd = -1;
   private int _nextStatBegin = -1;

	private String _workingString;
	private IncrementalBuffer _workingBuffer;
	private boolean _errorDetected;

   public ParserThread(SQLSchema schema)
	{
		super(PARSER_THREAD_NM);
      this._schema = schema;

		ErrorListener errListener = new ErrorListener()
		{
			public void errorDetected(String message, int line, int column)
			{
				onErrorDetected(message, line, column);
			}
		};


		this._errors = new Errors(errListener);

      setPriority(Thread.MIN_PRIORITY);

		start();
	}

	private void onErrorDetected(String message, int line, int column)
	{
		_errorDetected = true;
		int errPos = getPos(line, column);
		_lastErrEnd = getTokenEnd(errPos);
      _nextStatBegin = predictNextStatementBegin(errPos);

      if(_lastErrEnd > _nextStatBegin)
      {
         return;
      }

      int beginPos = _lastParserRunOffset + errPos;
      int endPos = _lastParserRunOffset + _lastErrEnd;

      if(beginPos < endPos)
      {
         _workingErrorInfos.add(new ErrorInfo(message, _lastParserRunOffset + errPos , _lastParserRunOffset + _lastErrEnd-1));
      }
	}

   private int predictNextStatementBegin(int errPos)
   {
      int commentIntervals[][] = calculateCommentIntervals();









      int ret = errPos;
      while(   _workingString.length() > ret && (false == startsWithBeginKeyWord(ret) || isInComment(ret, commentIntervals)) )
      {
         ++ret;
      }







      return ret;
   }

   private int[][] calculateCommentIntervals()
   {
      Vector<int[]> ret = new Vector<int[]>();
      boolean inMultiLineComment = false;
      boolean inLineComment = false;
      boolean isaSlash = false;
      boolean isaStar = false;
      boolean isaMinus = false;

      int[] curComment = null;

      for(int i=0; i < _workingString.length(); ++i)
      {
         if('*' == _workingString.charAt(i) && isaSlash && false == inMultiLineComment && false == inLineComment)
         {
            inMultiLineComment = true;
            curComment = new int[]{i-1, -1};
         }
         else if('/' == _workingString.charAt(i) && isaStar && false == inLineComment && inMultiLineComment)
         {
            inMultiLineComment = false;
            curComment[1] = i;
            ret.add(curComment);
            curComment = null;

         }
         else if('-' == _workingString.charAt(i) && isaMinus && false == inMultiLineComment && false == inLineComment)
         {
            inLineComment = true;
            curComment = new int[]{i-1, -1};
         }
         else if('\n' == _workingString.charAt(i) && false == inMultiLineComment && inLineComment)
         {
            inLineComment = false;
            curComment[1] = i;
            ret.add(curComment);
            curComment = null;
         }



         if('/' == _workingString.charAt(i))
         {
            isaSlash = true;
         }
         else if('*' == _workingString.charAt(i))
         {
            isaStar = true;
         }
         else if('-' == _workingString.charAt(i))
         {
            isaMinus = true;
         }
         else
         {
            isaSlash = false;
            isaStar = false;
            isaMinus = false;
         }
      }

      if(null != curComment)
      {
         curComment[1] = _workingString.length();
      }

      return ret.toArray(new int[ret.size()][]);


   }

   private boolean isInComment(int ret, int commentIntervals[][])
   {
      for(int i=0; i < commentIntervals.length; ++i)
      {
         if(commentIntervals[i][0] <= ret && ret <= commentIntervals[i][1])
         {
            return true;
         }
      }

      return false;
   }

   private boolean startsWithBeginKeyWord(int ret)
   {
      return    startsWithIgnoreCase(ret, "SELECT")
             || startsWithIgnoreCase(ret, "UPDATE")
             || startsWithIgnoreCase(ret, "DELETE")
             || startsWithIgnoreCase(ret, "INSERT")
             || startsWithIgnoreCase(ret, "ALTER")
             || startsWithIgnoreCase(ret, "CREATE")
             || startsWithIgnoreCase(ret, "DROP");
   }

   private boolean startsWithIgnoreCase(int ret, String keyWord)
   {
      int beginPos = ret;
      int endPos;

      if(ret == 0)
      {
         
         beginPos = 0;
      }
      else if(Character.isWhitespace(_workingString.charAt(ret-1)))
      {
         
         beginPos = ret;
      }
      else
      {
         return false;
      }

      if(_workingString.length() == beginPos + keyWord.length())
      {
         endPos = beginPos + keyWord.length();
      }
      else if(_workingString.length() > beginPos + keyWord.length() && Character.isWhitespace(_workingString.charAt(beginPos + keyWord.length())))
      {
         endPos = beginPos + keyWord.length();
      }
      else
      {
         return false;
      }

      return keyWord.equalsIgnoreCase(_workingString.substring(beginPos, endPos));
   }


   private int getTokenEnd(int errPos)
	{
		int ret = errPos;
		while(_workingString.length() > ret && false == Character.isWhitespace(_workingString.charAt(ret)))
		{
			++ret;
		}
		return ret;
	}


	private int getPos(int line, int column)
	{
		int ix = 0;

		for (int i = 0; i < line-1; i++)
		{
			ix =_workingString.indexOf('\n', ix) + 1;
		}
		ix += column;

		return ix - 1; 
	}

	public void notifyParser(String sqlText)
	{
		synchronized(this)
		{
			_pendingString = sqlText;
			this.notify();
		}
	}

	public void exitThread()
	{
		_exitThread = true;
		synchronized(this)
		{
			this.notify();
		}
	}

	public void setParsingFinishedListener(ParsingFinishedListener parsingFinishedListener)
	{
		_parsingFinishedListener = parsingFinishedListener;
	}


	public void run()
	{
		try
		{
			while(true)
			{
				synchronized(this)
				{
					this.wait();
					_workingString = _pendingString;
					_workingBuffer = new IncrementalBuffer(new StringCharacterIterator(_workingString));
				}

				if(_exitThread)
				{
					break;
				}

				
				
				_errorDetected = false;
				runParser();
				while(_errorDetected)
				{
					if(_workingString.length() > _nextStatBegin)
					{
						_workingString = _workingString.substring(_nextStatBegin, _workingString.length());
						if("".equals(_workingString.trim()))
						{
							break;
						}
					}
					else
					{
						break;
					}

					_lastParserRunOffset += _nextStatBegin;
					_workingBuffer = new IncrementalBuffer(new StringCharacterIterator(_workingString));

					_errorDetected = false;
					runParser();
				}

				
				


				
				
				
				_lastRunTableAliasInfos = _workingTableAliasInfos.toArray(new TableAliasInfo[_workingTableAliasInfos.size()]);
				_lastRunErrorInfos = _workingErrorInfos.toArray(new ErrorInfo[_workingErrorInfos.size()]);
				_workingTableAliasInfos.clear();
				_workingErrorInfos.clear();
				_lastParserRunOffset = 0;
				if(null != _parsingFinishedListener)
				{
					_parsingFinishedListener.parsingFinished();
				}
				
				

				if(_exitThread)
				{
					break;
				}
			}
		}
		catch (Exception e)
		{
			if(null != _parsingFinishedListener)
			{
				_parsingFinishedListener.parserExitedOnException(e);
			}
			e.printStackTrace();
		}
	}

	private void runParser()
	{
		_errors.reset();
		Scanner scanner = new Scanner(_workingBuffer, _errors);

		Parser parser = new Parser(scanner);
		parser.rootSchema = _schema;

		parser.addParserListener(new ParserListener()
		{
			public void statementAdded(SQLStatement statement)
			{
				onStatementAdded(statement);
			}
		});

		parser.addSQLSelectStatementListener(new SQLSelectStatementListener()
		{
			public void aliasDefined(String tableName, String aliasName)
			{
				onAliasDefined(tableName, aliasName);
			}
		});


		parser.parse();
	}

	private void onStatementAdded(SQLStatement statement)
	{
		_curSQLSelectStat = statement;
	}

	private void onAliasDefined(String tableName, String aliasName)
	{
		_workingTableAliasInfos.add(new TableAliasInfo(aliasName, tableName, _curSQLSelectStat.getStart() + _lastParserRunOffset));
	}

	public TableAliasInfo[] getTableAliasInfos()
	{
		return _lastRunTableAliasInfos;
	}

	public ErrorInfo[] getErrorInfos()
	{
		return _lastRunErrorInfos;
	}

	
	public void reset(CharacterIterator chars)
	{
		IncrementalBuffer oldBuffer = this._workingBuffer;
		this._workingBuffer = new IncrementalBuffer(chars);
		oldBuffer.eof();
	}

	
	public void end()
	{
		IncrementalBuffer oldBuffer = this._workingBuffer;
		this._workingBuffer = null;
		oldBuffer.eof();
	}

	
	public void accept(CharacterIterator chars)
	{
		_workingBuffer.waitChars();     
		_workingBuffer.accept(chars);   
	}

	
	private static class IncrementalBuffer extends Scanner.Buffer
	{
		private CharacterIterator chars;
		private char current;
		private boolean atEnd;

		IncrementalBuffer(CharacterIterator chars)
		{
			this.atEnd = false;
			this.chars = chars;
			this.current = chars != null ? chars.first() : CharacterIterator.DONE;
		}

		
		protected synchronized char read()
		{
			if (atEnd)
			{
				return eof;
			}
			else
			{
				if (current == CharacterIterator.DONE)
				{
					if (chars != null)
					{
						synchronized (chars)
						{
							chars.notify(); 
						}
					}







				}
				if (atEnd)
				{
					current = eof;
					return eof;
				}
				else
				{
					char prev = current;
					
					current = chars.next();
					return prev;
				}
			}
		}

		synchronized void eof()
		{
			atEnd = true;
			notify();
		}

		
		synchronized void accept(CharacterIterator chars)
		{
			this.chars = chars;
			this.current = chars != null ? chars.first() : CharacterIterator.DONE;
			notify();
		}

		
		void waitChars()
		{
			if (chars != null && current != CharacterIterator.DONE)
			{
				synchronized (chars)
				{
					try
					{
						chars.wait();
					}
					catch (InterruptedException e)
					{
					}
				}
			}
		}

		int getBeginIndex()
		{
			return chars != null ? chars.getBeginIndex() : 0;
		}

      protected void setIndex(int position)
      {
         this.current = chars.setIndex(position);
      }
   }

	
	private static class Errors extends ErrorStream
	{
		private int[][] errorStore;
		private int count;
		private ErrorListener listener;

		public Errors(ErrorListener listener)
		{
			this.listener = listener;
			errorStore = new int[5][3];
		}

		protected void ParsErr(int n, int line, int col)
		{
			errorStore[count][0] = n;
			errorStore[count][1] = line;
			errorStore[count][2] = col;
			count = (count + 1) % 5;
			if (listener != null)
				super.ParsErr(n, line, col);
		}

		protected void SemErr(int n, int line, int col)
		{
			errorStore[count][0] = n;
			errorStore[count][1] = line;
			errorStore[count][2] = col;
			count = (count + 1) % 5;
			if (listener != null)
			{
				switch (n)
				{
					case ParsingConstants.KW_MINUS:
						
                  StoreError(n, line, col, s_stringMgr.getString("parserthread.undefinedTable"));
						break;
					default:
						super.SemErr(n, line, col);
				}
			}
		}

		protected void StoreError(int n, int line, int col, String s)
		{
			if (listener != null)
				listener.errorDetected(s, line, col);
		}

		public void reset()
		{
			errorStore = new int[5][3];
		}
	}
}
