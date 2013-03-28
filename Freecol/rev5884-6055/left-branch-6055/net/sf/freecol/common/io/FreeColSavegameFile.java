

package net.sf.freecol.common.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;


public class FreeColSavegameFile extends FreeColDataFile {
    private static final Logger logger = Logger.getLogger(FreeColSavegameFile.class.getName());
    
    public static final String SAVEGAME_FILE = "savegame.xml";
    
    public FreeColSavegameFile(File file) throws IOException {
        super(file);
    }
    
    
    public InputStream getSavegameInputStream() throws IOException {
        return getInputStream(SAVEGAME_FILE);
    }

    
    @Override
    protected String[] getFileEndings() {
        return new String[] {".fsg", ".zip"};
    }
}