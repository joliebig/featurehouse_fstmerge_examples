package net.sf.jabref.external;

import net.sf.jabref.*;
import net.sf.jabref.gui.*;
import net.sf.jabref.undo.NamedCompound;
import net.sf.jabref.undo.UndoableFieldChange;

import javax.swing.*;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.File;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.builder.ButtonBarBuilder;


public class SynchronizeFileField extends AbstractWorker {

    private String fieldName = GUIGlobals.FILE_FIELD;
    private BasePanel panel;
    private BibtexEntry[] sel = null;
    private SynchronizeFileField.OptionsDialog optDiag = null;

    Object[] brokenLinkOptions =
            {Globals.lang("Ignore"), Globals.lang("Assign new file"), Globals.lang("Clear field"),
                    Globals.lang("Quit synchronization")};

    private boolean goOn = true, autoSet = true, overWriteAllowed = true, checkExisting = true;

    private int skipped = 0, brokenLinks = 0, entriesChangedCount = 0;

    public SynchronizeFileField(BasePanel panel) {
        this.panel = panel;
    }

    public void init() {
        goOn = true;
        Collection col = panel.database().getEntries();
        sel = new BibtexEntry[col.size()];
        sel = (BibtexEntry[]) col.toArray(sel);

        
        if (optDiag == null)
            optDiag = new SynchronizeFileField.OptionsDialog(panel.frame(), panel.metaData(), fieldName);
        Util.placeDialog(optDiag, panel.frame());
        optDiag.setVisible(true);
        if (optDiag.canceled()) {
            goOn = false;
            return;
        }
        autoSet = !optDiag.autoSetNone.isSelected();
        overWriteAllowed = optDiag.autoSetAll.isSelected();
        checkExisting = optDiag.checkLinks.isSelected();
        
        panel.output(Globals.lang("Synchronizing %0 links...", fieldName.toUpperCase()));
    }

    public void run() {
        if (!goOn) {
            panel.output(Globals.lang("No entries selected."));
            return;
        }
        panel.frame().setProgressBarValue(0);
        panel.frame().setProgressBarVisible(true);
        int weightAutoSet = 10; 
        int progressBarMax = (autoSet ? weightAutoSet * sel.length : 0)
                + (checkExisting ? sel.length : 0);
        panel.frame().setProgressBarMaximum(progressBarMax);
        int progress = 0;
        skipped = 0;
        brokenLinks = 0;
        final NamedCompound ce = new NamedCompound(Globals.lang("Autoset %0 field", fieldName));

        

        
        

        
        String dir = panel.metaData().getFileDirectory(GUIGlobals.FILE_FIELD);
        Set<BibtexEntry> changedEntries = new HashSet<BibtexEntry>();

        
        if (autoSet) {
            Collection<BibtexEntry> entries = new ArrayList<BibtexEntry>();
            for (int i = 0; i < sel.length; i++) {
                entries.add(sel[i]);
            }

            
            ArrayList<File> dirs = new ArrayList<File>();
            String dr = panel.metaData().getFileDirectory(GUIGlobals.FILE_FIELD);
            if (dr != null)
                dirs.add(new File(dr));

            
            Thread t = FileListEditor.autoSetLinks(entries, ce, changedEntries, dirs);
            
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            

        }
        progress += sel.length*weightAutoSet;
        panel.frame().setProgressBarValue(progress);
        
        
        if (checkExisting) {
            mainLoop:
            for (int i = 0; i < sel.length; i++) {
                panel.frame().setProgressBarValue(progress++);
                final Object old = sel[i].getField(fieldName);
                
                if ((old != null) && !old.equals("")) {
                    FileListTableModel tableModel = new FileListTableModel();
                    tableModel.setContent((String)old);
                    for (int j=0; j<tableModel.getRowCount(); j++) {
                        FileListEntry flEntry = tableModel.getEntry(j);
                        
                        boolean httpLink = flEntry.getLink().toLowerCase().startsWith("http");
                        if (httpLink)
                            continue; 
                        
                        
                        
                        File file = Util.expandFilename(flEntry.getLink(), new String[]{dir, "."});
                        if ((file == null) || !file.exists()) {
                            int answer = JOptionPane.showOptionDialog(panel.frame(),
                                Globals.lang("<HTML>Could not find file '%0'<BR>linked from entry '%1'</HTML>",
                                        new String[]{flEntry.getLink(), sel[i].getCiteKey()}),
                                Globals.lang("Broken link"),
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, brokenLinkOptions, brokenLinkOptions[0]);
                            switch (answer) {
                                case 1:
                                    
                                    FileListEntryEditor flEditor = new FileListEntryEditor
                                            (panel.frame(), flEntry, false, panel.metaData());
                                    flEditor.setVisible(true);
                                    break;
                                case 2:
                                    
                                    tableModel.removeEntry(j);
                                    j--; 
                                    break;
                                case 3:
                                    
                                    break mainLoop;
                            }
                            brokenLinks++;
                        }

                    }
                    if (!tableModel.getStringRepresentation().equals(old)) {
                        
                        String toSet = tableModel.getStringRepresentation();
                        if (toSet.length() == 0)
                            toSet = null;
                        ce.addEdit(new UndoableFieldChange(sel[i], fieldName, old,
                                toSet));
                        sel[i].setField(fieldName, toSet);
                        changedEntries.add(sel[i]);
                        
                    }


                }
            }
        }

        entriesChangedCount = changedEntries.size();
	
	
        if (entriesChangedCount > 0) {
            
            ce.end();
            panel.undoManager.addEdit(ce);
            
        }
    }


    public void update() {
        if (!goOn)
            return;

        panel.output(Globals.lang("Finished synchronizing %0 links. Entries changed%c %1.",
                new String[]{fieldName.toUpperCase(), String.valueOf(entriesChangedCount)}));
        panel.frame().setProgressBarVisible(false);
        if (entriesChangedCount > 0) {
            panel.markBaseChanged();
        }
    }

    static class OptionsDialog extends JDialog {
        JRadioButton autoSetUnset, autoSetAll, autoSetNone;
        JCheckBox checkLinks;
        JButton ok = new JButton(Globals.lang("Ok")),
                cancel = new JButton(Globals.lang("Cancel"));
        JLabel description;
        private boolean canceled = true;
        private String fieldName;
        private MetaData metaData;

        public OptionsDialog(JFrame parent, MetaData metaData, String fieldName) {
            super(parent, Globals.lang("Synchronize %0 links", fieldName.toUpperCase()), true);
            this.metaData = metaData;
            final String fn = Globals.lang("file");
            this.fieldName = fieldName;
            ok.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    canceled = false;
                    dispose();
                }
            });

            Action closeAction = new AbstractAction() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            };


            cancel.addActionListener(closeAction);

            InputMap im = cancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
            ActionMap am = cancel.getActionMap();
            im.put(Globals.prefs.getKey("Close dialog"), "close");
            am.put("close", closeAction);

            autoSetUnset = new JRadioButton(Globals.lang("Autoset %0 links. Do not overwrite existing links.", fn), true);
            autoSetAll = new JRadioButton(Globals.lang("Autoset %0 links. Allow overwriting existing links.", fn), false);
            autoSetNone = new JRadioButton(Globals.lang("Do not autoset"), false);
            checkLinks = new JCheckBox(Globals.lang("Check existing %0 links", fn), true);
            ButtonGroup bg = new ButtonGroup();
            bg.add(autoSetUnset);
            bg.add(autoSetNone);
            bg.add(autoSetAll);
            FormLayout layout = new FormLayout("fill:pref", "");
            DefaultFormBuilder builder = new DefaultFormBuilder(layout);
            description = new JLabel("<HTML>" +
                    Globals.lang(
                            "Attempt to autoset %0 links for your entries. Autoset works if "
                                    + "a %0 file in your %0 directory or a subdirectory<BR>is named identically to an entry's BibTeX key, plus extension.", fn)
                    + "</HTML>");
            
            builder.appendSeparator(Globals.lang("Autoset"));
            builder.append(description);
            builder.nextLine();
            builder.append(autoSetUnset);
            builder.nextLine();
            builder.append(autoSetAll);
            builder.nextLine();
            builder.append(autoSetNone);
            builder.nextLine();
            builder.appendSeparator(Globals.lang("Check links"));

            description = new JLabel("<HTML>" +
                    Globals.lang("This makes JabRef look up each %0 extension and check if the file exists. If not, you will "
                            + "be given options<BR>to resolve the problem.", fn)
                    + "</HTML>");
            builder.append(description);
            builder.nextLine();
            builder.append(checkLinks);
            builder.nextLine();
            builder.appendSeparator();


            JPanel main = builder.getPanel();
            main.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            ButtonBarBuilder bb = new ButtonBarBuilder();
            bb.addGlue();
            bb.addGridded(ok);
            bb.addGridded(cancel);
            bb.addGlue();
            getContentPane().add(main, BorderLayout.CENTER);
            getContentPane().add(bb.getPanel(), BorderLayout.SOUTH);

            pack();
        }

        public void setVisible(boolean visible) {
            if (visible)
                canceled = true;

            String dir = metaData.getFileDirectory(GUIGlobals.FILE_FIELD);
            if ((dir == null) || (dir.trim().length() == 0)) {

                autoSetNone.setSelected(true);
                autoSetNone.setEnabled(false);
                autoSetAll.setEnabled(false);
                autoSetUnset.setEnabled(false);
            } else {
                autoSetNone.setEnabled(true);
                autoSetAll.setEnabled(true);
                autoSetUnset.setEnabled(true);
            }

            new FocusRequester(ok);
            super.setVisible(visible);

        }

        public boolean canceled() {
            return canceled;
        }
    }
}
