
package genj.timeline;

import genj.almanac.Almanac;
import genj.gedcom.Gedcom;
import genj.gedcom.Grammar;
import genj.gedcom.PropertyEvent;
import genj.gedcom.TagPath;
import genj.util.Resources;
import genj.util.swing.ColorsWidget;
import genj.util.swing.DialogHelper;
import genj.util.swing.ImageIcon;
import genj.util.swing.ListSelectionWidget;
import genj.util.swing.NestedBlockLayout;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class TimelineViewSettings extends JTabbedPane {
  
  
  private Resources resources = Resources.get(this);
  
  
  private ListSelectionWidget<TagPath> pathsList;
  
  
  private ListSelectionWidget<String> almanacsList;
  
  
  private JCheckBox checkTags,checkDates,checkGrid;
  
  
  private JSpinner spinCmBefEvent, spinCmAftEvent;
     
  
  private ColorsWidget colorWidget;
  
  private Commit commit;
    
  
  TimelineViewSettings(final TimelineView view) {
    
    final Gedcom gedcom = view.getModel().getGedcom();

    commit = new Commit(view);

    
    pathsList = new ListSelectionWidget<TagPath>() {
      protected ImageIcon getIcon(TagPath path) {
        Grammar grammar = Grammar.V55;
        if (gedcom!=null)
          grammar = gedcom.getGrammar();
        return grammar.getMeta(path).getImage();
      }
    };
    if (gedcom!=null)
      pathsList.setChoices(PropertyEvent.getTagPaths(gedcom));
    pathsList.setCheckedChoices(view.getModel().getPaths());
    pathsList.addChangeListener(commit);

    
    almanacsList = new ListSelectionWidget<String>() {
      protected String getText(String choice) {
        return "<html><body>"+choice+"</body></html>";
      }
    };
    Almanac almanac = Almanac.getInstance();
    almanac.waitLoaded();
    List<String> cats = almanac.getCategories();
    almanacsList.setChoices(cats);
    almanacsList.setCheckedChoices(view.getAlmanacCategories());
    almanacsList.addChangeListener(commit);
    

    
    JPanel panelOptions = new JPanel(new NestedBlockLayout(
        "<col><check gx=\"1\"/><check gx=\"1\"/><check gx=\"1\"/><row><label/><spin/></row><row><label/><spin/></row></col>"
        ));
    panelOptions.setOpaque(false);
    
    
    checkTags = createCheck("info.show.tags", view.isPaintTags());
    checkDates = createCheck("info.show.dates", view.isPaintDates());
    checkGrid = createCheck("info.show.grid", view.isPaintGrid());
    panelOptions.add(checkTags);
    panelOptions.add(checkDates);
    panelOptions.add(checkGrid);
    
    spinCmBefEvent = createSpinner(TimelineView.MIN_CM_BEF_EVENT, view.getCmBeforeEvents(), TimelineView.MAX_CM_BEF_EVENT, "info.befevent.tip");
    panelOptions.add(new JLabel(resources.getString("info.befevent")));
    panelOptions.add(spinCmBefEvent);

    spinCmAftEvent = createSpinner(TimelineView.MIN_CM_AFT_EVENT, view.getCmAfterEvents(), TimelineView.MAX_CM_AFT_EVENT, "info.aftevent.tip");
    panelOptions.add(new JLabel(resources.getString("info.aftevent")));
    panelOptions.add(spinCmAftEvent);
    
    
    JPanel panelMain = new JPanel(new BorderLayout());
    panelMain.add(new JLabel(resources.getString("info.events")), BorderLayout.NORTH);
    panelMain.add(pathsList   , BorderLayout.CENTER);
    panelMain.add(panelOptions, BorderLayout.SOUTH);
    
    
    colorWidget = new ColorsWidget();
    for (String key : view.colors.keySet()) 
      colorWidget.addColor(key, resources.getString("color."+key), view.colors.get(key));
    colorWidget.addChangeListener(commit);
    
    
    add(resources.getString("page.main")  , panelMain);
    add(resources.getString("page.colors"), colorWidget);
    add(resources.getString("page.almanac"), almanacsList);

    
    DialogHelper.setOpaque(colorWidget, false);
    DialogHelper.setOpaque(panelMain, false);
    
    
  }
  
  private JCheckBox createCheck(String key, boolean on) {
    JCheckBox result = new JCheckBox(resources.getString(key), on);
    result.addActionListener(commit);
    result.setOpaque(false);
    return result;
  }

  private class Commit implements ChangeListener, ActionListener {
    
    private TimelineView view;
    
    private Commit(TimelineView view) {
      this.view = view;
    }

    public void stateChanged(ChangeEvent e) {
      actionPerformed(null);
    }
    
    public void actionPerformed(ActionEvent e) {
      
      
      view.getModel().setPaths(pathsList.getCheckedChoices());
      
      
      view.setPaintTags(checkTags.isSelected());
      view.setPaintDates(checkDates.isSelected());
      view.setPaintGrid(checkGrid.isSelected());
      
      
      view.setCMPerEvents(
         ((Double)spinCmBefEvent.getModel().getValue()).doubleValue(), 
         ((Double)spinCmAftEvent.getModel().getValue()).doubleValue()
      );
      
      
      for (String key : view.colors.keySet()) 
        view.colors.put(key, colorWidget.getColor(key));
      
      
      view.setAlmanacCategories(almanacsList.getCheckedChoices());
    }
  }
  
  private JSpinner createSpinner(double min, double value, double max, String tip) {
    JSpinner result = new JSpinner(new SpinnerNumberModel(value, min, max, 0.1D));
    JSpinner.NumberEditor editor = new JSpinner.NumberEditor(result, "##0.0");
    result.setEditor(editor);
    result.addChangeListener(editor);
    result.addChangeListener(commit);
    result.setToolTipText(resources.getString(tip));
    return result;
  }

} 
