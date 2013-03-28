namespace WorldWind
{
    partial class TimeSetterDialog
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
            this.dateTimePicker1 = new System.Windows.Forms.DateTimePicker();
            this.checkBoxUTC = new System.Windows.Forms.CheckBox();
            this.SuspendLayout();
            this.dateTimePicker1.CustomFormat = "yyyy-MM-dd HH:mm:ss";
            this.dateTimePicker1.Format = System.Windows.Forms.DateTimePickerFormat.Custom;
            this.dateTimePicker1.Location = new System.Drawing.Point(46, 12);
            this.dateTimePicker1.Name = "dateTimePicker1";
            this.dateTimePicker1.Size = new System.Drawing.Size(200, 20);
            this.dateTimePicker1.TabIndex = 1;
            this.dateTimePicker1.ValueChanged += new System.EventHandler(this.dateTimePicker1_ValueChanged);
            this.checkBoxUTC.AutoSize = true;
            this.checkBoxUTC.Location = new System.Drawing.Point(46, 39);
            this.checkBoxUTC.Name = "checkBoxUTC";
            this.checkBoxUTC.Size = new System.Drawing.Size(96, 17);
            this.checkBoxUTC.TabIndex = 2;
            this.checkBoxUTC.Text = "Universal Time";
            this.checkBoxUTC.UseVisualStyleBackColor = true;
            this.checkBoxUTC.CheckedChanged += new System.EventHandler(this.checkBoxUTC_CheckedChanged);
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(287, 68);
            this.Controls.Add(this.checkBoxUTC);
            this.Controls.Add(this.dateTimePicker1);
            this.Name = "TimeSetterDialog";
            this.Text = "Time Setter Dialog";
            this.ResumeLayout(false);
            this.PerformLayout();
        }
        private System.Windows.Forms.DateTimePicker dateTimePicker1;
        private System.Windows.Forms.CheckBox checkBoxUTC;
    }
}
