namespace ThoughtWorks.CruiseControl.CCTrayLib.Presentation
{
 partial class ExecSettingsControl
 {
  private System.ComponentModel.IContainer components = null;
  protected override void Dispose(bool disposing)
  {
   if (disposing && (components != null))
   {
    components.Dispose();
   }
   base.Dispose(disposing);
  }
  private void InitializeComponent()
  {
      this.txtExecSuccess = new System.Windows.Forms.TextBox();
      this.txtExecBroken = new System.Windows.Forms.TextBox();
      this.txtExecBrokenAndBuilding = new System.Windows.Forms.TextBox();
      this.txtExecBuilding = new System.Windows.Forms.TextBox();
      this.txtExecNotConnected = new System.Windows.Forms.TextBox();
      this.dlgOpenFile = new System.Windows.Forms.OpenFileDialog();
      this.label1 = new System.Windows.Forms.Label();
      this.label2 = new System.Windows.Forms.Label();
      this.label3 = new System.Windows.Forms.Label();
      this.label4 = new System.Windows.Forms.Label();
      this.label5 = new System.Windows.Forms.Label();
      this.label6 = new System.Windows.Forms.Label();
      this.SuspendLayout();
      this.txtExecSuccess.Location = new System.Drawing.Point(160, 16);
      this.txtExecSuccess.Name = "txtExecSuccess";
      this.txtExecSuccess.Size = new System.Drawing.Size(353, 20);
      this.txtExecSuccess.TabIndex = 1;
      this.txtExecBroken.Location = new System.Drawing.Point(160, 42);
      this.txtExecBroken.Name = "txtExecBroken";
      this.txtExecBroken.Size = new System.Drawing.Size(353, 20);
      this.txtExecBroken.TabIndex = 4;
      this.txtExecBrokenAndBuilding.Location = new System.Drawing.Point(160, 68);
      this.txtExecBrokenAndBuilding.Name = "txtExecBrokenAndBuilding";
      this.txtExecBrokenAndBuilding.Size = new System.Drawing.Size(353, 20);
      this.txtExecBrokenAndBuilding.TabIndex = 7;
      this.txtExecBuilding.Location = new System.Drawing.Point(160, 94);
      this.txtExecBuilding.Name = "txtExecBuilding";
      this.txtExecBuilding.Size = new System.Drawing.Size(353, 20);
      this.txtExecBuilding.TabIndex = 10;
      this.txtExecNotConnected.Location = new System.Drawing.Point(160, 120);
      this.txtExecNotConnected.Name = "txtExecNotConnected";
      this.txtExecNotConnected.Size = new System.Drawing.Size(353, 20);
      this.txtExecNotConnected.TabIndex = 13;
      this.dlgOpenFile.Filter = "All Files|*.*";
      this.label1.AutoSize = true;
      this.label1.Location = new System.Drawing.Point(64, 168);
      this.label1.Name = "label1";
      this.label1.Size = new System.Drawing.Size(414, 13);
      this.label1.TabIndex = 15;
      this.label1.Text = "If specified, the commands will be run when the system enters the corresponding s" +
          "tate.\r\n";
      this.label2.AutoSize = true;
      this.label2.Location = new System.Drawing.Point(31, 19);
      this.label2.Name = "label2";
      this.label2.Size = new System.Drawing.Size(48, 13);
      this.label2.TabIndex = 16;
      this.label2.Text = "Success";
      this.label3.AutoSize = true;
      this.label3.Location = new System.Drawing.Point(31, 45);
      this.label3.Name = "label3";
      this.label3.Size = new System.Drawing.Size(41, 13);
      this.label3.TabIndex = 17;
      this.label3.Text = "Broken";
      this.label4.AutoSize = true;
      this.label4.Location = new System.Drawing.Point(31, 71);
      this.label4.Name = "label4";
      this.label4.Size = new System.Drawing.Size(101, 13);
      this.label4.TabIndex = 18;
      this.label4.Text = "Broken and building";
      this.label5.AutoSize = true;
      this.label5.Location = new System.Drawing.Point(31, 97);
      this.label5.Name = "label5";
      this.label5.Size = new System.Drawing.Size(44, 13);
      this.label5.TabIndex = 19;
      this.label5.Text = "Building";
      this.label6.AutoSize = true;
      this.label6.Location = new System.Drawing.Point(31, 123);
      this.label6.Name = "label6";
      this.label6.Size = new System.Drawing.Size(78, 13);
      this.label6.TabIndex = 20;
      this.label6.Text = "Not connected";
      this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Inherit;
      this.Controls.Add(this.label6);
      this.Controls.Add(this.label5);
      this.Controls.Add(this.label4);
      this.Controls.Add(this.label3);
      this.Controls.Add(this.label2);
      this.Controls.Add(this.label1);
      this.Controls.Add(this.txtExecNotConnected);
      this.Controls.Add(this.txtExecBuilding);
      this.Controls.Add(this.txtExecBrokenAndBuilding);
      this.Controls.Add(this.txtExecBroken);
      this.Controls.Add(this.txtExecSuccess);
      this.Name = "ExecSettingsControl";
      this.Size = new System.Drawing.Size(667, 289);
      this.ResumeLayout(false);
      this.PerformLayout();
  }
    private System.Windows.Forms.TextBox txtExecSuccess;
    private System.Windows.Forms.TextBox txtExecBroken;
    private System.Windows.Forms.TextBox txtExecBrokenAndBuilding;
    private System.Windows.Forms.TextBox txtExecBuilding;
    private System.Windows.Forms.TextBox txtExecNotConnected;
  private System.Windows.Forms.OpenFileDialog dlgOpenFile;
    private System.Windows.Forms.Label label1;
    private System.Windows.Forms.Label label2;
    private System.Windows.Forms.Label label3;
    private System.Windows.Forms.Label label4;
    private System.Windows.Forms.Label label5;
    private System.Windows.Forms.Label label6;
 }
}
