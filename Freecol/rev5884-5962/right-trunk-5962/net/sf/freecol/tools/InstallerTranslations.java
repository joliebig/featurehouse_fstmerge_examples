


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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class InstallerTranslations {

    private static final File SOURCE_DIRECTORY =
        new File("src/net/sf/freecol/client/gui/i18n/");
    private static final File MAIN_FILE =
        new File(SOURCE_DIRECTORY, "FreeColMessages.properties");
    private static final File DESTINATION_DIRECTORY =
        new File("build/installer");
    private static final File LANGUAGE_CODES =
        new File(DESTINATION_DIRECTORY, "iso-639-2.txt");

    private static final String[] KEYS = {
        "FreeCol",
        "FreeCol.description",
        "GameManual",
        "GameManual.description",
        "SourceCode",
        "SourceCode.description",
        "Music",
        "Music.description",
        "SoundEffects",
        "SoundEffects.description",
        "MovieClips",
        "MovieClips.description",
        "MovieClips.description2",
        "Location.Web",
        "FreeColLanguage",
        "FreeColLanguage.autodetect",
        "FreeColLanguage.description",
        "UserFiles",
        "UserFiles.home",
        "UserFiles.freecol",
        "UserFiles.other"
    };

    
    public static void main(String[] args) throws Exception {

        if (!LANGUAGE_CODES.exists()) {
            System.out.println("Language codes not found.");
            System.exit(1);
        }

        if (!MAIN_FILE.exists()) {
            System.out.println("Main input file not found.");
            System.exit(1);
        }

        Map<String, String> languageMappings = readLanguageMappings(LANGUAGE_CODES);
        Map<String, String> mainProperties = readFile(MAIN_FILE);
        Set<String> languages = new HashSet<String>();

        String[] sourceFiles = SOURCE_DIRECTORY.list(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.matches("FreeColMessages_.*\\.properties");
                }
            });

        for (String name : sourceFiles) {

            int index = name.indexOf('_', 16);
            if (index < 0) {
                index = name.indexOf('.', 16);
            }
            String languageCode = name.substring(16, index);
            if (languageCode.length() == 2) {
                languageCode = languageMappings.get(languageCode);
            }
            if (languages.contains(languageCode)) {
                System.out.println("Skipping source file: " + name);
                continue;
            }

            System.out.println("Processing source file: " + name);
            languages.add(languageCode);

            File sourceFile = new File(SOURCE_DIRECTORY, name);
            Map<String, String> sourceProperties = readFile(sourceFile);
            StringBuilder output = new StringBuilder();
            output.append("<?xml version = '1.0' encoding = 'UTF-8' standalone = 'yes'?>\n");
            output.append("<!-- ATTENTION: Do not modify this file directly,\n");
            output.append("     modify the source file\n         ");
            output.append(sourceFile.getPath());
            output.append("\n     instead. -->\n");
            output.append("<langpack>\n");

            for (String key : KEYS) {
                String longKey = "installer." + key;
                String value = sourceProperties.get(longKey);
                if (value == null) {
                    value = mainProperties.get(longKey);
                }
                output.append("    <str id=\"");
                output.append(key);
                output.append("\" txt=\"");
                output.append(value);
                output.append("\" />\n");
            }
            output.append("</langpack>\n");
            File destinationFile = new File(DESTINATION_DIRECTORY, "lang.xml_" + languageCode);
            FileWriter out = new FileWriter(destinationFile);
            out.write(output.toString());
            out.close();
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

    private static Map<String, String> readLanguageMappings(File file) {
        Map<String, String> result = new HashMap<String, String>();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader); 
            String line = bufferedReader.readLine();
            String[] fields;
            while (line != null) {
                fields = line.split(":");
                if (fields[1].length() > 0) {
                    result.put(fields[1], fields[0].substring(0, 3));
                }
                line = bufferedReader.readLine();
            }
        } catch(Exception e) {
            
        }
        return result;
    }

}

