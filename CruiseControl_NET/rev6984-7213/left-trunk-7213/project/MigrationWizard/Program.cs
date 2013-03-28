using System;
using System.Windows.Forms;
namespace ThoughtWorks.CruiseControl.MigrationWizard
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
            var controller = new MainFormController();
            controller.ShowUI();
        }
    }
}
