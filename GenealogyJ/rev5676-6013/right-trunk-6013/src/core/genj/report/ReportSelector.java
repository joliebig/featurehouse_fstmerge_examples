
package genj.report;

import genj.option.OptionsWidget;
import genj.util.Resources;
import genj.util.swing.Action2;
import genj.util.swing.ImageIcon;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Collections;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;


class ReportSelector extends JPanel {
  
  private final static ImageIcon
    imgReload= new ImageIcon(ReportView.class,"Reload"     );
  
  private ReportDetail detail = new ReportDetail();
  private ReportList list = new ReportList(ReportLoader.getInstance().getReports(), false);
  private OptionsWidget options = new OptionsWidget(getName());

  
  public ReportSelector() {
    
    super(new BorderLayout());

    Resources res = Resources.get(this);
    
    final JTabbedPane right = new JTabbedPane();
    right.add(res.getString("title"), detail);
    right.add(res.getString("report.options"), options);
    detail.setOpaque(false);
    
    add(new JLabel(res.getString("report.reports")), BorderLayout.NORTH);
    add(new JScrollPane(list), BorderLayout.WEST);
    add(right, BorderLayout.CENTER);
    
    list.setSelectionListener(new ReportSelectionListener() {
      public void valueChanged(Report report) {
        detail.setReport(report);
        right.setTitleAt(0, report.getName());
        options.setOptions(report!=null ? report.getOptions() : Collections.EMPTY_LIST);
      }
    });
    
    if (list.getModel().getSize()>0)
      list.setSelectedIndex(0);
    
  }
  
  
  public void select(Report report) {
    if (report!=null)
      list.setSelection(report);
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
