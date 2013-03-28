
package genj.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;


public class AnselCharsetTest extends TestCase {
  
  
  public void testEncodingDecoding() {

    Iterator strings = getStrings().iterator();
    while (strings.hasNext()) {

      String s = strings.next().toString();

      
      byte[] anselbytes = null;
      try {     
        ByteArrayOutputStream bout = new ByteArrayOutputStream(s.length());
        OutputStreamWriter out = new OutputStreamWriter(bout, new AnselCharset());
        out.write(s);
        out.flush();
        anselbytes = bout.toByteArray();
      } catch (IOException e) {
        fail("ioex during encode("+s+")");
      }

      
      String unicode = null;
      try {     
        BufferedReader in = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(anselbytes), new AnselCharset()));
        unicode = in.readLine();
      } catch (IOException e) {
        fail("ioex during decode(encode("+s+"))");
      }
      
      
      assertEquals("decode(encode("+s+"))!="+s, s, unicode);
    }

    
  }
  
  private final static char
   oe = '\u', 
   ae = '\u',
   ue = '\u',
   ss = '\u';
  
  
  private List getStrings() {
    
    ArrayList result = new ArrayList(32);

    
    result.add(""+oe+ae+ue+ss);
    
    
    StringBuffer buf = new StringBuffer(8193);
    for (int i=0;i<8192;i++)
      buf.append('x');
    buf.append('z');
    result.add(buf.toString());
      
    
    result.add("abc");
      
    
    result.add("ab"+oe);
      
    
    result.add("abc"+oe);

    
    buf.setLength(0);
    for (int i=0;i<8191;i++)
      buf.append('x');
    buf.append(oe);
    result.add(buf.toString());

    
    buf.append(oe);
    result.add(buf.toString());
    
    
    return result;
  }

} 
