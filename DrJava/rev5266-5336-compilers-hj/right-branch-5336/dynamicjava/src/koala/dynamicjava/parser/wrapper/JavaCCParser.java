

package koala.dynamicjava.parser.wrapper;

import java.io.*;
import java.util.*;

import edu.rice.cs.dynamicjava.Options;

import koala.dynamicjava.parser.impl.*;
import koala.dynamicjava.tree.*;



public class JavaCCParser implements SourceCodeParser {
  
  private final Parser _parser;
  private final File _f;
  
  public JavaCCParser(InputStream is, File f, Options opt) {
    _parser = new Parser(is);
    _parser.setFile(f);
    _parser.setOptions(opt);
    _f = f;
  }
  
  public JavaCCParser(InputStream is, Options opt) {
    _parser = new Parser(is);
    _parser.setOptions(opt);
    _f = null;
  }
  
  public JavaCCParser(Reader r, File f, Options opt) {
    _parser = new Parser(r);
    _parser.setFile(f);
    _parser.setOptions(opt);
    _f = f;
  }
  
  public JavaCCParser(Reader r, Options opt) {
    _parser = new Parser(r);
    _parser.setOptions(opt);
    _f = null;
  }
  
  
  public List<Node> parseStream() {
    try {
      return _parser.parseStream();
    }
    catch (ParseException e) {
      throw new ParseError(e, _f);
    }
    catch (TokenMgrError e) {
      throw new ParseError(e, SourceInfo.point(_f, 0, 0));
    }
    catch (Error e) {
      
      String msg = e.getMessage();
      if (msg != null && msg.startsWith("Invalid escape character")) {
        throw new ParseError(e, SourceInfo.point(_f, 0, 0));
      }
      else { throw e; }
    }
  }
  
  
  public CompilationUnit parseCompilationUnit() {
    try {
      return _parser.parseCompilationUnit();
    }
    catch (ParseException e) {
      throw new ParseError(e, _f);
    }
    catch (TokenMgrError e) {
      throw new ParseError(e, SourceInfo.point(_f, 0, 0));
    }
    catch (Error e) {
      
      String msg = e.getMessage();
      if (msg != null && msg.startsWith("Invalid escape character")) {
        throw new ParseError(e, SourceInfo.point(_f, 0, 0));
      }
      else { throw e; }
    }
  }
}
