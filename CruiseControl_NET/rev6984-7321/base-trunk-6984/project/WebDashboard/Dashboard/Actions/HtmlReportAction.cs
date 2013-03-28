using System;
using System.IO;
using ThoughtWorks.CruiseControl.WebDashboard.IO;
using ThoughtWorks.CruiseControl.WebDashboard.MVC;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise;
using ThoughtWorks.CruiseControl.WebDashboard.ServerConnection;
using ThoughtWorks.CruiseControl.Remote;
using System.Text.RegularExpressions;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.View;
using System.Collections;
namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard.Actions
{
    public class HtmlReportAction
        : ICruiseAction, IConditionalGetFingerprintProvider
    {
     private readonly IFingerprintFactory fingerprintFactory;
        private readonly IFarmService farmService;
        private readonly IVelocityViewGenerator viewGenerator;
        public HtmlReportAction(IFingerprintFactory fingerprintFactory, IFarmService farmService,
            IVelocityViewGenerator viewGenerator)
        {
            this.fingerprintFactory = fingerprintFactory;
            this.farmService = farmService;
            this.viewGenerator = viewGenerator;
        }
        public string HtmlFileName { get; set; }
        public IResponse Execute(ICruiseRequest cruiseRequest)
  {
            var velocityContext = new Hashtable();
            velocityContext["url"] = string.Format("RetrieveBuildFile.aspx?file={0}", HtmlFileName);
            return viewGenerator.GenerateView("HtmlReport.vm", velocityContext);
        }
        public ConditionalGetFingerprint GetFingerprint(IRequest request)
     {
            return fingerprintFactory.BuildFromFileNames(HtmlFileName);
     }
    }
}
