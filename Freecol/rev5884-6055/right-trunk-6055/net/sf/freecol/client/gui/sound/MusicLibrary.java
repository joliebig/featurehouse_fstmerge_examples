


package net.sf.freecol.client.gui.sound;

import java.io.File;

import net.sf.freecol.common.FreeColException;



public final class MusicLibrary extends SoundLibrary {
    

    
    public static final int SILENCE = 0;
    public static final int INTRO = 1;


    
    public MusicLibrary(String freeColHome) throws FreeColException {
        super(new File((freeColHome.equals("") ? "data" + System.getProperty("file.separator"): freeColHome) + "audio" + System.getProperty("file.separator") + "music"));
    }


}
