

package net.sf.freecol.client.gui.sound;

import java.io.File;
import java.util.Arrays;


public final class Playlist {
    
    
    public static final int PLAY_ALL = 0,   
                            REPEAT_ALL = 1, 
                            PLAY_ONE = 2,   
                            REPEAT_ONE = 3; 

    
    public static final int FORWARDS = 0,   
                            BACKWARDS = 1,  
                            SHUFFLE = 2;    

    private final File[] soundFiles;
    private int num; 
    private int repeatMode;
    private int pickMode;
    private final int[] playedSounds;



    
    public Playlist(File... soundFiles) {
        if (soundFiles.length == 0) {
            throw new IllegalArgumentException("It's not possible to create an empty Playlist.");
        }

        this.soundFiles = soundFiles;
        repeatMode = REPEAT_ALL;
        pickMode = FORWARDS;
        num = -1;
        playedSounds = null;
    }



    
    public Playlist(File[] soundFiles, int repeatMode, int pickMode) {
        if (soundFiles.length == 0) {
            throw new IllegalArgumentException("It's not possible to create an empty Playlist.");
        }

        this.soundFiles = soundFiles;
        this.repeatMode = repeatMode;
        this.pickMode = pickMode;
        num = -1;

        if (pickMode == SHUFFLE) {
            playedSounds = new int[soundFiles.length];
            for (int i = 0; i < playedSounds.length; i++) {
                playedSounds[i] = Integer.MAX_VALUE;
            }
        }
        else {
            playedSounds = null;
        }
    }



    
    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }



    
    public void setPickMode(int pickMode) {
        this.pickMode = pickMode;
    }



    
    public File next() {
        if (num == -2) {
            
        }
        else if (num == -1) {
            
            if (pickMode == FORWARDS) {
                num = 0;
            }
            else if (pickMode == BACKWARDS) {
                num = soundFiles.length - 1;
            }
            else { 
                num = (int)(Math.random() * soundFiles.length);
                playedSounds[0] = num;
            }
        }
        else if (repeatMode == PLAY_ONE) {
            num = -2;
        }
        else if (repeatMode == REPEAT_ONE) {
            
        }
        else if (pickMode == SHUFFLE) {
            if (playedSounds[playedSounds.length - 1] != Integer.MAX_VALUE) {
                if (repeatMode == PLAY_ALL) {
                    num = -2;
                }
                else { 
                    for (int i = 1; i < playedSounds.length; i++) {
                        playedSounds[i] = Integer.MAX_VALUE;
                    }
                    num = (int)(Math.random() * soundFiles.length);
                    playedSounds[0] = num;
                }
            }
            else {
                int i = 0;
                for (; i < playedSounds.length; i++) {
                    if (playedSounds[i] == Integer.MAX_VALUE) {
                        break;
                    }
                }

                int tmp = (int)(Math.random() * (soundFiles.length - i));
                for (int j = 0; j < i; j++) {
                    if (tmp < playedSounds[j]) {
                        num = tmp;
                        break;
                    }
                    else {
                        tmp++;
                    }
                }

                playedSounds[i] = num;
                Arrays.sort(playedSounds);
            }
        }
        else {
            switch (repeatMode) {
                case PLAY_ALL:
                    if (pickMode == FORWARDS) {
                        num++;
                        if (num == soundFiles.length) {
                            num = -2;
                        }
                    }
                    else { 
                        num--;
                        if (num == -1) {
                            num = -2;
                        }
                    }
                    break;
                case REPEAT_ALL:
                    if (pickMode == FORWARDS) {
                        num++;
                        if (num == soundFiles.length) {
                            num = 0;
                        }
                    }
                    else { 
                        num--;
                        if (num == -1) {
                            num = soundFiles.length - 1;
                        }
                    }
                    break;
            }
        }
        
        if ((num >= 0) && (num < soundFiles.length)) {
            return soundFiles[num];
        }
        else {
            return null;
        }
    }

    

    
    public boolean hasNext() {
        if (repeatMode == PLAY_ALL) {
            if (num == -1) {
                return true;
            }
            else if (num == -2) {
                return false;
            }
            else {
                if (((pickMode == FORWARDS) && (num == soundFiles.length - 1))
                        || ((pickMode == BACKWARDS) && (num == 0))
                        || ((pickMode == SHUFFLE) && (playedSounds[playedSounds.length - 1] != Integer.MAX_VALUE))) {
                    return false;
                }
                else {
                    return true;
                }
            }
        }
        else if (repeatMode == PLAY_ONE) {
            if (num == -1) {
                return true;
            }
            else {
                return false;
            }
        }
        else {
            return true;
        }
    }
}
