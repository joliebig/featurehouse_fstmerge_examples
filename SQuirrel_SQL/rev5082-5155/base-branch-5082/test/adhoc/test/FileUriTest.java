
package test;

import java.io.File;
import java.net.URL;


public class FileUriTest
{

	public static void main(String args[]) throws Exception {
		File f = new File(args[0]);
		if (f.exists()) {
			System.out.println("File ("+args[0]+") exists");
		} else {
			System.out.println("File ("+args[0]+") does not exist");
		}
		URL url = f.toURI().toURL();
		
		
		File f2 = new File(url.getFile());
		if (f2.exists()) {
			System.out.println("File from URI ("+url.getFile()+") exists");
		} else {
			System.out.println("File from URI ("+url.getFile()+") does not exist");
		}

		
		File f3 = new File(url.toURI());
		if (f3.exists()) {
			System.out.println("2nd File from URI ("+f3.getAbsolutePath()+") exists");
		} else {
			System.out.println("2nd File from URI ("+f3.getAbsolutePath()+") does not exist");
		}
		
	}
}
