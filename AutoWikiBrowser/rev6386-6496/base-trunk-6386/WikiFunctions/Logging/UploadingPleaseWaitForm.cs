using System;
using System.Windows.Forms;
namespace WikiFunctions.Logging
{
    public partial class UploadingPleaseWaitForm : Form
    {
        Cursor OldCursor;
        public UploadingPleaseWaitForm()
        {
            InitializeComponent();
        }
        private void Form_Closing(object sender, FormClosingEventArgs e)
        {
            Cursor = OldCursor;
        }
        private void Form_Shown(object sender, EventArgs e)
        {
            OldCursor = Cursor;
            Cursor = Cursors.WaitCursor;
        }
    }
}
