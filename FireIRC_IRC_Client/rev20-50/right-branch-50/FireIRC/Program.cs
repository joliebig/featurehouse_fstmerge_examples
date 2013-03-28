using System;
using System.Collections.Generic;
using System.Windows.Forms;
using System.Reflection;
using System.IO;
using System.Xml.Serialization;
using OVT.Melissa.PluginSupport;
using OVT.FireIRC.Resources;
namespace FireIRC
{
    public static class Program
    {
        [STAThread]
        static void Main(string[] args)
        {
            try
            {
                if (args[0] == "-createpackage")
                {
                    FireIRCUpdatePackage p = new FireIRCUpdatePackage();
                    foreach (string i in Directory.GetFiles(UpdatorSystem.InstallPath, "*.dll"))
                    {
                        p.Files.Add(new FireIRCUpdatePackage.FilePackage(i.Split('\\')[i.Split('\\').GetUpperBound(0)], File.ReadAllBytes(i)));
                    }
                    foreach (string i in Directory.GetFiles(UpdatorSystem.InstallPath, "*.pdb"))
                    {
                        p.Files.Add(new FireIRCUpdatePackage.FilePackage(i.Split('\\')[i.Split('\\').GetUpperBound(0)], File.ReadAllBytes(i)));
                    }
                    XmlSerializer x = new XmlSerializer(typeof(FireIRCUpdatePackage));
                    FileStream ps = File.Open("fireirc.pak", FileMode.Create);
                    x.Serialize(ps, p);
                    ps.Close();
                    MessageBox.Show("Creation of FireIRC Package is Complete.", "FireIRC", MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
                else if (args[0] == "-installlocalpak")
                {
                    foreach (FireIRCUpdatePackage.FilePackage p in UpdatorSystem.GetPackage().Files)
                    {
                        File.WriteAllBytes(Path.Combine(UpdatorSystem.InstallPath, p.FileName), p.FileData);
                    }
                    MessageBox.Show("Installation of FireIRC is Complete.", "FireIRC", MessageBoxButtons.OK, MessageBoxIcon.Information);
                }
                else if (args[0] == "-debug")
                {
                    throw new IndexOutOfRangeException();
                }
            }
            catch (IndexOutOfRangeException)
            {
                Execute(args);
            }
        }
        static void Execute(string[] args)
        {
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);
            LoggingService.Info("Application start");
            Assembly exe = typeof(FireIRC.Program).Assembly;
            FileUtility.ApplicationRootPath = Path.GetDirectoryName(exe.Location);
            LoggingService.Info("Starting core services...");
            CoreStartup coreStartup = null;
            try
            {
                if (args[0] == "-debug")
                {
                    coreStartup = new CoreStartup("FireIRC2-Debug");
                }
            }
            catch (IndexOutOfRangeException)
            {
                coreStartup = new CoreStartup("FireIRC2");
            }
            coreStartup.PropertiesName = "AppProperties";
            coreStartup.StartCoreServices();
            ResourceService.RegisterNeutralStrings(OVT.FireIRC.Resources.StringResources.ResourceManager);
            LoggingService.Info("Looking for AddIns...");
            coreStartup.AddAddInsFromDirectory(
              Path.Combine(FileUtility.ApplicationRootPath, "AddIns"));
            coreStartup.ConfigureExternalAddIns(
              Path.Combine(PropertyService.ConfigDirectory, "AddIns.xml"));
            coreStartup.ConfigureUserAddIns(
              Path.Combine(PropertyService.ConfigDirectory, "AddInInstallTemp"),
                       Path.Combine(PropertyService.ConfigDirectory, "AddIns"));
            LoggingService.Info("Loading AddInTree...");
            coreStartup.RunInitialization();
            LoggingService.Info("Initializing Workbench...");
            OVT.FireIRC.Resources.FireIRCCore.ReadSettings();
            OVT.FireIRC.Resources.FireIRCCore.Setup();
            OVT.FireIRC.Resources.FireIRCCore.PrimaryForm = new MainForm();
            try
            {
                LoggingService.Info("Running application...");
                Application.Run(OVT.FireIRC.Resources.FireIRCCore.PrimaryForm);
            }
            finally
            {
                try
                {
                    OVT.FireIRC.Resources.FireIRCCore.SaveSettings();
                    PropertyService.Save();
                }
                catch (Exception ex)
                {
                    MessageService.ShowError(ex, "Error storing properties");
                }
            }
            LoggingService.Info("Application shutdown");
        }
    }
}
