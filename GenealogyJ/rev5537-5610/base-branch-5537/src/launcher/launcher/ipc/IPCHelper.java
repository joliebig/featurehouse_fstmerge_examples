
package launcher.ipc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;


 class IPCHelper {
  
   final static String[] TERMINATORS = { "\n\n", "\r\n\r\n" };
  
   static String read(InputStream stream, String[] terminators, int max) throws IOException {
    
    
    Reader in = new InputStreamReader(stream);
    char[] buffer = new char[max];
    int msglen = 0;
    loop: while (true) {
      
      int read = in.read(buffer, msglen, buffer.length-msglen);
      if (read<0) break;
      msglen += read;
      
      if (msglen==buffer.length)
        break;
      
      String snapshot = new String(buffer, 0, msglen);
      for (int i = 0; i < terminators.length; i++) {
        if (snapshot.endsWith(terminators[i])) {
          msglen -= terminators[i].length();
          break loop;
        }
      }
      
    }
    
    
    return new String(buffer, 0, msglen);
  }

   static void write(OutputStream stream, String string) throws IOException {
    Writer out = new OutputStreamWriter(stream, Charset.forName("utf8"));
    out.write(string);
    out.flush();
  }

}
