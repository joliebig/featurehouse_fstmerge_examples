using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise;
using ThoughtWorks.CruiseControl.Core.Reporting.Dashboard.Navigation;
using ThoughtWorks.CruiseControl.WebDashboard.MVC;
using ThoughtWorks.CruiseControl.WebDashboard.IO;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.View;
using System.Collections;
using System.Web;
using System.IO;
using ThoughtWorks.CruiseControl.WebDashboard.Configuration;
using System.Xml;
using ThoughtWorks.CruiseControl.WebDashboard.Resources;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.Administration
{
    public class AdministerAction
        : ICruiseAction
    {
        public const string ActionName = "AdministerDashboard";
        private readonly IVelocityViewGenerator viewGenerator;
        private readonly PackageManager manager;
        private IRemoteServicesConfiguration servicesConfiguration;
        private readonly IPhysicalApplicationPathProvider physicalApplicationPathProvider;
        private string password;
        private Translations translations;
        public AdministerAction(PackageManager manager,
            IVelocityViewGenerator viewGenerator,
            IRemoteServicesConfiguration servicesConfiguration,
            IPhysicalApplicationPathProvider physicalApplicationPathProvider)
        {
            this.manager = manager;
            this.viewGenerator = viewGenerator;
            this.servicesConfiguration = servicesConfiguration;
            this.physicalApplicationPathProvider = physicalApplicationPathProvider;
        }
        public string Password
        {
            get { return password; }
            set { password = value; }
        }
        public IResponse Execute(ICruiseRequest cruiseRequest)
        {
            this.translations = Translations.RetrieveCurrent();
            Hashtable velocityContext = new Hashtable();
            velocityContext["Error"] = string.Empty;
            if (ValidateSession(velocityContext))
            {
                velocityContext["Result"] = string.Empty;
                velocityContext["InstallPackage"] = string.Empty;
                string action = cruiseRequest.Request.GetText("Action") ?? string.Empty;
                string type = cruiseRequest.Request.GetText("Type");
                string name = cruiseRequest.Request.GetText("Name");
                if (action == this.translations.Translate("Reload dashboard"))
                {
                    CachingDashboardConfigurationLoader.ClearCache();
                    velocityContext["Result"] = this.translations.Translate("The dashboard configuration has been reloaded");
                }
                else if (action == string.Empty)
                {
                }
                else if (action == this.translations.Translate("Save"))
                {
                    SaveServer(cruiseRequest.Request, velocityContext);
                }
                else if (action == this.translations.Translate("Delete"))
                {
                    DeleteServer(cruiseRequest.Request, velocityContext);
                }
                else if (action == this.translations.Translate("Import"))
                {
                    ImportPackage(HttpContext.Current, velocityContext);
                }
                else if (action == this.translations.Translate("Install"))
                {
                    InstallPackage(cruiseRequest, velocityContext);
                }
                else if (action == this.translations.Translate("Uninstall"))
                {
                    UninstallPackage(cruiseRequest, velocityContext);
                }
                else if (action == this.translations.Translate("Remove"))
                {
                    RemovePackage(cruiseRequest, velocityContext);
                }
                else if (action == this.translations.Translate("Logout"))
                {
                    Logout();
                }
                else
                {
                    velocityContext["Error"] = this.translations.Translate("Unknown action '{0}'", action);
                }
                velocityContext["Servers"] = servicesConfiguration.Servers;
                List<PackageManifest> packages = manager.ListPackages();
                packages.Sort();
                velocityContext["Packages"] = packages;
                if (action == "Logout")
                {
                    return viewGenerator.GenerateView("AdminLogin.vm", velocityContext);
                }
                else
                {
                    return viewGenerator.GenerateView("AdministerDashboard.vm", velocityContext);
                }
            }
            else
            {
                return viewGenerator.GenerateView("AdminLogin.vm", velocityContext);
            }
        }
        private void SaveServer(IRequest request, Hashtable velocityContext)
        {
            string newName = request.GetText("newName");
            string oldName = request.GetText("oldName");
            string serverUri = request.GetText("serverUri");
            bool serverForceBuild = request.GetChecked("serverForceBuild");
            bool serverStartStop = request.GetChecked("serverStartStop");
            bool backwardsCompatible = request.GetChecked("serverBackwardsCompatible");
            if (string.IsNullOrEmpty(newName))
            {
                velocityContext["Error"] = this.translations.Translate("Name is a compulsory value");
                return;
            }
            if (string.IsNullOrEmpty(serverUri))
            {
                velocityContext["Error"] = this.translations.Translate("URI is a compulsory value");
                return;
            }
            XmlDocument configFile = LoadConfig();
            XmlElement serverEl = configFile.SelectSingleNode(
                string.Format(
                    "/dashboard/remoteServices/servers/server[@name='{0}']",
                    oldName)) as XmlElement;
            ServerLocation location = null;
            if (serverEl == null)
            {
                serverEl = configFile.CreateElement("server");
                configFile.SelectSingleNode("/dashboard/remoteServices/servers")
                    .AppendChild(serverEl);
                location = new ServerLocation();
                ServerLocation[] locations = new ServerLocation[servicesConfiguration.Servers.Length + 1];
                servicesConfiguration.Servers.CopyTo(locations, 0);
                locations[servicesConfiguration.Servers.Length] = location;
                servicesConfiguration.Servers = locations;
            }
            else
            {
                foreach (ServerLocation locationToCheck in servicesConfiguration.Servers)
                {
                    if (locationToCheck.Name == oldName)
                    {
                        location = locationToCheck;
                        break;
                    }
                }
            }
            serverEl.SetAttribute("name", newName);
            serverEl.SetAttribute("url", serverUri);
            serverEl.SetAttribute("allowForceBuild", serverForceBuild ? "true" : "false");
            serverEl.SetAttribute("allowStartStopBuild", serverStartStop ? "true" : "false");
            serverEl.SetAttribute("backwardsCompatible", backwardsCompatible ? "true" : "false");
            SaveConfig(configFile);
            if (location != null)
            {
                location.Name = newName;
                location.Url = serverUri;
                location.AllowForceBuild = serverForceBuild;
                location.AllowStartStopBuild = serverStartStop;
                location.BackwardCompatible = backwardsCompatible;
            }
            velocityContext["Result"] = this.translations.Translate("Server has been saved");
            CachingDashboardConfigurationLoader.ClearCache();
        }
        private void DeleteServer(IRequest request, Hashtable velocityContext)
        {
            string serverName = request.GetText("ServerName");
            if (string.IsNullOrEmpty(serverName))
            {
                velocityContext["Error"] = this.translations.Translate("Server name has not been set");
                return;
            }
            XmlDocument configFile = LoadConfig();
            XmlElement serverEl = configFile.SelectSingleNode(
                string.Format(
                    "/dashboard/remoteServices/servers/server[@name='{0}']",
                    serverName)) as XmlElement;
            ServerLocation location = null;
            if (serverEl != null)
            {
                foreach (ServerLocation locationToCheck in servicesConfiguration.Servers)
                {
                    if (locationToCheck.Name == serverName)
                    {
                        location = locationToCheck;
                        break;
                    }
                }
                if (location != null)
                {
                    List<ServerLocation> locations = new List<ServerLocation>(servicesConfiguration.Servers);
                    locations.Remove(location);
                    servicesConfiguration.Servers = locations.ToArray();
                }
                serverEl.ParentNode.RemoveChild(serverEl);
                SaveConfig(configFile);
                velocityContext["Result"] = this.translations.Translate("Server has been deleted");
                CachingDashboardConfigurationLoader.ClearCache();
            }
            else
            {
                velocityContext["Error"] = this.translations.Translate("Unable to find server");
                return;
            }
        }
        private XmlDocument LoadConfig()
        {
            string configPath = DashboardConfigurationLoader.CalculateDashboardConfigPath();
            XmlDocument document = new XmlDocument();
            document.Load(configPath);
            return document;
        }
        private void SaveConfig(XmlDocument configFile)
        {
            string configPath = DashboardConfigurationLoader.CalculateDashboardConfigPath();
            configFile.Save(configPath);
        }
        private void ImportPackage(HttpContext context, Hashtable velocityContext)
        {
            HttpPostedFile file = context.Request.Files["package"];
            if (file.ContentLength == 0)
            {
                velocityContext["Error"] = this.translations.Translate("No file selected to import!");
            }
            else
            {
                PackageManifest manifest = manager.StorePackage(file.FileName, file.InputStream);
                if (manifest == null)
                {
                    velocityContext["Error"] = this.translations.Translate("Invalid package - manifest file is missing");
                }
                else
                {
                    velocityContext["Result"] = this.translations.Translate("Package '{0}' has been loaded",
                        manifest.Name);
                    velocityContext["InstallPackage"] = manifest.FileName;
                }
            }
        }
        private void InstallPackage(ICruiseRequest cruiseRequest, Hashtable velocityContext)
        {
            List<PackageImportEventArgs> events = manager.InstallPackage(
                cruiseRequest.Request.GetText("PackageName"));
            if (events != null)
            {
                velocityContext["Result"] = this.translations.Translate("Package has been installed");
                velocityContext["Install"] = true;
                velocityContext["Events"] = events;
                CachingDashboardConfigurationLoader.ClearCache();
            }
            else
            {
                velocityContext["Error"] = this.translations.Translate("Package has been removed");
            }
        }
        private void UninstallPackage(ICruiseRequest cruiseRequest, Hashtable velocityContext)
        {
            List<PackageImportEventArgs> events = manager.UninstallPackage(
                cruiseRequest.Request.GetText("PackageName"));
            if (events != null)
            {
                velocityContext["Result"] = this.translations.Translate("Package has been uninstalled");
                velocityContext["Install"] = true;
                velocityContext["Events"] = events;
                CachingDashboardConfigurationLoader.ClearCache();
            }
            else
            {
                velocityContext["Error"] = this.translations.Translate("Unable to uninstall package");
            }
        }
        private void RemovePackage(ICruiseRequest cruiseRequest, Hashtable velocityContext)
        {
            string name = manager.RemovePackage(cruiseRequest.Request.GetText("PackageName"));
            if (!string.IsNullOrEmpty(name))
            {
                velocityContext["Result"] = string.Format("Package '{0}' has been removed",
                            name);
            }
            else
            {
                velocityContext["Error"] = this.translations.Translate("Unable to remove package - has the package already been removed?");
            }
        }
        private bool ValidateSession(Hashtable velocityContext)
        {
            bool isValid = false;
            if (string.IsNullOrEmpty(password))
            {
                velocityContext["Error"] = this.translations.Translate("Administration password may not be empty. Update dashboard.config, section administrationPlugin.");
                isValid = false;
            }
            else
            {
                HttpContext context = HttpContext.Current;
                string ticket = RetrieveSessionCookie(context);
                if (ticket == "webAdmin")
                {
                    isValid = true;
                }
                else
                {
                    string userPassword = context.Request.Form["password"];
                    if (string.Equals(password, userPassword))
                    {
                        isValid = true;
                        AddSessionCookie(context);
                    }
                    else
                    {
                        if (!string.IsNullOrEmpty(userPassword))
                        {
                            velocityContext["Error"] = this.translations.Translate("Invalid password");
                        }
                    }
                }
            }
            return isValid;
        }
        private string RetrieveSessionCookie(HttpContext context)
        {
            HttpCookie cookie = context.Request.Cookies["CCNetDashboard"];
            if ((cookie != null) && !string.IsNullOrEmpty(cookie.Value))
            {
                return cookie.Value;
            }
            else
            {
                return null;
            }
        }
        private void AddSessionCookie(HttpContext context)
        {
            string cookieData = "webAdmin";
            HttpCookie cookie = new HttpCookie("CCNetDashboard", cookieData);
            cookie.HttpOnly = true;
            cookie.Expires = DateTime.Now.AddMinutes(30);
            context.Response.Cookies.Add(cookie);
        }
        private void Logout()
        {
            if (HttpContext.Current.Request.Cookies["CCNetDashboard"] != null)
            {
                HttpCookie cookie = new HttpCookie("CCNetDashboard", string.Empty);
                cookie.HttpOnly = true;
                cookie.Expires = DateTime.Now.AddDays(-1);
                HttpContext.Current.Response.Cookies.Add(cookie);
            }
        }
    }
}
