namespace WikiFunctions.Logging
{
    partial class UploadingPleaseWaitForm
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(UploadingPleaseWaitForm));
            this.PictureBox1 = new System.Windows.Forms.PictureBox();
            this.Label1 = new System.Windows.Forms.Label();
            ((System.ComponentModel.ISupportInitialize)(this.PictureBox1)).BeginInit();
            this.SuspendLayout();
            this.PictureBox1.Image = ((System.Drawing.Image)(resources.GetObject("PictureBox1.Image")));
            this.PictureBox1.Location = new System.Drawing.Point(12, 12);
            this.PictureBox1.Name = "PictureBox1";
            this.PictureBox1.Size = new System.Drawing.Size(70, 70);
            this.PictureBox1.TabIndex = 1;
            this.PictureBox1.TabStop = false;
            this.Label1.AutoSize = true;
            this.Label1.Location = new System.Drawing.Point(88, 22);
            this.Label1.Name = "Label1";
            this.Label1.Size = new System.Drawing.Size(191, 13);
            this.Label1.TabIndex = 2;
            this.Label1.Text = "Your log is being uploaded, please wait";
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(292, 95);
            this.ControlBox = false;
            this.Controls.Add(this.Label1);
            this.Controls.Add(this.PictureBox1);
            this.Name = "UploadingPleaseWaitForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.CenterScreen;
            this.Text = "Uploading...";
            ((System.ComponentModel.ISupportInitialize)(this.PictureBox1)).EndInit();
            base.Shown += new System.EventHandler(this.Form_Shown);
            base.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.Form_Closing);
            this.ResumeLayout(false);
            this.PerformLayout();
        }
        protected internal System.Windows.Forms.PictureBox PictureBox1;
        protected internal System.Windows.Forms.Label Label1;
    }
}
