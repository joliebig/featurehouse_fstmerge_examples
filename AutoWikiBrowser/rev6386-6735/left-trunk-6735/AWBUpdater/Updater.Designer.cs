namespace AwbUpdater
{
    partial class Updater
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
            this.components = new System.ComponentModel.Container();
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(Updater));
            this.progressUpdate = new System.Windows.Forms.ProgressBar();
            this.tmrTimer = new System.Windows.Forms.Timer(this.components);
            this.btnCancel = new System.Windows.Forms.Button();
            this.lstLog = new System.Windows.Forms.ListBox();
            this.lblStatus = new System.Windows.Forms.Label();
            this.SuspendLayout();
            this.progressUpdate.Location = new System.Drawing.Point(12, 124);
            this.progressUpdate.Name = "progressUpdate";
            this.progressUpdate.Size = new System.Drawing.Size(311, 23);
            this.progressUpdate.TabIndex = 0;
            this.tmrTimer.Tick += new System.EventHandler(this.tmrTimer_Tick);
            this.btnCancel.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.btnCancel.Enabled = false;
            this.btnCancel.Location = new System.Drawing.Point(345, 124);
            this.btnCancel.Name = "btnCancel";
            this.btnCancel.Size = new System.Drawing.Size(75, 23);
            this.btnCancel.TabIndex = 4;
            this.btnCancel.Text = "Cancel";
            this.btnCancel.UseVisualStyleBackColor = true;
            this.btnCancel.Click += new System.EventHandler(this.btnCancel_Click);
            this.lstLog.FormattingEnabled = true;
            this.lstLog.Location = new System.Drawing.Point(12, 12);
            this.lstLog.Name = "lstLog";
            this.lstLog.Size = new System.Drawing.Size(408, 95);
            this.lstLog.TabIndex = 5;
            this.lblStatus.AutoSize = true;
            this.lblStatus.Location = new System.Drawing.Point(12, 129);
            this.lblStatus.Name = "lblStatus";
            this.lblStatus.Size = new System.Drawing.Size(47, 13);
            this.lblStatus.TabIndex = 6;
            this.lblStatus.Text = "lblStatus";
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = this.btnCancel;
            this.ClientSize = new System.Drawing.Size(432, 159);
            this.Controls.Add(this.lstLog);
            this.Controls.Add(this.btnCancel);
            this.Controls.Add(this.progressUpdate);
            this.Controls.Add(this.lblStatus);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.FixedSingle;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.Name = "Updater";
            this.Text = "AWB Updater";
            this.Load += new System.EventHandler(this.Updater_Load);
            this.ResumeLayout(false);
            this.PerformLayout();
        }
        private System.Windows.Forms.ProgressBar progressUpdate;
        private System.Windows.Forms.Timer tmrTimer;
        private System.Windows.Forms.Button btnCancel;
        private System.Windows.Forms.ListBox lstLog;
        private System.Windows.Forms.Label lblStatus;
    }
}
