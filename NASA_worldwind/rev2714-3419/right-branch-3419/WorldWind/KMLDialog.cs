using System;
using System.Collections;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Diagnostics;
using AxSHDocVw;
using MSHTML;
using System.Runtime.InteropServices;
namespace WorldWind
{
    public class KMLDialog : Form
    {
        private System.Windows.Forms.Label closeButton;
        public AxWebBrowser myBrowser = new AxWebBrowser();
        public bool isVisible = false;
        public bool HTMLIsSet = false;
        private String HTML;
        public KMLDialog()
        {
            InitializeComponent();
            myBrowser.BeginInit();
            myBrowser.Size = new Size(270, 150);
            myBrowser.Location = new Point(2, 2);
            myBrowser.Dock = DockStyle.Fill;
            myBrowser.EndInit();
            this.Controls.Add(myBrowser);
            isVisible = true;
        }
        void myBrowser_BeforeNavigate2(object sender, DWebBrowserEvents2_BeforeNavigate2Event e)
        {
            String URL = e.uRL.ToString();
            MainApplication nt = (MainApplication)this.FindForm().Owner;
            nt.BrowseTo(URL);
        }
        void myBrowser_DocumentComplete(object sender, DWebBrowserEvents2_DocumentCompleteEvent e)
        {
            IHTMLDocument2 htmlDoc = myBrowser.Document as IHTMLDocument2;
            htmlDoc.clear();
            htmlDoc.writeln(HTML);
            htmlDoc.close();
        }
        public void SetHTML(String Html){
            HTMLIsSet = true;
            if (Html.IndexOf("<font>") == -1)
            {
                Html = "<font face='Arial' size='1'>" + Html + "</font>";
            }
            myBrowser.Navigate("about:blank");
            HTML = Html;
            IHTMLDocument2 htmlDoc = myBrowser.Document as IHTMLDocument2;
            htmlDoc.clear();
            htmlDoc.writeln(Html);
            htmlDoc.close();
            myBrowser.BeforeNavigate2 += new DWebBrowserEvents2_BeforeNavigate2EventHandler(myBrowser_BeforeNavigate2);
            myBrowser.DocumentComplete += new DWebBrowserEvents2_DocumentCompleteEventHandler(myBrowser_DocumentComplete);
         }
        private void closeButton_Click(object sender, EventArgs e)
        {
            this.FindForm().Visible = false;
            isVisible = false;
        }
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
            this.closeButton = new System.Windows.Forms.Label();
            this.SuspendLayout();
            this.closeButton.AutoSize = true;
            this.closeButton.BackColor = System.Drawing.Color.Transparent;
            this.closeButton.Cursor = System.Windows.Forms.Cursors.Hand;
            this.closeButton.Font = new System.Drawing.Font("Arial Black", 9.75F, System.Drawing.FontStyle.Bold, System.Drawing.GraphicsUnit.Point, ((byte)(0)));
            this.closeButton.ForeColor = System.Drawing.Color.DodgerBlue;
            this.closeButton.Location = new System.Drawing.Point(249, 9);
            this.closeButton.Name = "closeButton";
            this.closeButton.Size = new System.Drawing.Size(19, 18);
            this.closeButton.TabIndex = 0;
            this.closeButton.Text = "X";
            this.closeButton.TextAlign = System.Drawing.ContentAlignment.MiddleCenter;
            this.closeButton.Click += new System.EventHandler(this.closeButton_Click);
            this.BackColor = System.Drawing.Color.White;
            this.ClientSize = new System.Drawing.Size(280, 160);
            this.Controls.Add(this.closeButton);
            this.FormBorderStyle = System.Windows.Forms.FormBorderStyle.None;
            this.MaximizeBox = false;
            this.MinimizeBox = false;
            this.Name = "KMLDialog";
            this.ShowInTaskbar = false;
            this.StartPosition = System.Windows.Forms.FormStartPosition.Manual;
            this.ResumeLayout(false);
            this.PerformLayout();
        }
    }
}
