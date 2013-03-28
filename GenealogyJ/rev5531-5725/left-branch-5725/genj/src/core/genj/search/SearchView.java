
package genj.search;

import genj.gedcom.Context;
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
import genj.util.swing.ChoiceWidget;
import genj.util.swing.DialogHelper;
import genj.util.swing.HeadlessLabel;
import genj.util.swing.ImageIcon;
import genj.util.swing.PopupWidget;
import genj.view.ContextProvider;
import genj.view.SelectionSink;
import genj.view.ToolBar;
import genj.view.View;
import genj.view.ViewContext;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import spin.Spin;


public class SearchView extends View {
  
  
  private final static String
   OPEN = "<font color=red>",
   CLOSE = "</font>",
   NEWLINE = "<br>";
  
  
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
  
  
  private Context context = new Context();
  
  
  private final static Registry REGISTRY = Registry.get(SearchView.class);
  
  
  private Results results = new Results();
  private ResultWidget listResults = new ResultWidget();

  
  private HeadlessLabel viewFactory = new HeadlessLabel(listResults.getFont()); 

  
  private ChoiceWidget choicePath, choiceValue;
  private JCheckBox checkRegExp;
  private JLabel labelCount;
  
  private Action2 actionStart = new ActionStart(), actionStop = new ActionStop();
  
  
  private LinkedList<String> oldPaths, oldValues;
  
  
  private final static ImageIcon
    IMG_START = new ImageIcon(SearchView.class, "Start"),
    IMG_STOP  = new ImageIcon(SearchView.class, "Stop" );
  
  
  private Worker worker;

  
  public SearchView() {
    
    
    worker = new Worker((WorkerListener)Spin.over(new WorkerListener() {
      public void more(List<Hit> hits) {
        results.add(hits);
        labelCount.setText(""+results.getSize());
      }
      public void started() {
        
        results.clear();
        labelCount.setText("");
        actionStart.setEnabled(false);
        actionStop.setEnabled(true);
      }
      public void stopped() {
        actionStop.setEnabled(false);
        actionStart.setEnabled(context.getGedcom()!=null);
      }
    }));
    
    
    oldPaths = new LinkedList<String>(Arrays.asList(REGISTRY.get("old.paths" , DEFAULT_PATHS)));
    oldValues= new LinkedList<String>(Arrays.asList(REGISTRY.get("old.values", DEFAULT_VALUES)));
    boolean useRegEx = REGISTRY.get("regexp", false);

    
    ActionListener aclick = new ActionListener() {
      
      public void actionPerformed(ActionEvent e) {
        stop();
        start();
      }
    };
    
    
    JLabel labelValue = new JLabel(resources.getString("label.value"));
    checkRegExp = new JCheckBox(resources.getString("label.regexp"), useRegEx);

    choiceValue = new ChoiceWidget(oldValues);
    choiceValue.addActionListener(aclick);

    PopupWidget popupPatterns = new PopupWidget("...", null);
    popupPatterns.addItems(createPatternActions());
    popupPatterns.setMargin(new Insets(0,0,0,0));

    JLabel labelPath = new JLabel(resources.getString("label.path"));    
    choicePath = new ChoiceWidget(oldPaths);
    choicePath.addActionListener(aclick);
    
    PopupWidget popupPaths = new PopupWidget("...", null);
    popupPaths.addItems(createPathActions());
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
  
  public void start() {
    
    
    if (context==null)
      return;

    
    worker.stop();
    
    
    String value = choiceValue.getText();
    String path = choicePath.getText();
    remember(choiceValue, oldValues, value);
    remember(choicePath , oldPaths , path );
    
    
    TagPath p = null;
    if (path.length()>0) try {
      p = new TagPath(path);
    } catch (IllegalArgumentException iae) {
      DialogHelper.openDialog(value,DialogHelper.ERROR_MESSAGE,iae.getMessage(),Action2.okOnly(),SearchView.this);
      return;
    }
    
    worker.start(context.getGedcom(), p, value, checkRegExp.isSelected());
    
    
  }
  
  public void stop() {
    
    worker.stop();
    
  }
  
  
  public void removeNotify() {
    
    REGISTRY.put("regexp"    , checkRegExp.isSelected());
    REGISTRY.put("old.values", oldValues);
    REGISTRY.put("old.paths" , oldPaths );
    
    super.removeNotify();
  }

  @Override
  public void setContext(Context newContext, boolean isActionPerformed) {

    
    if (context.getGedcom()!=null && context.getGedcom()!=newContext.getGedcom()) {
      
      stop();
      results.clear();
      actionStart.setEnabled(false);
      
      context.getGedcom().removeGedcomListener((GedcomListener)Spin.over(results));
    }
    
    
    context = newContext;
    
    
    if (context.getGedcom()!=null) {
      context = new Context(newContext.getGedcom());
      context.getGedcom().addGedcomListener((GedcomListener)Spin.over(results));
      actionStart.setEnabled(true);
    }
    
  }
  
  
  public void populate(ToolBar toolbar) {
    toolbar.add(actionStart);
    toolbar.add(actionStop);
  }
  
  
  private void remember(ChoiceWidget choice, LinkedList<String> old, String value) {
    
    if (value.trim().length()==0) return;
    
    old.remove(value);
    old.addFirst(value);
    if (old.size()>MAX_OLD) old.removeLast();
    
    choice.setValues(old);
    choice.setText(value);
    
  }

  
  private List<Action2> createPathActions() {
    
    
    List<Action2> result = new ArrayList<Action2>();
    for (int i=0;i<DEFAULT_PATHS.length;i++) {
      result.add(new ActionPath(DEFAULT_PATHS[i]));
    }
    
    
    return result;
  }

  
  private List<Action2> createPatternActions() {
    
    List<Action2> result = new ArrayList<Action2>();
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
    
    
    public void actionPerformed(ActionEvent event) {
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
    
    public void actionPerformed(ActionEvent event) {

      
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

  
  private class ActionStart extends Action2 {
    
    
    private ActionStart() {
      setImage(IMG_START);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
      stop();
      start();
    }
    
  } 
  
  
  private class ActionStop extends Action2 {
    
    private ActionStop() {
      setImage(IMG_STOP);
      setEnabled(false);
    }
    
    public void actionPerformed(ActionEvent event) {
      stop();
    }
  } 

  
  private class Results extends AbstractListModel implements GedcomListener {
    
    
    private List<Hit> hits = new ArrayList<Hit>(255);
    
    
    private void clear() {
      
      if (hits.isEmpty())
        return;
      
      int size = hits.size();
      hits.clear();
      fireIntervalRemoved(this, 0, size-1);
      
    }
    
    
    private void add(List<Hit> list) {
      
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
      
      if (context==null)
        return null;
      
      List<Property> properties = new ArrayList<Property>();
      Object[] selection = getSelectedValues();
      for (int i = 0; i < selection.length; i++) {
        Hit hit = (Hit)selection[i];
        properties.add(hit.getProperty());
      }
      return new ViewContext(context.getGedcom(), null, properties);
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
    	  SelectionSink.Dispatcher.fireSelection(SearchView.this, new Context(results.getHit(row).getProperty()), false);
    }

    
  } 
 
} 
