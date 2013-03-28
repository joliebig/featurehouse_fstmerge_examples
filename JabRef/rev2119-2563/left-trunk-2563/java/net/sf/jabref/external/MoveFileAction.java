package net.sf.jabref.external;

import net.sf.jabref.*;
import net.sf.jabref.gui.FileListEditor;
import net.sf.jabref.gui.FileListEntry;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;


public class MoveFileAction extends AbstractAction {
    private JabRefFrame frame;
    private EntryEditor eEditor;
    private FileListEditor editor;

    public MoveFileAction(JabRefFrame frame, EntryEditor eEditor, FileListEditor editor) {
        this.frame = frame;
        this.eEditor = eEditor;
        this.editor = editor;
    }

    public void actionPerformed(ActionEvent event) {
        int selected = editor.getSelectedRow();
        if (selected == -1)
            return;
        FileListEntry flEntry = editor.getTableModel().getEntry(selected);
        
        String ln = flEntry.getLink();
        boolean httpLink = ln.toLowerCase().startsWith("http");
        if (httpLink) {
            

        }

        
        String dir = frame.basePanel().metaData().getFileDirectory(GUIGlobals.FILE_FIELD);
        File file = Util.expandFilename(ln, new String[]{dir});
        if ((file != null) && file.exists()) {
            
            String extension = null;
            if (flEntry.getType() != null)
                extension = "." + flEntry.getType().getExtension();

            File newFile = null;
            boolean repeat = true;
            while (repeat) {
                repeat = false;
                String chosenFile = Globals.getNewFile(frame, file, extension, JFileChooser.SAVE_DIALOG, false);
                if (chosenFile == null) {
                    return; 
                }
                newFile = new File(chosenFile);
                
                if (newFile.exists() && (JOptionPane.showConfirmDialog
                        (frame, "'" + newFile.getName() + "' " + Globals.lang("exists. Overwrite file?"),
                                Globals.lang("Move/Rename file"), JOptionPane.OK_CANCEL_OPTION)
                        != JOptionPane.OK_OPTION)) {
                    repeat = true;
                }
            }

            if (!newFile.equals(file)) {
                try {
                    boolean success = file.renameTo(newFile);
                    if (!success) {
                        success = Util.copyFile(file, newFile, true);
                    }
                    if (success) {
                        
                        flEntry.setLink(newFile.getPath());
                        eEditor.updateField(editor);
                        JOptionPane.showMessageDialog(frame, Globals.lang("File moved"),
                                Globals.lang("Move/Rename file"), JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, Globals.lang("Move file failed"),
                                Globals.lang("Move/Rename file"), JOptionPane.ERROR_MESSAGE);
                    }

                } catch (SecurityException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, Globals.lang("Could not move file") + ": " + ex.getMessage(),
                            Globals.lang("Move/Rename file"), JOptionPane.ERROR_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, Globals.lang("Could not move file") + ": " + ex.getMessage(),
                            Globals.lang("Move/Rename file"), JOptionPane.ERROR_MESSAGE);
                }

            }
        }
        else {

            
            JOptionPane.showMessageDialog(frame, Globals.lang("Could not find file '%0'.", flEntry.getLink()),
                    Globals.lang("File not found"), JOptionPane.ERROR_MESSAGE);
            
        }

    }
}