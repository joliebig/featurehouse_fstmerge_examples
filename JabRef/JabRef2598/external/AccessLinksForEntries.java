package net.sf.jabref.external; 

import java.awt.BorderLayout; 
import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener; 
import java.io.File; 
import java.io.IOException; 
import java.util.ArrayList; 
import java.util.HashSet; 
import java.util.Iterator; 
import java.util.List; 
import java.util.Set; 

import javax.swing.JDialog; 
import javax.swing.JProgressBar; 
import javax.swing.SwingUtilities; 

import net.sf.jabref.BaseAction; 
import net.sf.jabref.BasePanel; 
import net.sf.jabref.BibtexEntry; 
import net.sf.jabref.GUIGlobals; 
import net.sf.jabref.MetaData; 
import net.sf.jabref.Util; 
import net.sf.jabref.gui.FileListEntry; 
import net.sf.jabref.gui.FileListTableModel; 



public  class  AccessLinksForEntries {
	

    
    public static List<FileListEntry> getExternalLinksForEntries(List<BibtexEntry> entries) {
        List<FileListEntry> files = new ArrayList<FileListEntry>();
        FileListTableModel model = new FileListTableModel();
        for (Iterator<BibtexEntry> iterator = entries.iterator(); iterator.hasNext();) {
            BibtexEntry entry = iterator.next();
            String links = entry.getField(GUIGlobals.FILE_FIELD);
            if (links == null)
                continue;
            model.setContent(links);
            for (int i=0; i<model.getRowCount(); i++)
                files.add(model.getEntry(i));
        }
        return files;
    }


	

    
    public static void copyExternalLinksToDirectory(final List<FileListEntry> files, File toDir,
                                                    MetaData metaData, final JProgressBar prog,
                                                    boolean deleteOriginalFiles,
                                                    final ActionListener callback) {

        if (prog != null) SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                prog.setMaximum(files.size());
                prog.setValue(0);
                prog.setIndeterminate(false);
            }
        });

        Set<String> fileNames = new HashSet<String>();

        int i=0;

        for (Iterator<FileListEntry> iterator = files.iterator(); iterator.hasNext();) {
            FileListEntry entry = iterator.next();
            File file = new File(entry.getLink());

            
            String name = file.getName();
            int pos = name.lastIndexOf('.');
            String extension = ((pos >= 0) && (pos < name.length() - 1)) ? name.substring(pos + 1)
                .trim().toLowerCase() : null;

            
            String dir = metaData.getFileDirectory(extension);
            
            String fileDir = metaData.getFileDirectory(GUIGlobals.FILE_FIELD);

            
            String databaseDir = metaData.getFile().getParent();
            File tmp = Util.expandFilename(entry.getLink(),
                    new String[] { dir, fileDir, databaseDir });
            if (tmp != null)
                file = tmp;

            
            if (file.exists()) {
                if (fileNames.contains(name)) {
                    
                }
                else {
                    fileNames.add(name);
                    File destination = new File(toDir, name);

                    
                    if (!destination.equals(file)) {
                        try {
                            
                            Util.copyFile(file, destination, false);
                            
                            if (deleteOriginalFiles)
                                file.delete();
                            
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    else {
                        
                    }
                    
                    i++;
                    final int j = i;

                    if (prog != null) SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            prog.setValue(j);
                        }
                    });
                }
            }
            else {
                
                
            }
        }

        if (callback != null) {
            callback.actionPerformed(null);
        }
    }


	


    public static  class  SaveWithLinkedFiles  extends BaseAction {
		
        private BasePanel panel;

		

        public SaveWithLinkedFiles(BasePanel panel) {

            this.panel = panel;
        }


		

        @Override
        public void action() throws Throwable {

            ArrayList<BibtexEntry> entries = new ArrayList<BibtexEntry>();
            BibtexEntry[] sel = panel.getSelectedEntries();
            for (int i = 0; i < sel.length; i++) {
                BibtexEntry bibtexEntry = sel[i];
                entries.add(bibtexEntry);
            }
            final List<FileListEntry> links =
                    AccessLinksForEntries.getExternalLinksForEntries(entries);
            for (Iterator<FileListEntry> iterator = links.iterator(); iterator.hasNext();) {
                FileListEntry entry = iterator.next();
                System.out.println("Link: " + entry.getLink());
            }

            final JProgressBar prog = new JProgressBar();
            prog.setIndeterminate(true);
            final JDialog diag = new JDialog(panel.frame(), false);
            diag.getContentPane().add(prog, BorderLayout.CENTER);
            diag.pack();
            diag.setLocationRelativeTo(panel.frame());
            diag.setVisible(true);
            Thread t = new Thread(new Runnable() {
                public void run() {
                    AccessLinksForEntries.copyExternalLinksToDirectory(links,
                            new File("/home/alver/tmp"), panel.metaData(), prog, false,
                            new ActionListener() {
                                public void actionPerformed(ActionEvent actionEvent) {
                                    diag.dispose();
                                }
                            });
                }
            });
            t.start();

            
        }



	}


}
