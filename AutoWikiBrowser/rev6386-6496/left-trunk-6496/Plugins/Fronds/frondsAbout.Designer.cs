namespace Fronds
{
    partial class FrondsAbout
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(FrondsAbout));
            this.btnAboutOK = new System.Windows.Forms.Button();
            this.lblAbout = new System.Windows.Forms.Label();
            this.SuspendLayout();
            this.btnAboutOK.Location = new System.Drawing.Point(111, 286);
            this.btnAboutOK.Name = "btnAboutOK";
            this.btnAboutOK.Size = new System.Drawing.Size(75, 23);
            this.btnAboutOK.TabIndex = 0;
            this.btnAboutOK.Text = "Okay";
            this.btnAboutOK.UseVisualStyleBackColor = true;
            this.btnAboutOK.Click += new System.EventHandler(this.btnAboutOK_Click);
            this.lblAbout.AutoSize = true;
            this.lblAbout.Font = new System.Drawing.Font("Microsoft Sans Serif", 9F, System.Drawing.FontStyle.Regular, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.lblAbout.Location = new System.Drawing.Point(19, 9);
            this.lblAbout.Name = "lblAbout";
            this.lblAbout.Size = new System.Drawing.Size(266, 270);
            this.lblAbout.TabIndex = 1;
            this.lblAbout.Text = resources.GetString("lblAbout.Text");
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(307, 319);
            this.Controls.Add(this.lblAbout);
            this.Controls.Add(this.btnAboutOK);
            this.MaximumSize = new System.Drawing.Size(323, 355);
            this.MinimumSize = new System.Drawing.Size(323, 355);
            this.Name = "FrondsAbout";
            this.ShowIcon = false;
            this.SizeGripStyle = System.Windows.Forms.SizeGripStyle.Hide;
            this.Text = "Fronds";
            this.Load += new System.EventHandler(this.FrondsAbout_Load);
            this.ResumeLayout(false);
            this.PerformLayout();
        }
        private System.Windows.Forms.Button btnAboutOK;
        private System.Windows.Forms.Label lblAbout;
    }
}
