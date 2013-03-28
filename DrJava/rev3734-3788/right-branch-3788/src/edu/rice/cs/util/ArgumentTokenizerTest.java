

package edu.rice.cs.util;

import edu.rice.cs.drjava.DrJavaTestCase;

import java.util.Arrays;
import java.util.List;


public class ArgumentTokenizerTest extends DrJavaTestCase {

  
  public ArgumentTokenizerTest(String name) {
    super(name);
  }

  
  protected void _assertTokenized(String typed, String[] expected) {
    _assertTokenized(typed, expected, false);
  }

  
  protected void _assertTokenized(String typed, String[] expected,
                                  boolean stringify) {
    List<String> actual = ArgumentTokenizer.tokenize(typed, stringify);
    List expectedList = Arrays.asList(expected);
    assertEquals("tokenized argument list should match expected",
                 expectedList, actual);
  }

  
  public void testTokenizeArguments() {
    
    
    _assertTokenized("a b c",
                     new String[]{"a","b","c"});
    
    
    _assertTokenized("\"a b c\"",
                     new String[]{"a b c"});

    
    
    
    
    _assertTokenized("\"a b\"c d",
                     new String[]{"a bc","d"});

    
    
    
    
    _assertTokenized("'a b'c d",
                     new String[]{"a bc","d"});

    
    
    
    
    _assertTokenized("a b\"c d\"",
                     new String[]{"a","bc d"});

    
    
    
    
    _assertTokenized("a b'c d'",
                     new String[]{"a","bc d"});

    
    
    _assertTokenized("a b'c d'\"e f\" g \"hi \"",
                     new String[]{"a","bc de f","g","hi "});

    
    
    _assertTokenized("c:\\\\file.txt",
                     new String[]{"c:\\file.txt"});

    
    
    _assertTokenized("/home/user/file",
                     new String[]{"/home/user/file"});

    
    
    _assertTokenized("\"asdf",
                     new String[]{"asdf"});
  }

  
  public void testTokenizeEscapedArgs() {
    
    
    _assertTokenized("\\j",
                     new String[]{"j"});
    
    
    _assertTokenized("\\\"",
                     new String[]{"\""});
    
    
    _assertTokenized("\\\\",
                     new String[]{"\\"});
    
    
    _assertTokenized("a\\ b",
                     new String[]{"a b"});
  }

  
  public void testTokenizeQuotedEscapedArgs() {
    
    
    _assertTokenized("\"a \\\" b\"",
                     new String[]{"a \" b"});
    
    
    _assertTokenized("\"'\"",
                     new String[]{"'"});
    
    
    _assertTokenized("\\\\",
                     new String[]{"\\"});
    
    
    _assertTokenized("\"\\\" \\d\"",
                     new String[]{"\" \\d"});
    
    
    _assertTokenized("\"\\n\"",
                     new String[]{"\\n"});
    
    
    _assertTokenized("\"\\t\"",
                     new String[]{"\\t"});
    
    
    _assertTokenized("\"\\r\"",
                     new String[]{"\\r"});
    
    
    _assertTokenized("\"\\f\"",
                     new String[]{"\\f"});
    
    
    _assertTokenized("\"\\b\"",
                     new String[]{"\\b"});
  }

  
  public void testTokenizeSingleQuotedArgs() {
    
    
    _assertTokenized("'asdf'",
                     new String[]{"asdf"});
    
    
    _assertTokenized("'a b c'",
                     new String[]{"a b c"});
    
    
    _assertTokenized("'\\'",
                     new String[]{"\\"});
  }

  
  public void testTokenizeAndStringify() {
    
    
    _assertTokenized("a b c",
                     new String[]{"\"a\"", "\"b\"", "\"c\""},
                     true);
    
    
    _assertTokenized("\\",
                     new String[]{"\"\\\\\""},
                     true);
    
    
    _assertTokenized("\\\"",
                     new String[]{"\"\\\"\""},
                     true);
    
    
    _assertTokenized("\"\\n\"",
                     new String[]{"\"\\\\n\""},
                     true);
  }
}
