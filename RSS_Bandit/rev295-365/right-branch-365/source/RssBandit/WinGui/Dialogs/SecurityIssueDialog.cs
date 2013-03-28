using System.Windows.Forms;
namespace RssBandit.WinGui.Dialogs
{
 internal partial class SecurityIssueDialog : System.Windows.Forms.Form
 {
  private System.Windows.Forms.Button buttonYes;
  private System.Windows.Forms.Button buttonNo;
  private System.Windows.Forms.Label labelIssueCaption;
  private System.Windows.Forms.Label labelProceedMessage;
  private System.Windows.Forms.Label labelIssueDescription;
  private System.Windows.Forms.Label horizontalEdge;
  internal System.Windows.Forms.Button CustomCommand;
  private System.Windows.Forms.Label labelCaptionImage;
  private System.Windows.Forms.Label labelAttentionImage;
  private System.ComponentModel.Container components = null;
  public SecurityIssueDialog(string issueCaption, string issueDescription)
  {
   InitializeComponent();
   labelIssueCaption.Text = issueCaption;
   if (issueDescription != null) {
    labelIssueDescription.Text = issueDescription;
   } else {
    labelIssueDescription.Visible = labelAttentionImage.Visible = false;
   }
   CustomCommand.Visible = false;
  }
  protected override void Dispose( bool disposing )
  {
   if( disposing )
   {
    if(components != null)
    {
     components.Dispose();
    }
   }
   base.Dispose( disposing );
  }
  private void buttonYes_Click(object sender, System.EventArgs e) {
   this.DialogResult = DialogResult.OK;
   this.Close();
  }
 }
}
