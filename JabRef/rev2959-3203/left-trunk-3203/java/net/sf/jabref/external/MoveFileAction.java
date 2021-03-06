package net.sf.jabref.external;

import net.sf.jabref.*;
import net.sf.jabref.gui.FileListEditor;
import net.sf.jabref.gui.FileListEntry;
import net.sf.jabref.gui.FileDialogs;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;


public class MoveFileAction extends AbstractAction {
    private JabRefFrame frame;
    private EntryEditor eEditor;
    private FileListEditor editor;
    private boolean toFileDir;

    public MoveFileAction(JabRefFrame frame, EntryEditor eEditor, FileListEditor editor,
                          boolean toFileDir) {
        this.frame = frame;
        this.eEditor = eEditor;
        this.editor = editor;
        this.toFileDir = toFileDir;
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
        if ((dir == null) || (dir.trim().length() == 0) || !(new File(dir)).exists()) {
            JOptionPane.showMessageDialog(frame, Globals.lang("File_directory_is_not_set_or_does_not_exist!"),
                    Globals.lang("Move/Rename file"), JOptionPane.ERROR_MESSAGE);
            return;
        }
        File file = new File(ln);
        if (!file.isAbsolute()) {
            file = Util.expandFilename(ln, new String[]{dir});
        }
        if ((file != null) && file.exists()) {
            
            String extension = null;
            if (flEntry.getType() != null)
                extension = "." + flEntry.getType().getExtension();

            File newFile = null;
            boolean repeat = true;
            while (repeat) {
                repeat = false;
                String chosenFile;
                if (toFileDir) {
                    String suggName = eEditor.getEntry().getCiteKey()+extension;
                    CheckBoxMessage cbm = new CheckBoxMessage(Globals.lang("Move file to file directory?"),
                            Globals.lang("Rename to '%0'",suggName),
                            Globals.prefs.getBoolean("renameOnMoveFileToFileDir"));
                    int answer;
                    
                    if (!suggName.equals(file.getName()))
                        answer = JOptionPane.showConfirmDialog(frame, cbm, Globals.lang("Move/Rename file"),
                                JOptionPane.YES_NO_OPTION);
                    else
                        answer = JOptionPane.showConfirmDialog(frame, Globals.lang("Move file to file directory?"),
                                Globals.lang("Move/Rename file"), JOptionPane.YES_NO_OPTION);
                    if (answer != JOptionPane.YES_OPTION)
                        return;
                    Globals.prefs.putBoolean("renameOnMoveFileToFileDir", cbm.isSelected());
                    StringBuilder sb = new StringBuilder(dir);
                    if (!dir.endsWith(File.separator))
                        sb.append(File.separator);
                    if (cbm.isSelected()) {
                        
                        sb.append(suggName);
                    }
                    else {
                        
                        sb.append(file.getName());
                    }
                    chosenFile = sb.toString();
                } else {
                    chosenFile = FileDialogs.getNewFile(frame, file, extension, JFileChooser.SAVE_DIALOG, false);
                }
                if (chosenFile == null) {
                    return; 
                }
                newFile = new File(chosenFile);
                
                if (newFile.exists() && (JOptionPane.showConfirmDialog
                        (frame, "'" + newFile.getName() + "' " + Globals.lang("exists. Overwrite file?"),
                                Globals.lang("Move/Rename file"), JOptionPane.OK_CANCEL_OPTION)
                        != JOptionPane.OK_OPTION)) {
                    if (!toFileDir)
                        repeat = true;
                    else
                        return;
                }
            }

            if (!newFile.equals(file)) {
                try {
                    boolean success = file.renameTo(newFile);
                    if (!success) {
                        success = Util.copyFile(file, newFile, true);
                    }
                    if (success) {
                        
                        file.delete();
                        
                        String canPath = (new File(dir)).getCanonicalPath();
                        if (newFile.getCanonicalPath().startsWith(canPath)) {
                            if ((newFile.getPath().length() > canPath.length()) &&
                                    (newFile.getPath().charAt(canPath.length()) == File.separatorChar))
                                flEntry.setLink(newFile.getPath().substring(1+canPath.length()));
                            else
                                flEntry.setLink(newFile.getPath().substring(canPath.length()));


                        }
                        else
                            flEntry.setLink(newFile.getCanonicalPath());
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