using System;
using System.Windows.Forms;
namespace DBScanner
{
    static class Program
    {
        [STAThread]
        static void Main()
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            Application.Run(new WikiFunctions.DBScanner.DatabaseScanner());
        }
    }
}
