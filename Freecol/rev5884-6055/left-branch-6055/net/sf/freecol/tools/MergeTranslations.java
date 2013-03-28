


package net.sf.freecol.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class MergeTranslations {
    
    public static void main(String[] args) throws Exception {

        File sourceDirectory = new File(args[0]);
        if (!sourceDirectory.isDirectory()) {
            System.exit(1);
        }

        File targetDirectory = new File(args[1]);
        if (!targetDirectory.isDirectory()) {
            System.exit(1);
        }

        final String localeKey = args.length > 2 ? args[2] : "";
        String[] sourceFiles = sourceDirectory.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.matches("FreeColMessages_" + localeKey + ".*\\.properties");
                }
            });
        String[] targetFiles = targetDirectory.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.matches("FreeColMessages_" + localeKey + ".*\\.properties");
                }
            });

        for (String name : sourceFiles) {

            System.out.println("Processing source file: " + name);

            File sourceFile = new File(sourceDirectory, name);
            Map<String, String> sourceProperties = readFile(sourceFile);

            File targetFile = new File(targetDirectory, name);

            if (targetFile.exists()) {

                Map<String, String> targetProperties = readFile(targetFile);

                List<Entry> missingProperties = new ArrayList<Entry>();
                for (Entry entry : sourceProperties.entrySet()) {
                    if (!targetProperties.containsKey(entry.getKey())) {
                        missingProperties.add(entry);
                    }
                }

                if (!missingProperties.isEmpty()) {
                    FileWriter out = new FileWriter(targetFile, true);
                    out.write("### Merged from trunk on "
                              + DateFormat.getDateTimeInstance().format(new Date())
                              + " ###\n");
                    for (Entry entry : missingProperties) {
                        out.write((String) entry.getKey());
                        out.write("=");
                        out.write((String) entry.getValue());
                        out.write("\n");
                    }
                    out.close();
                }
            } else {
                System.out.println("Copying " + name + " from trunk.");
                FileReader in = new FileReader(sourceFile);
                FileWriter out = new FileWriter(targetFile);

                int c;
                
                while ((c = in.read()) != -1) {
                    out.write(c);
                }

                in.close();
                out.close();

            }
        }
    }

    private static Map<String, String> readFile(File file) {
        Map<String, String> result = new HashMap<String, String>();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader); 
            String line = bufferedReader.readLine();
            while (line != null) {
                int index = line.indexOf('=');
                if (index >= 0) {
                    result.put(line.substring(0, index), line.substring(index + 1));
                }
                line = bufferedReader.readLine();
            }
        } catch(Exception e) {
            
        }
        return result;
    }

}

