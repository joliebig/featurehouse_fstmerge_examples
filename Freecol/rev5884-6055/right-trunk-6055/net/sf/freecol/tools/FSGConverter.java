


package net.sf.freecol.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;


public class FSGConverter {

    
    private static FSGConverter singleton;
    
    
    
    private FSGConverter() {
        
    }
    
    
    
    public static FSGConverter getFSGConverter() {
        
        if (singleton == null) {
            singleton = new FSGConverter();
        }
        return singleton;
    }

    
    
    public void convertToXML(File in, File out) throws FileNotFoundException, IOException {
        convertToXML(new FileInputStream(in), new FileOutputStream(out));
    }

    
    public void convertToXML(InputStream in, OutputStream out) throws IOException {
        try {
            in = new BufferedInputStream(in);
            out = new BufferedOutputStream(out);
            
            
            in.mark(10);
            byte[] buf = new byte[5];
            in.read(buf, 0, 5);
            in.reset();
            if (!(new String(buf)).equals("<?xml")) {                
                in =  new BufferedInputStream(new GZIPInputStream(in));
            }

            
            int indent = 0;
            int i;      
            while ((i = in.read()) != -1) {
                char c = (char) i;
                if (c == '<') {
                    i = in.read();
                    char b = (char) i;
                    if (b == '/') {
                        indent -= 4;
                    }
                    for (int h=0; h<indent; h++) {
                        out.write(' ');
                    }
                    out.write(c);
                    if (b != '\n' && b != '\r') {
                        out.write(b);
                    }
                    if (b != '/' && b != '?') {
                        indent += 4;
                    }
                } else if (c == '/') {
                    out.write(c);
                    i = in.read();
                    c = (char) i;
                    if (c == '>') {
                        indent -= 4;
                        out.write(c);
                        out.write('\n');
                    }
                } else if (c == '>') {
                    out.write(c);
                    out.write('\n');
                } else if (c != '\n' && c != '\r') {
                    out.write(c);
                }           
            }

        } finally {
            in.close();
            out.close();
        }
    }
    
    
    
    private static void printUsage() {
        System.out.println("A program for converting FreeCol Savegames.");
        System.out.println();
        System.out.println("Usage: java -cp FreeCol.jar net.sf.freecol.tools.FSGConverter [-][-]output:xml FSG_FILE [OUTPUT_FILE]");
        System.out.println();
        System.out.println("output:xml \tThe output will be indented XML.");
        System.out.println();
        System.out.println("The output file will get the same name as FSG_FILE if not specified (with \".fsg\" replaced with \".xml\").");
    }
    
    
    public static void main(String[] args) {
        if (args.length >= 2 && args[0].endsWith("output:xml")) {
            File in = new File(args[1]);
            if (!in.exists()) {
                printUsage();
                System.exit(1);
            }
            File out;
            if (args.length >= 3) {
                out = new File(args[2]);
            } else {
                String filename = in.getName().replaceAll(".fsg", ".xml");
                if (filename.equals(in.getName())) {
                    filename += ".xml";
                }
                out = new File(filename);
            }
            try {
                FSGConverter fsgc = FSGConverter.getFSGConverter();
                fsgc.convertToXML(in, out);
            } catch (IOException e) {
                System.out.println("An error occured while converting the file.");
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            printUsage();
            System.exit(1);
        }
    }
}
