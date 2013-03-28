

package edu.rice.cs.plt.text;

import java.util.regex.Pattern;

public class Bracket {
  private final Pattern _left;
  private final Pattern _right;
  private final boolean _nests;
  
  public Bracket(String leftRegex, String rightRegex, boolean nests) {
    _left = Pattern.compile(leftRegex); _right = Pattern.compile(rightRegex); _nests = nests;
  }
  
  public Pattern left() { return _left; }
  public Pattern right() { return _right; }
  public boolean nests() { return _nests; }
  
  public static Bracket literal(String leftLiteral, String rightLiteral, boolean nests) {
    return new Bracket(Pattern.quote(leftLiteral), Pattern.quote(rightLiteral), nests);
  }
  
  public static final Bracket PARENTHESES = literal("(", ")", true);
  public static final Bracket SQUARE_BRACKETS = literal("[", "]", true);
  public static final Bracket BRACES = literal("{", "}", true);
  public static final Bracket ANGLE_BRACKETS = literal("<", ">", true);
  public static final Bracket QUOTES = literal("\"", "\"", false);
  public static final Bracket APOSTROPHES = literal("'", "'", false);
  public static final Bracket C_LINE_COMMENT = new Bracket("//", TextUtil.NEWLINE_PATTERN, false);
  public static final Bracket PERL_LINE_COMMENT = new Bracket("#", TextUtil.NEWLINE_PATTERN, false);
  public static final Bracket C_BLOCK_COMMENT = literal("/*", "*/", false);
  public static final Bracket ML_BLOCK_COMMENT = literal("(*", "*)", true);
}
