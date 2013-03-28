using System;
using System.Windows.Forms;
using System.Threading;
namespace WikiFunctions.Background
{
    public partial class PleaseWait : Form
    {
        delegate void SetTextCallback(string text);
        delegate void SetProgressCallback(int completed, int total);
        public Thread Worker;
        public PleaseWait()
        {
            InitializeComponent();
        }
        private void btnCancel_Click(object sender, EventArgs e)
        {
            Worker.Abort();
            Close();
        }
        private void SetStatus(string status)
        {
            if (lblStatus.InvokeRequired)
            {
                SetTextCallback d = SetStatus;
                Invoke(d, new object[] { status });
            }
            else
                lblStatus.Text = status;
        }
        public string Status
        {
            get { return lblStatus.Text; }
            set { SetStatus(value); }
        }
        public void SetProgress(int completed, int total)
        {
            if (Progress.InvokeRequired)
            {
                SetProgressCallback d = SetProgress;
                Invoke(d, new object[] { completed, total });
            }
            else
            {
                Progress.Maximum = total;
                Progress.Value = completed;
                groupBox.Text = string.Format("{0}/{1} complete", completed, total);
            }
        }
    }
}
