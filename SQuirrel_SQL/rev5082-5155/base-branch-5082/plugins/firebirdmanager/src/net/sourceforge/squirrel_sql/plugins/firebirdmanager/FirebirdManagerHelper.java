
package net.sourceforge.squirrel_sql.plugins.firebirdmanager;

import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class FirebirdManagerHelper {
	
	public final static int DISPLAY_MODE = 0;
	
	public final static int NEW_MODE = 1;
	public final static int EDIT_MODE = 2;
	
	
    private final static ILogger log = LoggerController.createLogger(FirebirdManagerHelper.class);
    
    public final static String CR = System.getProperty("line.separator", "\n");

    
    private FirebirdManagerHelper() {}
	
    
	public static ImageIcon loadIcon(String imageIconName) {
        URL imgURL = FirebirdManagerPlugin.class.getResource("images/" + imageIconName);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            log.error("Couldn't find file: images/" + imageIconName);
            return null;
        }
	}

	
	
	public static String getFileOrDir(String startName, boolean fileSelect) {
		String selection = "";

		JFileChooser fc = createFileChooser(fileSelect, startName, false);
	    fc.setMultiSelectionEnabled(false);

	    int returnVal = -1;
    	returnVal = fc.showOpenDialog(null);

    	if (returnVal == JFileChooser.APPROVE_OPTION) {
	      selection = fc.getSelectedFile().getAbsolutePath();
	    }
    	
    	return selection;
	}
	
	
	private static JFileChooser createFileChooser(boolean fileSelect, String startDirectory,
			boolean saveDialog) {
		if (startDirectory == null) {
			startDirectory = "";
		}
	    JFileChooser fc = new JFileChooser(startDirectory);
	    fc.setSelectedFile(new File(startDirectory));
	    fc.setCurrentDirectory(fc.getSelectedFile());
	    fc.enableInputMethods(false);
	    fc.setFileHidingEnabled(true);
	    if (fileSelect)
	    	fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    else
	    	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    
	    if (saveDialog)
	    	fc.setDialogType(JFileChooser.SAVE_DIALOG);
	    else
	    	fc.setDialogType(JFileChooser.OPEN_DIALOG);
	    	
	    return fc;
	}

	
	public static boolean fileExists(String filename) {
		File file = new File(filename);
		return file.exists();
	}
	
	
	public static int convertStringToIntDef(String string, int defaultValue) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	
    public static String getHost(String url) {
		
    	String s = getUrlWithoutClass(url);
    	if ("//".equals(s.substring(0, 2))) {
    		String host = s.substring(2);
    		int posPort = host.indexOf(':');
    		int posPath = host.indexOf('/');
    		if (posPath == 0) {
    			posPath = host.indexOf('\\');
    		}
    		
    		if (posPort > 0
    				&& posPort < posPath) {
    			return host.substring(0, posPort);
    		} else {
    			return host.substring(0, posPath);
    		}
    	} else {
    		return "localhost";
    	}
    }
    
	
    public static int getPort(String url) {
    	String s = getUrlWithoutClass(url);
    	if ("//".equals(s.substring(0, 2))) {
    		String host = s.substring(2);
    		int posPort = host.indexOf(':');
    		int posPath = host.indexOf('/');
    		if (posPath == 0) {
    			posPath = host.indexOf('\\');
    		}
    		
    		if (posPort > 0
    				&& posPort < posPath) {
    			return Integer.parseInt(host.substring(posPort, posPath));
    		}
    	}
		return 3050;
    }
    
    private static String getUrlWithoutClass(String url) {
    	String type = "jdbc:firebirdsql:";
    	return url.substring(type.length());
    }
	
    
	public static File getPropertiesFile(boolean saving, String startName,
			String extension, String description) {
		JFileChooser fc = createFileChooser(true, startName, saving);
		final String finalExtension = extension;
		final String finalDescription = description;

		
		if (extension != null && extension.length() > 0) {
			FileFilter ff = new FileFilter() {
				public boolean accept(File f) {
					return f.isDirectory()
							|| f.getName().toLowerCase().endsWith(
									finalExtension);
				}

				public String getDescription() {
					return finalDescription;
				}
			};
			fc.setFileFilter(ff);
		}
		
		int returnVal = -1;
		if (saving) {
			returnVal = fc.showSaveDialog(null);
		} else {
			returnVal = fc.showOpenDialog(null);
		}
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (fc.getSelectedFile().getAbsolutePath().endsWith("." + finalExtension)) {
				return fc.getSelectedFile();
			} else {
				return new File(fc.getSelectedFile().getAbsolutePath() + "." + finalExtension);
			}
		} else {
			return null;
		}
		
	}

}
