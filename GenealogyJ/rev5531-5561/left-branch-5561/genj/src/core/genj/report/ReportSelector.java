
package genj.report;

import genj.option.OptionsWidget;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Collections;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;


class ReportSelector extends JTabbedPane {
  
  private final static ImageIcon
    imgReload= new ImageIcon(ReportView.class,"Reload"     );
  
  private ReportDetail detail = new ReportDetail();
  private ReportList list = new ReportList(ReportLoader.getInstance().getReports(), false);
  private OptionsWidget options = new OptionsWidget(getName());

  
  public ReportSelector() {

    Resources res = Resources.get(this);
    
    JPanel tab = new JPanel(new BorderLayout());
    tab.add(new JScrollPane(list), BorderLayout.WEST);
    tab.add(detail, BorderLayout.CENTER);
    
    add(res.getString("report.reports"), tab);
    add(res.getString("report.options"), options);
    
    list.setSelectionListener(new ReportSelectionListener() {
      public void valueChanged(Report report) {
        detail.setReport(report);
        options.setOptions(report!=null ? report.getOptions() : Collections.EMPTY_LIST);
      }
    });
    
    
    if (list.getRowCount()>0)
      list.setSelectionRow(0);
    
  }
  

  
  private class ActionReload extends Action2 {
    protected ActionReload() {
      setImage(imgReload);
      setTip(Resources.get(this), "report.reload.tip");
      setEnabled(!ReportLoader.getInstance().isReportsInClasspath());
    }
    public void actionPerformed(ActionEvent event) {
      
      ReportLoader.clear();
      
      Report reports[] = ReportLoader.getInstance().getReports();
      
      list.setReports(reports);
      
    }
  } 


  
  Report getReport() {
    
    
    options.stopEditing();

    
    return list.getSelection();
  }
}
