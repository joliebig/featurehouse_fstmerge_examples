
package org.jmol.jvxl.readers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

import org.jmol.util.BinaryDocument;
import org.jmol.util.Parser;

abstract class SurfaceFileReader extends SurfaceReader {

  protected BufferedReader br;
  protected BinaryDocument binarydoc;
  protected OutputStream os;
 
  SurfaceFileReader(SurfaceGenerator sg, BufferedReader br) {
    super(sg);
    this.br = br; 
  }
  
  protected void setOutputStream(OutputStream os) {
    if (binarydoc == null)
      this.os = os; 
    else
      binarydoc.setOutputStream(os);
  }
  
  protected void closeReader() {
    if (br != null)
      try {
        br.close();
      } catch (IOException e) {
        
      }
    if (os != null)
      try {
        os.flush();
        os.close();
      } catch (IOException e) {
        
      }
    if (binarydoc != null)
      binarydoc.close();
  }
  
  static String determineFileType(BufferedReader bufferedReader) {
    
    
    
    
    
    String line;
    LimitedLineReader br = new LimitedLineReader(bufferedReader, 16000);
    
    if ((line = br.info()).indexOf("<jvxl") >= 0 && line.indexOf("<?xml") >= 0)
      return "JvxlXML";
    if (line.indexOf("#JVXL+") >= 0)
      return "Jvxl+";
    if (line.indexOf("#JVXL") >= 0)
      return "Jvxl";
    if (line.indexOf("&plot") == 0)
      return "Jaguar";
    if (line.indexOf("!NTITLE") >= 0 || line.indexOf("REMARKS ") >= 0)
      return "Xplor";
    if (line.indexOf("MAP ") == 208)
      return "MRC" + (line.charAt(67) == '\0' ? "-" : "+");
    if (line.indexOf("<efvet ") >= 0)
      return "Efvet";
    if (line.indexOf(PmeshReader.PMESH_BINARY_MAGIC_NUMBER) == 0)
      return "Pmesh";
    line = br.readNonCommentLine();
    if (line.indexOf("object 1 class gridpositions counts") == 0)
      return "Apbs";

    
    
    String[] tokens = Parser.getTokens(line); 
    line = br.readNonCommentLine();
    if (tokens.length == 2 
        && Parser.parseInt(tokens[0]) == 3 
        && Parser.parseInt(tokens[1])!= Integer.MIN_VALUE) {
      tokens = Parser.getTokens(line);
      if (tokens.length == 3 
          && Parser.parseInt(tokens[0])!= Integer.MIN_VALUE 
          && Parser.parseInt(tokens[1])!= Integer.MIN_VALUE
          && Parser.parseInt(tokens[2])!= Integer.MIN_VALUE)
        return "PltFormatted";
    }
    line = br.readNonCommentLine(); 
    
    int nAtoms = Parser.parseInt(line);
    if (nAtoms == Integer.MIN_VALUE)
      return (line.indexOf("+") == 0 ? "Jvxl+" : "UNKNOWN");
    if (nAtoms >= 0)
      return "Cube"; 
    nAtoms = -nAtoms;
    for (int i = 4 + nAtoms; --i >=0;)
      if ((line = br.readNonCommentLine()) == null)
        return "UNKNOWN";
    int nSurfaces = Parser.parseInt(line);
    if (nSurfaces == Integer.MIN_VALUE)
      return "UNKNOWN";
    return (nSurfaces < 0 ?  "Jvxl" : "Cube"); 
  }
  
  void discardTempData(boolean discardAll) {
    closeReader();
    super.discardTempData(discardAll);
  }
     
  protected String line;
  protected int[] next = new int[1];
  
  protected String[] getTokens() {
    return Parser.getTokens(line, 0);
  }

  protected float parseFloat() {
    return Parser.parseFloat(line, next);
  }

  protected float parseFloat(String s) {
    next[0] = 0;
    return Parser.parseFloat(s, next);
  }

  protected int parseInt() {
    return Parser.parseInt(line, next);
  }
  
  protected int parseInt(String s) {
    next[0] = 0;
    return Parser.parseInt(s, next);
  }
  
  protected int parseIntNext(String s) {
    return Parser.parseInt(s, next);
  }
    
  protected float[] parseFloatArray(String s) {
    next[0] = 0;
    return Parser.parseFloatArray(s, next);
  }

  protected float[] parseFloatArray() {
    return Parser.parseFloatArray(line, next);
  }

  protected String getNextQuotedString() {
    return Parser.getNextQuotedString(line, next);
  }

  protected void skipTo(String info, String what) throws Exception {
    if (info != null)
      while (readLine().indexOf(info) < 0) {
      }
    if (what != null)
      next[0] = line.indexOf(what) + what.length() + 2;
  }

  protected String readLine() throws Exception {
    line = br.readLine();
    nBytes += line.length();
    if (os != null && line != null) {
      os.write(line.getBytes());
      os.write('\n');
    }
    return line;
  } 
}

class LimitedLineReader {
  
  private char[] buf;
  private int cchBuf;
  private int ichCurrent;

  LimitedLineReader(BufferedReader bufferedReader, int readLimit) {
    buf = new char[readLimit];
    try {
      bufferedReader.mark(readLimit);
      cchBuf = bufferedReader.read(buf);
      ichCurrent = 0;
      bufferedReader.reset();
    } catch (Exception e) {      
    }
  }

  protected String info() {
    return new String(buf);  
  }
  
  protected String readNonCommentLine() {
    while (ichCurrent < cchBuf) {
      int ichBeginningOfLine = ichCurrent;
      char ch = 0;
      while (ichCurrent < cchBuf &&
             (ch = buf[ichCurrent++]) != '\r' && ch != '\n') {
      }
      int cchLine = ichCurrent - ichBeginningOfLine;
      if (ch == '\r' && ichCurrent < cchBuf && buf[ichCurrent] == '\n')
        ++ichCurrent;
      if (buf[ichBeginningOfLine] == '#') 
        continue;
      StringBuffer sb = new StringBuffer(cchLine);
      sb.append(buf, ichBeginningOfLine, cchLine);
      return sb.toString();
    }
    return "";
  }
}
