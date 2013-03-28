
package net.sf.jabref.export;

import net.sf.jabref.BibtexEntry;
import net.sf.jabref.Globals;



public class SaveException extends Exception
{
    

    public static final SaveException FILE_LOCKED = new SaveException
            (Globals.lang("Could not save, file locked by another JabRef instance."));
    public static final SaveException BACKUP_CREATION = new SaveException
            (Globals.lang("Unable to create backup"));

    private BibtexEntry entry;
    private int status = 0;
    

    public SaveException(String message)
    {
        super(message);
        entry = null;
    }

    public SaveException(String message, int status)
        {
            super(message);
            entry = null;
            this.status = status;
        }


    public SaveException(String message, BibtexEntry entry)
    {
        super(message);
        this.entry = entry;
    }

    

    public int getStatus() {
        return status;
    }

    public BibtexEntry getEntry()
    {
        return entry;
    }

    public boolean specificEntry()
    {
        return (entry != null);
    }
}



