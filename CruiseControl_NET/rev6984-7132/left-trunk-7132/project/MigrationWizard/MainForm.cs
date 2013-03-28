using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
namespace ThoughtWorks.CruiseControl.MigrationWizard
{
    public partial class MainForm : Form
    {
        private MainFormController controller;
        private WizardPageBase currentPage;
        public MainForm(MainFormController controller)
        {
            this.controller = controller;
            InitializeComponent();
        }
        public void Start()
        {
            LoadPage(new IntroductionPage());
        }
        private void LoadPage(WizardPageBase newPage)
        {
            if (currentPage != null)
            {
                currentPage.CompletePage();
                currentPage.PreviousPageChanged -= currentPage_PageChanged;
                currentPage.NextPageChanged -= currentPage_PageChanged;
                currentPage.PageCompeleted -= currentPage_Completed;
            }
            pageContainer.Controls.Clear();
            currentPage = newPage;
            currentPage.Controller = controller;
            currentPage.MigrationOptions = controller.MigrationOptions;
            pageContainer.Controls.Add(currentPage);
            currentPage.Dock = DockStyle.Fill;
            currentPage.Visible = true;
            currentPage.PreviousPageChanged += currentPage_PageChanged;
            currentPage.NextPageChanged += currentPage_PageChanged;
            currentPage.PageCompeleted += currentPage_Completed;
            ChangeNavigation();
            currentPage.RunPage();
        }
        private void ChangeNavigation()
        {
            cancelButton.Enabled = currentPage.CanCancel;
            previousButton.Enabled = (currentPage.PreviousPage != null);
            nextButton.Enabled = (currentPage.NextPage != null) && currentPage.IsValid;
            finishButton.Enabled = currentPage.CanFinish;
        }
        private void finishButton_Click(object sender, EventArgs e)
        {
            controller.Close();
        }
        private void currentPage_PageChanged(object sender, EventArgs e)
        {
            ChangeNavigation();
        }
        private void currentPage_Completed(object sender, EventArgs e)
        {
            LoadPage(currentPage.NextPage);
        }
        private void cancelButton_Click(object sender, EventArgs e)
        {
            var canCancel = true;
            if (currentPage.ConfirmCancel)
            {
                canCancel = (MessageBox.Show(this,
                    "Are you sure you want to cancel this wizard?",
                    "Confirm cancel",
                    MessageBoxButtons.YesNo,
                    MessageBoxIcon.Information,
                    MessageBoxDefaultButton.Button2) == DialogResult.Yes);
            }
            if (canCancel) controller.Close();
        }
        private void nextButton_Click(object sender, EventArgs e)
        {
            LoadPage(currentPage.NextPage);
        }
        private void previousButton_Click(object sender, EventArgs e)
        {
            LoadPage(currentPage.PreviousPage);
        }
    }
}
