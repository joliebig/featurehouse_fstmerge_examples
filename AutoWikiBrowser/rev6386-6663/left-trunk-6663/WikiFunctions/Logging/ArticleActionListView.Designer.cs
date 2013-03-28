namespace WikiFunctions.Logging
{
    partial class ArticleActionListView
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
            this.colPage = new System.Windows.Forms.ColumnHeader();
            this.colAction = new System.Windows.Forms.ColumnHeader();
            this.colMessage = new System.Windows.Forms.ColumnHeader();
            this.colTime = new System.Windows.Forms.ColumnHeader();
            this.SuspendLayout();
            this.colPage.Text = "Page";
            this.colPage.Width = 58;
            this.colAction.Text = "Action";
            this.colMessage.Text = "Remark";
            this.colTime.Text = "Time";
            this.Anchor = ((System.Windows.Forms.AnchorStyles)(((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.colPage,
            this.colAction,
            this.colTime,
            this.colMessage});
            this.FullRowSelect = true;
            this.Location = new System.Drawing.Point(6, 16);
            this.Name = "lvSuccessful";
            this.ShowItemToolTips = true;
            this.Size = new System.Drawing.Size(246, 115);
            this.SortColumnsOnClick = true;
            this.TabIndex = 20;
            this.UseCompatibleStateImageBehavior = false;
            this.View = System.Windows.Forms.View.Details;
            this.ResumeLayout(false);
        }
        private System.Windows.Forms.ColumnHeader colPage;
        private System.Windows.Forms.ColumnHeader colTime;
        private System.Windows.Forms.ColumnHeader colAction;
        private System.Windows.Forms.ColumnHeader colMessage;
    }
}
