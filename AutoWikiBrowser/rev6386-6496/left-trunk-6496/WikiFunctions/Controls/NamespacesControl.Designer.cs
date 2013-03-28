namespace WikiFunctions.Controls
{
    partial class NamespacesControl
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
            this.chkContents = new System.Windows.Forms.CheckBox();
            this.checkedLBContent = new System.Windows.Forms.CheckedListBox();
            this.chkTalk = new System.Windows.Forms.CheckBox();
            this.checkedLBTalk = new System.Windows.Forms.CheckedListBox();
            this.splitContainer1 = new System.Windows.Forms.SplitContainer();
            this.splitContainer1.Panel1.SuspendLayout();
            this.splitContainer1.Panel2.SuspendLayout();
            this.splitContainer1.SuspendLayout();
            this.SuspendLayout();
            this.chkContents.AutoSize = true;
            this.chkContents.Location = new System.Drawing.Point(0, 0);
            this.chkContents.Name = "chkContents";
            this.chkContents.Size = new System.Drawing.Size(63, 17);
            this.chkContents.TabIndex = 7;
            this.chkContents.Tag = "1000";
            this.chkContents.Text = "&Content";
            this.chkContents.CheckedChanged += new System.EventHandler(this.chkContents_CheckedChanged);
            this.checkedLBContent.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.checkedLBContent.FormattingEnabled = true;
            this.checkedLBContent.Location = new System.Drawing.Point(0, 23);
            this.checkedLBContent.Name = "checkedLBContent";
            this.checkedLBContent.Size = new System.Drawing.Size(104, 214);
            this.checkedLBContent.TabIndex = 9;
            this.chkTalk.AutoSize = true;
            this.chkTalk.Location = new System.Drawing.Point(1, 0);
            this.chkTalk.Name = "chkTalk";
            this.chkTalk.Size = new System.Drawing.Size(47, 17);
            this.chkTalk.TabIndex = 8;
            this.chkTalk.Tag = "1001";
            this.chkTalk.Text = "&Talk";
            this.chkTalk.CheckedChanged += new System.EventHandler(this.chkTalk_CheckedChanged);
            this.checkedLBTalk.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.checkedLBTalk.FormattingEnabled = true;
            this.checkedLBTalk.Location = new System.Drawing.Point(1, 23);
            this.checkedLBTalk.Name = "checkedLBTalk";
            this.checkedLBTalk.Size = new System.Drawing.Size(108, 214);
            this.checkedLBTalk.TabIndex = 10;
            this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.splitContainer1.Location = new System.Drawing.Point(0, 0);
            this.splitContainer1.Name = "splitContainer1";
            this.splitContainer1.Panel1.Controls.Add(this.chkContents);
            this.splitContainer1.Panel1.Controls.Add(this.checkedLBContent);
            this.splitContainer1.Panel2.Controls.Add(this.chkTalk);
            this.splitContainer1.Panel2.Controls.Add(this.checkedLBTalk);
            this.splitContainer1.Size = new System.Drawing.Size(221, 246);
            this.splitContainer1.SplitterDistance = 105;
            this.splitContainer1.TabIndex = 11;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this.splitContainer1);
            this.Name = "NamespacesControl";
            this.Size = new System.Drawing.Size(221, 246);
            this.splitContainer1.Panel1.ResumeLayout(false);
            this.splitContainer1.Panel1.PerformLayout();
            this.splitContainer1.Panel2.ResumeLayout(false);
            this.splitContainer1.Panel2.PerformLayout();
            this.splitContainer1.ResumeLayout(false);
            this.ResumeLayout(false);
        }
        private System.Windows.Forms.CheckBox chkContents;
        private System.Windows.Forms.CheckedListBox checkedLBContent;
        private System.Windows.Forms.CheckBox chkTalk;
        private System.Windows.Forms.CheckedListBox checkedLBTalk;
        private System.Windows.Forms.SplitContainer splitContainer1;
    }
}
