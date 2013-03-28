package org.jmol.util;

import java.io.BufferedReader;
import java.util.Hashtable;
import java.util.Vector;

import org.jmol.api.JmolLineReader;

public class CifDataReader {
  
  private JmolLineReader reader;
  private BufferedReader br;

  private String line;  
  public String str;
  public int ich;
  public int cch;
  public boolean wasUnQuoted;
  public String strPeeked;
  public int ichPeeked;
  public int fieldCount;
  public String[] loopData;

  
  
  

  
  public CifDataReader(JmolLineReader reader) {
    this.reader = reader;
  }

  public CifDataReader(BufferedReader br) {
    this.br = br;
  }

  public static Hashtable readCifData(BufferedReader br) {
    CifDataReader cdr = new CifDataReader(br);
    return cdr.getAllCifData();
  }
  
  
  
  private Hashtable getAllCifData() {
    line = "";
    String key;
    allData = new Hashtable();
    Vector models = new Vector();
    allData.put("models", models);
    try {
      while ((key = getNextToken()) != null) {
        if (key.startsWith("global_") || key.startsWith("data_")) {
          models.add(data = new Hashtable());
          data.put("name", key);
          continue;
        }
        if (key.startsWith("loop_")) {
          getCifLoopData();
          continue;
        }
        if (key.indexOf("_") != 0) {
          Logger.warn("CIF ERROR ? should be an underscore: " + key);
        } else {
          String value = getNextToken();
          if (value == null) {
            Logger.warn("CIF ERROR ? end of file; data missing: " + key);
          } else {
            data.put(key, value);
          }
        }
      }
    } catch (Exception e) {
      
    }
    try {
      if (br != null)
        br.close();
    } catch (Exception e) {
      
    }
    return allData;
  }

  public String readLine() {
    try {
      return (line = (reader != null ? reader.readLine() : br.readLine()));
    } catch (Exception e) {
      return null;
    }
  }
  
  
  public boolean getData() throws Exception {
    
    for (int i = 0; i < fieldCount; ++i)
      if ((loopData[i] = getNextDataToken()) == null)
        return false;
    return true;
  }

  
  public String getNextToken() throws Exception {
    while (!hasMoreTokens())
      if (setStringNextLine() == null)
        return null;
    return nextToken();
  }

  
  private void setString(String str) {
    this.str = line = str;
    cch = (str == null ? 0 : str.length());
    ich = 0;
  }

  
  
  
  private String setStringNextLine() throws Exception {
    setString(readLine());
    if (line == null || line.length() == 0 || line.charAt(0) != ';')
      return line;
    ich = 1;
    String str = '\1' + line.substring(1) + '\n';
    while (readLine() != null) {
      if (line.startsWith(";")) {
        
        str = str.substring(0, str.length() - 1)
          + '\1' + line.substring(1);
        break;
      }
      str += line + '\n';
    }
    setString(str);
    return str;
  }

  
  private boolean hasMoreTokens() {
    if (str == null)
      return false;
    char ch = '#';
    while (ich < cch && ((ch = str.charAt(ich)) == ' ' || ch == '\t'))
      ++ich;
    return (ich < cch && ch != '#');
  }

  
  private String nextToken() {
    if (ich == cch)
      return null;
    int ichStart = ich;
    char ch = str.charAt(ichStart);
    if (ch != '\'' && ch != '"' && ch != '\1') {
      wasUnQuoted = true;
      while (ich < cch && (ch = str.charAt(ich)) != ' ' && ch != '\t')
        ++ich;
      if (ich == ichStart + 1)
        if (str.charAt(ichStart) == '.' || str.charAt(ichStart) == '?')
          return "\0";
      return str.substring(ichStart, ich);
    }
    wasUnQuoted = false;
    char chOpeningQuote = ch;
    boolean previousCharacterWasQuote = false;
    while (++ich < cch) {
      ch = str.charAt(ich);
      if (previousCharacterWasQuote && (ch == ' ' || ch == '\t'))
        break;
      previousCharacterWasQuote = (ch == chOpeningQuote);
    }
    if (ich == cch) {
      if (previousCharacterWasQuote) 
        return str.substring(ichStart + 1, ich - 1);
      
      return str.substring(ichStart, ich);
    }
    ++ich; 
    return str.substring(ichStart + 1, ich - 2);
  }

  
  public String getNextDataToken() throws Exception { 
    String str = peekToken();
    if (str == null)
      return null;
    if (wasUnQuoted)
      if (str.charAt(0) == '_' || str.startsWith("loop_")
          || str.startsWith("data_")
          || str.startsWith("stop_")
          || str.startsWith("global_"))
        return null;
    return getTokenPeeked();
  }
  
  
  public String peekToken() throws Exception {
    while (!hasMoreTokens())
      if (setStringNextLine() == null)
        return null;
    int ich = this.ich;
    strPeeked = nextToken();
    ichPeeked= this.ich;
    this.ich = ich;
    return strPeeked;
  }
  
  
  public String getTokenPeeked() {
    ich = ichPeeked;
    return strPeeked;
  }
  
  
  public String fullTrim(String str) {
    int pt0 = 0;
    int pt1 = str.length();
    for (;pt0 < pt1; pt0++)
      if ("\n\t ".indexOf(str.charAt(pt0)) < 0)
        break;
    for (;pt0 < pt1; pt1--)
      if ("\n\t ".indexOf(str.charAt(pt1 - 1)) < 0)
        break;
    return str.substring(pt0, pt1);
  }

  Hashtable data;
  Hashtable allData;
  private void getCifLoopData() throws Exception {
    String str;
    Vector keyWords = new Vector();
    while ((str = peekToken()) != null && str.charAt(0) == '_') {
      str  = getTokenPeeked();
      keyWords.add(str);
      data.put(str, new Vector());
    }
    fieldCount = keyWords.size();
    if (fieldCount == 0)
      return;
    loopData = new String[fieldCount];
    while (getData()) {
      for (int i = 0; i < fieldCount; i++) {
        ((Vector)data.get(keyWords.get(i))).add(loopData[i]);
      }
    }
  }  
}