
package genj.report;

import java.awt.Color;
import java.awt.Insets;

import genj.util.GridBagHelper;
import genj.util.Resources;
import genj.util.swing.EditorHyperlinkSupport;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;


class ReportDetail extends JPanel {

  private JLabel      lFile,lAuthor,lVersion;
  private JTextPane   tpInfo;
  private HTMLEditorKit editorKit = new HTMLEditorKit(ReportDetail.class);
  
  
  public ReportDetail() {

    Resources res = Resources.get(this);
    
    GridBagHelper gh = new GridBagHelper(this);
    
    
    gh.setParameter(GridBagHelper.FILL_HORIZONTAL);
    gh.setInsets(new Insets(0, 0, 0, 5));

    lFile = new JLabel("");
    lFile.setForeground(Color.black);

    gh.add(new JLabel(res.getString("report.file")),2,0);
    gh.add(lFile,3,0,1,1,GridBagHelper.GROWFILL_HORIZONTAL);
    
    

    lAuthor = new JLabel("");
    lAuthor.setForeground(Color.black);

    gh.add(new JLabel(res.getString("report.author")),2,1);
    gh.add(lAuthor,3,1,1,1,GridBagHelper.GROWFILL_HORIZONTAL);

    
    lVersion = new JLabel();
    lVersion.setForeground(Color.black);

    gh.add(new JLabel(res.getString("report.version")),2,2);
    gh.add(lVersion,3,2);

    
    tpInfo = new JTextPane();
    tpInfo.setEditable(false);
    tpInfo.setEditorKit(editorKit);
    tpInfo.setFont(new JTextField().getFont()); 
    tpInfo.addHyperlinkListener(new EditorHyperlinkSupport(tpInfo));
    JScrollPane spInfo = new JScrollPane(tpInfo);
    gh.add(new JLabel(res.getString("report.info")),2,3);
    gh.add(spInfo,2,4,2,1,GridBagHelper.GROWFILL_BOTH);

    tpInfo.setText("Some very long info on report from file");

  }
  
  public void setReport(Report report) {
    
    if (report == null) {
      lFile    .setText("");
      lAuthor  .setText("");
      lVersion .setText("");
      tpInfo   .setText("");
    } else {
      editorKit.setFrom(report.getClass());
      lFile    .setText(report.getFile().getName());
      lAuthor  .setText(report.getAuthor());
      lVersion .setText(getReportVersion(report));
      tpInfo   .setText(report.getInfo().replaceAll("\n", "<br>"));
      tpInfo   .setCaretPosition(0);
    }
  }
  
  
  private String getReportVersion(Report report) {
    String version = report.getVersion();
    String update = report.getLastUpdate();
    if (update != null)
      version += " - " + Resources.get(this).getString("report.updated") + ": " + update;
    return version;
  }
    
}
