namespace Validator
{
    partial class VersionInformationForm
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
            System.Windows.Forms.Label label1;
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(VersionInformationForm));
            this.closeButton = new System.Windows.Forms.Button();
            this.versionInformation = new System.Windows.Forms.TreeView();
            this.imageList1 = new System.Windows.Forms.ImageList(this.components);
            label1 = new System.Windows.Forms.Label();
            this.SuspendLayout();
            label1.AutoSize = true;
            label1.Location = new System.Drawing.Point(12, 9);
            label1.Name = "label1";
            label1.Size = new System.Drawing.Size(200, 13);
            label1.TabIndex = 0;
            label1.Text = "The following libraries have been loaded:";
            this.closeButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.closeButton.DialogResult = System.Windows.Forms.DialogResult.OK;
            this.closeButton.Location = new System.Drawing.Point(197, 229);
            this.closeButton.Name = "closeButton";
            this.closeButton.Size = new System.Drawing.Size(75, 23);
            this.closeButton.TabIndex = 2;
            this.closeButton.Text = "&Close";
            this.closeButton.UseVisualStyleBackColor = true;
            this.closeButton.Click += new System.EventHandler(this.closeButton_Click);
            this.versionInformation.Anchor = ((System.Windows.Forms.AnchorStyles)((((System.Windows.Forms.AnchorStyles.Top | System.Windows.Forms.AnchorStyles.Bottom)
                        | System.Windows.Forms.AnchorStyles.Left)
                        | System.Windows.Forms.AnchorStyles.Right)));
            this.versionInformation.FullRowSelect = true;
            this.versionInformation.ImageIndex = 0;
            this.versionInformation.ImageList = this.imageList1;
            this.versionInformation.Location = new System.Drawing.Point(12, 25);
            this.versionInformation.Name = "versionInformation";
            this.versionInformation.SelectedImageIndex = 0;
            this.versionInformation.Size = new System.Drawing.Size(260, 198);
            this.versionInformation.TabIndex = 3;
            this.imageList1.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("imageList1.ImageStream")));
            this.imageList1.TransparentColor = System.Drawing.Color.Transparent;
            this.imageList1.Images.SetKeyName(0, "assembly");
            this.imageList1.Images.SetKeyName(1, "type");
            this.AcceptButton = this.closeButton;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = this.closeButton;
            this.ClientSize = new System.Drawing.Size(284, 264);
            this.Controls.Add(this.versionInformation);
            this.Controls.Add(this.closeButton);
            this.Controls.Add(label1);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.SizableToolWindow;
            this.Name = "VersionInformationForm";
            this.ShowInTaskbar = false;
            this.Text = "Version Information";
            this.ResumeLayout(false);
            this.PerformLayout();
        }
        private System.Windows.Forms.Button closeButton;
        private System.Windows.Forms.TreeView versionInformation;
        private System.Windows.Forms.ImageList imageList1;
    }
}
