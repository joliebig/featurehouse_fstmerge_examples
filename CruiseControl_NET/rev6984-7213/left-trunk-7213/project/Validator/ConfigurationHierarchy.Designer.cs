namespace Validator
{
    partial class ConfigurationHierarchy
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(ConfigurationHierarchy));
            this.splitContainer1 = new System.Windows.Forms.SplitContainer();
            this.hierarchy = new System.Windows.Forms.TreeView();
            this.localImages = new System.Windows.Forms.ImageList(this.components);
            this.itemDetails = new System.Windows.Forms.PropertyGrid();
            this.splitContainer1.Panel1.SuspendLayout();
            this.splitContainer1.Panel2.SuspendLayout();
            this.splitContainer1.SuspendLayout();
            this.SuspendLayout();
            this.splitContainer1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.splitContainer1.Location = new System.Drawing.Point(0, 0);
            this.splitContainer1.Name = "splitContainer1";
            this.splitContainer1.Panel1.Controls.Add(this.hierarchy);
            this.splitContainer1.Panel2.Controls.Add(this.itemDetails);
            this.splitContainer1.Size = new System.Drawing.Size(525, 288);
            this.splitContainer1.SplitterDistance = 227;
            this.splitContainer1.TabIndex = 0;
            this.hierarchy.Dock = System.Windows.Forms.DockStyle.Fill;
            this.hierarchy.FullRowSelect = true;
            this.hierarchy.HideSelection = false;
            this.hierarchy.ImageIndex = 3;
            this.hierarchy.ImageList = this.localImages;
            this.hierarchy.Location = new System.Drawing.Point(0, 0);
            this.hierarchy.Name = "hierarchy";
            this.hierarchy.SelectedImageIndex = 3;
            this.hierarchy.ShowNodeToolTips = true;
            this.hierarchy.Size = new System.Drawing.Size(227, 288);
            this.hierarchy.TabIndex = 0;
            this.hierarchy.AfterSelect += new System.Windows.Forms.TreeViewEventHandler(this.hierarchy_AfterSelect);
            this.localImages.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("localImages.ImageStream")));
            this.localImages.TransparentColor = System.Drawing.Color.Transparent;
            this.localImages.Images.SetKeyName(0, "queue");
            this.localImages.Images.SetKeyName(1, "project");
            this.localImages.Images.SetKeyName(2, "security");
            this.localImages.Images.SetKeyName(3, "root");
            this.localImages.Images.SetKeyName(4, "sourcecontrol");
            this.localImages.Images.SetKeyName(5, "tasks");
            this.localImages.Images.SetKeyName(6, "task");
            this.localImages.Images.SetKeyName(7, "triggers");
            this.localImages.Images.SetKeyName(8, "trigger");
            this.localImages.Images.SetKeyName(9, "permissions");
            this.localImages.Images.SetKeyName(10, "permission");
            this.localImages.Images.SetKeyName(11, "users");
            this.localImages.Images.SetKeyName(12, "user");
            this.itemDetails.Dock = System.Windows.Forms.DockStyle.Fill;
            this.itemDetails.Location = new System.Drawing.Point(0, 0);
            this.itemDetails.Name = "itemDetails";
            this.itemDetails.Size = new System.Drawing.Size(294, 288);
            this.itemDetails.TabIndex = 0;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this.splitContainer1);
            this.Name = "ConfigurationHierarchy";
            this.Size = new System.Drawing.Size(525, 288);
            this.splitContainer1.Panel1.ResumeLayout(false);
            this.splitContainer1.Panel2.ResumeLayout(false);
            this.splitContainer1.ResumeLayout(false);
            this.ResumeLayout(false);
        }
        private System.Windows.Forms.SplitContainer splitContainer1;
        private System.Windows.Forms.TreeView hierarchy;
        private System.Windows.Forms.PropertyGrid itemDetails;
        private System.Windows.Forms.ImageList localImages;
    }
}
