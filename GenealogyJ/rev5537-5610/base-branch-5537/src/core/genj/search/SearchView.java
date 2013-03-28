
package genj.search;

import genj.gedcom.Entity;
import genj.gedcom.Gedcom;
import genj.gedcom.GedcomListener;
import genj.gedcom.Grammar;
import genj.gedcom.MetaProperty;
import genj.gedcom.Property;
import genj.gedcom.TagPath;
import genj.util.GridBagHelper;
import genj.util.Registry;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ButtonHelper;
import genj.util.swing.ChoiceWidget;
import genj.util.swing.HeadlessLabel;
import genj.util.swing.ImageIcon;
import genj.util.swing.PopupWidget;
import genj.view.ContextProvider;
import genj.view.ContextSelectionEvent;
import genj.view.ToolBarSupport;
import genj.view.ViewContext;
import genj.window.WindowManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.AbstractListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import spin.Spin;


public class SearchView extends JPanel implements ToolBarSupport {
  
  
  private final static String
   OPEN = "<font color=red>",
   CLOSE = "</font>",
   NEWLINE = "<br>";
  
  
  private final static int MAX_HITS = 255;
  
  
  private final static String[]
    DEFAULT_VALUES = {
      "M(a|e)(i|y)er", "San.+Francisco", "^(M|F)"
    },
    DEFAULT_PATHS = {
      "INDI", "INDI:NAME", "INDI:BIRT", "INDI:OCCU", "INDI:NOTE", "INDI:RESI",
      "FAM"
    }
  ;
  
  
  private final static int MAX_OLD = 16;
  
  
   static Resources resources = Resources.get(SearchView.class);
  
  
  private Gedcom gedcom;
  
  
  private Registry registry;
  
  
  private Results results = new Results();
  private ResultWidget listResults = new ResultWidget();

  
  private HeadlessLabel viewFactory = new HeadlessLabel(listResults.getFont()); 

  
  private ChoiceWidget choicePath, choiceValue;
  private JCheckBox checkRegExp;
  private JLabel labelCount;
  
  
  private LinkedList oldPaths, oldValues;
  
  
  private AbstractButton bSearch, bStop;
  
  
  private final static ImageIcon
    IMG_START = new ImageIcon(SearchView.class, "Start"),
    IMG_STOP  = new ImageIcon(SearchView.class, "Stop" );

  
  public SearchView(Gedcom geDcom, Registry reGistry) {
    
    
    gedcom = geDcom;
    registry = reGistry;
    
    
    oldPaths = new LinkedList(Arrays.asList(registry.get("old.paths" , DEFAULT_PATHS)));
    oldValues= new LinkedList(Arrays.asList(registry.get("old.values", DEFAULT_VALUES)));
    boolean useRegEx = registry.get("regexp", false);

    
    ActionListener aclick = new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {
        bStop.doClick();
        bSearch.doClick();
      }
    };
    
    
    JLabel labelValue = new JLabel(resources.getString("label.value"));
    checkRegExp = new JCheckBox(resources.getString("label.regexp"), useRegEx);

    choiceValue = new ChoiceWidget(oldValues);
    choiceValue.addActionListener(aclick);

    PopupWidget popupPatterns = new PopupWidget("...", null, createPatternActions());
    popupPatterns.setMargin(new Insets(0,0,0,0));

    JLabel labelPath = new JLabel(resources.getString("label.path"));    
    choicePath = new ChoiceWidget(oldPaths);
    choicePath.addActionListener(aclick);
    
    PopupWidget popupPaths = new PopupWidget("...", null, createPathActions());
    popupPaths.setMargin(new Insets(0,0,0,0));
    
    labelCount = new JLabel();
    
    JPanel paneCriteria = new JPanel();
    try {
      paneCriteria.setFocusCycleRoot(true);
    } catch (Throwable t) {
    }
    
    GridBagHelper gh = new GridBagHelper(paneCriteria);
    
    gh.add(labelValue    ,0,0,2,1,0, new Insets(0,0,0,8));
    gh.add(checkRegExp   ,2,0,1,1, GridBagHelper.GROW_HORIZONTAL|GridBagHelper.FILL_HORIZONTAL);
    gh.add(labelCount    ,3,0,1,1);
    
    gh.add(popupPatterns ,0,1,1,1);
    gh.add(choiceValue   ,1,1,3,1, GridBagHelper.GROW_HORIZONTAL|GridBagHelper.FILL_HORIZONTAL, new Insets(3,3,3,3));
    
    gh.add(labelPath     ,0,2,4,1, GridBagHelper.GROW_HORIZONTAL|GridBagHelper.FILL_HORIZONTAL);
    
    gh.add(popupPaths    ,0,3,1,1);
    gh.add(choicePath    ,1,3,3,1, GridBagHelper.GROW_HORIZONTAL|GridBagHelper.FILL_HORIZONTAL, new Insets(0,3,3,3));
    
    
    setLayout(new BorderLayout());
    add(BorderLayout.NORTH , paneCriteria);
    add(BorderLayout.CENTER, new JScrollPane(listResults) );
    choiceValue.requestFocusInWindow();

    
  }
  
  
  public void addNotify() {
    
    gedcom.addGedcomListener((GedcomListener)Spin.over(results));
    
    super.addNotify();
    
    choiceValue.requestFocusInWindow();
  }
  
  
  public void removeNotify() {
    
    gedcom.removeGedcomListener((GedcomListener)Spin.over(results));
    
    registry.put("regexp"    , checkRegExp.isSelected());
    registry.put("old.values", oldValues);
    registry.put("old.paths" , oldPaths );
    
    super.removeNotify();
  }

  
  
  public void populate(JToolBar bar) {
    ButtonHelper bh = new ButtonHelper().setContainer(bar).setInsets(0);
    ActionSearch search = new ActionSearch();
    ActionStop   stop   = new ActionStop  (search);
    bSearch = bh.create(search);
    bStop   = bh.create(stop);
  }
  
  
  private void remember(ChoiceWidget choice, LinkedList old, String value) {
    
    if (value.trim().length()==0) return;
    
    old.remove(value);
    old.addFirst(value);
    if (old.size()>MAX_OLD) old.removeLast();
    
    choice.setValues(old);
    choice.setText(value);
    
  }

  
  private Matcher getMatcher(String pattern, boolean regex) {

    Matcher result = regex ? (Matcher)new RegExMatcher() : (Matcher)new SimpleMatcher();
    
    
    result.init(pattern);
    
    
    return result;
  }

  
  private List createPathActions() {
    
    
    List result = new ArrayList();
    for (int i=0;i<DEFAULT_PATHS.length;i++) {
      result.add(new ActionPath(DEFAULT_PATHS[i]));
    }
    
    
    return result;
  }

  
  private List createPatternActions() {
    
    List result = new ArrayList();
    for (int i=0;;i++) {
      
      String 
        key = "regexp."+i,
        txt = resources.getString(key+".txt", false),
        pat = resources.getString(key+".pat", false);
      
      if (txt==null) break;
      
      if (pat==null) 
        continue;
      
      result.add(new ActionPattern(txt,pat));
    }
    return result; 
  }
  
  
  private class ActionPath extends Action2 {
    
    private TagPath tagPath;
    
    
    private ActionPath(String path) {
      tagPath = new TagPath(path);
      MetaProperty meta = Grammar.V55.getMeta(tagPath);
      setText(meta.getName());
      setImage(meta.getImage());
    }
    
    
    protected void execute() {
      choicePath.setText(tagPath.toString());
    }
  } 

  
  private class ActionPattern extends Action2 {
    
    private String pattern;
    
    private ActionPattern(String txt, String pat) {
      
      int i = txt.indexOf(' ');
      if (i>0)
        txt = "<html><b>"+txt.substring(0,i)+"</b>&nbsp;&nbsp;&nbsp;"+txt.substring(i)+"</html>";
          
      setText(txt);
      pattern = pat;
    }
    
    protected void execute() {

      
      final JTextField field = choiceValue.getTextEditor();
      int 
        selStart = field.getSelectionStart(),
        selEnd   = field.getSelectionEnd  ();
      if (selEnd<=selStart) {
        selStart = field.getCaretPosition();
        selEnd   = selStart;
      }
      
      String all = field.getText();
      
      String before = all.substring(0, selStart);
      
      String selection = selEnd>selStart ? '('+all.substring(selStart, selEnd)+')' : "";
      
      String after = all.substring(selEnd);

      
      final String result = MessageFormat.format(pattern, new Object[]{ all, before, selection, after} );

      
      SwingUtilities.invokeLater(new Runnable() { public void run() {
        
        int pos = result.indexOf('#');
        
        
        field.setText(result.substring(0,pos)+result.substring(pos+1));
        field.select(0,0);
        field.setCaretPosition(pos);
        
        
        checkRegExp.setSelected(true);
      }});
      
      
    }
  } 

  
  private class ActionSearch extends Action2 {
    
    private TagPath tagPath = null;
    
    private int hitCount = 0;
    
    private Set entities = new HashSet();
    
    private List hits = new ArrayList(MAX_HITS);
    
    private Matcher matcher;
    
    private ActionSearch() {
      setImage(IMG_START);
      setAsync(ASYNC_SAME_INSTANCE);
    }
    
    protected boolean preExecute() {
      
      results.clear();
      
      bSearch.setEnabled(false);
      bStop.setEnabled(true);
      
      String value = choiceValue.getText();
      String path = choicePath.getText();
      try {
        matcher = getMatcher(value, checkRegExp.isSelected());
        tagPath = path.length()>0 ? new TagPath(path) : null;
      } catch (IllegalArgumentException e) {
        WindowManager.getInstance(getTarget()).openDialog(null,value,WindowManager.ERROR_MESSAGE,e.getMessage(),Action2.okOnly(),SearchView.this);
        return false;
      }
      
      remember(choiceValue, oldValues, value);
      remember(choicePath , oldPaths , path );
      
      return true;
    }

    
    protected void execute() {
      search(gedcom);
    }
    
    
    protected void syncExecute() {
      synchronized (hits) {
        results.add(hits);
        hits.clear();
      }
      labelCount.setText(""+hitCount);
    }
    
    
    protected void handleThrowable(String phase, Throwable t) {
      WindowManager.getInstance(getTarget()).openDialog(null,null,WindowManager.INFORMATION_MESSAGE,t.getMessage() ,Action2.okOnly(),SearchView.this);
    }

    
    protected void postExecute(boolean preExecuteResult) {
      
      labelCount.setText(""+hitCount);
      
      entities.clear();
      hits.clear();
      hitCount = 0;
      
      bSearch.setEnabled(true);
      bStop.setEnabled(false);
      
    }
    
    
    private void search(Gedcom gedcom) {
      for (int t=0; t<Gedcom.ENTITIES.length; t++) {
        for (Iterator es=gedcom.getEntities(Gedcom.ENTITIES[t]).iterator();es.hasNext();) {
          search((Entity)es.next());
        }
      }
    }
    
    
    private void search(Entity entity) {
      search(entity, entity, 0);
    }
    
    
    private void search(Entity entity, Property prop, int pathIndex) {
      
      if (getThread().isInterrupted()) return;
      
      boolean searchThis = true;
      if (tagPath!=null) {
        
        if (pathIndex<tagPath.length()&&!tagPath.get(pathIndex).equals(prop.getTag())) 
          return;
        
        searchThis = pathIndex>=tagPath.length()-1;
      }
      
      if (searchThis&&!prop.isTransient()) {
        
        if (entity==prop)
          search(entity, entity, entity.getId(), true);
        
        search(entity, prop, prop.getDisplayValue(), false);
      }
      
      int n = prop.getNoOfProperties();
      for (int i=0;i<n;i++) {
        search(entity, prop.getProperty(i), pathIndex+1);
      }
      
    }

    
    private void search(Entity entity, Property prop, String value, boolean isID) {
      
      Matcher.Match[] matches = matcher.match(value);
      if (matches.length==0)
        return;
      
      if (hitCount==MAX_HITS)
        throw new IndexOutOfBoundsException(resources.getString("maxhits", Integer.toString(MAX_HITS)));
      hitCount++;
      
      entities.add(entity);
      
      Hit hit = new Hit(prop, value, matches, entities.size(), isID);
      
      synchronized (hits) {
        hits.add(hit);
        
        if (hits.size()==1) sync();
      }
      
    }
    
  } 
  
  
  private class ActionStop extends Action2 {
    
    private ActionSearch start;
    
    private ActionStop(ActionSearch start) {
      setImage(IMG_STOP);
      setEnabled(false);
      this.start = start;
    }
    
    protected void execute() {
      start.cancel(false);
    }
  } 

  
  private class Results extends AbstractListModel implements GedcomListener {
    
    
    private List hits = new ArrayList(255);
    
    
    private void clear() {
      
      if (hits.isEmpty())
        return;
      
      int size = hits.size();
      hits.clear();
      fireIntervalRemoved(this, 0, size-1);
      
    }
    
    
    private void add(List list) {
      
      if (list.isEmpty()) 
        return;
      
      int size = hits.size();
      hits.addAll(list);
      fireIntervalAdded(this, size, hits.size()-1);
      
    }
    
    
    public Object getElementAt(int index) {
      return hits.get(index);
    }
    
    
    public int getSize() {
      return hits.size();
    }
    
    
    private Hit getHit(int i) {
      return (Hit)hits.get(i);
    }

    public void gedcomEntityAdded(Gedcom gedcom, Entity entity) {
      
    }

    public void gedcomEntityDeleted(Gedcom gedcom, Entity entity) {
      
    }

    public void gedcomPropertyAdded(Gedcom gedcom, Property property, int pos, Property added) {
      
    }

    public void gedcomPropertyChanged(Gedcom gedcom, Property prop) {
      for (int i=0;i<hits.size();i++) {
        Hit hit = (Hit)hits.get(i);
        if (hit.getProperty()==prop) 
          fireContentsChanged(this, i, i);
      }
    }

    public void gedcomPropertyDeleted(Gedcom gedcom, Property property, int pos, Property removed) {
      for (int i=0;i<hits.size();) {
        Hit hit = (Hit)hits.get(i);
        if (hit.getProperty()==removed) {
          hits.remove(i);
          fireIntervalRemoved(this, i, i);
        } else {
          i++;
        }
      }
    }

  } 

    
  private class ResultWidget extends JList implements ListSelectionListener, ListCellRenderer, ContextProvider  {
    
    
    private JTextPane text = new JTextPane();
    
    
    private Color[] bgColors = new Color[3];

    
    private ResultWidget() {
      super(results);
      
      bgColors[0] = getSelectionBackground();
      bgColors[1] = getBackground();
      bgColors[2] = new Color( 
        Math.max(bgColors[1].getRed  ()-16,  0), 
        Math.min(bgColors[1].getGreen()+16,255), 
        Math.max(bgColors[1].getBlue ()-16,  0)
      );
      
      
      setCellRenderer(this);
      addListSelectionListener(this);
      text.setOpaque(true);
    }
    
    
    public ViewContext getContext() {
      
      ViewContext result = new ViewContext(gedcom);
      
      Object[] selection = getSelectedValues();
      for (int i = 0; i < selection.length; i++) {
        Hit hit = (Hit)selection[i];
        result.addProperty(hit.getProperty());
      }
      return result;
    }

    
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      Hit hit = (Hit)value;
      
      
      int c = isSelected ? 0 : 1 + (hit.getEntity()&1);  
      text.setBackground(bgColors[c]);
      
      
      text.setDocument(hit.getDocument());
      
      
      return text;
    }
    
    
    public void valueChanged(ListSelectionEvent e) {
      int row = listResults.getSelectedIndex();
      if (row>=0)
        WindowManager.broadcast(new ContextSelectionEvent(new ViewContext(results.getHit(row).getProperty()), this));
    }

    
  } 
 
} 
