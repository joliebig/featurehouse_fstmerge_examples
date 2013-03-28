


package net.sf.freecol.client.gui.sound;

import java.io.File;

import net.sf.freecol.common.FreeColException;


public final class SfxLibrary extends SoundLibrary {

    
    public SfxLibrary(String freeColHome) throws FreeColException {
        super(new File((freeColHome.equals("") ?
                        "data" + System.getProperty("file.separator"): 
                        freeColHome) + "audio" + System.getProperty("file.separator") + "sfx"));
    }


}
