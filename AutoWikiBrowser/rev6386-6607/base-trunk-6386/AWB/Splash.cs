using System;
using System.Windows.Forms;
namespace AutoWikiBrowser
{
    internal sealed partial class Splash : Form
    {
        public Splash()
        {
            InitializeComponent();
            lblVersion.Text = "Version " + Program.VersionString;
            SetProgress(0);
        }
        private void ClickHandler(object sender, EventArgs e)
        {
            Close();
        }
        public void SetProgress(int percent)
        {
            System.Reflection.MethodBase method = new System.Diagnostics.StackFrame(1).GetMethod();
            MethodLabel.Text = method.DeclaringType.Name + "::" + method.Name + "()";
            progressBar.Value = percent;
            Application.DoEvents();
        }
    }
}
