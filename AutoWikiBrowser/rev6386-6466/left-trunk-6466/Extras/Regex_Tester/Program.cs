using System;
using System.Windows.Forms;
namespace Regex_Tester
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new WikiFunctions.Controls.RegexTester());
        }
    }
}
