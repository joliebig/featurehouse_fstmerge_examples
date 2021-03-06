namespace ThoughtWorks.CruiseControl.CCTrayLib.Presentation
{
    partial class BuildParameters
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(BuildParameters));
            this.buildButton = new System.Windows.Forms.Button();
            this.cancelButton = new System.Windows.Forms.Button();
            this.parameters = new System.Windows.Forms.PropertyGrid();
            this.SuspendLayout();
            this.buildButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.buildButton.Location = new System.Drawing.Point(205, 231);
            this.buildButton.Name = "buildButton";
            this.buildButton.Size = new System.Drawing.Size(75, 23);
            this.buildButton.TabIndex = 1;
            this.buildButton.Text = "&Build";
            this.buildButton.UseVisualStyleBackColor = true;
            this.buildButton.Click += new System.EventHandler(this.buildButton_Click);
            this.cancelButton.Anchor = ((System.Windows.Forms.AnchorStyles)((System.Windows.Forms.AnchorStyles.Bottom | System.Windows.Forms.AnchorStyles.Right)));
            this.cancelButton.DialogResult = System.Windows.Forms.DialogResult.Cancel;
            this.cancelButton.Location = new System.Drawing.Point(124, 231);
            this.cancelButton.Name = "cancelButton";
            this.cancelButton.Size = new System.Drawing.Size(75, 23);
            this.cancelButton.TabIndex = 2;
            this.cancelButton.Text = "&Cancel";
            this.cancelButton.UseVisualStyleBackColor = true;
            this.parameters.CommandsVisibleIfAvailable = false;
            this.parameters.Location = new System.Drawing.Point(12, 12);
            this.parameters.Name = "parameters";
            this.parameters.PropertySort = System.Windows.Forms.PropertySort.Alphabetical;
            this.parameters.Size = new System.Drawing.Size(268, 213);
            this.parameters.TabIndex = 3;
            this.parameters.ToolbarVisible = false;
            this.parameters.PropertyValueChanged += new System.Windows.Forms.PropertyValueChangedEventHandler(this.parameters_PropertyValueChanged);
            this.AcceptButton = this.buildButton;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.CancelButton = this.cancelButton;
            this.ClientSize = new System.Drawing.Size(292, 266);
            this.ControlBox = false;
            this.Controls.Add(this.parameters);
            this.Controls.Add(this.cancelButton);
            this.Controls.Add(this.buildButton);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "BuildParameters";
            this.Text = "Build Parameters";
            this.ResumeLayout(false);
        }
        private System.Windows.Forms.Button buildButton;
        private System.Windows.Forms.Button cancelButton;
        private System.Windows.Forms.PropertyGrid parameters;
    }
}
