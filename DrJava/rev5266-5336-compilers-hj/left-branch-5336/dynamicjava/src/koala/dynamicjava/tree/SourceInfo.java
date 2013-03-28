



package koala.dynamicjava.tree;

import java.io.*;

import edu.rice.cs.plt.object.ObjectUtil;
import edu.rice.cs.plt.tuple.Option;


public final class SourceInfo implements Comparable<SourceInfo> {
  
  public interface Wrapper {
    SourceInfo getSourceInfo();
  }
  
  public static final SourceInfo NONE = new SourceInfo(Option.<File>none(), 0, 0, 0, 0);
  
  public static SourceInfo point(File f, int line, int column) {
    return new SourceInfo(Option.wrap(f), line, column, line, column);
  }
  
  public static SourceInfo range(File f, int startLine, int startColumn, int endLine, int endColumn) {
    return new SourceInfo(Option.wrap(f), startLine, startColumn, endLine, endColumn);
  }
  
  public static SourceInfo extend(SourceInfo si, int endLine, int endColumn) {
    return new SourceInfo(si._file, si._startLine, si._startColumn, endLine, endColumn);
  }
  
  public static SourceInfo extend(Wrapper wrapper, int endLine, int endColumn) {
    return extend(wrapper.getSourceInfo(), endLine, endColumn);
  }
  
  public static SourceInfo prepend(int startLine, int startColumn, SourceInfo si) {
    return new SourceInfo(si._file, startLine, startColumn, si._endLine, si._endColumn);
  }
  
  public static SourceInfo prepend(int startLine, int startColumn, Wrapper wrapper) {
    return prepend(startLine, startColumn, wrapper.getSourceInfo());
  }
  
  public static SourceInfo span(SourceInfo first, SourceInfo second) {
    assert ObjectUtil.equal(first._file, second._file);
    return new SourceInfo(first._file, first._startLine, first._startColumn, second._endLine, second._endColumn);
  }
  
  public static SourceInfo span(SourceInfo first, Wrapper second) {
    return span(first, second.getSourceInfo());
  }
  
  public static SourceInfo span(Wrapper first, SourceInfo second) {
    return span(first.getSourceInfo(), second);
  }
  
  public static SourceInfo span(Wrapper first, Wrapper second) {
    return span(first.getSourceInfo(), second.getSourceInfo());
  }
  
  
  private final Option<File> _file;
  
  
  private final int _startLine;
  
  
  private final int _startColumn;
  
  
  private final int _endLine;
  
  
  private final int _endColumn;

  private SourceInfo(Option<File> file, int startLine, int startColumn, int endLine, int endColumn) {
    _file = file;
    _startLine = startLine;
    _startColumn = startColumn;
    _endLine = endLine;
    _endColumn = endColumn;
  }
  
  
  public File getFile() { return _file.unwrap(null); }
  
  
  public String getFilename() { return _file.isNone() ? "(no file)" : _file.unwrap().getPath(); }
  
  public int getStartLine() { return _startLine; }
  public int getStartColumn() { return _startColumn; }
  public int getEndLine() { return _endLine; }
  public int getEndColumn() { return _endColumn; }
  
  
  public String toString() {
    return "[" + getFilename() + ": " +
           "(" + _startLine + "," + _startColumn + ")-" +
           "(" + _endLine + "," + _endColumn + ")]";
  }

  
  @Override public boolean equals(Object obj) {
    if (obj == null || obj.getClass() != this.getClass()) {
      return false;
    }
    else {
      SourceInfo casted = (SourceInfo) obj;
      return
        this._file.equals(casted._file) &&
        this._startLine == casted._startLine &&
        this._startColumn == casted._startColumn &&
        this._endLine == casted._endLine &&
        this._endColumn == casted._endColumn;
    }
  }

  @Override public int hashCode() {
    return ObjectUtil.hash(getClass(), _file, _startLine, _startColumn, _endLine, _endColumn);
  }
  
  public int compareTo(SourceInfo that) {
    int result = Option.<File>comparator().compare(this._file, that._file);
    if (result == 0) {
      result = ObjectUtil.compare(this._startLine, that._startLine,
                                  this._startColumn, that._startColumn,
                                  this._endLine, that._endLine,
                                  this._endColumn, that._endColumn);
    }
    return result;
  }
  
}
