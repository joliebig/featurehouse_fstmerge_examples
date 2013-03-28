namespace OVT.FireIRC.Resources
{
    partial class ChannelWindow
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
            System.Windows.Forms.ListViewGroup listViewGroup1 = new System.Windows.Forms.ListViewGroup("Operators", System.Windows.Forms.HorizontalAlignment.Left);
            System.Windows.Forms.ListViewGroup listViewGroup2 = new System.Windows.Forms.ListViewGroup("Half-Operators", System.Windows.Forms.HorizontalAlignment.Left);
            System.Windows.Forms.ListViewGroup listViewGroup3 = new System.Windows.Forms.ListViewGroup("Voiced", System.Windows.Forms.HorizontalAlignment.Left);
            System.Windows.Forms.ListViewGroup listViewGroup4 = new System.Windows.Forms.ListViewGroup("Normal", System.Windows.Forms.HorizontalAlignment.Left);
            this.panel1 = new System.Windows.Forms.Panel();
            this.textBox3 = new System.Windows.Forms.TextBox();
            this.textBox2 = new System.Windows.Forms.TextBox();
            this.listView1 = new System.Windows.Forms.ListView();
            this.columnHeader1 = new System.Windows.Forms.ColumnHeader();
            this.columnHeader2 = new System.Windows.Forms.ColumnHeader();
            this.splitter1 = new System.Windows.Forms.Splitter();
            this.label1 = new System.Windows.Forms.Label();
            this.textBox1 = new System.Windows.Forms.ComboBox();
            this.richTextBox1 = new System.Windows.Forms.RichTextBox();
            this.button1 = new System.Windows.Forms.Button();
            this.fireIRCChat1 = new OVT.FireIRC.ExperimentalChatControl.FireIRCChat();
            this.panel1.SuspendLayout();
            this.SuspendLayout();
            this.panel1.Controls.Add(this.textBox3);
            this.panel1.Controls.Add(this.textBox2);
            this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
            this.panel1.Location = new System.Drawing.Point(0, 0);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(753, 20);
            this.panel1.TabIndex = 1;
            this.textBox3.Dock = System.Windows.Forms.DockStyle.Fill;
            this.textBox3.Location = new System.Drawing.Point(0, 0);
            this.textBox3.Name = "textBox3";
            this.textBox3.ReadOnly = true;
            this.textBox3.Size = new System.Drawing.Size(653, 20);
            this.textBox3.TabIndex = 1;
            this.textBox2.Dock = System.Windows.Forms.DockStyle.Right;
            this.textBox2.Location = new System.Drawing.Point(653, 0);
            this.textBox2.Name = "textBox2";
            this.textBox2.ReadOnly = true;
            this.textBox2.Size = new System.Drawing.Size(100, 20);
            this.textBox2.TabIndex = 0;
            this.listView1.Columns.AddRange(new System.Windows.Forms.ColumnHeader[] {
            this.columnHeader1,
            this.columnHeader2});
            this.listView1.Dock = System.Windows.Forms.DockStyle.Right;
            listViewGroup1.Header = "Operators";
            listViewGroup1.Name = "Operators";
            listViewGroup2.Header = "Half-Operators";
            listViewGroup2.Name = "Half-Operators";
            listViewGroup3.Header = "Voiced";
            listViewGroup3.Name = "Voiced";
            listViewGroup4.Header = "Normal";
            listViewGroup4.Name = "Normal";
            this.listView1.Groups.AddRange(new System.Windows.Forms.ListViewGroup[] {
            listViewGroup1,
            listViewGroup2,
            listViewGroup3,
            listViewGroup4});
            this.listView1.Location = new System.Drawing.Point(578, 20);
            this.listView1.MultiSelect = false;
            this.listView1.Name = "listView1";
            this.listView1.Size = new System.Drawing.Size(175, 472);
            this.listView1.Sorting = System.Windows.Forms.SortOrder.Ascending;
            this.listView1.TabIndex = 3;
            this.listView1.UseCompatibleStateImageBehavior = false;
            this.listView1.View = System.Windows.Forms.View.Details;
            this.listView1.SelectedIndexChanged += new System.EventHandler(this.listView1_SelectedIndexChanged);
            this.listView1.DoubleClick += new System.EventHandler(this.listView1_DoubleClick);
            this.columnHeader1.Text = "{FireIRC.NickName}";
            this.columnHeader1.Width = 166;
            this.columnHeader2.Text = "{FireIRC.HostMask}";
            this.columnHeader2.Width = 150;
            this.splitter1.Dock = System.Windows.Forms.DockStyle.Right;
            this.splitter1.Location = new System.Drawing.Point(575, 20);
            this.splitter1.Name = "splitter1";
            this.splitter1.Size = new System.Drawing.Size(3, 472);
            this.splitter1.TabIndex = 4;
            this.splitter1.TabStop = false;
            this.label1.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.label1.Dock = System.Windows.Forms.DockStyle.Right;
            this.label1.Location = new System.Drawing.Point(564, 20);
            this.label1.Name = "label1";
            this.label1.Size = new System.Drawing.Size(11, 472);
            this.label1.TabIndex = 5;
            this.label1.Text = ">";
            this.label1.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.label1.DoubleClick += new System.EventHandler(this.label1_DoubleClick);
            this.label1.Click += new System.EventHandler(this.label1_Click);
            this.textBox1.Dock = System.Windows.Forms.DockStyle.Bottom;
            this.textBox1.FormattingEnabled = true;
            this.textBox1.Location = new System.Drawing.Point(0, 492);
            this.textBox1.Name = "textBox1";
            this.textBox1.Size = new System.Drawing.Size(753, 21);
            this.textBox1.TabIndex = 6;
            this.textBox1.KeyDown += new System.Windows.Forms.KeyEventHandler(this.textBox1_KeyDown);
            this.richTextBox1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.richTextBox1.Location = new System.Drawing.Point(0, 20);
            this.richTextBox1.Name = "richTextBox1";
            this.richTextBox1.ReadOnly = true;
            this.richTextBox1.ScrollBars = System.Windows.Forms.RichTextBoxScrollBars.ForcedVertical;
            this.richTextBox1.Size = new System.Drawing.Size(564, 472);
            this.richTextBox1.TabIndex = 1;
            this.richTextBox1.Text = "";
            this.richTextBox1.LinkClicked += new System.Windows.Forms.LinkClickedEventHandler(this.richTextBox1_LinkClicked);
            this.richTextBox1.DoubleClick += new System.EventHandler(this.richTextBox1_DoubleClick);
            this.richTextBox1.TextChanged += new System.EventHandler(this.richTextBox1_TextChanged);
            this.button1.Location = new System.Drawing.Point(0, 0);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(0, 0);
            this.button1.TabIndex = 7;
            this.button1.Text = "button1";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            this.fireIRCChat1.BackColor = System.Drawing.Color.White;
            this.fireIRCChat1.Cleared = true;
            this.fireIRCChat1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.fireIRCChat1.Location = new System.Drawing.Point(0, 20);
            this.fireIRCChat1.Name = "fireIRCChat1";
            this.fireIRCChat1.Size = new System.Drawing.Size(564, 472);
            this.fireIRCChat1.TabIndex = 8;
            this.fireIRCChat1.Visible = false;
            this.AcceptButton = this.button1;
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(753, 513);
            this.Controls.Add(this.fireIRCChat1);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.richTextBox1);
            this.Controls.Add(this.label1);
            this.Controls.Add(this.splitter1);
            this.Controls.Add(this.listView1);
            this.Controls.Add(this.textBox1);
            this.Controls.Add(this.panel1);
            this.Name = "ChannelWindow";
            this.Text = "ChannelWindow";
            this.Load += new System.EventHandler(this.ChannelWindow_Load);
            this.FormClosing += new System.Windows.Forms.FormClosingEventHandler(this.ChannelWindow_FormClosing);
            this.Resize += new System.EventHandler(this.ChannelWindow_Resize);
            this.panel1.ResumeLayout(false);
            this.panel1.PerformLayout();
            this.ResumeLayout(false);
        }
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.TextBox textBox3;
        private System.Windows.Forms.TextBox textBox2;
        private System.Windows.Forms.ListView listView1;
        private System.Windows.Forms.Splitter splitter1;
        private System.Windows.Forms.ColumnHeader columnHeader1;
        private System.Windows.Forms.Label label1;
        private System.Windows.Forms.ComboBox textBox1;
        private System.Windows.Forms.RichTextBox richTextBox1;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.ColumnHeader columnHeader2;
        private OVT.FireIRC.ExperimentalChatControl.FireIRCChat fireIRCChat1;
    }
}
