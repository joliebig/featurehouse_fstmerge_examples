using System;
using System.Collections.Generic;
using System.Text;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.View;
using ThoughtWorks.CruiseControl.WebDashboard.ServerConnection;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise;
using System.Collections;
using ThoughtWorks.CruiseControl.WebDashboard.IO;
using ThoughtWorks.CruiseControl.WebDashboard.MVC;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.ProjectReport
{
    public class PackageListAction
        : ICruiseAction
    {
        public const string ActionName = "PackageList";
        private readonly IVelocityViewGenerator viewGenerator;
        private readonly IFarmService farmService;
        public PackageListAction(IVelocityViewGenerator viewGenerator,
            IFarmService farmService)
  {
            this.viewGenerator = viewGenerator;
            this.farmService = farmService;
        }
        public IResponse Execute(ICruiseRequest cruiseRequest)
        {
            var velocityContext = new Hashtable();
            velocityContext.Add("projectName", cruiseRequest.ProjectName);
            var packages = farmService.RetrievePackageList(cruiseRequest.ProjectSpecifier, cruiseRequest.RetrieveSessionToken());
            var packageList = new List<PackageDisplay>();
            foreach (var package in packages)
            {
                packageList.Add(
                    new PackageDisplay()
                    {
                        Name = package.Name,
                        BuildLabel = package.BuildLabel,
                        NumberOfFiles = package.NumberOfFiles.ToString("#,##0"),
                        Size = FormatSize(package.Size),
                        FileName = package.FileName.Replace("\\", "\\\\")
                    });
            }
            velocityContext.Add("packages", packageList);
            return viewGenerator.GenerateView("PackageList.vm", velocityContext);
        }
        private string FormatSize(long size)
        {
            double workingSize = size;
            if (size > 1048576)
            {
                workingSize = workingSize / 1048576;
                return string.Format("{0:0.00}Mb", workingSize);
            }
            else if (size > 1024)
            {
                workingSize = workingSize / 1024;
                return string.Format("{0:0.00}Kb", workingSize);
            }
            else
            {
                return string.Format("{0}b", workingSize);
            }
        }
        public class PackageDisplay
        {
            public string Name { get; set; }
            public string BuildLabel { get; set; }
            public string NumberOfFiles { get; set; }
            public string Size { get; set; }
            public string FileName { get; set; }
        }
    }
}
