using System;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise;
namespace ThoughtWorks.CruiseControl.WebDashboard.Dashboard
{
    public class ImmutableNamedActionWithoutSiteTemplate
        : ImmutableNamedAction, INoSiteTemplateAction
    {
        public ImmutableNamedActionWithoutSiteTemplate(string actionName, ICruiseAction action)
            : base(actionName, action)
        { }
    }
}
