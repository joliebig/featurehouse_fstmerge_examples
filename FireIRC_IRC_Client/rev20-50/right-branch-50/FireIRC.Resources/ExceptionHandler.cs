using System;
using System.Collections.Generic;
using System.Text;
namespace OVT.FireIRC.Resources
{
    public static class ExceptionHandler
    {
        public static void CurrentDomain_UnhandledException(object sender, UnhandledExceptionEventArgs e)
        {
            ExceptionHandled ex = new ExceptionHandled((Exception)e.ExceptionObject);
            ex.ShowDialog();
        }
    }
}
