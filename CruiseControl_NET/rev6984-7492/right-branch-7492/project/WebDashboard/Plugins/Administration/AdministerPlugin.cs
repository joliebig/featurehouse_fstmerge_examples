using System;
using System.Collections.Generic;
using System.Text;
using Exortech.NetReflector;
using ThoughtWorks.CruiseControl.WebDashboard.Dashboard;
using ThoughtWorks.CruiseControl.WebDashboard.MVC.Cruise;
namespace ThoughtWorks.CruiseControl.WebDashboard.Plugins.Administration
{
    [ReflectorType("administrationPlugin")]
    public class AdministerPlugin
        : IPlugin
    {
        private readonly IActionInstantiator actionInstantiator;
        private string password;
        public AdministerPlugin(IActionInstantiator actionInstantiator)
  {
   this.actionInstantiator = actionInstantiator;
        }
        [ReflectorProperty("password", Required = false)]
        public string Password
        {
            get { return password; }
            set { password = value; }
        }
        public string LinkDescription
  {
   get { return "Administer Dashboard"; }
        }
        public INamedAction[] NamedActions
  {
   get
   {
                ICruiseAction action = actionInstantiator.InstantiateAction(typeof(AdministerAction));
                if (action is AdministerAction)
                {
                    (action as AdministerAction).Password = password;
                }
    return new INamedAction[]
     {
      new ImmutableNamedAction(AdministerAction.ActionName, action)
     };
   }
  }
    }
}
