using System;
using System.Collections.Generic;
using System.Windows.Forms;
namespace AutoWikiBrowser
{
    internal sealed partial class SummaryEditor : Form
    {
        public SummaryEditor()
        {
            InitializeComponent();
        }
        private void btnSort_Click(object sender, EventArgs e)
        {
            List<string> list =
                new List<string>(Summaries.Text.Split(new[] {"\r\n"}, StringSplitOptions.RemoveEmptyEntries));
            list.Sort();
            Summaries.Clear();
            foreach (string s in list)
                Summaries.Text += s + "\r\n";
        }
    }
}
