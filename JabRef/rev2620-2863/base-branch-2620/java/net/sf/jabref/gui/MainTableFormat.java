package net.sf.jabref.gui;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

import net.sf.jabref.AuthorList;
import net.sf.jabref.BasePanel;
import net.sf.jabref.BibtexEntry;
import net.sf.jabref.BibtexFields;
import net.sf.jabref.GUIGlobals;
import net.sf.jabref.Globals;
import net.sf.jabref.SearchRuleSet;
import net.sf.jabref.Util;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.Matcher;


public class MainTableFormat implements TableFormat<BibtexEntry> {

    public static final String[]
            PDF = {"pdf", "ps"}
    ,
    URL_ = {"url", "doi"}
    ,
    CITESEER = {"citeseerurl"},
    FILE = {GUIGlobals.FILE_FIELD};

    BasePanel panel;

    String[] columns; 
    public int padleft = -1; 
    
    private HashMap<Integer, String[]> iconCols = new HashMap<Integer, String[]>();
    int[] nameCols = null;
    boolean namesAsIs, abbr_names, namesNatbib, namesFf, namesLf, namesLastOnly, showShort;

    public MainTableFormat(BasePanel panel) {
        this.panel = panel;
    }

    public int getColumnCount() {
        return padleft + columns.length;
    }

    public String getColumnName(int col) {
        if (col == 0) {
            return GUIGlobals.NUMBER_COL;
        } else if (getIconTypeForColumn(col) != null) {
            return "";
        }
        else 
        {
          String disName = BibtexFields.getFieldDisplayName(columns[col - padleft]) ;
          if ( disName != null)
          {
            return disName ;
          }
        }
        return Util.nCase(columns[col - padleft]);
    }

    
    public String[] getIconTypeForColumn(int col) {
        Object o = iconCols.get(new Integer(col));
        if (o != null)
            return (String[]) o;
        else
            return null;
    }

    
    public int getColumnIndex(String colName) {
        for (int i=0; i<columns.length; i++) {
            if (columns[i].equalsIgnoreCase(colName))
                return i+padleft;
        }
        return -1;
    }

    public Object getColumnValue(BibtexEntry be, int col) {
        Object o;
        String[] iconType = getIconTypeForColumn(col); 
        if (col == 0) {
            o = "#";
        }

        else if (iconType != null) {
            int hasField = -1;
            for (int i = iconType.length - 1; i >= 0; i--)
                if (hasField(be, iconType[i]))
                    hasField = i;
            if (hasField < 0)
                return null;

            
            if (iconType[hasField].equals(GUIGlobals.FILE_FIELD)) {
                o = FileListTableModel.getFirstLabel(be.getField(GUIGlobals.FILE_FIELD));
            } else
                o = GUIGlobals.getTableIcon(iconType[hasField]);
        } else if (columns[col - padleft].equals(GUIGlobals.TYPE_HEADER)) {
            o = be.getType().getName();
        } else {

            o = be.getField(columns[col - padleft]);
            for (int i = 0; i < nameCols.length; i++) {
                if (col - padleft == nameCols[i]) {
                    if (o == null) {
                        return null;
                    }
                    if (namesAsIs) return o;
                    if (namesNatbib) o = AuthorList.fixAuthor_Natbib((String) o);
                    else if (namesLastOnly) o = AuthorList.fixAuthor_lastNameOnlyCommas((String) o, false);
                    else if (namesFf) o = AuthorList.fixAuthor_firstNameFirstCommas((String) o, abbr_names, false);
                    else if (namesLf) o = AuthorList.fixAuthor_lastNameFirstCommas((String) o, abbr_names, false);

                    return o;
                }
            }


        }

        return o;
    }

    public boolean hasField(BibtexEntry be, String field) {
        
        
        return ((be != null) && (be.getField(field) != null));
    }

    public void updateTableFormat() {

        
        columns = Globals.prefs.getStringArray("columnNames");

        
        showShort = Globals.prefs.getBoolean("showShort");        
        namesNatbib = Globals.prefs.getBoolean("namesNatbib");    
        namesLastOnly = Globals.prefs.getBoolean("namesLastOnly");
        namesAsIs = Globals.prefs.getBoolean("namesAsIs");
        abbr_names = Globals.prefs.getBoolean("abbrAuthorNames"); 
        namesFf = Globals.prefs.getBoolean("namesFf");
        namesLf = !(namesAsIs || namesFf || namesNatbib || namesLastOnly); 

        
        
        iconCols.clear();
        int coln = 1;
        if (Globals.prefs.getBoolean("fileColumn"))
            iconCols.put(new Integer(coln++), FILE);
        if (Globals.prefs.getBoolean("pdfColumn"))
            iconCols.put(new Integer(coln++), PDF);
        if (Globals.prefs.getBoolean("urlColumn"))
            iconCols.put(new Integer(coln++), URL_);
        if (Globals.prefs.getBoolean("citeseerColumn"))
            iconCols.put(new Integer(coln++), CITESEER);

        
        padleft = 1 + iconCols.size();

        
        
        
        Vector<Integer> tmp = new Vector<Integer>(2, 1);
        for (int i = 0; i < columns.length; i++) {
            if (columns[i].equals("author")
                    || columns[i].equals("editor")) {
                tmp.add(new Integer(i));
            }
        }
        nameCols = new int[tmp.size()];
        for (int i = 0; i < nameCols.length; i++) {
            nameCols[i] = tmp.elementAt(i).intValue();
        }
    }

    public boolean isIconColumn(int col) {
        return (getIconTypeForColumn(col) != null);
    }



    static class NoSearchMatcher implements Matcher<BibtexEntry> {
        public boolean matches(BibtexEntry object) {
            return true;
        }
    }

    static class SearchMatcher implements Matcher<BibtexEntry> {
        private SearchRuleSet ruleSet;
        private Hashtable<String, String> searchOptions;

        public SearchMatcher(SearchRuleSet ruleSet, Hashtable<String, String> searchOptions) {
            this.ruleSet = ruleSet;
            this.searchOptions = searchOptions;
        }
        public boolean matches(BibtexEntry entry) {
            int result = ruleSet.applyRule(searchOptions, entry);
            return result > 0;
        }
    }
}
