using System;
using System.Collections.Generic;
using System.Windows.Forms;
using WikiFunctions;
using System.Text.RegularExpressions;
using WikiFunctions.Background;
using WikiFunctions.Lists.Providers;
namespace AutoWikiBrowser.Plugins.CFD
{
    internal sealed partial class CfdOptions : Form
    {
        public CfdOptions()
        {
            InitializeComponent();
        }
        static string prevContent = "";
        readonly Dictionary<string, string> ToDo = new Dictionary<string, string>();
        public new void Show()
        {
            chkSkip.Checked = CfdAWBPlugin.Settings.Skip;
            Grid.Rows.Clear();
            foreach (KeyValuePair<string, string> p in CfdAWBPlugin.Settings.Categories)
            {
                Grid.Rows.Add(new [] { p.Key, p.Value });
            }
            txtBacklog.Text = prevContent;
            if (ShowDialog() != DialogResult.OK) return;
            CfdAWBPlugin.Settings.Skip = chkSkip.Checked;
            CfdAWBPlugin.Settings.Categories = ToDo;
            prevContent = txtBacklog.Text;
        }
        private void RefreshCats()
        {
           Regex catReplace = new Regex(@"\*+\s*\[\[:" + Variables.NamespacesCaseInsensitive[14] +
                @"([^\]\|]*)(?:\|[^\]]*|)\]\][^\[]*\[\[:" + Variables.NamespacesCaseInsensitive[14] + @"([^\]\|]*)\]\]");
           Regex catRemove = new Regex(@"\*+\s*\[\[:" + Variables.NamespacesCaseInsensitive[14] +
               @"\s*([^\]\|]*)(?:\|[^\]]*|)\]\][^\[]*?$");
            Grid.Rows.Clear();
            foreach (string s in txtBacklog.Lines)
            {
                Match m = catReplace.Match(s.Replace("â", ""));
                if (m.Success)
                {
                    Grid.Rows.Add(new [] { m.Groups[1].Value.Trim().Replace("_", " "), m.Groups[2].Value.Trim().Replace("_", "") });
                    continue;
                }
                m = catRemove.Match(s.Replace("â", ""));
                if (m.Success)
                    Grid.Rows.Add(new [] { m.Groups[1].Value.Trim().Replace("_", " "), "" });
            }
        }
        private void btnOk_Click(object sender, EventArgs e)
        {
            try
            {
                timer.Enabled = false;
                RefreshCats();
                txtBacklog.Text = txtBacklog.Text.Replace("_", " ");
                ToDo.Clear();
                foreach (DataGridViewRow r in Grid.Rows)
                {
                    if (!r.IsNewRow && !string.IsNullOrEmpty((string)r.Cells[0].Value)
                        && !ToDo.ContainsKey((string)r.Cells[0].Value))
                        ToDo.Add((string)r.Cells[0].Value, (string)r.Cells[1].Value);
                }
                BackgroundRequest req = new BackgroundRequest();
                if (ToDo.Count > 0)
                {
                    string[] cats = new string[ToDo.Count];
                    ToDo.Keys.CopyTo(cats, 0);
                    Enabled = false;
                    req.GetList(new CategoryListProvider(), cats);
                    req.Wait();
                    Enabled = true;
                    if (req.Result != null && req.Result is List<Article>)
                        CfdAWBPlugin.AWB.ListMaker.Add((List<Article>)req.Result);
                }
            }
            catch (Exception ex)
            {
                ErrorHandler.Handle(ex);
            }
            DialogResult = DialogResult.OK;
        }
        private void txtBacklog_TextChanged(object sender, EventArgs e)
        {
            timer.Enabled = true;
        }
        private void timer_Tick(object sender, EventArgs e)
        {
            timer.Enabled = false;
            RefreshCats();
        }
    }
}
