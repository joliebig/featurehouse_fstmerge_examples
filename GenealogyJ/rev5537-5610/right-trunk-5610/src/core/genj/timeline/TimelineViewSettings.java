
package genj.timeline;

import genj.almanac.Almanac;
import genj.gedcom.PropertyEvent;
import genj.gedcom.TagPath;
import genj.util.Resources;
import genj.util.swing.ColorsWidget;
import genj.util.swing.ImageIcon;
import genj.util.swing.ListSelectionWidget;
import genj.util.swing.NestedBlockLayout;
import genj.view.Settings;
import genj.view.ViewManager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;


public class TimelineViewSettings extends JTabbedPane implements Settings {
  
  
  private Resources resources = Resources.get(this);
  
  
  private TimelineView view;
  
  
  private ListSelectionWidget pathsList = new ListSelectionWidget() {
    protected ImageIcon getIcon(Object choice) {
      TagPath path = (TagPath)choice;
      return view.getModel().gedcom.getGrammar().getMeta(path).getImage();
    }
  };
  
  
  private ListSelectionWidget almanacCategories = new ListSelectionWidget() {
    protected String getText(Object choice) {
      return "<html><body>"+choice+"</body></html>";
    }
  };
  
  
  private JCheckBox 
    checkTags = new JCheckBox(resources.getString("info.show.tags" )),
    checkDates = new JCheckBox(resources.getString("info.show.dates")),
    checkGrid = new JCheckBox(resources.getString("info.show.grid" ));
  
  
  private JSpinner spinCmBefEvent, spinCmAftEvent;
     
  
  private ColorsWidget colorWidget;
    
  
  public void init(ViewManager manager) {
    
    
    JPanel panelOptions = new JPanel(new NestedBlockLayout(
        "<col><check gx=\"1\"/><check gx=\"1\"/><check gx=\"1\"/><row><label/><spin/></row><row><label/><spin/></row></col>"
        ));
    
    
    panelOptions.add(checkTags);
    panelOptions.add(checkDates);
    panelOptions.add(checkGrid);
    
    spinCmBefEvent = createSpinner(TimelineView.MIN_CM_BEF_EVENT, TimelineView.MAX_CM_BEF_EVENT, resources.getString("info.befevent.tip"));
    panelOptions.add(new JLabel(resources.getString("info.befevent")));
    panelOptions.add(spinCmBefEvent);

    spinCmAftEvent = createSpinner(TimelineView.MIN_CM_AFT_EVENT, TimelineView.MAX_CM_AFT_EVENT, resources.getString("info.aftevent.tip"));
    panelOptions.add(new JLabel(resources.getString("info.aftevent")));
    panelOptions.add(spinCmAftEvent);
    
    
    JPanel panelMain = new JPanel(new BorderLayout());
    panelMain.add(new JLabel(resources.getString("info.events")), BorderLayout.NORTH);
    panelMain.add(pathsList   , BorderLayout.CENTER);
    panelMain.add(panelOptions, BorderLayout.SOUTH);
    
    
    JPanel panelEvents = new JPanel(new BorderLayout());
    panelEvents.add(almanacCategories, BorderLayout.CENTER);
    
    
    colorWidget = new ColorsWidget();
    
    
    add(resources.getString("page.main")  , panelMain);
    add(resources.getString("page.colors"), colorWidget);
    add(resources.getString("page.almanac"), panelEvents);

    
  }
  
  private JSpinner createSpinner(double min, double max, String tip) {
    
    JSpinner result = new JSpinner(new SpinnerNumberModel(min, min, max, 0.1D));
    JSpinner.NumberEditor editor = new JSpinner.NumberEditor(result, "##0.0");
    result.setEditor(editor);
    result.addChangeListener(editor);
    result.setToolTipText(tip);
    return result;
  }

  
  public void apply() {
    
    
    view.getModel().setPaths(pathsList.getSelection());
    
    
    view.setPaintTags(checkTags.isSelected());
    view.setPaintDates(checkDates.isSelected());
    view.setPaintGrid(checkGrid.isSelected());
    
    
    view.setCMPerEvents(
       ((Double)spinCmBefEvent.getModel().getValue()).doubleValue(), 
       ((Double)spinCmAftEvent.getModel().getValue()).doubleValue()
    );
    
    
    Iterator colors = view.colors.keySet().iterator();
    while (colors.hasNext()) {
      String key = colors.next().toString();
      view.colors.put(key, colorWidget.getColor(key));
    }
    
    
    view.setAlmanacCategories(almanacCategories.getSelection());
    
    
  }
  
  
  public void setView(JComponent viEw) {
    
    view = (TimelineView)viEw;
  }


  
  public void reset() {
    
    
    pathsList.setChoices(PropertyEvent.getTagPaths(view.getModel().gedcom));
    pathsList.setSelection(view.getModel().getPaths());
    
    
    checkTags.setSelected(view.isPaintTags());
    checkDates.setSelected(view.isPaintDates());
    checkGrid.setSelected(view.isPaintGrid());

    
    spinCmBefEvent.setValue(new Double(view.getCmBeforeEvents()));
    spinCmAftEvent.setValue(new Double(view.getCmAfterEvents()));
    
    
    colorWidget.removeAllColors();
    Iterator keys = view.colors.keySet().iterator();
    while (keys.hasNext()) {
      String key = keys.next().toString();
      String name = resources.getString("color."+key);
      Color color = (Color)view.colors.get(key);
      colorWidget.addColor(key, name, color);
    }
    
    
    Almanac almanac = Almanac.getInstance();
    almanac.waitLoaded();
    List cats = almanac.getCategories();
    almanacCategories.setChoices(cats);
    almanacCategories.setSelection(view.getAlmanacCategories());
    
    
  }
  
  
  public JComponent getEditor() {
    return this;
  }


} 
