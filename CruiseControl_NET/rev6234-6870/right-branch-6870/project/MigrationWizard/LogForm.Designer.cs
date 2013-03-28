namespace ThoughtWorks.CruiseControl.MigrationWizard
{
    partial class LogForm
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
            System.Windows.Forms.ColumnHeader columnHeader1;
            System.Windows.Forms.ColumnHeader columnHeader2;
            System.Windows.Forms.ColumnHeader columnHeader3;
            System.Windows.Forms.Button closeButton;
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(LogForm));
            this.messageList = new System.Windows.Forms.ListView();
            columnHeader1 = new System.Windows.Forms.ColumnHeader();
            columnHeader2 = new System.Windows.Forms.ColumnHeader();
            columnHeader3 = new System.Windows.Forms.ColumnHeader();
            closeButton = new System.Windows.Forms.Button();
            this.SuspendLayout();
            columnHeader1.Text = "Type";
            columnHeader1.Width = 65;
            columnHeader2.Text = "Time";
            columnHeader2.Width = 84;
            columnHeader3.Text = "Message";
            columnHeader3.Width = 324;
            closeButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            closeButton.DialogResult = System.Windows.Forms.DialogResult.OK;
            closeButton.Location = new System.Drawing.Point(436, 274);
            closeButton.Name = "closeButton";
            closeButton.Size = new System.Drawing.Size(75, 23);
            closeButton.TabIndex = 1;
            closeButton.Text = "&Close";
            closeButton.UseVisualStyleBackColor = true;
            this.messageList.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.messageList.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            columnHeader1,
            columnHeader2,
            columnHeader3});
            this.messageList.FullRowSelect = true;
            this.messageList.Location = new System.Drawing.Point(12, 12);
            this.messageList.MultiSelect = false;
            this.messageList.Name = "messageList";
            this.messageList.Size = new System.Drawing.Size(499, 256);
            this.messageList.TabIndex = 0;
            this.messageList.UseCompatibleStateImageBehavior = false;
            this.messageList.View = System.Windows.Forms.View.Details;
            this.messageList.DoubleClick += new System.EventHandler(this.messageList_DoubleClick);
            this.AcceptButton = closeButton;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = closeButton;
            this.ClientSize = new System.Drawing.Size(523, 309);
            this.Controls.Add(closeButton);
            this.Controls.Add(this.messageList);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.SizableToolWindow;
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.Name = "LogForm";
            this.ShowInTaskbar = false;
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterParent;
            this.Text = "Migration Log Details";
            this.ResumeLayout(false);
        }
        private System.Windows.Forms.ListView messageList;
    }
}
