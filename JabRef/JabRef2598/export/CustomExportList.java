package net.sf.jabref.export; 

import java.util.TreeSet; 
import java.util.Comparator; 
import java.util.TreeMap; 

import net.sf.jabref.Globals; 
import net.sf.jabref.JabRefPreferences; 
import ca.odell.glazedlists.EventList; 
import ca.odell.glazedlists.SortedList; 
import ca.odell.glazedlists.BasicEventList; 



public  class  CustomExportList  extends TreeSet<String[]> {
	
    private TreeMap<String, ExportFormat> formats = new TreeMap<String, ExportFormat>();

	
	private Object[] array;

	

    <<<<<<< C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var1_43783
public CustomExportList(Comparator<String[]> comp) {
	    list = new BasicEventList<String[]>();
        sorted = new SortedList<String[]>(list, comp);
    }
=======
public CustomExportList(JabRefPreferences prefs_, Comparator<String[]> comp) {
		super(comp);
		
		
	}
>>>>>>> C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var2_43785


	

	public TreeMap<String, ExportFormat> getCustomExportFormats() {
<<<<<<< C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var1_43786
        formats.clear();
        readPrefs();
        return formats;
=======
        formats.clear();
        readPrefs();
        sort();
        return formats;
>>>>>>> C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var2_43788
	}


	

	private void readPrefs() {
<<<<<<< C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var1_43789
        formats.clear();
        list.clear();
        int i = 0;
		String[] s;
		while ((s = Globals.prefs.getStringArray("customExportFormat" + i)) != null) {
            ExportFormat format = createFormat(s);
=======
        formats.clear();
        int i = 0;
		String[] s;
		while ((s = Globals.prefs.getStringArray("customExportFormat" + i)) != null) {
            ExportFormat format = createFormat(s);
>>>>>>> C:\Dokumente und Einstellungen\Sven Apel\Eigene Dateien\Uni\fstmerge\fstmerge_tmp\fstmerge_var2_43791
			formats.put(format.getConsoleName(), format);
			list.add(s);
			i++;
		}
	}


	

    private ExportFormat createFormat(String[] s) {
		String lfFileName;
		if (s[1].endsWith(".layout"))
			lfFileName = s[1].substring(0, s[1].length() - 7);
		else
			lfFileName = s[1];
		ExportFormat format = new ExportFormat(s[0], s[0], lfFileName, null,
			s[2]);
		format.setCustomExport(true);
		return format;
	}


	

	public String[] getElementAt(int pos) {
		return (String[]) (array[pos]);
	}


	

	public void addFormat(String[] s) {
		list.add(s);
		ExportFormat format = createFormat(s);
		formats.put(format.getConsoleName(), format);
	}


	

	


	

	


	

	public void store() {

		if (list.size() == 0)
			purge(0);
		else {
			for (int i = 0; i < list.size(); i++) {
				
				Globals.prefs.putStringArray("customExportFormat" + i,
					list.get(i));
			}
			purge(list.size());
		}
	}


	

	private void purge(int from) {
		int i = from;
		while (Globals.prefs.getStringArray("customExportFormat" + i) != null) {
			Globals.prefs.remove("customExportFormat" + i);
			i++;
		}
	}


	

    private EventList<String[]> list;

	
    private SortedList<String[]> sorted;

	

    public int size() {
        return list.size();
    }

	

    public EventList<String[]> getSortedList() {
        return sorted;
    }

	

	public void remove(String[] toRemove) {

        ExportFormat format = createFormat(toRemove);
        formats.remove(format.getConsoleName());
        list.remove(toRemove);
        
	}


}
