

import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;


public class ManifestBuilder {

    public static String BUILT_BY_KEY = "-builtBy";

    public static String VERSION_KEY = "-version";

    
    public static void main(String[] args) throws Exception {
        if (args.length == 0 || args.length % 2 != 0) {
            printUsage();
        }
        buildManifest(parseArgs(args));
    }

    private static void printUsage() {
        System.err.println("Usage: MainfestBuilder [" + 
                           BUILT_BY_KEY+" <user> | " +
                           VERSION_KEY + " <version> ]");
        System.err.println();
        System.err.println("\t" + BUILT_BY_KEY
                            + " : author of the manifest");
        System.err.println();
        System.err.println("\t" + VERSION_KEY
                            + " : version of the jar");
        System.exit(1);
    }

    private static HashMap<String, String> parseArgs(String[] args) {
        HashMap<String, String> result = new HashMap<String, String>();

        for (int i = 0; i < args.length; i += 2) {
            String key = args[i];
            validateKey(key);
            String value = args[i + 1];
            result.put(key, value);
        }
        return result;
    }

    private static void validateKey(String key) {
        if (!BUILT_BY_KEY.equals(key) && !VERSION_KEY.equals(key)) {
            printUsage();
        }
    }

    private static void buildManifest(HashMap<String, String> argMap)
            throws Exception {
        Manifest m = new Manifest();
        Attributes a = m.getMainAttributes();
        a.put(Attributes.Name.MANIFEST_VERSION, "1.0");
        
        a.put("Application-Version", argMap.get(VERSION_KEY));
        a.put(Attributes.Name.CLASS_PATH, 
                "lib/antlr-2.7.5H3.jar "
                + "lib/commons-cli.jar " 
                + "lib/commons-logging-1.0.4.jar "
                + "lib/forms.jar " 
                + "lib/fw.jar " 
                + "lib/hibernate3.jar "
                + "lib/jxl.jar " 
                + "lib/log4j.jar " 
                + "lib/nanoxml-2.1.jar "
                + "lib/openide.jar  " 
                + "lib/openide-loaders.jar "
                + "lib/org-netbeans-modules-editor-fold.jar "
                + "lib/org-netbeans-modules-editor.jar "
                + "lib/org-netbeans-modules-editor-lib.jar "
                + "lib/org-netbeans-modules-editor-util.jar "
                + "lib/syntax.jar ");
        a.put(Attributes.Name.MAIN_CLASS, 
              "net.sourceforge.squirrel_sql.client.Main");
        m.write(System.out);
    }

}
