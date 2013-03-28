
package com.csvreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import java.util.HashMap;


public class CsvReader {
	private Reader inputStream = null;

	private String fileName = null;

	
	private UserSettings userSettings = new UserSettings();

	private Charset charset = null;

	private boolean useCustomRecordDelimiter = false;

	
	

	private DataBuffer dataBuffer = new DataBuffer();

	private ColumnBuffer columnBuffer = new ColumnBuffer();

	private RawRecordBuffer rawBuffer = new RawRecordBuffer();

	private boolean[] isQualified = null;

	private String rawRecord = "";

	private HeadersHolder headersHolder = new HeadersHolder();

	
	
	

	private boolean startedColumn = false;

	private boolean startedWithQualifier = false;

	private boolean hasMoreData = true;

	private char lastLetter = '\0';

	private boolean hasReadNextLine = false;

	private int columnsCount = 0;

	private long currentRecord = 0;

	private String[] values = new String[StaticSettings.INITIAL_COLUMN_COUNT];

	private boolean initialized = false;

	private boolean closed = false;

	
	public static final int ESCAPE_MODE_DOUBLED = 1;

	
	public static final int ESCAPE_MODE_BACKSLASH = 2;

	
	public CsvReader(String fileName, char delimiter, Charset charset)
			throws FileNotFoundException {
		if (fileName == null) {
			throw new IllegalArgumentException(
					"Parameter fileName can not be null.");
		}

		if (charset == null) {
			throw new IllegalArgumentException(
					"Parameter charset can not be null.");
		}

		if (!new File(fileName).exists()) {
			throw new FileNotFoundException("File " + fileName
					+ " does not exist.");
		}

		this.fileName = fileName;
		this.userSettings.Delimiter = delimiter;
		this.charset = charset;

		isQualified = new boolean[values.length];
	}

	
	public CsvReader(String fileName, char delimiter)
			throws FileNotFoundException {
		this(fileName, delimiter, Charset.forName("ISO-8859-1"));
	}

	
	public CsvReader(String fileName) throws FileNotFoundException {
		this(fileName, Letters.COMMA);
	}

	
	public CsvReader(Reader inputStream, char delimiter) {
		if (inputStream == null) {
			throw new IllegalArgumentException(
					"Parameter inputStream can not be null.");
		}

		this.inputStream = inputStream;
		this.userSettings.Delimiter = delimiter;
		initialized = true;

		isQualified = new boolean[values.length];
	}

	
	public CsvReader(Reader inputStream) {
		this(inputStream, Letters.COMMA);
	}

	
	public CsvReader(InputStream inputStream, char delimiter, Charset charset) {
		this(new InputStreamReader(inputStream, charset), delimiter);
	}

	
	public CsvReader(InputStream inputStream, Charset charset) {
		this(new InputStreamReader(inputStream, charset));
	}

	public boolean getCaptureRawRecord() {
		return userSettings.CaptureRawRecord;
	}

	public void setCaptureRawRecord(boolean captureRawRecord) {
		userSettings.CaptureRawRecord = captureRawRecord;
	}

	public String getRawRecord() {
		return rawRecord;
	}

	
	public boolean getTrimWhitespace() {
		return userSettings.TrimWhitespace;
	}

	
	public void setTrimWhitespace(boolean trimWhitespace) {
		userSettings.TrimWhitespace = trimWhitespace;
	}

	
	public char getDelimiter() {
		return userSettings.Delimiter;
	}

	
	public void setDelimiter(char delimiter) {
		userSettings.Delimiter = delimiter;
	}

	public char getRecordDelimiter() {
		return userSettings.RecordDelimiter;
	}

	
	public void setRecordDelimiter(char recordDelimiter) {
		useCustomRecordDelimiter = true;
		userSettings.RecordDelimiter = recordDelimiter;
	}

	
	public char getTextQualifier() {
		return userSettings.TextQualifier;
	}

	
	public void setTextQualifier(char textQualifier) {
		userSettings.TextQualifier = textQualifier;
	}

	
	public boolean getUseTextQualifier() {
		return userSettings.UseTextQualifier;
	}

	
	public void setUseTextQualifier(boolean useTextQualifier) {
		userSettings.UseTextQualifier = useTextQualifier;
	}

	
	public char getComment() {
		return userSettings.Comment;
	}

	
	public void setComment(char comment) {
		userSettings.Comment = comment;
	}

	
	public boolean getUseComments() {
		return userSettings.UseComments;
	}

	
	public void setUseComments(boolean useComments) {
		userSettings.UseComments = useComments;
	}

	
	public int getEscapeMode() {
		return userSettings.EscapeMode;
	}

	
	public void setEscapeMode(int escapeMode) throws IllegalArgumentException {
		if (escapeMode != ESCAPE_MODE_DOUBLED
				&& escapeMode != ESCAPE_MODE_BACKSLASH) {
			throw new IllegalArgumentException(
					"Parameter escapeMode must be a valid value.");
		}

		userSettings.EscapeMode = escapeMode;
	}

	public boolean getSkipEmptyRecords() {
		return userSettings.SkipEmptyRecords;
	}

	public void setSkipEmptyRecords(boolean skipEmptyRecords) {
		userSettings.SkipEmptyRecords = skipEmptyRecords;
	}

	
	public boolean getSafetySwitch() {
		return userSettings.SafetySwitch;
	}

	
	public void setSafetySwitch(boolean safetySwitch) {
		userSettings.SafetySwitch = safetySwitch;
	}

	
	public int getColumnCount() {
		return columnsCount;
	}

	
	public long getCurrentRecord() {
		return currentRecord - 1;
	}

	
	public int getHeaderCount() {
		return headersHolder.Length;
	}

	
	public String[] getHeaders() throws IOException {
		checkClosed();

		if (headersHolder.Headers == null) {
			return null;
		} else {
			
			
			
			String[] clone = new String[headersHolder.Length];
			System.arraycopy(headersHolder.Headers, 0, clone, 0,
					headersHolder.Length);
			return clone;
		}
	}

	public void setHeaders(String[] headers) {
		headersHolder.Headers = headers;

		headersHolder.IndexByName.clear();

		if (headers != null) {
			headersHolder.Length = headers.length;
		} else {
			headersHolder.Length = 0;
		}

		
		for (int i = 0; i < headersHolder.Length; i++) {
			headersHolder.IndexByName.put(headers[i], Integer.valueOf(i));
		}
	}

	public String[] getValues() throws IOException {
		checkClosed();

		
		
		String[] clone = new String[columnsCount];
		System.arraycopy(values, 0, clone, 0, columnsCount);
		return clone;
	}

	
	public String get(int columnIndex) throws IOException {
		checkClosed();

		if (columnIndex > -1 && columnIndex < columnsCount) {
			return values[columnIndex];
		} else {
			return "";
		}
	}

	
	public String get(String headerName) throws IOException {
		checkClosed();

		return get(getIndex(headerName));
	}

	
	public static CsvReader parse(String data) {
		if (data == null) {
			throw new IllegalArgumentException(
					"Parameter data can not be null.");
		}

		return new CsvReader(new StringReader(data));
	}

	
	public boolean readRecord() throws IOException {
		checkClosed();

		columnsCount = 0;
		rawBuffer.Position = 0;

		dataBuffer.LineStart = dataBuffer.Position;

		hasReadNextLine = false;

		

		if (hasMoreData) {
			
			

			do {
				if (dataBuffer.Position == dataBuffer.Count) {
					checkDataLength();
				} else {
					startedWithQualifier = false;

					

					char currentLetter = dataBuffer.Buffer[dataBuffer.Position];

					if (userSettings.UseTextQualifier
							&& currentLetter == userSettings.TextQualifier) {
						
						
						
						

						lastLetter = currentLetter;

						
						startedColumn = true;
						dataBuffer.ColumnStart = dataBuffer.Position + 1;
						startedWithQualifier = true;
						boolean lastLetterWasQualifier = false;

						char escapeChar = userSettings.TextQualifier;

						if (userSettings.EscapeMode == ESCAPE_MODE_BACKSLASH) {
							escapeChar = Letters.BACKSLASH;
						}

						boolean eatingTrailingJunk = false;
						boolean lastLetterWasEscape = false;
						boolean readingComplexEscape = false;
						int escape = ComplexEscape.UNICODE;
						int escapeLength = 0;
						char escapeValue = (char) 0;

						dataBuffer.Position++;

						do {
							if (dataBuffer.Position == dataBuffer.Count) {
								checkDataLength();
							} else {
								

								currentLetter = dataBuffer.Buffer[dataBuffer.Position];

								if (eatingTrailingJunk) {
									dataBuffer.ColumnStart = dataBuffer.Position + 1;

									if (currentLetter == userSettings.Delimiter) {
										endColumn();
									} else if ((!useCustomRecordDelimiter && (currentLetter == Letters.CR || currentLetter == Letters.LF))
											|| (useCustomRecordDelimiter && currentLetter == userSettings.RecordDelimiter)) {
										endColumn();

										endRecord();
									}
								} else if (readingComplexEscape) {
									escapeLength++;

									switch (escape) {
									case ComplexEscape.UNICODE:
										escapeValue *= (char) 16;
										escapeValue += hexToDec(currentLetter);

										if (escapeLength == 4) {
											readingComplexEscape = false;
										}

										break;
									case ComplexEscape.OCTAL:
										escapeValue *= (char) 8;
										escapeValue += (char) (currentLetter - '0');

										if (escapeLength == 3) {
											readingComplexEscape = false;
										}

										break;
									case ComplexEscape.DECIMAL:
										escapeValue *= (char) 10;
										escapeValue += (char) (currentLetter - '0');

										if (escapeLength == 3) {
											readingComplexEscape = false;
										}

										break;
									case ComplexEscape.HEX:
										escapeValue *= (char) 16;
										escapeValue += hexToDec(currentLetter);

										if (escapeLength == 2) {
											readingComplexEscape = false;
										}

										break;
									}

									if (!readingComplexEscape) {
										appendLetter(escapeValue);
									} else {
										dataBuffer.ColumnStart = dataBuffer.Position + 1;
									}
								} else if (currentLetter == userSettings.TextQualifier) {
									if (lastLetterWasEscape) {
										lastLetterWasEscape = false;
										lastLetterWasQualifier = false;
									} else {
										updateCurrentValue();

										if (userSettings.EscapeMode == ESCAPE_MODE_DOUBLED) {
											lastLetterWasEscape = true;
										}

										lastLetterWasQualifier = true;
									}
								} else if (userSettings.EscapeMode == ESCAPE_MODE_BACKSLASH
										&& lastLetterWasEscape) {
									switch (currentLetter) {
									case 'n':
										appendLetter(Letters.LF);
										break;
									case 'r':
										appendLetter(Letters.CR);
										break;
									case 't':
										appendLetter(Letters.TAB);
										break;
									case 'b':
										appendLetter(Letters.BACKSPACE);
										break;
									case 'f':
										appendLetter(Letters.FORM_FEED);
										break;
									case 'e':
										appendLetter(Letters.ESCAPE);
										break;
									case 'v':
										appendLetter(Letters.VERTICAL_TAB);
										break;
									case 'a':
										appendLetter(Letters.ALERT);
										break;
									case '0':
									case '1':
									case '2':
									case '3':
									case '4':
									case '5':
									case '6':
									case '7':
										escape = ComplexEscape.OCTAL;
										readingComplexEscape = true;
										escapeLength = 1;
										escapeValue = (char) (currentLetter - '0');
										dataBuffer.ColumnStart = dataBuffer.Position + 1;
										break;
									case 'u':
									case 'x':
									case 'o':
									case 'd':
									case 'U':
									case 'X':
									case 'O':
									case 'D':
										switch (currentLetter) {
										case 'u':
										case 'U':
											escape = ComplexEscape.UNICODE;
											break;
										case 'x':
										case 'X':
											escape = ComplexEscape.HEX;
											break;
										case 'o':
										case 'O':
											escape = ComplexEscape.OCTAL;
											break;
										case 'd':
										case 'D':
											escape = ComplexEscape.DECIMAL;
											break;
										}

										readingComplexEscape = true;
										escapeLength = 0;
										escapeValue = (char) 0;
										dataBuffer.ColumnStart = dataBuffer.Position + 1;

										break;
									default:
										break;
									}

									lastLetterWasEscape = false;

									
								} else if (currentLetter == escapeChar) {
									updateCurrentValue();
									lastLetterWasEscape = true;
								} else {
									if (lastLetterWasQualifier) {
										if (currentLetter == userSettings.Delimiter) {
											endColumn();
										} else if ((!useCustomRecordDelimiter && (currentLetter == Letters.CR || currentLetter == Letters.LF))
												|| (useCustomRecordDelimiter && currentLetter == userSettings.RecordDelimiter)) {
											endColumn();

											endRecord();
										} else {
											dataBuffer.ColumnStart = dataBuffer.Position + 1;

											eatingTrailingJunk = true;
										}

										
										

										lastLetterWasQualifier = false;
									}
								}

								
								

								lastLetter = currentLetter;

								if (startedColumn) {
									dataBuffer.Position++;

									if (userSettings.SafetySwitch
											&& dataBuffer.Position
													- dataBuffer.ColumnStart
													+ columnBuffer.Position > 100000) {
										close();

										throw new IOException(
												"Maximum column length of 100,000 exceeded in column "
														+ NumberFormat
																.getIntegerInstance()
																.format(
																		columnsCount)
														+ " in record "
														+ NumberFormat
																.getIntegerInstance()
																.format(
																		currentRecord)
														+ ". Set the SafetySwitch property to false"
														+ " if you're expecting column lengths greater than 100,000 characters to"
														+ " avoid this error.");
									}
								}
							} 

						} while (hasMoreData && startedColumn);
					} else if (currentLetter == userSettings.Delimiter) {
						
						

						lastLetter = currentLetter;

						endColumn();
					} else if (useCustomRecordDelimiter
							&& currentLetter == userSettings.RecordDelimiter) {
						
						if (startedColumn || columnsCount > 0
								|| !userSettings.SkipEmptyRecords) {
							endColumn();

							endRecord();
						} else {
							dataBuffer.LineStart = dataBuffer.Position + 1;
						}

						lastLetter = currentLetter;
					} else if (!useCustomRecordDelimiter
							&& (currentLetter == Letters.CR || currentLetter == Letters.LF)) {
						
						if (startedColumn
								|| columnsCount > 0
								|| (!userSettings.SkipEmptyRecords && (currentLetter == Letters.CR || lastLetter != Letters.CR))) {
							endColumn();

							endRecord();
						} else {
							dataBuffer.LineStart = dataBuffer.Position + 1;
						}

						lastLetter = currentLetter;
					} else if (userSettings.UseComments && columnsCount == 0
							&& currentLetter == userSettings.Comment) {
						
						

						lastLetter = currentLetter;

						skipLine();
					} else if (userSettings.TrimWhitespace
							&& (currentLetter == Letters.SPACE || currentLetter == Letters.TAB)) {
						
						

						startedColumn = true;
						dataBuffer.ColumnStart = dataBuffer.Position + 1;
					} else {
						
						

						startedColumn = true;
						dataBuffer.ColumnStart = dataBuffer.Position;
						boolean lastLetterWasBackslash = false;
						boolean readingComplexEscape = false;
						int escape = ComplexEscape.UNICODE;
						int escapeLength = 0;
						char escapeValue = (char) 0;

						boolean firstLoop = true;

						do {
							if (!firstLoop
									&& dataBuffer.Position == dataBuffer.Count) {
								checkDataLength();
							} else {
								if (!firstLoop) {
									
									currentLetter = dataBuffer.Buffer[dataBuffer.Position];
								}

								if (!userSettings.UseTextQualifier
										&& userSettings.EscapeMode == ESCAPE_MODE_BACKSLASH
										&& currentLetter == Letters.BACKSLASH) {
									if (lastLetterWasBackslash) {
										lastLetterWasBackslash = false;
									} else {
										updateCurrentValue();
										lastLetterWasBackslash = true;
									}
								} else if (readingComplexEscape) {
									escapeLength++;

									switch (escape) {
									case ComplexEscape.UNICODE:
										escapeValue *= (char) 16;
										escapeValue += hexToDec(currentLetter);

										if (escapeLength == 4) {
											readingComplexEscape = false;
										}

										break;
									case ComplexEscape.OCTAL:
										escapeValue *= (char) 8;
										escapeValue += (char) (currentLetter - '0');

										if (escapeLength == 3) {
											readingComplexEscape = false;
										}

										break;
									case ComplexEscape.DECIMAL:
										escapeValue *= (char) 10;
										escapeValue += (char) (currentLetter - '0');

										if (escapeLength == 3) {
											readingComplexEscape = false;
										}

										break;
									case ComplexEscape.HEX:
										escapeValue *= (char) 16;
										escapeValue += hexToDec(currentLetter);

										if (escapeLength == 2) {
											readingComplexEscape = false;
										}

										break;
									}

									if (!readingComplexEscape) {
										appendLetter(escapeValue);
									} else {
										dataBuffer.ColumnStart = dataBuffer.Position + 1;
									}
								} else if (userSettings.EscapeMode == ESCAPE_MODE_BACKSLASH
										&& lastLetterWasBackslash) {
									switch (currentLetter) {
									case 'n':
										appendLetter(Letters.LF);
										break;
									case 'r':
										appendLetter(Letters.CR);
										break;
									case 't':
										appendLetter(Letters.TAB);
										break;
									case 'b':
										appendLetter(Letters.BACKSPACE);
										break;
									case 'f':
										appendLetter(Letters.FORM_FEED);
										break;
									case 'e':
										appendLetter(Letters.ESCAPE);
										break;
									case 'v':
										appendLetter(Letters.VERTICAL_TAB);
										break;
									case 'a':
										appendLetter(Letters.ALERT);
										break;
									case '0':
									case '1':
									case '2':
									case '3':
									case '4':
									case '5':
									case '6':
									case '7':
										escape = ComplexEscape.OCTAL;
										readingComplexEscape = true;
										escapeLength = 1;
										escapeValue = (char) (currentLetter - '0');
										dataBuffer.ColumnStart = dataBuffer.Position + 1;
										break;
									case 'u':
									case 'x':
									case 'o':
									case 'd':
									case 'U':
									case 'X':
									case 'O':
									case 'D':
										switch (currentLetter) {
										case 'u':
										case 'U':
											escape = ComplexEscape.UNICODE;
											break;
										case 'x':
										case 'X':
											escape = ComplexEscape.HEX;
											break;
										case 'o':
										case 'O':
											escape = ComplexEscape.OCTAL;
											break;
										case 'd':
										case 'D':
											escape = ComplexEscape.DECIMAL;
											break;
										}

										readingComplexEscape = true;
										escapeLength = 0;
										escapeValue = (char) 0;
										dataBuffer.ColumnStart = dataBuffer.Position + 1;

										break;
									default:
										break;
									}

									lastLetterWasBackslash = false;
								} else {
									if (currentLetter == userSettings.Delimiter) {
										endColumn();
									} else if ((!useCustomRecordDelimiter && (currentLetter == Letters.CR || currentLetter == Letters.LF))
											|| (useCustomRecordDelimiter && currentLetter == userSettings.RecordDelimiter)) {
										endColumn();

										endRecord();
									}
								}

								
								

								lastLetter = currentLetter;
								firstLoop = false;

								if (startedColumn) {
									dataBuffer.Position++;

									if (userSettings.SafetySwitch
											&& dataBuffer.Position
													- dataBuffer.ColumnStart
													+ columnBuffer.Position > 100000) {
										close();

										throw new IOException(
												"Maximum column length of 100,000 exceeded in column "
														+ NumberFormat
																.getIntegerInstance()
																.format(
																		columnsCount)
														+ " in record "
														+ NumberFormat
																.getIntegerInstance()
																.format(
																		currentRecord)
														+ ". Set the SafetySwitch property to false"
														+ " if you're expecting column lengths greater than 100,000 characters to"
														+ " avoid this error.");
									}
								}
							} 
						} while (hasMoreData && startedColumn);
					}

					if (hasMoreData) {
						dataBuffer.Position++;
					}
				} 
			} while (hasMoreData && !hasReadNextLine);

			
			

			if (startedColumn || lastLetter == userSettings.Delimiter) {
				endColumn();

				endRecord();
			}
		}

		if (userSettings.CaptureRawRecord) {
			if (hasMoreData) {
				if (rawBuffer.Position == 0) {
					rawRecord = new String(dataBuffer.Buffer,
							dataBuffer.LineStart, dataBuffer.Position
									- dataBuffer.LineStart - 1);
				} else {
					rawRecord = new String(rawBuffer.Buffer, 0,
							rawBuffer.Position)
							+ new String(dataBuffer.Buffer,
									dataBuffer.LineStart, dataBuffer.Position
											- dataBuffer.LineStart - 1);
				}
			} else {
				
				
				
				rawRecord = new String(rawBuffer.Buffer, 0, rawBuffer.Position);
			}
		} else {
			rawRecord = "";
		}

		return hasReadNextLine;
	}

	
	private void checkDataLength() throws IOException {
		if (!initialized) {
			if (fileName != null) {
				inputStream = new BufferedReader(new InputStreamReader(
						new FileInputStream(fileName), charset),
						StaticSettings.MAX_FILE_BUFFER_SIZE);
			}

			charset = null;
			initialized = true;
		}

		updateCurrentValue();

		if (userSettings.CaptureRawRecord && dataBuffer.Count > 0) {
			if (rawBuffer.Buffer.length - rawBuffer.Position < dataBuffer.Count
					- dataBuffer.LineStart) {
				int newLength = rawBuffer.Buffer.length
						+ Math.max(dataBuffer.Count - dataBuffer.LineStart,
								rawBuffer.Buffer.length);

				char[] holder = new char[newLength];

				System.arraycopy(rawBuffer.Buffer, 0, holder, 0,
						rawBuffer.Position);

				rawBuffer.Buffer = holder;
			}

			System.arraycopy(dataBuffer.Buffer, dataBuffer.LineStart,
					rawBuffer.Buffer, rawBuffer.Position, dataBuffer.Count
							- dataBuffer.LineStart);

			rawBuffer.Position += dataBuffer.Count - dataBuffer.LineStart;
		}

		try {
			dataBuffer.Count = inputStream.read(dataBuffer.Buffer, 0,
					dataBuffer.Buffer.length);
		} catch (IOException ex) {
			close();

			throw ex;
		}

		
		

		if (dataBuffer.Count == -1) {
			hasMoreData = false;
		}

		dataBuffer.Position = 0;
		dataBuffer.LineStart = 0;
		dataBuffer.ColumnStart = 0;
	}

	
	public boolean readHeaders() throws IOException {
		boolean result = readRecord();

		
		

		headersHolder.Length = columnsCount;

		headersHolder.Headers = new String[columnsCount];

		for (int i = 0; i < headersHolder.Length; i++) {
			String columnValue = get(i);

			headersHolder.Headers[i] = columnValue;

			
			headersHolder.IndexByName.put(columnValue, Integer.valueOf(i));
		}

		if (result) {
			currentRecord--;
		}

		columnsCount = 0;

		return result;
	}

	
	public String getHeader(int columnIndex) throws IOException {
		checkClosed();

		

		
		

		if (columnIndex > -1 && columnIndex < headersHolder.Length) {
			

			return headersHolder.Headers[columnIndex];
		} else {
			return "";
		}
	}

	public boolean isQualified(int columnIndex) throws IOException {
		checkClosed();

		if (columnIndex < columnsCount && columnIndex > -1) {
			return isQualified[columnIndex];
		} else {
			return false;
		}
	}

	
	private void endColumn() throws IOException {
		String currentValue = "";

		
		if (startedColumn) {
			if (columnBuffer.Position == 0) {
				if (dataBuffer.ColumnStart < dataBuffer.Position) {
					int lastLetter = dataBuffer.Position - 1;

					if (userSettings.TrimWhitespace && !startedWithQualifier) {
						while (lastLetter >= dataBuffer.ColumnStart
								&& (dataBuffer.Buffer[lastLetter] == Letters.SPACE || dataBuffer.Buffer[lastLetter] == Letters.TAB)) {
							lastLetter--;
						}
					}

					currentValue = new String(dataBuffer.Buffer,
							dataBuffer.ColumnStart, lastLetter
									- dataBuffer.ColumnStart + 1);
				}
			} else {
				updateCurrentValue();

				int lastLetter = columnBuffer.Position - 1;

				if (userSettings.TrimWhitespace && !startedWithQualifier) {
					while (lastLetter >= 0
							&& (columnBuffer.Buffer[lastLetter] == Letters.SPACE || columnBuffer.Buffer[lastLetter] == Letters.SPACE)) {
						lastLetter--;
					}
				}

				currentValue = new String(columnBuffer.Buffer, 0,
						lastLetter + 1);
			}
		}

		columnBuffer.Position = 0;

		startedColumn = false;

		if (columnsCount >= 100000 && userSettings.SafetySwitch) {
			close();

			throw new IOException(
					"Maximum column count of 100,000 exceeded in record "
							+ NumberFormat.getIntegerInstance().format(
									currentRecord)
							+ ". Set the SafetySwitch property to false"
							+ " if you're expecting more than 100,000 columns per record to"
							+ " avoid this error.");
		}

		
		
		

		if (columnsCount == values.length) {
			
			int newLength = values.length * 2;

			String[] holder = new String[newLength];

			System.arraycopy(values, 0, holder, 0, values.length);

			values = holder;

			boolean[] qualifiedHolder = new boolean[newLength];

			System.arraycopy(isQualified, 0, qualifiedHolder, 0,
					isQualified.length);

			isQualified = qualifiedHolder;
		}

		values[columnsCount] = currentValue;

		isQualified[columnsCount] = startedWithQualifier;

		currentValue = "";

		columnsCount++;
	}

	private void appendLetter(char letter) {
		if (columnBuffer.Position == columnBuffer.Buffer.length) {
			int newLength = columnBuffer.Buffer.length * 2;

			char[] holder = new char[newLength];

			System.arraycopy(columnBuffer.Buffer, 0, holder, 0,
					columnBuffer.Position);

			columnBuffer.Buffer = holder;
		}
		columnBuffer.Buffer[columnBuffer.Position++] = letter;
		dataBuffer.ColumnStart = dataBuffer.Position + 1;
	}

	private void updateCurrentValue() {
		if (startedColumn && dataBuffer.ColumnStart < dataBuffer.Position) {
			if (columnBuffer.Buffer.length - columnBuffer.Position < dataBuffer.Position
					- dataBuffer.ColumnStart) {
				int newLength = columnBuffer.Buffer.length
						+ Math.max(
								dataBuffer.Position - dataBuffer.ColumnStart,
								columnBuffer.Buffer.length);

				char[] holder = new char[newLength];

				System.arraycopy(columnBuffer.Buffer, 0, holder, 0,
						columnBuffer.Position);

				columnBuffer.Buffer = holder;
			}

			System.arraycopy(dataBuffer.Buffer, dataBuffer.ColumnStart,
					columnBuffer.Buffer, columnBuffer.Position,
					dataBuffer.Position - dataBuffer.ColumnStart);

			columnBuffer.Position += dataBuffer.Position
					- dataBuffer.ColumnStart;
		}

		dataBuffer.ColumnStart = dataBuffer.Position + 1;
	}

	
	private void endRecord() throws IOException {
		
		

		hasReadNextLine = true;

		currentRecord++;
	}

	
	public int getIndex(String headerName) throws IOException {
		checkClosed();

		Integer indexValue = headersHolder.IndexByName.get(headerName);

		if (indexValue != null) {
			return indexValue.intValue();
		} else {
			return -1;
		}
	}

	
	public boolean skipRecord() throws IOException {
		checkClosed();

		boolean recordRead = false;

		if (hasMoreData) {
			recordRead = readRecord();

			if (recordRead) {
				currentRecord--;
			}
		}

		return recordRead;
	}

	
	public boolean skipLine() throws IOException {
		checkClosed();

		

		columnsCount = 0;

		boolean skippedLine = false;

		if (hasMoreData) {
			boolean foundEol = false;

			do {
				if (dataBuffer.Position == dataBuffer.Count) {
					checkDataLength();
				} else {
					skippedLine = true;

					

					char currentLetter = dataBuffer.Buffer[dataBuffer.Position];

					if (currentLetter == Letters.CR
							|| currentLetter == Letters.LF) {
						foundEol = true;
					}

					
					

					lastLetter = currentLetter;

					if (!foundEol) {
						dataBuffer.Position++;
					}

				} 
			} while (hasMoreData && !foundEol);

			columnBuffer.Position = 0;

			dataBuffer.LineStart = dataBuffer.Position + 1;
		}

		rawBuffer.Position = 0;
		rawRecord = "";

		return skippedLine;
	}

	
	public void close() {
		if (!closed) {
			close(true);

			closed = true;
		}
	}

	
	private void close(boolean closing) {
		if (!closed) {
			if (closing) {
				charset = null;
				headersHolder.Headers = null;
				headersHolder.IndexByName = null;
				dataBuffer.Buffer = null;
				columnBuffer.Buffer = null;
				rawBuffer.Buffer = null;
			}

			try {
				if (initialized) {
					inputStream.close();
				}
			} catch (Exception e) {
				
			}

			inputStream = null;

			closed = true;
		}
	}

	
	private void checkClosed() throws IOException {
		if (closed) {
			throw new IOException(
					"This instance of the CsvReader class has already been closed.");
		}
	}

	
	protected void finalize() {
		close(false);
	}

	private class ComplexEscape {
		private static final int UNICODE = 1;

		private static final int OCTAL = 2;

		private static final int DECIMAL = 3;

		private static final int HEX = 4;
	}

	private static char hexToDec(char hex) {
		char result;

		if (hex >= 'a') {
			result = (char) (hex - 'a' + 10);
		} else if (hex >= 'A') {
			result = (char) (hex - 'A' + 10);
		} else {
			result = (char) (hex - '0');
		}

		return result;
	}

	private class DataBuffer {
		public char[] Buffer;

		public int Position;

		
		
		
		
		public int Count;

		
		
		
		
		
		public int ColumnStart;

		public int LineStart;

		public DataBuffer() {
			Buffer = new char[StaticSettings.MAX_BUFFER_SIZE];
			Position = 0;
			Count = 0;
			ColumnStart = 0;
			LineStart = 0;
		}
	}

	private class ColumnBuffer {
		public char[] Buffer;

		public int Position;

		public ColumnBuffer() {
			Buffer = new char[StaticSettings.INITIAL_COLUMN_BUFFER_SIZE];
			Position = 0;
		}
	}

	private class RawRecordBuffer {
		public char[] Buffer;

		public int Position;

		public RawRecordBuffer() {
			Buffer = new char[StaticSettings.INITIAL_COLUMN_BUFFER_SIZE
					* StaticSettings.INITIAL_COLUMN_COUNT];
			Position = 0;
		}
	}

	private class Letters {
		public static final char LF = '\n';

		public static final char CR = '\r';

		public static final char QUOTE = '"';

		public static final char COMMA = ',';

		public static final char SPACE = ' ';

		public static final char TAB = '\t';

		public static final char POUND = '#';

		public static final char BACKSLASH = '\\';

		public static final char NULL = '\0';

		public static final char BACKSPACE = '\b';

		public static final char FORM_FEED = '\f';

		public static final char ESCAPE = '\u'; 

		public static final char VERTICAL_TAB = '\u';

		public static final char ALERT = '\u';
	}

	private class UserSettings {
		
		
		public boolean CaseSensitive;

		public char TextQualifier;

		public boolean TrimWhitespace;

		public boolean UseTextQualifier;

		public char Delimiter;

		public char RecordDelimiter;

		public char Comment;

		public boolean UseComments;

		public int EscapeMode;

		public boolean SafetySwitch;

		public boolean SkipEmptyRecords;

		public boolean CaptureRawRecord;

		public UserSettings() {
			CaseSensitive = true;
			TextQualifier = Letters.QUOTE;
			TrimWhitespace = true;
			UseTextQualifier = true;
			Delimiter = Letters.COMMA;
			RecordDelimiter = Letters.NULL;
			Comment = Letters.POUND;
			UseComments = false;
			EscapeMode = CsvReader.ESCAPE_MODE_DOUBLED;
			SafetySwitch = true;
			SkipEmptyRecords = true;
			CaptureRawRecord = true;
		}
	}

	private class HeadersHolder {
		public String[] Headers;

		public int Length;

		public HashMap<String, Integer> IndexByName;

		public HeadersHolder() {
			Headers = null;
			Length = 0;
			IndexByName = new HashMap<String, Integer>();
		}
	}

	private class StaticSettings {
		
		
		
		public static final int MAX_BUFFER_SIZE = 1024;

		public static final int MAX_FILE_BUFFER_SIZE = 4 * 1024;

		public static final int INITIAL_COLUMN_COUNT = 10;

		public static final int INITIAL_COLUMN_BUFFER_SIZE = 50;
	}
}