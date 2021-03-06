package net.sf.jabref.external;

import java.io.IOException;

import javax.swing.Icon;

import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.GUIGlobals;
import net.sf.jabref.Globals;


public class PushToLatexEditor implements PushToApplication {

    private boolean couldNotCall=false;
    private boolean notDefined=false;

    public String getName() {
        return Globals.menuTitle("Insert selected citations into LatexEditor");
    }

    public String getApplicationName() {
        return "LatexEditor";
    }

    public String getTooltip() {
        return Globals.lang("Push to LatexEditor");
    }

    public Icon getIcon() {
        return GUIGlobals.getImage("edit");
    }

    public String getKeyStrokeName() {
        return null;
    }

    public void pushEntries(BibtexEntry[] entries, String keyString) {

        couldNotCall = false;
        notDefined = false;

        String led = Globals.prefs.get("latexEditorPath");

        if ((led == null) || (led.trim().length() == 0)) {
            notDefined = true;
            return;
        }

        try {
            StringBuffer toSend = new StringBuffer("-i \\")
                    .append(Globals.prefs.get("citeCommand")).append("{")
                    .append(keyString)
                    .append("}");
            Runtime.getRuntime().exec(led + " " + toSend.toString());

        }

        catch (IOException excep) {
            couldNotCall = true;
            excep.printStackTrace();
        }
    }

    public void operationCompleted(BasePanel panel) {
        if (notDefined) {
            panel.output(Globals.lang("Error") + ": "+
                    Globals.lang("Path to %0 not defined", getApplicationName())+".");
        }
        else if (couldNotCall) {
            panel.output(Globals.lang("Error") + ": " + Globals.lang("Could not call executable") + " '"
                    +Globals.prefs.get("latexEditorPath") + "'.");
        }
        else
            Globals.lang("Pushed citations to WinEdt");
    }

    public boolean requiresBibtexKeys() {
        return false;  
    }
}
