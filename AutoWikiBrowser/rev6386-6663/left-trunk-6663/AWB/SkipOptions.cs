using System;
using System.Collections.Generic;
using System.Windows.Forms;
using WikiFunctions.Plugin;
namespace AutoWikiBrowser
{
    internal sealed partial class SkipOptions : Form, ISkipOptions
    {
        public SkipOptions()
        {
            InitializeComponent();
        }
        public bool SkipNoUnicode
        {
            get { return chkNoUnicode.Checked; }
        }
        public bool SkipNoTag
        {
            get { return chkNoTag.Checked; }
        }
        public bool SkipNoHeaderError
        {
            get { return chkNoHeaderError.Checked; }
        }
        public bool SkipNoBoldTitle
        {
            get { return chkNoBoldTitle.Checked; }
        }
        public bool SkipNoBulletedLink
        {
            get { return chkNoBulletedLink.Checked; }
        }
        public bool SkipNoBadLink
        {
            get { return chkNoBadLink.Checked; }
        }
        public bool SkipNoDefaultSortAdded
        {
            get { return chkDefaultSortAdded.Checked; }
        }
        public bool SkipNoUserTalkTemplatesSubstd
        {
            get { return chkUserTalkTemplates.Checked; }
        }
        public bool SkipNoCiteTemplateDatesFixed
        {
            get { return chkCiteTemplateDates.Checked; }
        }
        public bool SkipNoPeopleCategoriesFixed
        {
            get { return chkPeopleCategories.Checked; }
        }
        private void SkipOptions_FormClosing(object sender, FormClosingEventArgs e)
        {
            e.Cancel = true;
            Hide();
        }
        private void btnClose_Click(object sender, EventArgs e)
        {
            Hide();
        }
        public List<int> SelectedItems
        {
            get
            {
                List<int> ret = new List<int>();
                foreach (CheckBox c in CheckBoxPanel.Controls)
                {
                    if (c.Checked)
                        ret.Add((int)c.Tag);
                }
                return ret;
            }
            set
            {
                if (value.Count <= 0) return;
                CheckAll.Checked = false;
                CheckNone.Checked = false;
                SetCheckboxes(false);
                foreach (CheckBox c in CheckBoxPanel.Controls)
                {
                    c.Checked = value.Contains((int)c.Tag);
                }
            }
        }
        private void CheckAll_CheckedChanged(object sender, EventArgs e)
        {
            if (CheckAll.Checked)
            {
                CheckNone.Checked = false;
                SetCheckboxes(true);
            }
        }
        private void CheckNone_CheckedChanged(object sender, EventArgs e)
        {
            if (CheckNone.Checked)
            {
                CheckAll.Checked = false;
                SetCheckboxes(false);
            }
        }
        private void SetCheckboxes(bool value)
        {
            foreach (CheckBox c in CheckBoxPanel.Controls)
            {
                c.Checked = value;
            }
        }
    }
}
