using System;
using System.Globalization;
using System.Threading;
using System.Windows.Forms;
using log4net;
using RssBandit.Common.Logging;
using RssBandit.Resources;
using RssBandit.WinGui.Forms;
namespace RssBandit
{
    static class Program
    {
        [STAThread]
        private static int Main(string[] args)
        {
            bool running = true;
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            ApplicationExceptionHandler eh = new ApplicationExceptionHandler();
            AppDomain.CurrentDomain.UnhandledException += eh.OnAppDomainException;
            FormWindowState initialStartupState = Win32.GetStartupWindowState();
            RssBanditApplication appInstance = new RssBanditApplication();
            OtherInstanceCallback callback = appInstance.OnOtherInstance;
            try
            {
                running = InitialInstanceActivator.Activate(appInstance, callback, args);
            }
            catch (Exception )
            {
            }
            RssBanditApplication.StaticInit();
            if (!running)
            {
                RssBanditApplication.SharedCulture = CultureInfo.CurrentCulture;
                RssBanditApplication.SharedUICulture = CultureInfo.CurrentUICulture;
                if (appInstance.HandleCommandLineArgs(args))
                {
                    if (!string.IsNullOrEmpty(appInstance.CommandLineArgs.LocalCulture))
                    {
                        try
                        {
                            RssBanditApplication.SharedUICulture =
                                CultureInfo.CreateSpecificCulture(appInstance.CommandLineArgs.LocalCulture);
                            RssBanditApplication.SharedCulture = RssBanditApplication.SharedUICulture;
                        }
                        catch (Exception ex)
                        {
                            appInstance.MessageError(
                                SR.ExceptionProcessCommandlineCulture(appInstance.CommandLineArgs.LocalCulture,
                                                                      ex.Message));
                        }
                    }
                    Thread.CurrentThread.CurrentCulture = RssBanditApplication.SharedCulture;
                    Thread.CurrentThread.CurrentUICulture = RssBanditApplication.SharedUICulture;
                    if (!appInstance.CommandLineArgs.StartInTaskbarNotificationAreaOnly &&
                        initialStartupState != FormWindowState.Minimized)
                    {
                        Splash.Show();
                        Splash.Version = String.Format("v{0}", RssBanditApplication.Version);
                        Splash.Status = SR.AppLoadStateLoading;
                    }
                    appInstance.Init();
                    appInstance.StartMainGui(initialStartupState);
                    Splash.Close();
                    return 0;
                }
                else
                {
                    return 2;
                }
            }
            else
            {
                return 1;
            }
        }
    }
}
