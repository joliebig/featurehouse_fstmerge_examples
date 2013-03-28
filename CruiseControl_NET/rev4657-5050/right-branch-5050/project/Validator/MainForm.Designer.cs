namespace Validator
{
    partial class MainForm
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
            System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(MainForm));
            this.menuStrip1 = new System.Windows.Forms.MenuStrip();
            this.fileToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.openMenuButton = new System.Windows.Forms.ToolStripMenuItem();
            this.reloadMenuButton = new System.Windows.Forms.ToolStripMenuItem();
            this.historyMenu = new System.Windows.Forms.ToolStripMenuItem();
            this.emptyToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripMenuItem1 = new System.Windows.Forms.ToolStripSeparator();
            this.printMenuButton = new System.Windows.Forms.ToolStripMenuItem();
            this.toolStripMenuItem2 = new System.Windows.Forms.ToolStripSeparator();
            this.exitMenuButton = new System.Windows.Forms.ToolStripMenuItem();
            this.viewToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.configurationToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.vericalToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.horizontalToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.offToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.helpToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.aboutToolStripMenuItem = new System.Windows.Forms.ToolStripMenuItem();
            this.statusStrip1 = new System.Windows.Forms.StatusStrip();
            this.progressLabel = new System.Windows.Forms.ToolStripStatusLabel();
            this.progressBar = new System.Windows.Forms.ToolStripProgressBar();
            this.resultsDisplay = new System.Windows.Forms.SplitContainer();
            this.validationResults = new System.Windows.Forms.WebBrowser();
            this.tabControl1 = new System.Windows.Forms.TabControl();
            this.tabPage1 = new System.Windows.Forms.TabPage();
            this.xmlDisplay = new System.Windows.Forms.WebBrowser();
            this.tabPage2 = new System.Windows.Forms.TabPage();
            this.processedDisplay = new System.Windows.Forms.WebBrowser();
            this.codeFormatter = new System.ComponentModel.BackgroundWorker();
            this.menuStrip1.SuspendLayout();
            this.statusStrip1.SuspendLayout();
            this.resultsDisplay.Panel1.SuspendLayout();
            this.resultsDisplay.Panel2.SuspendLayout();
            this.resultsDisplay.SuspendLayout();
            this.tabControl1.SuspendLayout();
            this.tabPage1.SuspendLayout();
            this.tabPage2.SuspendLayout();
            this.SuspendLayout();
            this.menuStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.fileToolStripMenuItem,
            this.viewToolStripMenuItem,
            this.helpToolStripMenuItem});
            this.menuStrip1.Location = new System.Drawing.Point(0, 0);
            this.menuStrip1.Name = "menuStrip1";
            this.menuStrip1.Size = new System.Drawing.Size(686, 24);
            this.menuStrip1.TabIndex = 0;
            this.menuStrip1.Text = "menuStrip1";
            this.fileToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.openMenuButton,
            this.reloadMenuButton,
            this.historyMenu,
            this.toolStripMenuItem1,
            this.printMenuButton,
            this.toolStripMenuItem2,
            this.exitMenuButton});
            this.fileToolStripMenuItem.Name = "fileToolStripMenuItem";
            this.fileToolStripMenuItem.Size = new System.Drawing.Size(37, 20);
            this.fileToolStripMenuItem.Text = "&File";
            this.openMenuButton.Name = "openMenuButton";
            this.openMenuButton.Size = new System.Drawing.Size(112, 22);
            this.openMenuButton.Text = "&Open...";
            this.openMenuButton.Click += new System.EventHandler(this.openMenuButton_Click);
            this.reloadMenuButton.Name = "reloadMenuButton";
            this.reloadMenuButton.Size = new System.Drawing.Size(112, 22);
            this.reloadMenuButton.Text = "&Reload";
            this.reloadMenuButton.Click += new System.EventHandler(this.reloadMenuButton_Click);
            this.historyMenu.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.emptyToolStripMenuItem});
            this.historyMenu.Name = "historyMenu";
            this.historyMenu.Size = new System.Drawing.Size(112, 22);
            this.historyMenu.Text = "&History";
            this.emptyToolStripMenuItem.Font = new System.Drawing.Font("Tahoma", 8.25F, System.Drawing.FontStyle.Italic);
            this.emptyToolStripMenuItem.Name = "emptyToolStripMenuItem";
            this.emptyToolStripMenuItem.Size = new System.Drawing.Size(120, 22);
            this.emptyToolStripMenuItem.Text = "<empty>";
            this.toolStripMenuItem1.Name = "toolStripMenuItem1";
            this.toolStripMenuItem1.Size = new System.Drawing.Size(109, 6);
            this.printMenuButton.Name = "printMenuButton";
            this.printMenuButton.Size = new System.Drawing.Size(112, 22);
            this.printMenuButton.Text = "&Print...";
            this.printMenuButton.Click += new System.EventHandler(this.printMenuButton_Click);
            this.toolStripMenuItem2.Name = "toolStripMenuItem2";
            this.toolStripMenuItem2.Size = new System.Drawing.Size(109, 6);
            this.exitMenuButton.Name = "exitMenuButton";
            this.exitMenuButton.Size = new System.Drawing.Size(112, 22);
            this.exitMenuButton.Text = "E&xit";
            this.exitMenuButton.Click += new System.EventHandler(this.exitMenuButton_Click);
            this.viewToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.configurationToolStripMenuItem});
            this.viewToolStripMenuItem.Name = "viewToolStripMenuItem";
            this.viewToolStripMenuItem.Size = new System.Drawing.Size(44, 20);
            this.viewToolStripMenuItem.Text = "&View";
            this.configurationToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.vericalToolStripMenuItem,
            this.horizontalToolStripMenuItem,
            this.offToolStripMenuItem});
            this.configurationToolStripMenuItem.Name = "configurationToolStripMenuItem";
            this.configurationToolStripMenuItem.Size = new System.Drawing.Size(148, 22);
            this.configurationToolStripMenuItem.Text = "Configuration";
            this.vericalToolStripMenuItem.Checked = true;
            this.vericalToolStripMenuItem.CheckState = System.Windows.Forms.CheckState.Checked;
            this.vericalToolStripMenuItem.Name = "vericalToolStripMenuItem";
            this.vericalToolStripMenuItem.Size = new System.Drawing.Size(129, 22);
            this.vericalToolStripMenuItem.Text = "Verical";
            this.vericalToolStripMenuItem.Click += new System.EventHandler(this.vericalToolStripMenuItem_Click);
            this.horizontalToolStripMenuItem.Name = "horizontalToolStripMenuItem";
            this.horizontalToolStripMenuItem.Size = new System.Drawing.Size(129, 22);
            this.horizontalToolStripMenuItem.Text = "Horizontal";
            this.horizontalToolStripMenuItem.Click += new System.EventHandler(this.horizontalToolStripMenuItem_Click);
            this.offToolStripMenuItem.Name = "offToolStripMenuItem";
            this.offToolStripMenuItem.Size = new System.Drawing.Size(129, 22);
            this.offToolStripMenuItem.Text = "Off";
            this.offToolStripMenuItem.Click += new System.EventHandler(this.offToolStripMenuItem_Click);
            this.helpToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.aboutToolStripMenuItem});
            this.helpToolStripMenuItem.Name = "helpToolStripMenuItem";
            this.helpToolStripMenuItem.Size = new System.Drawing.Size(44, 20);
            this.helpToolStripMenuItem.Text = "&Help";
            this.aboutToolStripMenuItem.Name = "aboutToolStripMenuItem";
            this.aboutToolStripMenuItem.Size = new System.Drawing.Size(116, 22);
            this.aboutToolStripMenuItem.Text = "&About...";
            this.aboutToolStripMenuItem.Click += new System.EventHandler(this.aboutToolStripMenuItem_Click);
            this.statusStrip1.Items.AddRange(new System.Windows.Forms.ToolStripItem[] {
            this.progressLabel,
            this.progressBar});
            this.statusStrip1.Location = new System.Drawing.Point(0, 358);
            this.statusStrip1.Name = "statusStrip1";
            this.statusStrip1.Size = new System.Drawing.Size(686, 22);
            this.statusStrip1.TabIndex = 1;
            this.statusStrip1.Text = "statusStrip1";
            this.progressLabel.Name = "progressLabel";
            this.progressLabel.Size = new System.Drawing.Size(569, 17);
            this.progressLabel.Spring = true;
            this.progressLabel.TextAlign = System.Drawing.ContentAlignment.MiddleLeft;
            this.progressBar.Name = "progressBar";
            this.progressBar.Size = new System.Drawing.Size(100, 16);
            this.progressBar.Style = System.Windows.Forms.ProgressBarStyle.Continuous;
            this.resultsDisplay.BorderStyle = System.Windows.Forms.BorderStyle.Fixed3D;
            this.resultsDisplay.Dock = System.Windows.Forms.DockStyle.Fill;
            this.resultsDisplay.Location = new System.Drawing.Point(0, 24);
            this.resultsDisplay.Name = "resultsDisplay";
            this.resultsDisplay.Panel1.Controls.Add(this.validationResults);
            this.resultsDisplay.Panel2.Controls.Add(this.tabControl1);
            this.resultsDisplay.Size = new System.Drawing.Size(686, 334);
            this.resultsDisplay.SplitterDistance = 318;
            this.resultsDisplay.TabIndex = 4;
            this.validationResults.Dock = System.Windows.Forms.DockStyle.Fill;
            this.validationResults.IsWebBrowserContextMenuEnabled = false;
            this.validationResults.Location = new System.Drawing.Point(0, 0);
            this.validationResults.MinimumSize = new System.Drawing.Size(20, 20);
            this.validationResults.Name = "validationResults";
            this.validationResults.ScriptErrorsSuppressed = true;
            this.validationResults.Size = new System.Drawing.Size(314, 330);
            this.validationResults.TabIndex = 3;
            this.validationResults.WebBrowserShortcutsEnabled = false;
            this.tabControl1.Controls.Add(this.tabPage1);
            this.tabControl1.Controls.Add(this.tabPage2);
            this.tabControl1.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tabControl1.Location = new System.Drawing.Point(0, 0);
            this.tabControl1.Name = "tabControl1";
            this.tabControl1.SelectedIndex = 0;
            this.tabControl1.Size = new System.Drawing.Size(360, 330);
            this.tabControl1.TabIndex = 5;
            this.tabPage1.Controls.Add(this.xmlDisplay);
            this.tabPage1.Location = new System.Drawing.Point(4, 22);
            this.tabPage1.Name = "tabPage1";
            this.tabPage1.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage1.Size = new System.Drawing.Size(352, 304);
            this.tabPage1.TabIndex = 0;
            this.tabPage1.Text = "Original";
            this.tabPage1.UseVisualStyleBackColor = true;
            this.xmlDisplay.Dock = System.Windows.Forms.DockStyle.Fill;
            this.xmlDisplay.IsWebBrowserContextMenuEnabled = false;
            this.xmlDisplay.Location = new System.Drawing.Point(3, 3);
            this.xmlDisplay.MinimumSize = new System.Drawing.Size(20, 20);
            this.xmlDisplay.Name = "xmlDisplay";
            this.xmlDisplay.ScriptErrorsSuppressed = true;
            this.xmlDisplay.Size = new System.Drawing.Size(346, 298);
            this.xmlDisplay.TabIndex = 4;
            this.xmlDisplay.WebBrowserShortcutsEnabled = false;
            this.tabPage2.Controls.Add(this.processedDisplay);
            this.tabPage2.Location = new System.Drawing.Point(4, 22);
            this.tabPage2.Name = "tabPage2";
            this.tabPage2.Padding = new System.Windows.Forms.Padding(3);
            this.tabPage2.Size = new System.Drawing.Size(352, 304);
            this.tabPage2.TabIndex = 1;
            this.tabPage2.Text = "Processed";
            this.tabPage2.UseVisualStyleBackColor = true;
            this.processedDisplay.Dock = System.Windows.Forms.DockStyle.Fill;
            this.processedDisplay.IsWebBrowserContextMenuEnabled = false;
            this.processedDisplay.Location = new System.Drawing.Point(3, 3);
            this.processedDisplay.MinimumSize = new System.Drawing.Size(20, 20);
            this.processedDisplay.Name = "processedDisplay";
            this.processedDisplay.ScriptErrorsSuppressed = true;
            this.processedDisplay.Size = new System.Drawing.Size(346, 298);
            this.processedDisplay.TabIndex = 5;
            this.processedDisplay.WebBrowserShortcutsEnabled = false;
            this.codeFormatter.DoWork += new System.ComponentModel.DoWorkEventHandler(this.codeFormatter_DoWork);
            this.codeFormatter.RunWorkerCompleted += new System.ComponentModel.RunWorkerCompletedEventHandler(this.codeFormatter_RunWorkerCompleted);
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(686, 380);
            this.Controls.Add(this.resultsDisplay);
            this.Controls.Add(this.statusStrip1);
            this.Controls.Add(this.menuStrip1);
            this.Icon = ((System.Drawing.Icon)(resources.GetObject("$this.Icon")));
            this.MainMenuStrip = this.menuStrip1;
            this.Name = "MainForm";
            this.StartPosition = System.Windows.Forms.FormStartPosition.Manual;
            this.Text = "CruiseControl.Net: Configuration Validation";
            this.menuStrip1.ResumeLayout(false);
            this.menuStrip1.PerformLayout();
            this.statusStrip1.ResumeLayout(false);
            this.statusStrip1.PerformLayout();
            this.resultsDisplay.Panel1.ResumeLayout(false);
            this.resultsDisplay.Panel2.ResumeLayout(false);
            this.resultsDisplay.ResumeLayout(false);
            this.tabControl1.ResumeLayout(false);
            this.tabPage1.ResumeLayout(false);
            this.tabPage2.ResumeLayout(false);
            this.ResumeLayout(false);
            this.PerformLayout();
        }
        private System.Windows.Forms.MenuStrip menuStrip1;
        private System.Windows.Forms.ToolStripMenuItem fileToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem openMenuButton;
        private System.Windows.Forms.ToolStripSeparator toolStripMenuItem1;
        private System.Windows.Forms.ToolStripMenuItem exitMenuButton;
        private System.Windows.Forms.StatusStrip statusStrip1;
        private System.Windows.Forms.ToolStripStatusLabel progressLabel;
        private System.Windows.Forms.ToolStripProgressBar progressBar;
        private System.Windows.Forms.SplitContainer resultsDisplay;
        private System.Windows.Forms.WebBrowser validationResults;
        private System.Windows.Forms.WebBrowser xmlDisplay;
        private System.Windows.Forms.ToolStripMenuItem viewToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem reloadMenuButton;
        private System.Windows.Forms.ToolStripMenuItem printMenuButton;
        private System.Windows.Forms.ToolStripSeparator toolStripMenuItem2;
        private System.Windows.Forms.ToolStripMenuItem configurationToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem vericalToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem horizontalToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem offToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem helpToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem aboutToolStripMenuItem;
        private System.Windows.Forms.ToolStripMenuItem historyMenu;
        private System.Windows.Forms.ToolStripMenuItem emptyToolStripMenuItem;
        private System.Windows.Forms.TabControl tabControl1;
        private System.Windows.Forms.TabPage tabPage1;
        private System.Windows.Forms.TabPage tabPage2;
        private System.Windows.Forms.WebBrowser processedDisplay;
        private System.ComponentModel.BackgroundWorker codeFormatter;
    }
}
