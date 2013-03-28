

package net.sf.freecol.client.gui.sound;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import net.sf.freecol.common.FreeColException;


public class SoundLibrary {


    
    public static enum SoundEffect { ERROR,
            ATTACK,
            ILLEGAL_MOVE,
            LOAD_CARGO,
            SELL_CARGO,
            ATTACK_ARTILLERY,
            SUNK,
            ATTACK_DRAGOON,
            ATTACK_NAVAL,
            CAPTURED_BY_ARTILLERY,
            BUILDING_COMPLETE,
            MISSION_ESTABLISHED}

    
    
    private Map<String, Playlist> playlists = new HashMap<String, Playlist>();

    private static final FileFilter soundFileFilter =
        new FileFilter() {
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return false;
                } else {
                    String s = file.getName();
                    return (s.endsWith(".au") || s.endsWith(".wav") || 
                            s.endsWith(".aif") || s.endsWith(".aiff") ||
                            s.endsWith(".ogg") || s.endsWith(".mp3"));
                }
            }
        };
                

    
    public SoundLibrary(File dir) throws FreeColException {
        if (!dir.isDirectory()) {
            throw new FreeColException("The file \"" + dir.getName() + "\" is not a directory.");
        }

        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".svn")) {
                continue;
            } else if (file.isDirectory()) {
                File[] files = file.listFiles(soundFileFilter);
                Arrays.sort(files);
                playlists.put(file.getName(), new Playlist(files));
            } else {
                String fileName = file.getName();
                fileName = fileName.substring(0, fileName.lastIndexOf('.'));
                playlists.put(fileName, new Playlist(file));
            }

        }
    }

    
    public Playlist get(String string) {
        return playlists.get(string);
    }


    
    public Playlist get(SoundEffect effect) {
        return playlists.get(effect.toString().toLowerCase());
    }

}
