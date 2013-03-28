


package net.sf.freecol.tools;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import net.sf.freecol.common.io.FreeColSavegameFile;

import org.xml.sax.SAXParseException;


public class SaveGameValidator {

    private static FileFilter fsgFilter = new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".fsg");
            }
        };
    
    public static void main(String[] args) throws Exception {

        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        File schemaLocation = new File("schema/data/data-savedGame.xsd");
        Schema schema = factory.newSchema(schemaLocation);
        Validator saveGameValidator = schema.newValidator();

        List<File> allFiles = new ArrayList<File>();
        for (String name : args) {
            File file = new File(name);
            if (file.exists()) {
                if (file.isDirectory()) {
                    for (File fsg : file.listFiles(fsgFilter)) {
                        allFiles.add(fsg);
                    }
                } else if (fsgFilter.accept(file)) {
                    allFiles.add(file);
                }
            }
        }

        for (File file : allFiles) {
            System.out.println("Processing file " + file.getPath());
            try {
                FreeColSavegameFile mapFile = new FreeColSavegameFile(file);
                saveGameValidator.validate(new StreamSource(mapFile.getSavegameInputStream()));
                System.out.println("Successfully validated " + file.getName());
            } catch(SAXParseException e) {
                System.out.println(e.getMessage() 
                                   + " at line=" + e.getLineNumber() 
                                   + " column=" + e.getColumnNumber());
            }
        }
    }

}

