package net.sf.jabref.imports;

import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JOptionPane;

import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntryType;
import net.sf.jabref.Globals;


public class CheckForNewEntryTypesAction implements PostOpenAction {

    public boolean isActionNecessary(ParserResult pr) {
        
        for (Iterator<String> i = pr.getEntryTypes().keySet().iterator(); i.hasNext();) {
            String typeName = (i.next()).toLowerCase();
            if (BibtexEntryType.ALL_TYPES.get(typeName) != null)
                i.remove();
        }
        return pr.getEntryTypes().size() > 0;
    }

    public void performAction(BasePanel panel, ParserResult pr) {

        StringBuffer sb = new StringBuffer(Globals.lang("Custom entry types found in file") + ": ");
        Object[] types = pr.getEntryTypes().keySet().toArray();
        Arrays.sort(types);
        for (int i = 0; i < types.length; i++) {
            sb.append(types[i].toString()).append(", ");
        }
        String s = sb.toString();
        int answer = JOptionPane.showConfirmDialog(panel.frame(),
                s.substring(0, s.length() - 2) + ".\n"
                        + Globals.lang("Remember these entry types?"),
                Globals.lang("Custom entry types"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        
        if (answer == JOptionPane.YES_OPTION) {
            
            for (BibtexEntryType typ : pr.getEntryTypes().values()){
                BibtexEntryType.ALL_TYPES.put(typ.getName().toLowerCase(), typ);
            }

        }
    }

}
