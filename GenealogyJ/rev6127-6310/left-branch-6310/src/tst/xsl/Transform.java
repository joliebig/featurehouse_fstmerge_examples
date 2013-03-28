package xsl;

import java.io.File;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


public class Transform {
    
    public static void main(String[] args) {

        
        if (args.length!=2) {
            System.out.println("Please specify xml and xsl");
            System.exit(1);
            return;
        }
        
        System.out.println("xml to transform: "+args[0]);
        System.out.println("xslt to use: "+args[1]);
        
        
        
        try {
            Source xslt = new StreamSource(new File(args[1]));

            Transformer t = TransformerFactory.newInstance().newTransformer(xslt);
            
            Source s = new StreamSource(new File(args[0]));
            
            Result r = new StreamResult(System.out);
            
            t.transform(s, r);
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

}
