
package genj.report;

import genj.util.Registry;
import genj.util.Resources;

import java.awt.Component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;


class ReportList extends JList {

  
  private Callback callback = new Callback();

  
  private ReportSelectionListener selectionListener = null;

  
  private Registry registry;

  
  private static final Resources RESOURCES = Resources.get(ReportView.class);
  
  private boolean byGroup;

  
  public ReportList(Report[] reports, boolean byGroup) {
    
    this.byGroup = byGroup;

    setReports(reports);
    setVisibleRowCount(3);
    getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setCellRenderer(callback);
    addListSelectionListener(callback);

    
  }

  
  public void setSelection(Report report) {
    if (report == null) {
      clearSelection();
    } else {
      setSelectedValue(report, true);
    }
  }

  
  public Report getSelection() {
    return (Report)getSelectedValue();
  }

  
  public void setSelectionListener(ReportSelectionListener listener) {
    selectionListener = listener;
  }

  
  public void setReports(Report[] reports) {
    setModel(new DefaultComboBoxModel(reports));
  }

  
  private class Callback extends DefaultListCellRenderer implements ListCellRenderer, ListSelectionListener {
    


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      
      Report report = (Report) value;
      setText(report.getName());
      setIcon(report.getIcon());
      
      return this;
    }

    
    public void valueChanged(javax.swing.event.ListSelectionEvent e) {
      Report r = (Report)getSelectedValue();
      if (selectionListener != null)
        selectionListener.valueChanged(r);
    }

  }

}
