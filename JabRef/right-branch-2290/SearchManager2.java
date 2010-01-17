
package net.sf.jabref;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Collection;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import net.sf.jabref.search.*;
import net.sf.jabref.search.SearchExpression;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.EventList;

class SearchManager2 extends SidePaneComponent
    implements ActionListener, KeyListener, ItemListener, CaretListener, ErrorMessageDisplay {

    private JabRefFrame frame;

    GridBagLayout gbl = new GridBagLayout() ;
    GridBagConstraints con = new GridBagConstraints() ;

    IncrementalSearcher incSearcher;

    
    private JTextField searchField = new JTextField("", 12);
    private JLabel lab = 
    new JLabel(GUIGlobals.getImage("search"));
    private JPopupMenu settings = new JPopupMenu();
    private JButton openset = new JButton(Globals.lang("Settings"));
    private JButton escape = new JButton(Globals.lang("Clear"));
    private JButton help = new JButton(GUIGlobals.getImage("help"));
    
    private JButton search = new JButton();
    private JCheckBoxMenuItem searchReq, searchOpt, searchGen,
    searchAll, caseSensitive, regExpSearch;

    private JRadioButton increment, floatSearch, hideSearch;
    private JCheckBoxMenuItem select;
    private ButtonGroup types = new ButtonGroup();
    private boolean incSearch = false, startedFloatSearch=false, startedFilterSearch=false;

    private int incSearchPos = -1; 
                   
                   


    public SearchManager2(JabRefFrame frame, SidePaneManager manager) {
    super(manager, GUIGlobals.getIconUrl("search"), Globals.lang("Search"));

        this.frame = frame;
    incSearcher = new IncrementalSearcher(Globals.prefs);



    

        searchReq = new JCheckBoxMenuItem
        (Globals.lang("Search required fields"),
         Globals.prefs.getBoolean("searchReq"));
    searchOpt = new JCheckBoxMenuItem
        (Globals.lang("Search optional fields"),
         Globals.prefs.getBoolean("searchOpt"));
    searchGen = new JCheckBoxMenuItem
        (Globals.lang("Search general fields"),
         Globals.prefs.getBoolean("searchGen"));
        searchAll = new JCheckBoxMenuItem
        (Globals.lang("Search all fields"),
         Globals.prefs.getBoolean("searchAll"));
        regExpSearch = new JCheckBoxMenuItem
        (Globals.lang("Use regular expressions"),
         Globals.prefs.getBoolean("regExpSearch"));


    increment = new JRadioButton(Globals.lang("Incremental"), false);
    floatSearch = new JRadioButton(Globals.lang("Float"), true);
    hideSearch = new JRadioButton(Globals.lang("Filter"), true);
    types.add(increment);
    types.add(floatSearch);
        types.add(hideSearch);

        select = new JCheckBoxMenuItem(Globals.lang("Select matches"), false);
        increment.setToolTipText(Globals.lang("Incremental search"));
        floatSearch.setToolTipText(Globals.lang("Gray out non-matching entries"));
        hideSearch.setToolTipText(Globals.lang("Hide non-matching entries"));

    
    
    increment.addItemListener(this);
        floatSearch.addItemListener(this);
        hideSearch.addItemListener(this);

        
        
        searchField.addFocusListener(Globals.focusListener);


    if (searchAll.isSelected()) {
        searchReq.setEnabled(false);
        searchOpt.setEnabled(false);
        searchGen.setEnabled(false);
    }
    searchAll.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent event) {
            boolean state = !searchAll.isSelected();
            searchReq.setEnabled(state);
            searchOpt.setEnabled(state);
            searchGen.setEnabled(state);
        }
    });

        caseSensitive = new JCheckBoxMenuItem(Globals.lang("Case sensitive"),
                      Globals.prefs.getBoolean("caseSensitiveSearch"));
settings.add(select);

    
        
    
    
    
    
    
    
    
    settings.addSeparator();
        settings.add(caseSensitive);
    settings.add(regExpSearch);
    


    searchField.addActionListener(this);
    searchField.addCaretListener(this);
        search.addActionListener(this);
    searchField.addFocusListener(new FocusAdapter() {
          public void focusGained(FocusEvent e) {
            if (increment.isSelected())
              searchField.setText("");
          }
        public void focusLost(FocusEvent e) {
            incSearch = false;
            incSearchPos = -1; 
                       
                       
                       
                       
                    if (increment.isSelected()) {
                      
                      
                    }
        }
        });
    escape.addActionListener(this);
    escape.setEnabled(false); 

    openset.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
                  if (settings.isVisible()) {
                    
                    
                  }
                  else {
                    JButton src = (JButton) e.getSource();
                    settings.show(src, 0, openset.getHeight());
                  }
        }
        });

            Insets margin = new Insets(0, 2, 0, 2);
            
            escape.setMargin(margin);
            openset.setMargin(margin);
            int butSize = help.getIcon().getIconHeight() + 5;
            Dimension butDim = new Dimension(butSize, butSize);
            help.setPreferredSize(butDim);
            help.setMinimumSize(butDim);
            help.setMargin(margin);
            help.addActionListener(new HelpAction(Globals.helpDiag, GUIGlobals.searchHelp, "Help"));

    if (Globals.prefs.getBoolean("incrementS"))
        increment.setSelected(true);

    JPanel main = new JPanel();
    main.setLayout(gbl);
    
    con.gridwidth = GridBagConstraints.REMAINDER;
    con.fill = GridBagConstraints.BOTH;
        con.weightx = 1;
    
    
    
        
        gbl.setConstraints(searchField,con);
        main.add(searchField) ;
        
        gbl.setConstraints(search,con);
        main.add(search) ;
        con.gridwidth = GridBagConstraints.REMAINDER;
        gbl.setConstraints(escape,con);
        main.add(escape) ;
        con.insets = new Insets(0, 2, 0,  0);
        gbl.setConstraints(increment, con);
        main.add(increment);
        gbl.setConstraints(floatSearch, con);
        main.add(floatSearch);
        gbl.setConstraints(hideSearch, con);
        main.add(hideSearch);
    con.insets = new Insets(0, 0, 0,  0);
        JPanel pan = new JPanel();
        GridBagLayout gb = new GridBagLayout();
        gbl.setConstraints(pan, con);
        pan.setLayout(gb);
        con.weightx = 1;
        con.gridwidth = 1;
        gb.setConstraints(openset, con);
        pan.add(openset);
        con.weightx = 0;
        gb.setConstraints(help, con);
        pan.add(help);
        main.add(pan);
        main.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
        
        setContent(main);

    searchField.getInputMap().put(Globals.prefs.getKey("Repeat incremental search"),
                      "repeat");

    searchField.getActionMap().put("repeat", new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            if (increment.isSelected())
            repeatIncremental();
        }
        });
    searchField.getInputMap().put(Globals.prefs.getKey("Clear search"), "escape");
    searchField.getActionMap().put("escape", new AbstractAction() {
        public void actionPerformed(ActionEvent e) {
            hideAway();
            
        }
        });
    setSearchButtonSizes();
    updateSearchButtonText();
    }

    
    private void setSearchButtonSizes() {
        search.setText(Globals.lang("Search Specified Field(s)"));
        Dimension size1 = search.getPreferredSize();
        search.setText(Globals.lang("Search All Fields"));
        Dimension size2 = search.getPreferredSize();
        size2.width = Math.max(size1.width,size2.width);
        search.setMinimumSize(size2);
        search.setPreferredSize(size2);
    }

    public void updatePrefs() {
    Globals.prefs.putBoolean("searchReq", searchReq.isSelected());
    Globals.prefs.putBoolean("searchOpt", searchOpt.isSelected());
    Globals.prefs.putBoolean("searchGen", searchGen.isSelected());
    Globals.prefs.putBoolean("searchAll", searchAll.isSelected());
    Globals.prefs.putBoolean("incrementS", increment.isSelected());
    Globals.prefs.putBoolean("selectS", select.isSelected());
    Globals.prefs.putBoolean("grayOutNonHits", floatSearch.isSelected());
    Globals.prefs.putBoolean("caseSensitiveSearch",
             caseSensitive.isSelected());
    Globals.prefs.putBoolean("regExpSearch", regExpSearch.isSelected());

    }

    public void startIncrementalSearch() {
    increment.setSelected(true);
    searchField.setText("");
        
    searchField.requestFocus();
    }

    
    public void startSearch() {
    if (increment.isSelected() && incSearch) {
        repeatIncremental();
        return;
    }
    if (!searchField.hasFocus()) {
        
            searchField.selectAll();
        searchField.requestFocus();
    } else {
        if (increment.isSelected())
            floatSearch.setSelected(true);
        else if (floatSearch.isSelected())
            hideSearch.setSelected(true);
        else {
        increment.setSelected(true);
        }
        increment.revalidate();
        increment.repaint();

        searchField.requestFocus();

    }
    }

    public void actionPerformed(ActionEvent e) {
    if (e.getSource() == escape) {
        incSearch = false;
        if (panel != null) {
            Thread t = new Thread() {
                public void run() {
                    clearSearch();
                }
            };
            
            SwingUtilities.invokeLater(t);
        }
    }
    else if (((e.getSource() == searchField) || (e.getSource() == search))
         && !increment.isSelected()
         && (panel != null)) {
        updatePrefs(); 
            if (searchField.getText().equals("")) {
              
              panel.stopShowingSearchResults();
              return;
            }
        
        Hashtable searchOptions = new Hashtable();
        searchOptions.put("option",searchField.getText()) ;
        SearchRuleSet searchRules = new SearchRuleSet() ;
        SearchRule rule1;

        rule1 = new BasicSearch(Globals.prefs.getBoolean("caseSensitiveSearch"),
                Globals.prefs.getBoolean("regExpSearch"));

        
        try {
            
            
            rule1 = new SearchExpression(Globals.prefs,searchOptions);
        } catch (Exception ex) {
            
        }

        searchRules.addRule(rule1) ;
        SearchWorker worker = new SearchWorker(searchRules, searchOptions);
        worker.getWorker().run();
        worker.getCallBack().update();
        escape.setEnabled(true);
    }
    }

    class SearchWorker extends AbstractWorker {
        private SearchRuleSet rules;
        Hashtable searchTerm;
        int hits = 0;
        public SearchWorker(SearchRuleSet rules, Hashtable searchTerm) {
            this.rules = rules;
            this.searchTerm = searchTerm;
        }

        public void run() {
            Collection entries = panel.getDatabase().getEntries();
            for (Iterator i=entries.iterator(); i.hasNext();) {
                BibtexEntry entry = (BibtexEntry)i.next();
                boolean hit = rules.applyRule(searchTerm, entry) > 0;
                entry.setSearchHit(hit);
                if (hit) hits++;
            }
        }

        public void update() {
            panel.output(Globals.lang("Searched database. Number of hits")
                    + ": " + hits);

            
            if (hideSearch.isSelected()) {
                
                if (startedFloatSearch) {
                    panel.mainTable.stopShowingFloatSearch();
                    startedFloatSearch = false;
                }
                startedFilterSearch = true;
                panel.setSearchMatcher(SearchMatcher.INSTANCE);

            } else {
                
                if (startedFilterSearch) {
                    panel.stopShowingSearchResults();
                    startedFilterSearch = false;
                }
                startedFloatSearch = true;
                panel.mainTable.showFloatSearch(SearchMatcher.INSTANCE);

            }

            
            searchField.select(0, searchField.getText().length());

        }
    }

    public void clearSearch() {
        if (startedFloatSearch) {
            startedFloatSearch = false;
            panel.mainTable.stopShowingFloatSearch();
        } else if (startedFilterSearch) {
            startedFilterSearch = false;
            panel.stopShowingSearchResults();
        }
        
        escape.setEnabled(false);
    }
    public void itemStateChanged(ItemEvent e) {
    if (e.getSource() == increment) {
        if (startedFilterSearch || startedFloatSearch) {
            clearSearch();
        }
        updateSearchButtonText();
        if (increment.isSelected())
        searchField.addKeyListener(this);
        else
        searchField.removeKeyListener(this);
    } else  {
        updateSearchButtonText();

        
        
        
    }
    }

    private void repeatIncremental() {
    incSearchPos++;
    if (panel != null)
        goIncremental();
    }

    
    public void keyTyped(KeyEvent e) {
    if (e.isControlDown()) {
        return;
    }
    if (panel != null)
        goIncremental();
    }

    private void goIncremental() {
    incSearch = true;
    escape.setEnabled(true);
    SwingUtilities.invokeLater(new Thread() {
        public void run() {
            String text = searchField.getText();


            if (incSearchPos >= panel.getDatabase().getEntryCount()) {
            panel.output("'"+text+"' : "+Globals.lang

                     ("Incremental search failed. Repeat to search from top.")+".");
            incSearchPos = -1;
            return;
            }

            if (searchField.getText().equals("")) return;
            if (incSearchPos < 0)
            incSearchPos = 0;
            BibtexEntry be = panel.mainTable.getEntryAt(incSearchPos);
            while (!incSearcher.search(text, be)) {
                incSearchPos++;
                if (incSearchPos < panel.getDatabase().getEntryCount())
                    be = panel.mainTable.getEntryAt(incSearchPos);
            else {
                panel.output("'"+text+"' : "+Globals.lang
                     ("Incremental search failed. Repeat to search from top."));
                incSearchPos = -1;
                return;
            }
            }
            if (incSearchPos >= 0) {

            panel.selectSingleEntry(incSearchPos);
            panel.output("'"+text+"' "+Globals.lang

                     ("found")+".");

            }
        }
        });
    }

    public void componentClosing() {
    frame.searchToggle.setSelected(false);
        if (panel != null) {
            if (startedFilterSearch || startedFloatSearch)
                clearSearch();
        }
    }


    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {}

    public void caretUpdate(CaretEvent e) {
        if (e.getSource() == searchField) {
            updateSearchButtonText();
        }
    }

    
    private void updateSearchButtonText() {
        search.setText(!increment.isSelected()
                && SearchExpressionParser.checkSyntax(
                searchField.getText(),
                caseSensitive.isSelected(),
                regExpSearch.isSelected()) != null
                ? Globals.lang("Search Specified Field(s)")
                : Globals.lang("Search All Fields"));
    }

    
    public void reportError(String errorMessage) {
        JOptionPane.showMessageDialog(panel, errorMessage, Globals.lang("Search error"),
                JOptionPane.ERROR_MESSAGE);
    }

    
    public void reportError(String errorMessage, Exception exception) {
        reportError(errorMessage);
    }
}
