using System;
using System.Windows.Forms;
namespace AutoWikiBrowser
{
    internal sealed partial class ExitQuestion : Form
    {
        public ExitQuestion(TimeSpan time, int edits, string msg)
        {
            InitializeComponent();
            lblPrompt.Text = msg + "Are you sure you want to exit?";
            lblTimeAndEdits.Text = "You made " + edits + " edits in " + time;
        }
        public bool CheckBoxDontAskAgain
        {
            get { return chkDontAskAgain.Checked; }
        }
    }
}
